# Contratos por etapa

Estos tests son código Java real con extensión `.disabled` para que el starter base permanezca verde.

1. Copia el contrato que contiene la etapa que estás trabajando a `src/test/java/com/example/productosmvc/`.
2. Renómbralo a `.java` y ajusta el nombre de la clase al nombre del archivo si activas solo una parte.
3. Elimina temporalmente los métodos de etapas futuras.
4. Ejecuta `./mvnw test`: el nuevo contrato debe quedar RED antes de implementar.

Mapa de métodos:

| Etapa | Contrato |
| --- | --- |
| Listado | `listsProducts` |
| Alta | `newFormUsesAWebSpecificFormObject`, `createsFromAMappedValidForm` |
| Validación | `rejectsInvalidFormBeforeMappingOrPersistence` |
| Mapeo form–entidad | `mapsWebInputToANewDomainEntity`, `mapsStoredEntityToAnEditForm` |
| Edición (controlador) | `updatesThePathIdFromAMappedForm` |
| Edición (servicio) | `updatesTheStoredEntityWithoutTrustingAnInputId` |
| Eliminación | `deletesProduct` |
| Persistencia | `persistsProduct` |
