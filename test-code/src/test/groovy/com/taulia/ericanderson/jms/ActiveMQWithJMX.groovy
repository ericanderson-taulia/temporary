package com.taulia.ericanderson.jms

import org.apache.activemq.broker.jmx.BrokerViewMBean
import org.apache.activemq.broker.jmx.QueueViewMBean
import org.junit.Test

import javax.management.MBeanServerConnection
import javax.management.MBeanServerInvocationHandler
import javax.management.ObjectName
import javax.management.remote.JMXConnector
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL

/**
 * Created by eanderson on 4/25/16.
 */
class ActiveMQWithJMX {

  private static final String HOST = 'localhost'


  @Test
  void testMe() {

    JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://${HOST}:1099/jmxrmi")
    JMXConnector jmxc = JMXConnectorFactory.connect(url)
    MBeanServerConnection connection = jmxc.getMBeanServerConnection()

    ObjectName activeMQ = new ObjectName("org.apache.activemq:BrokerName=localhost,Type=Broker");
    BrokerViewMBean mbean = (BrokerViewMBean) MBeanServerInvocationHandler.newProxyInstance(conn, activeMQ,BrokerViewMBean.class, true);

    for (ObjectName name : mbean.getQueues()) {
      QueueViewMBean queueMbean = (QueueViewMBean)
      MBeanServerInvocationHandler.newProxyInstance(mbsc, name, QueueViewMBean.class, true);

      if (queueMbean.getName().equals(queueName)) {
        queueViewBeanCache.put(cacheKey, queueMbean);
      }
    }

  }


}
