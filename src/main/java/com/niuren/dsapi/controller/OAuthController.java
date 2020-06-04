package com.niuren.dsapi.controller;

import com.niuren.dsapi.model.Entity.User;
import com.niuren.dsapi.model.dto.OAuthAccessToken;
import com.niuren.dsapi.service.OAuthService;
import com.niuren.dsapi.service.impl.DsapiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: dailinwei
 * @description: OAuth认证
 * @create: 2018-05-02 10:53
 **/

@RestController
@RequestMapping("/api/v1.0/oauth")
public class OAuthController {

    @Resource
    private OAuthService oAuthService;

    @GetMapping("/login/{code}")
    public Object login(@PathVariable("code") String code, HttpSession session) throws Exception {
        OAuthAccessToken accessToken = oAuthService.getAccessTokenByCode(code);
        User user = oAuthService.getUser(accessToken.getAccessToken());
        User oldUser = null;
        if (session.getAttribute("user") != null) {
            oldUser = (User)session.getAttribute("user");
            session.removeAttribute("user");
        }
        session.setAttribute("user", user);
         String sessionId = session.getId();

        Map<String, Object> data = new HashMap<String, Object>(){{
            put("user", user);
            put("session_id", sessionId);
            put("access_token", accessToken.getAccessToken());
            put("refresh_token", accessToken.getRefreshToken());
        }};
        return new DsapiResult(data);
    }

    @GetMapping("/validate")
    public Object validate(HttpSession session) {
        User user = null;
        if (session.getAttribute("user") != null) {
            user = (User) session.getAttribute("user");
        }
        if (user != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            return new DsapiResult(data);
        }
        return DsapiResult.FALSE_NOT_LOGIN;
    }

    @PostMapping("/logout")
    public Object logout(HttpSession session) {
        session.removeAttribute("user");
        return DsapiResult.TRUE_REST_API_RESULT;
    }

    @GetMapping("/oauthUrl")
    public Object getOAuthUrl() {
        String url = oAuthService.getOAuthUrl();
        return new DsapiResult(url);
    }


}