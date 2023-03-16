package com.catchtwobirds.soboro.entity;

import com.catchtwobirds.soboro.consulting.entity.Consulting;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no")
    private Long id;

    private String userId;
    private String userPassword;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userBirthdate;
    private String userGender;
    private boolean userTerms;

    @OneToMany(mappedBy = "user")
    private List<Consulting> consulting = new ArrayList<>();

}
