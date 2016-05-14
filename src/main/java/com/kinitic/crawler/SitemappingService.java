package com.kinitic.crawler;

import com.kinitic.crawler.model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.partitioningBy;

@Component
public class SitemappingService {

    private HttpClient httpClient;

    @Autowired
    public SitemappingService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }


    public Sitemap buildSitemap(String domain) throws Exception {
        return new Sitemap(domain, processPage(domain, domain, new HashSet<>()));
    }

    private SitemapFragment processPage(String domain, String internalLink, Set<String> internalLinksAlreadyCrawled) throws Exception {
        SitemapFragment.SitemapFragmentBuilder rootFragmentBuilder =  SitemapFragment.SitemapFragmentBuilder.aSitemapFragmentBuilder();

        System.out.println(String.format("Opening page: = %s", internalLink));
        String pageContent = httpClient.getPageContent(internalLink);
        internalLinksAlreadyCrawled.add(internalLink);

        final Document document = Jsoup.parse(pageContent);

        List<String> allLinksOnCurrentPage = getAllLinksOnCurrentPage(document);
        Map<Boolean, List<String>> links = allLinksOnCurrentPage.stream().collect(partitioningBy(link -> link.startsWith(domain)));
        Set<String> dedupedInternalLinks = links.get(true).stream().collect(Collectors.toSet());

        List<String> externalLinksOnCurrentPage = links.get(false);

        List<InternalLink> internalLinks = new ArrayList<>();
        for (String newInternalLink : dedupedInternalLinks) {
            if (!newInternalLink.equals(internalLink) && !internalLinksAlreadyCrawled.contains(newInternalLink)) {
                try {
                    final SitemapFragment fragment = processPage(domain, newInternalLink, internalLinksAlreadyCrawled);
                    internalLinks.add(InternalLink.InternalLinkBuilder.anInternalLinkBuilder().withSitemapFragment(fragment).build());
                } catch(Exception e) {
                    // for this exercise, lets ignore the exception
                }
            }
        }
        final List<ExternalLink> sanitisedExternalLinks = externalLinksOnCurrentPage.stream().filter(removingAnchorsPredicate()).map(link->ExternalLink.ExternalLinkBuilder.anExternalLinkBuilder().withName(link).build()).collect(Collectors.toList());

        List<Resource> imagesOnCurrentPage = getAllImagesOnCurrentPage(document).stream().map(image->Resource.ResourceBuilder.aResourceBuilder().withName(image).build()).collect(Collectors.toList());

        return rootFragmentBuilder.withExternalLinks(sanitisedExternalLinks).withInternalLinks(internalLinks).withResources(imagesOnCurrentPage).withUrl(internalLink).build();
    }

    private List<String> getAllImagesOnCurrentPage(Document document) {
        List<String> imagesOnCurrentPage = new ArrayList<>();
        document.select("img[src]").stream().forEach(img-> {
            imagesOnCurrentPage.add(img.attr("src"));

        });
        return imagesOnCurrentPage;
    }

    private Predicate<String> removingAnchorsPredicate() {
        return link -> !link.startsWith("#") && !link.startsWith("/#");
    }

    private List<String> getAllLinksOnCurrentPage(Document document) {
        List<String> allLinksOnCurrentPage = new ArrayList<>();
        document.select("a[href]").stream().forEach(link-> {
            allLinksOnCurrentPage.add(link.attr("href"));

        });
        return allLinksOnCurrentPage;
    }

}
