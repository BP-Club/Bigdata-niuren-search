package com.niuren.dsapi.model.Entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

public enum RoleEnum {

    ADMIN ("admin"), USER ("user"), VISITOR ("visitor");

    @Getter
    @Setter
    @JsonValue
    private String name;

    RoleEnum(String name) {
        this.name = name;
    }

}
