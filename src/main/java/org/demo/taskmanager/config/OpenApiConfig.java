package org.demo.taskmanager.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.demo.taskmanager.model.User;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;

        // 
@Configuration
public class OpenApiConfig {

    static {
        // Ignorer les types liés à la sécurité pour éviter les erreurs d'introspection
        SpringDocUtils.getConfig()
                .addRequestWrapperToIgnore(User.class)
                .addRequestWrapperToIgnore(Principal.class)
                .addRequestWrapperToIgnore(Authentication.class)
                .addRequestWrapperToIgnore(UserDetails.class)
                .addRequestWrapperToIgnore(GrantedAuthority.class);
    }

    @Bean
    public OpenAPI taskManagerOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .info(new Info()
                        .title("TaskManager API")
                        .description("API REST de gestion de tâches avec JWT et documentation OpenAPI 3.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("DevOps Team")
                                .email("contact@taskmanager.demo"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}
