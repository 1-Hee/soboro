package com.catchtwobirds.soboro.auth.info;

import com.catchtwobirds.soboro.auth.entity.ProviderType;
import com.catchtwobirds.soboro.auth.info.impl.FacebookOAuth2UserInfo;
import com.catchtwobirds.soboro.auth.info.impl.GoogleOAuth2UserInfo;
import com.catchtwobirds.soboro.auth.info.impl.KakaoOAuth2UserInfo;
import com.catchtwobirds.soboro.auth.info.impl.NaverOAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(ProviderType providerType, Map<String, Object> attributes) {
        switch (providerType) {
            case GOOGLE: return new GoogleOAuth2UserInfo(attributes);
            case FACEBOOK: return new FacebookOAuth2UserInfo(attributes);
            case NAVER: return new NaverOAuth2UserInfo(attributes);
            case KAKAO: return new KakaoOAuth2UserInfo(attributes);
            default: throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}