package org.zicat.common.config.listener;

import java.util.List;

import org.zicat.common.config.AbstractConfig;

/**
 * 
 * @author lz31
 *
 * @param <C>
 */
public class ComboConfigListener<C extends AbstractConfig<?, ?>> extends BaseConfigListener<C> {
	
	private final List<AbstractConfigListener<C>> listeners;
	
	public ComboConfigListener(List<AbstractConfigListener<C>> listeners) {
		
		if(listeners == null || listeners.isEmpty())
			throw new IllegalArgumentException("listenerList is null or empty");
		
		this.listeners = listeners;
	}

	@Override
	public void onModify(C config) throws Exception {
		
		for(AbstractConfigListener<C> listener: listeners) {  //serial call
			listener.onModify(config);
		}
	}
}
