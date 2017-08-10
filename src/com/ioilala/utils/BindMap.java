package com.ioilala.utils;

import java.util.HashMap;

/**
 * key和value都唯一，且可以通过value获取对应的key的Map
 * 目前不支持并发操作
 * @author hust_wsh
 *
 * @param <K>
 * @param <V>
 */
public class BindMap<K,V> extends HashMap<K, V> {
	/**
	 * 根据value移除key
	 * @param value
	 */
	public void removeKeyByValue(Object value)
	{
		for(Object key: keySet())
		{
			if(get(key)==value)
			{
				remove(key);
				break;
			}
		}
	}
	
//	public Set<V> valueSet()
//	{
//		Set
//	}
	
	
	public K getKeyByValue(Object value)
	{
		for(K key:keySet())
		{
			if(get(key)==value)
			{
				return key;
			}
		}
		return null;
	}
	
	public V put(K key,V value) throws RuntimeException
	{
		for(V val:values())
		{
			if(val==value)
			{
				throw new RuntimeException("BindMap的value不允许重复");
			}
		}
		return super.put(key, value);
	}
}