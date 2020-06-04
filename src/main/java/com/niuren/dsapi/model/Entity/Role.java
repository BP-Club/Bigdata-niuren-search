package com.niuren.dsapi.model.Entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


/**
 * @author: DoubleLuck.Li
 * @description:
 * @create: 2018-07-26 14:26
 **/
@Getter
@Setter
@ToString
public class Role {

    @JsonValue
    private String name;
    private List<Permission> permissions;

}
