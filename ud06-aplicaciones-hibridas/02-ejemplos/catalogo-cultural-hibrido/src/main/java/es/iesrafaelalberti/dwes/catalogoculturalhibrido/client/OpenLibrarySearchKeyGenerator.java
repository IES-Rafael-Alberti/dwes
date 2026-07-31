package es.iesrafaelalberti.dwes.catalogoculturalhibrido.client;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * Cache key for Open Library searches.
 *
 * <p>The key is the <em>normalized</em> query — trimmed and lower-cased, so
 * {@code "  DON QUIXOTE "} and {@code "don quixote"} map to the same entry — plus
 * the bounded {@code limit}, which is part of the provider request and therefore
 * part of the response identity. Two searches that differ only in case or
 * surrounding whitespace must never duplicate a provider call.</p>
 */
@Component("openLibrarySearchKeyGenerator")
public class OpenLibrarySearchKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        String query = params[0] == null ? "" : ((String) params[0]).trim().toLowerCase(Locale.ROOT);
        int limit = (Integer) params[1];
        return query + "::" + limit;
    }
}
