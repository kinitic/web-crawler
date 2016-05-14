package com.kinitic.crawler;

import com.kinitic.crawler.model.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SitemappingServiceTest {

    @Mock
    HttpClient mockHttpClient = mock(HttpClient.class);

    SitemappingService sitemappingService;

    @Before
    public void setup() {
        sitemappingService = new SitemappingService(mockHttpClient);
    }

    @Test
    public void shouldCreateSimpleSitemapWhenThereIsOnlyOneExternalGoogleLinkOnDomainPage() throws Exception {
        String simpleHtmlWithJustOneAnchorWithExternalUrl =
                "<body>"+
                "   <a href=\"http://www.google.com/\" title=\"Digital Transformation &#8211; Wipro Digital\" rel=\"home\"></a>" +
                "</body>";

        final String FAKE_DOMAIN = "http://something.com";
        when(mockHttpClient.getPageContent(FAKE_DOMAIN)).thenReturn(simpleHtmlWithJustOneAnchorWithExternalUrl);
        final Sitemap sitemap = sitemappingService.buildSitemap(FAKE_DOMAIN);

        assertThat(sitemap.getDomain(), is(FAKE_DOMAIN));
        assertThat(sitemap.getSitemapFragment().getUrl(), is(FAKE_DOMAIN));
        assertThat(sitemap.getSitemapFragment().getInternalLinks().size(), is(0));
        assertThat(sitemap.getSitemapFragment().getResources().size(), is(0));
        assertThat(sitemap.getSitemapFragment().getExternalLinks(), is(singletonList(ExternalLink.ExternalLinkBuilder.anExternalLinkBuilder().withName("http://www.google.com/").build())));
    }

    @Test
    public void shouldCreateAnotherSimpleSitemapWhenThereIsOneExternalGoogleLinkAndImageOnDomainPage() throws Exception {
        String simpleHtmlWithOneAnchorWithExternalUrlAndAnImage =
                "<body>"+
                "       <a href=\"http://www.google.com/\" title=\"Digital Transformation &#8211; Wipro Digital\" rel=\"home\">" +
                "           <img src=\"http://whatever.com/test.png\" alt=\"Digital Transformation &#8211; Wipro Digital\">" +
                "       </a>" +
                "</body>";

        final String FAKE_DOMAIN = "http://something.com";
        when(mockHttpClient.getPageContent(FAKE_DOMAIN)).thenReturn(simpleHtmlWithOneAnchorWithExternalUrlAndAnImage);
        final Sitemap sitemap = sitemappingService.buildSitemap(FAKE_DOMAIN);

        assertThat(sitemap.getDomain(), is(FAKE_DOMAIN));
        assertThat(sitemap.getSitemapFragment().getUrl(), is(FAKE_DOMAIN));
        assertThat(sitemap.getSitemapFragment().getInternalLinks().size(), is(0));
        assertThat(sitemap.getSitemapFragment().getExternalLinks(), is(singletonList(ExternalLink.ExternalLinkBuilder.anExternalLinkBuilder().withName("http://www.google.com/").build())));
        assertThat(sitemap.getSitemapFragment().getResources(), is(singletonList(Resource.ResourceBuilder.aResourceBuilder().withName("http://whatever.com/test.png").build())));
    }

    @Test
    public void shouldFilterOutAnyAnchorTagsFromTheExternalLinksInTheSitemap() throws Exception{
        String simpleHtmlWithOneAnchorWithExternalUrlAndAnImage =
                "<body>" +
                        "   <a href=\"http://www.google.com/\" title=\"Digital Transformation &#8211; Wipro Digital\" rel=\"home\">" +
                        "       <img src=\"http://whatever/crawl.png\" alt=\"Digital Transformation &#8211; Wipro Digital\">" +
                        "   </a>" +
                        "   <a href=\"/#section-our-story\"" +
                        "   <a href=\"#section-my-story\"" +
                        "</body>";

        final String FAKE_DOMAIN = "http://something.com";
        when(mockHttpClient.getPageContent(FAKE_DOMAIN)).thenReturn(simpleHtmlWithOneAnchorWithExternalUrlAndAnImage);
        final Sitemap sitemap = sitemappingService.buildSitemap(FAKE_DOMAIN);

        assertThat(sitemap.getDomain(), is(FAKE_DOMAIN));
        assertThat(sitemap.getSitemapFragment().getUrl(), is(FAKE_DOMAIN));
        assertThat(sitemap.getSitemapFragment().getInternalLinks().size(), is(0));
        assertThat(sitemap.getSitemapFragment().getExternalLinks(), is(singletonList(ExternalLink.ExternalLinkBuilder.anExternalLinkBuilder().withName("http://www.google.com/").build())));
        assertThat(sitemap.getSitemapFragment().getResources(), is(singletonList(Resource.ResourceBuilder.aResourceBuilder().withName("http://whatever/crawl.png").build())));
    }

    @Test
    public void shouldCreateSitemapWithInternalLinksAndExternalLinksAndImagesIfAllArePresent() throws Exception {
        final String FAKE_DOMAIN = "http://something.com/";
        String simpleHtmlWithOneAnchorWithExternalUrlAndAnImage =
                "<body>"+
                "       <a href=\"http://www.google.com/\" title=\"Digital Transformation &#8211; Wipro Digital\" rel=\"home\">" +
                "           <img src=\"http://whichever.com/WD_logo_150X27.png\" alt=\"Digital Transformation &#8211; Wipro Digital\">" +
                "       </a>" +
                "       <a href=\"/#section-our-story\"/>" +
                "       <a href=\"#section-my-story\"/>" +
                "       <a href=" +FAKE_DOMAIN + "somewhere/>" +
                "</body>";

        when(mockHttpClient.getPageContent(FAKE_DOMAIN)).thenReturn(simpleHtmlWithOneAnchorWithExternalUrlAndAnImage);
        when(mockHttpClient.getPageContent(FAKE_DOMAIN + "somewhere/")).thenReturn("<body></body>");
        final Sitemap sitemap = sitemappingService.buildSitemap(FAKE_DOMAIN);


        assertThat(sitemap.getDomain(), is(FAKE_DOMAIN));
        assertThat(sitemap.getSitemapFragment().getUrl(), is(FAKE_DOMAIN));

        SitemapFragment expectedSitemapFragment = SitemapFragment.SitemapFragmentBuilder.aSitemapFragmentBuilder().
                withUrl("http://something.com/somewhere/").
                withInternalLinks(Collections.emptyList()).
                withResources(Collections.emptyList()).
                withExternalLinks(Collections.emptyList()).
                build();

        assertThat(sitemap.getSitemapFragment().getInternalLinks(), is(singletonList(InternalLink.InternalLinkBuilder.anInternalLinkBuilder().withSitemapFragment(expectedSitemapFragment).build())));
        assertThat(sitemap.getSitemapFragment().getExternalLinks(), is(singletonList(ExternalLink.ExternalLinkBuilder.anExternalLinkBuilder().withName("http://www.google.com/").build())));
        assertThat(sitemap.getSitemapFragment().getResources(), is(singletonList(Resource.ResourceBuilder.aResourceBuilder().withName("http://whichever.com/WD_logo_150X27.png").build())));
    }

    @Test
    public void shouldCreateNestedSitemapWhenInternalLinksArePresent() throws Exception {
        final String FAKE_DOMAIN = "http://something.com/";
        String simpleHtmlWithOneAnchorWithExternalUrlAndAnImage =
                "<body>"+
                "       <a href=\"http://www.google.com/\" title=\"Digital Transformation &#8211; Wipro Digital\" rel=\"home\">" +
                "           <img src=\"http://whatever.com/WD_logo_150X27.png\" alt=\"Digital Transformation &#8211; Wipro Digital\">" +
                "       </a>" +
                "       <a href=\"/#section-our-story\"/>" +
                "       <a href=\"#section-my-story\"/>" +
                "       <a href=" +FAKE_DOMAIN + "somewhere/>" +
                "</body>";

        String secondsubLinkWithOneAnchorWithExternalUrl =
                "<body>"+
                "       <a href=\"http://www.bbc.co.uk/\" title=\"Digital Transformation. No way.\" rel=\"home\">" +
                "           <img src=\"http://a-different-one-from-above/WD_logo_150X27.png\" alt=\"Digital Transformation &#8211; Wipro Digital\">" +
                "       </a>" +
                "</body>";

        when(mockHttpClient.getPageContent(FAKE_DOMAIN)).thenReturn(simpleHtmlWithOneAnchorWithExternalUrlAndAnImage);
        when(mockHttpClient.getPageContent(FAKE_DOMAIN + "somewhere/")).thenReturn(secondsubLinkWithOneAnchorWithExternalUrl);

        final Sitemap sitemap = sitemappingService.buildSitemap(FAKE_DOMAIN);

        assertThat(sitemap.getDomain(), is(FAKE_DOMAIN));
        assertThat(sitemap.getSitemapFragment().getUrl(), is(FAKE_DOMAIN));

        SitemapFragment expectedSitemapFragment = SitemapFragment.SitemapFragmentBuilder.aSitemapFragmentBuilder().
                withUrl(FAKE_DOMAIN + "somewhere/").
                withResources(singletonList(Resource.ResourceBuilder.aResourceBuilder().withName("http://a-different-one-from-above/WD_logo_150X27.png").build())).
                withExternalLinks(singletonList(ExternalLink.ExternalLinkBuilder.anExternalLinkBuilder().withName("http://www.bbc.co.uk/").build())).
                withInternalLinks(EMPTY_LIST).
                build();

        assertThat(sitemap.getSitemapFragment().getInternalLinks(), is(singletonList(InternalLink.InternalLinkBuilder.anInternalLinkBuilder().withSitemapFragment(expectedSitemapFragment).build())));
        assertThat(sitemap.getSitemapFragment().getExternalLinks(), is(singletonList(ExternalLink.ExternalLinkBuilder.anExternalLinkBuilder().withName("http://www.google.com/").build())));
        assertThat(sitemap.getSitemapFragment().getResources(), is(singletonList(Resource.ResourceBuilder.aResourceBuilder().withName("http://whatever.com/WD_logo_150X27.png").build())));
    }
}