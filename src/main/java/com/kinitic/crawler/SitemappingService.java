package com.kinitic.crawler;

import com.kinitic.crawler.model.Sitemap;
import com.kinitic.crawler.model.SitemapFragment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.partitioningBy;

@Component
public class SitemappingService {

    private HttpClient httpClient;

    // TODO: consider using immutable sets...
    private Set<String> internalLinks = new HashSet<>();
    private Set<String> externalLinks = new HashSet<>();
    private Set<String> images = new HashSet<>();

    @Autowired
    public SitemappingService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Sitemap buildSitemap(String domain) throws Exception {
        if (!isValid(domain)) throw new InvalidDomainException("Domain entered needs to end with .com or .co.uk<br/>Trailing slash is optional.");
        return new Sitemap(domain, processPage(domain, domain, new HashSet<>()));
    }

    private boolean isValid(String domain) {
        // TODO: make this a regex-aware validator     (current implementation is too rudimentary)
        return domain != null && (domain.endsWith("com") || domain.endsWith("com/") || domain.endsWith("co.uk") || domain.endsWith("co.uk/"));
    }

    private SitemapFragment processPage(String domain, String internalLink, Set<String> internalLinksAlreadyCrawled) throws Exception {
        SitemapFragment.SitemapFragmentBuilder sitemapFragmentBuilder = SitemapFragment.SitemapFragmentBuilder.aSitemapFragmentBuilder();

        System.out.println(format("Opening page: = %s", internalLink));

        String pageContent = httpClient.getPageContent(internalLink);
        internalLinksAlreadyCrawled.add(internalLink);

        final Document document = Jsoup.parse(pageContent);

        List<String> allLinksOnCurrentPage = getAllLinksOnCurrentPage(document);

        Map<Boolean, List<String>> allLinks = allLinksOnCurrentPage.stream().collect(partitioningBy(link -> link.startsWith(domain)));
        Set<String> internalLinksOnCurrentPage = allLinks.get(true).stream().collect(Collectors.toSet());
        Set<String> externalLinksOnCurrentPage = allLinks.get(false).stream().filter(removingAnchorsPredicate()).collect(Collectors.toSet());
        Set<String> imagesOnCurrentPage = getAllImagesOnCurrentPage(document).stream().collect(Collectors.toSet());

        images = update(imagesOnCurrentPage.stream(), this.images.stream());
        internalLinks = update(internalLinksOnCurrentPage.stream(), this.internalLinks.stream());
        externalLinks = update(externalLinksOnCurrentPage.stream(), this.externalLinks.stream());

        for (String newInternalLink : internalLinksOnCurrentPage) {
            if (newLinkAndPreviousLinkAreDifferent(internalLink, newInternalLink) && linkNotAlreadyBeenProcessed(internalLinksAlreadyCrawled, newInternalLink)) {
                try {
                    SitemapFragment fragment = processPage(domain, newInternalLink, internalLinksAlreadyCrawled);
                    images = update(fragment.getResources().stream(), this.images.stream());
                    internalLinks = update(fragment.getInternalLinks().stream(), this.internalLinks.stream());
                    externalLinks = update(fragment.getExternalLinks().stream(), this.externalLinks.stream());
                } catch(Exception e) {
                    // do nothing with the exception for this exercise, just continue....
                    internalLinksAlreadyCrawled.add(newInternalLink);
                }
            }
        }
        return sitemapFragmentBuilder.withExternalLinks(externalLinks).withResources(this.images).withInternalLinks(internalLinks).build();
    }

    private boolean newLinkAndPreviousLinkAreDifferent(String internalLink, String newInternalLink) {
        return !newInternalLink.equals(internalLink);
    }

    private boolean linkNotAlreadyBeenProcessed(Set<String> internalLinksAlreadyCrawled, String newInternalLink) {
        return !internalLinksAlreadyCrawled.contains(newInternalLink);
    }

    private Set<String> update(Stream<String> itemsOnCurrentPage, Stream<String> fragmentStream) {
        return Stream.concat(itemsOnCurrentPage, fragmentStream).collect(Collectors.toSet());
    }

    private List<String> getAllImagesOnCurrentPage(Document document) {
        List<String> imagesOnCurrentPage = new ArrayList<>();
        document.select("img[src]").stream().forEach(img -> {
            imagesOnCurrentPage.add(img.attr("src"));
        });
        return imagesOnCurrentPage;
    }

    private Predicate<String> removingAnchorsPredicate() {
        return link -> !isAnchor(link);
    }

    private boolean isAnchor(String link) {
        return link.contains("#");
    }

    private List<String> getAllLinksOnCurrentPage(Document document) {
        List<String> allLinksOnCurrentPage = new ArrayList<>();
        document.select("a[href]").stream().forEach(link -> {
            allLinksOnCurrentPage.add(link.attr("href"));
        });
        return allLinksOnCurrentPage;
    }
}
