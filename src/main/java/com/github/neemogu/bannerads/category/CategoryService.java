package com.github.neemogu.bannerads.category;

import com.github.neemogu.bannerads.banner.Banner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public final class CategoryService {
    private final CategoryRepository repository;
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
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
        repository.save(category);
        return Optional.empty();
    }

    private Optional<String> checkExistingCategory(Category category) {
        Optional<String> checkedByName = checkExistingCategoryByName(category);
        if (checkedByName.isPresent()) {
            return checkedByName;
        }
        return checkExistingCategoryByReqName(category);
    }

    private Optional<String> checkExistingCategoryByName(Category category) {
        if (repository.existsByNameAndIdIsNot(category.getName(), category.getId())) {
            return Optional.of("Category with such name is already exist");
        }
        return Optional.empty();
    }

    private Optional<String> checkExistingCategoryByReqName(Category category) {
        if (repository.existsByReqNameAndIdIsNot(category.getReqName(), category.getId())) {
            return Optional.of("Category with such request name is already exist");
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
        Optional<Category> foundByName = repository.findByName(category.getName());
        if (foundByName.isPresent()) {
            if (!foundByName.get().getDeleted()) {
                return Optional.of("Category with such name is already exists");
            } else {
                repository.delete(foundByName.get());
            }
        }
        return Optional.empty();
    }

    private Optional<String> checkNewCategoryByReqName(Category category) {
        Optional<Category> foundByReqName = repository.findByReqName(category.getReqName());
        if (foundByReqName.isPresent()) {
            if (!foundByReqName.get().getDeleted()) {
                return Optional.of("Category with such request name is already exists");
            } else {
                repository.delete(foundByReqName.get());
            }
        }
        return Optional.empty();
    }

    /**
     * Removes a category by it's id from a database.
     *
     * @param id Category id.
     * @return If category has any banners returns a list of these banners,
     * else returns an empty list.
     */
    public List<Banner> deleteCategory(Integer id) {
        Category category = repository.findById(id).orElse(null);
        if (category != null) {
            List<Banner> banners = category.getBanners()
                    .stream()
                    .filter(b -> !b.getDeleted())
                    .collect(Collectors.toList());
            if (banners.size() > 0) {
                return banners;
            }
            category.setDeleted(true);
            saveCategory(category);
        }
        return Collections.emptyList();
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
     * Returns list of categories containing search string in name
     * or list of all categories if search string is empty. All categories in list is not deleted.
     *
     * @param searchName Search string
     * @return List of categories containing search string
     * or List of all categories if search string is empty
     */

    public List<Category> getCategoryList(String searchName) {
        EntityManager em = entityManagerFactory.createEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Category> query = builder.createQuery(Category.class);
        Root<Category> root = query.from(Category.class);

        List<Predicate> wherePredicates = getWherePredicates(builder, root, searchName);

        query.where(builder.and(wherePredicates.toArray(new Predicate[0])));

        List<Category> result = em.createQuery(query.select(root)).getResultList();
        em.close();
        return result;
    }

    /**
     * Returns a category by it's id
     *
     * @param id Category id
     * @return Optional - category object if category with such id exists and not deleted else empty
     */
    public Optional<Category> getSpecificCategory(Integer id) {
        Optional<Category> found = repository.findById(id);
        if (found.isPresent() && found.get().getDeleted()) {
            return Optional.empty();
        }
        return found;
    }
}
