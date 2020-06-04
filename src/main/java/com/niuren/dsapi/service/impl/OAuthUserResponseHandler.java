package com.niuren.dsapi.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niuren.dsapi.model.Entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author: dailinwei
 * @description:
 * @create: 2018-06-29 15:21
 **/

@Service
public class OAuthUserResponseHandler implements ResponseHandler<User> {
    private static final Logger log = LoggerFactory.getLogger(OAuthUserResponseHandler.class);
    private ObjectMapper objectMapper;

    public OAuthUserResponseHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    @Override
    public User handleResponse(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        String content = EntityUtils.toString(entity, "UTF-8");
        log.debug("Response content is : {}", content);
        EntityUtils.consume(entity);
        return objectMapper.readValue(content, User.class);
    }
}
