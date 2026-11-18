/**
 * PASO 1 - Interfaces de los services.
 *
 * TODO: crea aqui dos interfaces:
 *  - CanchaService:   List<CanchaResponse> listar() · CanchaResponse obtener(Long id) · CanchaResponse crear(CanchaRequest)
 *                     · CanchaResponse actualizar(Long id, CanchaRequest) · CanchaResponse cambiarEstado(Long id, boolean activa)
 *  - ArriendoService: consultarDisponibilidad(LocalDate) · crear(ArriendoRequest) · obtener(Long id, String email)
 *                     · listarPorEmail(String) · modificar(Long id, CambioArriendoRequest) · cancelar(Long id, String email)
 *                     · listarAgenda(LocalDate fecha) · cancelarComoAdmin(Long id)
 *
 * Los controllers dependeran de estas interfaces, no de las clases de service.impl.
 * Documenta cada metodo con Javadoc (que hace, que excepcion lanza).
 *
 * Codigo de referencia: canchas-backend/src/main/java/cl/ipss/canchas/service
 */
package cl.ipss.canchas.service;
