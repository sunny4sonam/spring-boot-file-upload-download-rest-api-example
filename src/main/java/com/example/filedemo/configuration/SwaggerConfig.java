package com.example.filedemo.configuration;

import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.base.Predicate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Controller
public class SwaggerConfig {

	@Value("${swagger.uri}")
	private String uri;

	@Value("${swagger.title}")
	private String title;

	@Value("${swagger.description}")
	private String description;

	@Value("${swagger.version}")
	private String version;

	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.example.filedemo.controller")).paths(postPaths()).build()
				.apiInfo(apiInfo());
	}

	private Predicate<String> postPaths() {
		return regex("/file-demo.*");
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title(title).description(description).version(version).build();
	}

	@RequestMapping("/swagger")
	public String home() {
		return "redirect:/swagger-ui.html";
	}

}
