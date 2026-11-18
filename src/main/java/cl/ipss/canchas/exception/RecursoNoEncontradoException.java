package cl.ipss.canchas.exception;

/** El recurso pedido no existe (o no pertenece a quien lo pide). Se traduce a HTTP 404. */
public class RecursoNoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
