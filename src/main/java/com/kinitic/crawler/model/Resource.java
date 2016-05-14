package com.kinitic.crawler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public class Resource {
    @JsonProperty("name")
    String name;

    private Resource(String name) {
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

        Resource resource = (Resource) o;

        return name != null ? name.equals(resource.name) : resource.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "name='" + name + '\'' +
                '}';
    }

    public static class ResourceBuilder {
        private String name;

        public static ResourceBuilder aResourceBuilder() {
            return new ResourceBuilder();
        }

        public ResourceBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public Resource build() {
            return new Resource(name);
        }
    }
}
