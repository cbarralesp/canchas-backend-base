package cl.ipss.canchas.controller.web;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** CONTROLLER WEB: la raíz del sitio (http://localhost:8085/) lleva a la pantalla de disponibilidad. */
@Controller
@Hidden // no aparece en la documentación Swagger (que solo describe la API REST)
public class InicioWebController {

    @GetMapping("/")
    public String inicio() {
        return "redirect:/web/disponibilidad";
    }
}
