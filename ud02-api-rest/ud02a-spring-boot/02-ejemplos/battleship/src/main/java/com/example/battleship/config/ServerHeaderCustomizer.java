package com.example.battleship.config;

import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class ServerHeaderCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addContextCustomizers(ctx -> ctx.setDisplayName(null));
        factory.addConnectorCustomizers(connector -> {
            connector.setProperty("server", " ");
        });
    }
}
