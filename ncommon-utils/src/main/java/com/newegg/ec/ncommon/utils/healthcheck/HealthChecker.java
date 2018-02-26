package com.newegg.ec.ncommon.utils.healthcheck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * Health Check And Manager List<T>
 * @author lz31
 *
 * @param <T>
 */
public class HealthChecker<T, P> {
	
	private final List<T> nodes;
	private final NodePing<T> nodePing;
	private final NodeSelectHandler<T, P> handler;
	
	private Thread t;
	private final String name;
	private final AtomicBoolean started = new AtomicBoolean(false);
	private final AtomicBoolean isClosed = new AtomicBoolean(false);
	
	private final List<T> healthNodes;
	private final List<T> deathNodes;
	
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
	private final WriteLock writeLock = lock.writeLock();
	private final ReadLock readLock = lock.readLock();
	
	public HealthChecker(String name, List<T> nodes, NodePing<T> nodePing, NodeSelectHandler<T, P> handler) {
		
		if(name == null)
			throw new NullPointerException("name is null");
		
		if(nodes == null)
			throw new NullPointerException("nodes is null");
		
		if(nodePing == null)
			throw new NullPointerException("node ping is null");
		
		if(handler == null)
			throw new NullPointerException("handler is null");
		
		this.name = name;
		this.nodes = new ArrayList<>(nodes);
		this.nodePing = nodePing;
		this.handler = handler;
		this.healthNodes = new ArrayList<>(nodes.size());
		this.deathNodes = new ArrayList<>(nodes.size());
	}
	
	public void start(StartMode startMode) {
		
		if(isClosed.get())
			throw new IllegalStateException("health check is closed");
		
		if(started.get())
			return;
		
		synchronized(this) {
			
			if(isClosed.get())
				throw new IllegalStateException("health check is closed");
			
			if(started.get())
				return;
			
			checkTask(startMode);
			
			Runnable runnable  = new Runnable(){
				@Override
				public void run() {
					while(!isClosed.get()) {
						try {
							Thread.sleep(nodePing.scanIntervals());
							checkTask(StartMode.Full);
						} catch (InterruptedException e) {
							break;
						}
					}
				}
			};
			t = new Thread(runnable);
			t.setName(name);
			t.setDaemon(true);
			t.start();
			started.set(true);
		}
	}
	
	/**
	 * 
	 * @param allNewNodes
	 */
	public void resetNodes(T[] allNewNodes) {
		WriteLock writeLock = this.writeLock;
		writeLock.lock();
		try {
			nodes.clear();
			if(allNewNodes != null)
				nodes.addAll(Arrays.asList(allNewNodes));
			handler.nodeChanged(nodes);
		} finally {
			writeLock.unlock();
		}
	}
	
	/**
	 * 
	 * @param removeNodes
	 */
	public void removeNode(T[] removeNodes) {
		
		WriteLock writeLock = this.writeLock;
		writeLock.lock();
		try {
			boolean isUpdate = false;
			if(removeNodes != null) {
				for(T node: removeNodes) {
					if(node == null)
						continue;
					if(nodes.remove(node)) {
						healthNodes.remove(node);
						deathNodes.remove(node);
						isUpdate = true;
					}
				}
			}
			
			if(isUpdate) {
				handler.nodeChanged(nodes);
			}
		} finally {
			writeLock.unlock();
		}
	}
	
	/**
	 * 
	 * @param nodes
	 */
	public void addNode(T[] newNodes) {
		
		List<T> tmp = new ArrayList<>();
		WriteLock writeLock = this.writeLock;
		writeLock.lock();
		try {
			boolean isUpdate = false;
			if(newNodes != null) {
				for(T newNode: newNodes) {
					if(newNode == null)
						continue;
					if(!nodes.contains(newNode)) {
						nodes.add(newNode);
						tmp.add(newNode);
						isUpdate = true;
					}
				}
				
			}
			
			if(isUpdate) {
				handler.nodeChanged(nodes);
			}
		} finally {
			writeLock.unlock();
		}
		
		for(T node: tmp) {
			boolean isHealth = false;
			try {
				isHealth = nodePing.ping(node);
			} catch (Throwable e) {
				isHealth = false;
			}
			if(isHealth) {
				addToGoodNode(node);
			}
		}
	}
	
	/**
	 * 
	 */
	private void checkStatus() {
		
		if(isClosed.get())
			throw new IllegalStateException("health check is closed");
		
		if(!started.get())
			throw new IllegalStateException("Please call start() first");
	}
	
	/**
	 * 
	 */
	public void start() {
		start(StartMode.Quict);
	}
	
	/**
	 * 
	 */
	public synchronized void close() {
		this.isClosed.set(true);
		this.t.interrupt();
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public T getGoodServer(P payLoad) {
		checkStatus();
		return getServer(healthNodes, payLoad);
	}
	
	/**
	 * 
	 * @param handler
	 * @param healthNodes
	 * @return
	 */
	private T getServer(List<T> healthNodes, P payLoad) {
		
		ReadLock readLock = this.readLock;
		readLock.lock();
		try {
			if(healthNodes == null || healthNodes.isEmpty()) {
				return null;
			}
			return handler.select(healthNodes, payLoad);
		} finally {
			readLock.unlock();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public List<T> getAllGoodServer() {
		checkStatus();
		return copy(healthNodes);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<T> getAllDeathServer() {
		checkStatus();
		return copy(deathNodes);
	}
	
	/**
	 * 
	 * @param server
	 * @return
	 */
	private List<T> copy(List<T> server) {
		
		ReadLock readLock = this.readLock;
		readLock.lock();
		try {
			List<T> result = new ArrayList<>(server);
			return result;
		} finally {
			readLock.unlock();
		}
	}
	
	/**
	 * 
	 * @param node
	 */
	private void addToGoodNode(T node) {
		addToNode(node, healthNodes);
	}
	
	/**
	 * 
	 * @param node
	 */
	private void addToDeathNode(T node) {
		addToNode(node, deathNodes);
	}
	
	/**
	 * 
	 * @param node
	 */
	private void removeFromGoodNode(T node) {
		removeFromNode(node, healthNodes);
	}
	
	/**
	 * 
	 * @param node
	 */
	private void removeFromDeathNode(T node) {
		removeFromNode(node, deathNodes);
	}
	
	/**
	 * 
	 * @param node
	 * @param nodes
	 */
	private void addToNode(T node, List<T> nodes) {
		
		ReadLock readLock = this.readLock;
		readLock.lock();
		try {
			if(nodes.contains(node)) {
				return;
			}
		} finally {
			readLock.unlock();
		}
		
		WriteLock writeLock = this.writeLock;
		writeLock.lock();
		try {
			if(!nodes.contains(node)) {
				nodes.add(node);
			}
		} finally {
			writeLock.unlock();
		}
	}
	
	/**
	 * 
	 * @param node
	 * @param nodes
	 */
	private void removeFromNode(T node, List<T> nodes) {
		
		ReadLock readLock = this.readLock;
		readLock.lock();
		try {
			if(!nodes.contains(node)) {
				return;
			}
		} finally {
			readLock.unlock();
		}
		
		WriteLock lock = this.writeLock;
		lock.lock();
		try {
			if(nodes.contains(node)) {
				nodes.remove(node);
			}
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * 
	 */
	private void checkTask(StartMode startMode) {
		
		ReadLock readLock = this.readLock;
		List<T> nodes = null;
		try {
			readLock.lock();
			nodes = new ArrayList<>(this.nodes);
		} finally {
			readLock.unlock();
		}
		
		if(nodes == null || nodes.isEmpty())
			return;
		
		for(T node: nodes) {
			
			if(node == null)
				continue;
			
			try {
				if(startMode == StartMode.Quict || nodePing.ping(node)) {
					addToGoodNode(node);
					removeFromDeathNode(node);
					if(startMode == StartMode.LastLeastOne) {
						break;
					}
				} else {
					addToDeathNode(node);
					removeFromGoodNode(node);
				}
			} catch (Throwable e) {
				addToDeathNode(node);
				removeFromGoodNode(node);
			}
		}
	}
}
