package cl.ipss.canchas.config;

import cl.ipss.canchas.entity.Arrendatario;
import cl.ipss.canchas.entity.Arriendo;
import cl.ipss.canchas.entity.Cancha;
import cl.ipss.canchas.enums.Deporte;
import cl.ipss.canchas.repository.ArrendatarioRepository;
import cl.ipss.canchas.repository.ArriendoRepository;
import cl.ipss.canchas.repository.CanchaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

/**
 * Carga datos de ejemplo la primera vez que se levanta la aplicación (si no hay canchas en la base),
 * para poder probar el sistema sin ingresar nada a mano.
 *
 * El {@code ApplicationRunner} se ejecuta una vez, cuando Spring termina de arrancar; Hibernate ya creó las tablas
 * gracias a {@code spring.jpa.hibernate.ddl-auto=update}.
 */
@Configuration
public class DatosIniciales {

    private static final Logger log = LoggerFactory.getLogger(DatosIniciales.class);

    @Bean
    ApplicationRunner cargarDatos(CanchaRepository canchas, ArrendatarioRepository arrendatarios,
                                  ArriendoRepository arriendos) {
        return args -> {
            if (canchas.count() > 0) {
                return; // ya hay datos: no se duplican
            }
            List<Cancha> creadas = canchas.saveAll(List.of(
                    new Cancha("Cancha 1 Fútbol 7", Deporte.FUTBOL, 30000),
                    new Cancha("Cancha 2 Fútbol 7", Deporte.FUTBOL, 30000),
                    new Cancha("Pádel A", Deporte.PADEL, 20000),
                    new Cancha("Tenis Central", Deporte.TENIS, 15000),
                    new Cancha("Básquet Techada", Deporte.BASQUET, 18000)));

            // Dos arriendos de mañana para que la disponibilidad muestre horarios ocupados desde el inicio.
            Arrendatario ana = arrendatarios.save(new Arrendatario("Ana Pérez", "ana@correo.cl", "+56912345678"));
            LocalDate manana = LocalDate.now().plusDays(1);
            arriendos.save(new Arriendo(creadas.get(0), ana, manana, 19));
            arriendos.save(new Arriendo(creadas.get(2), ana, manana, 10));
            log.info("--- datos iniciales cargados: {} canchas ---", creadas.size());
        };
    }
}
