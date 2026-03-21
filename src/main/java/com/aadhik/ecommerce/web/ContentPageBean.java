package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.service.ContentPageService;
import com.aadhik.ecommerce.service.ContentPageService.ContentPage;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class ContentPageBean extends AdminBean {

    @Inject
    private ContentPageService contentPageService;

    private String selectedPageKey;
    private String editorHtml;

    @PostConstruct
    @Override
    public void init() {
        super.init();
    }

    @Override
    public void resetForm() {
        if (selectedPageKey == null) {
            selectedPageKey = "shipping";
        }
        loadSelectedPage();
    }

    @Override
    public void saveForm() {
        contentPageService.savePage(selectedPageKey, editorHtml);
        addInfo(getSelectedPageTitle() + " saved successfully.");
    }

    @Override
    public boolean validateForm() {
        return true;
    }

    @Override
    public void editForm(Object form) {
    }

    @Override
    public boolean deleteForm(Object form) {
        return false;
    }

    public void selectPage(String key) {
        selectedPageKey = key;
        loadSelectedPage();
    }

    private void loadSelectedPage() {
        ContentPage page = contentPageService.getPage(selectedPageKey);
        editorHtml = page == null ? "" : page.getHtmlContent();
    }

    public List<ContentPage> getPages() {
        return new ArrayList<>(contentPageService.getPages().values());
    }

    public String getSelectedPageKey() {
        return selectedPageKey;
    }

    public void setSelectedPageKey(String selectedPageKey) {
        this.selectedPageKey = selectedPageKey;
    }

    public String getEditorHtml() {
        return editorHtml;
    }

    public void setEditorHtml(String editorHtml) {
        this.editorHtml = editorHtml;
    }

    public String getSelectedPageTitle() {
        ContentPage page = contentPageService.getPage(selectedPageKey);
        return page == null ? "Content Page" : page.getTitle();
    }
}