package com.github.neemogu.bannerads.banner;

import com.github.neemogu.bannerads.category.CategoryRepository;
import com.github.neemogu.bannerads.category.CategoryService;
import com.github.neemogu.bannerads.util.SortDirection;
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
    private final BannerRepository bannerRepository;
    private final CategoryRepository categoryRepository;
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    public BannerService(BannerRepository bannerRepository, CategoryRepository categoryRepository) {
        this.bannerRepository = bannerRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Adds (when id in a banner object is null)
     * or updates (when id in a banner object is not null)
     * a banner in a database.
     *
     * @param banner Banner object.
     * @return Optional - string containing error message if there was an error else empty.
     */
    public Optional<String> saveBanner(Banner banner) {
        Optional<String> checked = checkCategory(banner);
        if (checked.isPresent()) {
            return checked;
        }
        if (banner.getId() != null) {
            checked = checkExistingBanner(banner);
        } else {
            checked = checkNewBanner(banner);
        }
        if (checked.isPresent()) {
            return checked;
        }
        bannerRepository.save(banner);
        return Optional.empty();
    }

    private Optional<String> checkCategory(Banner banner) {
        if (!categoryRepository.existsByIdAndDeletedFalse(banner.getCategory().getId())) {
            return Optional.of("Category with such ID does not exist");
        }
        return Optional.empty();
    }

    private Optional<String> checkExistingBanner(Banner banner) {
        if (!bannerRepository.existsById(banner.getId())) {
            return Optional.of("Banner with such ID does not exist");
        }
        Optional<Banner> foundByName = bannerRepository.findByNameAndIdIsNot(banner.getName(), banner.getId());
        if (foundByName.isPresent()) {
            if (!foundByName.get().getDeleted()) {
                return Optional.of("Banner with such name is already exists");
            } else {
                bannerRepository.delete(foundByName.get());
            }
        }
        return Optional.empty();
    }

    private Optional<String> checkNewBanner(Banner banner) {
        Optional<Banner> foundByName = bannerRepository.findByName(banner.getName());
        if (foundByName.isPresent()) {
            if (!foundByName.get().getDeleted()) {
                return Optional.of("Banner with such name is already exists");
            } else {
                bannerRepository.delete(foundByName.get());
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
        Banner banner = bannerRepository.findById(id).orElse(null);
        if (banner != null) {
            banner.setDeleted(true);
            saveBanner(banner);
        }
    }

    private List<Predicate> getWherePredicates(CriteriaBuilder builder,
                                               Root<Banner> root,
                                               String searchName,
                                               Integer categoryId) {
        List<Predicate> predicates = new LinkedList<>();
        if (!searchName.equals("")) {
            predicates.add(builder.like(builder.lower(root.get("name")), "%" + searchName.toLowerCase() + "%"));
        }
        if (categoryId != null) {
            predicates.add(builder.equal(root.get("category").get("id"), categoryId));
        }
        predicates.add(builder.isFalse(root.get("deleted")));
        return predicates;
    }

    /**
     * Returns paged list of banners satisfying the parameters. All banners in list is not deleted.
     * If search string is empty there won't be any filtering by name.
     *
     * @param parameters Parameters object that contain page number, page size,
     *                  sort direction, sort by field name, a search string and may contain a category id.
     * @return Paged list of banners satisfying the parameters.
     */

    public List<Banner> getBannerList(BannerFetchParameters parameters) {
        EntityManager em = entityManagerFactory.createEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Banner> query = builder.createQuery(Banner.class);
        Root<Banner> root = query.from(Banner.class);

        if (parameters.getSortDirection() == SortDirection.ASC) {
            query.orderBy(builder.asc(root.get(parameters.getSortBy().name().toLowerCase())));
        } else if (parameters.getSortDirection() == SortDirection.DESC) {
            query.orderBy(builder.desc(root.get(parameters.getSortBy().name().toLowerCase())));
        }

        List<Predicate> wherePredicates = getWherePredicates(
                builder,
                root,
                parameters.getSearchName(),
                parameters.getCategoryId()
        );
        query.where(builder.and(wherePredicates.toArray(new Predicate[0])));

        List<Banner> result = em.createQuery(query.select(root))
                .setFirstResult(parameters.getPage() * parameters.getPageSize())
                .setMaxResults(parameters.getPageSize()).getResultList();
        em.close();
        return result;
    }

    /**
     * Returns number of pages of banners satisfying the parameters.
     * If search string is empty there won't be any filtering by name.
     *
     * @param parameters Parameters object that contain page size, a search string and may contain a category id.
     * @return Number of pages of banners satisfying the parameters.
     */

    public long getBannerListPageCount(BannerFetchParameters parameters) {
        EntityManager em = entityManagerFactory.createEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Banner> root = query.from(Banner.class);

        List<Predicate> wherePredicates = getWherePredicates(
                builder,
                root,
                parameters.getSearchName(),
                parameters.getCategoryId()
        );
        query.where(builder.and(wherePredicates.toArray(new Predicate[0])));
        query.select(builder.count(root));

        Long result = em.createQuery(query).getSingleResult();
        em.close();
        return result / parameters.getPageSize() +(result % parameters.getPageSize() == 0 ? 0 : 1);
    }

    /**
     * Returns a banner by it's id.
     *
     * @param id Banner id.
     * @return Optional - banner object if banner with such id exists and not deleted else empty.
     */
    public Optional<Banner> getSpecificBanner(Integer id) {
        Optional<Banner> found = bannerRepository.findById(id);
        if (found.isPresent() && found.get().getDeleted()) {
            return Optional.empty();
        }
        return found;
    }
}
