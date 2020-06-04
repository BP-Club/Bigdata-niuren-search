package com.niuren.dsapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author: dailinwei
 * @description: OAuth 相关配置
 * @create: 2018-05-02 17:31
 **/
@Component
public class OAuthConfig {

    @Value("${oauth.url.redirect}")
    private String prefixRedirectUrl;


    @Value("${oauth.server.innerHost}")
    private String oauthServerInnerHost;


    @Value("${oauth.server.outerHost}")
    private String oauthServerOuterHost;


    @Value("${oauth.server.innerPort}")
    private Integer oauthServerInnerPort;

    @Value("${oauth.server.outerPort}")
    private Integer oauthServerOuterPort;


    @Value("${oauth.path.token}")
    private String tokenPath;


    @Value("${oauth.path.authorize}")
    private String authorizePath;


    @Value("${oauth.path.user}")
    private String userPath;


    @Value("${oauth.path.redirect}")
    private String redirectPath;


    @Value("${oauth.client_id}")
    private String clientId;


    @Value("${oauth.client_secret}")
    private String clientSecret;


    @Value("${oauth.grant_type}")
    private String grantType;


    @Value("${oauth.response_type}")
    private String responseType;


    @Value("${oauth.scope}")
    private String scope;


    private String userUrl;

    private String rawTokenUrl;

    private String authorizeUrl;

    @Value("${oauth.url.redirect_uri}")
    private String redirectUrl;

    public String getPrefixRedirectUrl() {
        return prefixRedirectUrl;
    }

    public void setPrefixRedirectUrl(String prefixRedirectUrl) {
        this.prefixRedirectUrl = prefixRedirectUrl;
    }

    public String getOauthServerInnerHost() {
        return oauthServerInnerHost;
    }

    public void setOauthServerInnerHost(String oauthServerInnerHost) {
        this.oauthServerInnerHost = oauthServerInnerHost;
    }

    public String getOauthServerOuterHost() {
        return oauthServerOuterHost;
    }

    public void setOauthServerOuterHost(String oauthServerOuterHost) {
        this.oauthServerOuterHost = oauthServerOuterHost;
    }

    public Integer getOauthServerInnerPort() {
        return oauthServerInnerPort;
    }

    public void setOauthServerInnerPort(Integer oauthServerInnerPort) {
        this.oauthServerInnerPort = oauthServerInnerPort;
    }

    public Integer getOauthServerOuterPort() {
        return oauthServerOuterPort;
    }

    public void setOauthServerOuterPort(Integer oauthServerOuterPort) {
        this.oauthServerOuterPort = oauthServerOuterPort;
    }

    public String getTokenPath() {
        return tokenPath;
    }

    public void setTokenPath(String tokenPath) {
        this.tokenPath = tokenPath;
    }

    public String getAuthorizePath() {
        return authorizePath;
    }

    public void setAuthorizePath(String authorizePath) {
        this.authorizePath = authorizePath;
    }

    public String getUserPath() {
        return userPath;
    }

    public void setUserPath(String userPath) {
        this.userPath = userPath;
    }

    public String getRedirectPath() {
        return redirectPath;
    }

    public void setRedirectPath(String redirectPath) {
        this.redirectPath = redirectPath;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public String getRawTokenUrl() {
        return rawTokenUrl;
    }

    public void setRawTokenUrl(String rawTokenUrl) {
        this.rawTokenUrl = rawTokenUrl;
    }

    public String getAuthorizeUrl() {
        return authorizeUrl;
    }

    public void setAuthorizeUrl(String authorizeUrl) {
        this.authorizeUrl = authorizeUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    @PostConstruct
    public void init() throws UnsupportedEncodingException {
        //redirectUrl = prefixRedirectUrl + redirectPath;

        authorizeUrl = "http://" + oauthServerOuterHost + (oauthServerOuterPort == 80 ? "" : ":" + oauthServerOuterPort) + authorizePath +
                "?response_type=" + responseType +
                "&scope=" + scope +
                "&client_id=" + clientId +
                "&redirect_uri=" + URLEncoder.encode(redirectUrl, "utf-8");

        rawTokenUrl = "http://" + oauthServerInnerHost + ":" + oauthServerInnerPort + tokenPath +
                "?client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&grant_type=" + grantType +
//                .append("&code=").append(code)
                "&redirect_uri=" + URLEncoder.encode(redirectUrl, "utf-8");

        userUrl = "http://" + oauthServerInnerHost + ":" + oauthServerInnerPort + userPath;
    }

    public String getTokenUrl(String code) {
        return rawTokenUrl + "&code=" + code;
    }

}
