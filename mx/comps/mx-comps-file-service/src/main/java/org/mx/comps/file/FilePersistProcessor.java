package org.mx.comps.file;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件存储处理接口定义
 *
 * @author : john.peng created on date : 2017/12/04
 */
public interface FilePersistProcessor {
    /**
     * 获取文件的长度
     *
     * @return 长度
     */
    long getLength();

    /**
     * 获取文件名
     *
     * @return 文件名
     */
    String getFilename();

    /**
     * 判断处理器是否已经被打开
     *
     * @return 返回true表示已经被打开，否则未打开。
     */
    boolean isOpened();

    /**
     * 关闭文件
     */
    void close();

    /**
     * 根据HTTP请求中的参数初始化处理器
     *
     * @param req HTTP请求
     */
    void init(HttpServletRequest req);

    /**
     * 执行一条从WebSocket中收到的文本命令，该文本命令一般是JSON格式的字符串
     *
     * @param command 文本命令
     */
    void command(String command);

    /**
     * 将打开的文件内容读入到输出流中
     *
     * @param out 输出流
     */
    void read(OutputStream out);

    /**
     * 将输入流中的数据写入到打开的文件中
     *
     * @param in 输入流
     */
    void persist(InputStream in);
}
