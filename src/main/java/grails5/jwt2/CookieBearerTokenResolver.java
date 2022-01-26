package grails5.jwt2;

import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Returns the value of a named cookie (see {@link #cookieName}). Useful for getting a JWT token from Cloudflare Access'
 * CF_Authorization cookie.
 */
public class CookieBearerTokenResolver implements BearerTokenResolver {

    String cookieName;

    public CookieBearerTokenResolver() {
    }

    public CookieBearerTokenResolver(String cookieName) {
        this.cookieName = cookieName;
    }

    @Override
    public String resolve(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        Cookie cookie = Arrays.stream(cookies)
                .filter(aCookie -> aCookie.getName().equals(cookieName))
                .findFirst()
                .orElse(null);
        if(cookie == null) return null;
        return cookie.getValue();
    }
}
