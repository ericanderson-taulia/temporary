package com.taulia.ericanderson.camel

import org.apache.camel.Exchange

import javax.servlet.ServletRequest


class CamelUtils {

  static String remoteIpAddress(Exchange exchange) {
//    org.apache.cxf.message.Message cxfMessage = exchange.getIn().getHeader(CxfConstants.CAMEL_CXF_MESSAGE, org.apache.cxf.message.Message)
//    ServletRequest request = (ServletRequest) cxfMessage.get("HTTP.REQUEST")
//    String remoteAddress = request.getRemoteAddr()
  }

}
