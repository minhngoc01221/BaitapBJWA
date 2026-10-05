package com.library.library_management.repository;

import com.library.library_management.entity.Member;
import com.library.library_management.entity.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    List<Member> findByStatusOrderByFullNameAsc(MemberStatus status);

    Page<Member> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String fullName, String email, Pageable pageable);
}
