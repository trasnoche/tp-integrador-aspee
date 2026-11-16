package ar.edu.undec.integrador.service;

import ar.edu.undec.integrador.dto.PrestamoRequest;
import ar.edu.undec.integrador.exception.ConflictoException;
import ar.edu.undec.integrador.exception.NoEncontradoException;
import ar.edu.undec.integrador.model.EstadoPrestamo;
import ar.edu.undec.integrador.model.Libro;
import ar.edu.undec.integrador.model.Prestamo;
import ar.edu.undec.integrador.model.Socio;
import ar.edu.undec.integrador.repository.PrestamoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PrestamoService {

    private static final int MAX_PRESTAMOS_POR_SOCIO = 3;
    private static final long MULTA_POR_DIA = 500;
    private static final int DIAS_RENOVACION = 7;

    private final PrestamoRepository prestamos;
    private final LibroService libroService;
    private final SocioService socioService;

    public PrestamoService(PrestamoRepository prestamos, LibroService libroService, SocioService socioService) {
        this.prestamos = prestamos;
        this.libroService = libroService;
        this.socioService = socioService;
    }

    public List<Prestamo> listar(EstadoPrestamo estado) {
        return estado == null ? prestamos.findAll() : prestamos.findByEstado(estado);
    }

    public List<Prestamo> delSocio(Long socioId) {
        socioService.buscar(socioId);
        return prestamos.findBySocioId(socioId);
    }

    public Prestamo buscar(Long id) {
        return prestamos.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe el prestamo " + id));
    }

    @Transactional
    public Prestamo crear(PrestamoRequest datos) {
        Libro libro = libroService.buscar(datos.libroId());
        Socio socio = socioService.buscar(datos.socioId());

        if (prestamos.existsByLibroIdAndEstadoNot(libro.getId(), EstadoPrestamo.DEVUELTO)) {
            throw new ConflictoException("El libro ya esta prestado");
        }
        if (prestamos.countBySocioIdAndEstadoNot(socio.getId(), EstadoPrestamo.DEVUELTO) >= MAX_PRESTAMOS_POR_SOCIO) {
            throw new ConflictoException("El socio ya tiene " + MAX_PRESTAMOS_POR_SOCIO + " prestamos sin devolver");
        }
        if (prestamos.existsBySocioIdAndMultaGreaterThanAndMultaPagadaFalse(socio.getId(), 0)) {
            throw new ConflictoException("El socio tiene una multa sin pagar");
        }
        return prestamos.save(new Prestamo(libro, socio, datos.fechaDevolucion()));
    }

    @Transactional
    public Prestamo cambiarEstado(Long id, EstadoPrestamo nuevo) {
        Prestamo prestamo = buscar(id);
        if (!prestamo.getEstado().puedePasarA(nuevo)) {
            throw new ConflictoException("No se puede pasar de " + prestamo.getEstado() + " a " + nuevo);
        }
        if (nuevo == EstadoPrestamo.DEVUELTO) {
            LocalDate hoy = LocalDate.now();
            prestamo.setFechaRealDevolucion(hoy);
            if (hoy.isAfter(prestamo.getFechaDevolucion())) {
                long diasDeAtraso = ChronoUnit.DAYS.between(prestamo.getFechaDevolucion(), hoy);
                prestamo.setMulta(diasDeAtraso * MULTA_POR_DIA);
            }
        }
        prestamo.setEstado(nuevo);
        return prestamos.save(prestamo);
    }

    @Transactional
    public Prestamo renovar(Long id) {
        Prestamo prestamo = buscar(id);
        if (prestamo.getEstado() != EstadoPrestamo.ACTIVO) {
            throw new ConflictoException("Solo se puede renovar un prestamo activo");
        }
        if (prestamo.isRenovado()) {
            throw new ConflictoException("El prestamo ya fue renovado");
        }
        prestamo.setFechaDevolucion(prestamo.getFechaDevolucion().plusDays(DIAS_RENOVACION));
        prestamo.setRenovado(true);
        return prestamos.save(prestamo);
    }

    @Transactional
    public Prestamo pagarMulta(Long id) {
        Prestamo prestamo = buscar(id);
        prestamo.setMultaPagada(true);
        return prestamos.save(prestamo);
    }
}
