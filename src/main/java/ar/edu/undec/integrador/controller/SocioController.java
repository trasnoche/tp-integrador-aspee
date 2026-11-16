package ar.edu.undec.integrador.controller;

import ar.edu.undec.integrador.dto.PrestamoResponse;
import ar.edu.undec.integrador.model.Socio;
import ar.edu.undec.integrador.service.PrestamoService;
import ar.edu.undec.integrador.service.SocioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/socios")
public class SocioController {

    private final SocioService socioService;
    private final PrestamoService prestamoService;

    public SocioController(SocioService socioService, PrestamoService prestamoService) {
        this.socioService = socioService;
        this.prestamoService = prestamoService;
    }

    @GetMapping
    public List<Socio> listar() {
        return socioService.listar();
    }

    @GetMapping("/{id}")
    public Socio buscar(@PathVariable Long id) {
        return socioService.buscar(id);
    }

    @GetMapping("/{id}/prestamos")
    public List<PrestamoResponse> prestamos(@PathVariable Long id) {
        return prestamoService.delSocio(id).stream().map(PrestamoResponse::de).toList();
    }

    @PostMapping
    public ResponseEntity<Socio> crear(@Valid @RequestBody Socio socio) {
        Socio creado = socioService.crear(socio);
        return ResponseEntity.created(URI.create("/api/socios/" + creado.getId())).body(creado);
    }

    @PutMapping("/{id}")
    public Socio modificar(@PathVariable Long id, @Valid @RequestBody Socio socio) {
        return socioService.modificar(id, socio);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrar(@PathVariable Long id) {
        socioService.borrar(id);
    }
}
