package com.example.battleship.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
    info = @Info(
        title = "Battleship API",
        version = "1.0.0",
        description = "API REST para jugar al Hundir la Flota con TDD",
        contact = @Contact(name = "DWES", email = "dwes@ies.com"),
        license = @License(name = "MIT")
    )
)
public class OpenApiConfig {}
