package com.taulia.ericanderson.jms

import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.ActiveMQSslConnectionFactory

import javax.jms.ConnectionFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

enum ActiveMQConnections {

  LOCALHOST_BROKER1('tcp://localhost:61616'),
  LOCALHOST_BROKER2('tcp://localhost:61617'),
  LOCALHOST_FAILOVER('failover:(tcp://localhost:61616,tcp://localhost:61617)'),
  TAULIA_AMQ_BROKER1('tcp://qetest-broker1-amq.taulia.com:61616', false),
  TAULIA_AMQ_BROKER2('tcp://qetest-broker2-amq.taulia.com:61616', false),
  TAULIA_AMQ_FAILOVER('failover:(tcp://qetest-broker1-amq.taulia.com:61616,tcp://qetest-broker2-amq.taulia.com:61616)', false),
  TAULIA_AMQ_LEVELDB_FAILOVER('failover:(tcp://qetest-broker1-leveldb-amq.taulia.com:61616,tcp://qetest-broker2-leveldb-amq.taulia.com:61616,tcp://qetest-broker3-leveldb-amq.taulia.com:61616)', false)


  ConnectionFactory connectionFactory

  private ActiveMQConnections(String connectionFactoryUri, boolean ssl = false) {
    this.connectionFactory = buildFactory(connectionFactoryUri, ssl)
  }

  private static final ConnectionFactory buildFactory(String uri, boolean ssl) {
    (ssl) ? new ActiveMQSslConnectionFactory ('tauliamq', '3cl84BAwedaUy8jjMCnt', uri) :
      new ActiveMQConnectionFactory('tauliamq', '3cl84BAwedaUy8jjMCnt', uri)
  }

  private static final ActiveMQConnectionFactory buildSSL(String uri) {
    def activeMQConnectionFactory = new ActiveMQConnectionFactory('tauliamq', '3cl84BAwedaUy8jjMCnt', uri)
  }

//  TrustManager[] trustAllCerts = new TrustManager[] {
//    new X509TrustManager() {
//      public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//        return null;
//      }
//      public void checkClientTrusted(
//        java.security.cert.X509Certificate[] certificates, String authType) {
//      }
//      public void checkServerTrusted(
//        java.security.cert.X509Certificate[] certificates, String authType) {
//      }
//    }
//  };

}
