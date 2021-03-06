package com.ds.retl.service;

import com.ds.retl.dal.entity.Topology;
import com.ds.retl.exception.UserInterfaceErrorException;

/**
 * 计算拓扑管理相关服务接口定义
 *
 * @author : john.peng created on date : 2017/10/10
 */
public interface TopologyManageService {
    /**
     * 保存一个输入配置信息的计算拓扑
     *
     * @param id              拓扑关键字ID，如果新增id为null
     * @param topologyJsonStr 拓扑配置信息
     * @return 成功返回拓扑对象，否则抛出异常
     * @throws UserInterfaceErrorException 保存过程中发生的异常
     */
    Topology save(String id, String topologyJsonStr) throws UserInterfaceErrorException;

    /**
     * 杀死指定ID的计算拓扑
     *
     * @param id 拓扑关键字ID
     * @return 成功返回拓扑对象，否则抛出异常
     * @throws UserInterfaceErrorException 操作过程中发生的异常
     */
    Topology kill(String id) throws UserInterfaceErrorException;

    /**
     * 逻辑删除指定的计算拓扑
     *
     * @param id 拓扑关键字ID
     * @throws UserInterfaceErrorException 操作过程中发生的异常
     */
    void delete(String id) throws UserInterfaceErrorException;

    /**
     * 提交指定关键字ID的计算拓扑到Storm集群
     *
     * @param id         拓扑关键字ID
     * @param simulation 是否仿真
     * @return 成功返回拓扑对象，否则抛出异常
     * @throws UserInterfaceErrorException 提交过程中发生的异常
     */
    Topology submit(String id, boolean simulation) throws UserInterfaceErrorException;

    /**
     * 提交一个输入配置信息的计算拓扑到Storm集群，提交之前会先保存拓扑信息。
     *
     * @param id              拓扑关键字ID，如果是新增，则id为null
     * @param topologyJsonStr 拓扑配置信息
     * @return 成功返回拓扑对象，否则抛出异常
     * @throws UserInterfaceErrorException 提交过程中发生的异常
     * @see #save(String, String)
     */
    Topology submit(String id, String topologyJsonStr) throws UserInterfaceErrorException;

    /**
     * 校验检测配置的ZOOKEEPER服务器是否有效
     *
     * @param resourceJsonStr ZOOKEEPER服务器配置信息
     * @return 成功返回true，否则抛出异常
     * @throws UserInterfaceErrorException 检测过程中发生的异常
     */
    boolean validateZookeepers(String resourceJsonStr) throws UserInterfaceErrorException;

    /**
     * 校验检测JDBC数据源是否有效
     *
     * @param resourceJsonStr JDBC数据源配置信息
     * @return 成功返回true，否则抛出异常
     * @throws UserInterfaceErrorException 检测过程中发生的异常
     */
    boolean validateJdbcDataSource(String resourceJsonStr) throws UserInterfaceErrorException;

    /**
     * 校验检测JMS数据源是否有效
     *
     * @param resourceJsonStr JMS数据源配置信息
     * @return 成功返回true，否则抛出异常
     * @throws UserInterfaceErrorException 检测过程中发生的异常
     */
    boolean validateJmsDataSource(String resourceJsonStr) throws UserInterfaceErrorException;
}
