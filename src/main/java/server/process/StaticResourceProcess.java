package server.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.carrier.Request;
import server.carrier.Response;

import java.io.IOException;

/**
 * @author zhout
 * @date 2020/6/10 15:46
 */
public class StaticResourceProcess {

  private static final Logger LOGGER = LoggerFactory.getLogger(StaticResourceProcess.class);

  /**
   * 执行静态资源处理
   *
   * @param request request
   * @param response response
   * @throws IOException IO异常
   */
  public void process(Request request, Response response) throws IOException {
    LOGGER.debug("Start Process static resource...");
    response.accessStaticResources();
  }
}
