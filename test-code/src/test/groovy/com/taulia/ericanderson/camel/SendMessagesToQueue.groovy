package com.taulia.ericanderson.camel

import com.google.common.base.Stopwatch
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.jms.JmsComponent
import org.apache.camel.impl.DefaultCamelContext
import org.junit.Test

import javax.jms.ConnectionFactory
import java.util.concurrent.TimeUnit

class SendMessagesToQueue {

  private static final Random random = new Random()


  /**
   * Sends messages to a queue on broker1, the messages are transfered to another broker and then sent to a delay queue.
   *
   *
   *
   */
  @Test
  void sendAndRemoveMessages() {

    def myProcessor = new Processor() {
      public void process(Exchange exchange) {
      }
    };


    CamelContext context = new DefaultCamelContext();

    context.addRoutes(new RouteBuilder() {
      public void configure() {
        from("jms2:queue:test.queue.delayed").process(myProcessor)
      }
    })

    ConnectionFactory connectionFactory1 = new ActiveMQConnectionFactory("tcp://localhost:61616");
    ConnectionFactory connectionFactory2 = new ActiveMQConnectionFactory("tcp://localhost:61617");

    // Note we can explicit name the component
    context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory1));
    context.addComponent("jms2", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory2));

    ProducerTemplate template = context.createProducerTemplate();
    context.start();

    Stopwatch stopwatch = Stopwatch.createStarted()

//    while (stopwatch.elapsed(TimeUnit.MINUTES) < 20) {
//      template.sendBodyAndHeader(
//        "jms:queue:test.queue",
//        "Test Message: " + stopwatch.elapsed(TimeUnit.MILLISECONDS),
//        "AMQ_SCHEDULED_DELAY", 1000 * random.nextInt(10)
//      )
//    }

    //Let the Consumer finish cleaning up the messages
    Thread.sleep(10000)

  }


}
