package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.MarqueeConfig;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * @author THIRUPATHI G
 */
@Named
@ViewScoped
public class MarqueeConfigBean extends AdminBean {

    private MarqueeConfig marqueeForm;
    private String marqueeItemInput;
    private int gradientColorCount;
    private String gradientColor1;
    private String gradientColor2;
    private String gradientColor3;
    private String gradientColor4;
    private String gradientColor5;

    @Override
    public void resetForm() {
        marqueeForm = new MarqueeConfig();
        marqueeForm.setItemsData("Free shipping above $50\nFlash sale live now");
        marqueeForm.setDirection("rtl");
        marqueeForm.setSpeedSeconds(12);
        marqueeForm.setBackgroundMode("solid");
        marqueeForm.setSolidColor("#0f1f49");
        marqueeForm.setGradientColors("#0f1f49,#325ac7");
        gradientColor1 = "#0f1f49";
        gradientColor2 = "#325ac7";
        gradientColor3 = "#5f85ee";
        gradientColor4 = "#7f9bff";
        gradientColor5 = "#b7c8ff";
        marqueeForm.setFontSizePx(22);
        marqueeForm.setFontWeight("700");
        marqueeForm.setTextColor("#ffffff");
        marqueeForm.setPauseOnHover(true);
        marqueeForm.setActive(true);
        marqueeItemInput = "";
        gradientColorCount = 2;
    }

    @Override
    public void saveForm() {
        if (!validateForm()) {
            return;
        }
        marqueeForm.setGradientColors(String.join(",", getGradientColors()));
        marqueeForm.setItemsData(String.join("\n", getMarqueeItems()));
        catalogService.saveMarqueeConfig(marqueeForm);
        resetForm();
        addInfo("Marquee saved successfully");
    }

    @Override
    public boolean validateForm() {
        if (getMarqueeItems().isEmpty()) {
            addError("Add at least one marquee item.");
            return false;
        }
        if (marqueeForm.getSpeedSeconds() < 5 || marqueeForm.getSpeedSeconds() > 30) {
            addError("Speed must be between 5 and 30 seconds.");
            return false;
        }
        if (marqueeForm.getFontSizePx() < 8 || marqueeForm.getFontSizePx() > 60) {
            addError("Font size must be between 8 and 60 px.");
            return false;
        }
        return true;
    }

    @Override
    public void editForm(Object form) {
        if (form instanceof MarqueeConfig config) {
            MarqueeConfig draft = new MarqueeConfig();
            draft.setId(config.getId());
            draft.setItemsData(config.getItemsData());
            draft.setDirection(config.getDirection());
            draft.setSpeedSeconds(config.getSpeedSeconds());
            draft.setBackgroundMode(config.getBackgroundMode());
            draft.setSolidColor(config.getSolidColor());
            draft.setGradientColors(config.getGradientColors());
            draft.setFontSizePx(config.getFontSizePx());
            draft.setFontWeight(config.getFontWeight());
            draft.setTextColor(config.getTextColor());
            draft.setPauseOnHover(config.isPauseOnHover());
            draft.setActive(config.isActive());
            marqueeForm = draft;
            loadGradientColorsFromString(config.getGradientColors());
        }
    }

    @Override
    public boolean deleteForm(Object form) {
        if (form instanceof MarqueeConfig config) {
            catalogService.deleteMarqueeConfig(config.getId());
            addInfo("Marquee deleted successfully.");
            return true;
        }
        return false;
    }

    public void addMarqueeItem() {
        if (isBlank(marqueeItemInput)) {
            addWarn("Please enter marquee content.");
            return;
        }
        List<String> items = getMarqueeItems();
        items.add(marqueeItemInput.trim());
        marqueeForm.setItemsData(String.join("\n", items));
        marqueeItemInput = "";
    }

    public void removeMarqueeItem(int index) {
        List<String> items = getMarqueeItems();
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            marqueeForm.setItemsData(String.join("\n", items));
        } else {
            marqueeForm.setItemsData(String.join("\n", items));
        }
    }

    public void applyGradientColorCount() {
        if (gradientColorCount < 2) {
            gradientColorCount = 2;
        }
        if (gradientColorCount > 5) {
            gradientColorCount = 5;
        }
        marqueeForm.setGradientColors(String.join(",", getGradientColors()));
    }

    public List<String> getMarqueeItems() {
        List<String> items = new ArrayList<>();
        if (marqueeForm == null) {
            marqueeForm = new MarqueeConfig();
        }
        if (isBlank(marqueeForm.getItemsData())) {
            return items;
        }
        String[] lines = marqueeForm.getItemsData().split("\\n");
        for (String line : lines) {
            if (!isBlank(line)) {
                items.add(line.trim());
            }
        }
        return items;
    }

    public List<String> getLoopedMarqueeItems() {
        List<String> items = getMarqueeItems();
        if (items.isEmpty()) {
            items.add("Add marquee content");
        }
        return buildContinuousItems(items, 18);
    }

    private List<String> buildContinuousItems(List<String> source, int minimumVisibleItems) {
        int repeatCount = Math.max(2, (int) Math.ceil((double) minimumVisibleItems / source.size()));
        List<String> repeated = new ArrayList<>(source.size() * repeatCount);
        for (int i = 0; i < repeatCount; i++) {
            repeated.addAll(source);
        }
        return repeated;
    }

    public List<String> getGradientColors() {
        List<String> colors = new ArrayList<>();
        colors.add(defaultColor(gradientColor1, "#0f1f49"));
        colors.add(defaultColor(gradientColor2, "#325ac7"));
        if (gradientColorCount >= 3) {
            colors.add(defaultColor(gradientColor3, "#5f85ee"));
        }
        if (gradientColorCount >= 4) {
            colors.add(defaultColor(gradientColor4, "#7f9bff"));
        }
        if (gradientColorCount >= 5) {
            colors.add(defaultColor(gradientColor5, "#b7c8ff"));
        }
        return colors;
    }

    public void setGradientColorByIndex(int index, String value) {
        switch (index) {
            case 0 ->
                gradientColor1 = value;
            case 1 ->
                gradientColor2 = value;
            case 2 ->
                gradientColor3 = value;
            case 3 ->
                gradientColor4 = value;
            case 4 ->
                gradientColor5 = value;
        }
    }

    private String defaultColor(String value, String fallback) {
        return isBlank(value) ? fallback : value;
    }

    private void loadGradientColorsFromString(String text) {
        gradientColor1 = "#0f1f49";
        gradientColor2 = "#325ac7";
        gradientColor3 = "#5f85ee";
        gradientColor4 = "#7f9bff";
        gradientColor5 = "#b7c8ff";
        gradientColorCount = 2;
        if (isBlank(text)) {
            return;
        }
        String[] colors = text.split(",");
        gradientColorCount = Math.max(2, Math.min(5, colors.length));
        if (colors.length > 0 && !isBlank(colors[0])) {
            gradientColor1 = colors[0].trim();
        }
        if (colors.length > 1 && !isBlank(colors[1])) {
            gradientColor2 = colors[1].trim();
        }
        if (colors.length > 2 && !isBlank(colors[2])) {
            gradientColor3 = colors[2].trim();
        }
        if (colors.length > 3 && !isBlank(colors[3])) {
            gradientColor4 = colors[3].trim();
        }
        if (colors.length > 4 && !isBlank(colors[4])) {
            gradientColor5 = colors[4].trim();
        }
    }

    public String buildMarqueePreviewBackground() {
        if ("gradient".equals(marqueeForm.getBackgroundMode())) {
            String angle = isVerticalDirection() ? "180deg" : "90deg";
            return "linear-gradient(" + angle + "," + String.join(",", getGradientColors()) + ")";
        }
        return isBlank(marqueeForm.getSolidColor()) ? "#0f1f49" : marqueeForm.getSolidColor();
    }

    public String getMarqueeAnimationClass() {
        if ("ltr".equals(marqueeForm.getDirection())) {
            return "anim-horizontal-ltr";
        }
        if ("ttb".equals(marqueeForm.getDirection())) {
            return "vertical anim-vertical-ttb";
        }
        if ("btt".equals(marqueeForm.getDirection())) {
            return "vertical anim-vertical-btt";
        }
        return "anim-horizontal-rtl";
    }

    public boolean isVerticalDirection() {
        return "ttb".equals(marqueeForm.getDirection()) || "btt".equals(marqueeForm.getDirection());
    }

    public int getMarqueeFontWeightValue() {
        if (isBlank(marqueeForm.getFontWeight())) {
            return 700;
        }
        try {
            int parsed = Integer.parseInt(marqueeForm.getFontWeight());
            return Math.max(400, Math.min(800, parsed));
        } catch (NumberFormatException exception) {
            return 700;
        }
    }

    public void setMarqueeFontWeightValue(int value) {
        int bounded = Math.max(400, Math.min(800, value));
        int normalized = (bounded / 100) * 100;
        marqueeForm.setFontWeight(String.valueOf(normalized));
    }

    public MarqueeConfig getMarqueeForm() {
        return marqueeForm;
    }

    public void setMarqueeForm(MarqueeConfig marqueeForm) {
        this.marqueeForm = marqueeForm;
    }

    public String getMarqueeItemInput() {
        return marqueeItemInput;
    }

    public void setMarqueeItemInput(String marqueeItemInput) {
        this.marqueeItemInput = marqueeItemInput;
    }

    public int getGradientColorCount() {
        return gradientColorCount;
    }

    public void setGradientColorCount(int gradientColorCount) {
        this.gradientColorCount = gradientColorCount;
    }

    public String getGradientColor1() {
        return gradientColor1;
    }

    public void setGradientColor1(String gradientColor1) {
        this.gradientColor1 = gradientColor1;
    }

    public String getGradientColor2() {
        return gradientColor2;
    }

    public void setGradientColor2(String gradientColor2) {
        this.gradientColor2 = gradientColor2;
    }

    public String getGradientColor3() {
        return gradientColor3;
    }

    public void setGradientColor3(String gradientColor3) {
        this.gradientColor3 = gradientColor3;
    }

    public String getGradientColor4() {
        return gradientColor4;
    }

    public void setGradientColor4(String gradientColor4) {
        this.gradientColor4 = gradientColor4;
    }

    public String getGradientColor5() {
        return gradientColor5;
    }

    public void setGradientColor5(String gradientColor5) {
        this.gradientColor5 = gradientColor5;
    }

}
