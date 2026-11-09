# Proyecciones JPA: consultar solo lo que necesita la respuesta

Una API suele devolver una representación distinta de la entidad persistida. Un
**DTO de respuesta** define el JSON que ofrecemos al cliente; una **proyección JPA**
permite que el repositorio consulte y devuelva solo una parte de los datos. Se
pueden usar juntas, pero resuelven problemas distintos.

## El caso de Gestión de Eventos

En el ejercicio, `EventoServicio.listarEventos` pagina entidades con
`eventoRepo.findAll(pageable)` y después las convierte a `EventoResponseDTO`.
Ese DTO contiene `id`, `nombre`, fechas y `organizadorId`. La entidad también tiene
`descripcion`, `tipo` y una colección de participantes. Para una lista que no usa
`descripcion` ni `tipo`, la consulta de entidad selecciona más columnas de las
necesarias y después las mapea. La colección de participantes es `LAZY` y el
mapper actual no la recorre: no presentaremos este caso como un arreglo de N+1.

El DTO sigue siendo útil como contrato HTTP. La proyección ofrece una alternativa
para la consulta de lectura de esa lista.

## Proyección de una lista paginada

Podemos definir un `record` específico para los datos consultados:

```java
package daw2a.gestioneventos.dto;

import java.time.LocalDateTime;

public record EventoResumen(
        Long id,
        String nombre,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        Long organizadorId) {
}
```

El repositorio selecciona esos campos. El `LEFT JOIN` conserva eventos sin
organizador, ya que la relación permite que sea nulo. Al devolver un `Page`, se
declara también la consulta de recuento:

```java
import daw2a.gestioneventos.dominio.Evento;
import daw2a.gestioneventos.dto.EventoResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventoRepo extends JpaRepository<Evento, Long> {

@Query(
    value = """
        select new daw2a.gestioneventos.dto.EventoResumen(
            e.id, e.nombre, e.fechaInicio, e.fechaFin, o.id
        )
        from Evento e left join e.organizador o
        """,
    countQuery = "select count(e) from Evento e"
)
Page<EventoResumen> findAllResumenes(Pageable pageable);
}
```

El servicio conserva el DTO HTTP y adapta el resultado de la consulta:

```java
public Page<EventoResponseDTO> listarEventos(Pageable pageable) {
    return eventoRepo.findAllResumenes(pageable)
            .map(resumen -> new EventoResponseDTO(
                    resumen.id(),
                    resumen.nombre(),
                    resumen.fechaInicio() == null ? null : resumen.fechaInicio().toString(),
                    resumen.fechaFin() == null ? null : resumen.fechaFin().toString(),
                    resumen.organizadorId()));
}
```

Así se mantienen separados el resultado de persistencia y el contrato HTTP,
mientras la consulta selecciona solo las columnas necesarias y no hidrata una
entidad para construir cada elemento del resumen.

## ¿Cuándo elegir cada opción?

- **Entidad + mapeo a DTO**: adecuado cuando la operación necesita modificar la
  entidad, aplicar reglas sobre ella o construir una respuesta más compleja.
- **Proyección**: útil en consultas de lectura, en especial listas paginadas, si
  se necesitan pocos campos y la selección puede expresarse claramente en la
  consulta.
- **DTO de respuesta**: sigue siendo conveniente para controlar el JSON, incluso
  cuando los datos se obtienen mediante una proyección.

No toda proyección hace automáticamente la consulta más rápida. Antes de
preferirla, comprueba qué columnas solicita SQL, qué relaciones se consultan y
si el recuento de páginas se comporta como esperas. Las proyecciones anidadas
pueden implicar joins y traer más datos de los previstos; para consultas con
relaciones, expresa y revisa los `JOIN` de forma explícita.

## Para probarlo en el ejercicio

1. Observa el método actual de listado: `findAll(pageable)` seguido del mapeo de
   `Evento` a `EventoResponseDTO`.
2. Añade la proyección de resumen como alternativa de lectura, sin borrar el
   método original.
3. Comprueba que ambas respuestas HTTP contienen los mismos campos y mantienen
   la paginación.
4. Activa el registro SQL y compara las columnas seleccionadas; confirma que la
   consulta no carga participantes para construir el resumen.
5. Decide cuál de las dos soluciones resulta más sencilla y suficiente para
   este caso.

La finalidad no es sustituir siempre el mapeo por proyecciones, sino reconocer
cuándo consultar directamente la forma de datos que necesita una operación
evita cargar y transformar una entidad completa.
