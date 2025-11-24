package com.epam.travel_agency.repositories;

import com.epam.travel_agency.entity.UserRole;
import com.epam.travel_agency.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRolesRepository extends JpaRepository<UserRole, UserRoleId> {
}
