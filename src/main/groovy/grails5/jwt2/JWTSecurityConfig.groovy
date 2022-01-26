package grails5.jwt2

import groovy.transform.CompileStatic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@CompileStatic
@Configuration
@EnableWebSecurity
class JWTSecurityConfig extends WebSecurityConfigurerAdapter {
  JWTSecurityConfig() {
    System.out.println(">>> JWTSecurityConfig.ctor()")
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    super.configure(http)
    System.out.println(">>> JWTSecurityConfig.configure(HttpSecurity)")
  }

  @Override
  void configure(WebSecurity web) throws Exception {
    super.configure(web)
    System.out.println(">>> JWTSecurityConfig.configure(WebSecurity)")
  }

  @Bean
  @Override
  AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
  }
}

/*

Alternate configure() body:

        http.authorizeRequests(authz -> authz
                .anyRequest().authenticated()
//             // Use this one to match the role I have in AD
//            .antMatchers(HttpMethod.GET, "/foo/findAll").hasAuthority("Global Administrator")
//
//            // Use this one to test someone NOT having a role
//            .antMatchers(HttpMethod.GET, "/foo/findSecret").hasAuthority("SuperDuperAdmin")
//
//            .anyRequest().anonymous()
        );
        http.oauth2ResourceServer(oauth2 -> oauth2
            .jwt()
            .jwtAuthenticationConverter(getAuthConverter())
            .and().bearerTokenResolver(new CookieBearerTokenResolver("CF_Authorization"))
        );
*/

/*

    @Bean
    public JwtUserInfoAuthenticationConverter getAuthConverter() {
        JwtUserInfoAuthenticationConverter converter = new JwtUserInfoAuthenticationConverter();
        converter.setUserInfoUri("https://vpstech-int.cloudflareaccess.com/cdn-cgi/access/get-identity");
        return converter;
    }

 */