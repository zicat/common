package com.newegg.ec.ncommon.utils.ds.collection;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Lists {

	public static <T> void duplicateRemval(List<T> data) {

		if (data == null || data.size() <= 1)
			return;

		Set<T> set = new LinkedHashSet<T>(data);
		data.clear();
		Iterator<T> it = set.iterator();
		while (it.hasNext()) {
			data.add(it.next());
		}
	}
}
