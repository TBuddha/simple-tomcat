package server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.carrier.Request;
import server.carrier.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Optional;

/**
 * 使用java socket api 实现简单的静态web服务器。
 *
 * @author zhout
 * @date 2020/6/9 14:53
 */
public class App {

  private static final Integer PORT = 8080;

  private static final String HOST = "127.0.0.1";

  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

  /** 用户自定义web项目的相对路径 */
  public static final String WEB_PROJECT_ROOT;

  /** 关闭服务器的命令 */
  private static final String SHUTDOWN_SERVER = "/SHUTDOWN-SERVER";

  /** 是否关闭服务器标识 */
  private transient boolean shutDowned = false;

  static {
    // 初始化相对目录
    URL webrootURL = App.class.getClassLoader().getResource("webroot");
    WEB_PROJECT_ROOT =
        Optional.ofNullable(webrootURL)
            .orElseThrow(() -> new IllegalStateException("can't not find user web root file."))
            .getPath();
  }

  public static void main(String[] args) {
    new App().await();
  }

  private void await() {
    //new 一个 byte缓冲数组
    ServerSocket serverSocket;
    try {
      serverSocket = new ServerSocket(PORT, 1, InetAddress.getByName(HOST));
      LOGGER.info("Server is starting ... listener port {}", PORT);
    } catch (IOException e) {
      LOGGER.error("Server shutdown!", e);
      throw new RuntimeException(e);
    }

    while (!shutDowned) {
      try (Socket accept = serverSocket.accept();
          InputStream inputStream = accept.getInputStream();
          OutputStream outputStream = accept.getOutputStream()) {
        // 解析用户的请求
        Request request = new Request();
        request.setRequestStream(inputStream);
        request.parseRequest();
        // 生成相应的响应
        Response response = new Response(outputStream, request);
        response.accessStaticResources();

        // 如果本次请求是关闭服务器则修改标识为关闭
        shutDowned = SHUTDOWN_SERVER.equals(request.getUri());
      } catch (IOException e) {
        LOGGER.warn("catch from user request.", e);
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
