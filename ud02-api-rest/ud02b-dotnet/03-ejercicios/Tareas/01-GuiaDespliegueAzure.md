Perfecto 👍
Te dejo **la guía reescrita directamente para el alumnado**, con tono claro, instrucciones directas y sin referencias al “profe”. Está pensada para **seguirla paso a paso desde VS Code**.

---

# 📘 GUÍA PARA EL ALUMNADO

## Despliegue de una API .NET en **Azure** usando **Visual Studio Code**

### Objetivo de la tarea

Al final de esta guía tendrás tu **API REST .NET publicada en Internet**, con una **URL pública HTTPS** accesible desde cualquier sitio (incluido Swagger).

No se usan servidores manuales. Usamos **Azure App Service**, un servicio PaaS (Platform as a Service).

---

## 1️⃣ Crear tu cuenta de **Azure for Students**

Azure ofrece una cuenta gratuita para estudiantes con **100 $ de crédito** y **sin tarjeta bancaria**.

### Pasos

1. Abre esta web:
   👉 [https://azure.microsoft.com/free/students](https://azure.microsoft.com/free/students)
2. Inicia sesión con tu **cuenta Microsoft**

   * Puede ser educativa o personal.
3. Sigue el proceso de verificación de estudiante.
4. Finaliza el registro.

Cuando termines:

* Entra en 👉 [https://portal.azure.com](https://portal.azure.com)
* Debes ver una suscripción llamada **Azure for Students**

> ⚠️ Si ya usaste Azure for Students hace años y está caducado, avisa.

---

## 2️⃣ Preparar tu entorno en **Visual Studio Code**

### Extensiones obligatorias en VS Code

Instala estas extensiones:

* **Azure Account**
* **Azure App Service**
* **C# Dev Kit** (o C#)

---

### Azure CLI (muy importante)

Abre un terminal y escribe:

```bash
az version
```

Si no está instalado:

* **Linux**

```bash
curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash
```

* **macOS**

```bash
brew install azure-cli
```

* **Windows**
  Instalador oficial de Azure CLI.

Después, inicia sesión:

```bash
az login
```

Se abrirá el navegador → inicia sesión → vuelve al terminal.

Comprueba que tienes la suscripción correcta:

```bash
az account show
```

Debe aparecer **Azure for Students**.

---

## 3️⃣ Comprobar que tu API funciona en local

Desde la carpeta del proyecto:

```bash
dotnet build
dotnet run
```

Comprueba en el navegador:

* Que la API arranca
* Que Swagger funciona

👉 **Importante**
No fijes el puerto manualmente en el código.
En `Program.cs` debe bastar con:

```csharp
app.Run();
```

Azure asigna el puerto automáticamente.

---

## 4️⃣ Crear el App Service desde VS Code

1. Abre VS Code.
2. Ve al icono **Azure** ☁️ (barra lateral).
3. En **App Service**, pulsa **Create New Web App**.

Responde a las preguntas:

* **Subscription** → Azure for Students
* **Name** → nombre único (ej. `api-dotnet-tu-nombre`)
* **Runtime** → `.NET 8 (LTS)`
* **Operating System** → Linux
* **Region** → West Europe
* **Plan** → Free o Basic

VS Code creará automáticamente:

* Grupo de recursos
* App Service
* Plan

---

## 5️⃣ Desplegar tu API en Azure

1. En el panel de Azure de VS Code:

   * Busca tu App Service
2. Clic derecho → **Deploy to Web App**
3. Selecciona el proyecto actual
4. Confirma el despliegue

VS Code:

* Compila el proyecto
* Lo sube a Azure
* Reinicia la aplicación

Espera 1–2 minutos.

---

## 6️⃣ Comprobar el resultado

En el App Service:

* Pulsa **Browse Website**

La URL será algo como:

```
https://api-dotnet-tu-nombre.azurewebsites.net/swagger
```

Comprueba:

* Swagger carga
* Los endpoints responden

🎉 Tu API está en producción.

---

## 7️⃣ Alternativa (si falla el despliegue desde VS Code)

1. Sube el proyecto a **GitHub**.
2. Entra en Azure Portal → App Service.
3. Ve a **Deployment Center**.
4. Selecciona **GitHub**.
5. Elige repositorio y rama.

Cada `git push` redepliega la API automáticamente.

---

## 8️⃣ Qué tienes que entregar

Entrega obligatoria:

* ✅ URL pública de la API
* ✅ Captura de Swagger funcionando
* ✅ Enlace al repositorio GitHub

Opcional (para nota extra):

* Explicación breve de:

  * Qué es Azure App Service
  * Qué pasos has seguido para desplegar

---

## 9️⃣ Problemas habituales

* ❌ No aparece Swagger
  → En App Service → Configuration →
  `ASPNETCORE_ENVIRONMENT = Development`

* ❌ Error 502
  → Revisa:

  * App Service → Log stream

* ❌ La app no arranca
  → Quita cualquier puerto fijo (`localhost:xxxx`)

---

## 10️⃣ Qué estás aprendiendo con esto

* Despliegue real en la nube
* Uso de PaaS profesional
* Ciclo completo:

  **API → Código → Cloud → URL pública**

Esto es exactamente lo que se hace en proyectos reales.

---

Cuando quieras, sigo con:

➡️ **Guía para AWS Academy + App Runner (lenguaje alumnado)**
➡️ **Versión resumida para Moodle (1 página)**

---
Perfecto, aclarado 👍.
Esto **sí es Azure**, y aquí el problema es **estructural**, no vuestro.

Voy directo y claro.

---

## 🔍 Qué está pasando con **Azure for Students**

El portal **Azure for Students** (verificación automática) **NO reconoce** muchos dominios educativos españoles, entre ellos:

* `@iesrafaelalberti.es`
* `@g.educaand.es`

Esto **no es un error de configuración**, ni vuestro ni del alumno.

### Motivo real

Microsoft **solo valida automáticamente**:

* dominios universitarios claros
* dominios que Microsoft tenga previamente catalogados como “Higher Education”

Los dominios:

* de FP
* de consejerías autonómicas
* de centros de secundaria

👉 **suelen fallar en la verificación automática**.

Esto es **muy común en FP en España**.

---

## ❌ Qué NO hacer (importante)

* ❌ No insistir en que “prueben otra vez”
* ❌ No cambiar correos del centro
* ❌ No perder una clase entera con esto
* ❌ No condicionar prácticas o examen a Azure for Students

---

## ✅ Soluciones reales (ordenadas por lo que mejor funciona)

---

## 🟢 OPCIÓN 1 – Azure con **cuenta personal + verificación manual**

*(la única “oficial” que funciona en FP)*

### Qué deben hacer los alumnos

1. Crear cuenta Microsoft con **correo personal**

   * Gmail / Outlook / Hotmail
2. Ir a **Azure for Students**
3. Cuando falle la verificación automática:

   * elegir **verificación manual**
4. Subir **documento oficial**, por ejemplo:

   * matrícula
   * certificado del centro
   * justificante de estudios sellado

📌 Tarda **1–3 días** (a veces más).

⚠️ **NO sirve para algo inmediato**.

---

## 🟢 OPCIÓN 2 – Azure Free normal (para el curso y el examen)

**(RECOMENDADA para no bloquear nada)**

👉 Usar **Azure Free**, NO “Azure for Students”.

### Qué implica

* Cuenta Microsoft personal
* Tarjeta bancaria (puede ser virtual/prepago)
* 200 $ de crédito inicial
* Muchos servicios gratuitos 12 meses

📌 Para:

* APIs
* App Service
* contenedores
* pruebas básicas

👉 **es suficiente** para lo que estáis haciendo.

✔️ Funciona el mismo día
✔️ No depende de correos educativos
✔️ No bloquea al alumnado
