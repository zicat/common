package org.zicat.common.config.listener;

import org.zicat.common.config.AbstractConfig;

/**
 * 
 * @author zicat
 *
 * @param <C>
 */
public abstract class BaseConfigListener<C extends AbstractConfig<?, ?>> implements AbstractConfigListener<C> {

	@Override
	public void init(C config) throws Exception {
		onModify(config);
	}
}
