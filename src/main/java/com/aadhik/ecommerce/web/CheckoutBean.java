package com.aadhik.ecommerce.web;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Named
@SessionScoped
public class CheckoutBean extends BaseBean implements Serializable {

    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("1500");
    private static final BigDecimal TAX_RATE = new BigDecimal("0.18");
    private static final BigDecimal COD_FEE = new BigDecimal("49");

    @Inject
    private CartBean cartBean;

    private String customerName;
    private String email;
    private String phone;
    private String addressLine;
    private String city;
    private String state;
    private String postalCode;

    private String shippingMethod = "STANDARD";
    private String paymentMethod = "ONLINE";
    private String promoCode;
    private boolean giftWrap;

    private String lastOrderReference;

    public String startCheckout() {
        if (cartBean == null || cartBean.isEmpty()) {
            addWarn("Cart is empty. Add products before checkout.");
            return null;
        }
        return "/checkout.xhtml?faces-redirect=true";
    }

    public String placeOrder() {
        if (cartBean == null || cartBean.isEmpty()) {
            addError("Your cart is empty.");
            return null;
        }
        if (isBlank(customerName) || isBlank(email) || isBlank(phone) || isBlank(addressLine)
                || isBlank(city) || isBlank(state) || isBlank(postalCode)) {
            addError("Please fill customer and shipping details to continue.");
            return null;
        }

        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        lastOrderReference = "ORD-" + datePart + "-" + randomPart;

        BigDecimal total = getGrandTotal();
        cartBean.clear();
        addInfo("Order placed successfully. Ref: " + lastOrderReference + ", payable: " + money(total));
        return "/index.xhtml?faces-redirect=true";
    }

    public List<ShippingMethod> getShippingMethods() {
        List<ShippingMethod> methods = new ArrayList<>();
        methods.add(new ShippingMethod("STANDARD", "Standard (3-5 days)", new BigDecimal("60")));
        methods.add(new ShippingMethod("EXPRESS", "Express (1-2 days)", new BigDecimal("140")));
        methods.add(new ShippingMethod("SAME_DAY", "Same day (metro only)", new BigDecimal("220")));
        return methods;
    }

    public List<PaymentMethod> getPaymentMethods() {
        List<PaymentMethod> methods = new ArrayList<>();
        methods.add(new PaymentMethod("ONLINE", "UPI / Card / Net banking"));
        methods.add(new PaymentMethod("COD", "Cash on Delivery"));
        methods.add(new PaymentMethod("WALLET", "Wallet / Store credits"));
        return methods;
    }

    public BigDecimal getSubtotal() {
        return cartBean == null ? BigDecimal.ZERO : cartBean.getEstimatedTotal();
    }

    public BigDecimal getShippingCharge() {
        if (getSubtotal().compareTo(FREE_SHIPPING_THRESHOLD) >= 0 && "STANDARD".equals(shippingMethod)) {
            return BigDecimal.ZERO;
        }
        return getShippingMethods().stream()
                .filter(method -> method.code().equals(shippingMethod))
                .map(ShippingMethod::fee)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal getDiscountAmount() {
        if (isBlank(promoCode)) {
            return BigDecimal.ZERO;
        }
        String normalized = promoCode.trim().toUpperCase(Locale.ROOT);
        if ("SAVE10".equals(normalized)) {
            return percent(getSubtotal(), new BigDecimal("0.10"));
        }
        if ("FREESHIP".equals(normalized)) {
            return getShippingCharge();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getPackagingCharge() {
        return giftWrap ? new BigDecimal("30") : BigDecimal.ZERO;
    }

    public BigDecimal getTaxAmount() {
        BigDecimal taxable = getSubtotal().add(getShippingCharge()).add(getPackagingCharge()).subtract(getDiscountAmount());
        if (taxable.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return percent(taxable, TAX_RATE);
    }

    public BigDecimal getPaymentSurcharge() {
        return "COD".equals(paymentMethod) ? COD_FEE : BigDecimal.ZERO;
    }

    public BigDecimal getGrandTotal() {
        return getSubtotal()
                .add(getShippingCharge())
                .add(getPackagingCharge())
                .add(getTaxAmount())
                .add(getPaymentSurcharge())
                .subtract(getDiscountAmount())
                .setScale(2, RoundingMode.HALF_UP);
    }

    public String money(BigDecimal value) {
        return "Rs. " + value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private BigDecimal percent(BigDecimal base, BigDecimal rate) {
        return base.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddressLine() { return addressLine; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(String shippingMethod) { this.shippingMethod = shippingMethod; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }
    public boolean isGiftWrap() { return giftWrap; }
    public void setGiftWrap(boolean giftWrap) { this.giftWrap = giftWrap; }
    public String getLastOrderReference() { return lastOrderReference; }

    public record ShippingMethod(String code, String label, BigDecimal fee) implements Serializable { }
    public record PaymentMethod(String code, String label) implements Serializable { }
}
