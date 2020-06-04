package com.niuren.dsapi.service.impl;

import com.niuren.dsapi.config.OAuthConfig;
import com.niuren.dsapi.exception.OAuthException;
import com.niuren.dsapi.exception.ServiceException;
import com.niuren.dsapi.model.Entity.User;
import com.niuren.dsapi.model.dto.OAuthAccessToken;
import com.niuren.dsapi.service.OAuthService;
import com.niuren.dsapi.util.HttpClientUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: dailinwei
 * @description: OAuth 相关服务
 * @create: 2018-05-02 11:02
 **/
@Service
public class OAuthServiceImpl implements OAuthService {

    @Resource
    private OAuthConfig oAuthConfig;
    @Resource
    private OAuthTokenResponseHandler oAuthTokenResponseHandler;
    @Resource
    private OAuthUserResponseHandler oAuthUserResponseHandler;
    @Resource
    private OAuthUserListResponseHandler oAuthUserListResponseHandler;
    private Map<String, String> defaultGetHeaderMap;

    private ConcurrentMap<String, User> userMap;

    @PostConstruct
    public void init() {
        defaultGetHeaderMap = new HashMap<String, String>() {{
            put("Accept", "application/json");
        }};
        this.userMap = new ConcurrentHashMap<>();
    }

    @Override
    public OAuthAccessToken getAccessTokenByCode(String code) throws OAuthException {
        String url = oAuthConfig.getTokenUrl(code);
        try {
            return HttpClientUtil.post(url, defaultGetHeaderMap, null, oAuthTokenResponseHandler);
        } catch (IOException e) {

            throw new OAuthException(e.getMessage());
        }
    }

    @Override
    public User getUser(String accessToken) throws OAuthException {
        String url = oAuthConfig.getUserUrl();
        Map<String, String> headerMap = new HashMap<String, String>() {{
            put("Authorization", "Bearer " + accessToken);
        }};
        User user;
        try {
            user = HttpClientUtil.get(url, headerMap, null, oAuthUserResponseHandler);
            user.setAccessToken(accessToken);
        } catch (IOException e) {

            throw new OAuthException(e.getMessage());
        }
        if (CollectionUtils.isEmpty(userMap)) {
            url = oAuthConfig.getUserUrl() + "/list";
            try {
                List<User> userList = HttpClientUtil.get(url, headerMap, null, oAuthUserListResponseHandler);
                for (User u : userList) {
                    userMap.put(String.valueOf(u.getId()), u);
                }
            } catch (IOException e) {

            }
        }
        return user;
    }

    // FIXME header authorization
    @Override
    public User getUserById(String id) {
        User user = userMap.get(id);
        if (user == null) {
            String url = oAuthConfig.getUserUrl() + "/id/" + id;
            try {
                user = HttpClientUtil.get(url, null, null, oAuthUserResponseHandler);
                userMap.put(String.valueOf(user.getId()), user);
            } catch (Exception e) {

                throw new ServiceException(e.getMessage());
            }
        }
        return user;
    }

    @Override
    public String getOAuthUrl() {
        return oAuthConfig.getAuthorizeUrl();
    }

}
