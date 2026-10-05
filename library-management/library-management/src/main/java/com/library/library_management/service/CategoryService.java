package com.library.library_management.service;

import com.library.library_management.dto.CategoryForm;
import com.library.library_management.entity.Category;
import com.library.library_management.exception.BusinessException;
import com.library.library_management.exception.ResourceNotFoundException;
import com.library.library_management.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Page<Category> search(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return categoryRepository.findAll(pageable);
        }
        return categoryRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

    public List<Category> findActiveCategories() {
        return categoryRepository.findByActiveTrueOrderByNameAsc();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại"));
    }

    @Transactional
    public Category save(CategoryForm form) {
        Long id = form.getId() == null ? 0L : form.getId();
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(form.getName().trim(), id)) {
            throw new BusinessException("Tên thể loại đã tồn tại");
        }

        Category category = form.getId() == null ? new Category() : findById(form.getId());
        category.setName(form.getName().trim());
        category.setDescription(form.getDescription());
        category.setActive(form.getActive() == null || form.getActive());
        return categoryRepository.save(category);
    }

    @Transactional
    public void softDelete(Long id) {
        Category category = findById(id);
        category.setActive(false);
    }

    public CategoryForm toForm(Category category) {
        CategoryForm form = new CategoryForm();
        form.setId(category.getId());
        form.setName(category.getName());
        form.setDescription(category.getDescription());
        form.setActive(category.getActive());
        return form;
    }
}
