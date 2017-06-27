package banka.endpoint;

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
import banka.mt102.PojedinacnoPlacanjeMT102;
import banka.mt102.ZaglavljeMT102;
import banka.mt103.GetMT103Request;
import banka.mt103.MT103;
import banka.mt900.GetMT900Response;
import banka.mt900.MT900;
import banka.nalog.GetNalogRequest;
import banka.nalog.GetNalogResponse;
import banka.nalog.Nalog;
import banka.presek.GetPresekResponse;
import banka.presek.Presek;
import banka.presek.StavkaPreseka;
import banka.repozitorijumi.BankaRepozitorijum;
import banka.repozitorijumi.MT102Repozitorijum;
import banka.repozitorijumi.MT900Repozitorijum;
import banka.repozitorijumi.NalogRepozitorijum;
import banka.repozitorijumi.RacunRepozitorijum;
import banka.repozitorijumi.ZaglavljeMT102Repozitorijum;
import banka.zahtev.GetZahtevRequest;

@Endpoint
@Component
public class BankaEndpoint {
	private static final String NAMESPACE_URI = "http://paket/nalog";
	private static final String NAMESPACE_URI2 = "http://paket/zahtev";

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
	private WebServiceTemplate webServiceTemplate;

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getNalogRequest")
	@ResponsePayload
	public GetNalogResponse getNalog(@RequestPayload GetNalogRequest request) {

		GetNalogResponse response = new GetNalogResponse();
		Nalog nalog = request.getNalog();
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
			GetMT900Response mt900response = (GetMT900Response) webServiceTemplate.marshalSendAndReceive(mt);
			MT900 mt900 = mt900response.getMT900();

			duznik.rezervisanaSredstva = duznik.rezervisanaSredstva.subtract(mt900.getIznos());
			duznik.novoStanje = duznik.novoStanje.subtract(mt900.getIznos());

		} else {
			ZaglavljeMT102 zaglavljeMT102 = zaglavljeMT102Rep.findBySwiftKodBankePoverioca(bankaPrimaoca.swiftKod);
			if (zaglavljeMT102 != null && !mt102Rep.findByZaglavljeMT102(zaglavljeMT102).isPoslata()) {
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
					GetMT900Response mt900response = (GetMT900Response) webServiceTemplate.marshalSendAndReceive(mtr);
					MT900 mt900 = mt900response.getMT900();
					mt900Rep.save(mt900);
					duznik.rezervisanaSredstva = duznik.rezervisanaSredstva.subtract(mt900.getIznos());
					duznik.novoStanje = duznik.novoStanje.subtract(mt900.getIznos());
					mt102.setPoslata(true);
				}
				mt102Rep.save(mt102);

			} else {
				MT102 mt102 = new MT102();
				mt102.setPoslata(false);
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
		return stavka;
	}
	
	private List<Nalog> getNalogeZaBankuDanIRacun(Banka banka, Date datum, String brRacuna) {
		List<Nalog> nalozi = new ArrayList<Nalog>();
		List<Nalog> naloziUBazi = nalogRep.findByracunDuznika(brRacuna);
		for (Nalog nalogUBazi : naloziUBazi) {
			if (nalogUBazi.getDatumNaloga().compareTo(datum) == 0) {
				nalozi.add(nalogUBazi);
				}
		}
		return nalozi;
	}
	
	int velicinaStranice = 4;

	@PayloadRoot(namespace = NAMESPACE_URI2, localPart = "getZahtevRequest")
	@ResponsePayload
	public GetPresekResponse getZahtevRequest(@RequestPayload GetZahtevRequest request) {
		System.out.println("asfas safas");

		GetPresekResponse response = new GetPresekResponse();
		Presek presek = new Presek();

		Date datum = request.getZahtev().getDatumZahteva();
		String brRacuna = request.getZahtev().getBrojRacuna();
		int stranica = request.getZahtev().getRedniBrojPreseka().intValue();
		Racun r = racunRep.findByBrojRacuna(request.getZahtev().getBrojRacuna());

		Banka banka = r.banka;
		// Banka banka = getCurrentBank(brRacuna);
	
		List<Nalog> nalozi = getNalogeZaBankuDanIRacun(banka, datum, brRacuna);
		
		List<Nalog> stranicaNaloga = null;

		// ako nema za tu stranicu
		int start = velicinaStranice * (stranica - 1);

		if (nalozi.size() < start)
			stranicaNaloga = new ArrayList<>();
		else if (nalozi.size() < start + 4) //ako na stranici nema tacno 4
			stranicaNaloga = nalozi.subList(start, nalozi.size());
		else //ako je normalno
			stranicaNaloga = nalozi.subList(start, start + velicinaStranice);

		for (int i = 0; i < stranicaNaloga.size(); i++) {
			StavkaPreseka stavka = setStavkaNalogaIzNaloga(stranicaNaloga.get(i));
			presek.getStavkaPreseka().add(stavka);
		}
		response.setPresek(presek);
        for(int i = 0;i<response.getPresek().getStavkaPreseka().size(); i++){
		System.out.println(response.getPresek().getStavkaPreseka().get(i).getModelZaduzenja());
        }
		return response;

		
	}

}
