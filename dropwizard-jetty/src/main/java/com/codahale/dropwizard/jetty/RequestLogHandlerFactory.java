package com.codahale.dropwizard.jetty;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import com.codahale.dropwizard.logging.LoggingOutput;
import com.google.common.collect.ImmutableList;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.slf4j.LoggerFactory;

import java.util.TimeZone;

// TODO: 11/7/11 <coda> -- document RequestLogHandlerFactory
// TODO: 11/7/11 <coda> -- test RequestLogHandlerFactory

public class RequestLogHandlerFactory {
    private static class RequestLogLayout extends LayoutBase<ILoggingEvent> {
        @Override
        public String doLayout(ILoggingEvent event) {
            return event.getFormattedMessage() + CoreConstants.LINE_SEPARATOR;
        }
    }

    private final TimeZone timeZone;
    private final ImmutableList<LoggingOutput> outputs;
    private final String name;

    public RequestLogHandlerFactory(String name,
                                    Iterable<LoggingOutput> outputs,
                                    TimeZone timeZone) {
        this.name = name;
        this.outputs = ImmutableList.copyOf(outputs);
        this.timeZone = timeZone;
    }
    
    public boolean isEnabled() {
        return !outputs.isEmpty();
    }

    public RequestLogHandler build() {
        final Logger logger = (Logger) LoggerFactory.getLogger("http.request");
        logger.setAdditive(false);
        final LoggerContext context = logger.getLoggerContext();

        final AppenderAttachableImpl<ILoggingEvent> appenders = new AppenderAttachableImpl<>();

        final RequestLogLayout layout = new RequestLogLayout();
        layout.start();

        for (LoggingOutput output : outputs) {
            appenders.addAppender(output.build(context, name, layout));
        }

        final RequestLogHandler handler = new RequestLogHandler();
        handler.setRequestLog(new AsyncRequestLog(appenders, timeZone));

        return handler;
    }
}
