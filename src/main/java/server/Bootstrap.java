package server;

import server.connector.HttpConnector;

/**
 * 启动模块目前我们没有多大工作，只是启动HttpConnector
 * @author zhout
 * @date 2020/6/11 15:42
 */
public final class Bootstrap {

  public static void main(String[] args){
    //启动连接器等待连接
    new HttpConnector().start();
  }
}
