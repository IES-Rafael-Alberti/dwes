# Starter — Productos MVC

Este repositorio es un **punto de partida**, no una solución incompleta por accidente.

## Arranque rápido

```bash
./mvnw test
./mvnw spring-boot:run
```

Requiere Java 25. La consola H2 está deshabilitada por defecto: la práctica se verifica mediante la aplicación y los tests, no manipulando directamente la base de datos. Al abrir `http://localhost:8080/` verás únicamente la portada: el CRUD se construye por etapas.

## Itinerario

Sigue el único enunciado canónico: [`../../TareaSpringMVC-Productos/TareaSpringMVC-Productos.md`](../../TareaSpringMVC-Productos/TareaSpringMVC-Productos.md).
Los contratos de cada etapa están en `checkpoints/`. Copia **solo el test de la etapa actual** a `src/test/java/...`, observa RED, implementa lo mínimo y vuelve a ejecutar hasta GREEN.

> No copies todos los checkpoints a la vez: expresan el destino de etapas futuras y deben fallar antes de implementarlas.

## Frontera formulario–dominio

La solución objetivo usa `ProductForm` para binding y validación web, `Product` para persistencia y un mapper explícito entre ambos. No coloques reglas específicas del formulario en la entidad JPA ni uses la entidad como `@ModelAttribute`.
