package aop;

import java.lang.annotation.Annotation;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.aspectj.lang.reflect.MethodSignature;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Annotated;

import beans.AppManagerBean;
import utils.Log;

public aspect Aspect {

	 pointcut newAgent(): execution (* agents.AgentManager.*(..));
	 
	 pointcut updateRunningAgents(): get (* agents.AgentManager.myRunningAgents);
	 
	 pointcut allRestCalls(): execution (* rest.*.*(..)) ;
	 
	 pointcut restCalls(): @annotation (Path);
	 
	 pointcut postConstruct(): @annotation (PostConstruct);
	 
	 pointcut preDestroy(): @annotation (PreDestroy);
	 
	 
	 before():restCalls() {
		 
		 if(thisJoinPoint.getSignature().toLongString().contains("status()")) {
			 return;
		 }
		 
		 try {
			 MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
			 String output = "";
			 
			 for(Annotation a : signature.getMethod().getAnnotations()) {
				 if(a.annotationType().getSimpleName().equals("Path")) {
					 
					 output += a.toString().split("value=")[1].replace(')', ' ');
					 
				 } else {
					 output += a.annotationType().getSimpleName() + " ";
				 }
			 }
			 
			 Log.out("##### REST - " + thisJoinPoint.getSignature().toShortString() + " " + output);
		 } catch (Exception e) {
			 Log.out("##### REST - " + thisJoinPoint.getSignature().toString());
		 }
	 }
	 
	 after():postConstruct() {
		 Log.out("@PostConstruct - " + thisJoinPoint.getSignature().toString());
	 }
	 
	 after():preDestroy() {
		 Log.out("@PreDestroy - " + thisJoinPoint.getSignature().toString());
	 }
	 
	 

	 
	 
}
