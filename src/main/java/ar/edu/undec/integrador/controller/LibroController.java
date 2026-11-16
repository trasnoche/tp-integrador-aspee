package ar.edu.undec.integrador.controller;

import ar.edu.undec.integrador.dto.LibroRanking;
import ar.edu.undec.integrador.model.Libro;
import ar.edu.undec.integrador.service.LibroService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping
    public List<Libro> listar(@RequestParam(defaultValue = "false") boolean disponible) {
        return libroService.listar(disponible);
    }

    @GetMapping("/mas-prestados")
    public List<LibroRanking> masPrestados(@RequestParam(defaultValue = "5") int top) {
        return libroService.masPrestados(top);
    }

    @GetMapping("/{id}")
    public Libro buscar(@PathVariable Long id) {
        return libroService.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Libro> crear(@Valid @RequestBody Libro libro) {
        Libro creado = libroService.crear(libro);
        return ResponseEntity.created(URI.create("/api/libros/" + creado.getId())).body(creado);
    }

    @PutMapping("/{id}")
    public Libro modificar(@PathVariable Long id, @Valid @RequestBody Libro libro) {
        return libroService.modificar(id, libro);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrar(@PathVariable Long id) {
        libroService.borrar(id);
    }
}
