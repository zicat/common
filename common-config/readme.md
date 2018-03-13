# Common Config
> Newegg Common Config provide config parse, watch listener, parent child extend。

## Class Define:
### 1.1 Schema
Schema is a interface, parse from source to instance
```java
public interface Schema<S, T> {
	
	/**
	 * 
	 * @param source
	 * @return
	 * @throws Exception
	 */
	T unmarshal(S source) throws Exception;
	
	/**
	 * 
	 * @param parentTarget
	 * @param source
	 * @return
	 * @throws Exception
	 */
	T unmarshal(T parentTarget, S source) throws Exception;
}
```
### 1.2 InputStreamSchema
InputStreamSchema extends Schema, Set source as InputStream
```java
public interface InputStreamSchema<T> extends Schema<InputStream, T> {

}
```
### 1.3 JAXBSchema
JAXBSchema extends InputStreamSchema, Parse InputStream to Object by jaxb.

### 1.4 PropertiesSchema
PropertiesSchema extends InputStreamSchema, Parse InputStream to Properties by JDK Properties Api.

### 1.5 GsonSchema
GsonSchema extends InputStreamSchema, Parse InputStream to Object by GSon Api.

### 2.1 Config
Config provide config abstract, source as key.
### 2.2 AbstractConfig
AbstractConfig extends Config, Linked parent config and children config
### 2.3 LocalConfig
LocalConfig extends AbstractConfig, create inputstream with url
### 2.4 ZookeeperConfig
ZookeeperConfig extends AbstractConfig, create inputstream with zookeeper client

### 3.1 Watcher
Watcher provide Config Watcher, Define:
```java
public interface Watcher<C extends AbstractConfig<?, ?>> extends Closeable {
	
	/**
	 * 
	 * @param config
	 * @param listener
	 * @throws Exception
	 */
	void register(C config, AbstractConfigListener<C> listener) throws Exception;
	
	/**
	 * 
	 * @param config
	 * @throws Exception
	 */
	void register(C config) throws Exception; 
	
	/**
	 * 
	 * @param config
	 * @throws Exception
	 */
	void unregister(C config) throws Exception;
	
	/**
	 * 
	 */
	void close() throws IOException;
}
```
### 3.2 CycleWatcher
CycleWatcher extends Watcher provide Watcher by cycle.All AbstractConfig can be register

### 3.3 ZookeeperWatcher
ZookeeperWatcher extends Watcher  provide Watcher by Zookeeper Api, ZookeeperConfig can be register.

### 3.4 LocalWatcher
LocalWatcher extends Watcher  provide Watcher by JDK7 WatchService API, LocalConfig can be register.

### 4.1 AbstractConfigListener
AbstractConfigListener provide change call back interface.
```java
public interface AbstractConfigListener<C extends AbstractConfig<?, ?>> {
	
	/**
	 * 
	 * @param config
	 * @throws Exception
	 */
	void onModify(C config) throws Exception;
}
```
### 4.2 LoggerConfigListener 
LoggerConfigListener extends AbstractConfigListener, implements call back by slf4j api
```java
public class LoggerConfigListener<C extends AbstractConfig<?, ?>> implements AbstractConfigListener<C> {
	
	private static final Logger LOG = LoggerFactory.getLogger(LoggerConfigListener.class);
	
	@Override
	public void onModify(C config) throws Exception {
		LOG.info(config.getSource() + " changed");
	}
}
```
## Example:
```java
// create properties schema
private static final InputStreamSchema<Properties> PROPERTIES_SCHEMA = InputStreamSchemaFactory.createPropertiesSchema(StandardCharsets.UTF_8);

// create listener
private static final AbstractConfigListener<LocalConfig<?>> listener = new LoggerConfigListener2<>();

// create watcher
private static final Watcher<LocalConfig<?>> watcher = new CycleWatcher<>(1000 * 60);

//create config 1
private static final LocalConfig<Properties> localConfig = new LocalConfig<>("aa.properties", PROPERTIES_SCHEMA);

//create config 2, it's parent is config 1
private static final LocalConfig<Properties> localConfig2 = new LocalConfig<>("schema-aa2.properties", PROPERTIES_SCHEMA, localConfig);
static {
	try {
		// register config
		watcher.register(localConfig, listener);
		watcher.register(localConfig2, listener);
	} catch (Exception e) {
		throw new RuntimeException(e);
	}
}

public static void main(String[] args) {
	// get newest config instance
	System.out.println(localConfig.getInstance());
}
```
