package com.web.scraper.utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UrlUtils {

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

}