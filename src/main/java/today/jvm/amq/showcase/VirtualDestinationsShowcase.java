package today.jvm.amq.showcase;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQMessage;

import javax.jms.*;
import javax.naming.NamingException;


/**
 * Example of how to use Virtual Destinations. See activemq-vdest.xml classpath resource.
 *
 * In this example, messages from queues 'amq.simple.queue.>' are forwarded to
 * 'amq.simple.queue.notifications'. In order for original queues to remain
 * usable, 'forwardOnly' is set to false.
 *
 * @author Arturs Licis
 */
public class VirtualDestinationsShowcase {
	public static void main(String[] args) throws JMSException, NamingException, InterruptedException {
		ActiveMQConnectionFactory connectionFactory = EmbeddedXmlBrokerFactory.createConnectionFactory("today/jvm/amq/showcase/activemq-vdest.xml");

		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session producerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer producer1 = producerSession.createProducer(producerSession.createQueue("amq.simple.queue.1"));
		MessageProducer producer2 = producerSession.createProducer(producerSession.createQueue("amq.simple.queue.2"));

		Session consumerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer consumer = consumerSession.createConsumer(producerSession.createTopic("amq.simple.queue.notifications"));
		consumer.setMessageListener(new MessageListener() {
			public void onMessage(Message message) {
				System.out.println("Received a message originating from " + ((ActiveMQMessage) message).getOriginalDestination() + ":\n\t" + message);
			}
		});

		producer1.send(producerSession.createTextMessage("[P1] This is a first message's payload"));
		producer2.send(producerSession.createTextMessage("[P2] This is a first message's payload"));

		Thread.sleep(100);

		producerSession.close();
		consumerSession.close();

		connection.close();
	}
}
