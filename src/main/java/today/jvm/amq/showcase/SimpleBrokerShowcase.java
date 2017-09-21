package today.jvm.amq.showcase;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.NamingException;

/**
 * Simple broker showcase: use minimal configuration (see activemq-simple.xml) to send/receive message.
 *
 * @author Arturs Licis
 */
public class SimpleBrokerShowcase {
	public static void main(String[] args) throws JMSException, NamingException, InterruptedException {
		ActiveMQConnectionFactory connectionFactory = EmbeddedXmlBrokerFactory.createConnectionFactory("today/jvm/amq/showcase/activemq-simple.xml");

		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session producerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer producer = producerSession.createProducer(producerSession.createTopic("amq.simple.topic"));

		Session consumerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer consumer = producerSession.createConsumer(producerSession.createTopic("amq.simple.topic"));
		consumer.setMessageListener(new MessageListener() {
			public void onMessage(Message message) {
				System.out.println("Received a message: " + message);
			}
		});

		producer.send(producerSession.createTextMessage("This is a first message's payload"));

		Thread.sleep(100);

		producerSession.close();
		consumerSession.close();

		connection.close();
	}
}
