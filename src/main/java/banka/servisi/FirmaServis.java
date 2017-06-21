package banka.servisi;

import org.springframework.http.ResponseEntity;

import banka.model.Firma;


public interface FirmaServis {

	ResponseEntity<Firma> login(String port, String lozinka);


}
