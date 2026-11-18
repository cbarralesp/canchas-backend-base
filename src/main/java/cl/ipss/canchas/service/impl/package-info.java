/**
 * PASO 2 - Implementacion de los services (aqui esta lo mas importante de la evaluacion).
 *
 * TODO: crea CanchaServiceImpl y ArriendoServiceImpl con @Service. Inyecta los repositories por constructor
 * (sin @Autowired ni Lombok) y marca con @Transactional cada metodo (readOnly = true los que solo consultan).
 *
 * Reglas que debe cumplir ArriendoServiceImpl:
 *  RN-01  un horario de una cancha no se arrienda dos veces (usa existsByCanchaIdAndFechaAndHoraAndEstado)
 *  RN-02  solo se arriendan canchas activas
 *  RN-03  la hora debe estar entre Arriendo.HORA_MINIMA y Arriendo.HORA_MAXIMA
 *  RN-04  no se arrienda ni se mueve un arriendo a un horario pasado
 *  RN-05  solo se modifica o cancela un arriendo confirmado que aun no comienza, y solo su dueno (email normalizado)
 *         o el administrador al cancelar. Un arriendo ajeno responde "no encontrado" (404), no "prohibido"
 *
 * Errores: RecursoNoEncontradoException (404) y ReglaNegocioException (409), ya creadas en el paquete exception.
 * Consejo: separa cada regla en un metodo privado pequeno (exigirHorarioLibre, exigirModificable...).
 * Orden sugerido: CanchaServiceImpl -> consultarDisponibilidad -> crear -> modificar -> cancelar.
 *
 * Codigo de referencia: canchas-backend/src/main/java/cl/ipss/canchas/service/impl
 */
package cl.ipss.canchas.service.impl;
