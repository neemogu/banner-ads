package com.github.neemogu.bannerads.category;

import com.github.neemogu.bannerads.banner.Banner;
import com.github.neemogu.bannerads.banner.BannerRepository;
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
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final BannerRepository bannerRepository;
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, BannerRepository bannerRepository) {
        this.categoryRepository = categoryRepository;
        this.bannerRepository = bannerRepository;
    }

    /**
     * Adds (when id in a category object is null)
     * or updates (when id in a category object is not null)
     * a category in a database.
     *
     * @param category Category object.
     * @return Optional - string containing error message if there was an error else empty
     */
    public Optional<String> saveCategory(Category category) {
        Optional<String> checked;
        if (category.getId() != null) {
            checked = checkExistingCategory(category);
        } else {
            checked = checkNewCategory(category);
        }
        if (checked.isPresent()) {
            return checked;
        }
        categoryRepository.save(category);
        return Optional.empty();
    }

    private Optional<String> checkExistingCategory(Category category) {
        if (!categoryRepository.existsByIdAndDeletedFalse(category.getId())) {
            return Optional.of("Category with such ID does not exist");
        }
        Optional<String> checkedByName = checkExistingCategoryByName(category);
        if (checkedByName.isPresent()) {
            return checkedByName;
        }
        return checkExistingCategoryByReqName(category);
    }

    private Optional<String> checkExistingCategoryByName(Category category) {
        Optional<Category> foundByName = categoryRepository.findByNameAndIdIsNot(category.getName(), category.getId());
        if (foundByName.isPresent()) {
            if (!foundByName.get().getDeleted()) {
                return Optional.of("Category with such name is already exists");
            } else {
                categoryRepository.delete(foundByName.get());
            }
        }
        return Optional.empty();
    }

    private Optional<String> checkExistingCategoryByReqName(Category category) {
        Optional<Category> foundByReqName = categoryRepository
                .findByReqNameAndIdIsNot(category.getReqName(), category.getId());
        if (foundByReqName.isPresent()) {
            if (!foundByReqName.get().getDeleted()) {
                return Optional.of("Category with such request name is already exists");
            } else {
                categoryRepository.delete(foundByReqName.get());
            }
        }
        return Optional.empty();
    }

    private Optional<String> checkNewCategory(Category category) {
        Optional<String> checkedByName = checkNewCategoryByName(category);
        if (checkedByName.isPresent()) {
            return checkedByName;
        }
        return checkNewCategoryByReqName(category);
    }

    private Optional<String> checkNewCategoryByName(Category category) {
        Optional<Category> foundByName = categoryRepository.findByName(category.getName());
        if (foundByName.isPresent()) {
            if (!foundByName.get().getDeleted()) {
                return Optional.of("Category with such name is already exists");
            } else {
                categoryRepository.delete(foundByName.get());
            }
        }
        return Optional.empty();
    }

    private Optional<String> checkNewCategoryByReqName(Category category) {
        Optional<Category> foundByReqName = categoryRepository.findByReqName(category.getReqName());
        if (foundByReqName.isPresent()) {
            if (!foundByReqName.get().getDeleted()) {
                return Optional.of("Category with such request name is already exists");
            } else {
                categoryRepository.delete(foundByReqName.get());
            }
        }
        return Optional.empty();
    }

    /**
     * Removes a category by it's id from a database.
     *
     * @param id Category id.
     * @return Optional - string containing error message if there was an error else empty
     */
    public Optional<String> deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            List<Banner> banners = bannerRepository.findAllByDeletedFalseAndCategoryIs(category);
            if (banners.size() > 0) {
                List<Integer> bannerIds = banners.stream().map(Banner::getId).collect(Collectors.toList());
                return Optional.of("Error: category still has banners with IDs: " + bannerIds);
            }
            category.setDeleted(true);
            categoryRepository.save(category);
        }
        return Optional.empty();
    }

    private List<Predicate> getWherePredicates(CriteriaBuilder builder,
                                               Root<Category> root,
                                               String searchName) {
        List<Predicate> predicates = new LinkedList<>();
        if (!searchName.equals("")) {
            predicates.add(builder.like(builder.lower(root.get("name")), "%" + searchName.toLowerCase() + "%"));
        }
        predicates.add(builder.isFalse(root.get("deleted")));
        return predicates;
    }

    /**
     * Returns paged list of categories satisfying the parameters. All categories in list is not deleted.
     * If search string is empty there won't be any filtering by name.
     *
     * @param parameters Parameters object containing page number, page size and a search string.
     * @return Paged list of categories satisfying the parameters.
     */

    public List<Category> getCategoryList(CategoryFetchParameters parameters) {
        EntityManager em = entityManagerFactory.createEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Category> query = builder.createQuery(Category.class);
        Root<Category> root = query.from(Category.class);

        List<Predicate> wherePredicates = getWherePredicates(builder, root, parameters.getSearchName());
        query.where(builder.and(wherePredicates.toArray(new Predicate[0])));

        List<Category> result = em.createQuery(query.select(root))
                .setFirstResult(parameters.getPage() * parameters.getPageSize())
                .setMaxResults(parameters.getPageSize()).getResultList();
        em.close();
        return result;
    }

    /**
     * Returns number of pages of categories satisfying the parameters.
     * If search string is empty there won't be any filtering by name.
     *
     * @param parameters Parameters object containing page size and search string.
     * @return Number of pages of categories satisfying the parameters.
     */

    public long getCategoryListPageCount(CategoryFetchParameters parameters) {
        EntityManager em = entityManagerFactory.createEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Category> root = query.from(Category.class);

        List<Predicate> wherePredicates = getWherePredicates(builder, root, parameters.getSearchName());
        query.where(builder.and(wherePredicates.toArray(new Predicate[0])));
        query.select(builder.count(root));

        Long result = em.createQuery(query).getSingleResult();
        em.close();
        return result / parameters.getPageSize() + (result % parameters.getPageSize() == 0 ? 0 : 1);
    }

    /**
     * Returns a category by it's id.
     *
     * @param id Category id.
     * @return Optional - category object if category with such id exists and not deleted else empty.
     */
    public Optional<Category> getSpecificCategory(Integer id) {
        return categoryRepository.findByIdAndDeletedFalse(id);
    }
}
