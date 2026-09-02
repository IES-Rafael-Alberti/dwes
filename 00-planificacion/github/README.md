# Operaciones docentes con GitHub

Estas herramientas sustituyen a GitHub Classroom para el curso 2026/2027. No
crean repositorios ni modifican GitHub por defecto: primero generan un plan y
solo ejecutan operaciones remotas cuando se indica `--apply` explícitamente.

## Preparación

1. Instalar y autenticar `gh` con una cuenta docente.
2. Copiar `grupos.csv` y completar una fila por grupo.
3. Configurar organización, repositorio template y visibilidad.
4. Ejecutar primero `python3 gh_entregas.py plan --csv grupos.csv`.
5. Revisar el plan y, solo después, ejecutar `create --apply`.

## CSV

```csv
grupo,repo,integrantes,template,visibilidad,hito
ud1-g01,apellidos-nombre-ud1-g01,"alumno1;alumno2;alumno3",ORG/template-dwes,private,ud1-h1
```

`integrantes` contiene logins GitHub separados por `;`. No incluir contraseñas,
tokens, correos personales ni datos sensibles.

## Operaciones

```text
python3 gh_entregas.py plan --csv grupos.csv
python3 gh_entregas.py create --csv grupos.csv --org ORG --apply
python3 gh_entregas.py report --csv grupos.csv --org ORG --out informe.csv
```

`create` crea el repositorio desde `--template`, añade colaboradores, crea el
issue inicial y aplica etiquetas. `report` consulta los repositorios del mismo
CSV y resume commits, pull requests, issues (sin contar pull requests) y
releases para preparar la defensa. Si `repo` ya contiene `ORG/nombre`, se puede
omitir `--org` al ejecutar `report`.

Los contadores son señales para decidir qué revisar, no una nota automática:
un commit grande al final no prueba por sí solo la autoría.
