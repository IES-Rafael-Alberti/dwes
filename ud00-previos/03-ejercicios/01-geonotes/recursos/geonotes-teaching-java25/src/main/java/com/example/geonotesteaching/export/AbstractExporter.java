package com.example.geonotesteaching.export;

// Clase base para exportadores de datos.
// Implementa la interfaz Exporter, que define el contrato para cualquier exportador (por ejemplo, exportar a JSON, HTML, etc).
// Al ser abstracta no se puede instanciar directamente; las subclases implementan export().
public abstract class AbstractExporter implements Exporter  {
    // Método que cada exportador debe implementar para producir la salida final (por ejemplo, JSON, HTML, etc).
    public abstract String export();
}
