package mdb;

import javax.ejb.LocalBean;
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
	public boolean sendMessageToAgent(ACLMessage message) {
		init();
		try {
			//String str = new Gson().toJson(message);

			TemporaryQueue temp = session.createTemporaryQueue();
			MessageConsumer tempConsumer = session.createConsumer(temp);

			//TextMessage msg = session.createTextMessage(str);
			ObjectMessage msg = session.createObjectMessage(message);
			msg.setJMSReplyTo(temp);
			producer.send(msg);

			Message ret = tempConsumer.receive(AppConst.JMS_RESPONSE_TIME);
			if (ret != null) {
				//Log.out(this, "Return value: " + ret.getBooleanProperty("success"));
				return ret.getBooleanProperty("success");
			} else {
				return false;
			}

		} catch (JMSException e) {
			Log.out(this, "JMS Exception" + e.getMessage());
		}
		destroy();
		return false;
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
			Log.out(this, "@reply - JMSException");
		}
		destroy();

	}

}
