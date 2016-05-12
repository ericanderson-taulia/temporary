package com.taulia.ericanderson.jms

import com.google.common.base.Preconditions
import com.google.common.base.Stopwatch
import com.taulia.ericanderson.BasicTest
import groovy.util.logging.Log4j2
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
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.atomic.AtomicInteger

@Log4j2
class SendAndVerifyMessagesTest extends BasicTest {


  /************************************************************/
  /* Adjustable settings
  /************************************************************/

  private static final int MESSAGE_COUNT = 5000

  private static final int PRODUCER_THREAD_COUNT = 20

  private static final int CONSUMER_THREAD_COUNT = 1

  private static final int CONSUMER_THREAD_WAIT = 100

  /**
   * Time to wait for consumers to finish consuming all messages. This is the amount of time in seconds, that the test
   * will wait since the last consumed message before terminating.
   */
  private static final int TIME_TO_WAIT = 120

  /**
   * The connection factory for the test
   */
  private static final ActiveMQConnections PRODUCER_CONNECTION = ActiveMQConnections.TAULIA_AMQ_FAILOVER
  private static final ActiveMQConnections CONNSUMER_CONNECTION = ActiveMQConnections.TAULIA_AMQ_FAILOVER


  /************************************************************/

  static final String ID_KEY = 'ID'

  static final Map<String, Boolean> messageTable = new Hashtable<>()

  static final List<String> duplicateMessages = new CopyOnWriteArrayList<String>()

  private static final Random random = new Random()

  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");

  volatile long lastTimeConsumedAMessage

  private AtomicInteger sentCount = new AtomicInteger(0)
  private AtomicInteger recievedCount = new AtomicInteger(0)

  @Test
  void testSendMessagesAndVerify() {

    Stopwatch stopwatch = Stopwatch.createStarted()

    def messageProcessor = new Processor() {
      public void process(Exchange exchange) {

        recievedCount.andIncrement

        String id = exchange.getIn().getHeader(ID_KEY)
        Preconditions.checkState(!StringUtils.isEmpty(id))

        if (recievedCount.get() % 100 == 0) {
          log.info("Recieved Count [${recievedCount.get()}], message ${id} and sleeping ${simpleDateFormat.format(new Date(System.currentTimeMillis()))}")
        }

        if (messageTable.containsKey(id)) {
          messageTable.remove(id)
        }
        else {
          duplicateMessages.add(id)
        }

        if (CONSUMER_THREAD_WAIT) {
          Thread.sleep(CONSUMER_THREAD_WAIT)
        }

        lastTimeConsumedAMessage = System.currentTimeMillis()
      }
    }

    CamelContext producerContext = new DefaultCamelContext()
    producerContext.addComponent("jms-producer", JmsComponent.jmsComponentAutoAcknowledge(PRODUCER_CONNECTION.connectionFactory))
    producerContext.start()

    CamelContext consumerContext = new DefaultCamelContext()
    consumerContext.addComponent("jms-consumer", JmsComponent.jmsComponentAutoAcknowledge(CONNSUMER_CONNECTION.connectionFactory))
    CONSUMER_THREAD_COUNT.times {

      consumerContext.addRoutes(new RouteBuilder() {
        public void configure() {
          from("jms-consumer:queue:a1.test.queue.delayed").process(messageProcessor)
        }
      })
    }
    consumerContext.start()

    def listOfFutures = []
    ThreadPoolExecutor executor = Executors.newFixedThreadPool(PRODUCER_THREAD_COUNT)
    PRODUCER_THREAD_COUNT.times {
      listOfFutures << executor.submit( new Producer(producerContext) )
    }

    while (! listOfFutures.every { Future future -> future.done } ) {
      Thread.sleep(500)
    }

    listOfFutures.each { it.get() }

    while (! messageTable.every { it.value } && (System.currentTimeMillis() - lastTimeConsumedAMessage < (TIME_TO_WAIT * 1000))) {
      Thread.sleep(500)
    }

    executor.shutdownNow()
    producerContext.stop()
    consumerContext.stop()
    stopwatch.stop()

    log.info("Sent : ${sentCount.get()} Recieved : ${recievedCount.get()}")

    assert (messageTable.size() == 0 & duplicateMessages.size() == 0)
  }



  class Producer implements Runnable {

    CamelContext context

    Producer(CamelContext context) {
      this.context = context
    }

    @Override
    void run() {

      ProducerTemplate template = context.createProducerTemplate()

      while (sentCount.get() < MESSAGE_COUNT) {
        sentCount.andIncrement
        String id = UUID.randomUUID()
        messageTable.put(id, false)

        def headers = [
          AMQ_SCHEDULED_DELAY: "${1000 * random.nextInt(10)}",
          ID : "${id}"
        ]

        try {
          template.sendBodyAndHeaders(
            "jms-producer:queue:a1.test.queue",
            "Test Message: ID=" + id,
            headers
          )
        }
        catch (org.apache.camel.CamelExecutionException e) {
          log.error(e.message, e)
          messageTable.remove(id)
          sentCount.andDecrement
        }

        if (sentCount.get() % 100 == 0) {
          log.info ("Count: ${sentCount.get()}  Sent message with ID ${id}")
        }
      }
    }

  }

}
