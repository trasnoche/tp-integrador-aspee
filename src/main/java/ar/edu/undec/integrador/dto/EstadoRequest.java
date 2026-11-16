package ar.edu.undec.integrador.dto;

import ar.edu.undec.integrador.model.EstadoPrestamo;
import jakarta.validation.constraints.NotNull;

public record EstadoRequest(@NotNull EstadoPrestamo estado) {
}
