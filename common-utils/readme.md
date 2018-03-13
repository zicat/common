# Common Utils

Common Utils provides usefull api used pure Java 8 include [FileBlockingQueue](#fileblockingqueue), [Producer Consumer](#producer-consumer), [HealthChecker](#healthchecker) etc.<br />

## FileBlockingQueue

FileBlockingQueue implements AbstractQueue, support transaction.  <br />
Data which pushed to FileBlockingQueue flush to disk and never lost even if unexpected  exit of the program.

### FileBlockingQueue Example:
#### 1. Create FileBlockingQueue Instance
```java
SerializableHandler<String> handler = new StringSerializableHandler();
File file = File.createTempFile("test", ".txt");
//  segment factory created params:
//	1. segment.index file 
//  2. segment.data length(byte) 
//  3. Serializable handler 
//  4. Whether cleanup old data
SegmentFactory<String> segmentFactory = new SegmentFactory<>(file, 102400, handler, true);
//create FileBlockingQueue by segment factory
FileBlockingQueue<String> fileBlockingQueue = new FileBlockingQueue<>(segmentFactory);
```
#### 2. Write data to FileBlockingQueue
```java
//FileBlockingQueue add String data(Arbitrary data format that implements SerializableHandler)
//never blocking & thread safe
fileBlockingQueue.add("testing data")
```
#### 3. Read data from FileBlockingQueue
```java
// unblock, return null if no data & thread safe
fileBlockingQueue.poll();
// block 10 milliseconds if no data and return null & thread safe
fileBlockingQueue.poll(10, TimeUnit.MILLISECONDS);
// block until data coming & thread safe
fileBlockingQueue.take();
```
#### 4. Set Transaction from read
```java
//set transaction, note: set transaction not support multi thread
fileBlockingQueue.setTransaction();
try {
	String data = fileBlockingQueue.poll();
	//operation data by business
	System.out.println(data);
	fileBlockingQueue.commit(); //commit data
} catch(Exception e) {
	fileBlockingQueue.rollback();//roll back data
}
```

## Producer Consumer
Producer Consumer Model is useful in lots of business
### Class Introduction
#### 1. Producer
Producer produce data to blocking queue.<br />
Define:
```java
public class Producer<E> {
	protected final BlockingQueue<E> blockingQueue;
	public Producer(BlockingQueue<E> blockingQueue) {
		if(blockingQueue == null)
			throw new NullPointerException("blocking queue is null");		
		this.blockingQueue = blockingQueue;
	}	
}
```
#### 2. Consumer
Consumer consume data from blocking queue.<br />
Define:
```java
public class Consumer<E> {
	
	protected final BlockingQueue<E> blockingQueue;
	protected final long consumerMaxIntervalTimeMillis;
	protected final int consumerMaxCount;
	protected final int threadCount;
	protected final ExecutorService service;
	protected final long sleepTime;
	protected final AtomicBoolean started = new AtomicBoolean(false);
	protected final AtomicBoolean closed = new AtomicBoolean(false);
	protected final ReentrantLock lock = new ReentrantLock();
	protected final Condition closeCondition = lock.newCondition();
	
	public Consumer(BlockingQueue<E> blockingQueue, 
				long consumerMaxIntervalTimeMillis, int consumerMaxCount, int threadCount) {
		
		if(blockingQueue == null)
			throw new NullPointerException("blocking queue is null");
		
		if(consumerMaxIntervalTimeMillis <= 0)
			throw new IllegalArgumentException("consumerMaxIntervalTimeMillis must more than 0");
		
		if(consumerMaxCount <= 0)
			throw new IllegalArgumentException("consumerMaxCount must more than 0");
		
		if(threadCount <= 0)
			throw new IllegalArgumentException("thread count must more than 0");
			
		
		this.blockingQueue = blockingQueue;
		this.consumerMaxIntervalTimeMillis = consumerMaxIntervalTimeMillis;
		this.consumerMaxCount = consumerMaxCount;
		this.threadCount = threadCount;
		this.service = Executors.newFixedThreadPool(threadCount + 1);
		this.sleepTime = consumerMaxIntervalTimeMillis == 1? 1: consumerMaxIntervalTimeMillis / 2;
	}
}
```
#### 3. TransactionalConsumer
TransactionalConsumer extends Consumer, use FileBlockingQueue instead of blockingQueue. <br />
FileBlockingQueue support transaction, so TransactionalConsumer support set transaction, commit & rollback.<br /> 
Define:
```java
public class TransactionalConsumer<E> extends Consumer<E> {
	
	protected FileBlockingQueue<E> blockingQueue;
	
	public TransactionalConsumer(FileBlockingQueue<E> blockingQueue, 
				long consumerMaxIntervalTimeMillis, int consumerMaxCount, int threadCount){
		
		super(blockingQueue, consumerMaxIntervalTimeMillis, consumerMaxCount, threadCount);
		this.blockingQueue = blockingQueue;
	}
}
```
### Producer & Consumer Example:
```java
//create file block queue
SegmentFactory<String> factory = new SegmentFactory<>(File.createTempFile("aaa", "txt"), 
									1024 * 1024 * 500, new StringSerializableHandler(), true);
final FileBlockingQueue<String> queue = new FileBlockingQueue<>(factory);
//create producer
Producer<String> producer = new Producer<>(queue);
//create consumer
Consumer<String> consumer = new FastTransactionalConsumer<>(queue, 100, threadCount);
final AtomicInteger offset = new AtomicInteger(0);

consumer.start(new ConsumerHandler<String>() {
	
	@Override
	public void dealException(List<String> elements, Exception e) {
		//call back exception if consumer fail
		//note: If Deal exception still throw exception 
		//      TransactionalConsumer will roll back data and reconsume again.
		System.out.println(elements + e);
	}
	
	@Override
	public void consume(List<String> elements) throws Exception {
		//write business logic
		for(String e: elements) {
			System.out.println(e);
		}
	}
});

for(int i = 0; i < total; i ++) {
	producer.product(i + ""); //product data
}
// consumer close() method will block util all producer data from blocking queue consumed.
consumer.close(); 
```
## HealthChecker
When project need to query a service data from service list, project need to check which ones that are healthy and which ones that are dead. <br />
HealthChecker maintain health nodes and dead nodes by cycle ping check <br />
HealthChecker provide multi select logic from health nodes include random, hash, ConsistentHash etc.
### Class Introduction
#### 1. NodePing
NodePing is java interface, user need to implements this interface by specific business logic <br />
Define:
```java
public interface NodePing<T> {

	/**
	 * 
	 * @param node
	 * @return
	 * @throws Throwable
	 */
	public boolean ping(T node) throws Throwable;
	
	/**
	 * 
	 * @return
	 */
	public int scanIntervals();
}
```
#### 2. NodeSelectHandler
NodeSelectHandler is java interface, be used to select which health node will be selected in health list.<br />
common utils provide implements include RandomNodeSelectHandler, HashNodeSelectHandler, ConsistentHashNodeSelectHandler<br />
Define:
```java
public interface NodeSelectHandler<T, P> {
	
	/**
	 * 
	 * @param healthNodes
	 * @param payLoad
	 * @return
	 */
	public T select(final List<T> healthNodes, P payLoad);
	
	/**
	 * 
	 * @param allNodes
	 */
	public void nodeChanged(final List<T> allNodes);
}
```
#### 3. HealthChecker
HealthChecker is the core class of Health Checker, provide api to manager nodes.<br />
Define:
```java
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
	
	public HealthChecker(String name, List<T> nodes, NodePing<T> nodePing, 
								NodeSelectHandler<T, P> handler) {
		
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
}
```
### HealthChecker Example:
```java
// Define node, not only string type, all object is enable
List<String> nodes = new ArrayList<String>();
nodes.add("http://ip1:port1/path");
nodes.add("http://ip2:port2/path");

// Choice one Select Handler implements depending on business
ConsistentHashNodeSelectHandler<String> handler = new ConsistentHashNodeSelectHandler<>(nodes);

// Create healthchecker instance
// Params: 
//	1. health checker name, type = string
//  2. nodes
//  3. NodePing Implements
//  4. NodeSelectHandler Implements
HealthChecker<String, String> checker = new HealthChecker<>("test", nodes, new NodePing<String>() {
	@Override
	public boolean ping(String node) throws Throwable {
		// coding ping logic depending on business
		return true;
	}

	@Override
	public int scanIntervals() {
		return 5000;
	}
}, handler);
// Start health check, StartMode include Quick, LastLeastOne, Full
checker.start(StartMode.Full);
// select one good server, 
// HashNodeSelectHandler & ConsistentHashNodeSelectHandler need payload param to compute hash code
checker.getGoodServer("payload1");
// close checker if not needed
checker.close();
```