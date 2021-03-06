package org.mx.error;

/**
 * 系统级错误异常
 *
 * @author : john.peng created on date : 2017.11.29
 */
public class UserInterfaceSystemErrorException extends UserInterfaceException {
    /**
     * 默认的构造函数
     */
    public UserInterfaceSystemErrorException() {
        this(SystemErrors.SYSTEM_OTHER_FAIL);
    }

    /**
     * 构造函数
     *
     * @param error 人机界面错误枚举
     */
    public UserInterfaceSystemErrorException(SystemErrors error) {
        super(error.getErrorCode(), error.getErrorMessage());
    }

    /**
     * 系统级错误信息枚举定义
     */
    public enum SystemErrors implements UserInterfaceError {
        SYSTEM_ILLEGAL_PARAM("输入的参数错误，请联系开发人员。"),
        SYSTEM_UNSUPPORTED_OPERATE("不支持的操作方法，请联系开发人员。"),
        SYSTEM_NO_SUCH_ALGORITHM("不支持的系统算法。"),

        SYSTEM_UNSUPPORTED("不支持的操作系统，请联系开发人员。"),

        FILE_NOT_EXISTED("指定的文件不存在，请联系开发人员。"),
        FILE_READ_ERROR("读取指定文件内容错误。"),

        SPRING_CACHE_REDIS_TYPE_UNSUPPORTED("不支持的Redis缓存类型。"),

        SYSTEM_OTHER_FAIL("未知的系统错误。");

        public static final int BASE_ORDINAL = 0;
        private String errorMessage;

        /**
         * 构造函数
         *
         * @param errorMessage 错误信息
         */
        SystemErrors(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        /**
         * {@inheritDoc}
         *
         * @see UserInterfaceError#getErrorCode()
         */
        @Override
        public int getErrorCode() {
            return BASE_ORDINAL + super.ordinal();
        }

        /**
         * {@inheritDoc}
         *
         * @see UserInterfaceError#getErrorMessage()
         */
        @Override
        public String getErrorMessage() {
            return this.errorMessage;
        }
    }
}
