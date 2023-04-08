package com.catchtwobirds.soboro.auth.entity;

import com.catchtwobirds.soboro.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * 이 코드는 Spring Security를 사용해 인증한 사용자 정보를 표현하는 클래스입니다. <br>
 * UserPrincipal 클래스는 OAuth2User, UserDetails, OidcUser 인터페이스를 구현합니다. <br>
 * OAuth2User와 OidcUser는 OAuth2 및 OpenID Connect 프로토콜에서 인증된 사용자 정보를 제공하는 인터페이스이며,
 * UserDetails는 Spring Security에서 인증된 사용자 정보를 제공하는 인터페이스입니다. <br>
 * UserPrincipal 클래스의 필드로는 사용자 ID, 패스워드, 사용자가 인증한 인증 서비스 유형, 사용자의 권한 정보, 그리고 인증된 사용자의 속성을 나타내는 Map<String, Object> 가 있습니다.
 * 또한 UserPrincipal 클래스는 두 가지 create 메서드를 정의합니다. <br>
 * 첫 번째 메서드는 User 객체를 이용해 UserPrincipal 객체를 생성하며,
 * 두 번째 메서드는 User 객체와 인증된 사용자의 속성 정보를 이용해 UserPrincipal 객체를 생성합니다.
 * UserPrincipal 클래스는 인증된 사용자의 정보를 담고 있기 때문에,
 * 사용자 정보를 제공하는 메서드인 getAttributes(), getAuthorities(), getName(), getUsername(),
 * isAccountNonExpired(), isAccountNonLocked(), isCredentialsNonExpired(), isEnabled(),
 * getClaims(), getUserInfo(), getIdToken() 메서드를 구현하고 있습니다.
 */

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserPrincipal implements OAuth2User, UserDetails, OidcUser {
    private final String userId;
    private final String password;
    private final ProviderType providerType;
    private final RoleType roleType;
    private final Collection<GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return userId;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getClaims() {
        return null;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return null;
    }

    @Override
    public OidcIdToken getIdToken() {
        return null;
    }

    public static UserPrincipal create(User user) {
        return new UserPrincipal(
                user.getUserId(),
                user.getUserPassword(),
                user.getProviderType(),
                RoleType.USER,
                Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getCode()))
        );
    }

    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = create(user);
        userPrincipal.setAttributes(attributes);

        return userPrincipal;
    }
}

