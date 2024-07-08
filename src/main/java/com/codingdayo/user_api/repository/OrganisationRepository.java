package com.codingdayo.user_api.repository;

import com.codingdayo.user_api.model.Organisation;
import com.codingdayo.user_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganisationRepository extends JpaRepository<Organisation, Long> {

    List<Organisation> findByOrgId(Long orgId);

    @Query("SELECT o FROM Organisation o WHERE o.creator.id = :userId OR :user MEMBER OF o.users")
    List<Organisation> findByUser(@Param("userId") Long userId, @Param("user") User user);
}
