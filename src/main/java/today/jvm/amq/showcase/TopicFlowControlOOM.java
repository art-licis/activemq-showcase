package today.jvm.amq.showcase;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.NamingException;

/**
 * Use case showing ActiveMQ won't be able to hold big volume of 100k messages.
 *
 * @author Arturs Licis
 */
public class TopicFlowControlOOM {
	public static void main(String[] args) throws JMSException, NamingException, InterruptedException {
		ActiveMQConnectionFactory connectionFactory = EmbeddedXmlBrokerFactory.createConnectionFactory("today/jvm/amq/showcase/activemq-flow-control.xml");
		connectionFactory.setProducerWindowSize(10 * 1024 * 1024);

		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session producerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer producer = producerSession.createProducer(producerSession.createTopic("amq.topic"));
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		Session consumerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer consumer = consumerSession.createConsumer(consumerSession.createTopic("amq.topic"));
		consumer.setMessageListener(m -> {
			try {
				int seq = m.getIntProperty("seq");
				if (seq % 1000 == 0) System.out.println("Received message #" + seq);
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		for (int i = 1; i <= 1000000; i++) {
			BytesMessage bm = producerSession.createBytesMessage();
			bm.writeBytes(new byte[100 * 1024]);
			bm.setIntProperty("seq", i);
			producer.send(bm);
			if (i % 1000 == 0) System.out.println("Sent message #" + i);
		}

		Thread.sleep(60 * 60 * 1000);

		System.out.println("Closing sessions");
		consumerSession.close();
		producerSession.close();


		connection.close();
	}
}
