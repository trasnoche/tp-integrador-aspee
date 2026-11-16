package ar.edu.undec.integrador.repository;

import ar.edu.undec.integrador.dto.LibroRanking;
import ar.edu.undec.integrador.model.Libro;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    @Query("""
            select l from Libro l
            where not exists (select p from Prestamo p where p.libro = l
                and p.estado <> ar.edu.undec.integrador.model.EstadoPrestamo.DEVUELTO)
            """)
    List<Libro> findDisponibles();

    @Query("""
            select new ar.edu.undec.integrador.dto.LibroRanking(l.id, l.titulo, l.autor, l.isbn, count(p))
            from Prestamo p join p.libro l
            group by l.id, l.titulo, l.autor, l.isbn
            order by count(p) desc
            """)
    List<LibroRanking> findMasPrestados(Pageable pageable);
}
