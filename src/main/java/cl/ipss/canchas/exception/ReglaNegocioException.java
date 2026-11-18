package cl.ipss.canchas.exception;

/**
 * Se rompió una regla del negocio (horario ocupado, fecha pasada, cancha inactiva, arriendo ya cancelado...).
 * Una sola clase cubre todos los casos: el mensaje explica cuál fue. Se traduce a HTTP 409 (conflicto).
 * Las pantallas Thymeleaf también la capturan para mostrar el mensaje sin perder lo que el usuario escribió.
 */
public class ReglaNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
