package banka.endpoint;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import banka.model.Banka;
import banka.model.Racun;
import banka.mt103.GetMT103Request;
import banka.mt103.MT103;
import banka.mt900.GetMT900Response;
import banka.mt900.MT900;
import banka.nalog.GetNalogRequest;
import banka.nalog.GetNalogResponse;
import banka.nalog.Nalog;
import banka.repozitorijumi.BankaRepozitorijum;
import banka.repozitorijumi.NalogRepozitorijum;
import banka.repozitorijumi.RacunRepozitorijum;

@Endpoint
@Component
public class BankaEndpoint {
	private static final String NAMESPACE_URI = "http://paket/nalog";

	@Autowired 
	RacunRepozitorijum racunRep;
	
	@Autowired 
	BankaRepozitorijum bankaRep;
	
	@Autowired 
	NalogRepozitorijum nalogRep;
	
	@Autowired
	private WebServiceTemplate webServiceTemplate;
	
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getNalogRequest")
	@ResponsePayload
	public ResponseEntity<GetNalogResponse> getNalog(@RequestPayload GetNalogRequest request) {
		
		GetNalogResponse response = new GetNalogResponse();
		Nalog nalog = request.getNalog();
		Racun duznik = racunRep.findByBrojRacuna(nalog.getRacunDuznika());
		Racun primalac = racunRep.findByBrojRacunaAndBanka(nalog.getRacunPrimaoca(), duznik.banka);
		String narodnaBankaPort = bankaRep.findByTip("NARODNA").port;
		if(primalac != null){
			nalogRep.save(nalog);
			duznik.novoStanje = duznik.novoStanje.subtract(nalog.getIznos());
			racunRep.save(duznik);
			primalac.novoStanje = primalac.novoStanje.add(nalog.getIznos());
			racunRep.save(primalac);
			return new ResponseEntity<GetNalogResponse>(response, HttpStatus.ACCEPTED);
		}
		String banka3kodPrimalac = nalog.getRacunPrimaoca().substring(0, 3);
		Banka bankaPrimaoca = bankaRep.findByBanka3kod(banka3kodPrimalac);
		Banka bankaDuznika = duznik.banka;
		if(bankaPrimaoca == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		
			if (nalog.isHitno()) {  //rtgs
				
				MT103 mt103 = new MT103();
			
				mt103.setIdPoruke((UUID.randomUUID().toString()));
				mt103.setSwifKodBankeDuznika(bankaDuznika.swiftKod);
				mt103.setObracunskiRacunBankeDuznika(bankaDuznika.obracunskiRacun.brojRacuna);
				mt103.setSwiftKodBankePoverioca(bankaPrimaoca.swiftKod);
				mt103.setObracunskiRacunBankePoverioca(bankaPrimaoca.obracunskiRacun.brojRacuna);
				mt103.setDuznik(nalog.getDuznik());
				mt103.setSvrhaPlacanja(nalog.getSvrhaPlacanja());
				mt103.setPrimalac(nalog.getPrimalac());
				mt103.setDatumNaloga(nalog.getDatumNaloga());
				mt103.setDatumValute(nalog.getDatumValute());
				mt103.setRacunDuznika(nalog.getRacunDuznika());
				mt103.setModelZaduzenja(nalog.getModelZaduzenja());
				mt103.setPozivNaBrojZaduzenja(nalog.getPozivNaBrojZaduzenja());
				mt103.setRacunPoverioca(nalog.getRacunPrimaoca());
				mt103.setModelOdobrenja(nalog.getModelOdobrenja());
				mt103.setPozivNaBrojOdobrenja(nalog.getPozivNaBrojOdobrenja());
				mt103.setIznos(nalog.getIznos());
				mt103.setSifraValute(nalog.getOznakaValute());
				
				duznik.rezervisanaSredstva = duznik.rezervisanaSredstva.add(nalog.getIznos());
				racunRep.save(duznik);
				
				GetMT103Request mt = new GetMT103Request();		
				mt.setMT103(mt103);
				String uri = "http://localhost:" + narodnaBankaPort + "/ws";
				webServiceTemplate.setDefaultUri(uri);
				GetMT900Response mt900response = (GetMT900Response) webServiceTemplate.marshalSendAndReceive(mt);			
				MT900 mt900 = mt900response.getMT900();
				
				duznik.rezervisanaSredstva = duznik.rezervisanaSredstva.subtract(mt900.getIznos());
				duznik.novoStanje = duznik.novoStanje.subtract(mt900.getIznos());
				
			} else {
				//clearing
			}
		
		return new ResponseEntity<>(HttpStatus.OK);

	}

}
