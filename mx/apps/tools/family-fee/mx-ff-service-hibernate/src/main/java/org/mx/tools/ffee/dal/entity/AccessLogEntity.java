package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.BaseEntity;

import javax.persistence.*;

/**
 * 描述： 账户操作日志实体类定义，基于Hibernate JPA实现
 *
 * @author john peng
 * Date time 2018/9/15 下午9:31
 */
@Entity
@Table(name = "TB_ACCESS_LOG")
public class AccessLogEntity extends BaseEntity implements AccessLog {
    @ManyToOne(targetEntity = FfeeAccountEntity.class)
    @JoinColumn(name = "ACCOUNT_ID")
    private FfeeAccount account;
    @Column(name = "CONTENT", length = 500)
    private String content;
    @Column(name = "LATITUDE")
    private double latitude;
    @Column(name = "LONGITUDE")
    private double longitude;

    @Override
    public FfeeAccount getAccount() {
        return account;
    }

    @Override
    public void setAccount(FfeeAccount account) {
        this.account = account;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
