package ar.edu.undec.integrador.model;

public enum EstadoPrestamo {
    ACTIVO, VENCIDO, DEVUELTO;

    public boolean puedePasarA(EstadoPrestamo nuevo) {
        return switch (this) {
            case ACTIVO -> nuevo != ACTIVO;
            case VENCIDO -> nuevo == DEVUELTO;
            case DEVUELTO -> false;
        };
    }
}
