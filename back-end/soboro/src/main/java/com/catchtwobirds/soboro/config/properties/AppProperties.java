package com.catchtwobirds.soboro.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 위 코드는 Spring Boot 프레임워크에서 사용되는 @ConfigurationProperties 애노테이션을 사용하여 app prefix로 시작하는 설정 정보를 읽어들입니다. <br>
 * Auth와 OAuth2 클래스는 AppProperties 클래스 내부에 중첩 클래스로 선언되어 있습니다. <br>
 * Auth 클래스에는 tokenSecret, tokenExpiry, refreshTokenExpiry 세 개의 필드가 있습니다. <br>
 * OAuth2 클래스에는 authorizedRedirectUris 필드가 있습니다.
 * authorizedRedirectUris 필드는 OAuth2 인스턴스의 생성자에서 초기화되며, authorizedRedirectUris를 설정할 때 사용하는 메소드도 포함되어 있습니다. <br>
 * 따라서, application.yml과 같은 설정 파일에 app prefix로 시작하는 설정 정보가 있고, 이러한 설정 정보를 AppProperties 클래스의 객체로 바인딩하여 사용하고자 할 때, @ConfigurationProperties 애노테이션을 사용하여 해당 클래스를 설정 클래스로 사용합니다.
 */

@Getter
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Auth auth = new Auth();
    private final OAuth2 oauth2 = new OAuth2();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Auth {
        private String tokenSecret;
        private long tokenExpiry;
        private long refreshTokenExpiry;
    }

    public static final class OAuth2 {
        private List<String> authorizedRedirectUris = new ArrayList<>();

        public List<String> getAuthorizedRedirectUris() {
            return authorizedRedirectUris;
        }

        public OAuth2 authorizedRedirectUris(List<String> authorizedRedirectUris) {
            this.authorizedRedirectUris = authorizedRedirectUris;
            return this;
        }
    }
}
