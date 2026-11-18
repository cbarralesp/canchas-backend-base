package cl.ipss.canchas.enums;

/**
 * Estado de un arriendo. Un arriendo nace CONFIRMADO y solo puede pasar a CANCELADO (no hay vuelta atrás).
 * Un arriendo cancelado libera el horario para que otra persona lo tome.
 */
public enum EstadoArriendo {

    CONFIRMADO("Confirmado"),
    CANCELADO("Cancelado");

    private final String etiqueta;

    EstadoArriendo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
