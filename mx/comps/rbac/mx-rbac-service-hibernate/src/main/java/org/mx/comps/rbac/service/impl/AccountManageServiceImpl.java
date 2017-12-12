package org.mx.comps.rbac.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DigestUtils;
import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.LoginHistory;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.comps.rbac.service.AccountManageService;
import org.mx.dal.EntityFactory;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.OperateLogService;
import org.mx.dal.service.impl.GeneralDictEntityAccessorImpl;
import org.mx.error.UserInterfaceSystemErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 基于Hibernate JPA实现的账户管理相关服务
 *
 * @author : john.peng created on date : 2017/11/12
 */
@Component("accountManageService")
public class AccountManageServiceImpl extends GeneralDictEntityAccessorImpl implements AccountManageService {
    private static final Log logger = LogFactory.getLog(AccountManageServiceImpl.class);

    @Autowired
    private OperateLogService operateLogService = null;

    /**
     * {@inheritDoc}
     *
     * @see AccountManageService#saveAccount(AccountInfo)
     */
    @Transactional
    @Override
    public Account saveAccount(AccountInfo accountInfo) {
        if (accountInfo == null) {
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        try {
            String accountId = accountInfo.getAccountId();
            Account account;
            if (!StringUtils.isBlank(accountId)) {
                account = super.getById(accountId, Account.class);
                if (account == null) {
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("The Account entity[%s] not found.", accountId));
                    }
                    throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_FOUND);
                }
                // 这里不允许修改密码，密码必须通过另外途径进行修改
            } else {
                String password = accountInfo.getPassword();
                if (StringUtils.isBlank(password)) {
                    password = "ds110119";
                }
                account = EntityFactory.createEntity(Account.class);
                account.setPassword(DigestUtils.md5(password));
            }
            account.setCode(accountInfo.getCode());
            if (StringUtils.isBlank(accountInfo.getOwnerId())) {
                throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOALLOCATE_USER);
            } else {
                User owner = super.getById(accountInfo.getOwnerId(), User.class);
                if (owner == null) {
                    throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.USER_NOT_FOUND);
                }
                account.setOwner(owner);
                account.setName(owner.getFullName());
            }
            account.setDesc(accountInfo.getDesc());
            if (account.getRoles() != null && !account.getRoles().isEmpty()) {
                account.getRoles().clear();
            }
            for (String roleId : accountInfo.getRoleIds()) {
                Role role = super.getById(roleId, Role.class);
                if (role == null) {
                    throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ROLE_NOT_FOUND);
                }
                account.getRoles().add(role);
            }
            account.setValid(accountInfo.isValid());
            account = super.save(account, false);
            if (operateLogService != null) {
                operateLogService.writeLog(String.format("保存账户[code=%s, name=%s]成功。",
                        account.getCode(), account.getName()));
            }
            return account;
        } catch (UserInterfaceDalErrorException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.DB_OPERATE_FAIL);
        } catch (NoSuchAlgorithmException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_DIGEST_PASSWORD_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see AccountManageService#changePassword(String, String, String)
     */
    @Transactional
    @Override
    public Account changePassword(String accountId, String oldPassword, String newPassword) {
        Account account = super.getById(accountId, Account.class);
        if (account == null) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_FOUND);
        }
        try {
            if (account.getPassword().equals(DigestUtils.md5(oldPassword))) {
                // the old password is matched.
                account.setPassword(DigestUtils.md5(newPassword));
                account = super.save(account, false);
                if (operateLogService != null) {
                    operateLogService.writeLog(String.format("修改账户[code=%s, name=%s]的密码成功。",
                            account.getCode(), account.getName()));
                }
                return account;
            } else {
                throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_PASSWORD_NOT_MATCHED);
            }
        } catch (NoSuchAlgorithmException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_DIGEST_PASSWORD_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see AccountManageService#login(String, String, boolean)
     */
    @Transactional
    @Override
    public LoginHistory login(String accountCode, String password, boolean forced) {
        Account account = super.getByCode(accountCode, Account.class);
        if (account == null) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_FOUND);
        }
        try {
            if (!DigestUtils.md5(password).equals(account.getPassword())) {
                throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_PASSWORD_NOT_MATCHED);
            }
        } catch (NoSuchAlgorithmException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_UNSUPPORTED_OPERATE);
        }
        List<ConditionTuple> tuples = Arrays.asList(new ConditionTuple("account", account),
                new ConditionTuple("online", true));
        List<LoginHistory> loginHistories = super.find(tuples, LoginHistory.class);
        LoginHistory loginHistory;
        if (loginHistories != null && !loginHistories.isEmpty()) {
            // 已经登录
            if (forced) {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("The account[%s] has login, now login again.", accountCode));
                }
                // 强制重新登录
                if (loginHistories.size() > 1) {
                    // 根据登录时间排序
                    Collections.sort(loginHistories);
                }
                loginHistory = loginHistories.get(0);
            } else {
                throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_ALREADY_LOGINED);
            }
        } else {
            // 新登录
            loginHistory = EntityFactory.createEntity(LoginHistory.class);
            loginHistory.setAccount(account);
        }
        loginHistory.setLoginTime(new Date().getTime());
        loginHistory.setOnline(true);
        loginHistory = super.save(loginHistory, false);
        if (operateLogService != null) {
            operateLogService.writeLog(String.format("账户[code=%s, name=%s]登录系统成功。",
                    account.getCode(), account.getName()));
        }
        return loginHistory;
    }

    /**
     * {@inheritDoc}
     *
     * @see AccountManageService#logout(String)
     */
    @Transactional
    @Override
    public LoginHistory logout(String accountId) {
        Account account = super.getById(accountId, Account.class);
        if (account == null) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_FOUND);
        }
        List<ConditionTuple> tuples = Arrays.asList(new ConditionTuple("account", account),
                new ConditionTuple("online", true));
        List<LoginHistory> loginHistories = super.find(tuples, LoginHistory.class);
        if (loginHistories == null || loginHistories.isEmpty()) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_LOGIN);
        } else {
            if (loginHistories.size() > 1) {
                // 根据登录时间排序
                Collections.sort(loginHistories);
            }
            LoginHistory loginHistory = loginHistories.get(0);
            loginHistory.setLogoutTime(new Date().getTime());
            loginHistory.setOnline(false);
            loginHistory = super.save(loginHistory, false);
            if (operateLogService != null) {
                operateLogService.writeLog(String.format("账户[code=%s, name=%s]登出系统成功。",
                        account.getCode(), account.getName()));
            }
            return loginHistory;
        }
    }
}