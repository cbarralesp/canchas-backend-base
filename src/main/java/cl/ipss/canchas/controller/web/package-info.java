/**
 * PASO 4 (requisito de la evaluacion) - Controllers Spring MVC con vistas Thymeleaf.
 *
 * YA VIENEN HECHOS: las plantillas (src/main/resources/templates), static/web.css (mobile-first) y los
 * formularios del paquete form (ArriendoForm, EditarArriendoForm, CanchaForm).
 *
 * TODO: crea tres @Controller (con @Hidden para ocultarlos de Swagger):
 *  - InicioWebController: GET "/" -> "redirect:/web/disponibilidad"
 *  - ArriendoWebController (@RequestMapping("/web")): disponibilidad, arrendar (GET y POST), mis-arriendos,
 *      arriendos/{id}/editar (GET y POST), arriendos/{id}/cancelar (POST)
 *  - AdminWebController (@RequestMapping("/web/admin")): canchas (GET, POST), canchas/{id}/editar (GET, POST),
 *      canchas/{id}/estado (POST), arriendos (GET), arriendos/{id}/cancelar (POST)
 *
 * Reglas del patron Post/Redirect/Get:
 *  - Todo POST exitoso termina en "redirect:..." (al refrescar no se repite la operacion).
 *  - Los mensajes viajan por RedirectAttributes.addFlashAttribute("exito" | "error", ...).
 *  - Si la validacion falla (BindingResult) o salta una ReglaNegocioException, NO rediriges: devuelves la misma vista
 *    con el error para que el formulario conserve lo escrito.
 *
 * Los atributos que cada plantilla espera estan en el HTML (busca ${...} y los comentarios de cada archivo).
 * Una vez hecho, el sistema se ve en http://localhost:8085/web/disponibilidad
 *
 * Codigo de referencia: canchas-backend/src/main/java/cl/ipss/canchas/controller/web
 */
package cl.ipss.canchas.controller.web;
