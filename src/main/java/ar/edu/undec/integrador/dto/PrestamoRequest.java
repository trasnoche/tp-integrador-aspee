package ar.edu.undec.integrador.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PrestamoRequest(
        @NotNull Long libroId,
        @NotNull Long socioId,
        @NotNull @Future LocalDate fechaDevolucion) {
}
