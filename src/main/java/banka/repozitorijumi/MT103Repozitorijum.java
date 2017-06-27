package banka.repozitorijumi;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import banka.mt103.MT103;

public interface MT103Repozitorijum extends JpaRepository<MT103, Long> {

	
	List<MT103> findByracunPoverioca(String r);
}
