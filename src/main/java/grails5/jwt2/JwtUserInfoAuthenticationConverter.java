package grails5.jwt2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class JwtUserInfoAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    protected final Log logger = LogFactory.getLog(getClass());

    private URI userInfoUri;
    public void setUserInfoUri(String urlString) {
        try {
            userInfoUri = new URL(urlString).toURI();
        }
        catch(MalformedURLException mue) {
            throw new IllegalArgumentException(mue.toString(), mue);
        } catch (URISyntaxException use) {
            throw new IllegalArgumentException(use.toString(), use);
        }
    }
    public String getUserInfoUri() {
        return userInfoUri.toString();
    }

    /**
     * Modeled after {@link org.springframework.security.oauth2.server.resource.authentication.JwtBearerTokenAuthenticationConverter#convert(Jwt)}
     */
    @Override
    public AbstractAuthenticationToken convert(Jwt token) {
        if(logger.isDebugEnabled()) logger.debug("JWT: " + token.getClaims());
        Map<String,String> cookies = Collections.singletonMap("CF_Authorization", token.getTokenValue());

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        try {
            Map<String, Object> userInfoJson = getJson(cookies);
            Collection groups = (Collection)(userInfoJson.get("groups"));
            groups.forEach((Object groupObj) -> {
                Map group = (Map)groupObj;
                String groupName = (String)group.get("name");
                authorities.add(new SimpleGrantedAuthority(groupName));
            });
        }
        catch(IOException ioe) {
            throw new AuthenticationServiceException(ioe.toString());
        }
        if(logger.isDebugEnabled()) logger.debug("Granted authorities: " + authorities);

        Map<String, Object> attributes = token.getClaims();
        OAuth2AuthenticatedPrincipal principal = new DefaultOAuth2AuthenticatedPrincipal(attributes, authorities); // TODO Unfortunate that the `sub` (a GUID) is used as the Principal name, instead of `email`
        OAuth2AccessToken credentials = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, token.getTokenValue(), token.getIssuedAt(), token.getExpiresAt());
        BearerTokenAuthentication authToken = new BearerTokenAuthentication(principal, credentials, authorities);

        return authToken;
    }

    private Map<String, Object> getJson(Map<String,String> cookieMap) throws IOException {
        String userInfoString = null;

        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpGet userInfoGet = new HttpGet(userInfoUri);
            cookieMap.entrySet().forEach((entry) -> {
                userInfoGet.addHeader("Cookie", entry.getKey() + "=" + entry.getValue());
            });

            CloseableHttpResponse response = null;
            try {
                response = httpClient.execute(userInfoGet);
                userInfoString = EntityUtils.toString(response.getEntity());
            }
            finally {
                if(response != null) response.close();
            }
        }
        finally {
            if(httpClient != null) httpClient.close();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(userInfoString);
        Map<String, Object> result = objectMapper.convertValue(jsonNode, new TypeReference<Map<String, Object>>(){});

        return result;
    }
}
