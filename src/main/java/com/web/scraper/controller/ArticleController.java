package com.web.scraper.controller;

import com.web.scraper.data.entity.Article;
import com.web.scraper.data.enums.Library;
import com.web.scraper.data.repository.ArticleRepository;
import com.web.scraper.service.ArticleService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping
    public ResponseEntity<List<Article>> findAll() {
        var articles = articleRepository.findAll();
        return ResponseEntity.status(articles.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK)
                .body(articles);
    }

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
