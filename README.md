# canchas-backend-base (proyecto para completar)

Esqueleto con **arquitectura MVC en capas** que ya trae la base del proyecto hecha. Se completan **services, controllers REST y controllers Thymeleaf**. Misma idea que `biblioteca-backend-base`.

Compara siempre con el proyecto terminado: [`../canchas-backend`](../canchas-backend). Contexto y reglas: [`../docs`](../docs/README.md).

## Qué ya viene hecho y qué falta

```
cl.ipss.canchas
├── enums           ✔ hecho   (Deporte, EstadoArriendo)
├── entity          ✔ hecho   (Cancha, Arrendatario, Arriendo)
├── repository      ✔ hecho   (consultas derivadas y @EntityGraph)
├── dto             ✔ hecho   (requests, responses y Validaciones)
├── exception       ✔ hecho   (ReglaNegocioException, RecursoNoEncontradoException, GlobalExceptionHandler)
├── config          ✔ hecho   (DatosIniciales)
├── service         ✎ FALTA   <- Paso 1: 2 interfaces
├── service.impl    ✎ FALTA   <- Paso 2: reglas de negocio (lo más importante)
├── controller      ✎ FALTA   <- Paso 3: 2 controllers REST
└── controller.web  ✎ FALTA   <- Paso 4: 3 controllers Thymeleaf (las plantillas y los formularios ya están)
```

También trae el `pom.xml`, `application.yml`, `docker-compose.yml`, las plantillas HTML con `static/web.css` responsivo y una prueba que ya pasa (`ArriendoRepositoryTest`).

Los paquetes que faltan tienen un `package-info.java` con un `TODO` que explica **qué crear, en qué orden y con qué reglas**.

## Ruta sugerida

| Paso | Qué hacer | Cómo comprobarlo |
|---|---|---|
| 1 | Leer `Arriendo` (entidad) y `ArriendoRepository` | Entender qué métodos de consulta ya existen |
| 2 | Crear las 2 interfaces de `service` | Compila |
| 3 | Implementar `service.impl` (empieza por `CanchaServiceImpl`, luego `ArriendoServiceImpl`) | Compila y **levanta**: Spring inyecta los beans |
| 4 | Crear los 2 controllers REST | Probar en Swagger (`/swagger-ui.html`); repetir un POST debe dar **409** |
| 5 | Crear los 3 controllers web | Recorrer las pantallas en `http://localhost:8085/web/disponibilidad` a 360, 768 y 1280 px |
| 6 | Escribir pruebas del service (Mockito) y de los controllers (`@WebMvcTest`) | `.\mvnw.cmd test` en verde |
| 7 (opcional) | Agregar la **API del clima** siguiendo [`../docs/11-guia-api-clima-paso-a-paso.md`](../docs/11-guia-api-clima-paso-a-paso.md) | 38 pruebas en verde y el pronóstico en `/web/disponibilidad` |

## Qué pasa si lo levantas tal cual

**Compila y arranca sin errores** aunque falten services y controllers: nada del código existente depende de ellos. La API y las páginas quedan vacías (404) hasta completar los pasos 4 y 5.

## Ejecutar

```powershell
docker compose up -d            # PostgreSQL (o ejecuta crear-bd.sql en tu instalación)
.\mvnw.cmd spring-boot:run
.\mvnw.cmd test                 # la prueba de repositorio que ya viene hecha
```

Puerto 8085 · base `canchas_db` (usuario y clave `canchas`). **El usuario `canchas` debe existir:** crear solo la base no basta (ejecuta `crear-bd.sql` o usa Docker). Si falla con `la autentificación password falló`, ver el diagnóstico en `docs/08-guia-paso-a-paso.md` (Paso 0).

> Si ejecutas a la vez `canchas-backend` y este proyecto, apaga uno: usan el mismo puerto y la misma base.
