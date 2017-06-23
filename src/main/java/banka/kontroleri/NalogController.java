package banka.kontroleri;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import banka.servisi.NalogServis;

@RestController
@RequestMapping("/nalog")
public class NalogController {

	@Autowired
	NalogServis nalogServis;
	
}
