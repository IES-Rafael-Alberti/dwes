using RecetasApi.Api.Infrastructure;
using Microsoft.EntityFrameworkCore;
using RecetasApi.Api.Infrastructure;



var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();

// Swagger (OpenAPI) + UI
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Registrar el repositorio en DI (Program.cs)
//builder.Services.AddSingleton<IRecetasRepository, InMemoryRecetasRepository>();

// Cambiar el registro del repositorio a
builder.Services.AddScoped<IRecetasRepository, EfRecetasRepository>();


// Registrar EF (EntityFramework) en Program.cs
builder.Services.AddDbContext<RecetasDbContext>(opt =>
    opt.UseSqlite(builder.Configuration.GetConnectionString("RecetasDb")));

var app = builder.Build();


if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI(); // /swagger
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();
