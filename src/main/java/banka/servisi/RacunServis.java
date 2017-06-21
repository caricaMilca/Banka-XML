package banka.servisi;

import java.util.List;

import org.springframework.http.ResponseEntity;

import banka.model.Racun;

public interface RacunServis {

	ResponseEntity<List<Racun>> sviRacuniFirme();

}
