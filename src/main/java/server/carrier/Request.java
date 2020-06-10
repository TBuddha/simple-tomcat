package server.carrier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.util.ArrayUtil;

import javax.servlet.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * @author zhout
 * @date 2020/6/9 15:02
 */
public class Request implements ServletRequest {

  private static final Logger LOGGER = LoggerFactory.getLogger(Request.class);
  /** 用户请求输入流* */
  private InputStream requestStream;
  /** 解析用户请求后的URI* */
  private String uri;

  /** 解析用户的请求 */
  public void parseRequest() {
    // new 一个 byte缓冲数组
    StringBuilder request = new StringBuilder();
    int i;
    byte[] buffer = ArrayUtil.generatorCache();
    try {
      i = requestStream.read(buffer);
    } catch (IOException e) {
      e.printStackTrace();
      i = -1;
    }
    // 将读取到的byte转为String
    for (int j = 0; j < i; j++) {
      request.append((char) buffer[j]);
    }
    LOGGER.trace("parse request {}", request.toString());
    // 解析请求的字符串，提取请求的URI
    this.setUri(request.toString());
  }

  /**
   * 将解析的用户请求信息筛选出请求URI
   *
   * @param parsedContent 用户的请求信息
   */
  private void setUri(String parsedContent) {
    // 获取第一个空格的索引，是用户请求method后面的空格 例如 GET /index.html ABCD.....
    int oneSpace = parsedContent.indexOf(" ");
    // 获取第二个空格的索引，是用户请求URI后面的第一个空格
    int twoSpace = parsedContent.indexOf(" ", oneSpace + 1);
    if (oneSpace == -1 || twoSpace == -1) {
      LOGGER.debug("Parse Request is empty.");
      return;
    }
    // 截取获得用户请求URI
    uri = parsedContent.substring(oneSpace + 1, twoSpace);
    LOGGER.info("request URI:{}", uri);
  }

  public InputStream getRequestStream() {
    return requestStream;
  }

  public void setRequestStream(InputStream requestStream) {
    this.requestStream = requestStream;
  }

  public String getUri() {
    return uri;
  }

  @Override
  public Object getAttribute(String s) {
    return null;
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    return null;
  }

  @Override
  public String getCharacterEncoding() {
    return null;
  }

  @Override
  public void setCharacterEncoding(String s) throws UnsupportedEncodingException {}

  @Override
  public int getContentLength() {
    return 0;
  }

  @Override
  public String getContentType() {
    return null;
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    return null;
  }

  @Override
  public String getParameter(String s) {
    return null;
  }

  @Override
  public Enumeration<String> getParameterNames() {
    return null;
  }

  @Override
  public String[] getParameterValues(String s) {
    return new String[0];
  }

  @Override
  public Map<String, String[]> getParameterMap() {
    return null;
  }

  @Override
  public String getProtocol() {
    return null;
  }

  @Override
  public String getScheme() {
    return null;
  }

  @Override
  public String getServerName() {
    return null;
  }

  @Override
  public int getServerPort() {
    return 0;
  }

  @Override
  public BufferedReader getReader() throws IOException {
    return null;
  }

  @Override
  public String getRemoteAddr() {
    return null;
  }

  @Override
  public String getRemoteHost() {
    return null;
  }

  @Override
  public void setAttribute(String s, Object o) {}

  @Override
  public void removeAttribute(String s) {}

  @Override
  public Locale getLocale() {
    return null;
  }

  @Override
  public Enumeration<Locale> getLocales() {
    return null;
  }

  @Override
  public boolean isSecure() {
    return false;
  }

  @Override
  public RequestDispatcher getRequestDispatcher(String s) {
    return null;
  }

  @Override
  public String getRealPath(String s) {
    return null;
  }

  @Override
  public int getRemotePort() {
    return 0;
  }

  @Override
  public String getLocalName() {
    return null;
  }

  @Override
  public String getLocalAddr() {
    return null;
  }

  @Override
  public int getLocalPort() {
    return 0;
  }

  @Override
  public ServletContext getServletContext() {
    return null;
  }

  @Override
  public AsyncContext startAsync() throws IllegalStateException {
    return null;
  }

  @Override
  public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
      throws IllegalStateException {
    return null;
  }

  @Override
  public boolean isAsyncStarted() {
    return false;
  }

  @Override
  public boolean isAsyncSupported() {
    return false;
  }

  @Override
  public AsyncContext getAsyncContext() {
    return null;
  }

  @Override
  public DispatcherType getDispatcherType() {
    return null;
  }
}
