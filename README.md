# TP Integrador · DAW1 - Biblioteca

**Alumno/a:** Aspee, Nicolas · **Legajo:** 510104
**Enunciado elegido:** biblioteca

## Descripción

API REST para una biblioteca barrial. Registra libros, socios y prestamos, controla que un libro no se
preste dos veces a la vez y que cada socio no tenga mas de 3 prestamos sin devolver.

## Cómo ejecutarlo

Requisitos: Java 21 (Maven viene incluido con `mvnw`).

```bash
./mvnw spring-boot:run
```

La API queda en `http://localhost:8080/api`. La base H2 se guarda en `data/` (no se sube al repositorio).
Consola H2: `http://localhost:8080/h2-console` (URL `jdbc:h2:file:./data/biblioteca`, usuario `sa`, sin contraseña).

## Modelo

| Entidad | Atributos principales | Relaciones |
|---|---|---|
| Libro | id, titulo, autor, isbn (unico) | 1 a N con Prestamo |
| Socio | id, nombre, email | 1 a N con Prestamo |
| Prestamo | id, estado, fechaDevolucion, fechaRealDevolucion, multa, multaPagada, renovado | N a 1 con Libro y Socio |

Estados de Prestamo: `ACTIVO → DEVUELTO | VENCIDO`, `VENCIDO → DEVUELTO`. `DEVUELTO` es final.

## Endpoints

| Método | Ruta | Descripción | Respuestas |
|---|---|---|---|
| GET | /api/libros | Lista los libros (`?disponible=true` solo los que no estan prestados) | 200 |
| GET | /api/libros/{id} | Busca por id | 200, 400, 404 |
| GET | /api/libros/mas-prestados?top=5 | Libros con mas prestamos y su cantidad | 200 |
| POST | /api/libros | Crea | 201, 400, 409 |
| PUT | /api/libros/{id} | Modifica | 200, 400, 404, 409 |
| DELETE | /api/libros/{id} | Borra | 204, 404, 409 |
| GET | /api/socios | Lista los socios | 200 |
| GET | /api/socios/{id} | Busca por id | 200, 400, 404 |
| GET | /api/socios/{id}/prestamos | Prestamos del socio | 200, 404 |
| POST | /api/socios | Crea | 201, 400 |
| PUT | /api/socios/{id} | Modifica | 200, 400, 404 |
| DELETE | /api/socios/{id} | Borra | 204, 404, 409 |
| GET | /api/prestamos | Lista los prestamos (`?estado=ACTIVO` filtra por estado) | 200, 400 |
| GET | /api/prestamos/{id} | Busca por id | 200, 400, 404 |
| POST | /api/prestamos | Crea en estado ACTIVO | 201, 400, 404, 409 |
| PATCH | /api/prestamos/{id}/estado | Cambia el estado | 200, 400, 404, 409 |
| PATCH | /api/prestamos/{id}/renovar | Suma 7 dias a la fecha de devolucion | 200, 404, 409 |
| PATCH | /api/prestamos/{id}/pagar-multa | Marca la multa como pagada | 200, 404 |

Los errores se devuelven como ProblemDetail (`status`, `title`, `detail`). Los 400 de validacion agregan
`errores` con campo → mensaje.

## Reglas de negocio

1. Un libro con un prestamo no devuelto no se puede volver a prestar → 409.
2. Un socio puede tener como maximo 3 prestamos no devueltos → 409.
3. No puede haber dos libros con el mismo ISBN → 409.
4. Un libro o un socio con prestamos registrados no se puede borrar → 409.
5. Una transicion de estado no permitida → 409.

## Extras

- **Multa por atraso:** al pasar a DEVUELTO se guarda `fechaRealDevolucion`; si es posterior a
  `fechaDevolucion`, `multa` = dias de atraso × $500.
- **Socio suspendido:** un socio con una multa sin pagar no puede pedir prestamos (409).
- **Disponibilidad:** `GET /api/libros?disponible=true`.
- **Ranking:** `GET /api/libros/mas-prestados?top=5`.
- **Renovacion:** una sola vez y solo si el prestamo esta ACTIVO (si no, 409).

## Pruebas

En `pruebas.http` están todas las peticiones de ejemplo (se ejecutan desde IntelliJ).
