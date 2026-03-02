package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.HomeSlider;
import com.aadhik.ecommerce.model.MarqueeConfig;
import com.aadhik.ecommerce.service.CatalogService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.List;

@Named
@RequestScoped
public class HomePageBean {

    @Inject
    private CatalogService catalogService;

    public List<HomeSlider> getSliders() {
        return catalogService.getHomeActiveSliders();
    }

    public List<CatalogService.HomepageSectionView> getSectionViews() {
        return catalogService.getHomepageSectionsWithProducts();
    }

    public MarqueeConfig getActiveMarquee() {
        return catalogService.getActiveMarqueeConfig();
    }

    public List<String> getActiveMarqueeItems() {
        MarqueeConfig marquee = getActiveMarquee();
        if (marquee == null || marquee.getItemsData() == null || marquee.getItemsData().isBlank()) {
            return List.of();
        }
        return marquee.getItemsData().lines().map(String::trim).filter(text -> !text.isBlank()).collect(java.util.stream.Collectors.toList());
    }

    public List<String> getActiveMarqueeLoopedItems() {
        List<String> items = getActiveMarqueeItems();
        if (items.isEmpty()) {
            return List.of();
        }
        java.util.ArrayList<String> looped = new java.util.ArrayList<>(items);
        looped.addAll(items);
        return looped;
    }

    public String marqueeAnimationClass() {
        MarqueeConfig marquee = getActiveMarquee();
        if (marquee == null) {
            return "anim-horizontal-rtl";
        }
        if ("ltr".equals(marquee.getDirection())) {
            return "anim-horizontal-ltr";
        }
        if ("ttb".equals(marquee.getDirection())) {
            return "vertical anim-vertical-ttb";
        }
        if ("btt".equals(marquee.getDirection())) {
            return "vertical anim-vertical-btt";
        }
        return "anim-horizontal-rtl";
    }

    public String marqueeBackground() {
        MarqueeConfig marquee = getActiveMarquee();
        if (marquee == null) {
            return "#0f1f49";
        }
        if ("gradient".equals(marquee.getBackgroundMode()) && marquee.getGradientColors() != null && !marquee.getGradientColors().isBlank()) {
            boolean vertical = "ttb".equals(marquee.getDirection()) || "btt".equals(marquee.getDirection());
            String angle = vertical ? "180deg" : "90deg";
            return "linear-gradient(" + angle + "," + marquee.getGradientColors() + ")";
        }
        return marquee.getSolidColor();
    }

    public String resolveMediaUrl(String source) {
        if (source == null || source.isBlank()) {
            return "https://via.placeholder.com/700x900?text=No+Image";
        }
        if (source.startsWith("dbfile:")) {
            return "/resources/files/" + source.substring("dbfile:".length());
        }
        return source;
    }
}
