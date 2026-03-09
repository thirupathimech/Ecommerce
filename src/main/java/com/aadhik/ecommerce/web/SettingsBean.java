package com.aadhik.ecommerce.web;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * @author THIRUPATHI G
 */
@Named
@ViewScoped
public class SettingsBean extends AdminBean {

    private String storeName;
    private String storeLogo;
    private String storeFavicon;
    private String storeEmail;
    private String storePhoneNumber;
    private String storeAddress;
    private String storePinCode;
    private String storePassword;
    private String gstNumber;
    private boolean orderEmailNotification;
    private boolean adminOrderAlert;
    private String smsApiKey;
    private String whatsappApiKey;
    private String otpProvider;
    private String metaTitle;
    private String metaDescription;
    private String googleAnalyticsId;
    private String facebookPixelId;
    private String sitemapUrl;
    private boolean allowGuestCheckout;
    private Integer orderCancellationTime;
    private Integer returnPolicyDays;
    private String whatsappApi;
    private String smsApi;
    private String emailSmtp;
    private String deliveryTrackingApi;

    @Override
    public void resetForm() {
    }

    @Override
    public void saveForm() {
        addInfo("Settings saved successfully");
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

    private void loadDefaultSettings() {
        storeName = "My Ecommerce Store";
        storeLogo = "";
        storeFavicon = "";
        storeEmail = "support@example.com";
        storePhoneNumber = "+91";
        storeAddress = "";
        storePinCode = "";
        storePassword = "";
        gstNumber = "";
        orderEmailNotification = true;
        adminOrderAlert = true;
        smsApiKey = "";
        whatsappApiKey = "";
        otpProvider = "twilio";
        metaTitle = "Buy Online | My Ecommerce Store";
        metaDescription = "Best offers and products from our ecommerce store.";
        googleAnalyticsId = "";
        facebookPixelId = "";
        sitemapUrl = "https://example.com/sitemap.xml";
        allowGuestCheckout = true;
        orderCancellationTime = 24;
        returnPolicyDays = 7;
        whatsappApi = "Meta Cloud API";
        smsApi = "Twilio";
        emailSmtp = "smtp.gmail.com:587";
        deliveryTrackingApi = "Shiprocket";
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreLogo() {
        return storeLogo;
    }

    public void setStoreLogo(String storeLogo) {
        this.storeLogo = storeLogo;
    }

    public String getStoreFavicon() {
        return storeFavicon;
    }

    public void setStoreFavicon(String storeFavicon) {
        this.storeFavicon = storeFavicon;
    }

    public String getStoreEmail() {
        return storeEmail;
    }

    public void setStoreEmail(String storeEmail) {
        this.storeEmail = storeEmail;
    }

    public String getStorePhoneNumber() {
        return storePhoneNumber;
    }

    public void setStorePhoneNumber(String storePhoneNumber) {
        this.storePhoneNumber = storePhoneNumber;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getStorePinCode() {
        return storePinCode;
    }

    public void setStorePinCode(String storePinCode) {
        this.storePinCode = storePinCode;
    }

    public String getStorePassword() {
        return storePassword;
    }

    public void setStorePassword(String storePassword) {
        this.storePassword = storePassword;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public boolean isOrderEmailNotification() {
        return orderEmailNotification;
    }

    public void setOrderEmailNotification(boolean orderEmailNotification) {
        this.orderEmailNotification = orderEmailNotification;
    }

    public boolean isAdminOrderAlert() {
        return adminOrderAlert;
    }

    public void setAdminOrderAlert(boolean adminOrderAlert) {
        this.adminOrderAlert = adminOrderAlert;
    }

    public String getSmsApiKey() {
        return smsApiKey;
    }

    public void setSmsApiKey(String smsApiKey) {
        this.smsApiKey = smsApiKey;
    }

    public String getWhatsappApiKey() {
        return whatsappApiKey;
    }

    public void setWhatsappApiKey(String whatsappApiKey) {
        this.whatsappApiKey = whatsappApiKey;
    }

    public String getOtpProvider() {
        return otpProvider;
    }

    public void setOtpProvider(String otpProvider) {
        this.otpProvider = otpProvider;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getGoogleAnalyticsId() {
        return googleAnalyticsId;
    }

    public void setGoogleAnalyticsId(String googleAnalyticsId) {
        this.googleAnalyticsId = googleAnalyticsId;
    }

    public String getFacebookPixelId() {
        return facebookPixelId;
    }

    public void setFacebookPixelId(String facebookPixelId) {
        this.facebookPixelId = facebookPixelId;
    }

    public String getSitemapUrl() {
        return sitemapUrl;
    }

    public void setSitemapUrl(String sitemapUrl) {
        this.sitemapUrl = sitemapUrl;
    }

    public boolean isAllowGuestCheckout() {
        return allowGuestCheckout;
    }

    public void setAllowGuestCheckout(boolean allowGuestCheckout) {
        this.allowGuestCheckout = allowGuestCheckout;
    }

    public Integer getOrderCancellationTime() {
        return orderCancellationTime;
    }

    public void setOrderCancellationTime(Integer orderCancellationTime) {
        this.orderCancellationTime = orderCancellationTime;
    }

    public Integer getReturnPolicyDays() {
        return returnPolicyDays;
    }

    public void setReturnPolicyDays(Integer returnPolicyDays) {
        this.returnPolicyDays = returnPolicyDays;
    }

    public String getWhatsappApi() {
        return whatsappApi;
    }

    public void setWhatsappApi(String whatsappApi) {
        this.whatsappApi = whatsappApi;
    }

    public String getSmsApi() {
        return smsApi;
    }

    public void setSmsApi(String smsApi) {
        this.smsApi = smsApi;
    }

    public String getEmailSmtp() {
        return emailSmtp;
    }

    public void setEmailSmtp(String emailSmtp) {
        this.emailSmtp = emailSmtp;
    }

    public String getDeliveryTrackingApi() {
        return deliveryTrackingApi;
    }

    public void setDeliveryTrackingApi(String deliveryTrackingApi) {
        this.deliveryTrackingApi = deliveryTrackingApi;
    }

}
