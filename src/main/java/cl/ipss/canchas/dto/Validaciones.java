package cl.ipss.canchas.dto;

/**
 * Reglas de validación compartidas por los DTO (API REST) y los formularios (Thymeleaf),
 * para escribirlas una sola vez.
 */
public final class Validaciones {

    /** Teléfono chileno: opcionalmente "+" y entre 8 y 12 dígitos. */
    public static final String TELEFONO = "^\\+?[0-9]{8,12}$";

    public static final String MENSAJE_TELEFONO = "El teléfono debe tener entre 8 y 12 dígitos (puede partir con +)";

    private Validaciones() {
    }
}
