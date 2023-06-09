package com.web.scraper.utils;

import com.web.scraper.standard.HttpOperation;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UrlUtils {

	private UrlUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static String asString(Map<String, Object> params) {
		if (params == null || params.isEmpty())
			return "";
		return "?".concat(params.entrySet().stream()
				.map(entry -> entry.getKey() + "=" + entry.getValue())
				.collect(Collectors.joining("&")));
	}

	public static String encodeUrl(String url, List<String> pathParams) {
		return encodeUrl(url, pathParams, null);
	}

	public static String encodeUrl(String url, Map<String, Object> queryParams) {
		return encodeUrl(url, null, queryParams);
	}

	public static String encodeUrl(String url, List<String> pathParams, Map<String, Object> queryParams) {
		if (pathParams != null && !pathParams.isEmpty())
			url = String.format(url, pathParams.toArray());
		if (queryParams != null && !queryParams.isEmpty())
			url = url.concat(asString(queryParams));
		return url;
	}

	public static MultiValueMap<String, String> asMultiValueMap(Map<String, Object> params) {
		return params.entrySet().stream()
				.collect(LinkedMultiValueMap::new, (map, entry) -> {
					String key = entry.getKey();
					Object value = entry.getValue();
					if (value instanceof String string) {
						map.add(key, string);
					} else if (value instanceof Collection) {
						Collection<?> collection = (Collection<?>) value;
						for (Object element : collection) {
							map.add(key, String.valueOf(element));
						}
					} else {
						map.add(key, String.valueOf(value));
					}
				}, MultiValueMap::addAll);
	}

    public static String buildUrl(HttpOperation operation, Map<String, Object> params) {
		return UriComponentsBuilder.fromHttpUrl(operation.getUrl())
				.queryParams(asMultiValueMap(params))
				.encode(StandardCharsets.UTF_8)
				.build()
				.toString();
    }
}