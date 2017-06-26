package banka.repozitorijumi;

import org.springframework.data.jpa.repository.JpaRepository;

import banka.model.Banka;
import banka.model.TipBanke;

public interface BankaRepozitorijum extends JpaRepository<Banka, Long> {

	Banka findByBanka3kod(String banka3kodPrimalac);

	Banka findByTip(TipBanke narodna);

}
