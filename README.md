# canchas-backend-base (punto de partida de la clase)

Es el backend de **Arena Sur** (arriendo de canchas) **completo y funcionando, pero SIN la integración con la API externa del clima**. Todo lo demás es idéntico a [`../canchas-backend`](../canchas-backend): entidades, repositorios, services, controllers REST, controllers Thymeleaf, plantillas, manejo de errores y pruebas.

Lo que se construye en clase es **la última parte: el pronóstico del tiempo** (Open-Meteo). Contexto y reglas: [`../docs`](../docs/README.md).

## Qué trae y qué falta

```
cl.ipss.canchas
├── enums           ✔ Deporte, EstadoArriendo
├── entity          ✔ Cancha, Arrendatario, Arriendo
├── repository      ✔ consultas derivadas y @EntityGraph
├── dto             ✔ requests, responses y Validaciones
├── exception       ✔ ReglaNegocioException, RecursoNoEncontradoException, GlobalExceptionHandler
├── config          ✔ DatosIniciales            ✎ FALTA ClimaProperties y ClimaConfig
├── service         ✔ ArriendoService, CanchaService          ✎ FALTA ClimaService
├── service.impl    ✔ ArriendoServiceImpl, CanchaServiceImpl  ✎ FALTA ClimaServiceImpl
├── controller      ✔ 2 controllers REST                       ✎ FALTA ClimaRestController
├── controller.web  ✔ 3 controllers Thymeleaf (disponibilidad sin clima)
└── client          ✎ FALTA (paquete nuevo: ClimaClient y ClimaExterno)
```

Pruebas incluidas: **26 en verde** (`ArriendoRepositoryTest`, `ArriendoServiceImplTest`, `ArriendoRestControllerTest`, `ArriendoWebControllerTest`).

## Lo que se hace en clase: la API del clima

Seguir la guía [`../docs/11-guia-api-clima-paso-a-paso.md`](../docs/11-guia-api-clima-paso-a-paso.md) (teoría en `../docs/10-api-externa-teoria.md`). Al terminar:

| Resultado | Cómo comprobarlo |
|---|---|
| 38 pruebas en verde (26 + 12 del clima) | `.\mvnw.cmd test` |
| `GET /api/clima?fecha=AAAA-MM-DD` responde 200 (con datos) o 204 (sin datos) | Swagger: `/swagger-ui.html` |
| Bloque del pronóstico en la pantalla de disponibilidad | `http://localhost:8085/web/disponibilidad` |

Mientras no se agregue, la aplicación funciona normalmente: la pantalla de disponibilidad simplemente no muestra el bloque del clima.

## Ejecutar

```powershell
docker compose up -d            # PostgreSQL (o ejecuta crear-bd.sql en tu instalación)
.\mvnw.cmd spring-boot:run
.\mvnw.cmd test
```

Puerto 8085 · base `canchas_db` (usuario y clave `canchas`). **El usuario `canchas` debe existir:** crear solo la base no basta (ejecuta `crear-bd.sql` o usa Docker). Si falla con `la autentificación password falló`, ver el diagnóstico en `docs/08-guia-paso-a-paso.md` (Paso 0).

> Si ejecutas a la vez `canchas-backend` y este proyecto, apaga uno: usan el mismo puerto y la misma base.
