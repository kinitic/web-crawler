package com.kinitic.crawler;

import com.kinitic.crawler.model.Sitemap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class SitemappingServiceTest {

    @Mock
    HttpClient mockHttpClient = mock(HttpClient.class);

    SitemappingService sitemappingService;

    @Before
    public void setup() {
        sitemappingService = new SitemappingService(mockHttpClient);
    }

    @Test(expected = InvalidDomainException.class)
    public void shouldNotAllowDomainUrls_ThatDontEndInDotCom_OrDotCoUk() throws Exception {
        sitemappingService.buildSitemap("http://www.kinitic.it");
    }

    @Test(expected = InvalidDomainException.class)
    public void shouldNotAllowDomainUrls_ThatAreSubPages() throws Exception {
        sitemappingService.buildSitemap("http://www.kinitic.com/this-is-bad");
    }

    @Test(expected = InvalidDomainException.class)
    public void shouldNotAllowTopLevelDomainUrls_ThatAreEmpty() throws Exception {
        sitemappingService.buildSitemap("");
    }

    @Test(expected = InvalidDomainException.class)
    public void shouldNotAllowTopLevelDomainUrls_ThatAreNull() throws Exception {
        sitemappingService.buildSitemap(null);
    }


    @Test
    public void shouldCreateSimpleSitemap_WhenThereIsOnlyOneExternalLinkOnDomainPage() throws Exception {
        String simpleHtmlWithJustOneExternalUrl =
                "<body>" +
                "   <a href=\"http://www.google.com/\" title=\"Digital Transformation &#8211; Wipro Digital\" rel=\"home\"></a>" +
                "</body>";

        final String TEST_DOMAIN = "http://something.co.uk/";
        when(mockHttpClient.getPageContent(TEST_DOMAIN)).thenReturn(simpleHtmlWithJustOneExternalUrl);
        final Sitemap sitemap = sitemappingService.buildSitemap(TEST_DOMAIN);

        assertThat(sitemap.getDomain(), is(TEST_DOMAIN));
        assertThat(sitemap.getSitemapFragment().getInternalLinks(), is(emptySet()));
        assertThat(sitemap.getSitemapFragment().getResources(), is(emptySet()));
        assertThat(sitemap.getSitemapFragment().getExternalLinks(), is(singleton("http://www.google.com/")));
    }

    @Test
    public void shouldCreateAnotherSimpleSitemapWhenThereIsOneExternalGoogleLinkAndImageOnDomainPage() throws Exception {
        final String TEST_DOMAIN = "http://something.com/";
        String simpleHtmlWithOneAnchorWithExternalUrlAndAnImage =
                "<body>" +
                "       <a href=\"http://www.google.com/\" title=\"Digital Transformation &#8211; Wipro Digital\" rel=\"home\">" +
                "           <img src=\"http://whatever.com/test.png\" alt=\"Digital Transformation &#8211; Wipro Digital\">" +
                "       </a>" +
                "</body>";

        when(mockHttpClient.getPageContent(TEST_DOMAIN)).thenReturn(simpleHtmlWithOneAnchorWithExternalUrlAndAnImage);
        final Sitemap sitemap = sitemappingService.buildSitemap(TEST_DOMAIN);

        assertThat(sitemap.getDomain(), is(TEST_DOMAIN));
        assertThat(sitemap.getSitemapFragment().getInternalLinks(), is(emptySet()));
        assertThat(sitemap.getSitemapFragment().getExternalLinks(), is(singleton("http://www.google.com/")));
        assertThat(sitemap.getSitemapFragment().getResources(), is(singleton("http://whatever.com/test.png")));
    }

    @Test
    public void shouldCreateSitemapWithInternalLinks() throws Exception {
        final String TEST_DOMAIN = "http://something.com/";
        String simpleHtmlWithOneInternalUrlAndAnImage =
                "<body>" +
                "       <a href=\"" + TEST_DOMAIN + "countmein\" title=\"Digital Transformation &#8211; Wipro Digital\" rel=\"home\">" +
                "           <img src=\"http://whatever.com/test.png\" alt=\"Digital Transformation &#8211; Wipro Digital\">" +
                "       </a>" +
                "</body>";

        when(mockHttpClient.getPageContent(TEST_DOMAIN)).thenReturn(simpleHtmlWithOneInternalUrlAndAnImage);
        when(mockHttpClient.getPageContent(TEST_DOMAIN + "countmein")).thenReturn("<body/>");  // just return dummy nonsense html markup for the second page...
        final Sitemap sitemap = sitemappingService.buildSitemap(TEST_DOMAIN);

        assertThat(sitemap.getDomain(), is(TEST_DOMAIN));
        assertThat(sitemap.getSitemapFragment().getInternalLinks().size(), is(1));
        assertThat(sitemap.getSitemapFragment().getInternalLinks(), is(singleton(TEST_DOMAIN + "countmein")));
        assertThat(sitemap.getSitemapFragment().getExternalLinks(), is(emptySet()));
        assertThat(sitemap.getSitemapFragment().getResources(), is(singleton("http://whatever.com/test.png")));
    }

    @Test
    public void shouldCreateSitemapWithUniqueInternalLinks_AndUniqueExternalLinks_AndUniqueImages() throws Exception {
        final String TEST_DOMAIN = "http://something.com/";
        String simpleHtmlWithThreeIdenticalInternalUrlsAndAnExternalUrlAndImage =
                "<body>" +
                "       <a href=\"http://www.google.com/\" title=\"This is an external link!\" rel=\"home\">" +
                "       <a href=\"http://www.google.com/\" title=\"This is an external link!\" rel=\"home\">" +
                "       <a href=\"http://www.google.com/\" title=\"This is an external link!\" rel=\"home\">" +
                "       <a href=\"" + TEST_DOMAIN + "countmein\" title=\"Digital Transformation &#8211; Wipro Digital\" rel=\"home\">" +
                "           <img src=\"http://whatever.com/test.png\" alt=\"Digital Transformation &#8211; Wipro Digital\">" +
                "       </a>" +
                "       <a href=\"" + TEST_DOMAIN + "countmein\" title=\"Digital Transformation &#8211; Wipro Digital\" rel=\"home\">" +
                "           <img src=\"http://whatever.com/test.png\" alt=\"Digital Transformation &#8211; Wipro Digital\">" +
                "       </a>" +
                "       <a href=\"" + TEST_DOMAIN + "countmein\" title=\"Digital Transformation &#8211; Wipro Digital\" rel=\"home\">" +
                "           <img src=\"http://whatever.com/test.png\" alt=\"Digital Transformation &#8211; Wipro Digital\">" +
                "       </a>" +
                "</body>";

        when(mockHttpClient.getPageContent(TEST_DOMAIN)).thenReturn(simpleHtmlWithThreeIdenticalInternalUrlsAndAnExternalUrlAndImage);
        when(mockHttpClient.getPageContent(TEST_DOMAIN + "countmein")).thenReturn("<body/>");  // just return dummy nonsense html markup for the second page...

        final Sitemap sitemap = sitemappingService.buildSitemap(TEST_DOMAIN);

        assertThat(sitemap.getDomain(), is(TEST_DOMAIN));
        assertThat(sitemap.getSitemapFragment().getInternalLinks().size(), is(1));
        assertThat(sitemap.getSitemapFragment().getExternalLinks().size(), is(1));
        assertThat(sitemap.getSitemapFragment().getResources().size(), is(1));
        assertThat(sitemap.getSitemapFragment().getInternalLinks(), is(singleton(TEST_DOMAIN + "countmein")));
        assertThat(sitemap.getSitemapFragment().getExternalLinks(), is(singleton("http://www.google.com/")));
        assertThat(sitemap.getSitemapFragment().getResources(), is(singleton("http://whatever.com/test.png")));
    }

    @Test
    public void shouldFilterOutAnyAnchorTagsFromTheSitemap() throws Exception {
        final String TEST_DOMAIN = "http://something.com/";
        String simpleHtmlWithOneInternalUrlAndAnAnchorTag =
                "<body>" +
                "       <a href=\"#section-our-story\" title=\"I should not be sitemapped\" rel=\"home\">" +
                "       <a href=\"#top\" title=\"I should not be sitemapped either\" rel=\"top\">" +
                "       </a>" +
                "</body>";

        when(mockHttpClient.getPageContent(TEST_DOMAIN)).thenReturn(simpleHtmlWithOneInternalUrlAndAnAnchorTag);
        final Sitemap sitemap = sitemappingService.buildSitemap(TEST_DOMAIN);

        assertThat(sitemap.getSitemapFragment().getInternalLinks(), is(emptySet()));
        assertThat(sitemap.getSitemapFragment().getExternalLinks(), is(emptySet()));
        assertThat(sitemap.getSitemapFragment().getResources(), is(emptySet()));
    }

    @Test
    public void shouldCreateSitemapLoopingThroughTheHtmlMarkupWithinTheInternalLinks() throws Exception {
        final String TEST_DOMAIN = "http://something.com/";
        String simpleHtmlWithOneAnchorWithExternalUrlAndAnImage =
                "<body>"+
                "       <a href=\"http://www.google.com/\" title=\"Digital Transformation &#8211; Wipro Digital\" rel=\"home\">" +
                "           <img src=\"http://whatever.com/WD_logo_150X27.png\" alt=\"Digital Transformation &#8211; Wipro Digital\">" +
                "       </a>" +
                "       <a href=\"/#section-our-story\"/>" +
                "       <a href=\"#section-my-story\"/>" +
                "       <a href=" +TEST_DOMAIN + "somewhere/>" +
                "</body>";

        String secondsubLinkWithOneAnchorWithExternalUrl =
                "<body>"+
                "       <a href=\"http://www.bbc.co.uk/\" title=\"Digital Transformation. No way.\" rel=\"home\">" +
                "           <img src=\"http://a-different-one-from-above/WD_logo_150X27.png\" alt=\"Digital Transformation &#8211; Wipro Digital\">" +
                "       </a>" +
                "</body>";

        when(mockHttpClient.getPageContent(TEST_DOMAIN)).thenReturn(simpleHtmlWithOneAnchorWithExternalUrlAndAnImage);
        when(mockHttpClient.getPageContent(TEST_DOMAIN + "somewhere/")).thenReturn(secondsubLinkWithOneAnchorWithExternalUrl);

        final Sitemap sitemap = sitemappingService.buildSitemap(TEST_DOMAIN);

        assertThat(sitemap.getSitemapFragment().getInternalLinks().size(), is(1));
        assertThat(sitemap.getSitemapFragment().getExternalLinks().size(), is(2));
        assertThat(sitemap.getSitemapFragment().getResources().size(), is(2));
    }

    @Test
    public void shouldMakeSureThatAnInternalLinkIsOnlyCalledOnce_RegardlessOfNumberOfTimesItAppearsOnDifferentPages() throws Exception {
        final String TEST_DOMAIN = "http://something.com/";
        String simpleHtmlWithAnInternalLink =
                "<body>"+
                "       <a href=" +TEST_DOMAIN + "somewhere/>" +               // this one is repeated below!
                "</body>";

        String simpleHtmlWithAnotherInternalLink =
                "<body>"+
                "       <a href=" +TEST_DOMAIN + "somewhere-else/>" +
                "</body>";

        String simpleHtmlWithYetAnotherInternalLink =
                "<body>"+
                "       <a href=" +TEST_DOMAIN + "somewhere-else-again/>" +
                "</body>";

        String simpleHtmlWithWithOriginalInternalLink =
                "<body>"+
                "       <a href=" +TEST_DOMAIN + "somewhere-new/>" +
                "       <a href=" +TEST_DOMAIN + "somewhere/>" +          // this is repeated from above!
                "</body>";


        when(mockHttpClient.getPageContent(TEST_DOMAIN)).thenReturn(simpleHtmlWithAnInternalLink);
        when(mockHttpClient.getPageContent(TEST_DOMAIN + "somewhere/")).thenReturn(simpleHtmlWithAnotherInternalLink);
        when(mockHttpClient.getPageContent(TEST_DOMAIN + "somewhere-else/")).thenReturn(simpleHtmlWithYetAnotherInternalLink);
        when(mockHttpClient.getPageContent(TEST_DOMAIN + "somewhere-else-again/")).thenReturn(simpleHtmlWithWithOriginalInternalLink);
        when(mockHttpClient.getPageContent(TEST_DOMAIN + "somewhere-new/")).thenReturn("<body/>");


        final Sitemap sitemap = sitemappingService.buildSitemap(TEST_DOMAIN);

        verify(mockHttpClient, times(1)).getPageContent(TEST_DOMAIN);
        verify(mockHttpClient, times(1)).getPageContent(TEST_DOMAIN + "somewhere/");
        verify(mockHttpClient, times(1)).getPageContent(TEST_DOMAIN + "somewhere-else/");
        verify(mockHttpClient, times(1)).getPageContent(TEST_DOMAIN + "somewhere-else-again/");
        verify(mockHttpClient, times(1)).getPageContent(TEST_DOMAIN + "somewhere-new/");

        assertThat(sitemap.getSitemapFragment().getInternalLinks().size(), is(4));
    }

    @Test
    public void shouldCarryOnProcessingInternalLinksIfOneOfTheLinksIsUnavailable() throws Exception {
        final String TEST_DOMAIN = "http://something.com/";
        String simpleHtmlWithAnInternalLink =
                "<body>"+
                "       <a href=" +TEST_DOMAIN + "somewhere/>" +
                "</body>";

        String simpleHtmlWithOneBadInternalLink_AndOneGoodInternalLink =
                "<body>"+
                "       <a href=" +TEST_DOMAIN + "i-am-bad/>" +
                "       <a href=" +TEST_DOMAIN + "i-am-good/>" +
                "</body>";

        String simpleHtmlWithAnImage =
                "<body>"+
                "       <img src=\"http://whatever.com/WD_logo_150X27.png\" alt=\"Digital Transformation &#8211; Wipro Digital\">" +
                "</body>";

        when(mockHttpClient.getPageContent(TEST_DOMAIN)).thenReturn(simpleHtmlWithAnInternalLink);
        when(mockHttpClient.getPageContent(TEST_DOMAIN + "somewhere/")).thenReturn(simpleHtmlWithOneBadInternalLink_AndOneGoodInternalLink);
        when(mockHttpClient.getPageContent(TEST_DOMAIN + "i-am-bad/")).thenThrow(new ServiceUnavailableException("Oh dear - this is bad"));
        when(mockHttpClient.getPageContent(TEST_DOMAIN + "i-am-good/")).thenReturn(simpleHtmlWithAnImage);

        final Sitemap sitemap = sitemappingService.buildSitemap(TEST_DOMAIN);

        assertThat(sitemap.getSitemapFragment().getInternalLinks().size(), is(3));
        assertThat(sitemap.getSitemapFragment().getResources().size(), is(1));
    }
}