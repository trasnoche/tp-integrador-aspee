package ar.edu.undec.integrador.service;

import ar.edu.undec.integrador.dto.LibroRanking;
import ar.edu.undec.integrador.exception.ConflictoException;
import ar.edu.undec.integrador.exception.NoEncontradoException;
import ar.edu.undec.integrador.model.Libro;
import ar.edu.undec.integrador.repository.LibroRepository;
import ar.edu.undec.integrador.repository.PrestamoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroService {

    private final LibroRepository libros;
    private final PrestamoRepository prestamos;

    public LibroService(LibroRepository libros, PrestamoRepository prestamos) {
        this.libros = libros;
        this.prestamos = prestamos;
    }

    public List<Libro> listar(boolean soloDisponibles) {
        return soloDisponibles ? libros.findDisponibles() : libros.findAll();
    }

    public Libro buscar(Long id) {
        return libros.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe el libro " + id));
    }

    public Libro crear(Libro libro) {
        if (libros.existsByIsbn(libro.getIsbn())) {
            throw new ConflictoException("Ya existe un libro con el ISBN " + libro.getIsbn());
        }
        libro.setId(null);
        return libros.save(libro);
    }

    public Libro modificar(Long id, Libro datos) {
        Libro libro = buscar(id);
        if (libros.existsByIsbnAndIdNot(datos.getIsbn(), id)) {
            throw new ConflictoException("Ya existe un libro con el ISBN " + datos.getIsbn());
        }
        libro.setTitulo(datos.getTitulo());
        libro.setAutor(datos.getAutor());
        libro.setIsbn(datos.getIsbn());
        return libros.save(libro);
    }

    public void borrar(Long id) {
        Libro libro = buscar(id);
        if (prestamos.existsByLibroId(id)) {
            throw new ConflictoException("El libro tiene prestamos registrados");
        }
        libros.delete(libro);
    }

    public List<LibroRanking> masPrestados(int top) {
        return libros.findMasPrestados(PageRequest.of(0, Math.max(top, 1)));
    }
}
