package com.kinitic.crawler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SitemapFragment {
    @JsonProperty("internal")
    Set<String> internalLinks;
    @JsonProperty("external")
    Set<String> externalLinks;
    @JsonProperty("resource")
    Set<String> resources;

    private SitemapFragment(Set<String> internalLinks, Set<String> externalLinks, Set<String> resources) {
        this.internalLinks = internalLinks;
        this.externalLinks = externalLinks;
        this.resources = resources;
    }

    public Set<String> getInternalLinks() {
        return internalLinks;
    }

    public Set<String> getExternalLinks() {
        return externalLinks;
    }

    public Set<String> getResources() {
        return resources;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SitemapFragment fragment = (SitemapFragment) o;

        if (internalLinks != null ? !internalLinks.equals(fragment.internalLinks) : fragment.internalLinks != null)
            return false;
        if (externalLinks != null ? !externalLinks.equals(fragment.externalLinks) : fragment.externalLinks != null)
            return false;
        return resources != null ? resources.equals(fragment.resources) : fragment.resources == null;

    }

    @Override
    public int hashCode() {
        int result = internalLinks != null ? internalLinks.hashCode() : 0;
        result = 31 * result + (externalLinks != null ? externalLinks.hashCode() : 0);
        result = 31 * result + (resources != null ? resources.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SitemapFragment{" +
                ", internalLinks=" + internalLinks +
                ", externalLinks=" + externalLinks +
                ", resources=" + resources +
                '}';
    }

    public static class SitemapFragmentBuilder {
        private Set<String> internalLinks = new HashSet<>();
        private Set<String> externalLinks = new HashSet<>();
        private Set<String> resources = new HashSet<>();

        public static SitemapFragmentBuilder aSitemapFragmentBuilder() {
            return new SitemapFragmentBuilder();
        }

        public SitemapFragmentBuilder withInternalLinks(Set<String> internalLinks) {
            this.internalLinks = internalLinks;
            return this;
        }

        public SitemapFragmentBuilder withExternalLinks(Set<String> externalLinks) {
            this.externalLinks = externalLinks;
            return this;
        }

        public SitemapFragmentBuilder withResources(Set<String> resources) {
            this.resources = resources;
            return this;
        }

        public SitemapFragment build() {
            return new SitemapFragment(internalLinks, externalLinks, resources);
        }
    }
}
