package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.HomeDivSection;
import com.aadhik.ecommerce.model.HomeSlider;
import com.aadhik.ecommerce.model.MarqueeConfig;
import com.aadhik.ecommerce.model.ProductCollection;
import com.aadhik.ecommerce.model.VideoCarouselItem;
import com.aadhik.ecommerce.service.CatalogService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author THIRUPATHI G
 */
@Named
@RequestScoped
public class HomePageBean extends BaseBean {

    @Inject
    private CatalogService catalogService;

    public List<HomeSlider> getSliders() {
        return catalogService.getHomeActiveSliders();
    }

    public List<HomeDivSection> getHomeDivSections() {
        return catalogService.getHomeActiveDivSections();
    }

    public List<CatalogService.HomepageSectionView> getSectionViews() {
        return catalogService.getHomepageSectionsWithProducts();
    }

    public List<CatalogService.HomeRenderSection> getOrderedHomeSections() {
        return catalogService.getOrderedHomeRenderSections();
    }

    public boolean hasOrderedHomeSections() {
        return !getOrderedHomeSections().isEmpty();
    }

    public List<VideoCarouselItem> getVideoCarouselItems() {
        return catalogService.getHomeActiveVideoCarouselItems();
    }

    public List<ProductCollection> getActiveCollections() {
        return catalogService.getActiveCollections();
    }

    public MarqueeConfig getActiveMarquee() {
        return catalogService.getActiveMarqueeConfig();
    }

    public List<String> getCarouselVideos(VideoCarouselItem item) {
        if (item == null) {
            return List.of();
        }
        List<String> videos = parseVideoLines(item.getVideoUrls());
        if (videos.isEmpty()) {
            videos = parseVideoLines(item.getVideoUrl());
        }
        return videos;
    }

    public int carouselVideoCount(VideoCarouselItem item) {
        return getCarouselVideos(item).size();
    }

    private List<String> parseVideoLines(String values) {
        if (values == null || values.isBlank()) {
            return List.of();
        }
        return values.lines()
                .map(String::trim)
                .filter(text -> !text.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getMarqueeItems(MarqueeConfig marquee) {
        if (marquee == null || marquee.getItemsData() == null || marquee.getItemsData().isBlank()) {
            return List.of();
        }
        return marquee.getItemsData().lines().map(String::trim).filter(text -> !text.isBlank()).collect(Collectors.toList());
    }

    public List<String> getActiveMarqueeItems() {
        return getMarqueeItems(getActiveMarquee());
    }

    public List<String> getMarqueeLoopedItems(MarqueeConfig marquee) {
        List<String> items = getMarqueeItems(marquee);
        if (items.isEmpty()) {
            return List.of();
        }
        return buildContinuousItems(items, 18);
    }

    public List<String> getActiveMarqueeLoopedItems() {
        return getMarqueeLoopedItems(getActiveMarquee());
    }

    private List<String> buildContinuousItems(List<String> source, int minimumVisibleItems) {
        if (source == null || source.isEmpty()) {
            return List.of();
        }
        int repeatCount = Math.max(2, (int) Math.ceil((double) minimumVisibleItems / source.size()));
        ArrayList<String> repeated = new ArrayList<>(source.size() * repeatCount);
        for (int i = 0; i < repeatCount; i++) {
            repeated.addAll(source);
        }
        return repeated;
    }

    public String marqueeAnimationClass(MarqueeConfig marquee) {
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

    public String marqueeAnimationClass() {
        return marqueeAnimationClass(getActiveMarquee());
    }

    public String marqueeBackground(MarqueeConfig marquee) {
        if (marquee == null) {
            return "#0f1f49";
        }
        if ("gradient".equals(marquee.getBackgroundMode()) && marquee.getGradientColors() != null && !marquee.getGradientColors().isBlank()) {
            boolean vertical = "ttb".equals(marquee.getDirection()) || "btt".equals(marquee.getDirection());
            String angle = vertical ? "180deg" : "90deg";
            return "linear-gradient(" + angle + "," + marquee.getGradientColors() + ")";
        }
        return (marquee.getSolidColor() == null || marquee.getSolidColor().isBlank()) ? "#0f1f49" : marquee.getSolidColor();
    }

    public String marqueeBackground() {
        return marqueeBackground(getActiveMarquee());
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
