package com.aadhik.ecommerce.web;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.primefaces.event.TabChangeEvent;

/**
 * @author THIRUPATHI G
 */
@Named
@ViewScoped
public class AdminHomePageSettingsBean extends AdminBean {

    private int homeTabIndex = 0;

    @Override
    public void resetForm() {
    }

    @Override
    public void saveForm() {
    }

    @Override
    public boolean validateForm() {
        return false;
    }

    @Override
    public void editForm(Object form) {
    }

    @Override
    public boolean deleteForm(Object form) {
        return false;
    }

    public void onHomeTabChange(TabChangeEvent event) {
        if (event != null && event.getTab() != null) {
            homeTabIndex = event.getIndex();
        }
    }

    public int getHomeTabIndex() {
        return homeTabIndex;
    }
}
