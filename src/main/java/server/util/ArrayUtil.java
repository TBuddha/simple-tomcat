package server.util;

/**
 * @author zhout
 * @date 2020/6/9 15:12
 */
public class ArrayUtil {

  public static final int BUFFER_SIZE = 1024;

  /**
   * 获取一个缓存byte数组
   *
   * @return 获取结果
   */
  public static byte[] generatorCache() {
    return new byte[BUFFER_SIZE];
  }
}
