package com.camelgradle.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRoute extends RouteBuilder {
	
	private static Logger logger =  LoggerFactory.getLogger(TestRoute.class);

	@Override
	public void configure() throws Exception {
		
		onException(Throwable.class).maximumRedeliveries(3).redeliveryDelay(1000 * 30);
		
		from("quartz2:quartzRoute?trigger.repeatCount=-1&trigger.repeatInterval=10000")
			.routeId("quartzRoute")
			.log(LoggingLevel.DEBUG, "sending quartz timer")
			.to("direct:testRest");
		
		from("direct:testRest")
		.routeId("testRest")
		.log(LoggingLevel.DEBUG, "sending rest request")
		.to("http4:localhost:5000/hello")
		.to("http4:localhost:5000/hello")
		.process( exchange -> {
			String body = exchange.getIn().getBody(String.class);
			logger.debug("body: " + body.toString());
			exchange.getIn().setBody("Ja Era vacilao");
		})
		.to("http4:localhost:5000/hello")
		.log(LoggingLevel.DEBUG, "done.");
		
	}

}
