package server.util;

/**
 * @author zhout
 * @date 2020/6/28 15:43
 */
import java.util.*;

/**
 * 由于HttpServletRequest接口中关于params/headers/attributes等方法实现都会使用到<code>Enumeration</code>
 * 这个类似枚举的接口，在这里做个适配，使得我们的params/headers/attributes的集合能够正常适配接口
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.2 $ $Date: 2001/07/22 20:25:13 $
 */
public final class Enumerator<E> implements Enumeration {

  public Enumerator(Collection<E> collection) {
    this(collection.iterator());
  }

  public Enumerator(Iterator<E> iterator) {
    super();
    this.iterator = iterator;
  }

  public Enumerator(Map<Object, E> map) {
    this(map.values().iterator());
  }

  private Iterator<E> iterator;

  @Override
  public boolean hasMoreElements() {
    return (iterator.hasNext());
  }

  @Override
  public E nextElement() throws NoSuchElementException {
    return (iterator.next());
  }
}
