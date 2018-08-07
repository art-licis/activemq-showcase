package today.jvm.amq.showcase;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.NamingException;

/**
 * Example of how to use Mirrored Queues + composite destination to forward
 * messages from multiple topics (mirrored queues target) to single notification topic.
 *
 * See activemq-mirrored-composite.xml classpath resource.
 *
 * For each queue, there's a mirrored destination (topic) with the same name but prefix '.mirror'
 * (as defined in XML configuration). Along with that, there's a virtual destination interceptor
 * to create a composite destination based on pretty much the same wildcard used for mirrored
 * queues, forwarding all the messages to a single notification topic.
 *
 * @author Arturs Licis
 */
public class MirroredQueuesCompositeShowcase {
	public static void main(String[] args) throws JMSException, NamingException, InterruptedException {
		ActiveMQConnectionFactory connectionFactory = EmbeddedXmlBrokerFactory.createConnectionFactory("today/jvm/amq/showcase/activemq-mirrored-composite.xml");

		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session producerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer producerOne = producerSession.createProducer(producerSession.createQueue("amq.simple.queue.one"));
		MessageProducer producerTwo = producerSession.createProducer(producerSession.createQueue("amq.simple.queue.two"));

		Session consumerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer consumer = consumerSession.createConsumer(producerSession.createQueue("amq.simple.queue.>"));
		consumer.setMessageListener(message -> System.out.println("Received a message: " + message));

		Session mirrorConsumerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer mirrorConsumer = mirrorConsumerSession.createConsumer(producerSession.createTopic("amq.simple.queue.wiretap.notifications"));
		mirrorConsumer.setMessageListener(message -> System.out.println("(wiretap) Received a message: " + message));

		producerOne.send(producerSession.createTextMessage("[ONE] This is a first message's payload"));
		producerTwo.send(producerSession.createTextMessage("[TWO] This is a first message's payload"));

		Thread.sleep(100);

		producerSession.close();
		consumerSession.close();

		connection.close();
	}
}
