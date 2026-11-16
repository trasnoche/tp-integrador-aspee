package ar.edu.undec.integrador.controller;

import ar.edu.undec.integrador.dto.EstadoRequest;
import ar.edu.undec.integrador.dto.PrestamoRequest;
import ar.edu.undec.integrador.dto.PrestamoResponse;
import ar.edu.undec.integrador.model.EstadoPrestamo;
import ar.edu.undec.integrador.service.PrestamoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @GetMapping
    public List<PrestamoResponse> listar(@RequestParam(required = false) EstadoPrestamo estado) {
        return prestamoService.listar(estado).stream().map(PrestamoResponse::de).toList();
    }

    @GetMapping("/{id}")
    public PrestamoResponse buscar(@PathVariable Long id) {
        return PrestamoResponse.de(prestamoService.buscar(id));
    }

    @PostMapping
    public ResponseEntity<PrestamoResponse> crear(@Valid @RequestBody PrestamoRequest datos) {
        PrestamoResponse creado = PrestamoResponse.de(prestamoService.crear(datos));
        return ResponseEntity.created(URI.create("/api/prestamos/" + creado.id())).body(creado);
    }

    @PatchMapping("/{id}/estado")
    public PrestamoResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest datos) {
        return PrestamoResponse.de(prestamoService.cambiarEstado(id, datos.estado()));
    }

    @PatchMapping("/{id}/renovar")
    public PrestamoResponse renovar(@PathVariable Long id) {
        return PrestamoResponse.de(prestamoService.renovar(id));
    }

    @PatchMapping("/{id}/pagar-multa")
    public PrestamoResponse pagarMulta(@PathVariable Long id) {
        return PrestamoResponse.de(prestamoService.pagarMulta(id));
    }
}
