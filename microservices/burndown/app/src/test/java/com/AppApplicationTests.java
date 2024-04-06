package com;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AppApplicationTests {

	//@LocalServerPort
	private String url = "http://localhost:8081/encrypt";


	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	public void testSuccessCase() {
		// Prepare the JSON payload
		// the payload is a raw single line of text: foo 3

		String payload = "foo 3";

		// Prepare HTTP headers
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		// Create HTTP entity with headers and payload
		HttpEntity<String> entity = new HttpEntity<>(payload, headers);

		// Send POST request to the /aggregator endpoint
		ResponseEntity<String> response = restTemplate.exchange(url,
				HttpMethod.POST, entity, String.class);

		// Assert response
		assertEquals(200, response.getStatusCodeValue());
		assertEquals("irr", response.getBody());
	}

	@Test
	public void testErrorCase1() {
		// Prepare the JSON payload
		// the payload is a raw single line of text: foo 3

		String payload = "3";

		// Prepare HTTP headers
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		// Create HTTP entity with headers and payload
		HttpEntity<String> entity = new HttpEntity<>(payload, headers);

		// Send POST request to the /aggregator endpoint
		ResponseEntity<String> response = restTemplate.exchange(url,
				HttpMethod.POST, entity, String.class);

		// Assert response
		assertEquals(400, response.getStatusCodeValue());

	}

	@Test
	public void testErrorCase2() {
		// Prepare the JSON payload
		// the payload is a raw single line of text: foo 3

		String payload = "irr irr";

		// Prepare HTTP headers
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		// Create HTTP entity with headers and payload
		HttpEntity<String> entity = new HttpEntity<>(payload, headers);

		// Send POST request to the /aggregator endpoint
		ResponseEntity<String> response = restTemplate.exchange(url,
				HttpMethod.POST, entity, String.class);

		// Assert response
		assertEquals(400, response.getStatusCodeValue());

	}
}