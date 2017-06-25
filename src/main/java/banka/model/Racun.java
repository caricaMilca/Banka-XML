package banka.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Racun {

	@Id
	@GeneratedValue
	public Long id;
	
	@Column(unique=true,nullable=false)
	public String brojRacuna;
	
	@ManyToOne
	public Firma firma;
	
	@ManyToOne
	public Banka banka;
	
	@Column(columnDefinition = "boolean default false", insertable = true)
	public Boolean obracunski = false;
	
	public String valuta;

	public BigDecimal novoStanje;
	
	public BigDecimal rezervisanaSredstva;
	
	public Racun() {
		super();
	}
	
	
}
