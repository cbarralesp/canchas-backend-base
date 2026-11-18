package cl.ipss.canchas.repository;

import cl.ipss.canchas.entity.Arrendatario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Acceso a datos de los arrendatarios (clientes). */
public interface ArrendatarioRepository extends JpaRepository<Arrendatario, Long> {

    /** Busca a la persona por su email (ya normalizado). Optional vacío = es la primera vez que arrienda. */
    Optional<Arrendatario> findByEmail(String email);
}
