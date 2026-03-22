package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.Product;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Named
@SessionScoped
public class CartBean implements Serializable {

    private final List<CartItem> items = new ArrayList<>();

    public List<CartItem> getItems() {
        return items;
    }

    public int getItemCount() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public BigDecimal getEstimatedTotal() {
        return items.stream()
                .map(CartItem::getLineTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addProduct(Product product) {
        if (product == null) {
            return;
        }
        addOrIncrement(new CartItem(product.getId(), product.getName(), product.getImageUrl(), null, product.getPrice(), 1));
    }

    public void addVariant(Product product, HomePageBean.ProductVariantOption variant, String imageUrl) {
        if (product == null || variant == null) {
            return;
        }
        addOrIncrement(new CartItem(product.getId(), product.getName(), imageUrl, variant.getName(), variant.getPrice(), 1));
    }

    public void increase(int index) {
        CartItem item = getByIndex(index);
        if (item != null) {
            item.setQuantity(item.getQuantity() + 1);
        }
    }

    public void decrease(int index) {
        CartItem item = getByIndex(index);
        if (item == null) {
            return;
        }
        if (item.getQuantity() <= 1) {
            items.remove(index);
            return;
        }
        item.setQuantity(item.getQuantity() - 1);
    }

    public void remove(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    private void addOrIncrement(CartItem candidate) {
        for (CartItem item : items) {
            if (item.sameEntry(candidate)) {
                item.setQuantity(item.getQuantity() + candidate.getQuantity());
                return;
            }
        }
        items.add(candidate);
    }

    private CartItem getByIndex(int index) {
        if (index < 0 || index >= items.size()) {
            return null;
        }
        return items.get(index);
    }

    public static class CartItem implements Serializable {

        private Long productId;
        private String productName;
        private String imageUrl;
        private String variantName;
        private BigDecimal unitPrice;
        private int quantity;

        public CartItem() {
        }

        public CartItem(Long productId, String productName, String imageUrl, String variantName, BigDecimal unitPrice, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.imageUrl = imageUrl;
            this.variantName = variantName;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
        }

        public boolean sameEntry(CartItem other) {
            return Objects.equals(productId, other.productId)
                    && Objects.equals(variantName, other.variantName)
                    && Objects.equals(unitPrice, other.unitPrice);
        }

        public BigDecimal getLineTotal() {
            return unitPrice == null ? BigDecimal.ZERO : unitPrice.multiply(BigDecimal.valueOf(quantity));
        }

        public String getDisplayName() {
            return variantName == null || variantName.isBlank()
                    ? productName
                    : productName + " - " + variantName;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getVariantName() {
            return variantName;
        }

        public void setVariantName(String variantName) {
            this.variantName = variantName;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
