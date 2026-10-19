package com.splitledger.app.repository;

import com.splitledger.app.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findByActiveTrue();
}