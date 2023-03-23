package com.catchtwobirds.soboro.user.dto;

import com.catchtwobirds.soboro.user.entity.User;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private String userId;
    private String userPassword;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userBirthDate;
    private String userGender;
    private boolean userTerms;
    @Builder
    public UserResponseDto(User user) {
        this.userId = user.getUserId();
        this.userPassword = user.getUserPassword();
        this.userName = user.getUserName();
        this.userEmail = user.getUserEmail();
        this.userPhone = user.getUserPhone();
        this.userBirthDate = user.getUserBirthDate();
        this.userGender = user.getUserGender();
        this.userTerms = user.isUserTerms();
    }
}
