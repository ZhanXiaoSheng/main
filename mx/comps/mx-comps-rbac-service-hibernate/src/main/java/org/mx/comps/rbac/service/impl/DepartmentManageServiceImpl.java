package org.mx.comps.rbac.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Department;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.comps.rbac.service.DepartmentManageService;
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
     * @see DepartmentManageService#saveDepartment(Department)
     */
    @Transactional
    @Override
    public Department saveDepartment(Department department) {
        if (department == null) {
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        String id = department.getId();
        if (!StringUtils.isBlank(id)) {
            Department checked = super.getById(id, Department.class);
            if (checked == null) {
                throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.DEPARTMENT_NOT_FOUND);
            }
            checked.setDesc(department.getDesc());
            checked.setName(department.getName());
            checked.setManager(department.getManager());
            checked.setEmployees(department.getEmployees());
            checked.setValid(department.isValid());
            department = super.save(checked, false);
        } else {
            department = super.save(department, false);
        }
        if (operateLogService != null) {
            operateLogService.writeLog(String.format("保存部门[code=%s, name=%s]信息成功。",
                    department.getCode(), department.getName()));
        }
        return department;
    }
}
