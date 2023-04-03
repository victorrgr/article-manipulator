package com.web.scraper.controller;

import com.web.scraper.data.enums.Library;
import com.web.scraper.service.ArticleService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/article")
public class ArticleController {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private ArticleService articleService;

    @PostMapping("/importAcmLibrary")
    public ResponseEntity<Object> importAcmLibrary(@RequestParam String afterMonth,
                                                   @RequestParam String beforeMonth,
                                                   @RequestParam String afterYear,
                                                   @RequestParam String beforeYear,
                                                   @RequestParam String query) throws InterruptedException {
        return ResponseEntity.ok(articleService.importAcmLibraryArticles(afterMonth, beforeMonth, afterYear, beforeYear, query));
    }

    @PostMapping("/exportToMendeley")
    public ResponseEntity<Object> exportToMendeley(@RequestParam Library library) throws InterruptedException {
        return ResponseEntity.ok(articleService.processMendeleyExport(library));
    }

}
