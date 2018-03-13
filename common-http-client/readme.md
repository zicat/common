# Common Http Client
Common Http Client provides async & sync、pooled http client by JAX2.1.
## Class Induction
### 1. ClientFactory
ClientFactory is a interface that createClient and destory client
```java
public interface ClientFactory {

    /**
     *
     * @return
     */
    Client createClient();

    /**
     *
     */
    void destory(Client client);
    
}
```
### 2. AbstractClientFactory
AbstractClientFactory extends ClientFactory.<br />
AsyncReducedSMTPAppender build client and maintain it.

### 3. JerseyClientFactory
JerseyClientFactory extends JerseyClientFactory, create Client by Jersey.
```java
public abstract class JerseyClientFactory extends AbstractClientFactory {

    public JerseyClientFactory(int aSynHttpThreadCount) {
    	super(aSynHttpThreadCount);
    }

    /**
     * class can implements JerseyClientFactory.class and set property and property value
     * @return
     */
    protected ClientConfig buildDefaultConfig() {
    	
    	 ClientConfig config = new ClientConfig();
         config = config.connectorProvider(newProvider());
         return config;
    }
    
    protected abstract ConnectorProvider newProvider();
    
	@Override
	protected Configuration buildConfig() {
		return buildDefaultConfig();
	}
}
```
### 4. DefaultJerseyClientFactory
DefaultJerseyClientFactory extends JerseyClientFactory, create Client by Jersey Default(JDK URLConnection).

### 5. GrizzlyJerseyClientFactory
GrizzlyJerseyClientFactory extends JerseyClientFactory, create Client by Grizzly.

### 6. JettyJerseyClientFactory
JettyJerseyClientFactory extends JerseyClientFactory, create Client by Jetty.
## Example:
### 1. A Simple Example
```java
//create client factory,note: factory is thread safe
private static final AbstractClientFactory factory = new GrizzlyJerseyClientFactory(10, 5000, 2000);

public static void main(String[] args) throws Exception {
	
	//create client with factory,note: RestfullClient is not thread safe
	RestfullClient client = new RestfullClient(factory);
	//http get request,params:1. url, 2. path, 3. heads, 4. params
	System.out.println(client.get("http://ip:port/example", "/path", null, null));
	//http async get request
	System.out.println(client.getAsync("http://ip:port/example", "/path", null, null).get());
}
```
### 2. JSON Serialize 
```java
//create client factory,note: factory is thread safe
private static final AbstractClientFactory factory = new GrizzlyJerseyClientFactory(10, 5000, 2000);

public static void main(String[] args) throws Exception {
	
	//create client with factory,note: RestfullClient is not thread safe
	RestfullClient client = new RestfullClient(factory);
	//http get request,params:1. url, 2. path, 3. heads, 4. params, 5. Json Object
	Response response = client.get("http://ip:port/example", "/path", null, null, Response.class);
	System.out.println(response);
	//http async get request
	Future<Response> responseFuture = client.getAsync("http://ip:port/example", "/path", null, null, Response.class);
	System.out.println(responseFuture.get());
}
```
### 3. Low Level Request Api
All RestfullClient Api is implement by Low Level Request Api. <br />
User can use low level request api implements the method that not provided in RestfullClient.
```java
//create client factory,note: factory is thread safe
private static final AbstractClientFactory factory = new GrizzlyJerseyClientFactory(10, 5000, 2000);

public static void main(String[] args) throws Exception {
	
	//create client with factory,note: RestfullClient is not thread safe
	RestfullClient restfullClient = new RestfullClient(factory);
	//http async get request, same with client.getAsync
	Future<Response> response = restfullClient.request((client) -> {
		Invocation.Builder buidler = RestfullClient.build(client, "http://ip:port/example", "/path", null, null);
		return buidler.async().get(Response.class);
	});
	System.out.println(response.get());
}
```
### 4. Multi Threads
```java
//create client factory,note: factory is thread safe
private static final AbstractClientFactory factory = new GrizzlyJerseyClientFactory(10, 5000, 2000);

public static void main(String[] args) throws Exception {
	
	for(int i = 0; i < 100; i++) {
		Thread t = new Thread(() ->{
			try {
				//each thread client one RestfullClient
				RestfullClient restfullClient = new RestfullClient(factory);
				Future<Response> response = restfullClient.request((client) -> {
					Invocation.Builder buidler = RestfullClient.build(client, "http://ip:port/example", "/path", null, null);
					return buidler.async().get(Response.class);
				});
				System.out.println(response.get());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		t.start();
	}
}
```
### 5. Build Multi ClientFacotry With different Business
```java
//create client factory,note: factory is thread safe
private static final AbstractClientFactory pingFactory = new GrizzlyJerseyClientFactory(10, 200, 100);
private static final AbstractClientFactory searchFactory = new GrizzlyJerseyClientFactory(10, 5000, 2000);

public static void main(String[] args) throws Exception {
	RestfullClient restfullClient = new RestfullClient(pingFactory);
	Future<PingResponse> pingResponse = restfullClient.request((client) -> {
		Invocation.Builder buidler = RestfullClient.build(client, "http://ip:port/example", "/ping", null, null);
		return buidler.async().get(PingResponse.class);
	});
	Future<SearchResponse> searchResponse = restfullClient.request((client) -> {
		Invocation.Builder buidler = RestfullClient.build(client, "http://ip:port/example", "/search", null, null);
		return buidler.async().get(SearchResponse.class);
	});
	System.out.println(searchResponse);
	System.out.println(pingResponse);
}
``` 
### 6. Dynamic ClientFactory Params modify
```java
//create client factory,note: factory is thread safe
private static final GrizzlyJerseyClientFactory factory = new GrizzlyJerseyClientFactory(10, 200, 100);

public static void main(String[] args) throws Exception {
	factory.setConnectionTimeout(400);
	factory.setReadTimeout(600);
	factory.reload();
}
``` 

### 7. Close ClientFactory
Close ClientFactory only when never used
```java
//create client factory,note: factory is thread safe
private static final GrizzlyJerseyClientFactory factory = new GrizzlyJerseyClientFactory(10, 200, 100);

public static void main(String[] args) throws Exception {
	factory.close();
}
```