package com.catchtwobirds.soboro.auth.exception;

/**
 * 이 코드는 OAuthProviderMissMatchException 이라는 커스텀 예외를 정의하는 클래스입니다.
 * RuntimeException 클래스를 확장하여 예외를 발생시키는 데 사용됩니다.
 *
 * 이 예외는 OAuth 인증 서버에서 사용되는 공급자(provider)가 잘못된 경우에 발생할 수 있습니다.
 * 예를 들어, 사용자가 Google로 로그인을 시도했는데 OAuth 인증 서버가 Facebook을 공급자로 사용하고 있을 때 발생할 수 있습니다.
 *
 * OAuthProviderMissMatchException 클래스는 생성자에서 예외 메시지를 인자로 받습니다.
 * 이 메시지는 예외가 발생했을 때 출력되는 메시지입니다.
 * 예외 메시지는 getMessage() 메소드를 통해 얻을 수 있습니다.
 */

public class OAuthProviderMissMatchException extends RuntimeException {

    public OAuthProviderMissMatchException(String message) {
        super(message);
    }
}
