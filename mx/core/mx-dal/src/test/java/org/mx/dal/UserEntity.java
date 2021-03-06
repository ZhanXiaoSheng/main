package org.mx.dal;

public class UserEntity extends BaseEntity implements User {
    private String code = null;

    public UserEntity() {
        super();
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }
}
