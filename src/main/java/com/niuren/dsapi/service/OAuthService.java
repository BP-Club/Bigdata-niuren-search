package com.niuren.dsapi.service;

import com.niuren.dsapi.exception.OAuthException;
import com.niuren.dsapi.model.Entity.User;
import com.niuren.dsapi.model.dto.OAuthAccessToken;


public interface OAuthService {

    OAuthAccessToken getAccessTokenByCode(String code) throws OAuthException;

    User getUser(String accessToken) throws OAuthException;

    User getUserById(String id);

    String getOAuthUrl();

}
