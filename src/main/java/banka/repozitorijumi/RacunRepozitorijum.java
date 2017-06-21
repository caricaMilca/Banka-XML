package banka.repozitorijumi;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import banka.model.Firma;
import banka.model.Racun;

public interface RacunRepozitorijum extends JpaRepository<Racun, Long> {

	List<Racun> findByFirma(Firma attribute);

}
