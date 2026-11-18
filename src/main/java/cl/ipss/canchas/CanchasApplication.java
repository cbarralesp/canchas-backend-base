package cl.ipss.canchas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación "Arena Sur" (arriendo de canchas deportivas).
 *
 * {@code @SpringBootApplication} activa tres cosas a la vez:
 *  - la configuración automática de Spring Boot (servidor web, JPA, Thymeleaf...),
 *  - el escaneo de componentes del paquete cl.ipss.canchas y sus subpaquetes
 *    (controllers, services, repositories, config),
 *  - la clase como fuente de configuración.
 */
@SpringBootApplication
public class CanchasApplication {

    public static void main(String[] args) {
        SpringApplication.run(CanchasApplication.class, args);
    }
}
