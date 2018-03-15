package org.zicat.common.log.log4j.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.spi.LoggingEvent;

import org.zicat.common.log.log4j.AsyncAppenderSkeleton;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * 
 * @author lz31
 *
 */
public class AsyncKafkaAppender extends AsyncAppenderSkeleton {

	public static String BROKER_LIST = "metadata.broker.list";
	public static String REQUEST_ACK = "request.required.acks";
	public static String SERIALIZAER_CLASS = "serializer.class";
	public static String PRODUCTER_TYPE = "producer.type";

	private String topic = null;
	private String brokerList = null;
	private String serializerClass = "kafka.serializer.StringEncoder";
	private int requiredNumAcks = Integer.MAX_VALUE;
	private Producer<String, String> producer = null;

	@Override
	public void activateOptions() {

		super.activateOptions();
		producer = new Producer<>(initProducerConfig());
	}

	@Override
	public void close() {
		try {
			super.close();
		} finally {
			if (producer != null)
				producer.close();
		}
	}

	@Override
	public void consume(List<LoggingEvent> elements) throws Exception {
		List<KeyedMessage<String, String>> messages = buildMessage(elements);
		producer.send(messages);
	}

	private List<KeyedMessage<String, String>> buildMessage(List<LoggingEvent> elements) {

		List<KeyedMessage<String, String>> messages = new ArrayList<>();
		for (LoggingEvent event : elements) {
			KeyedMessage<String, String> keyedMessage = new KeyedMessage<String, String>(topic,
					event.getMessage().toString());
			messages.add(keyedMessage);
		}
		return messages;
	}

	@Override
	public void dealException(List<LoggingEvent> elements, Exception e) {
		throw new RuntimeException(e);
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	/**
	 * 
	 * @return
	 */
	protected ProducerConfig initProducerConfig() {

		if (brokerList == null)
			throw new RuntimeException("The metadata.broker.list property should be specified");

		if (topic == null)
			throw new RuntimeException("topic must be specified by the Kafka log4j appender");

		Properties properties = new Properties();
		properties.put(BROKER_LIST, brokerList);
		properties.put(SERIALIZAER_CLASS, serializerClass);
		if (requiredNumAcks != Integer.MAX_VALUE) {
			properties.put(REQUEST_ACK, String.valueOf(requiredNumAcks));
		} else {
			properties.put(REQUEST_ACK, "1");
		}

		properties.put(PRODUCTER_TYPE, "sync");
		ProducerConfig config = new ProducerConfig(properties);
		return config;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getBrokerList() {
		return brokerList;
	}

	public void setBrokerList(String brokerList) {
		this.brokerList = brokerList;
	}

	public int getRequiredNumAcks() {
		return requiredNumAcks;
	}

	public void setRequiredNumAcks(int requiredNumAcks) {
		this.requiredNumAcks = requiredNumAcks;
	}
}
