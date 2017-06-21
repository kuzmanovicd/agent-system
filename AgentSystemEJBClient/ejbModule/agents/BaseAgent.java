package agents;

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
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import models.ACLMessage;
import models.AID;
import models.Agent;
import utils.AppConst;
import utils.Log;

public abstract class BaseAgent implements Agent {

	private static final long serialVersionUID = 1L;

	protected AID AID;

	@Override
	public abstract void handleMessage(ACLMessage msg);

	public AID getAID() {
		return AID;
	}

	public void setAID(AID AID) {
		this.AID = AID;
	}

	@Override
	public String toString() {
		return "Agent [id=" + AID.getName() + "]";
	}

	protected void sendMessage(ACLMessage message) {
		/*
		ResteasyClient restClient = new ResteasyClientBuilder().build();
		String url = HTTP.gen("localhost:8081", AppConst.WAR_NAME, AppConst.REST_ROOT) + "agents";
		ResteasyWebTarget rtarget = restClient.target(url);
		AgentProxy rest = (AgentProxy) rtarget.proxy(AgentProxy.class);
		rest.sendMessage(message);
		 */
		Connection connection = null;
		Queue queue = null;
		Session session = null;
		MessageProducer producer = null;

		try {
			Context context = new InitialContext();
			ConnectionFactory cf = (ConnectionFactory) context.lookup("jboss/exported/jms/RemoteConnectionFactory");
			queue = (Queue) context.lookup("jms/queue/AgentQueue");
			context.close();
			connection = cf.createConnection("guest", "tiptop123");
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			connection.start();
			producer = session.createProducer(queue);

			TemporaryQueue temp = session.createTemporaryQueue();
			MessageConsumer tempConsumer = session.createConsumer(temp);
			ObjectMessage msg = session.createObjectMessage(message);
			msg.setJMSReplyTo(temp);
			producer.send(msg);
			Message ret = tempConsumer.receive(AppConst.JMS_RESPONSE_TIME);

			tempConsumer.close();
			producer.close();
			session.close();
			connection.close();

		} catch (NamingException | JMSException e) {
			Log.out(this, "@PostConstruct - " + e.getMessage());
		}

	}

}
