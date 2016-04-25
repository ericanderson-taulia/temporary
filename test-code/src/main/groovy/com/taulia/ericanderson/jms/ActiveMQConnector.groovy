package com.taulia.ericanderson.jms

import org.apache.activemq.ActiveMQConnectionFactory

import javax.jms.ConnectionFactory

enum ActiveMQConnector {

  BROKER1(new ActiveMQConnectionFactory('tcp://localhost:61616?wireFormat=openwire&wireFormat.tightEncodingEnabled=true')),
  BROKER2(new ActiveMQConnectionFactory('tcp://localhost:61616?wireFormat=openwire&wireFormat.tightEncodingEnabled=true'))

  ConnectionFactory connectionFactory

  private ActiveMQConnector(ConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory
  }

}