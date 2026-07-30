import os
import re
from pathlib import Path

EXCLUDE_DIRS = {
    '90-archivo', '90-historico', '99-profesor', '_profesor', 'Presentaciones', '__pycache__',
    'vendor', 'node_modules', '.git', '.idea', '.settings', 'build', 'target', 'bin',
    'Seguridad',
}
EXCLUDE_FILES = {
    'README_Docente.md', 'README_Ingesta.md', 'referencia_meltano.md',
    'mini-spring-boot-Guia.md',
}

SECTION_ORDER = {
    '01-documentacion': 'Documentación',
    '02-ejemplos': 'Ejemplos',
    '03-ejercicios': 'Ejercicios',
    '04-proyectos': 'Proyectos',
    '05-cuestionarios': 'Cuestionarios',
    '06-seguridad': 'Seguridad',
}
SECTION_KEYS = list(SECTION_ORDER.keys())


FILE_TITLE_OVERRIDES = {
}


def clean_title(filename, rel_path=''):
    if rel_path and rel_path in FILE_TITLE_OVERRIDES:
        return FILE_TITLE_OVERRIDES[rel_path]
    name = filename.replace('.md', '')
    name = re.sub(r'^UD\d+(_[A-Z]\d+)?_', '', name, flags=re.IGNORECASE)
    name = re.sub(r'^\d{2,3}-', '', name)
    name = name.replace('_', ' ').replace('-', ' ').replace('.', ' ')
    name = re.sub(r'\s+', ' ', name).strip()
    if len(name) > 75:
        name = name[:72] + '...'
    return name or filename.replace('.md', '')


def scan_files(dirpath, docs_dir):
    entries = []
    try:
        items = sorted(os.listdir(dirpath))
    except PermissionError:
        return entries

    for item in items:
        if item.startswith('.'):
            continue
        path = os.path.join(dirpath, item)
        rel = str(Path(path).relative_to(docs_dir))

        if os.path.isdir(path):
            if item in EXCLUDE_DIRS:
                continue
            sub = scan_files(path, docs_dir)
            if sub:
                nice = item.replace('-', ' ').replace('_', ' ').title()
                entries.append({nice: sub})
        elif item.endswith('.md') and item not in EXCLUDE_FILES:
            title = clean_title(item, rel)
            entries.append({title: rel})

    return entries


def scan_unit(unit_symlink, docs_dir, index_path):
    children = [{'Guía de la unidad': index_path}]

    for item in sorted(os.listdir(unit_symlink)):
        if item == 'README.md' or item in EXCLUDE_FILES:
            continue
        item_path = unit_symlink / item
        if not item_path.is_file() or not item.endswith('.md'):
            continue
        rel = str(item_path.relative_to(docs_dir))
        title = clean_title(item, rel)
        children.append({title: rel})

    for section_key, section_title in SECTION_ORDER.items():
        section_path = unit_symlink / section_key
        if not section_path.is_dir():
            continue
        entries = scan_files(section_path, docs_dir)
        if entries:
            children.append({section_title: entries})

    for item in sorted(os.listdir(unit_symlink)):
        if item.startswith('.') or item in EXCLUDE_DIRS:
            continue
        if item in SECTION_KEYS:
            continue
        item_path = unit_symlink / item
        if not item_path.is_dir():
            continue
        entries = scan_files(item_path, docs_dir)
        if entries:
            nice = item.replace('-', ' ').replace('_', ' ').title()
            children.append({nice: entries})

    return children


def on_config(config):
    docs_dir = Path(config['docs_dir'])

    nav = [{'Inicio': 'index.md'}]

    UNIT_NAMES = {
        '00-unidad-0-previos': ('UD0 — Previos', 'unidades/ud00.md'),
        'ud01-introduccion-entorno-servidor': ('UD1 — Introducción', 'unidades/ud01.md'),
        'ud02a-spring-boot': ('UD2a — API REST Spring Boot', 'unidades/ud02a.md'),
        'ud02b-dotnet': ('UD2b — API REST .NET', 'unidades/ud02b.md'),
        'ud02c-graphql': ('UD2c — GraphQL', 'unidades/ud02c.md'),
        'ud03-mvc-spring-boot': ('UD3 — MVC Spring Boot', 'unidades/ud03.md'),
        'ud04-php': ('UD4 — PHP', 'unidades/ud04.md'),
        'ud05-laravel': ('UD5 — Laravel', 'unidades/ud05.md'),
        'ud06-aplicaciones-hibridas': ('UD6 — Aplicaciones Híbridas', 'unidades/ud06.md'),
        'ud07-proyecto-integrador': ('UD7 — Proyecto Integrador', 'unidades/ud07.md'),
    }
    disabled_units = {'ud02b-dotnet', 'ud02c-graphql'}

    for unit_dirname in sorted(UNIT_NAMES):
        if unit_dirname in disabled_units:
            continue
        # Search for symlink in docs/ subdirectories
        unit_symlink = None
        for candidate in docs_dir.rglob(unit_dirname):
            if candidate.is_symlink() or candidate.is_dir():
                unit_symlink = candidate
                break
        if unit_symlink is None:
            continue

        unit_title, index_path = UNIT_NAMES[unit_dirname]
        children = scan_unit(unit_symlink, docs_dir, index_path)
        if children:
            nav.append({unit_title: children})

    plantillas = docs_dir / 'plantillas'
    if plantillas.is_dir():
        entries = scan_files(plantillas, docs_dir)
        if entries:
            nav.append({'Plantillas': entries})

    config['nav'] = nav
    return config
