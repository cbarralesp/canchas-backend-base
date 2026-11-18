/**
 * PASO 3 - Controllers REST (JSON) que consume Angular.
 *
 * TODO: crea dos @RestController delgados: validan con @Valid, llaman al service y eligen el codigo HTTP.
 * NO accedas a repositories ni decidas reglas aqui. Los errores (400, 404, 409) los traduce GlobalExceptionHandler.
 *
 *  - ArriendoRestController  @RequestMapping("/api")
 *      GET  /disponibilidad?fecha=            (fecha con @DateTimeFormat(iso = DATE), obligatoria)
 *      POST /arriendos                        (201 + cabecera Location)
 *      GET  /arriendos?email=                 (mis arriendos)
 *      GET  /arriendos/{id}?email=
 *      PUT  /arriendos/{id}
 *      PATCH /arriendos/{id}/cancelacion?email=
 *      GET  /admin/arriendos?fecha=           (fecha opcional)
 *      PATCH /admin/arriendos/{id}/cancelacion
 *  - CanchaRestController    @RequestMapping("/api/canchas")
 *      GET · GET /{id} · POST (201) · PUT /{id} · PATCH /{id}/estado?activa=
 *
 * Prueba con http://localhost:8085/swagger-ui.html. Repetir un POST de arriendo debe responder 409.
 *
 * Codigo de referencia: canchas-backend/src/main/java/cl/ipss/canchas/controller
 */
package cl.ipss.canchas.controller;
