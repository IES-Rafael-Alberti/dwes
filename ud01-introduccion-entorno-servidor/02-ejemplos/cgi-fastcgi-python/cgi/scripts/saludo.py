#!/usr/bin/env python3
"""CGI ejecutado por fcgiwrap para cada petición a /cgi-bin/saludo.py."""

import html
import os
from urllib.parse import parse_qs


parametros = parse_qs(os.environ.get("QUERY_STRING", ""))
nombre = parametros.get("nombre", ["visitante"])[0].strip()[:40] or "visitante"
saludo = f"Hola, {nombre}"

print("Content-Type: text/html; charset=utf-8")
print()
print("<!doctype html><html lang='es'><meta charset='utf-8'>")
print(f"<title>Respuesta CGI</title><h1>{html.escape(saludo)}</h1>")
print("<p>Esta respuesta la ha construido un script Python al recibir la petición.</p>")
print(f"<p>PID del script: {os.getpid()}</p>")
print("<form action='/cgi-bin/saludo.py'>")
print("<label>Nombre <input name='nombre'></label> <button>Enviar</button>")
print("</form><p><a href='/'>Volver a la página estática</a></p></html>")
