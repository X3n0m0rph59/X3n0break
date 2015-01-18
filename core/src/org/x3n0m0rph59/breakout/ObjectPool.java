package org.x3n0m0rph59.breakout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ObjectPool<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6841904461714168067L;
	
	private List<T> list = new ArrayList<T>();
	private Class<T> objectClass;
			
	public ObjectPool(Class<T> cls) {
		this.objectClass = cls;
	}
		
	public T get() throws InstantiationException, IllegalAccessException {		
		if (!list.isEmpty()) {
			final T o = list.get(0);
			list.remove(0);
			
			return o;
		} else {
			final T o = objectClass.newInstance();						
			
			return o;
		}
	}
	
	public void put(T o) {
		if (o instanceof Poolable)
			((Poolable) o).resetState();
		
		list.add(o);
	}
	
	public void clear() {		
		for (final T o: list) {
			if (o instanceof Poolable)
				((Poolable) o).dispose();
		}
		
		list.clear();
	}
	
	public List<T> getList() {
		return list;
	}
}
