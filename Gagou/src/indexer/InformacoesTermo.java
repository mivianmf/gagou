package indexer;

import java.util.LinkedList;
import java.util.List;


public class InformacoesTermo {
	
	/** MD5 do nome do documento. */
	String documento;
	
	/** Frequ�ncia do termo no documento. */
	int TF;
	
	/** Posi��es do documento que o termo aparece. */
	List<Integer> posicoes;
	
	
	/** Construtor. */
	public InformacoesTermo(String documento, int TF) {
		this.documento = documento;
		this.TF = TF;
		posicoes = new LinkedList<Integer>();
	}
}
