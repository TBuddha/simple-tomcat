package server.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.App;
import server.carrier.Request;
import server.carrier.Response;
import server.carrier.facade.RequestFacade;
import server.carrier.facade.ResponseFacade;
import server.enums.HttpStatusEnum;

import javax.servlet.Servlet;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author zhout
 * @date 2020/6/10 15:47
 */
public class ServletProcess {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServletProcess.class);

  private static final URLClassLoader URL_CLASS_LOADER;

  static {
    try {
      /*定位到我们的webroot/servlet/文件夹*/
      URL servletClassPath = new File(App.WEB_PROJECT_ROOT, "servlet").toURI().toURL();
      // 初始化classloader
      URL_CLASS_LOADER = new URLClassLoader(new URL[] {servletClassPath});
    } catch (Exception e) {
      LOGGER.warn("initialized servlet classloader is fail!", e);
      throw new RuntimeException(e);
    }
  }

  /**
   * 根据Request执行相应的Servlet
   *
   * @param request request对象
   * @param response response对象
   */
  public void process(Request request, Response response) throws IOException {
    // 根据请求的URI截取Servlet的名字
    String servletName = this.parseServletName(request.getUri());
    // 使用URLClassLoader加载这个Servlet并实例化
    Class servletClass;
    try {
      servletClass = URL_CLASS_LOADER.loadClass(servletName);
    } catch (ClassNotFoundException e) {
      LOGGER.info("servlet {} not found!", servletName);
      // 实例化失败则调用404页面
      response.accessStaticResources();
      return;
    }
    try {
      // 实例化这个Servlet
      Servlet servlet = (Servlet) servletClass.newInstance();
      response.getWriter().println(new String(response.responseToByte(HttpStatusEnum.OK)));
      // 调用servlet的service方法
      // servlet.service(request, response);

      // 处理缺陷
      // 使用者顶多只能将ServletRequest/ServletResponse向下转型为RequestFacade/ResponseFacade
      // 但是我们没提供getRequest()/getResponse()方法，所以它能调用的方法还是相应ServletRequest、ServletResponse接口定义的方法，
      // 这样我们内部的方法就不会被用户调用到啦~
      servlet.service(new RequestFacade(request), new ResponseFacade(response));
    } catch (Exception e) {
      LOGGER.info("Invoke Servlet {} is fail!", servletName);
    }
  }

  /**
   * 解析到用户请求的Servlet类名
   *
   * @param uri 请求URI
   * @return Servlet类名
   */
  private String parseServletName(String uri) {
    return uri.substring(uri.lastIndexOf("/") + 1);
  }
}
