package com.web.scraper.data.entity;

import com.web.scraper.data.enums.Library;
import com.web.scraper.utils.Utils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Article {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String abstractt;

    @Column(unique = true)
    private String doiLink;

    private Library library;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean exported;

    @Override
    public String toString() {
        return Utils.toString(this.getClass(), this);
    }
}
