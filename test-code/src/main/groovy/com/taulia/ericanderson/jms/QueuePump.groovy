package com.taulia.ericanderson.jms

import groovy.transform.CompileStatic
import org.apache.camel.CamelContext
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.jms.JmsComponent
import org.apache.camel.component.jms.JmsConfiguration
import org.apache.camel.impl.DefaultCamelContext

@CompileStatic
class QueuePump extends RouteBuilder {

  String queueName

  QueuePump(String queueName) {
    this.queueName = queueName
  }

  public static void main(String[] args) {
    CamelContext context = new DefaultCamelContext()

    addJmsComponent(context, ActiveMQConnector.BROKER1, 'input')
    addJmsComponent(context, ActiveMQConnector.BROKER2, 'output')

    args = ['com.taulia.xmlrpctranslator.queues.batch.extractTickets.input']

    args.each { String queueName ->
      def pump = new QueuePump(queueName)
      context.addRoutes(pump)
      println "created route for ${queueName}"
    }

    context.start()

    addShutdownHook { context.stop() }
//    synchronized (this) {
//      this.wait()
//    }
  }

  private static void addJmsComponent(DefaultCamelContext context, ActiveMQConnector connector, String name) {
    def conf = new JmsConfiguration()
    conf.setConnectionFactory(connector.connectionFactory)

    context.addComponent(name, new JmsComponent(conf))
  }

  @Override
  void configure() throws Exception {
    from('input:queue:' + queueName + '?testConnectionOnStartup=true')
      .to('output:queue:' + queueName + '.DLQ_E' + '?testConnectionOnStartup=true')
  }

}