package org.zicat.common.utils.healthcheck;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * 
 * @author lz31 Ketama Consistent Hash
 * @param <T>
 */
public class ConsistentHashNodeSelectHandler<T> implements NodeSelectHandler<T, String> {

	private volatile TreeMap<Integer, T> virtualNodes;
	private final int virtualNodesTimes;

	/**
	 * Message Digest is not thread safe
	 */
	private static ThreadLocal<MessageDigest> MD5 = new ThreadLocal<MessageDigest>() {
		@Override
		protected MessageDigest initialValue() {
			try {
				return MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				throw new IllegalStateException("no md5 algorythm found");
			}
		}
	};

	public ConsistentHashNodeSelectHandler(final List<T> allNodes, int virtualNodesTimes) {
		if (allNodes == null)
			throw new NullPointerException("nodes is null");

		this.virtualNodesTimes = virtualNodesTimes > 1 ? virtualNodesTimes : 1;
		this.virtualNodes = initVirtualNodes(allNodes);
	}

	public ConsistentHashNodeSelectHandler(final List<T> allNodes) {
		this(allNodes, 10);
	}

	/**
	 * Ketama Consistent Hash
	 * 
	 * @param key
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private static Integer hashCode(String key) {

		byte[] digest = getMD5(key);
		return ((digest[3] & 0xFF) << 24) | ((digest[2] & 0xFF) << 16) | ((digest[1] & 0xFF) << 8) | (digest[0] & 0xFF);
	}

	@Override
	public T select(final List<T> headlthNodes, String payLoad) {

		if (payLoad == null)
			throw new NullPointerException("consisitent hash must set the payload value, payload value must not null");

		if (headlthNodes == null || headlthNodes.isEmpty())
			return null;

		Integer hashCode = hashCode(payLoad);
		Entry<Integer, T> en = getNextEntry(hashCode);
		int retryTime = 0;
		while (en != null && !headlthNodes.contains(en.getValue())) {
			en = getNextEntry(en.getKey() + 1);
			retryTime++;
			if (retryTime > virtualNodes.size())
				break;
		}

		if (retryTime > virtualNodes.size() || en == null) {
			return headlthNodes.get(0);
		}
		return en.getValue();
	}

	/**
	 * 
	 * @param hashCode
	 * @return
	 */
	private Entry<Integer, T> getNextEntry(int hashCode) {

		Entry<Integer, T> en = virtualNodes.ceilingEntry(hashCode);
		return en == null ? virtualNodes.firstEntry() : en;
	}

	/**
	 * 
	 * @param allNodes
	 * @throws NoSuchAlgorithmException
	 */
	private TreeMap<Integer, T> initVirtualNodes(List<T> allNodes) {

		TreeMap<Integer, T> virtualNodes = new TreeMap<Integer, T>();
		int virtualNodesRealTime = virtualNodesTimes * 10;
		for (T node : allNodes) {
			for (int i = 0; i < virtualNodesRealTime; i++) {
				byte[] digest = getMD5(node + "-" + i);
				for (int h = 0; h < 4; h++) {
					Integer k = ((digest[3 + h * 4] & 0xFF) << 24) | ((digest[2 + h * 4] & 0xFF) << 16)
							| ((digest[1 + h * 4] & 0xFF) << 8) | (digest[h * 4] & 0xFF);
					virtualNodes.put(k, node);
				}
			}
		}
		return virtualNodes;
	}

	/**
	 * 
	 * @param message
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] getMD5(String message) {
		byte[] input = message.getBytes();
		return MD5.get().digest(input);
	}

	@Override
	public void nodeChanged(List<T> allNodes) {
		TreeMap<Integer, T> temp = initVirtualNodes(allNodes);
		this.virtualNodes = temp;
	}
}
