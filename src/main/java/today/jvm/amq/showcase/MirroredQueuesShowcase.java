package today.jvm.amq.showcase;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.NamingException;

/**
 * Example of how to use Mirrored Queues. See activemq-mirrored.xml classpath resource.
 *
 * For each queue, there's a mirrored destination (topic) with the same name but prefix '.mirror'
 * (as defined in XML configuration).
 *
 * @author Arturs Licis
 */
public class MirroredQueuesShowcase {
	public static void main(String[] args) throws JMSException, NamingException, InterruptedException {
		ActiveMQConnectionFactory connectionFactory = EmbeddedXmlBrokerFactory.createConnectionFactory("today/jvm/amq/showcase/activemq-mirrored.xml");

		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session producerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer producer = producerSession.createProducer(producerSession.createQueue("amq.simple.queue"));

		Session consumerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer consumer = consumerSession.createConsumer(producerSession.createQueue("amq.simple.queue"));
		consumer.setMessageListener(message -> System.out.println("Received a message: " + message));

		Session mirrorConsumerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer mirrorConsumer = mirrorConsumerSession.createConsumer(producerSession.createTopic("amq.simple.queue.mirror"));
		mirrorConsumer.setMessageListener(message -> System.out.println("Received a message (mirror): " + message));

		producer.send(producerSession.createTextMessage("This is a first message's payload"));

		Thread.sleep(100);

		producerSession.close();
		consumerSession.close();

		connection.close();
	}
}
