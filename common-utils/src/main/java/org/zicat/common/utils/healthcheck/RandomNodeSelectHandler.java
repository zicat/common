package org.zicat.common.utils.healthcheck;

import java.util.List;
import java.util.Random;

/**
 * 
 * @author lz31
 *
 * @param <T>
 */
public class RandomNodeSelectHandler<T, P> implements NodeSelectHandler<T, P> {
	
	
	final static Random DEFAULT = new Random();
	private Random random = DEFAULT;
	
	public RandomNodeSelectHandler(Random random) {
		this.random = random;
	}
	
	public RandomNodeSelectHandler() {
	}
	
	@Override
	public T select(final List<T> healthNodes, P payLoad) {

		if(healthNodes == null || healthNodes.isEmpty())
			return null;
		
		return healthNodes.get(random.nextInt(healthNodes.size()));
	}

	@Override
	public void nodeChanged(List<T> allNodes) {
		
	}
}
