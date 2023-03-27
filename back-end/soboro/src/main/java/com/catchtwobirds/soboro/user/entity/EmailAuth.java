package com.catchtwobirds.soboro.user.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name = "emailauth")
public class EmailAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emailauth_no")
    private Integer no;
    @Email
    @Column(name = "emailauth_email")
    private String email;
    @Valid
    @Column(name = "emailauth_code")
    private String code;

    @Builder
    public EmailAuth(String email, String code) {
        this.email = email;
        this.code = code;
    }
}
