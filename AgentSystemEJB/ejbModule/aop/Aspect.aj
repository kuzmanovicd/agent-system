package aop;

import javax.ejb.EJB;
import javax.inject.Inject;

import beans.AppManagerBean;
import utils.Log;

public aspect Aspect {

	 pointcut newAgent(): execution (* agents.AgentManager.*(..));
	 
	 pointcut updateRunningAgents(): get (* agents.AgentManager.myRunningAgents);
	 
	 pointcut allRestCalls(): execution (* rest.*.*(..)) ;
	 
	 before():newAgent() { 
		 Log.out("AOP2 - " + thisJoinPoint.getTarget().getClass().getName()); 

	 }
	
	 after():updateRunningAgents() {
		 
	 }
	 
	 before():allRestCalls() {
		 
	 }
	 
	 
}
