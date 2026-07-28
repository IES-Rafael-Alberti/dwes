using System.Net;
using System.Net.Http.Json;
using System.Security.Claims;
using System.Text.Encodings.Web;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.DependencyInjection.Extensions;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using TodoApi.Data;
using TodoApi.Models;

namespace TodoApi.Tests.Integration;

public class TaskAuthorizationIntegrationTests
{
    [Fact]
    public async Task Tasks_WithoutAuthHeaders_Returns401()
    {
        await using var ctx = await IntegrationContext.CreateAsync();
        var response = await ctx.Client.GetAsync("/api/tasks");
        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }

    [Fact]
    public async Task Supervisor_GetAll_CanSeeAllTasks()
    {
        await using var ctx = await IntegrationContext.CreateAsync();

        using var request = CreateAuthRequest(HttpMethod.Get, "/api/tasks", userId: 3, role: UserRole.Supervisor);
        var response = await ctx.Client.SendAsync(request);
        if (!response.IsSuccessStatusCode)
        {
            var body = await response.Content.ReadAsStringAsync();
            throw new InvalidOperationException(
                $"GET /api/tasks fallo ({(int)response.StatusCode}). Body: {body}");
        }

        var items = await response.Content.ReadFromJsonAsync<List<object>>();
        Assert.NotNull(items);
        Assert.Equal(2, items.Count);
    }

    [Fact]
    public async Task Supervisor_DeleteForeignTask_Returns403()
    {
        await using var ctx = await IntegrationContext.CreateAsync();

        using var request = CreateAuthRequest(
            HttpMethod.Delete,
            $"/api/tasks/{ctx.ForeignTaskId}",
            userId: 3,
            role: UserRole.Supervisor);
        var response = await ctx.Client.SendAsync(request);

        Assert.Equal(HttpStatusCode.Forbidden, response.StatusCode);
    }

    [Fact]
    public async Task Supervisor_CompleteForeignTask_Returns403()
    {
        await using var ctx = await IntegrationContext.CreateAsync();

        var body = new
        {
            title = "Tarea de otro usuario",
            description = "Intento de completar ajena",
            dueDate = DateTime.UtcNow.AddDays(3),
            status = "Completed"
        };

        using var request = CreateAuthRequest(
            HttpMethod.Put,
            $"/api/tasks/{ctx.ForeignTaskId}",
            userId: 3,
            role: UserRole.Supervisor);
        request.Content = JsonContent.Create(body);
        var response = await ctx.Client.SendAsync(request);

        Assert.Equal(HttpStatusCode.Forbidden, response.StatusCode);
    }

    [Fact]
    public async Task Admin_DeleteForeignTask_Returns204()
    {
        await using var ctx = await IntegrationContext.CreateAsync();

        using var request = CreateAuthRequest(
            HttpMethod.Delete,
            $"/api/tasks/{ctx.ForeignTaskId}",
            userId: 4,
            role: UserRole.Admin);
        var response = await ctx.Client.SendAsync(request);

        Assert.Equal(HttpStatusCode.NoContent, response.StatusCode);
    }

    private static HttpRequestMessage CreateAuthRequest(HttpMethod method, string url, int userId, UserRole role)
    {
        var request = new HttpRequestMessage(method, url);
        request.Headers.Add(TestAuthHandler.UserIdHeader, userId.ToString());
        request.Headers.Add(TestAuthHandler.RoleHeader, role.ToString());
        return request;
    }

    private sealed class IntegrationContext : IAsyncDisposable
    {
        public WebApplicationFactory<Program> Factory { get; }
        public HttpClient Client { get; }
        public int ForeignTaskId { get; }
        private readonly string _dbPath;

        private IntegrationContext(
            WebApplicationFactory<Program> factory,
            HttpClient client,
            string dbPath,
            int foreignTaskId)
        {
            Factory = factory;
            Client = client;
            _dbPath = dbPath;
            ForeignTaskId = foreignTaskId;
        }

        public static Task<IntegrationContext> CreateAsync()
        {
            var dbPath = Path.Combine(Path.GetTempPath(), $"todoapi-int-{Guid.NewGuid():N}.db");
            var factory = new TestWebAppFactory(dbPath);
            var client = factory.CreateClient(new WebApplicationFactoryClientOptions
            {
                BaseAddress = new Uri("https://localhost"),
                AllowAutoRedirect = false
            });

            var foreignTaskId = Seed(factory.Services);
            return Task.FromResult(new IntegrationContext(factory, client, dbPath, foreignTaskId));
        }

        private static int Seed(IServiceProvider services)
        {
            using var scope = services.CreateScope();
            var db = scope.ServiceProvider.GetRequiredService<TodoDbContext>();
            db.Database.EnsureDeleted();
            db.Database.EnsureCreated();

            db.Users.AddRange(
                new User { Id = 1, Username = "user1", Email = "user1@example.com", PasswordHash = "x", Role = UserRole.User },
                new User { Id = 2, Username = "user2", Email = "user2@example.com", PasswordHash = "x", Role = UserRole.User },
                new User { Id = 3, Username = "supervisor", Email = "supervisor@example.com", PasswordHash = "x", Role = UserRole.Supervisor },
                new User { Id = 4, Username = "admin", Email = "admin@example.com", PasswordHash = "x", Role = UserRole.Admin });

            var ownTask = new TodoTask
            {
                Id = 101,
                Title = "Tarea usuario 1",
                Description = "Propia",
                CreationDate = DateTime.UtcNow.AddMinutes(-30),
                DueDate = DateTime.UtcNow.AddDays(2),
                Status = TaskStatus.Pending,
                UserId = 1
            };

            var foreignTask = new TodoTask
            {
                Id = 102,
                Title = "Tarea de otro usuario",
                Description = "Ajena para supervisor",
                CreationDate = DateTime.UtcNow.AddMinutes(-20),
                DueDate = DateTime.UtcNow.AddDays(2),
                Status = TaskStatus.InProgress,
                UserId = 2
            };

            db.Tasks.AddRange(ownTask, foreignTask);
            db.SaveChanges();

            return foreignTask.Id;
        }

        public ValueTask DisposeAsync()
        {
            Client.Dispose();
            Factory.Dispose();
            if (File.Exists(_dbPath))
                File.Delete(_dbPath);
            return ValueTask.CompletedTask;
        }
    }

    private sealed class TestWebAppFactory : WebApplicationFactory<Program>
    {
        private readonly string _dbPath;

        public TestWebAppFactory(string dbPath)
        {
            _dbPath = dbPath;
        }

        protected override void ConfigureWebHost(IWebHostBuilder builder)
        {
            builder.UseEnvironment("Testing");
            builder.ConfigureServices(services =>
            {
                services.RemoveAll<DbContextOptions<TodoDbContext>>();
                services.AddDbContext<TodoDbContext>(options =>
                    options.UseSqlite($"Data Source={_dbPath}"));

                services.AddAuthentication(options =>
                    {
                        options.DefaultAuthenticateScheme = TestAuthHandler.SchemeName;
                        options.DefaultChallengeScheme = TestAuthHandler.SchemeName;
                    })
                    .AddScheme<AuthenticationSchemeOptions, TestAuthHandler>(
                        TestAuthHandler.SchemeName,
                        _ => { });
            });
        }
    }

    private sealed class TestAuthHandler : AuthenticationHandler<AuthenticationSchemeOptions>
    {
        public const string SchemeName = "TestAuth";
        public const string UserIdHeader = "X-Test-UserId";
        public const string RoleHeader = "X-Test-Role";

        public TestAuthHandler(
            IOptionsMonitor<AuthenticationSchemeOptions> options,
            ILoggerFactory logger,
            UrlEncoder encoder)
            : base(options, logger, encoder)
        {
        }

        protected override Task<AuthenticateResult> HandleAuthenticateAsync()
        {
            if (!Request.Headers.TryGetValue(UserIdHeader, out var userIdRaw)
                || !Request.Headers.TryGetValue(RoleHeader, out var roleRaw))
            {
                return Task.FromResult(AuthenticateResult.Fail("Missing test auth headers."));
            }

            var claims = new[]
            {
                new Claim("sub", userIdRaw.ToString()),
                new Claim(ClaimTypes.NameIdentifier, userIdRaw.ToString()),
                new Claim(ClaimTypes.Role, roleRaw.ToString())
            };
            var identity = new ClaimsIdentity(claims, SchemeName);
            var principal = new ClaimsPrincipal(identity);
            var ticket = new AuthenticationTicket(principal, SchemeName);

            return Task.FromResult(AuthenticateResult.Success(ticket));
        }
    }
}
