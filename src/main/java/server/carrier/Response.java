package server.carrier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.App;
import server.constant.HttpVersionConstant;
import server.enums.HttpStatusEnum;
import server.util.ArrayUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author zhout
 * @date 2020/6/9 15:02
 */
public class Response {

  private static final Logger LOGGER = LoggerFactory.getLogger(Response.class);

  private OutputStream outputStream;

  private Request request;

  public void accessStaticResources() throws IOException {
    // 根据请求URI找到用户对应请求的资源文件
    File staticResource = new File(App.WEB_PROJECT_ROOT + request.getUri());
    // 资源存在
    if (staticResource.exists() && staticResource.isFile()) {
      outputStream.write(responseToByte(HttpStatusEnum.OK));
      // 资源不存在
    } else {
      staticResource = new File(App.WEB_PROJECT_ROOT + "/404.html");
      outputStream.write(responseToByte(HttpStatusEnum.NOT_FOUND));
    }
    write(staticResource);
  }

  /**
   * 将读取到的资源文件输出
   *
   * @param file 读取到的文件
   * @throws IOException IOException
   */
  private void write(File file) throws IOException {
    try (FileInputStream fis = new FileInputStream(file)) {
      byte[] cache = ArrayUtil.generatorCache();
      int read;
      while ((read = fis.read(cache, 0, ArrayUtil.BUFFER_SIZE)) != -1) {
        outputStream.write(cache, 0, read);
      }
    }
  }

  /**
   * 将请求行 请求头转换为byte数组
   *
   * @param status 状态吗
   */
  private byte[] responseToByte(HttpStatusEnum status) {
    return new StringBuilder()
        .append(HttpVersionConstant.HTTP_1_1)
        .append(" ")
        .append(status.getStatus())
        .append(" ")
        .append(status.getDesc())
        .append("\r\n\r\n")
        .toString()
        .getBytes();
  }

  public Response(OutputStream outputStream, Request request) {
    this.outputStream = outputStream;
    this.request = request;
  }

  public OutputStream getOutputStream() {
    return outputStream;
  }

  public Request getRequest() {
    return request;
  }
}
