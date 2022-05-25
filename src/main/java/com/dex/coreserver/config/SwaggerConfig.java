package com.dex.coreserver.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${email.address}")
    private String email;
    @Value("${swagger.admin.site}")
    private String site;
    @Value("${swagger.title}")
    private String swaggerTitle;
    @Value("${swagger.description}")
    private String swaggerDescription;
    @Value("${swagger.version}")
    private String swaggerVersion;


    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String DEFAULT_INCLUDE_PATTERN = "/api/.*";


    @Bean
    public Docket swaggerConfiguration() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(regex(DEFAULT_INCLUDE_PATTERN))
                .build()
                .apiInfo(apiDetails())
                .globalOperationParameters(Collections.singletonList(authHeader()));
    }

    private Parameter authHeader(){
        return new ParameterBuilder()
                .parameterType("header")
                .name("Authorization")
                .modelRef(new ModelRef("string"))
                .build();
    }

    private ApiInfo apiDetails() {
        return new ApiInfo(
                swaggerTitle,
                swaggerDescription,
                swaggerVersion,
                "",
                new Contact("DEX",site,email),
                "",
                "",
                Collections.emptyList()
        );
    }

//    private ApiKey apiKey() {
//        return new ApiKey("jwtToken", AUTHORIZATION_HEADER, "Header");
//    }
//
//    private SecurityContext securityContext() {
//        return SecurityContext.builder()
//                .securityReferences(defaultAuth())
//                .forPaths(regex(DEFAULT_INCLUDE_PATTERN))
//                .build();
//    }
//
//    List<SecurityReference> defaultAuth() {
//        AuthorizationScope authorizationScope
//                = new AuthorizationScope("global", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        return Lists.newArrayList(
//                new SecurityReference("JWT", authorizationScopes));
//    }
}
