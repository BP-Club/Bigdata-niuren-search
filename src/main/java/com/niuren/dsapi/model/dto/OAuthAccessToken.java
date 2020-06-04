package com.niuren.dsapi.model.dto;

import lombok.Builder;

/**
 * @author: dailinwei
 * @description: ouath access_token
 * @create: 2018-05-02 11:10
 **/
public class OAuthAccessToken {

    private String accessToken;

    private String tokenType;

    private String refreshToken;

    private Integer expiresIn;

    private String scope;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("OAuthAccessToken{");
        sb.append("accessToken='").append(accessToken).append('\'');
        sb.append(", tokenType='").append(tokenType).append('\'');
        sb.append(", refreshToken='").append(refreshToken).append('\'');
        sb.append(", expiresIn=").append(expiresIn);
        sb.append(", scope='").append(scope).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
