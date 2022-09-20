package com.samitkumarpatel.springbootunitest;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

@SpringBootTest
class SpringbootUnitestApplicationTests {

	@Test
	void contextLoads() {
	}

}

@ExtendWith(SpringExtension.class)
@AutoConfigureWireMock(port = 0)
class WireMockTest {

	@Autowired
	WireMockServer wireMockServer;

	private WebClient webClient;
	private MockService mockService;

	@BeforeEach
	void setUp() {
		webClient = WebClient.builder().baseUrl("http://localhost:"+wireMockServer.port()).build();
		mockService = new MockService(webClient);
	}

	@Test
	@DisplayName("Mock Service Test")
	@Tag("MockTest")
	void mockServiceTest() {
		stubFor(get(urlPathEqualTo("/hello-world"))
				.withQueryParam("name", equalTo("samit"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/plain").withBody("Hello World!")));

		StepVerifier.create(mockService.getMessage("samit")).consumeNextWith(res -> {
			Assertions.assertEquals("Hello World!", res);
		}).verifyComplete();
	}
}

@SpringBootTest
@AutoConfigureWireMock(port = 0)
class WireMockTestV1 {
	@Autowired
	Environment environment;
	private WebClient webClient;
	private MockService mockService;

	@BeforeEach
	void setUp() {
		webClient = WebClient.builder().baseUrl("http://localhost:"+this.environment.getProperty("wiremock.server.port")).build();
		mockService = new MockService(webClient);
	}

	@Test
	@DisplayName("Mock Service Test")
	@Tag("MockTest")
	void mockServiceTest() {
		stubFor(get(urlPathEqualTo("/hello-world"))
				.withQueryParam("name", equalTo("samit"))
				.willReturn(aResponse()
				.withHeader("Content-Type", "text/plain").withBody("Hello World!")));

		StepVerifier.create(mockService.getMessage("samit")).consumeNextWith(res -> {
			Assertions.assertEquals("Hello World!", res);
		}).verifyComplete();
	}
}