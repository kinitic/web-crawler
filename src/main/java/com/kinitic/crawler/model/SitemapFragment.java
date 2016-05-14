package com.kinitic.crawler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SitemapFragment {
    @JsonProperty("url")
    String url;
    @JsonProperty("internal")
    List<InternalLink> internalLinks;
    @JsonProperty("external")
    List<ExternalLink> externalLinks;
    @JsonProperty("resource")
    List<Resource> resources;

    private SitemapFragment(String url, List<InternalLink> internalLinks, List<ExternalLink> externalLinks, List<Resource> resources) {
        this.url = url;
        this.internalLinks = internalLinks;
        this.externalLinks = externalLinks;
        this.resources = resources;
    }

    public String getUrl() {
        return url;
    }

    public List<InternalLink> getInternalLinks() {
        return internalLinks;
    }

    public List<ExternalLink> getExternalLinks() {
        return externalLinks;
    }

    public List<Resource> getResources() {
        return resources;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SitemapFragment fragment = (SitemapFragment) o;

        if (url != null ? !url.equals(fragment.url) : fragment.url != null) return false;
        if (internalLinks != null ? !internalLinks.equals(fragment.internalLinks) : fragment.internalLinks != null)
            return false;
        if (externalLinks != null ? !externalLinks.equals(fragment.externalLinks) : fragment.externalLinks != null)
            return false;
        return resources != null ? resources.equals(fragment.resources) : fragment.resources == null;

    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (internalLinks != null ? internalLinks.hashCode() : 0);
        result = 31 * result + (externalLinks != null ? externalLinks.hashCode() : 0);
        result = 31 * result + (resources != null ? resources.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SitemapFragment{" +
                "url='" + url + '\'' +
                ", internalLinks=" + internalLinks +
                ", externalLinks=" + externalLinks +
                ", resources=" + resources +
                '}';
    }

    public static class SitemapFragmentBuilder {
        private String url;
        private List<InternalLink> internalLinks;
        private List<ExternalLink> externalLinks;
        private List<Resource> resources;

        public static SitemapFragmentBuilder aSitemapFragmentBuilder() {
            return new SitemapFragmentBuilder();
        }

        public SitemapFragmentBuilder withUrl(String url) {
            this.url = url;
            return this;
        }

        public SitemapFragmentBuilder withInternalLinks(List<InternalLink> internalLinks) {
            this.internalLinks = internalLinks;
            return this;
        }

        public SitemapFragmentBuilder withExternalLinks(List<ExternalLink> externalLinks) {
            this.externalLinks = externalLinks;
            return this;
        }

        public SitemapFragmentBuilder withResources(List<Resource> resources) {
            this.resources = resources;
            return this;
        }

        public SitemapFragment build() {
            return new SitemapFragment(url, internalLinks, externalLinks, resources);
        }
    }

}
