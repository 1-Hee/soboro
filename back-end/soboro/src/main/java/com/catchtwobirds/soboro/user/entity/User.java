package com.catchtwobirds.soboro.user.entity;

import com.catchtwobirds.soboro.auth.entity.ProviderType;
import com.catchtwobirds.soboro.auth.entity.RoleType;
import com.catchtwobirds.soboro.consulting.entity.Consulting;
import com.catchtwobirds.soboro.user.dto.UserResponseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @JsonIgnore
    @Id
    @Column(name = "user_no")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userNo;
    @Column(name = "user_id", length = 64, unique = true)
    @Size(max = 64)
    private String userId;
    @JsonIgnore
    @Column(name = "user_password", length = 128)
    @Size(max = 128)
    private String userPassword;
    @Column(name = "user_name", length = 100)
    @Size(max = 100)
    private String userName;
    @Column(name = "user_email", length = 512, unique = true)
    @Size(max = 512)
    private String userEmail;
    @Column(name = "user_phone", length = 50, unique = true)
    @Size(max = 50)
    private String userPhone;
    @Column(name = "user_gender", length = 1)
    @Size(max = 1)
    private String userGender;
    @Column(name = "user_terms")
    private boolean userTerms;
    @Column(name = "user_provide_type", length = 20)
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;
    @Column(name = "user_role_type", length = 20)
    @Enumerated(EnumType.STRING)
    private RoleType roleType;
    @Column(name = "user_created_time")
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(name = "user_active")
    private boolean userActive;
}
