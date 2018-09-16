package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.BaseEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 描述： 家庭信息实体类，基于Hibernate实现。
 *
 * @author John.Peng
 * Date time 2018/2/18 上午10:49
 */
@Entity
@Table(name = "TB_FAMILY")
public class FamilyEntity extends BaseEntity implements Family {
    @Column(name = "NAME", unique = true)
    private String name;
    @Column(name = "DESCRIPTION")
    private String desc;
    @Column(name = "AVATAR_URL")
    private String avatarUrl;
    @ManyToOne(targetEntity = FfeeAccount.class)
    @OneToMany(targetEntity = FamilyMemberEntity.class, cascade = {CascadeType.REFRESH},
            mappedBy = "family", fetch = FetchType.EAGER)
    private Set<FamilyMember> members;

    /**
     * 默认的构造函数
     */
    public FamilyEntity() {
        super();
        members = new HashSet<>();
    }

    /**
     * {@inheritDoc}
     *
     * @see Family#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     *
     * @see Family#setName(String)
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public void setDescription(String desc) {
        this.desc = desc;
    }

    @Override
    public String getAvatarUrl() {
        return avatarUrl;
    }

    @Override
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /**
     * {@inheritDoc}
     *
     * @see Family#getMembers()
     */
    public Set<FamilyMember> getMembers() {
        return members;
    }

    /**
     * {@inheritDoc}
     *
     * @see Family#setMembers(Set)
     */
    public void setMembers(Set<FamilyMember> members) {
        this.members = members;
    }
}
