package cl.ipss.canchas.enums;

/**
 * Deporte para el que está habilitada una cancha.
 * Se guarda en la base como texto (por ejemplo "FUTBOL") y la etiqueta es lo que ve el usuario.
 */
public enum Deporte {

    FUTBOL("Fútbol"),
    PADEL("Pádel"),
    TENIS("Tenis"),
    BASQUET("Básquetbol");

    private final String etiqueta;

    Deporte(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
