package banka.repozitorijumi;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import banka.nalog.Nalog;

public interface NalogRepozitorijum extends JpaRepository<Nalog, Long>{

	List<Nalog> findByracunDuznika(String r);
	
}
