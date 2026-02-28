package com.aadhik.ecommerce.repository;

import com.aadhik.ecommerce.model.HomeSlider;
import com.aadhik.ecommerce.model.HomepageSection;
import com.aadhik.ecommerce.model.Product;
import com.aadhik.ecommerce.model.ProductCollection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class CatalogRepository {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager entityManager;

    public List<HomeSlider> findActiveSliders() {
        return entityManager.createQuery("""
                        select s from HomeSlider s
                        where s.active = true
                        order by s.sortOrder asc, s.id asc
                        """, HomeSlider.class)
                .getResultList();
    }

    public List<HomepageSection> findActiveSections() {
        return entityManager.createQuery("""
                        select hs from HomepageSection hs
                        left join fetch hs.collection c
                        where hs.active = true
                        order by hs.sortOrder asc, hs.id asc
                        """, HomepageSection.class)
                .getResultList();
    }

    public List<Product> findFeaturedProducts(int limit) {
        return entityManager.createQuery("""
                        select p from Product p
                        where p.active = true and p.featured = true
                        order by p.id desc
                        """, Product.class)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Product> findProductsByCollection(Long collectionId, int limit) {
        return entityManager.createQuery("""
                        select p from Product p
                        where p.active = true and p.collection.id = :collectionId
                        order by p.id desc
                        """, Product.class)
                .setParameter("collectionId", collectionId)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<ProductCollection> findCollections() {
        return entityManager.createQuery("""
                        select c from ProductCollection c
                        where c.active = true
                        order by c.id desc
                        """, ProductCollection.class)
                .getResultList();
    }

    public List<Product> findProducts() {
        return entityManager.createQuery("""
                        select p from Product p
                        left join fetch p.collection c
                        where p.active = true
                        order by p.id desc
                        """, Product.class)
                .getResultList();
    }

    @Transactional
    public HomeSlider saveSlider(HomeSlider slider) {
        if (slider.getId() == null) {
            entityManager.persist(slider);
            return slider;
        }
        return entityManager.merge(slider);
    }

    @Transactional
    public HomepageSection saveSection(HomepageSection section) {
        if (section.getCollection() != null) {
            if (section.getCollection().getId() != null) {
                ProductCollection managedCollection = entityManager.find(ProductCollection.class, section.getCollection().getId());
                section.setCollection(managedCollection);
            } else {
                section.setCollection(null);
            }
        }

        if (section.getId() == null) {
            entityManager.persist(section);
            return section;
        }

        return entityManager.merge(section);
    }

    @Transactional
    public ProductCollection saveCollection(ProductCollection collection) {
        if (collection.getId() == null) {
            entityManager.persist(collection);
            return collection;
        }
        return entityManager.merge(collection);
    }

    @Transactional
    public Product saveProduct(Product product) {
        if (product.getCollection() != null) {
            if (product.getCollection().getId() != null) {
                ProductCollection managedCollection = entityManager.find(ProductCollection.class, product.getCollection().getId());
                product.setCollection(managedCollection);
            } else {
                product.setCollection(null);
            }
        }

        if (product.getId() == null) {
            entityManager.persist(product);
            return product;
        }

        return entityManager.merge(product);
    }
}
