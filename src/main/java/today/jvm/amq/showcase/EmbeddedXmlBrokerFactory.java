package today.jvm.amq.showcase;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.util.Hashtable;

public class EmbeddedXmlBrokerFactory {

	/**
	 * Creates {@link ActiveMQConnectionFactory} using XBean provided configuration.
	 *
	 * @param xbeanUri URI of ActiveMQ XML configuration (could be file or classpath resource)
	 *
	 * @return instance of {@link ActiveMQConnectionFactory}
	 */
	public static ActiveMQConnectionFactory createConnectionFactory(String xbeanUri) throws NamingException, JMSException {
		Hashtable<String, String> properties = new Hashtable<String, String>();
		properties.put("java.naming.factory.initial", "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		properties.put(Context.PROVIDER_URL, "vm://localhost?brokerConfig=xbean:" + xbeanUri);
		InitialContext initialContext = new InitialContext(properties);
		ActiveMQConnectionFactory connectionFactory = (ActiveMQConnectionFactory) initialContext.lookup("ConnectionFactory");

		return connectionFactory;
	}
}
