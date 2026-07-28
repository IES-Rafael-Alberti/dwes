# Mini Spring Boot Tasks — Paquete Completo

> Referencia completa. No es una plantilla de partida ni un entregable. Úsala
> únicamente cuando lo indique el profesorado o para revisar el ejercicio una
> vez entregado.

Incluye:
- Proyecto con 4 etapas: V1 (controlador simple), V2 (ResponseEntity), V3 (Repositorio), V4 (Servicio + extras).
- Tests organizados por perfiles Maven.
- `requests.http` (IntelliJ) para probar rápido.

## Ejecutar
```bash
mvn spring-boot:run
```

## Tests
```bash
mvn -q -Pall-tests test
mvn -q -Ptests-basic test
mvn -q -Ptests-addons test
mvn -q -Ptests-extras test
mvn -q -Ptests-paging-cache test
mvn -q -Ptests-final test
```

## Notas de migración a Spring Boot 4

Este proyecto se migró de Spring Boot 3.3.3 a **4.0.5**. Los cambios no obvios que pueden afectar a otros proyectos:

| Cambio | Detalle |
|--------|---------|
| `-parameters` del compilador | SB4 requiere `<parameters>true</parameters>` en `maven-compiler-plugin`. Sin esto, `@RequestParam` sin `name` explícito lanza **404** con error "parameter name not available via reflection". |
| `@DataJpaTest` eliminado | No existe en SB4 ni hay un starter sustituto. Usar `@SpringBootTest(properties = "spring.jpa.defer-datasource-initialization=true")`. El `data.sql` se ejecuta antes del DDL de Hibernate si no se difiere. |
| `@WebMvcTest` nuevo paquete | `org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest` → `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest` |
| `@MockBean` → `@MockitoBean` | `org.springframework.boot.test.mock.mockito.MockBean` → `org.springframework.test.context.bean.override.mockito.MockitoBean` |
| Starter web partido | `spring-boot-starter-web` → `spring-boot-starter-webmvc`. Tests necesitan `spring-boot-starter-webmvc-test` (no incluido en `spring-boot-starter-test`). |
