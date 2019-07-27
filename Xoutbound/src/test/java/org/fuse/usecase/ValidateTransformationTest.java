package org.fuse.usecase;

import java.io.FileInputStream;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.DisableJmx;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mycompany.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@DisableJmx(true)
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = Application.class)
public class ValidateTransformationTest  {
	@Autowired
    private CamelContext camelContext;

	@EndpointInject(uri = "mock:personxml")
	private MockEndpoint resultEndpoint;

	@Produce(uri = "direct:wscall")
	private ProducerTemplate startEndpoint;

	@Test
	public void transform() throws Exception {
		// EXCLUDE-BEGIN
		// setup expectations
		resultEndpoint.expectedMessageCount(1);
		// set expected body as the unpretty print version of the json
		// (flattened)
		resultEndpoint.expectedBodiesReceived("Processed the data");

		// run test
		startEndpoint.sendBody(readFile("src/test/data/batchupdate.xml"));

		// verify results
		resultEndpoint.assertIsSatisfied();
		// EXCLUDE-END
	}

	/*
	 * @Override protected RouteBuilder createRouteBuilder() throws Exception {
	 * return new RouteBuilder() { public void configure() throws Exception { //
	 * EXCLUDE-BEGIN from("direct:inbox") .log("Before transformation:\n ${body}")
	 * .to("ref:csv2json") .log("After transformation:\n ${body}")
	 * .to("mock:personxml"); // EXCLUDE-END } }; }
	 */

	/*
	 * @Override protected AbstractXmlApplicationContext createApplicationContext()
	 * { ClassPathXmlApplicationContext cpc = new
	 * ClassPathXmlApplicationContext("spring/camel-context.xml"); return cpc; }
	 */

	private String readFile(String filePath) throws Exception {
		String content;
		FileInputStream fis = new FileInputStream(filePath);
		try {
			content = camelContext.getTypeConverter().convertTo(String.class, fis);
		} finally {
			fis.close();
		}
		return content;
	}

	
}
