package banka.repozitorijumi;

import org.springframework.data.jpa.repository.JpaRepository;

import banka.model.Firma;

public interface FirmaRepozitorijum extends JpaRepository<Firma, Long> {

	Firma findByPortAndLozinka(String port, String lozinka);

	Firma findByPib(String pibDobavljaca);

}
