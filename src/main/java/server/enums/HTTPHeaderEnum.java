package server.enums;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * HTTP请求头key枚举
 *
 * @author zhout
 * @date 2020/6/12 16:41
 */
public enum HTTPHeaderEnum {

  /*cookie*/
  COOKIE("cookie"),
  /*content-length*/
  CONTENT_LENGTH("content-length"),
  /*content-type*/
  CONTENT_TYPE("content-type");

  private String desc;

  HTTPHeaderEnum(String desc) {
    this.desc = desc;
  }

  public String getDesc() {
    return desc;
  }

  public static Optional<HTTPHeaderEnum> parse(String value) {
    return Stream.of(values()).filter(header -> header.getDesc().equals(value)).findAny();
  }
}
