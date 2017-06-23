package banka.endpoint;

import org.springframework.stereotype.Component;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import banka.nalog.GetNalogRequest;
import banka.nalog.GetNalogResponse;
import banka.nalog.Nalog;

@Endpoint
@Component
public class BankaEndpoint {
	private static final String NAMESPACE_URI = "http://paket/nalog";

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getNalogRequest")
	@ResponsePayload
	public GetNalogResponse getNalog(@RequestPayload GetNalogRequest request) {
		System.out.println("poslaliiii ++++++++++++++++++++++++++++++++++++++++++++");
		Nalog n = request.getNalog();
		if (n.isHitno()) {//rtgs, odmah narodnoj
			
		} else {
			
		}
		return null;

	}

}
