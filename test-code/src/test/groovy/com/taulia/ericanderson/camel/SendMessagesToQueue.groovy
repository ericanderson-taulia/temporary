package com.taulia.ericanderson.camel

import com.google.common.base.Stopwatch
import com.taulia.ericanderson.BasicTest
import com.taulia.ericanderson.jms.ActiveMQConnections
import groovy.util.logging.Log4j
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.jms.JmsComponent
import org.apache.camel.impl.DefaultCamelContext
import org.junit.Test

import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

@Log4j
class SendMessagesToQueue extends BasicTest {

  private static final Random random = new Random()

  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");


  @Test
  void testRemoveMessages() {
    def myProcessor = new Processor() {
      public void process(Exchange exchange) {

        println("Recieved message and sleeping ${simpleDateFormat.format(new Date(System.currentTimeMillis()))}")
        Thread.sleep(2000)

      }
    }

    CamelContext context = new DefaultCamelContext()
    context.addRoutes(new RouteBuilder() {
      public void configure() {
        from("jms2:queue:a1.test.queue.delayed").process(myProcessor)
      }
    })
    context.addComponent("jms2", JmsComponent.jmsComponentAutoAcknowledge(ActiveMQConnections.TAULIA_AMQ_LEVELDB_FAILOVER.connectionFactory))
    context.start()

    Stopwatch stopwatch = Stopwatch.createStarted()
    while (stopwatch.elapsed(TimeUnit.MINUTES) < 20) {
      Thread.sleep(1000)
    }

  }



  /**
   * Sends messages to a queue on broker1, the messages are transfered to another broker and then sent to a delay queue.
   *
   *
   *
   */
  @Test
  void testSendMessages() {

    CamelContext context = new DefaultCamelContext()
    context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(ActiveMQConnections.TAULIA_AMQ_LEVELDB_FAILOVER.connectionFactory))
    context.start()

    ProducerTemplate template = context.createProducerTemplate()

    Stopwatch stopwatch = Stopwatch.createStarted()
    int count = 0

    while (stopwatch.elapsed(TimeUnit.MINUTES) < 20) {
      template.sendBodyAndHeader(
        "jms:queue:a1.test.queue",
        "Test Message: " + stopwatch.elapsed(TimeUnit.MILLISECONDS),
        "AMQ_SCHEDULED_DELAY", 1000 * random.nextInt(10)
      )
      count++
      if (count % 100 == 0) {
        println("Sent ${count}")
      }
    }

    //Let the Consumer finish cleaning up the messages
    Thread.sleep(10000)

  }


}
