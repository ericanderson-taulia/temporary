package com.taulia.ericanderson

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Appender
import org.apache.logging.log4j.core.Layout
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.appender.ConsoleAppender
import org.apache.logging.log4j.core.appender.FileAppender
import org.apache.logging.log4j.core.config.AppenderRef
import org.apache.logging.log4j.core.config.Configuration
import org.apache.logging.log4j.core.config.LoggerConfig
import org.apache.logging.log4j.core.layout.PatternLayout

import java.nio.charset.Charset


class BasicTest {

//  static {
//    final LoggerContext ctx = (LoggerContext) LogManager.getContext(false)
//    final Configuration config = ctx.getConfiguration()
//
//    Layout layout = PatternLayout.createLayout(PatternLayout.SIMPLE_CONVERSION_PATTERN, config, null, null,null, null)
//    Appender appender = FileAppender.createAppender("target/test.log", "false", "false", "File", "true", "false", "false", "4000", layout, null, "false", null, config)
//    appender.start()
//    config.addAppender(appender)
//
//    AppenderRef ref = AppenderRef.createAppenderRef("File", null, null)
//    AppenderRef[] appenderRefs = new AppenderRef[ref]
//    LoggerConfig loggerConfig = LoggerConfig.createLogger("false", "info", "org.apache.logging.log4j", "true", appenderRefs, null, config, null )
//    loggerConfig.addAppender(appender, null, null)
//    config.addLogger("org.apache.logging.log4j", loggerConfig)
//    ctx.updateLoggers()
//  }

  static  {


    LoggerContext context = (LoggerContext) LogManager.getContext();
    Configuration config = context.getConfiguration();

    Layout layout = PatternLayout.createDefaultLayout()

    Appender appender = ConsoleAppender.createAppender(layout, null, null, "CONSOLE_APPENDER", null, null);
    appender.start();
    AppenderRef ref = AppenderRef.createAppenderRef("CONSOLE_APPENDER", null, null);
//    AppenderRef[] refs = [ref];
//    LoggerConfig loggerConfig= LoggerConfig.createLogger("false", Level.INFO,"CONSOLE_LOGGER","com",refs,null,null,null);
//    loggerConfig.addAppender(appender,null,null);
//
//    config.addAppender(appender);
//    config.addLogger("com", loggerConfig);
//    context.updateLoggers(config);
//
//    Logger logger=LogManager.getContext().getLogger("com");
//    logger.info("HELLO_WORLD");

  }

}
