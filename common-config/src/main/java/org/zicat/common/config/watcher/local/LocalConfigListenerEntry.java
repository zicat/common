package org.zicat.common.config.watcher.local;

import org.zicat.common.config.LocalConfig;
import org.zicat.common.config.listener.AbstractConfigListener;
import org.zicat.common.utils.ds.map.CommonEntry;

/**
 * 
 * @author lz31
 *
 */
public class LocalConfigListenerEntry extends CommonEntry<LocalConfig<?>, AbstractConfigListener<LocalConfig<?>>> {
	
	private static long DEFAULT_DELAY_Time = 5;
	
	private long lasteditDate;
	private long delayTime = DEFAULT_DELAY_Time;
	
	public LocalConfigListenerEntry(LocalConfig<?> k, AbstractConfigListener<LocalConfig<?>> v) {
		this(k, v, DEFAULT_DELAY_Time);
	}
	
	public LocalConfigListenerEntry(LocalConfig<?> k, AbstractConfigListener<LocalConfig<?>> v, long delayTime) {
		super(k, v);
		this.lasteditDate = k.getFile().lastModified();
		this.delayTime = delayTime < 0? DEFAULT_DELAY_Time: delayTime;
	}
	
	/**
	 * 
	 * @return
	 */
	public LocalConfig<?> getLocalConfig() {
		return getKey();
	}
	
	/**
	 * 
	 * @return
	 */
	public AbstractConfigListener<LocalConfig<?>>  getConfigListener() {
		return getValue();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void listenerCallback() throws Exception {
		
		fileEmptyCheck(3);
		long newDate = getLocalConfig().getFile().lastModified();
		if(newDate > lasteditDate + delayTime) {
			lasteditDate = newDate;
			getLocalConfig().newInstanceAndNotify(getConfigListener());
		}
	}
	
	private void fileEmptyCheck(int tryCount) throws InterruptedException {
		
		int times = 0;
		while(times < tryCount) {
			if(getLocalConfig().getFile().length() != 0)
				break;
			times++;
			Thread.sleep(2 * times);
		}
	}
}
