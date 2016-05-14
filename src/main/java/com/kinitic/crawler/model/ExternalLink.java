package com.kinitic.crawler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public class ExternalLink {
    @JsonProperty("name")
    String name;

    private ExternalLink(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExternalLink that = (ExternalLink) o;

        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ExternalLink{" +
                "name='" + name + '\'' +
                '}';
    }

    public static class ExternalLinkBuilder {
        private String name;

        public static ExternalLinkBuilder anExternalLinkBuilder() {
            return new ExternalLinkBuilder();
        }

        public ExternalLinkBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ExternalLink build() {
            return new ExternalLink(name);
        }
    }
}
