package com.library.library_management.repository;

import com.library.library_management.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    Optional<Category> findByNameIgnoreCase(String name);

    List<Category> findByActiveTrueOrderByNameAsc();

    Page<Category> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}
