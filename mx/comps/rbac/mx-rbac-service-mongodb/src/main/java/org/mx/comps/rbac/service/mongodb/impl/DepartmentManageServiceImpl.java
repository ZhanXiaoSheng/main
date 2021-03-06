package org.mx.comps.rbac.service.mongodb.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Department;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.service.DepartmentManageService;
import org.mx.comps.rbac.service.impl.DepartmentManageServiceCommonImpl;
import org.mx.dal.service.GeneralDictAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 基于Hibernate JPA实现的部门管理服务实现类。
 *
 * @author : john.peng created on date : 2017/11/19
 */
@Component("departmentManageServiceMongodb")
public class DepartmentManageServiceImpl extends DepartmentManageServiceCommonImpl {
    private static final Log logger = LogFactory.getLog(DepartmentManageServiceCommonImpl.class);

    private GeneralDictAccessor accessor;

    /**
     * 默认的构造函数
     *
     * @param accessor 字典数据实体访问器
     */
    @Autowired
    public DepartmentManageServiceImpl(@Qualifier("generalDictAccessor") GeneralDictAccessor accessor) {
        super();
        this.accessor = accessor;
    }

    /**
     * {@inheritDoc}
     *
     * @see DepartmentManageServiceCommonImpl#save(Department)
     */
    @Override
    protected Department save(Department department) {
        Set<User> oldEmployees = new HashSet<>();
        if (!StringUtils.isBlank(department.getId())) {
            oldEmployees.addAll(accessor.getById(department.getId(), Department.class).getEmployees());
        }
        department = accessor.save(department);
        Set<User> employees = department.getEmployees();
        for (User user : employees) {
            if (oldEmployees.contains(user)) {
                oldEmployees.remove(user);
            } else {
                user.setDepartment(department);
                accessor.save(user);
            }
        }
        for (User user : oldEmployees) {
            if (department.equals(user.getDepartment())) {
                // 如果用户原来的部门是当前部门，则应该将其部门清空。
                user.setDepartment(null);
                accessor.save(user);
            }
        }
        return department;
    }

    /**
     * {@inheritDoc}
     *
     * @see DepartmentManageService#saveDepartment(DepartInfo)
     */
    @Override
    public Department saveDepartment(DepartInfo departInfo) {
        super.accessor = accessor;
        return super.saveDepartment(departInfo);
    }
}
