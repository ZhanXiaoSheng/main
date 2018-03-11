package org.mx.comps.notify.online.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.mx.StringUtils;
import org.mx.comps.notify.online.OnlineDevice;
import org.mx.comps.notify.online.OnlineManager;
import org.mx.service.server.websocket.WsSessionManager;
import org.mx.spring.SpringContextHolder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 一个简单的在线设备管理实现
 *
 * @author : john.peng created on date : 2018/1/5
 */
@Component("onlineManagerSimple")
public class OnlineManagerSimpleImpl implements OnlineManager, InitializingBean, DisposableBean {
    private static final Log logger = LogFactory.getLog(OnlineManagerSimpleImpl.class);

    private final Serializable onlineDeviceMutex = "ONLINE_DEVICE";
    private ConcurrentMap<String, OnlineDevice> onlineDevices = null;
    private Timer cleanTimer = null;
    private long pongIdleTimeoutSecs = 60l;

    @Autowired
    private Environment env = null;

    /**
     * 默认的构造函数
     */
    public OnlineManagerSimpleImpl() {
        super();
        this.onlineDevices = new ConcurrentHashMap<>();
    }

    /**
     * {@inheritDoc}
     *
     * @see OnlineManager#getOnlineDevices(List)
     */
    @Override
    public Set<OnlineDevice> getOnlineDevices(List<Predicate<OnlineDevice>> filters) {
        if (filters != null && !filters.isEmpty()) {
            Stream<OnlineDevice> stream = onlineDevices.values().stream();
            for (Predicate<OnlineDevice> filter : filters) {
                stream = stream.filter(filter);
            }
            return stream.collect(Collectors.toSet());
        } else {
            Set<OnlineDevice> set = new HashSet<>();
            set.addAll(onlineDevices.values());
            return set;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (env != null) {
            pongIdleTimeoutSecs = env.getProperty("websocket.pong.idleTimeoutSecs", Long.class, 60l);
        }
        cleanTimer = new Timer();
        cleanTimer.scheduleAtFixedRate(new CleanTask(), 5000, pongIdleTimeoutSecs * 1000 / 3);
    }

    /**
     * {@inheritDoc}
     *
     * @see DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception {
        if (cleanTimer != null) {
            cleanTimer.cancel();
            cleanTimer.purge();
            cleanTimer = null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see OnlineManager#registryDevice(OnlineDevice)
     */
    @Override
    public boolean registryDevice(OnlineDevice onlineDevice) {
        // TODO 处理终端认证
        synchronized (OnlineManagerSimpleImpl.this.onlineDeviceMutex) {
            String key = String.format("%s@%s", onlineDevice.getDeviceId(), onlineDevice.getConnectKey());
            if (onlineDevices.containsKey(key)) {
                OnlineDevice device = onlineDevices.get(key);
                device.setRegistryTime(System.currentTimeMillis());
                device.update(onlineDevice.getState(), System.currentTimeMillis(), onlineDevice.getLastLongitude(),
                        onlineDevice.getLastLatitude());
                onlineDevices.put(key, device);
            } else {
                onlineDevice.setRegistryTime(System.currentTimeMillis());
                onlineDevice.setLastTime(System.currentTimeMillis());
                onlineDevices.put(key, onlineDevice);
            }
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Registry [%s] successfully.", key));
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see OnlineManager#unregistryDevice(OnlineDevice)
     */
    @Override
    public boolean unregistryDevice(OnlineDevice onlineDevice) {
        synchronized (OnlineManagerSimpleImpl.this.onlineDeviceMutex) {
            String key = String.format("%s@%s", onlineDevice.getDeviceId(), onlineDevice.getConnectKey());
            if (onlineDevices.containsKey(key)) {
                onlineDevices.remove(key);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Unregistry [%s] successfully.", key));
                }
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see OnlineManager#pongDevice(OnlineDevice)
     */
    @Override
    public void pongDevice(OnlineDevice onlineDevice) {
        synchronized (OnlineManagerSimpleImpl.this.onlineDeviceMutex) {
            String key = String.format("%s@%s", onlineDevice.getDeviceId(), onlineDevice.getConnectKey());
            if (onlineDevices.containsKey(key)) {
                OnlineDevice device = onlineDevices.get(key);
                device.update(onlineDevice.getState(), System.currentTimeMillis(), onlineDevice.getLastLongitude(),
                        onlineDevice.getLastLatitude());
                onlineDevices.put(key, device);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Pong [%s] successfully.", key));
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see OnlineManager#getConnectionSessions(Predicate)
     */
    @Override
    public Map<String, Session> getConnectionSessions(Predicate<OnlineDevice> filter) {
        Map<String, Session> sessions = new HashMap<>();
        onlineDevices.values().stream().filter(filter).forEach(onlineDevice -> {
            Session session = this.getConnectionSession(onlineDevice.getConnectKey());
            sessions.put(onlineDevice.getDeviceId(), session);
        });
        return sessions;
    }

    /**
     * {@inheritDoc}
     *
     * @see OnlineManager#getConnectionSession(String)
     */
    @Override
    public Session getConnectionSession(String connectKey) {
        if (!StringUtils.isBlank(connectKey)) {
            WsSessionManager sessionManager = SpringContextHolder.getBean(WsSessionManager.class);
            if (sessionManager == null) {
                return sessionManager.getSession(connectKey);
            } else {
                if (logger.isWarnEnabled()) {
                    logger.warn("The WsSessionManager is not initialized.");
                }
            }
        }
        return null;
    }

    /**
     * 根据心跳时间来清除无效的连接设备
     */
    private void cleanInvalidDevices() {
        WsSessionManager sessionManager = SpringContextHolder.getBean(WsSessionManager.class);
        synchronized (OnlineManagerSimpleImpl.this.onlineDeviceMutex) {
            Set<String> invalid = new HashSet<>();
            onlineDevices.forEach((k, v) -> {
                long delay = (System.currentTimeMillis() - v.getLastTime()) / 1000;
                if (delay > pongIdleTimeoutSecs) {
                    // 超过约定时间没有心跳，判定为无效在线设备
                    onlineDevices.remove(k);
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("The device[%s, %s] has been cleared for %d seconds without a heartbeat.",
                                v.getDeviceId(), v.getConnectKey(), delay));
                    }
                    if (sessionManager != null) {
                        // 同时对设备的会话进行清理
                        invalid.add(v.getConnectKey());
                    }
                }
            });
            sessionManager.removeWsSessions(invalid, 4002, "Be blocked for device's ping.");
        }
    }

    private class CleanTask extends TimerTask {
        /**
         * {@inheritDoc}
         *
         * @see TimerTask#run()
         */
        @Override
        public void run() {
            cleanInvalidDevices();
        }
    }
}
