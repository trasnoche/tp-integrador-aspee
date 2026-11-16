package ar.edu.undec.integrador.dto;

import ar.edu.undec.integrador.model.EstadoPrestamo;
import ar.edu.undec.integrador.model.Prestamo;

import java.time.LocalDate;

public record PrestamoResponse(
        Long id,
        Long libroId,
        Long socioId,
        LocalDate fechaDevolucion,
        EstadoPrestamo estado,
        LocalDate fechaRealDevolucion,
        long multa,
        boolean multaPagada,
        boolean renovado) {

    public static PrestamoResponse de(Prestamo p) {
        return new PrestamoResponse(p.getId(), p.getLibro().getId(), p.getSocio().getId(),
                p.getFechaDevolucion(), p.getEstado(), p.getFechaRealDevolucion(),
                p.getMulta(), p.isMultaPagada(), p.isRenovado());
    }
}
