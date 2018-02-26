package org.zicat.common.utils.test.ds.collection;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.zicat.common.utils.ds.collection.Lists;

public class TestLists {
	
	@Test
	public void test() {
		
		List<String> list = null;
		Lists.duplicateRemval(list);
		Assert.assertTrue(list == null);
		list = new ArrayList<>();
		Lists.duplicateRemval(list);
		Assert.assertTrue(list.isEmpty());
		
		list.add("1");
		Lists.duplicateRemval(list);
		Assert.assertTrue(list.size() == 1);
		Assert.assertTrue(list.get(0).equals("1"));
		
		list.add("-1");
		list.add("0");
		list.add("1");
		list.add("2");
		list.add("-1");
		Lists.duplicateRemval(list);
		Assert.assertTrue(list.size() == 4);
		Assert.assertTrue(list.get(0).equals("1"));
		Assert.assertTrue(list.get(1).equals("-1"));
		Assert.assertTrue(list.get(2).equals("0"));
		Assert.assertTrue(list.get(3).equals("2"));
	}
}
