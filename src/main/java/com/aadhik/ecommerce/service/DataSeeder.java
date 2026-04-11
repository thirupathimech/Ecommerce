package com.aadhik.ecommerce.service;

import com.aadhik.ecommerce.model.ContentPage;
import com.aadhik.ecommerce.model.HomeDivSection;
import com.aadhik.ecommerce.model.HomeSlider;
import com.aadhik.ecommerce.model.MenuItemTargetType;
import com.aadhik.ecommerce.model.Product;
import com.aadhik.ecommerce.model.ProductCollection;
import com.aadhik.ecommerce.model.StoreMenuItem;
import com.aadhik.ecommerce.model.ThemeConfig;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;

@Singleton
@Startup
public class DataSeeder {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager entityManager;

    @PostConstruct
    @Transactional
    public void seed() {
        try {
            seedThemeIfNeeded();
            seedContentPages();
            seedStoreMenusIfNeeded();

            Long existing = entityManager.createQuery("select count(c) from ProductCollection c", Long.class)
                    .getSingleResult();
            if (existing != null && existing > 0) {
                return;
            }

            ProductCollection bestSellers = createCollection("Best Sellers", "best-sellers",
                    "https://images.unsplash.com/photo-1494390248081-4e521a5940db?w=900&h=400&fit=crop",
                    "Top moving health drinks and cereals");

            ProductCollection nutrition = createCollection("Nutrition Drinks", "nutrition-drinks",
                    "https://images.unsplash.com/photo-1615486363974-bfd7c6f33c0f?w=900&h=400&fit=crop",
                    "Energy and immunity boosters");

            createSlider(1, "Sip Your Way to Health", "Premium wholesome powders",
                    "https://images.unsplash.com/photo-1593095948071-474c5cc2989d?w=1400&h=700&fit=crop");
            createSlider(2, "Sprouted Mix", "Natural goodness for all ages",
                    "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=1400&h=700&fit=crop");
            createSlider(3, "5 in 1 Weight Gain", "Healthy weight support",
                    "https://images.unsplash.com/photo-1467453678174-768ec283a940?w=1400&h=700&fit=crop");

            createProduct("Apple Instant Cerealac - Homemade & Nutritious",
                    "https://images.unsplash.com/photo-1602741007916-4e52790f3f3f?w=700&h=900&fit=crop",
                    new BigDecimal("320.00"), new BigDecimal("375.00"), true, bestSellers);
            createProduct("Nendran Banana Nutty Dates Drink",
                    "https://images.unsplash.com/photo-1627483262955-c873b837f078?w=700&h=900&fit=crop",
                    new BigDecimal("320.00"), new BigDecimal("750.00"), true, bestSellers);
            createProduct("5-in-1 Weight Gain Nutrii Drink",
                    "https://images.unsplash.com/photo-1523906630133-f6934a1ab2b9?w=700&h=900&fit=crop",
                    new BigDecimal("270.00"), new BigDecimal("675.00"), true, nutrition);
            createProduct("Sprouted Ragi Almond & Dates Powder",
                    "https://images.unsplash.com/photo-1514996937319-344454492b37?w=700&h=900&fit=crop",
                    new BigDecimal("250.00"), null, true, nutrition);

            createHomeDivSection(
                    "From mother's heart to baby's first smile 😇",
                    "The Mothers Care brings you 100% pure, organic baby essentials crafted with love and nature's finest ingredients",
                    "Visit",
                    "#",
                    "https://images.unsplash.com/photo-1544126592-807ade215a0b?w=900&h=1000&fit=crop",
                    "LEFT",
                    "CENTER",
                    1);
        } catch (Exception e) {
            System.out.println(">>>>> com.aadhik.ecommerce.service.DataSeeder.seed() ## START");
            e.printStackTrace();
            System.out.println(">>>>> com.aadhik.ecommerce.service.DataSeeder.seed() ## END");
        }
    }
    
    private void seedThemeIfNeeded() {
        Long themeCount = entityManager.createQuery("select count(t) from ThemeConfig t", Long.class)
                .getSingleResult();
        if (themeCount != null && themeCount > 0) {
            return;
        }

        ThemeConfig theme = new ThemeConfig();
        theme.setPrimaryBackground("#FFF8E8");
        theme.setPrimaryColor("#173676");
        theme.setBuyNowBackground("#173676");
        theme.setBuyNowTextColor("#FFFFFF");
        theme.setAddCartBackground("#F4D332");
        theme.setAddCartTextColor("#173676");
        theme.setMenuDrawerBackground("#F4D332");
        theme.setHeaderTextColor("#FFFFFF");
        theme.setUpdatedAt(java.time.LocalDateTime.now());
        entityManager.persist(theme);
    }

    private void seedStoreMenusIfNeeded() {
        Long menuCount = entityManager.createQuery("select count(m) from StoreMenuItem m", Long.class)
                .getSingleResult();
        if (menuCount != null && menuCount > 0) {
            return;
        }

        createStoreMenu("Home", MenuItemTargetType.HOME, null, 1);
        createStoreMenu("All Products", MenuItemTargetType.ALL_PRODUCTS, null, 2);

        ProductCollection firstCollection = entityManager.createQuery("select c from ProductCollection c order by c.id asc", ProductCollection.class)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElse(null);
        if (firstCollection != null) {
            createStoreMenu(firstCollection.getName(), MenuItemTargetType.COLLECTION, firstCollection.getSlug(), 3);
        }

        Product firstProduct = entityManager.createQuery("select p from Product p order by p.id asc", Product.class)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElse(null);
        if (firstProduct != null) {
            createStoreMenu(firstProduct.getName(), MenuItemTargetType.PRODUCT, String.valueOf(firstProduct.getId()), 4);
        }
    }

    private void seedContentPages() {
        Long pageCount = entityManager.createQuery("select count(c) from ContentPage c", Long.class)
                .getSingleResult();
        if (pageCount != null && pageCount > 0) {
            return;
        }

        createContentPage("shipping", "shipping-policy", "Shipping Policy", 1,
                "<h2>Shipping Policy</h2><p>Orders are usually packed within 1 to 2 business days and delivered based on your location and courier availability.</p><ul><li>Shipping confirmation is shared once your order is dispatched.</li><li>Delivery timelines can vary during weekends, holidays, or peak sale periods.</li><li>Please ensure your address and phone number are correct while placing the order.</li></ul>");
        createContentPage("refund", "refund-policy", "Refund Policy", 2,
                "<h2>Refund Policy</h2><p>If you receive a damaged, defective, or incorrect product, contact our support team within 48 hours of delivery.</p><ul><li>Approved refunds are processed to the original payment method.</li><li>Refund settlement time depends on your bank or payment provider.</li><li>Replacement can be arranged when eligible stock is available.</li></ul>");
        createContentPage("privacy", "privacy-policy", "Privacy Policy", 3,
                "<h2>Privacy Policy</h2><p>We collect only the customer information needed to process orders, provide support, and improve store communication.</p><ul><li>Contact and shipping details are used to fulfill your orders.</li><li>We do not share your personal data except when required for delivery or payment processing.</li><li>Reasonable operational safeguards are used to protect your data.</li></ul>");
        createContentPage("contact", "contact-information", "Contact Information", 4,
                "<h2>Contact Information</h2><p>For order help, delivery questions, or product clarification, please contact our support team.</p><ul><li><strong>Email:</strong> support@ecommerce-demo.com</li><li><strong>Phone:</strong> +91 90000 12345</li><li><strong>Business Hours:</strong> Monday to Saturday, 10:00 AM to 6:00 PM</li><li><strong>Address:</strong> 24 Market Lane, Chennai, Tamil Nadu</li></ul>");
    }

    private void createContentPage(String key, String slug, String title, int sortOrder, String htmlContent) {
        ContentPage page = new ContentPage();
        page.setKey(key);
        page.setSlug(slug);
        page.setTitle(title);
        page.setSortOrder(sortOrder);
        page.setHtmlContent(htmlContent);
        entityManager.persist(page);
    }

    private ProductCollection createCollection(String name, String slug, String image, String description) {
        ProductCollection collection = new ProductCollection();
        collection.setName(name);
        collection.setSlug(slug);
        collection.setBannerImage(image);
        collection.setDescription(description);
        collection.setActive(true);
        entityManager.persist(collection);
        return collection;
    }

    private void createSlider(int sortOrder, String title, String subtitle, String imageUrl) {
        HomeSlider slider = new HomeSlider();
        slider.setSortOrder(sortOrder);
        slider.setTitle(title);
        slider.setSubtitle(subtitle);
        slider.setImageUrl(imageUrl);
        slider.setActive(true);
        entityManager.persist(slider);
    }

    private void createProduct(String name, String image, BigDecimal price, BigDecimal comparePrice,
            boolean featured, ProductCollection collection) {
        Product product = new Product();
        product.setName(name);
        product.setImageUrl(image);
        product.setPrice(price);
        product.setComparePrice(comparePrice);
        product.setFeatured(featured);
        product.setActive(true);
        product.setCollection(collection);
        entityManager.persist(product);
    }

    private void createStoreMenu(String label, MenuItemTargetType targetType, String targetRef, int sortOrder) {
        StoreMenuItem item = new StoreMenuItem();
        item.setLabel(label);
        item.setTargetType(targetType);
        item.setTargetRef(targetRef);
        item.setSortOrder(sortOrder);
        item.setActive(true);
        entityManager.persist(item);
    }

    private void createHomeDivSection(String heading, String description, String buttonLabel, String buttonLink,
            String imageUrl, String imageSide, String contentAlign, int sortOrder) {
        HomeDivSection section = new HomeDivSection();
        section.setHeading(heading);
        section.setDescription(description);
        section.setButtonLabel(buttonLabel);
        section.setButtonLink(buttonLink);
        section.setImageUrl(imageUrl);
        section.setImageSide(imageSide);
        section.setContentAlign(contentAlign);
        section.setSortOrder(sortOrder);
        section.setActive(true);
        entityManager.persist(section);
    }
}
