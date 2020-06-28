package server.http.carrier;

import server.connector.HttpConnector;
import server.constant.HttpVersionConstant;
import server.enums.HttpStatusEnum;
import server.util.ArrayUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Collection;
import java.util.Locale;

/**
 * @author zhout
 * @date 2020/6/9 15:02
 */
public class HttpResponse implements HttpServletResponse {

  private OutputStream outputStream;
  private HttpRequest request;
  private PrintWriter writer;

  public HttpResponse(OutputStream outputStream, HttpRequest request) {
    this.outputStream = outputStream;
    this.request = request;
  }

  public void accessStaticResources() throws IOException {
    // 根据请求URI找到用户对应请求的资源文件
    File staticResource = new File(HttpConnector.WEB_PROJECT_ROOT + request.getRequestURI());
    // 资源存在
    if (staticResource.exists() && staticResource.isFile()) {
      outputStream.write(responseToByte(HttpStatusEnum.OK));
      // 资源不存在
    } else {
      staticResource = new File(HttpConnector.WEB_PROJECT_ROOT + "/404.html");
      outputStream.write(responseToByte(HttpStatusEnum.NOT_FOUND));
    }
    writeFile(staticResource);
  }

  /**
   * 将请求行 请求头转换为byte数组
   *
   * @param status 响应http状态
   * @return 响应头byte数组
   */
  public byte[] responseToByte(HttpStatusEnum status) {
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

  /**
   * 将读取到的资源文件输出
   *
   * @param file 读取到的文件
   * @throws IOException IOException
   */
  private void writeFile(File file) throws IOException {
    try (FileInputStream fis = new FileInputStream(file)) {
      byte[] cache = ArrayUtil.generatorCache();
      int read;
      while ((read = fis.read(cache, 0, ArrayUtil.BUFFER_SIZE)) != -1) {
        outputStream.write(cache, 0, read);
      }
    }
  }

  public void finishResponse() {
    // sendHeaders();
    // Flush and close the appropriate output mechanism
    if (writer != null) {
      writer.flush();
      writer.close();
    }
  }

  @Override
  public void addCookie(Cookie cookie) {}

  @Override
  public boolean containsHeader(String s) {
    return false;
  }

  @Override
  public String encodeURL(String s) {
    return null;
  }

  @Override
  public String encodeRedirectURL(String s) {
    return null;
  }

  @Override
  public String encodeUrl(String s) {
    return null;
  }

  @Override
  public String encodeRedirectUrl(String s) {
    return null;
  }

  @Override
  public void sendError(int i, String s) throws IOException {}

  @Override
  public void sendError(int i) throws IOException {}

  @Override
  public void sendRedirect(String s) throws IOException {}

  @Override
  public void setDateHeader(String s, long l) {}

  @Override
  public void addDateHeader(String s, long l) {}

  @Override
  public void setHeader(String s, String s1) {}

  @Override
  public void addHeader(String s, String s1) {}

  @Override
  public void setIntHeader(String s, int i) {}

  @Override
  public void addIntHeader(String s, int i) {}

  @Override
  public void setStatus(int i) {}

  @Override
  public void setStatus(int i, String s) {}

  @Override
  public int getStatus() {
    return 0;
  }

  @Override
  public String getHeader(String s) {
    return null;
  }

  @Override
  public Collection<String> getHeaders(String s) {
    return null;
  }

  @Override
  public Collection<String> getHeaderNames() {
    return null;
  }

  @Override
  public String getCharacterEncoding() {
    return null;
  }

  @Override
  public String getContentType() {
    return null;
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    return null;
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    if (writer != null) {
      return writer;
    }
    return (writer = new PrintWriter(outputStream));
  }

  @Override
  public void setCharacterEncoding(String s) {}

  @Override
  public void setContentLength(int i) {}

  @Override
  public void setContentType(String s) {}

  @Override
  public void setBufferSize(int i) {}

  @Override
  public int getBufferSize() {
    return 0;
  }

  @Override
  public void flushBuffer() throws IOException {}

  @Override
  public void resetBuffer() {}

  @Override
  public boolean isCommitted() {
    return false;
  }

  @Override
  public void reset() {}

  @Override
  public void setLocale(Locale locale) {}

  @Override
  public Locale getLocale() {
    return null;
  }
}
