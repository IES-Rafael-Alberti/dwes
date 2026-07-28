// Problema: conflicto de nombres entre System.Threading.Tasks.TaskStatus y TodoApi.Models.TaskStatus.
// Solucion: definir un alias global para usar TaskStatus como TodoApi.Models.TaskStatus en todo el proyecto.
// Alternativas: usar el nombre completo en cada uso, o crear un alias local con "using TaskStatus = ...;" por archivo.
global using TaskStatus = TodoApi.Models.TaskStatus;
