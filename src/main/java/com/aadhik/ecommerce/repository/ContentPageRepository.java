package com.aadhik.ecommerce.repository;

import com.aadhik.ecommerce.model.ContentPage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ContentPageRepository {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager entityManager;

    public List<ContentPage> findAll() {
        return entityManager.createQuery("""
                        select p from ContentPage p
                        order by p.sortOrder asc, p.id asc
                        """, ContentPage.class)
                .getResultList();
    }

    public ContentPage findByKey(String key) {
        List<ContentPage> pages = entityManager.createQuery("""
                        select p from ContentPage p
                        where p.pageKey = :key
                        """, ContentPage.class)
                .setParameter("key", key)
                .setMaxResults(1)
                .getResultList();
        return pages.isEmpty() ? null : pages.get(0);
    }

    public ContentPage findBySlug(String slug) {
        List<ContentPage> pages = entityManager.createQuery("""
                        select p from ContentPage p
                        where p.slug = :slug
                        """, ContentPage.class)
                .setParameter("slug", slug)
                .setMaxResults(1)
                .getResultList();
        return pages.isEmpty() ? null : pages.get(0);
    }

    public long countAll() {
        return entityManager.createQuery("select count(p) from ContentPage p", Long.class)
                .getSingleResult();
    }

    @Transactional
    public ContentPage save(ContentPage page) {
        if (page.getId() == null) {
            entityManager.persist(page);
            return page;
        }
        return entityManager.merge(page);
    }
}