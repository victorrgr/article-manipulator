package com.web.scraper.data.repository;

import com.web.scraper.data.entity.Article;
import com.web.scraper.data.enums.Library;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByLibraryAndExportedIsFalseAndDoiLinkIsNotNull(Library library);
}
