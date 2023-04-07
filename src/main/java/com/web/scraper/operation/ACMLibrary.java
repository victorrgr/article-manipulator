package com.web.scraper.operation;

import com.web.scraper.standard.HttpOperation;
import com.web.scraper.utils.RequestUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public enum ACMLibrary implements HttpOperation {
    DO_SEARCH("/action/doSearch", HttpMethod.GET);

    private final String baseUrl = "https://dl.acm.org";
    private final String endpoint;
    private final HttpMethod method;

    ACMLibrary(String endpoint, HttpMethod method) {
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
