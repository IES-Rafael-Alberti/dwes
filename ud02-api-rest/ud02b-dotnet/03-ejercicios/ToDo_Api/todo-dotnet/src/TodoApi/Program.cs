using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using System.Text;
using System.Text.Json.Serialization;
using TodoApi.Data;
using TodoApi.Middleware;
using TodoApi.Options;
using TodoApi.Repositories;
using TodoApi.Services;

// Host/Builder: prepara configuracion, DI y logging.
var builder = WebApplication.CreateBuilder(args);

// Registro de controladores (API REST con atributos).
builder.Services.AddControllers()
    .AddJsonOptions(options =>
    {
        // Serializa enums como strings ("Pending") en lugar de numeros.
        options.JsonSerializerOptions.Converters.Add(new JsonStringEnumConverter());
    });

// Swagger/OpenAPI (solo en desarrollo).
if (builder.Environment.IsDevelopment())
{
    builder.Services.AddEndpointsApiExplorer();
    builder.Services.AddSwaggerGen();
}

// DbContext de EF Core con SQLite (cadena en appsettings.json).
builder.Services.AddDbContext<TodoDbContext>(options =>
    options.UseSqlite(builder.Configuration.GetConnectionString("TodoDb")));
// DI: cada request obtiene su repositorio (scope).
builder.Services.AddScoped<ITasksRepository, TasksEfRepository>();
builder.Services.AddScoped<ITasksService, TasksService>();
builder.Services.AddScoped<IUsersRepository, UsersEfRepository>();
builder.Services.AddScoped<IAuthService, AuthService>();
builder.Services.AddScoped<IJwtTokenService, JwtTokenService>();
// Repositorio in-memory
// builder.Services.AddSingleton<ITasksRepository, TasksInMemoryRepository>();

// JWT: opciones desde appsettings.json (similar a @ConfigurationProperties).
builder.Services.Configure<JwtOptions>(builder.Configuration.GetSection("Jwt"));
var jwtOptions = builder.Configuration.GetSection("Jwt").Get<JwtOptions>() ?? new JwtOptions();

// Autenticacion JWT (equivalente a configurar filtros en Spring Security).
builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidIssuer = jwtOptions.Issuer,
            ValidAudience = jwtOptions.Audience,
            IssuerSigningKey = new SymmetricSecurityKey(
                Encoding.UTF8.GetBytes(jwtOptions.SigningKey)),
            ValidateIssuer = true,
            ValidateAudience = true,
            ValidateIssuerSigningKey = true,
            ValidateLifetime = true
        };
    });

// Construye la app con todo lo registrado.
var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

// Redirige HTTP a HTTPS.
app.UseHttpsRedirection();

// 🔹 FRONTEND ESTÁTICO (wwwroot)
app.UseDefaultFiles();   // busca index.html
app.UseStaticFiles();    // sirve HTML, JS, CSS

// Autenticacion antes de autorizacion.
app.UseAuthentication();
app.UseAuthorization();

// 🔹 Middleware de manejo de errores
app.UseMiddleware<ErrorHandlingMiddleware>();

// API REST: mapea rutas de controladores.
app.MapControllers();

// ❌ Eliminamos la mini app WeatherForecast
// (no la necesitamos para este proyecto)

// Arranque
app.Run();

// Exponemos Program para pruebas de integracion con WebApplicationFactory.
public partial class Program { }
