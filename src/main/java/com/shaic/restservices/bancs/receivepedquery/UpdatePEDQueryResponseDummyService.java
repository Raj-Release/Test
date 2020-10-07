package com.shaic.restservices.bancs.receivepedquery;

import java.io.IOException;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;
import com.shaic.restservices.GLXDocRequest;

@Path("/claims")
@Stateless
public class UpdatePEDQueryResponseDummyService {
	
	@POST
    @Path("/dummyupdatePEDQueryResponse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public String callUpdatePEDQueryResponseService(GLXDocRequest detailObj) throws JsonParseException, JsonMappingException, IOException, JSONException {
		String response = UpdatePEDQueryResponseService.callQueryResponseService();
		return response;
	}

}
