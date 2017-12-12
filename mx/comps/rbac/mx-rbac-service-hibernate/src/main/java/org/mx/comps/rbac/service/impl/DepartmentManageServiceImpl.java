package org.mx.comps.rbac.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Department;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.comps.rbac.service.DepartmentManageService;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.OperateLogService;
import org.mx.dal.service.impl.GeneralDictEntityAccessorImpl;
import org.mx.error.UserInterfaceSystemErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 基于Hibernate JPA实现的部门管理服务实现类。
 *
 * @author : john.peng created on date : 2017/11/19
 */
@Component("departmentManageService")
public class DepartmentManageServiceImpl extends GeneralDictEntityAccessorImpl implements DepartmentManageService {
    private static final Log logger = LogFactory.getLog(DepartmentManageServiceImpl.class);

    @Autowired
    private OperateLogService operateLogService = null;

    /**
     * {@inheritDoc}
     *
     * @see DepartmentManageService#saveDepartment(DepartInfo)
     */
    @Transactional
    @Override
    public Department saveDepartment(DepartInfo departInfo) {
        if (departInfo == null) {
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        String id = departInfo.getDepartId();
        Department department;
        if (!StringUtils.isBlank(id)) {
            department = super.getById(id, Department.class);
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
            User manager = super.getById(departInfo.getManagerId(), User.class);
            if (manager == null) {
                throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.USER_NOT_FOUND);
            }
            department.setManager(manager);
        }
        if (department.getEmployees() != null && !department.getEmployees().isEmpty()) {
            department.getEmployees().clear();
        }
        for (String employeeId : departInfo.getEmployeeIds()) {
            User employee = super.getById(employeeId, User.class);
            if (employee == null) {
                throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.USER_NOT_FOUND);
            }
            department.getEmployees().add(employee);
        }
        department.setValid(departInfo.isValid());
        department = super.save(department, false);
        if (operateLogService != null) {
            operateLogService.writeLog(String.format("保存部门[code=%s, name=%s]信息成功。",
                    departInfo.getCode(), departInfo.getName()));
        }
        return department;
    }
}