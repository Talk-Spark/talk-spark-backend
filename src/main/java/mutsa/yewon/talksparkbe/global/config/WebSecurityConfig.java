package mutsa.yewon.talksparkbe.global.config;

import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.global.security.JWTCheckFilter;
import mutsa.yewon.talksparkbe.global.util.JWTUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JWTUtil jwtUtil;
    private final JWTCheckFilter jwtCheckFilter;

    private final String[] Auth_Whitelist = {"/static/js/**", "/static/css/**", "/static/img/**"
            , "/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**", "/api/member/**"};

    @Bean
    public MvcRequestMatcher.Builder mvcRequestMatcher(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {

//        MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspector);
//
//        MvcRequestMatcher[] permitWhiteList = {
//                mvc.pattern("/api/member/**"),
//                mvc.pattern("/static/js/**"),
//                mvc.pattern("/static/css/**"),
//                mvc.pattern("/swagger-ui/**"),
//                mvc.pattern("/v3/api-docs/**"),
//                mvc.pattern("/api-docs/**"),
//        };


        http
                .csrf(CsrfConfigurer<HttpSecurity>::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(FormLoginConfigurer<HttpSecurity>::disable)
                .httpBasic(HttpBasicConfigurer<HttpSecurity>::disable)
                .headers(it -> it.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .sessionManagement(it ->
                        it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
//                .authorizeHttpRequests(auth -> auth.requestMatchers(permitWhiteList).permitAll()
//                        .anyRequest().authenticated())
                .addFilterBefore(jwtCheckFilter, UsernamePasswordAuthenticationFilter.class);

//                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
//                        .requestMatchers("/static/js/**", "/static/css/**", "/static/img/**"
//                                , "/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**").permitAll());

//                .requestMatchers( "/","/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                .authorizeHttpRequests(
//                        authorize -> authorize
//                                .anyRequest().permitAll()
//                );
        return http.build();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS","HEAD"));

        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

//    @Bean
//    public JWTCheckFilter jwtCheckFilter() {
//        return new JWTCheckFilter(jwtUtil);
//    }

}
