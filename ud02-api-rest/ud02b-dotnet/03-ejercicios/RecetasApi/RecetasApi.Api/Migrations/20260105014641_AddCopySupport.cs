using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace RecetasApi.Api.Migrations
{
    /// <inheritdoc />
    public partial class AddCopySupport : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<long>(
                name: "OriginalRecetaId",
                table: "Recetas",
                type: "INTEGER",
                nullable: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "OriginalRecetaId",
                table: "Recetas");
        }
    }
}
