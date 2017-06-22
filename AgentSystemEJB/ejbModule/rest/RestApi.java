package rest;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import models.ACLMessage;
import utils.Log;

// this class is purely for testing purposes
@RequestScoped
@Path("/test")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RestApi {

	@GET
	@Path("/oke")
	public String oke() {
		return RestApi.class.getSimpleName() + " - jeblo ga nebo kako sad ovako :D hehheehhe";
	}

	@POST
	@Path("/message")
	public String senden(String message) {
		Gson gson = new GsonBuilder().create();
		ACLMessage acl = gson.fromJson(message, ACLMessage.class);
		Log.out(this, acl.toString());
		return message;
	}
}
