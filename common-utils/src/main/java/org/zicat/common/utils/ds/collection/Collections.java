package org.zicat.common.utils.ds.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by lz31 on 2017/1/11.
 */
public class Collections {

	public static final <T> Collection<Collection<T>> subCollection(Collection<T> collection, int batchSize) {

		if (collection == null)
			return null;

		ArrayList<Collection<T>> result = new ArrayList<Collection<T>>();
		if (collection.isEmpty())
			return result;

		if (batchSize <= 0 || collection.size() < batchSize * 1.5) {
			result.add(collection);
			return result;
		}

		Iterator<T> it = collection.iterator();
		ArrayList<T> temp = new ArrayList<T>();
		while (it.hasNext()) {
			if (temp.size() < batchSize) {
				temp.add(it.next());
			} else {
				result.add(temp);
				temp = new ArrayList<T>();
			}
		}

		if (!result.contains(temp)) {
			if (temp.size() > batchSize * 0.5) {
				result.add(temp);
			} else {
				Collection<T> last = result.get(result.size() - 1);
				if (last == null) {
					result.add(temp);
				} else {
					last.addAll(temp);
				}
			}
		}
		return result;
	}

	public static final <T> List<Set<T>> subSet(Set<T> collection, int batchSize) {

		Collection<Collection<T>> subs = subCollection(collection, batchSize);
		if (subs == null)
			return null;

		List<Set<T>> result = new ArrayList<Set<T>>();
		if (subs.isEmpty())
			return result;

		for (Collection<T> c : subs) {
			Set<T> v = new HashSet<T>();
			for (T t : c) {
				v.add(t);
			}
			result.add(v);
		}
		return result;
	}
}
