package banka.endpoint;

import org.springframework.stereotype.Component;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import banka.nalog.GetNalogRequest;
import banka.nalog.GetNalogResponse;

@Endpoint
@Component
public class BankaEndpoint {
	private static final String NAMESPACE_URI = "http://firma/nalog";
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getNalogRequest")
	@ResponsePayload
	public GetNalogResponse getNalog(@RequestPayload GetNalogRequest request) {
		System.out.println("poslaliiii ++++++++++++++++++++++++++++++++++++++++++++");
		return null;
		
	}

}
