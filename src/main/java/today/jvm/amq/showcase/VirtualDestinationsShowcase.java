package today.jvm.amq.showcase;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.NamingException;


/**
 * Example of how to use Virtual Destinations. See activemq-vdest.xml classpath resource.
 *
 * In this example, messages from queues 'amq.simple.queue.>' are forwarded to
 * 'amq.simple.queue.notifications'. In order for original queues to remain
 * usable, 'forwardOnly' is set to false.
 *
 * For each queue, there's a mirrored destination (topic) with the same name but prefix '.mirror'
 * (as defined in XML configuration).
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
		MessageConsumer consumer = consumerSession.createConsumer(producerSession.createQueue("amq.simple.queue.>"));
		consumer.setMessageListener(new MessageListener() {
			public void onMessage(Message message) {
				System.out.println("Received a message: " + message);
			}
		});

		Session mirrorConsumerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer mirrorConsumer = mirrorConsumerSession.createConsumer(producerSession.createTopic("amq.simple.queue.notifications"));
		mirrorConsumer.setMessageListener(new MessageListener() {
			public void onMessage(Message message) {
				System.out.println("Received a message (notifications): " + message);
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
