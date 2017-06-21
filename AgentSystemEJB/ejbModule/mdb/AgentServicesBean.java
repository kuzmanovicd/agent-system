package mdb;

import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import models.ACLMessage;
import utils.AppConst;
import utils.Log;

/**
 * Session Bean implementation class AgentServicesBean
 */
@Stateless
@LocalBean
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
@Lock(LockType.READ)
public class AgentServicesBean implements AgentServicesBeanLocal {

	/**
	 * Default constructor. 
	 */
	private Connection connection;
	private MessageProducer producer;
	private Queue queue;
	private Session session;

	public AgentServicesBean() {
		// TODO Auto-generated constructor stub
	}

	//@PostConstruct
	private void init() {
		try {
			Context context = new InitialContext();
			ConnectionFactory cf = (ConnectionFactory) context.lookup("jboss/exported/jms/RemoteConnectionFactory");
			queue = (Queue) context.lookup("jms/queue/AgentQueue");
			context.close();
			connection = cf.createConnection("guest", "tiptop123");
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			connection.start();
			producer = session.createProducer(queue);
		} catch (NamingException | JMSException e) {
			Log.out(this, "@PostConstruct exception");
		}
	}

	//@PreDestroy
	private void destroy() {
		try {
			producer.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
			Log.out(this, "Destroy - Greska JMSException!!!");
		}
	}

	@Override
	@Lock(LockType.WRITE)
	public boolean sendMessageToAgent(ACLMessage message) {
		init();
		boolean isOkay = true;
		try {
			TemporaryQueue temp = session.createTemporaryQueue();
			MessageConsumer tempConsumer = session.createConsumer(temp);
			ObjectMessage msg = session.createObjectMessage(message);
			msg.setJMSReplyTo(temp);
			producer.send(msg);

			Message returnMessage = tempConsumer.receive(AppConst.JMS_RESPONSE_TIME);
			if (returnMessage != null) {
				isOkay = returnMessage.getBooleanProperty("success");
			} else {
				isOkay = false;
			}

		} catch (JMSException e) {
			Log.out(this, "JMS Exception" + e.getMessage());
			isOkay = false;
		}
		destroy();
		return isOkay;
	}

	@Override
	public void reply(Message msg, Boolean success) {
		init();
		TextMessage text;
		try {
			text = session.createTextMessage();
			text.setBooleanProperty("success", success);

			if (success) {
				text.setText("Uspesno");
			} else {
				text.setText("Neuspesno");
			}

			MessageProducer replyProducer = session.createProducer(null);
			replyProducer.send(msg.getJMSReplyTo(), text);
		} catch (JMSException e) {
			Log.out(this, "@reply " + e.getMessage());
		}
		destroy();

	}

}
