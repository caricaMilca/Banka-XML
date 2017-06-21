package banka.servisiImplementacija;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.client.core.WebServiceTemplate;

import banka.model.Faktura;
import banka.model.Firma;
import banka.model.ZaglavljeFakture;
import banka.nalog.GetNalogRequest;
import banka.nalog.Nalog;
import banka.servisi.NalogServis;

@Service
@Transactional
@Component
public class NalogServisImpl implements NalogServis {

	@Autowired
	HttpSession sesija;
	
	@Autowired
	WebServiceTemplate webServiceTemplate;

}
