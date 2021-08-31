package com.github.neemogu.bannerads.banner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class BannerService {
    private final BannerRepository repository;
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    public BannerService(BannerRepository repository) {
        this.repository = repository;
    }

    /**
     * Adds (when id in a banner object is null)
     * or updates (when id in a banner object is not null)
     * a banner in a database.
     *
     * @param banner Banner object.
     * @return Optional - string containing error message if there was an error else empty
     */
    public Optional<String> saveBanner(Banner banner) {
        Optional<String> checked;
        if (banner.getId() != null) {
            checked = checkExistingBanner(banner);
        } else {
            checked = checkNewBanner(banner);
        }
        if (checked.isPresent()) {
            return checked;
        }
        repository.save(banner);
        return Optional.empty();
    }

    private Optional<String> checkExistingBanner(Banner banner) {
        if (repository.existsByNameAndIdIsNot(banner.getName(), banner.getId())) {
            return Optional.of("Banner with such name is already exist");
        }
        return Optional.empty();
    }

    private Optional<String> checkNewBanner(Banner banner) {
        Optional<Banner> foundByName = repository.findByName(banner.getName());
        if (foundByName.isPresent()) {
            if (!foundByName.get().getDeleted()) {
                return Optional.of("Banner with such name is already exists");
            } else {
                repository.delete(foundByName.get());
            }
        }
        return Optional.empty();
    }

    /**
     * Removes a banner by it's id from a database.
     *
     * @param id Banner id.
     *
     */
    public void deleteBanner(Integer id) {
        Banner banner = repository.findById(id).orElse(null);
        if (banner != null) {
            banner.setDeleted(true);
            saveBanner(banner);
        }
    }

    private List<Predicate> getWherePredicates(CriteriaBuilder builder,
                                               Root<Banner> root,
                                               String searchName) {
        List<Predicate> predicates = new LinkedList<>();
        if (!searchName.equals("")) {
            predicates.add(builder.like(builder.lower(root.get("name")), "%" + searchName.toLowerCase() + "%"));
        }
        predicates.add(builder.isFalse(root.get("deleted")));
        return predicates;
    }

    /**
     * Returns list of banners containing search string in name
     * or list of all banners if search string is empty. All banners in list is not deleted.
     *
     * @param searchName Search string
     * @return List of banners containing search string
     * or List of all banners if search string is empty
     */

    public List<Banner> getBannerList(String searchName) {
        EntityManager em = entityManagerFactory.createEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Banner> query = builder.createQuery(Banner.class);
        Root<Banner> root = query.from(Banner.class);

        List<Predicate> wherePredicates = getWherePredicates(builder, root, searchName);

        query.where(builder.and(wherePredicates.toArray(new Predicate[0])));

        List<Banner> result = em.createQuery(query.select(root)).getResultList();
        em.close();
        return result;
    }

    /**
     * Returns a banner by it's id
     *
     * @param id Banner id
     * @return Optional - banner object if banner with such id exists and not deleted else empty
     */
    public Optional<Banner> getSpecificBanner(Integer id) {
        Optional<Banner> found = repository.findById(id);
        if (found.isPresent() && found.get().getDeleted()) {
            return Optional.empty();
        }
        return found;
    }
}
