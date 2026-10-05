package com.library.library_management.service;

import com.library.library_management.dto.MemberForm;
import com.library.library_management.entity.Member;
import com.library.library_management.entity.MemberStatus;
import com.library.library_management.exception.BusinessException;
import com.library.library_management.exception.ResourceNotFoundException;
import com.library.library_management.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Page<Member> search(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return memberRepository.findAll(pageable);
        }
        return memberRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable);
    }

    public List<Member> findActiveMembers() {
        return memberRepository.findByStatusOrderByFullNameAsc(MemberStatus.ACTIVE);
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thành viên"));
    }

    @Transactional
    public Member save(MemberForm form) {
        Long id = form.getId() == null ? 0L : form.getId();
        if (memberRepository.existsByEmailIgnoreCaseAndIdNot(form.getEmail().trim(), id)) {
            throw new BusinessException("Email thành viên đã tồn tại");
        }

        Member member = form.getId() == null ? new Member() : findById(form.getId());
        member.setFullName(form.getFullName().trim());
        member.setEmail(form.getEmail().trim());
        member.setPhone(form.getPhone());
        member.setStatus(form.getStatus() == null ? MemberStatus.ACTIVE : form.getStatus());
        return memberRepository.save(member);
    }

    @Transactional
    public void changeStatus(Long id, MemberStatus status) {
        Member member = findById(id);
        member.setStatus(status);
    }

    public MemberForm toForm(Member member) {
        MemberForm form = new MemberForm();
        form.setId(member.getId());
        form.setFullName(member.getFullName());
        form.setEmail(member.getEmail());
        form.setPhone(member.getPhone());
        form.setStatus(member.getStatus());
        return form;
    }
}
