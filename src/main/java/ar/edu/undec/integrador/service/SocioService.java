package ar.edu.undec.integrador.service;

import ar.edu.undec.integrador.exception.ConflictoException;
import ar.edu.undec.integrador.exception.NoEncontradoException;
import ar.edu.undec.integrador.model.Socio;
import ar.edu.undec.integrador.repository.PrestamoRepository;
import ar.edu.undec.integrador.repository.SocioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SocioService {

    private final SocioRepository socios;
    private final PrestamoRepository prestamos;

    public SocioService(SocioRepository socios, PrestamoRepository prestamos) {
        this.socios = socios;
        this.prestamos = prestamos;
    }

    public List<Socio> listar() {
        return socios.findAll();
    }

    public Socio buscar(Long id) {
        return socios.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe el socio " + id));
    }

    public Socio crear(Socio socio) {
        socio.setId(null);
        return socios.save(socio);
    }

    public Socio modificar(Long id, Socio datos) {
        Socio socio = buscar(id);
        socio.setNombre(datos.getNombre());
        socio.setEmail(datos.getEmail());
        return socios.save(socio);
    }

    public void borrar(Long id) {
        Socio socio = buscar(id);
        if (prestamos.existsBySocioId(id)) {
            throw new ConflictoException("El socio tiene prestamos registrados");
        }
        socios.delete(socio);
    }
}
