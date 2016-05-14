package com.kinitic.crawler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sitemap {
    @JsonProperty("domain")
    String domain;
    @JsonProperty("fragment")
    SitemapFragment sitemapFragment;

    public Sitemap(String domain, SitemapFragment sitemapFragment) {
        this.domain = domain;
        this.sitemapFragment = sitemapFragment;
    }

    public String getDomain() {
        return domain;
    }

    public SitemapFragment getSitemapFragment() {
        return sitemapFragment;
    }
}
