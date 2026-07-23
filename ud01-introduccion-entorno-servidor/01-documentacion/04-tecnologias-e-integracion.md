# Elegir tecnologías e integrar respuestas

Elegir backend no consiste en buscar "el mejor lenguaje". La decisión combina tipo de problema, conocimientos del equipo, ecosistema, operación, seguridad, soporte y coste de cambio. Un framework acelera decisiones repetidas, pero también impone convenciones.

## Vocabulario mínimo

| Concepto | Función |
|---|---|
| Lenguaje | Define sintaxis y semántica para expresar programas |
| Runtime | Ejecuta el programa y gestiona recursos básicos |
| Biblioteca | Aporta funciones que la aplicación invoca |
| Framework | Define un flujo y llama al código de la aplicación en puntos previstos |
| SDK | Reúne herramientas y APIs para una plataforma o servicio |
| CMS | Producto orientado a gestionar y publicar contenido |

Un mismo elemento puede combinar categorías. Lo importante es explicar qué responsabilidad delega la aplicación.

## Comparación orientativa del módulo

| Opción | Fortalezas | Costes | Papel en DWES |
|---|---|---|---|
| Java + Spring Boot | Tipado estático, ecosistema empresarial, pruebas y observabilidad maduras | Más estructura y conceptos iniciales | Backend principal |
| PHP + Laravel | Ciclo petición-respuesta directo, gran productividad y ecosistema web | Convenciones y comportamiento dinámico que deben entenderse | Segundo framework completo |
| .NET | Plataforma sólida, tipada y con herramientas integradas | Solapa varios conceptos ya cubiertos con Java | Demostración opcional |
| Node.js | Un solo lenguaje en cliente y servidor, ecosistema amplio | Modelo asíncrono y dependencias requieren criterio | Fuera del recorrido por solapamiento con MERN |

Esta tabla no asigna una calidad absoluta. Obliga a justificar una elección según el contexto.

## Framework frente a código propio

Un framework suele proporcionar:

- enrutamiento y ciclo de petición;
- configuración e inyección de dependencias;
- validación, seguridad y acceso a datos;
- pruebas y herramientas de diagnóstico;
- convenciones compartidas por el equipo.

El precio es aprender su modelo, actualizarlo y evitar depender de comportamiento que no se comprende. Usar un framework sin entender HTTP, estado y límites produce aplicaciones difíciles de depurar.

## Integración con lenguajes de marcas

El servidor puede integrar datos y HTML de varias formas:

| Respuesta | Producción | Uso habitual |
|---|---|---|
| HTML estático | Archivo existente | Contenido igual para todas las peticiones |
| HTML mediante plantilla | Plantilla + modelo de datos | Vistas renderizadas en servidor |
| JSON | Serialización de objetos o DTO | Clientes web, móviles u otros servicios |
| Redirección | Estado HTTP + `Location` | Cambiar el flujo del cliente |

Una plantilla no es la base de datos ni la lógica de negocio. Su responsabilidad es representar un modelo ya preparado. Del mismo modo, una respuesta JSON no convierte automáticamente un endpoint en REST.

## Criterios de selección

Antes de elegir, responde:

1. ¿Qué requisitos funcionales y de seguridad existen?
2. ¿Qué experiencia real tiene el equipo?
3. ¿Qué soporte, actualizaciones y documentación ofrece el ecosistema?
4. ¿Cómo se prueba, observa y despliega?
5. ¿Qué dependencia crea y cuánto costaría migrar?
6. ¿La herramienta resuelve el problema o solo añade novedad?

## Comprobación

Justifica por qué el módulo usa Spring Boot como backend principal y Laravel como segundo framework. La respuesta debe incluir al menos un criterio técnico, uno operativo y uno didáctico, y explicar cómo ambos pueden producir HTML y JSON.

Esta evidencia trabaja RA1.e, RA1.f y RA1.g.
