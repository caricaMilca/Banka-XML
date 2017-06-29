package banka.endpoint;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import banka.model.Banka;
import banka.model.Racun;
import banka.model.TipBanke;
import banka.mt102.GetMT102Request;
import banka.mt102.MT102;
import banka.mt102.MT102Status;
import banka.mt102.PojedinacnoPlacanjeMT102;
import banka.mt102.ZaglavljeMT102;
import banka.mt103.GetMT103Request;
import banka.mt103.MT103;
import banka.mt900.GetMT900Response;
import banka.mt900.MT900;
import banka.mt910.GetMT910Request;
import banka.mt910.MT910;
import banka.nalog.GetNalogRequest;
import banka.nalog.GetNalogResponse;
import banka.nalog.Nalog;
import banka.presek.GetPresekResponse;
import banka.presek.Presek;
import banka.presek.StavkaPreseka;
import banka.presek.ZaglavljePreseka;
import banka.repozitorijumi.BankaRepozitorijum;
import banka.repozitorijumi.MT102Repozitorijum;
import banka.repozitorijumi.MT103Repozitorijum;
import banka.repozitorijumi.MT900Repozitorijum;
import banka.repozitorijumi.MT910Repozitorijum;
import banka.repozitorijumi.NalogRepozitorijum;
import banka.repozitorijumi.RacunRepozitorijum;
import banka.repozitorijumi.ZaglavljeMT102Repozitorijum;
import banka.zahtev.GetZahtevRequest;
import xmlTransformacije.SAXValidator;

@Endpoint
@Component
public class BankaEndpoint {
	private static final String NAMESPACE_URI = "http://paket/nalog";
	private static final String NAMESPACE_URI2 = "http://paket/zahtev";
	private static final String NAMESPACE_URI3 = "http://paket/mt103";
	private static final String NAMESPACE_URI4 = "http://paket/mt102";
	private static final String NAMESPACE_URI5 = "http://paket/mt910";

	@Autowired
	RacunRepozitorijum racunRep;

	@Autowired
	BankaRepozitorijum bankaRep;

	@Autowired
	MT102Repozitorijum mt102Rep;

	@Autowired
	ZaglavljeMT102Repozitorijum zaglavljeMT102Rep;

	@Autowired
	NalogRepozitorijum nalogRep;

	@Autowired
	MT900Repozitorijum mt900Rep;

	@Autowired
	MT910Repozitorijum mt910Rep;

	@Autowired
	MT103Repozitorijum mt103Rep;

	@Autowired
	private WebServiceTemplate webServiceTemplate;
	
	SAXValidator validator = new SAXValidator();

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getNalogRequest")
	@ResponsePayload
	public GetNalogResponse getNalog(@RequestPayload GetNalogRequest request) {

		GetNalogResponse response = new GetNalogResponse();
		Nalog nalog = request.getNalog();
		
		System.out.println("-----Primljen nalog-----");
		
		Racun duznik = racunRep.findByBrojRacuna(nalog.getRacunDuznika());
		Racun primalac = racunRep.findByBrojRacunaAndBanka(nalog.getRacunPrimaoca(), duznik.banka);
		String narodnaBankaPort = bankaRep.findByTip(TipBanke.NARODNA).port;
		if (primalac != null) {
			nalogRep.save(nalog);
			duznik.novoStanje = duznik.novoStanje.subtract(nalog.getIznos());
			racunRep.save(duznik);
			primalac.novoStanje = primalac.novoStanje.add(nalog.getIznos());
			racunRep.save(primalac);
			nalogRep.save(nalog);
			return response;
		}
		String banka3kodPrimalac = nalog.getRacunPrimaoca().substring(0, 3);
		Banka bankaPrimaoca = bankaRep.findByBanka3kod(banka3kodPrimalac);
		Banka bankaDuznika = duznik.banka;
		String obracunskiDuznik = racunRep.findByObracunskiAndBanka(true, bankaDuznika).get(0).brojRacuna;
		String obracunskiPrimaoca = racunRep.findByObracunskiAndBanka(true, bankaPrimaoca).get(0).brojRacuna;

		if (bankaPrimaoca == null
				|| nalog.getIznos().compareTo(duznik.novoStanje.subtract(duznik.rezervisanaSredstva)) == 1)
			return response;
		String uri = "http://localhost:" + narodnaBankaPort + "/ws";
		webServiceTemplate.setDefaultUri(uri);
		if (nalog.isHitno()) { // rtgs

			MT103 mt103 = new MT103();

			mt103.setIdPoruke((UUID.randomUUID().toString()));
			mt103.setSwifKodBankeDuznika(bankaDuznika.swiftKod);
			mt103.setObracunskiRacunBankeDuznika(obracunskiDuznik);
			mt103.setSwiftKodBankePoverioca(bankaPrimaoca.swiftKod);
			mt103.setObracunskiRacunBankePoverioca(obracunskiPrimaoca);
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
			
			boolean parsovano = validator.parse(mt, "mt103");
			if(!parsovano){
				System.out.println("----Nije validan mt103----");
				return null;
			}
			
			System.out.println("------Poslat mt103------");
			GetMT900Response mt900response = (GetMT900Response) webServiceTemplate.marshalSendAndReceive(mt);
			if (mt900response != null) {
				MT900 mt900 = mt900response.getMT900();
				duznik.rezervisanaSredstva = duznik.rezervisanaSredstva.subtract(mt900.getIznos());
				duznik.novoStanje = duznik.novoStanje.subtract(mt900.getIznos());
				racunRep.save(duznik);
				System.out.println("------Primljen mt900------");
			}

		} else {
			ZaglavljeMT102 zaglavljeMT102 = zaglavljeMT102Rep.findBySwiftKodBankePoverioca(bankaPrimaoca.swiftKod);
			boolean udji = false;
			if (zaglavljeMT102 != null) {
				udji = true;
				if (mt102Rep.findByZaglavljeMT102(zaglavljeMT102).getStatus().equals(MT102Status.NA_CEKANJU)) {
					udji = true;
					MT102 mt102 = mt102Rep.findByZaglavljeMT102(zaglavljeMT102);
					mt102.getZaglavljeMT102().getUkupanIznos().add(nalog.getIznos());
					
					PojedinacnoPlacanjeMT102 ppMT102 = new PojedinacnoPlacanjeMT102((UUID.randomUUID().toString()),
							nalog.getDuznik(), nalog.getSvrhaPlacanja(), nalog.getPrimalac(), nalog.getDatumNaloga(),
							nalog.getRacunDuznika(), nalog.getModelZaduzenja(), nalog.getPozivNaBrojZaduzenja(),
							nalog.getRacunPrimaoca(), nalog.getModelOdobrenja(), nalog.getPozivNaBrojOdobrenja(),
							nalog.getIznos(), nalog.getOznakaValute());
					
					mt102.getPojedinacnoPlacanjeMT102().add(ppMT102);
					duznik.rezervisanaSredstva = duznik.rezervisanaSredstva.add(nalog.getIznos());
					racunRep.save(duznik);
					
					if (mt102.getPojedinacnoPlacanjeMT102().size() == 2) {
						GetMT102Request mtr = new GetMT102Request();
						mtr.setMT102(mt102);
						
						boolean parsovano = validator.parse(mtr, "mt102");
						if(!parsovano){
							System.out.println("----Nije validan mt102----");
							return null;
						}
						
						GetMT900Response mt900response = (GetMT900Response) webServiceTemplate.marshalSendAndReceive(mtr);
						
						System.out.println("------Poslat mt102------");
						
						if (mt900response != null) {
							MT900 mt900 = mt900response.getMT900();
							mt900Rep.save(mt900);
							System.out.println("------Primljen mt900------");
							List<PojedinacnoPlacanjeMT102> placanja = mt102.getPojedinacnoPlacanjeMT102();
							for (PojedinacnoPlacanjeMT102 p : placanja) {
								Racun duznika = racunRep.findByBrojRacuna(p.getRacunDuznika());
								duznika.rezervisanaSredstva = duznika.rezervisanaSredstva.subtract(p.getIznos());
								duznika.novoStanje = duznika.novoStanje.subtract(p.getIznos());
								racunRep.save(duznika);
							}

							mt102.setStatus(MT102Status.POSLATA);
						}
					}
					mt102Rep.save(mt102);
				}
			}
			if (!udji) {
				MT102 mt102 = new MT102();
				mt102.setStatus(MT102Status.NA_CEKANJU);
				
				PojedinacnoPlacanjeMT102 ppMT102 = new PojedinacnoPlacanjeMT102((UUID.randomUUID().toString()),
						nalog.getDuznik(), nalog.getSvrhaPlacanja(), nalog.getPrimalac(), nalog.getDatumNaloga(),
						nalog.getRacunDuznika(), nalog.getModelZaduzenja(), nalog.getPozivNaBrojZaduzenja(),
						nalog.getRacunPrimaoca(), nalog.getModelOdobrenja(), nalog.getPozivNaBrojOdobrenja(),
						nalog.getIznos(), nalog.getOznakaValute());
				
				mt102.getPojedinacnoPlacanjeMT102().add(ppMT102);
				ZaglavljeMT102 zm102 = new ZaglavljeMT102((UUID.randomUUID().toString()), bankaDuznika.swiftKod,
						obracunskiDuznik, bankaPrimaoca.swiftKod, obracunskiPrimaoca, nalog.getIznos(),
						nalog.getOznakaValute(), nalog.getDatumNaloga(),
						new Date(Calendar.getInstance().getTimeInMillis()));
				
				mt102.setZaglavljeMT102(zm102);
				zaglavljeMT102Rep.save(zm102);
				mt102Rep.save(mt102);
				duznik.rezervisanaSredstva = duznik.rezervisanaSredstva.add(nalog.getIznos());
				racunRep.save(duznik);
			}
			nalogRep.save(nalog);
		}

		return response;

	}

	private StavkaPreseka setStavkaNalogaIzNaloga(Nalog nalog) {
		StavkaPreseka stavka = new StavkaPreseka();
		stavka.setPozivNaBrojOdobrenja(nalog.getPozivNaBrojOdobrenja());
		stavka.setPozivNaBrojZaduzenja(nalog.getPozivNaBrojZaduzenja());
		stavka.setPrimalac(nalog.getPrimalac());
		stavka.setRacunDuznika(nalog.getRacunDuznika());
		stavka.setRacunPrimaoca(nalog.getRacunPrimaoca());
		stavka.setSvrhaPlacanja(nalog.getSvrhaPlacanja());
		stavka.setModelOdobrenja(nalog.getModelOdobrenja());
		stavka.setModelZaduzenja(nalog.getModelZaduzenja());
		stavka.setPozivNaBrojOdobrenja(nalog.getPozivNaBrojOdobrenja());
		stavka.setPozivNaBrojZaduzenja(nalog.getPozivNaBrojZaduzenja());
		stavka.setIznos(nalog.getIznos());
		stavka.setDuznik(nalog.getDuznik());
		stavka.setDatumValute(nalog.getDatumValute());
		stavka.setDatumNaloga(nalog.getDatumNaloga());
		stavka.setSmer("t");
		return stavka;
	}

	private StavkaPreseka setStavka103Iz103(MT103 nalog) {
		StavkaPreseka stavka = new StavkaPreseka();
		stavka.setPozivNaBrojOdobrenja(nalog.getPozivNaBrojOdobrenja());
		stavka.setPozivNaBrojZaduzenja(nalog.getPozivNaBrojZaduzenja());
		stavka.setPrimalac(nalog.getPrimalac());
		stavka.setRacunDuznika(nalog.getRacunDuznika());
		stavka.setRacunPrimaoca(nalog.getRacunPoverioca());
		stavka.setSvrhaPlacanja(nalog.getSvrhaPlacanja());
		stavka.setModelOdobrenja(nalog.getModelOdobrenja());
		stavka.setModelZaduzenja(nalog.getModelZaduzenja());
		stavka.setPozivNaBrojOdobrenja(nalog.getPozivNaBrojOdobrenja());
		stavka.setPozivNaBrojZaduzenja(nalog.getPozivNaBrojZaduzenja());
		stavka.setIznos(nalog.getIznos());
		stavka.setDuznik(nalog.getDuznik());
		stavka.setDatumValute(nalog.getDatumValute());
		stavka.setDatumNaloga(nalog.getDatumNaloga());
		stavka.setSmer("d");
		return stavka;
	}

	private StavkaPreseka setStavka102Iz102(PojedinacnoPlacanjeMT102 nalog) {
		StavkaPreseka stavka = new StavkaPreseka();
		stavka.setPozivNaBrojOdobrenja(nalog.getPozivNaBrojOdobrenja());
		stavka.setPozivNaBrojZaduzenja(nalog.getPozivNaBrojZaduzenja());
		stavka.setPrimalac(nalog.getPrimalac());
		stavka.setRacunDuznika(nalog.getRacunDuznika());
		stavka.setRacunPrimaoca(nalog.getRacunPoverioca());
		stavka.setSvrhaPlacanja(nalog.getSvrhaPlacanja());
		stavka.setModelOdobrenja(nalog.getModelOdobrenja());
		stavka.setModelZaduzenja(nalog.getModelZaduzenja());
		stavka.setPozivNaBrojOdobrenja(nalog.getPozivNaBrojOdobrenja());
		stavka.setPozivNaBrojZaduzenja(nalog.getPozivNaBrojZaduzenja());
		stavka.setIznos(nalog.getIznos());
		stavka.setDuznik(nalog.getDuznik());
		stavka.setDatumValute(nalog.getDatumNaloga());
		stavka.setDatumNaloga(nalog.getDatumNaloga());
		stavka.setSmer("d");
		return stavka;
	}

	private ZaglavljePreseka setZaglavljePreseka(String racun, Date datum, BigInteger str, Racun r, BigDecimal ns, BigDecimal unt, BigDecimal unk, BigInteger uk, BigInteger ut) {
		ZaglavljePreseka zaglavlje = new ZaglavljePreseka();
		zaglavlje.setBrojPreseka(str);
		zaglavlje.setBrojPromenaNaTeret(ut);
		zaglavlje.setBrojPromenaUKorist(uk);
		zaglavlje.setBrojRacuna(racun);
		zaglavlje.setDatumNaloga(datum);
		zaglavlje.setNovoStanje(ns);
		zaglavlje.setPrethodnoStanje(r.novoStanje);
		zaglavlje.setUkupnoNaTeret(unt);
		zaglavlje.setUkupnoUKorist(unk);
		return zaglavlje;
	}
	
	private List<Nalog> getNalogeZaBankuDanIRacun(Date datum, String brRacuna) {
		List<Nalog> nalozi = new ArrayList<Nalog>();
		List<Nalog> naloziUBazi = nalogRep.findByracunDuznika(brRacuna);
		for (Nalog nalogUBazi : naloziUBazi) {
			if (nalogUBazi.getDatumNaloga().compareTo(datum) == 0) {
				nalozi.add(nalogUBazi);
			}
		}
		return nalozi;
	}

	private List<MT103> getFrom103(Date datum, String brRacuna) {
		List<MT103> mt = new ArrayList<MT103>();
		List<MT103> MTUBazi = mt103Rep.findByracunPoverioca(brRacuna);
		for (MT103 mt103 : MTUBazi) {
			if (mt103.getDatumNaloga().compareTo(datum) == 0) {
				mt.add(mt103);
			}
		}
		return mt;
	}

	private List<PojedinacnoPlacanjeMT102> getFrom102(Date datum, String brRacuna) {
		List<PojedinacnoPlacanjeMT102> mt = new ArrayList<PojedinacnoPlacanjeMT102>();
		List<MT102> MT2UBazi = mt102Rep.findAll();
		for (int i = 0; i < MT2UBazi.size(); i++) {
			for (int j = 0; j < MT2UBazi.get(i).getPojedinacnoPlacanjeMT102().size(); j++) {
				if (MT2UBazi.get(i).getPojedinacnoPlacanjeMT102().get(j).getRacunPoverioca().equals(brRacuna)) {
					if (MT2UBazi.get(i).getPojedinacnoPlacanjeMT102().get(j).getDatumNaloga().compareTo(datum) == 0) {
						mt.add(MT2UBazi.get(i).getPojedinacnoPlacanjeMT102().get(j));
					}
				}
			}
		}
		return mt;
	}

	int velicinaStranice = 4;

	@PayloadRoot(namespace = NAMESPACE_URI2, localPart = "getZahtevRequest")
	@ResponsePayload
	public GetPresekResponse getZahtevRequest(@RequestPayload GetZahtevRequest request) {


		GetPresekResponse response = new GetPresekResponse();
		Presek presek = new Presek();

		Date datum = request.getZahtev().getDatumZahteva();
		String brRacuna = request.getZahtev().getBrojRacuna();
		Racun r = racunRep.findByBrojRacuna(brRacuna);
		int stranica = request.getZahtev().getRedniBrojPreseka().intValue();
		BigInteger str = request.getZahtev().getRedniBrojPreseka();
		List<Nalog> nalozi = getNalogeZaBankuDanIRacun(datum, brRacuna);
		List<MT103> mt103 = getFrom103(datum, brRacuna);
		List<PojedinacnoPlacanjeMT102> mt102 = getFrom102(datum, brRacuna);
		List<StavkaPreseka> stranicaStavki = new ArrayList<StavkaPreseka>();
		List<StavkaPreseka> spreseci = new ArrayList<StavkaPreseka>();
		
		 System.out.println("------Stigao zahtev------");
		
		// ako nema za tu stranicu
		int start = velicinaStranice * (stranica - 1);
		for (int i = 0; i < nalozi.size(); i++) {
			StavkaPreseka stavka = setStavkaNalogaIzNaloga(nalozi.get(i));
			spreseci.add(stavka);
			
		}

		for (int i = 0; i < mt103.size(); i++) {
			StavkaPreseka stavka = setStavka103Iz103(mt103.get(i));
			spreseci.add(stavka);
			
		}

		for (int i = 0; i < mt102.size(); i++) {
			StavkaPreseka stavka = setStavka102Iz102(mt102.get(i));
			spreseci.add(stavka);
			
		}

		if (spreseci.size() < start)
			stranicaStavki = new ArrayList<>();
		else if (spreseci.size() < start + 4) // ako na
																// stranici nema
																// tacno 4
			stranicaStavki = spreseci.subList(start, spreseci.size());
		else // ako je normalno
			stranicaStavki = spreseci.subList(start, start + velicinaStranice);

		System.out.println("-----Krece Zaglavlje-----");
		
		BigDecimal ns = r.novoStanje;
		
		for(int i=0;i<stranicaStavki.size();i++){
		   if(stranicaStavki.get(i).getSmer().equals("d")){
			ns = ns.add(stranicaStavki.get(i).getIznos());
		}else{
		    ns =ns.subtract(stranicaStavki.get(i).getIznos());	
		}
		}
		
		BigDecimal unk = BigDecimal.ZERO;
		BigDecimal unt = BigDecimal.ZERO;
		BigInteger uk = BigInteger.ZERO;
		BigInteger ut = BigInteger.ZERO;
		BigInteger inc = BigInteger.ONE;
		for(int i=0;i<stranicaStavki.size();i++){
			   if(stranicaStavki.get(i).getSmer().equals("d")){
				unk = unk.add(stranicaStavki.get(i).getIznos());
				uk = uk.add(inc);
			}else{
				unt = unt.add(stranicaStavki.get(i).getIznos()); 
                ut = ut.add(inc);
			}
			}
		System.out.println("ut: " + ut);
		System.out.println("uk: "+uk);
        presek.getStavkaPreseka().addAll(stranicaStavki);
        System.out.println("dosao ovdee");
        ZaglavljePreseka z = setZaglavljePreseka(brRacuna, datum, str, r, ns, unt, unk, uk, ut);
        
        presek.setZaglavljePreseka(z);
        
		response.setPresek(presek);
        
		return response;

	}

	@PayloadRoot(namespace = NAMESPACE_URI5, localPart = "GetMT910Request")
	@ResponsePayload
	public void getUplata(@RequestPayload GetMT910Request request) {
		MT910 mt910 = request.getMT900();
		System.out.println("------Primljen mt910------");
		mt910Rep.save(mt910);
	}

	@PayloadRoot(namespace = NAMESPACE_URI3, localPart = "GetMT103Request")
	@ResponsePayload
	public void getMT103(@RequestPayload GetMT103Request request) {
		MT103 mt103 = request.getMT103();
		System.out.println("------Primljen mt103------");
		
		Racun racunPrimaoca = racunRep.findByBrojRacuna(mt103.getRacunPoverioca());
		racunPrimaoca.novoStanje = racunPrimaoca.novoStanje.add(mt103.getIznos());
		racunRep.save(racunPrimaoca);
		mt103Rep.save(mt103);
	}

	@PayloadRoot(namespace = NAMESPACE_URI4, localPart = "getMT102Request")
	@ResponsePayload
	public void getMT102(@RequestPayload GetMT102Request request) {
		MT102 mt102 = request.getMT102();
		zaglavljeMT102Rep.save(mt102.getZaglavljeMT102());
		List<PojedinacnoPlacanjeMT102> placanja = mt102.getPojedinacnoPlacanjeMT102();
		for (PojedinacnoPlacanjeMT102 p : placanja) {
			Racun primaoca = racunRep.findByBrojRacuna(p.getRacunPoverioca());
			primaoca.novoStanje = primaoca.novoStanje.add(p.getIznos());
			racunRep.save(primaoca);
		}
		System.out.println("------Primljen mt102------");
		mt102.setStatus(MT102Status.PRIMLJENA);
		mt102Rep.save(mt102);

	}
}
