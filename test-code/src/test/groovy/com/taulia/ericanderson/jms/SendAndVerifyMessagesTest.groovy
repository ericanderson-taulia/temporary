package com.taulia.ericanderson.jms

import com.google.common.base.Preconditions
import com.google.common.base.Stopwatch
import com.taulia.ericanderson.BasicTest
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.jms.JmsComponent
import org.apache.camel.impl.DefaultCamelContext
import org.junit.Test
import org.springframework.util.StringUtils

import java.text.SimpleDateFormat
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ThreadPoolExecutor

class SendAndVerifyMessagesTest extends BasicTest {

  private final int MESSAGE_COUNT = 300

  private final int PRODUCER_THREAD_COUNT = 15

  private final int CONSUMER_THREAD_COUNT = 1

  static final String ID_KEY = 'ID'

  static final Map<String, Boolean> messageTable = new Hashtable<>()

  private static final Random random = new Random()

  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");

  volatile long lastTimeConsumedAMessage

  private int count = 0

  @Test
  void testSendMessagesAndVerify() {

    Stopwatch stopwatch = Stopwatch.createStarted()

    def messageProcessor = new Processor() {
      public void process(Exchange exchange) {

        String id = exchange.getIn().getHeader(ID_KEY)
        Preconditions.checkState(!StringUtils.isEmpty(id))
        println("Recieved message ${id} and sleeping ${simpleDateFormat.format(new Date(System.currentTimeMillis()))}")

        messageTable.remove(id)

        Thread.sleep(500)

        lastTimeConsumedAMessage = System.currentTimeMillis()
      }
    }

    CamelContext context = new DefaultCamelContext()
    context.addComponent("jms-producer", JmsComponent.jmsComponentAutoAcknowledge(ActiveMQConnections.TAULIA_AMQ_LEVELDB_FAILOVER.connectionFactory))
    context.addComponent("jms-consumer", JmsComponent.jmsComponentAutoAcknowledge(ActiveMQConnections.TAULIA_AMQ_LEVELDB_FAILOVER.connectionFactory))
    CONSUMER_THREAD_COUNT.times {
      context.addRoutes(new RouteBuilder() {
        public void configure() {
          from("jms-consumer:queue:a1.test.queue.delayed").process(messageProcessor)
        }
      })
    }
    context.start()

    def listOfFutures = []
    ThreadPoolExecutor executor = Executors.newFixedThreadPool(PRODUCER_THREAD_COUNT)
    PRODUCER_THREAD_COUNT.times {
      listOfFutures << executor.submit( new Producer(context) )
    }

    while (! listOfFutures.every { Future future -> future.done } ) {
      Thread.sleep(500)
    }

    listOfFutures.each { it.get() }

    while (! messageTable.every { it.value } && System.currentTimeMillis() - lastTimeConsumedAMessage < 3000) {
      Thread.sleep(500)
    }

    executor.shutdownNow()
    context.stop()
    stopwatch.stop()

    assert messageTable.size() == 0
  }



  class Producer implements Runnable {

    CamelContext context

    Producer(CamelContext context) {
      this.context = context
    }

    @Override
    void run() {

      ProducerTemplate template = context.createProducerTemplate()

      while (count < MESSAGE_COUNT) {
        count++
        String id = UUID.randomUUID()
        messageTable.put(id, false)

        def headers = [
          AMQ_SCHEDULED_DELAY: "${1000 * random.nextInt(10)}",
          ID : "${id}"
        ]

        template.sendBodyAndHeaders(
          "jms-producer:queue:a1.test.queue",
          "Test Message: ID=" + id,
          headers
        )

        println ("Sent message with ID ${id}")
      }
    }

  }

}
