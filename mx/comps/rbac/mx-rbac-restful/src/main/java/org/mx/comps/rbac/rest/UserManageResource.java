package org.mx.comps.rbac.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.rest.vo.AccountInfoVO;
import org.mx.comps.rbac.rest.vo.AccountVO;
import org.mx.comps.rbac.rest.vo.UserInfoVO;
import org.mx.comps.rbac.rest.vo.UserVO;
import org.mx.comps.rbac.service.UserManageService;
import org.mx.dal.Pagination;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.session.SessionDataStore;
import org.mx.error.UserInterfaceException;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.rest.vo.DataVO;
import org.mx.service.rest.vo.PaginationDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path("/rest")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserManageResource {
    private static final Log logger = LogFactory.getLog(UserManageResource.class);

    @Autowired
    @Qualifier("generalAccessor")
    private GeneralAccessor accessor = null;

    @Autowired
    private UserManageService userManageService = null;

    @Autowired
    private SessionDataStore sessionDataStore = null;


    @Path("users")
    @GET
    public DataVO<List<UserVO>> listUsers() {
        try {
            List<User> users = accessor.list(User.class);
            List<UserVO> userVOS = UserVO.transform(users);
            return new DataVO<>(userVOS);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("List users fail.", ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("users")
    @POST
    public PaginationDataVO<List<UserVO>> listUsersPagination(Pagination pagination) {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            List<User> users = accessor.list(pagination, User.class);
            List<UserVO> userVOs = UserVO.transform(users);
            return new PaginationDataVO<>(pagination, userVOs);
        } catch (UserInterfaceException ex) {
            return new PaginationDataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("List users fail.", ex);
            }
            return new PaginationDataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("users/{id}")
    @GET
    public DataVO<UserVO> getUser(@PathParam("id") String id) {
        try {
            User user = accessor.getById(id, User.class);
            UserVO userVO = UserVO.transform(user);
            return new DataVO<>(userVO);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Get user fail.", ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("users/new")
    @POST
    public DataVO<UserVO> newUser(@QueryParam("userCode") String userCode, UserInfoVO userInfoVO) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            userInfoVO.setId(null);
            User user = userManageService.saveUser(userInfoVO.getUserInfo());
            UserVO userVO = UserVO.transform(user);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(userVO);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Create a user fail.", ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("users/{userId}")
    @PUT
    public DataVO<UserVO> saveUser(@QueryParam("userCode") String userCode, @PathParam("userId") String userId,
                                   UserInfoVO userInfoVO) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            userInfoVO.setId(userId);
            User user = userManageService.saveUser(userInfoVO.getUserInfo());
            UserVO userVO = UserVO.transform(user);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(userVO);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Save user fail.", ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("users/{userId}")
    @DELETE
    public DataVO<UserVO> deleteUser(@QueryParam("userCode") String userCode, @PathParam("userId") String userId) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            User user = accessor.remove(userId, User.class);
            UserVO userVO = UserVO.transform(user);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(userVO);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Delete user fail.", ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("users/{userId}/allocate")
    @POST
    public DataVO<AccountVO> allocateAccount(@QueryParam("userCode") String userCode, @PathParam("userId") String userId,
                                             AccountInfoVO accountInfoVO) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            accountInfoVO.setOwnerId(userId);
            Account account = userManageService.allocateAccount(accountInfoVO.getAccountInfo());
            AccountVO accountVO = AccountVO.transform(account, true);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(accountVO);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Allocate account fail.", ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }
}
