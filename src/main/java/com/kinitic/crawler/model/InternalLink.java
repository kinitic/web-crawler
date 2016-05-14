package com.kinitic.crawler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public class InternalLink {
    @JsonProperty("frag")
    SitemapFragment sitemapFragment;

    private InternalLink(SitemapFragment sitemapFragment) {
        this.sitemapFragment = sitemapFragment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InternalLink that = (InternalLink) o;

        return sitemapFragment != null ? sitemapFragment.equals(that.sitemapFragment) : that.sitemapFragment == null;

    }

    @Override
    public int hashCode() {
        return sitemapFragment != null ? sitemapFragment.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "InternalLink{" +
                "sitemapFragment=" + sitemapFragment +
                '}';
    }

    @JsonValue
    public SitemapFragment getSitemapFragment() {
        return sitemapFragment;
    }

    public static class InternalLinkBuilder {
        private SitemapFragment sitemapFragment;

        public static InternalLinkBuilder anInternalLinkBuilder() {
            return new InternalLinkBuilder();
        }

        public InternalLinkBuilder withSitemapFragment(SitemapFragment sitemapFragment) {
            this.sitemapFragment = sitemapFragment;
            return this;
        }

        public InternalLink build() {
            return new InternalLink(sitemapFragment);
        }
    }
}
