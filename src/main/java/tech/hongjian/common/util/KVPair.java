package tech.hongjian.common.util;

/**
 * @author xiahongjian 
 * @time   2018-04-11 15:21:45
 *
 */
public class KVPair {
	private Object key;
	private Object value;
	
	public KVPair() {}
	
	public KVPair(Object key, Object value) {
		this.key = key;
		this.value = value;
	}
	
	public Object getKey() {
		return key;
	}
	public void setKey(Object key) {
		this.key = key;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
}
