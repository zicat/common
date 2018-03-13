# Common Log
Common Log provides async appenders, these appenders implements log4j appender or other log frameworks
## AsyncAppenderSkeleton
AsyncAppenderSkeleton is abstract class, base on common-utils Producer Consumer. <br />
AsyncAppenderSkeleton is store logs on disk and consumer it async. <br />
Based on common-utils Producer Consumer, Log data never be lost even if unexpected  exit of the program, and retry consume if fail and until consume success.
### Class Induction
#### 1. AsyncKafkaAppender
AsyncKafkaAppender extends AsyncAppenderSkeleton, aync consume data from disk log to kafka
```java
log4j.appender.kafka=org.zicat.common.log.log4j.kafka.AsyncKafkaAppender
log4j.appender.kafka.layout=org.apache.log4j.PatternLayout
log4j.appender.kafka.consumerMaxIntervalTimeMillis=50
log4j.appender.kafka.consumerMaxCount=50 
log4j.appender.kafka.threadCount=3  # thread count
log4j.appender.kafka.filePath=bbb.mg # file name
log4j.appender.kafka.topic=${topic name} # topic name
log4j.appender.kafka.brokerList=${broke list} #format ip1:port1,ip2:port2
```
#### 2. AsyncSMTPAppender & AsyncSubjectReducedSMTPAppender & AsyncReducedSMTPAppender
AsyncSMTPAppender extends SMTPAppender, aync consume data from disk log to smtp server.<br />
AsyncReducedSMTPAppender extends AsyncSMTPAppender, Duplicate removal by mail detail
AsyncSubjectReducedSMTPAppender extends AsyncSMTPAppender, Duplicate removal by mail subject
```java
log4j.appender.MAIL2=org.zicat.common.log.log4j.mail.AsyncSubjectReducedSMTPAppender
log4j.appender.MAIL2.layout=org.apache.log4j.PatternLayout
log4j.appender.MAIL2.Threshold=ERROR
log4j.appender.MAIL2.consumerMaxIntervalTimeMillis=50
log4j.appender.MAIL2.consumerMaxCount=50
log4j.appender.MAIL2.BufferSize=512
log4j.appender.MAIL2.SMTPHost=${smpt host}
log4j.appender.MAIL2.SMTPUsername=
log4j.appender.MAIL2.SMTPPassword=
log4j.appender.MAIL2.From=${from mail}
log4j.appender.MAIL2.Subject=[Local] Testing ERROR2
log4j.appender.MAIL2.To=${to mail}
```
#### 3. AsyncHttpAppender
AsyncHttpAppender extends AsyncAppenderSkeleton abstract class.<br />
User need to extends AsyncHttpAppender to finish detail send logic using RestfullClient api<br />
Define:
```java
/**
 * use client to send http request
 * 
 * @param client
 * @param elements
 * @throws Exception
 */
public abstract void consume(RestfullClient client, List<LoggingEvent> elements) throws Exception;

/**
 * deal with Exception, if dealException throw exception, data will be roll
 * back and consumer again else data will be discard and continue to
 * consumer next data
 * 
 * @param client
 * @param elements
 * @param e
 */
public abstract void dealException(RestfullClient client, List<LoggingEvent> elements, Exception e);
```
