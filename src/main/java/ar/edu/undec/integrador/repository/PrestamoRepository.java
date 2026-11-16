package ar.edu.undec.integrador.repository;

import ar.edu.undec.integrador.model.EstadoPrestamo;
import ar.edu.undec.integrador.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    List<Prestamo> findByEstado(EstadoPrestamo estado);

    List<Prestamo> findBySocioId(Long socioId);

    boolean existsByLibroId(Long libroId);

    boolean existsBySocioId(Long socioId);

    boolean existsByLibroIdAndEstadoNot(Long libroId, EstadoPrestamo estado);

    long countBySocioIdAndEstadoNot(Long socioId, EstadoPrestamo estado);

    boolean existsBySocioIdAndMultaGreaterThanAndMultaPagadaFalse(Long socioId, long multa);
}
