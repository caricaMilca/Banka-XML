package banka.repozitorijumi;

import org.springframework.data.jpa.repository.JpaRepository;

import banka.nalog.Nalog;

public interface NalogRepozitorijum extends JpaRepository<Nalog, Long>{

	
	
}
