package com.web.scraper.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.scraper.standard.HttpOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class RequestUtils {
	private static final Log logger = LogFactory.getLog(RequestUtils.class);
	private static ObjectMapper mapper;
    private static RestTemplate restTemplate;

	public RequestUtils(ObjectMapper objectMapper, Utils utils) {
		RequestUtils.mapper = objectMapper;
        RequestUtils.restTemplate = buildRestTemplate();
	}

    private RestTemplate buildRestTemplate() {
        var factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
        var client = new RestTemplate(factory);
        return client;
    }

	public static HttpHeaders basicHeaders() {
		var headers = new HttpHeaders();
		headers.add("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
		return headers;
	}

	/**
	 * Método genérico para fazer requisições HTTP com corpo JSON retornando o
	 * ResponseEntity sem exception mesmo se ocorrer erros HTTP na requisição
	 * @param url url do destino da requisição
	 * @param method método para executar a requisição
	 * @param body o corpo para a requisição
	 * @return a resposta para a requisição enviada
	 */
	public static ResponseEntity<String> doRequestHandled(String url, HttpMethod method, String body) {
		var headers = basicHeaders();
		headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		return doRequest(url, method, headers, body, false);
	}

	public static ResponseEntity<String> doRequest(String url, HttpMethod method, String body) {
		var headers = basicHeaders();
		headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		return doRequest(url, method, headers, body, true);
	}

	public static ResponseEntity<String> doRequest(HttpOperation operacao) {
		return doRequest(operacao, null, null, null);
	}

	public static ResponseEntity<String> doRequest(HttpOperation operacao, Object body) {
		return doRequest(operacao, body, null, null);
	}

	public static ResponseEntity<String> doRequest(HttpOperation operacao, List<String> pathParams) {
		return doRequest(operacao, null, pathParams, null);
	}

	public static ResponseEntity<String> doRequest(HttpOperation operacao, Object body, List<String> pathParams) {
		return doRequest(operacao, body, pathParams, null);
	}

	public static ResponseEntity<String> doRequest(HttpOperation operacao, Map<String,Object> queryParams) {
		return doRequest(operacao, null, null, queryParams);
	}

	public static ResponseEntity<String> doRequest(HttpOperation operacao, Object body, Map<String,Object> queryParams) {
		return doRequest(operacao, body, null, queryParams);
	}

	public static ResponseEntity<String> doRequest(HttpOperation operacao, Object body, List<String> pathParams, Map<String,Object> queryParams) {
		try {
			var url = UrlUtils.encodeUrl(operacao.getUrl(), pathParams, queryParams);
			String json = body != null ? mapper.writeValueAsString(body) : null;
			return doRequest(url, operacao.getMethod(), operacao.getHeaders(), json, true);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Invalid Body.");
		}
	}

	/**
	 * Método genérico para fazer requisições HTTP com corpo JSON com a opção de
	 * escolher os headers
	 * @param url url do destino da requisição
	 * @param method método para executar a requisição
	 * @param headers os cabeçalhos para a requisição
	 * @param body o corpo para a requisição
	 * @return a resposta para a requisição enviada
	 */
	public static ResponseEntity<String> doRequest(String url, HttpMethod method, HttpHeaders headers, String body) {
		return doRequest(url, method, headers, body, true);
	}

	/**
	 * Método genérico para fazer requisições HTTP com o código da execução da requisição
	 * @param url url do destino da requisição
	 * @param method método para executar a requisição
	 * @param headers os cabeçalhos para a requisição
	 * @param body o corpo para a requisição
	 * @param exception True para lançar exception para erros 400 e 500, False para
	 *                  mandar o response completo independente do status
	 * @return a resposta para a requisição enviada
	 */
	public static ResponseEntity<String> doRequest(String url, HttpMethod method, HttpHeaders headers, String body,
			boolean exception) {
		try {
			return restTemplate.exchange(url, method, new HttpEntity<>(body, headers), String.class);
		} catch (HttpStatusCodeException e) {
			if (exception)
				throw e;
			return new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
		}
	}

}