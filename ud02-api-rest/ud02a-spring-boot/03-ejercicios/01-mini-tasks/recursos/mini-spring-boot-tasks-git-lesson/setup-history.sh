#!/usr/bin/env bash
set -euo pipefail

# === Paths robustos ===
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$SCRIPT_DIR"
SNAP_DIR="$ROOT/snapshots"
PROJECT_DIR="$ROOT/project"

# === Comprobaciones ===
if [[ ! -d "$SNAP_DIR" ]]; then
  echo "No encuentro snapshots en: $SNAP_DIR" >&2
  exit 1
fi

mkdir -p "$PROJECT_DIR"

# === Inicializa repo en project/ ===
cd "$PROJECT_DIR"
if [[ ! -d .git ]]; then
  git init
  git config user.name "MiniTasks Teacher"
  git config user.email "teacher@example.com"
fi

# === copy función con fallback si no hay rsync ===
copy_snapshot () {
  local from="$1"
  local to="$2"
  if command -v rsync >/dev/null 2>&1; then
    rsync -a --delete "${from}/" "${to}/"
  else
    # Fallback portable
    shopt -s dotglob
    rm -rf "${to:?}/"*
    cp -a "${from}/." "${to}/"
    shopt -u dotglob
  fi
}

apply () {
  local SNAP="$1"
  local MSG="$2"
  local TAG="$3"

  echo ">> Applying snapshot: $SNAP"
  # copia los ficheros al directorio del repo
  copy_snapshot "$SNAP_DIR/$SNAP" "$PROJECT_DIR"

  # asegura que git se ejecuta dentro del repo
  ( cd "$PROJECT_DIR" && git add . && git commit -m "$MSG" && git tag -f "$TAG" ) || {
    echo "Fallo aplicando $SNAP" >&2
    exit 1
  }
}

apply "v1"        "V1: Controlador sin ResponseEntity (memoria)"           "v1"
apply "v2"        "V2: Refactor a ResponseEntity (memoria)"                 "v2"
apply "v3"        "V3: JPA + Repo + Controller V3 + ApiExceptionHandler"    "v3"
apply "v4-basic"  "V4: Servicio y controlador V4 (CRUD)"                    "v4"
apply "v5-final"  "V4 extras: ETag/Cache-Control, paginación, búsqueda, PUT, HEAD" "v4-extras"

echo ">> Hecho. Tags: v1, v2, v3, v4, v4-extras"
( cd "$PROJECT_DIR" && git log --oneline --decorate --graph --all )
