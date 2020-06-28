package server.util;

import java.util.Map;
import java.util.stream.Stream;

/**
 * @author zhout
 * @date 2020/6/11 17:05
 */
public final class RequestUtil {

  /**
   * 规范化URI/检测非法URI
   *
   * @param path URI
   * @return 规范化后的URI （null 规范化失败）
   */
  public static String normalize(String path) {
    if (path == null) {
      return null;
    }
    // 拷贝一个副本
    String normalized = path;

    // 把/%7E 或 /%7e 替换成 /~
    if (normalized.startsWith("/%7E") || normalized.startsWith("/%7e")) {
      normalized = "/~" + normalized.substring(4);
    }

    // 如果URI包含以下字符串，则停止规范化
    if ((normalized.contains("%25"))
        || (normalized.contains("%2F"))
        || (normalized.contains("%2E"))
        || (normalized.contains("%5C"))
        || (normalized.contains("%2f"))
        || (normalized.contains("%2e"))
        || (normalized.contains("%5c"))) {
      return null;
    }

    if ("/.".equals(normalized)) {
      return "/";
    }

    // 规范化斜杠
    if (normalized.indexOf('\\') >= 0) {
      normalized = normalized.replace('\\', '/');
    }
    // 如果URI不是以斜杠开头，则拼接一个斜杠
    if (!normalized.startsWith("/")) {
      normalized = "/" + normalized;
    }

    // 将双斜杠替换为单斜杠
    while (true) {
      int index = normalized.indexOf("//");
      if (index < 0) {
        break;
      }
      normalized = normalized.substring(0, index) + normalized.substring(index + 1);
    }

    // 将 "/./" 替换为单斜杠
    while (true) {
      int index = normalized.indexOf("/./");
      if (index < 0) {
        break;
      }
      normalized = normalized.substring(0, index) + normalized.substring(index + 2);
    }

    // 把 "/../" 替换为单斜杠
    while (true) {
      int index = normalized.indexOf("/../");
      if (index < 0) {
        break;
      }
      // 试图使用URI做路径跳转，判断为非法请求
      if (index == 0) {
        return null;
      }
      int index2 = normalized.lastIndexOf('/', index - 1);
      normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
    }

    // "/..." 也判定为非法请求
    if (normalized.contains("/...")) {
      return null;
    }
    return normalized;
  }

  /**
   * 解析请求头的字符编码
   *
   * @param contentType 请求头字符编码
   */
  public static String parseCharacterEncoding(String contentType) {
    if (contentType == null) {
      return null;
    }
    int start = contentType.indexOf("charset=");
    if (start < 0) {
      return null;
    }
    String encoding = contentType.substring(start + 8);
    int end = encoding.indexOf(';');
    if (end >= 0) {
      encoding = encoding.substring(0, end);
    }
    encoding = encoding.trim();
    if ((encoding.length() > 2) && (encoding.startsWith("\"")) && (encoding.endsWith("\""))) {
      encoding = encoding.substring(1, encoding.length() - 1);
    }
    return encoding.trim();
  }

  /**
   * 解析请求参数
   *
   * @param map Request对象中的参数map
   * @param params 解析前的参数
   * @param encoding 编码
   */
  public static void parseParameters(ParameterMap map, String params, String encoding) {
    if (StringUtil.isBlank(params)) {
      return;
    }
    String[] paramArray = params.split("&");
    if (ArrayUtil.isEmpty(paramArray)) {
      return;
    }
    Stream.of(paramArray)
        .forEach(
            param -> {
              String[] splitParam = param.split("=");
              String name = splitParam[0];
              String value = splitParam[1];
              putMapEntry(
                  map, StringUtil.urlDecode(name, encoding), StringUtil.urlDecode(value, encoding));
            });
  }

  /**
   * 将key和value添加进map中
   *
   * @param map Map
   * @param name key
   * @param value value
   */
  private static void putMapEntry(Map<String, String[]> map, String name, String value) {
    String[] newValues;
    String[] oldValues = map.get(name);
    if (oldValues == null) {
      newValues = new String[1];
      newValues[0] = value;
    } else {
      newValues = new String[oldValues.length + 1];
      System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
      newValues[oldValues.length] = value;
    }
    map.put(name, newValues);
  }
}
