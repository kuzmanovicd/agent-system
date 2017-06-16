package utils;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import logger.Log;

public class HTTP {
	
	private final static Boolean print = true;
	
	public static Response OK_200(Object obj) {
		return Response.status(200).entity(obj).build();
	}
	
	public static Response CREATED_201(Object obj) {
		return Response.status(201).entity(obj).build();
	}
	
	public static Response ACCEPTED_202(Object obj) {
		return Response.status(202).entity(obj).build();
	}

	public static Response BAD_400(Object obj) {
		return Response.status(400).entity(obj).build();
	}
	
	public static Response UNAUTHORIZED_401(Object obj) {
		return Response.status(401).entity(obj).build();
	}
	
	public static Response FORBIDDEN_403(Object obj) {
		return Response.status(403).entity(obj).build();
	}
	
	public static Response NOT_FOUND_404(Object obj) {
		return Response.status(404).entity(obj).build();
	}
	
	public static Response NOT_ALLOWED_405(Object obj) {
		return Response.status(405).entity(obj).build();
	}
	
	public static Response post(ResteasyClient client, String url, Object obj) {
		Entity data = Entity.entity(obj, MediaType.APPLICATION_JSON);
		Response response = target(client, url).request(MediaType.APPLICATION_JSON).post(data);
		Log.out("$$$$$ POST - " + url + " - " + response.getStatus());
		return response;
	}
	
	public static Response get(ResteasyClient client, String url) {
		Response response = target(client, url).request().get();
		Log.out("$$$$$ GET - " + url + " - " + response.getStatus());
		return response;
	}
	
	private static ResteasyWebTarget target(ResteasyClient client, String url) {
		ResteasyWebTarget target = client.target(url);
		return target;
	}
	
	public static String gen(String address, String appName, String apiRoot) {
		return "http://" + address + "/" + appName + "/" + apiRoot + "/";
	}
}


