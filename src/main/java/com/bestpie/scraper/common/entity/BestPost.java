package com.bestpie.scraper.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "best_post", uniqueConstraints = {@UniqueConstraint(columnNames = {"site_name", "title"})})
@Getter @Setter @NoArgsConstructor
public class BestPost implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(name = "site_name")
    private String siteName;

    @Column(name = "title")
    private String title;

    @Column(name = "url")
    private String url;
}
