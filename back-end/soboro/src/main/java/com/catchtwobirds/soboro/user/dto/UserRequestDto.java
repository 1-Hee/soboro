package com.catchtwobirds.soboro.user.dto;

import com.catchtwobirds.soboro.auth.entity.ProviderType;
import com.catchtwobirds.soboro.auth.entity.RoleType;
import com.catchtwobirds.soboro.user.entity.User;
import lombok.*;

@Builder
@Getter @Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    private String userId;
    private String userPassword;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userBirthDate;
    private String userGender;
    private boolean userTerms;

    public User toEntity() {
        return User.builder()
                .userId(userId)
                .userPassword(userPassword)
                .userName(userName)
                .userEmail(userEmail)
                .userPhone(userPhone)
                .userGender(userGender)
                .userBirthDate(userBirthDate)
                .userTerms(userTerms)
                .roleType(RoleType.USER)
                .providerType(ProviderType.LOCAL)
                .build();
    }
}
