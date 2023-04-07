package com.web.scraper.operation;

import com.web.scraper.standard.HttpOperation;
import com.web.scraper.utils.RequestUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public enum Mendeley implements HttpOperation {
    DO_AUTH("/sign-in?routeTo=https://www.mendeley.com/?interaction_required=true", HttpMethod.GET),
    REFERENCE_LIBRARY("/reference-manager/library/all-references", HttpMethod.GET);

    private final String baseUrl = "https://www.mendeley.com";
    private final String endpoint;
    private final HttpMethod method;

    Mendeley(String endpoint, HttpMethod method) {
        this.endpoint = endpoint;
        this.method = method;
    }

    public String getUrl() {
        return baseUrl.concat(this.endpoint);
    }

    public HttpMethod getMethod() {
        return  this.method;
    }

    @Override
    public HttpHeaders getHeaders() {
        var headers = RequestUtils.basicHeaders();
        return headers;
    }
}
