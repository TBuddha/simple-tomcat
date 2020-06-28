package server.util;

/**
 * @author zhout
 * @date 2020/6/28 15:51
 */
import java.util.HashMap;
import java.util.Map;

/**
 * Request参数Map，一个增强版的HashMap 内置布尔值锁字段，如果锁字段为真，则Map为只读状态 其他功能均与HashMap一致
 *
 * @author undefind
 */
public final class ParameterMap extends HashMap<String, String[]> {

  public ParameterMap() {
    super();
  }

  public ParameterMap(int initialCapacity) {
    super(initialCapacity);
  }

  public ParameterMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }

  public ParameterMap(Map<String, String[]> map) {
    super(map);
  }

  /** 此参数表示Map的锁定状态 */
  private boolean locked = false;

  /** 返回map的锁定状态 */
  public boolean isLocked() {
    return (this.locked);
  }
  /**
   * 设置Map的锁定状态
   *
   * @param locked 锁定状态
   */
  public void setLocked(boolean locked) {
    this.locked = locked;
  }
  /**
   * 清除Map中的所有映射
   *
   * @exception IllegalStateException 如果Map为只读时
   */
  @Override
  public void clear() {
    if (locked) {
      throw new IllegalStateException("ParameterMap is locked,clear map is fail!");
    }
    super.clear();
  }

  /**
   * 将参数名/参数内容放入map中，如果map已存在该参数名，则原先参数值会被替换
   *
   * @param key 参数名
   * @param value 参数值数组
   * @return 已添加的参数值
   */
  @Override
  public String[] put(String key, String[] value) {
    if (locked) {
      throw new IllegalStateException("ParameterMap is locked,put map is fail!");
    }
    return (super.put(key, value));
  }

  /**
   * 将参数的内容全部拷贝进map中
   *
   * @param map 被拷贝的参数
   */
  @Override
  public void putAll(Map<? extends String, ? extends String[]> map) {
    if (locked) {
      throw new IllegalStateException("ParameterMap is locked,putAll map is fail!");
    }
    super.putAll(map);
  }

  /**
   * 删除参数名和参数内容
   *
   * @param key 参数名
   * @return 删除的参数内容
   */
  @Override
  public String[] remove(Object key) {
    if (locked) {
      throw new IllegalStateException("ParameterMap is locked,remove map is fail!");
    }
    return (super.remove(key));
  }
}
