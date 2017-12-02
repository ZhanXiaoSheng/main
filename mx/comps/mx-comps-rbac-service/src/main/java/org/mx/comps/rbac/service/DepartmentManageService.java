package org.mx.comps.rbac.service;

import org.mx.comps.rbac.dal.entity.Department;
import org.mx.dal.service.GeneralDictAccessor;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门管理的服务接口定义
 *
 * @author : john.peng created on date : 2017/11/19
 */
public interface DepartmentManageService extends GeneralDictAccessor {
    /**
     * 保存部门信息
     *
     * @param departInfo 部门信息对象
     * @return 保存后的部门实体
     */
    Department saveDepartment(DepartInfo departInfo);

    class DepartInfo {
        private String code, name, desc, departId, managerId;
        private boolean valid = true;
        private List<String> employeeIds;

        public static final DepartInfo valueOf(String code, String name, String desc) {
            return new DepartInfo(code, name, desc);
        }

        public static final DepartInfo valueOf(String code, String name, String desc, String departId, String managerId,
                                               List<String> employeeIds, boolean valid) {
            return new DepartInfo(code, name, desc, departId, managerId, employeeIds, valid);
        }

        private DepartInfo() {
            super();
            this.employeeIds = new ArrayList<>();
        }

        private DepartInfo(String code, String name, String desc) {
            this();
            this.code = code;
            this.name = name;
            this.desc = desc;
        }

        private DepartInfo(String code, String name, String desc, String departId, String managerId,
                           List<String> employeeIds, boolean valid) {
            this(code, name, desc);
            this.departId = departId;
            this.managerId = managerId;
            this.employeeIds = employeeIds;
            this.valid = valid;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }

        public String getDepartId() {
            return departId;
        }

        public String getManagerId() {
            return managerId;
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getEmployeeIds() {
            return employeeIds;
        }
    }
}
