package banka.repozitorijumi;

import org.springframework.data.jpa.repository.JpaRepository;

import banka.model.Banka;

public interface BankaRepozitorijum extends JpaRepository<Banka, Long> {

	Banka findByBanka3kod(String banka3kodPrimalac);

	Banka findByTip(String string);

}
