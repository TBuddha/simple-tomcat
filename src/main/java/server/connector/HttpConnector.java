package server.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.http.process.HttpProcess;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Optional;

/**
 * Http连接器
 *
 * @author zhout
 * @date 2020/6/11 15:44
 */
public class HttpConnector implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpConnector.class);

  private transient boolean shutdowned;

  /** 用户自定义web项目的相对路径 */
  public static final String WEB_PROJECT_ROOT;

  static {
    // 初始化用户的相对目录
    URL webrootURL = HttpConnector.class.getClassLoader().getResource("webroot");
    WEB_PROJECT_ROOT =
        Optional.ofNullable(webrootURL)
            .orElseThrow(() -> new IllegalStateException("can't not find user web root file."))
            .getPath();
  }

  public void start() {
    new Thread(this).start();
  }

  @Override
  public void run() {
    // 开启SocketServer服务等待连接
    ServerSocket serverSocket;
    try {
      serverSocket = new ServerSocket(8080, 1, InetAddress.getByName("127.0.0.1"));
      LOGGER.info("Server is starting ... listener port {}", 8080);
    } catch (IOException e) {
      LOGGER.error("Server shutdown!", e);
      throw new RuntimeException(e);
    }

    while (!shutdowned) {
      // 阻塞等待连接
      try (Socket accept = serverSocket.accept()) {
        // 处理连接
        HttpProcess process = new HttpProcess(this);
        process.process(accept);
      } catch (IOException e) {
        LOGGER.warn("Catch from user process.", e);
      }
    }
    // 关闭服务器
    try {
      serverSocket.close();
    } catch (IOException e) {
      LOGGER.error("Shutdown server is fail!", e);
    }
  }
}
