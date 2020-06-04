package com.niuren.dsapi.model.Entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

public enum PermissionEnum {

    ADMIN ("admin"), WRITE ("write"), READ ("read");

    @Getter
    @Setter
    @JsonValue
    private String name;

    PermissionEnum(String name) {
        this.name = name;
    }

}
