package com.niuren.dsapi.model.Entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author: DoubleLuck.Li
 * @description:
 * @create: 2018-07-26 15:08
 **/
@Getter
@Setter
@ToString
public class Permission {

    @JsonValue
    private String name;

}
