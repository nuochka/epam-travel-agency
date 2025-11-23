package com.epam.travel_agency.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_roles")
@IdClass(UserRoleId.class)
public class UserRole {
    @Id
    private Long userId;

    @Id
    private String role;
}

