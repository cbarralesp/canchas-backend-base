package cl.ipss.canchas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad MODELO: la persona que arrienda (equivale al "cliente" del restaurante).
 * No tiene usuario ni contraseña: se identifica por su email, que es único.
 * Si alguien vuelve a arrendar con el mismo email se reutiliza este registro.
 */
@Entity
@Table(name = "arrendatario")
public class Arrendatario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    /** Siempre en minúsculas y sin espacios (lo normaliza el service); es la identificación de la persona. */
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(nullable = false, length = 20)
    private String telefono;

    protected Arrendatario() {
    }

    public Arrendatario(String nombre, String email, String telefono) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }

    /** Actualiza nombre y teléfono cuando la misma persona arrienda de nuevo con datos distintos. */
    public void actualizarContacto(String nombre, String telefono) {
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }
}
