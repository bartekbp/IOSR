package pl.edu.agh.kaflog.master;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import pl.edu.agh.kaflog.common.utils.HiveUtils;
import pl.edu.agh.kaflog.master.monitoring.ProducerMonitoring;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Kaflog master node, and web interface
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableScheduling
public class Main extends SpringBootServletInitializer {
    @Autowired
    ProducerMonitoring producerMonitoring;

    public static void main(String... args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Main.class);
    }

    @Scheduled(fixedRate = 20000)
    public void listClients() throws SQLException {

    }

    /**
     * Creates ApplicationSecurity Bean
     * @return
     */
    @Bean
    public ApplicationSecurity applicationSecurity() {
        return new ApplicationSecurity();
    }

    /**
     * creates bean responsible for authentication
     * Very simple authentication: admin admin
     * @return
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        return new AuthenticationManager() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String name = authentication.getName();
                String password = authentication.getCredentials().toString();

                if ("admin".equals(name) && "admin".equals(password)) {
                    List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
                    return new UsernamePasswordAuthenticationToken(name, password, grantedAuths);
                } else {
                    throw new BadCredentialsException("Unable to auth");
                }
            }
        };
    }

    /**
     * Creates HiveUtils bean
     * @return
     */
    @Bean
    public HiveUtils hiveUtils() {
        return new HiveUtils();
    }

    /**
     * ApplicationSecurity
     * Configure sub-pages availability and login page
     */
    protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers("/login", "/logout", "/webjars/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/login").defaultSuccessUrl("/").failureUrl("/login?error").permitAll();

            http.logout().logoutUrl("/logout").logoutSuccessUrl("/login?logout");

            http.csrf().disable();
        }
    }
}