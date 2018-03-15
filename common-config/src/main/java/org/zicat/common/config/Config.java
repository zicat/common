package org.zicat.common.config;

/**
 * 
 * @author lz31
 *
 * @param <T>
 */
public abstract class Config<S, T> {

	protected final S source;
	protected volatile T instance = null;
	protected volatile T oldInstance = null;

	public Config(S source) {

		if (source == null)
			throw new NullPointerException("source is null");

		this.source = source;
	}

	/**
	 * create a new instance getInstance() method will get the instance after call
	 * newInstance() method
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract T newInstance() throws Exception;

	/**
	 * read the instance from cache
	 * 
	 * @return
	 */
	public T getInstance() {
		return instance;
	}

	/**
	 * 
	 * @return
	 */
	public final S getSource() {
		return source;
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof Config))
			return false;

		Config<?, ?> config = (Config<?, ?>) obj;
		return getSource().equals(config.getSource());
	}

	@Override
	public int hashCode() {
		return source.hashCode();
	}

	/**
	 * 
	 * @return
	 */
	protected boolean isModified() {

		if (oldInstance == instance)
			return false;

		if (oldInstance == null || (!oldInstance.equals(instance)))
			return true;

		oldInstance = instance; // if same, move pointer
		return false;
	}
}
