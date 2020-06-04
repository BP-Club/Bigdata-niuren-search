package com.niuren.dsapi.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niuren.dsapi.model.dto.OAuthAccessToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * @author: dailinwei
 * @description:
 * @create: 2018-06-29 15:21
 **/

@Service
public class OAuthTokenResponseHandler implements ResponseHandler<OAuthAccessToken> {
    private ObjectMapper objectMapper;

    public OAuthTokenResponseHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public OAuthAccessToken handleResponse(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        String content = EntityUtils.toString(entity, "UTF-8");
        EntityUtils.consume(entity);
        Map<String, Object> map = objectMapper.readValue(content, Map.class);
        OAuthAccessToken token = new OAuthAccessToken();
        token.setAccessToken(map.get("access_token").toString());
        token.setTokenType(map.get("token_type").toString());
        token.setExpiresIn(Integer.parseInt(map.get("expires_in").toString()));
        token.setRefreshToken(map.get("refresh_token").toString());
        token.setScope(map.get("scope").toString());
        return token;
    }

}
