package com.library.library_management.repository;

import com.library.library_management.entity.Book;
import com.library.library_management.entity.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbnIgnoreCaseAndIdNot(String isbn, Long id);

    List<Book> findByStatusOrderByTitleAsc(BookStatus status);

    @Query("""
            select b from Book b
            join b.category c
            where (:keyword is null or :keyword = ''
                or lower(b.title) like lower(concat('%', :keyword, '%'))
                or lower(b.author) like lower(concat('%', :keyword, '%'))
                or lower(b.isbn) like lower(concat('%', :keyword, '%'))
                or lower(c.name) like lower(concat('%', :keyword, '%')))
            """)
    Page<Book> search(@Param("keyword") String keyword, Pageable pageable);
}
