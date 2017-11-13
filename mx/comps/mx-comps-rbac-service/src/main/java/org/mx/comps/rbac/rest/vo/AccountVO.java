package org.mx.comps.rbac.rest.vo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.dal.EntityFactory;
import org.mx.dal.exception.EntityInstantiationException;
import org.mx.rest.vo.BaseDictVO;

import java.util.ArrayList;
import java.util.List;

public class AccountVO extends BaseDictVO {
    private static final Log logger = LogFactory.getLog(AccountVO.class);

    private String password;
    private UserVO owner;
    private List<RoleVO> roles;

    public static void transform(Account account, AccountVO accountVO) {
        if (account == null || accountVO == null) {
            return;
        }
        BaseDictVO.transform(account, accountVO);
        accountVO.password = account.getPassword();
        if (account.getOwner() != null) {
            UserVO vo = new UserVO();
            UserVO.transform(account.getOwner(), vo);
            accountVO.owner = vo;
        }
        List<Role> roles = account.getRoles();
        if (roles != null && !roles.isEmpty()) {
            accountVO.roles = RoleVO.transformRoleVOs(roles);
        }
    }

    public static void transform(AccountVO accountVO, Account account) {
        if (account == null || accountVO == null) {
            return;
        }
        BaseDictVO.transform(accountVO, account);
        account.setPassword(accountVO.getPassword());
        if (accountVO.getOwner() != null) {
            try {
                User user = EntityFactory.createEntity(User.class);
                UserVO.transform(accountVO.getOwner(), user);
                account.setOwner(user);
            } catch (EntityInstantiationException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn(ex);
                }
            }
        }
        List<RoleVO> roleVOs = accountVO.getRoles();
        if (roleVOs != null && !roleVOs.isEmpty()) {
            account.setRoles(RoleVO.transformRoles(roleVOs));
        }
    }

    public static List<Account> transformAccounts(List<AccountVO> accountVOs) {
        if (accountVOs == null || accountVOs.isEmpty()) {
            return null;
        }
        List<Account> accounts = new ArrayList<>(accountVOs.size());
        for (AccountVO vo : accountVOs) {
            try {
                Account account = EntityFactory.createEntity(Account.class);
                AccountVO.transform(vo, account);
                accounts.add(account);
            } catch (EntityInstantiationException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn(ex);
                }
            }
        }
        return accounts;
    }

    public static List<AccountVO> transformAccountVOs(List<Account> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            return null;
        }
        List<AccountVO> accountVOs = new ArrayList<>(accounts.size());
        for (Account account : accounts) {
            AccountVO vo = new AccountVO();
            AccountVO.transform(account, vo);
            accountVOs.add(vo);
        }
        return accountVOs;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setOwner(UserVO owner) {
        this.owner = owner;
    }

    public void setRoles(List<RoleVO> roles) {
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }

    public UserVO getOwner() {
        return owner;
    }

    public List<RoleVO> getRoles() {
        return roles;
    }
}