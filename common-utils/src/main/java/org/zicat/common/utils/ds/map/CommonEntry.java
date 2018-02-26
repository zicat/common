package org.zicat.common.utils.ds.map;

import java.util.Map.Entry;

/**
 * Created by lz31 on 9/28/2016.
 */
public class CommonEntry<K, V> implements Entry<K, V> {

    private K k;
    private V v;

    /**
     *
     * @param k
     * @param v
     */
    public CommonEntry(K k, V v) {
    	
    	if(k == null)
    		throw new NullPointerException("k is null");
    	
    	if(v == null)
    		throw new NullPointerException("v is null");
    	
        this.k = k;
        this.v = v;
    }
    
    @Override
    public int hashCode() {
    	return toString().hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
    	
    	if(this == obj)
    		return true;
    	
    	if(obj == null)
    		return false;
    	
    	if(obj instanceof CommonEntry) {
    		CommonEntry<?, ?> other = (CommonEntry<?, ?>) obj;
    		return other.k.equals(this.k) && other.v.equals(this.v);
    	}
    	
    	return false;
    }
    
    @Override
    public String toString() {
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append(k);
    	sb.append("@");
    	sb.append(v);
    	return sb.toString();
    }

    /**
     * get key
     * @return
     */
    @Override
    public K getKey() {
        return k;
    }

    /**
     * get value
     * @return
     */
    @Override
    public V getValue() {
        return v;
    }

    /**
     * set new value and return old value
     * @param value
     * @return
     */
    @Override
    public V setValue(V value) {
        V old = this.v;
        this.v = value;
        return old;
    }
}
