package es.iesrafaelalberti.dwes.catalogoculturalhibrido;

import es.iesrafaelalberti.dwes.catalogoculturalhibrido.client.OpenLibraryProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(OpenLibraryProperties.class)
public class CatalogoCulturalHibridoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogoCulturalHibridoApplication.class, args);
    }
}
