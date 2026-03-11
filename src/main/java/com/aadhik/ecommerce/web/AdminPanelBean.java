package com.aadhik.ecommerce.web;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * @author THIRUPATHI G
 */
@Named
@ViewScoped
public class AdminPanelBean extends AdminBean {

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
    
    public void setActiveMenu(String activeMenu) {
        this.activeMenu = activeMenu;
    }

    public String getActiveMenu() {
        return activeMenu;
    }

}
