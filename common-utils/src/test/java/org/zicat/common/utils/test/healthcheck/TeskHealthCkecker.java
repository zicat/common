package org.zicat.common.utils.test.healthcheck;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.zicat.common.utils.healthcheck.ConsistentHashNodeSelectHandler;
import org.zicat.common.utils.healthcheck.HealthChecker;
import org.zicat.common.utils.healthcheck.NodeSelectHandler;
import org.zicat.common.utils.healthcheck.StartMode;
import org.zicat.common.utils.healthcheck.NodePing;

/**
 * 
 * @author lz31
 *
 */
public class TeskHealthCkecker {
	
	@Test
	public void test3() throws NoSuchAlgorithmException, InterruptedException {
		
		List<TestServerTransaction> transactions = new ArrayList<>();
		ConsistentHashNodeSelectHandler<TestServerTransaction> handler = new ConsistentHashNodeSelectHandler<>(transactions);
		//RandomNodeSelectHandler<TestServerTransaction, String> handler = new RandomNodeSelectHandler<>();
		//HashNodeSelectHandle<TestServerTransaction> handler = new HashNodeSelectHandle<>();
		
		final HealthChecker<TestServerTransaction, String> checker = new HealthChecker<>("test", transactions, new TestServerPingImpl(), handler);
		checker.start();
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				for(int i = 0; i < 10; i++) {
					checker.addNode(new TestServerTransaction[]{new TestServerTransaction(i).turnOn()});
				}
			}
		});
		t.start();
		
		Thread t2 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for(int i = 0; i < 5; i++) {
					checker.removeNode(new TestServerTransaction[]{new TestServerTransaction(i)});
				}
			}
		});
		t2.start();
		try {
			Thread.sleep(40);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i < 20; i ++) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TestServerTransaction ss  = checker.getGoodServer(i + "sasadfasdfdfas");
			if(ss != null)
				System.out.println("Node Id " + ss.getI() + "======>" + i);
			else {
				System.out.println(i);
			}
		}
		
		Thread.sleep(1000);
		System.out.println("=============================");
		for(int i = 0; i < 20; i ++) {
			TestServerTransaction ss  = checker.getGoodServer(i + "sasadfasdfdfas");
			if(ss != null)
				System.out.println("Node Id " + ss.getI() + "======>" + i);
			else {
				System.out.println(i);
			}
		}
		
		for(int i = 10; i < 20; i ++) {
			transactions.add(new TestServerTransaction(i).turnOn());
		}
		checker.resetNodes(transactions.toArray(new TestServerTransaction[]{}));
		Thread.sleep(1000);
		System.out.println("=============================");
		for(int i = 0; i < 20; i ++) {
			TestServerTransaction ss  = checker.getGoodServer(i + "sasadfasdfdfas");
			if(ss != null)
				System.out.println("Node Id " + ss.getI() + "======>" + i);
			else {
				System.out.println(i);
			}
		}
	}
	
	@Test
	public void test() throws InterruptedException, NoSuchAlgorithmException {
		
		List<TestServerTransaction> transactions = new ArrayList<>();
		for(int i = 0; i < 3; i++) {
			transactions.add(new TestServerTransaction(i));
		}
		ConsistentHashNodeSelectHandler<TestServerTransaction> handler = new ConsistentHashNodeSelectHandler<>(transactions);
		
		HealthChecker<TestServerTransaction, String> checker = new HealthChecker<>("test", transactions, new TestServerPingImpl(), handler);
		checker.start(StartMode.Full);
		Assert.assertTrue(checker.getGoodServer("asdfasf") == null);
		Assert.assertTrue(checker.getAllDeathServer().size() == transactions.size());
		Assert.assertTrue(checker.getAllGoodServer().isEmpty());
		
		transactions.get(2).turnOn();
		Thread.sleep(100);
		Assert.assertTrue(checker.getGoodServer("asdfasf").getI() == 2);
		Assert.assertTrue(checker.getAllDeathServer().size() == transactions.size() -1);
		Assert.assertTrue(checker.getAllGoodServer().size() == 1);
		
		transactions.get(2).turnOff();
		Thread.sleep(60);
		Assert.assertTrue(checker.getGoodServer(null) == null);
		Assert.assertTrue(checker.getAllDeathServer().size() == transactions.size());
		Assert.assertTrue(checker.getAllGoodServer().isEmpty());
		
		checker.close();
		Thread.sleep(1000);
	}
	
	public static class TestServerAchieveHandler implements NodeSelectHandler<TestServerTransaction, String> {

		@Override
		public TestServerTransaction select(List<TestServerTransaction> server, String payload) {
			Assert.assertTrue(server.size() == 1);
			return server.get(0);
		}

		@Override
		public void nodeChanged(List<TestServerTransaction> allNodes) {
			
		}
	}
	
	public static class TestServerTransaction {
		
		private int i;
		private boolean isOn = false;
		
		public TestServerTransaction(int i) {
			this.i = i;
		}
		
		public TestServerTransaction turnOn() {
			isOn = true;
			return this;
		}
		
		public TestServerTransaction turnOff() {
			isOn = false;
			return this;
		}
		
		public boolean isOn() {
			return isOn;
		}
		
		public int getI() {
			return i;
		}
		
		@Override
		public boolean equals(Object other) {
			
			if(other == null)
				return false;
			
			if(other instanceof TestServerTransaction) {
				TestServerTransaction testServerTransaction = (TestServerTransaction) other;
				return testServerTransaction.getI() == this.i;
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return toString().hashCode();
		}
		
		@Override
		public String toString() {
			return i + "";
		}
	}
	
	public static class TestServerPingImpl implements NodePing<TestServerTransaction> {
		
		public TestServerPingImpl() {
		}
		

		@Override
		public int scanIntervals() {
			return 1;
		}

		@Override
		public boolean ping(TestServerTransaction serverTransaction) throws Throwable {
			return serverTransaction.isOn();
		}
		
	}
}
