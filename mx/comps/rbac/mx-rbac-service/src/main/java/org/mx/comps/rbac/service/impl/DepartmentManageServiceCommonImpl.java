package org.mx.comps.rbac.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Department;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.comps.rbac.service.DepartmentManageService;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.error.UserInterfaceSystemErrorException;

/**
 * 部门管理服务公共实现类。
 *
 * @author : john.peng created on date : 2017/11/19
 */
public abstract class DepartmentManageServiceCommonImpl implements DepartmentManageService {
    private static final Log logger = LogFactory.getLog(DepartmentManageServiceCommonImpl.class);

    protected GeneralDictAccessor accessor = null;

    /**
     * 保存部门实体对象
     *
     * @param department 部门实体对象
     * @return 保存后的部门实体对象
     */
    protected abstract Department save(Department department);

    /**
     * {@inheritDoc}
     *
     * @see DepartmentManageService#saveDepartment(DepartInfo)
     */
    @Override
    public Department saveDepartment(DepartInfo departInfo) {
        if (departInfo == null) {
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        String id = departInfo.getId();
        Department department;
        if (!StringUtils.isBlank(id)) {
            department = accessor.getById(id, Department.class);
            if (department == null) {
                throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.DEPARTMENT_NOT_FOUND);
            }
        } else {
            department = EntityFactory.createEntity(Department.class);
        }
        department.setCode(departInfo.getCode());
        department.setName(departInfo.getName());
        department.setDesc(departInfo.getDesc());
        if (StringUtils.isBlank(departInfo.getManagerId())) {
            department.setManager(null);
        } else {
            User manager = accessor.getById(departInfo.getManagerId(), User.class);
            if (manager == null) {
                throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.USER_NOT_FOUND);
            }
            department.setManager(manager);
        }
        if (department.getEmployees() != null && !department.getEmployees().isEmpty()) {
            department.getEmployees().clear();
        }
        if (departInfo.getEmployeeIds() != null) {
            for (String employeeId : departInfo.getEmployeeIds()) {
                User employee = accessor.getById(employeeId, User.class);
                if (employee == null) {
                    throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.USER_NOT_FOUND);
                }
                department.getEmployees().add(employee);
            }
        }
        department.setValid(departInfo.isValid());
        department = this.save(department);
        return department;
    }
}
