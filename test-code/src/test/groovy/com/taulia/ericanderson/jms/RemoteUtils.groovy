package com.taulia.ericanderson.jms

import org.apache.activemq.ActiveMQConnection
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.ScheduledMessage
import org.apache.activemq.advisory.DestinationSource
import org.apache.activemq.broker.BrokerFactory
import org.apache.activemq.broker.BrokerService
import org.apache.activemq.command.ActiveMQQueue
import org.junit.Test

import javax.jms.Connection
import javax.jms.ConnectionFactory
import javax.jms.Destination
import javax.jms.JMSException
import javax.jms.Message
import javax.jms.MessageConsumer
import javax.jms.MessageProducer
import javax.jms.QueueBrowser
import javax.jms.QueueConnection
import javax.jms.Session
import javax.jms.TextMessage
import javax.jms.Queue

/**
 * Created by eanderson on 4/25/16.
 */
class RemoteUtils {

  @Test
  void send() {

    final ActiveMQConnectionFactory conFactory = new ActiveMQConnectionFactory("tcp://localhost:61616")
    final QueueConnection connection = conFactory.createQueueConnection()
    final Session session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE)
    final Destination destination = new ActiveMQQueue("MJ_SAF")
    final MessageProducer producer = session.createProducer(destination)

    final MessageConsumer consumer = session.createConsumer(destination)

    Message message = session.createTextMessage("test")
    message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 20)
    message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, 1)
    message.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, 1)
    producer.send(message)

  }


  @Test
  void testBrowse() {

    final ActiveMQConnectionFactory conFactory = new ActiveMQConnectionFactory("tcp://localhost:61617")
    final QueueConnection connection = conFactory.createQueueConnection()
    final Session session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE)
    final Queue queue = session.createQueue("test.queue.delayed")
    connection.start()


    System.out.println("Browse through the elements in queue")
    final QueueBrowser browser = session.createBrowser(queue)
    Enumeration e = browser.getEnumeration()
    while (e.hasMoreElements()) {
      TextMessage message = (TextMessage) e.nextElement()
      System.out.println("Browse [" + message.getText() + " AMQ_SCHEDULED_DELAY:" + message.getLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY) +  "]")
    }
    System.out.println("Done")
    browser.close()
    
  }

  @Test
  void test2() {
    Connection connection = null
    try {
      // Producer
      ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616")
      connection = connectionFactory.createConnection()
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
      Queue queue = session.createQueue("browseQueue-test")
      MessageProducer producer = session.createProducer(queue)
      String task = "Task"
      for (int i = 0; i < 10; i++) {
        String payload = task + i
        Message msg = session.createTextMessage(payload)
        System.out.println("Sending text '" + payload + "'")
        producer.send(msg)
      }

      MessageConsumer consumer = session.createConsumer(queue)
      connection.start()

      System.out.println("Browse through the elements in queue")
      QueueBrowser browser = session.createBrowser(queue)
      Enumeration e = browser.getEnumeration()
      while (e.hasMoreElements()) {
        TextMessage message = (TextMessage) e.nextElement()
        System.out.println("Browse [" + message.getText() + "]")
      }
      System.out.println("Done")
      browser.close()


      for (int i = 0; i < 10; i++) {
        TextMessage textMsg = (TextMessage) consumer.receive()

        System.out.println(textMsg)

        System.out.println("Received: " + textMsg.getText())

      }

      session.close()

    } finally {

      if (connection != null) {

        connection.close()
      }

    }

  }


  @Test
  void testGetDestinations() {

    ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616")
    ActiveMQConnection connection = connectionFactory.createConnection()
    connection.start()

    DestinationSource destinationSource = connection.getDestinationSource();
    Set<ActiveMQQueue> queues = destinationSource.getQueues();
    def info = connection.brokerInfo
    println info



    for(ActiveMQQueue queue : queues){
      try {
        System.out.println(queue.getQueueName());
      } catch (JMSException e) {
        e.printStackTrace();
      }
    }

    connection.stop()
  }


  @Test
  void startBroker() {
    BrokerService broker = BrokerFactory.createBroker(new URI(
      "broker:(tcp://localhost:61616)"))
    broker.start()
  }


}
