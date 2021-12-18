package org.dbs.pi4j.myrestgpio.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    public static final String DEFAULT_INCLUDE_PATTERN ="/api/.*";
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select() //
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.regex(DEFAULT_INCLUDE_PATTERN)).build()//
            .apiInfo(apiInfo());
    }

    ApiInfo apiInfo() {
        return new ApiInfoBuilder()//
            .title("Swagger gpio service") //
            .description("No description provided") //
            .license("private use") //
            .licenseUrl("none") //
            .termsOfServiceUrl("") //
            .version("1.0") //
            .contact(new Contact("", "", "daniel@missud.net")) //
            .build();
    }

}
