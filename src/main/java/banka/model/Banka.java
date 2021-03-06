package banka.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement()
@Entity
public class Banka {

	@Id
	@GeneratedValue
	public Long id;

	@Column(unique = true, nullable = false)
	public String banka3kod;
	
	@Column(unique = true, nullable = false) //4 velika slova
	public String banka4kod;
	
	public String swiftKod; //8 : banka4kod + drzavaOznaka + NMOznaka

	public String naziv;
	
	public String port;

	public String lozinka;
	
	@Enumerated(EnumType.STRING)
	public TipBanke tip;

	@XmlTransient
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "banka", cascade = CascadeType.ALL)
	@JsonIgnore
	public Set<Racun> racuni = new HashSet<Racun>();

	public Banka() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	
}
