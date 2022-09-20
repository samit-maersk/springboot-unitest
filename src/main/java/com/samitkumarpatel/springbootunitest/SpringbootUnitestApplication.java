package com.samitkumarpatel.springbootunitest;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Objects;

@SpringBootApplication
public class SpringbootUnitestApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootUnitestApplication.class, args);
	}
	@Value("${spring.application.mockservice.host:'http://localhost:8080'}")
	private String host;

	@Bean
	WebClient mockServiceWebClient(){
		return WebClient.builder().baseUrl(host).build();
	}
}

@Configuration
class UniTestRouter {
	@Bean
	public RouterFunction route(UniTestHandler uniTestHandler) {
		return RouterFunctions.route()
				.GET("/get", RequestPredicates.accept(MediaType.APPLICATION_JSON), uniTestHandler::getText)
				.build();

	}
}

//Handler
@Configuration
@RequiredArgsConstructor
class UniTestHandler {
	private final MockService mockService;
	public Mono<ServerResponse> getText(ServerRequest request) {
		return request
				.bodyToMono(String.class)
				.doOnNext(this::validate)
				.flatMap(mySting -> mockService.getMessage(mySting).flatMap(r -> ServerResponse.ok().body(r, String.class)));
	}

	private void validate(@Validated @NonNull String s) {
		if(Objects.isNull(s)) {
			throw new BadRequestException("not valid");
		}
	}
}

//service
@Service
@RequiredArgsConstructor
class MockService {
	private final WebClient mockServiceWebClient;
	public Mono<String> getMessage(String message) {
		return mockServiceWebClient
				.get()
				.uri(uriBuilder -> uriBuilder.path("/hello-world").queryParam("name",message).build())
				.accept(MediaType.TEXT_PLAIN)
				.retrieve()
				.bodyToMono(String.class)
				.flatMap(m -> Mono.just(m));
	}
}

//exception
@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequestException extends RuntimeException {
	BadRequestException(String message) {
		super(message);
	}
	BadRequestException(String message, Throwable t) {
		super(message,t);
	}
}