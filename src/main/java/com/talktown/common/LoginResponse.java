package com.talktown.common;

import jakarta.servlet.http.Cookie;

public class LoginResponse {
    public LoginResponse(String accessToken, String refreshToken, String username) {
        AccessToken = accessToken;
        RefreshToken = refreshToken;
        Username = username;
    }

    public LoginResponse(Cookie accessTokenCookie, Cookie refreshTokenCookie, Cookie usernameCookie) {
    }

    public String getAccessToken() {
        return AccessToken;
    }

    public void setAccessToken(String accessToken) {
        AccessToken = accessToken;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getRefreshToken() {
        return RefreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        RefreshToken = refreshToken;
    }

    private String AccessToken;
    private String RefreshToken;
    private String Username;
}
