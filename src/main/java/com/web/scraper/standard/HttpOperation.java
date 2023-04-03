package com.web.scraper.standard;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public interface HttpOperation {
    String getUrl();
    HttpMethod getMethod();
    HttpHeaders getHeaders();
}
