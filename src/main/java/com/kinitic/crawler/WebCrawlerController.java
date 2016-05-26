package com.kinitic.crawler;

import com.kinitic.crawler.model.Sitemap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class WebCrawlerController {
    SitemappingService sitemappingService;

    @Autowired
    public WebCrawlerController(SitemappingService sitemappingService) {
        this.sitemappingService = sitemappingService;
    }

    @RequestMapping(value="/crawler", method = GET)
    public ModelAndView entry() {
        return new ModelAndView("crawler");
    }

    @RequestMapping(value="/sitemap", method = POST)
    public @ResponseBody Sitemap sitemap(@RequestParam("domain") String domain) throws Exception {
        return sitemappingService.buildSitemap(domain);
    }

    @ExceptionHandler(InvalidDomainException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleInvalidDomainException(InvalidDomainException ex) {
        return ex.getMessage();
    }
}
