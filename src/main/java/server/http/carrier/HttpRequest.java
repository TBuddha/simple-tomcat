package server.http.carrier;

import server.enums.HTTPMethodEnum;
import server.http.stream.RequestStream;
import server.util.Enumerator;
import server.util.ParameterMap;
import server.util.RequestUtil;
import server.util.StringUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhout
 * @date 2020/6/9 15:02
 */
public class HttpRequest implements HttpServletRequest {

  /** Socket客户端输入流 */
  private InputStream input;

  /** HTTP请求方法 */
  private String method;

  /** HTTP请求协议 */
  private String protocol;

  /** URI携带的查询参数 */
  private String queryString;

  /** 携带的jsessionid */
  private String requestedSessionId;

  /** jsessionid是否从URL携带 */
  private boolean requestedSessionURL;

  /** HTTP请求URI */
  private String requestURI;

  /** POST请求表单参数 */
  private String postParams;

  /** 请求内容长度 */
  private int contentLength;

  /** 请求内容类型 */
  private String contentType;

  /** 该请求关联的Cookie列表 */
  protected ArrayList<Cookie> cookies = new ArrayList<>();

  /** 与此请求关联的HTTP请求头，key:请求头名称 value:请求头内容数组 */
  protected HashMap<String, ArrayList<String>> headers = new HashMap<>();

  /** jsessionid是否从cookie携带 */
  private boolean requestedSessionCookie;

  /** 用于返回空枚举的空集合。请不要在此集合中添加任何元素~ */
  protected static ArrayList empty = new ArrayList();

  /** getDateHeader()方法中使用到的日期格式 */
  protected SimpleDateFormat[] formats = {
    new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US),
    new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US),
    new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US)
  };

  /**
   * 该请求的解析参数。只有在通过getParameter()系列方法调用的系列中请求参数信息时，才会填充该信息 key是参数名，而value是该参数的的字符串数组。
   * 一旦对特定请求的参数进行解析并存储在这里，它们就不会被修改。因此，对参数的应用程序级别访问不需要同步。
   */
  protected ParameterMap parameters;

  /** 标识本次请求的参数是否解析完毕 */
  protected boolean parsed = false;

  protected String pathInfo;

  /** 这个请求的上下文路径 */
  protected String contextPath = "";

  /** 服务器名称 */
  private String serverName;

  /** Socket的InputStream的字符流版本 */
  protected BufferedReader reader;

  /** 封装的Socket InputStream (Servlet版本） */
  protected ServletInputStream stream;

  /** 构造方法 */
  public HttpRequest(InputStream input) {
    this.input = input;
  }

  /** HTTP请求方法 */
  public void setMethod(String method) {
    this.method = method;
  }

  /** HTTP请求协议 */
  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  /** URI携带的查询参数 */
  public void setQueryString(String queryString) {
    this.queryString = queryString;
  }

  /** 设置本次请求的jsessionid */
  public void setRequestedSessionId(String requestedSessionId) {
    this.requestedSessionId = requestedSessionId;
  }

  /** 设置本次请求的jsessionid是否为URL携带传入 */
  public void setRequestedSessionURL(boolean flag) {
    this.requestedSessionURL = flag;
  }

  /** HTTP请求URI */
  public void setRequestURI(String requestURI) {
    this.requestURI = requestURI;
  }

  /**
   * 设置本次请求的jsessionid是否为cookie携带传入
   *
   * @param flag jsessionid是否为cookie携带传入
   */
  public void setRequestedSessionCookie(boolean flag) {
    this.requestedSessionCookie = flag;
  }

  /**
   * 添加请求头
   *
   * @param name 请求头key
   * @param value 请求头内容
   */
  public void addHeader(String name, String value) {
    name = name.toLowerCase();
    ArrayList<String> values = headers.computeIfAbsent(name, k -> new ArrayList<>());
    values.add(value);
  }

  /** POST请求表单参数 */
  public void setPostParams(String postParams) {
    this.postParams = postParams;
  }

  /** 请求内容长度 */
  public void setContentLength(int length) {
    this.contentLength = length;
  }

  /** 请求内容类型 */
  public void setContentType(String type) {
    this.contentType = type;
  }

  /** 添加Cookie */
  public void addCookie(Cookie cookie) {
    cookies.add(cookie);
  }

  public String getPostParams() {
    return postParams;
  }

  /** 如果本次请求参数并未解析，如果在URI和POST表单中都存在参数， 则将它们合并，最后放入ParameterMap中 */
  protected void parseParameters() {
    if (parsed) {
      return;
    }
    ParameterMap results = parameters;
    if (results == null) {
      results = new ParameterMap();
    }
    results.setLocked(false);
    String encoding = getCharacterEncoding();
    if (encoding == null) {
      encoding = StringUtil.ISO_8859_1;
    }
    // 解析URI携带的请求参数
    String queryString = getQueryString();
    RequestUtil.parseParameters(results, queryString, encoding);

    // 初始化Content-Type的值
    String contentType = getContentType();
    if (contentType == null) {
      contentType = "";
    }
    int semicolon = contentType.indexOf(';');
    if (semicolon >= 0) {
      contentType = contentType.substring(0, semicolon).trim();
    } else {
      contentType = contentType.trim();
    }
    // 解析POST请求的表单参数
    if (HTTPMethodEnum.POST.name().equals(getMethod())
        && getContentLength() > 0
        && "application/x-www-form-urlencoded".equals(contentType)) {
      RequestUtil.parseParameters(results, getPostParams(), encoding);
    }

    // 解析完毕就锁定
    results.setLocked(true);
    parsed = true;
    parameters = results;
  }

  /**
   * 创建一个输入流，是一个RequestStream包装的Socket InputStream
   *
   * @throws IOException if an input/output error occurs
   */
  public ServletInputStream createInputStream() throws IOException {
    return (new RequestStream(this));
  }

  public InputStream getStream() {
    return input;
  }

  @Override
  public String getAuthType() {
    return null;
  }

  @Override
  public Cookie[] getCookies() {
    return cookies.toArray(new Cookie[cookies.size()]);
  }

  @Override
  public long getDateHeader(String name) {
    String value = getHeader(name);
    if (value == null) {
      return (-1L);
    }
    // Work around a bug in SimpleDateFormat in pre-JDK1.2b4
    // (Bug Parade bug #4106807)
    value += " ";

    // Attempt to convert the date header in a variety of formats
    for (SimpleDateFormat format : formats) {
      try {
        Date date = format.parse(value);
        return (date.getTime());
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    throw new IllegalArgumentException(value);
  }

  /**
   * 获取请求头值
   *
   * @param name 请求头名字
   * @return 请求头值
   */
  @Override
  public synchronized String getHeader(String name) {
    if (name != null) {
      name = name.toLowerCase();
    }
    ArrayList<String> values = headers.get(name);
    if (values != null) {
      return values.get(0);
    } else {
      return null;
    }
  }

  /**
   * 获取该请求头所有的值
   *
   * @param name 请求头名
   * @return 请求头值集合
   */
  @Override
  public Enumeration<String> getHeaders(String name) {
    name = name.toLowerCase();
    ArrayList<String> values = headers.get(name);
    if (values != null) {
      return new Enumerator<>(values);
    } else {
      return new Enumerator<>(empty);
    }
  }

  /** 获取所有请求头的名字集合 */
  @Override
  public Enumeration getHeaderNames() {
    return new Enumerator<>(headers.keySet());
  }

  @Override
  public int getIntHeader(String name) {
    String value = getHeader(name);
    if (value == null) {
      return (-1);
    } else {
      return (Integer.parseInt(value));
    }
  }

  @Override
  public String getMethod() {
    return method;
  }

  @Override
  public String getPathInfo() {
    return pathInfo;
  }

  @Override
  public String getPathTranslated() {
    return null;
  }

  @Override
  public String getContextPath() {
    return contextPath;
  }

  @Override
  public String getQueryString() {
    return queryString;
  }

  @Override
  public String getRemoteUser() {
    return null;
  }

  @Override
  public boolean isUserInRole(String s) {
    return false;
  }

  @Override
  public Principal getUserPrincipal() {
    return null;
  }

  @Override
  public String getRequestedSessionId() {
    return requestedSessionId;
  }

  @Override
  public String getRequestURI() {
    return requestURI;
  }

  @Override
  public StringBuffer getRequestURL() {
    return null;
  }

  @Override
  public String getServletPath() {
    return null;
  }

  @Override
  public HttpSession getSession(boolean b) {
    return null;
  }

  @Override
  public HttpSession getSession() {
    return null;
  }

  @Override
  public boolean isRequestedSessionIdValid() {
    return false;
  }

  @Override
  public boolean isRequestedSessionIdFromCookie() {
    return false;
  }

  @Override
  public boolean isRequestedSessionIdFromURL() {
    return requestedSessionURL;
  }

  @Override
  public boolean isRequestedSessionIdFromUrl() {
    return isRequestedSessionIdFromURL();
  }

  @Override
  public boolean authenticate(HttpServletResponse httpServletResponse)
      throws IOException, ServletException {
    return false;
  }

  @Override
  public void login(String s, String s1) throws ServletException {}

  @Override
  public void logout() throws ServletException {}

  @Override
  public Collection<Part> getParts() throws IOException, ServletException {
    return null;
  }

  @Override
  public Part getPart(String s) throws IOException, ServletException {
    return null;
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
    return contentLength;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    if (reader != null) {
      throw new IllegalStateException("getInputStream has been called");
    }
    if (stream == null) {
      stream = createInputStream();
    }
    return (stream);
  }

  @Override
  public String getParameter(String name) {
    parseParameters();
    String[] values = parameters.get(name);
    return Optional.ofNullable(values).map(arr -> arr[0]).orElse(null);
  }

  @Override
  public Enumeration<String> getParameterNames() {
    parseParameters();
    return (new Enumerator<>(parameters.keySet()));
  }

  @Override
  public String[] getParameterValues(String name) {
    parseParameters();
    return parameters.get(name);
  }

  @Override
  public Map<String, String[]> getParameterMap() {
    parseParameters();
    return this.parameters;
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
    return serverName;
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
