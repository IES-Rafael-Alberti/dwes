# Configuración, perfiles y carga inicial en `bshipAlong`

Continuamos después de [usuarios, perfiles y roles](12-usuarios-perfiles-y-roles-bshipalong.md).
Aprenderemos a cambiar una opción sin recompilar y a cargar un pequeño catálogo
solo en un entorno de demostración. No implementamos todavía subida de archivos,
contraseñas, login ni permisos. Un rol guardado no concede acceso por sí solo.

## 1. Punto de partida y seguridad

- Completa antes la documentación 12: `AppRole`, su repositorio y la migración
  de identidades deben existir. Comprueba el número real de migración y la suite.
- Trabaja en `bshipAlong` y ejecuta los comandos desde su raíz.
- Se conserva `application-postgres.yaml` y su configuración de credenciales.
  Nunca publiques ni muestres `.env` o contraseñas.
- La carga `demo` escribe en la base de datos activa. **Usa una BD H2 aislada**
  y no combines el perfil `demo` con PostgreSQL compartido o producción.
- En pruebas H2 se usa rollback; la ejecución real de `demo` no es un test y
  puede dejar datos en la base que se configure.

## 2. ¿Para qué sirve configurar?

Una ruta de almacenamiento puede ser distinta en cada ordenador. Si la escribimos
en código, cambiarla obliga a modificarlo y volver a compilar. Una **propiedad**
es un valor externo que la aplicación recibe al arrancar.

Un **perfil Spring** agrupa ajustes y componentes para un contexto, por ejemplo
`postgres` para conexión o `demo` para una carga de demostración. No es una
cuenta de usuario ni un perfil de `UserProfile`. El perfil no es una barrera de
seguridad: hay que decidir dónde y con qué base se arranca la aplicación.

`application.yaml` contiene valores generales; archivos por perfil y otras
fuentes externas pueden reemplazarlos. El orden exacto depende de la fuente; no
confundir variables del proceso Java con variables exclusivas de Docker Compose.

## 3. Una propiedad tipada

En `src/main/resources/application.yaml`, **añade** estas líneas en el nivel
superior, conservando toda la configuración existente:

```yaml
app:
  storage:
    avatars-path: ./data/avatars
```

La ruta es un valor de ejemplo relativo al directorio de ejecución. **Todavía
no crea directorios ni almacena imágenes.** Más adelante tendremos que validar
la ubicación y definir una política segura de almacenamiento.

**Archivo:** `src/main/java/es/daw2a/bshipalong/config/StorageProperties.java`

```java
package es.daw2a.bshipalong.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage")
public record StorageProperties(String avatarsPath) {}
```

**Archivo:** `src/main/java/es/daw2a/bshipalong/config/AppConfiguration.java`

```java
package es.daw2a.bshipalong.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(StorageProperties.class)
public class AppConfiguration {}
```

`@ConfigurationProperties` agrupa valores de `app.storage` en un tipo Java;
Spring enlaza `avatars-path` con `avatarsPath`. `@EnableConfigurationProperties`
registra ese tipo, y la inyección permite pedirlo en otras clases. Un bean es un
objeto cuya creación y ciclo de vida gestiona Spring; esta configuración no es
una capa de negocio adicional.

Para comprobar que el valor se puede cambiar sin tocar código, añade:

**Archivo:** `src/test/java/es/daw2a/bshipalong/config/StoragePropertiesTest.java`

```java
package es.daw2a.bshipalong.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "app.storage.avatars-path=./test-avatars")
class StoragePropertiesTest {
    @Autowired StorageProperties storage;

    @Test
    void propertiesCanBeOverridden() {
        assertThat(storage.avatarsPath()).isEqualTo("./test-avatars");
    }

}
```

```bash
./mvnw -Dtest=StoragePropertiesTest test
```

La propiedad de `@SpringBootTest(properties=...)` solo modifica el contexto de
esa prueba; no cambia el YAML ni demuestra la configuración de producción.

## 4. Un recurso del classpath

Vamos a guardar nombres de roles de demostración en un archivo que viajará dentro
del JAR. Es distinto de un fichero externo editable por el administrador. Se lee
como recurso, **no como ruta física `src/main/resources/...`**: esa ruta no
existe igual dentro de un JAR empaquetado.

**Archivo:** `src/main/resources/catalog/roles.json`

```json
["PLAYER", "ADMIN"]
```

El catálogo es la fuente de datos de demostración. Flyway sigue siendo dueño del
**esquema**; no añadimos otra migración que inserte estos mismos roles. Si en el
futuro los roles pasan a ser datos obligatorios de referencia, habrá que elegir
un único responsable y diseñar su migración, no cargar duplicados por ambos lados.

## 5. Cargar sin duplicar en ejecuciones consecutivas

**Archivo:** `src/main/java/es/daw2a/bshipalong/service/RoleCatalogLoader.java`

```java
package es.daw2a.bshipalong.service;

import es.daw2a.bshipalong.domain.AppRole;
import es.daw2a.bshipalong.repository.AppRoleRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;

@Service
public class RoleCatalogLoader {
    private final AppRoleRepository roles;
    private final ObjectMapper mapper;

    public RoleCatalogLoader(AppRoleRepository roles, ObjectMapper mapper) {
        this.roles = roles;
        this.mapper = mapper;
    }

    @Transactional
    public void load() throws IOException {
        try (var stream = new ClassPathResource("catalog/roles.json").getInputStream()) {
            String[] names = mapper.readValue(stream, String[].class);
            for (String name : names) {
                if (roles.findByName(name).isEmpty()) {
                    roles.save(new AppRole(name));
                }
            }
        }
    }
}
```

El servicio lee el recurso, consulta si cada nombre existe y solo inserta si
falta. Llamarlo dos veces **consecutivas** deja el mismo catálogo; eso es el
comportamiento idempotente que comprobamos aquí. La restricción `UNIQUE` de la
BD sigue siendo necesaria: una consulta previa no elimina una carrera entre dos
arranques concurrentes. No demostramos aquí recuperación automática de ese caso.

La transacción agrupa la carga de roles; no gestiona archivos externos ni cambia
las migraciones. Si el recurso no existe o el JSON no es válido, la carga debe
fallar y mostrar el error al profesor; no ignorarlo ni borrar la BD.

## 6. Ejecutar al arrancar, solo en `demo`

**Archivo:** `src/main/java/es/daw2a/bshipalong/config/DemoRolesRunner.java`

```java
package es.daw2a.bshipalong.config;

import es.daw2a.bshipalong.service.RoleCatalogLoader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("demo")
public class DemoRolesRunner implements CommandLineRunner {
    private final RoleCatalogLoader loader;
    public DemoRolesRunner(RoleCatalogLoader loader) { this.loader = loader; }
    @Override public void run(String... args) throws Exception { loader.load(); }
}
```

`CommandLineRunner` se ejecuta al arrancar el contexto. `@Profile("demo")`
permite crear este componente únicamente cuando se activa ese perfil; sin él,
la clase de carga existe, pero no se invoca automáticamente. Este ejemplo **no
crea cuentas ni asigna roles**, y no garantiza que `demo` no se combine con otro
perfil: esa elección es responsabilidad de la configuración de ejecución.

Para comprobar ambos comportamientos, añade las siguientes clases:

**Archivo:** `src/test/java/es/daw2a/bshipalong/config/RoleCatalogLoaderTest.java`

```java
package es.daw2a.bshipalong.config;

import es.daw2a.bshipalong.repository.AppRoleRepository;
import es.daw2a.bshipalong.service.RoleCatalogLoader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class RoleCatalogLoaderTest {
    @Autowired RoleCatalogLoader loader;
    @Autowired AppRoleRepository roles;
    @Autowired ApplicationContext context;

    @Test
    void demoRunnerIsDisabledByDefault() {
        assertThat(context.getBeansOfType(DemoRolesRunner.class)).isEmpty();
    }

    @Test
    void loadingTwiceDoesNotDuplicateRoles() throws Exception {
        loader.load();
        loader.load();
        assertThat(roles.findByName("PLAYER")).isPresent();
        assertThat(roles.findByName("ADMIN")).isPresent();
        assertThat(roles.count()).isEqualTo(2);
    }
}
```

**Archivo:** `src/test/java/es/daw2a/bshipalong/config/DemoRolesRunnerTest.java`

```java
package es.daw2a.bshipalong.config;

import es.daw2a.bshipalong.repository.AppRoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:demoRoles;DB_CLOSE_DELAY=-1")
@ActiveProfiles("demo")
class DemoRolesRunnerTest {
    @Autowired AppRoleRepository roles;

    @Test
    void demoProfileLoadsCatalogAtStartup() {
        assertThat(roles.findByName("PLAYER")).isPresent();
        assertThat(roles.findByName("ADMIN")).isPresent();
    }
}
```

La primera usa una transacción con rollback y comprueba dos llamadas consecutivas.
La segunda arranca un contexto con `demo` y **una base H2 independiente**; no
usa PostgreSQL ni presupone que la base de otra prueba esté vacía.

```bash
./mvnw -Dtest=StoragePropertiesTest,RoleCatalogLoaderTest,DemoRolesRunnerTest test
./mvnw test
./mvnw package
```

Antes de probar manualmente el perfil, confirma en la configuración del proceso
que el datasource es H2 aislado. Con esa condición, desde la raíz del proyecto:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=demo
```

Comprueba el arranque y que se cargan `PLAYER` y `ADMIN`; vuelve a arrancar
contra la **misma base de pruebas** si quieres observar idempotencia entre
arranques. La H2 en memoria desaparece cuando se termina el proceso: reiniciarla
no demuestra que conservase filas. Para probar persistencia entre procesos hace
falta una H2 de fichero aislada, configurada expresamente y sin borrar datos.

## 7. Prueba en aplicación empaquetada

El comando `./mvnw package` crea el JAR del proyecto. El recurso de `catalog/`
se empaqueta dentro; si se comprueba su ejecución, hacerlo con un perfil y una
base H2 aislada conocidos. No imprimir propiedades sensibles ni usar datos de
producción para esta comprobación. El comando de empaquetado por sí solo no
acredita que el JAR se haya arrancado ni que la carga se haya ejecutado.

## 8. Qué hemos comprobado y qué queda

- Propiedad tipada que puede variar sin editar el código.
- Recurso JSON accesible desde el classpath y lectura transaccional.
- Carga consecutiva sin duplicados y runner activado con el perfil `demo`.
- No hay subida real de archivos, permisos, cuentas de prueba ni seguridad.
- Un perfil no impide por sí solo una configuración peligrosa y dos arranques
  simultáneos requieren una estrategia adicional.

Preguntas: ¿por qué se usa `ClassPathResource`? ¿Qué se puede cambiar sin
recompilar? ¿Qué hace Flyway y qué hace el runner? ¿Por qué `UNIQUE` sigue siendo
necesario? ¿Por qué reiniciar una BD H2 en memoria no prueba persistencia?
