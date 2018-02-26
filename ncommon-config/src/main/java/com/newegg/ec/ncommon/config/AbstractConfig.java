package com.newegg.ec.ncommon.config;

import java.util.LinkedList;
import java.util.List;

import com.newegg.ec.ncommon.config.listener.AbstractConfigListener;
import com.newegg.ec.ncommon.config.schema.Schema;

/**
 * 
 * @author lz31
 * @param <S>
 * @param <T>
 */
public abstract class AbstractConfig<S, T> extends Config<S, T> {
	
	protected final List<AbstractConfig<?, T>> children = new LinkedList<>();
	protected final AbstractConfig<?, T> parentConfig;
	
	public AbstractConfig(AbstractConfig<?, T> parentConfig, S source) {
		
		super(source);
		
		if(equals(parentConfig))
			throw new IllegalArgumentException("parent config is same with this config");
		
		this.parentConfig = parentConfig;
		if(parentConfig != null)
			parentConfig.addChild(this);
	}

	@Override
	public T newInstance() throws Exception {
		return newSelfInstance();
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	private T newSelfInstance() throws Exception {
		
		T tmpInstance = super.instance;
		super.instance = newInstance(parentConfig == null? null: parentConfig.getInstance());
		super.oldInstance = tmpInstance;
		return getInstance();
	}
	
	/**
	 * create instance, if modified, create child instance and modify
	 * @param listener
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <C extends AbstractConfig<?, ?>> T newInstanceAndNotify(AbstractConfigListener<C> listener) throws Exception {
		
		newSelfInstance();
		boolean isModified = isModified();
		if(listener != null && isModified)
			listener.onModify((C)this);
		
		if(isModified)
			newChildrenInstanceAndNotify(listener);
		return getInstance();
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	private <C extends AbstractConfig<?, ?>> void newChildrenInstanceAndNotify(AbstractConfigListener<C> listener) throws Exception {
		
		for(AbstractConfig<?, ?> child: children) {
			child.newInstanceAndNotify(listener);
		}
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	protected abstract T newInstance(T parentInstance) throws Exception;
	
	/**
	 * 
	 * @param childConfig
	 */
	protected void addChild(AbstractConfig<?, T> childConfig) {
		
		if(childConfig == null)
			return;
		children.add(childConfig);
	}
	
	/**
	 * 
	 * @param schema
	 * @param stream
	 * @return
	 * @throws Exception
	 */
	protected <Stream> T createInstanceBySchema(Schema<Stream, T> schema, Stream stream) throws Exception {
		return parentConfig == null? schema.unmarshal(stream): schema.unmarshal(parentConfig.getInstance(), stream);
	}
}
