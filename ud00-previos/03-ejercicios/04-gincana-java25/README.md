# Gincana Java 25: descifra el siguiente reto

Actividad practica de repaso de Java antes del trabajo de servidor. Tiempo:
**dos horas** en parejas o equipos de tres. No tiene nota independiente.

El primer reto se lee aqui. Los siguientes enunciados estan cifrados en
`retos-cifrados/` y solo se abren si calculas correctamente la clave del reto
anterior.

## Como paso al siguiente reto

El mecanismo siempre es el mismo:

1. Lees el reto actual y escribes tu programa Java.
2. Tu programa puede imprimir comprobaciones intermedias.
3. La **ultima linea** de la salida de tu programa debe ser exactamente
   `CLAVE=...`.
4. Pasas esa salida al descifrador `SiguienteReto`, o copias solo la parte de
   la clave si lo haces manualmente.
5. Si la clave es correcta, aparece por pantalla el enunciado del siguiente reto.

Importante: el descifrador **no ejecuta ni corrige tu programa**. Solo toma la
clave que tu programa ha producido e intenta descifrar el siguiente enunciado.
Si la clave esta mal, revisa tus calculos o consulta una pista.

Hay dos formas de usarlo:

- Con tuberia (`|`): tu programa imprime `CLAVE=...` y `SiguienteReto` extrae la
  clave de esa ultima linea.
- Manualmente: escribes solo la clave como argumento. No escribas `CLAVE=`.

## Preparacion (JDK 25)

Desde esta carpeta, compila el descifrador:

```bash
javac -d out src/SiguienteReto.java
```

Haz tus programas en una carpeta `equipo-NOMBRE/reto01/`,
`equipo-NOMBRE/reto02/`, etc. Para desbloquear el siguiente reto desde terminal,
ejecuta tu programa y conecta su salida con el descifrador mediante `|`.
Ejemplo desde la carpeta de la gincana:

```bash
javac -d equipo-demo/reto01/out equipo-demo/reto01/Reto01.java
java -cp equipo-demo/reto01/out Reto01 | java -cp out SiguienteReto 1
```

Ese comando significa: "ejecuta mi `Reto01`, toma la linea `CLAVE=...` que
imprime al final, le quita el prefijo `CLAVE=` y usa la clave para abrir el
reto 2".

Sustituye `equipo-demo` por tu carpeta, `Reto01` por tu clase y `1` por el reto
que acabas de resolver. Para pasar del reto 2 al 3, cambia el ultimo numero por
`2`; para pasar del 3 al 4, usa `3`, y asi sucesivamente.

Si trabajas en IntelliJ y prefieres ejecutar alli tu programa, copia la ultima
linea que imprime. Puedes probar la clave manualmente desde esta misma carpeta:

```bash
java -cp out SiguienteReto 1 R1-cantidad-minimo-maximo-media-aparicionesDel25
```

En ese ejemplo debes sustituir `R1-cantidad-minimo-maximo-media-aparicionesDel25`
por la clave real que haya calculado tu programa, **sin escribir `CLAVE=`
delante**. Si pones `CLAVE=...` como argumento manual, la clave sera incorrecta.
No hace falta instalar librerias ni conectarse a Internet.

### Error frecuente: no encuentra `SiguienteReto`

Si aparece un mensaje parecido a `Could not find or load main class
SiguienteReto`, revisa esto antes de pedir ayuda:

1. Estas situado en la carpeta de la gincana, donde estan `README.md`, `src/` y
   `retos-cifrados/`.
2. Has compilado antes el descifrador:

   ```bash
   javac -d out src/SiguienteReto.java
   ```

3. Existe el archivo `out/SiguienteReto.class`.
4. Lo estas ejecutando con `-cp out`:

   ```bash
   java -cp out SiguienteReto 1 TU_CLAVE
   ```

   En ese comando `TU_CLAVE` es solo la clave. No pongas `CLAVE=TU_CLAVE`.

No ejecutes `java SiguienteReto` directamente: Java no sabe buscar la clase si
no le indicas que esta dentro de la carpeta `out/`.

## Reto 1 - Brujula de numeros

Crea `Reto01.java`. Con esta lista de enteros:

```text
25, 18, 25, 41, 9, 30, 25, 12
```

Calcula la cantidad, el minimo, el maximo, la media con **tres decimales** y
cuantas veces aparece el 25. Imprime esos datos para comprobarlos. La ultima
linea debe formarse a partir de tus resultados, exactamente con este formato:

```text
CLAVE=R1-cantidad-minimo-maximo-media-aparicionesDel25
```

Usa punto decimal, sin espacios y con `Locale.ROOT` para que funcione tambien
en equipos configurados en espanol. Por ejemplo, una media de 7 se escribe
`7.000`. No escribas a mano los numeros de la clave: calculalos con el programa.

Cuando hayas generado la clave, desbloquea el siguiente reto. En
[`PISTAS.md`](PISTAS.md) hay dos pistas por reto si te atascas mas de 10 minutos.

## Entrega

Guarda el codigo de los retos completados y un `README.md` del equipo con sus
integrantes, comandos de compilacion/ejecucion, una decision de diseno y algo
aprendido. Entrega un ZIP sin `out/`, `.idea/` ni archivos compilados. No importa
si no llegais al reto 6: es mas importante poder explicar vuestro codigo.
