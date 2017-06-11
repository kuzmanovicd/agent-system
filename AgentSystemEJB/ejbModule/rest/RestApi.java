package rest;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

// this class is purely for testing purposes
@RequestScoped
@Path("/c")
public class RestApi {

	@GET
	@Path("/oke")
	public String oke() {
		return RestApi.class.getSimpleName() + " - jeblo ga nebo kako sad ovako :D hehheehhe";
	}
}
