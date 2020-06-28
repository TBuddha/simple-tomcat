package server.http.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.http.carrier.HttpRequest;
import server.http.carrier.HttpResponse;

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
   * @param httpRequest request
   * @param httpResponse response
   * @throws IOException IO异常
   */
  public void process(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
    LOGGER.debug("Start Process static resource...");
    httpResponse.accessStaticResources();
  }
}
