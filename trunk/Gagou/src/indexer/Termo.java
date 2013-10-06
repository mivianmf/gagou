package indexer;

import java.util.LinkedList;
import java.util.List;




public class Termo implements Comparable<Termo>{
	
	/** Termo. */
	String termo;
	
	/** Id. */
	int id;
	
	/** Lista de documentos no qual o termo aparece. */
	List<InformacoesTermo> documentos;
	
	
	
	/** Construtor. */
	public Termo(String termo, int id){
		
		this.termo = termo;
		this.id = id;
		documentos = new LinkedList<InformacoesTermo>();
	}
	
	
	/** Retorna true se esse termo já tem esse documento em sua lista <br/>
	 * False, senão. */
	public boolean contains(String documento){
		
		for (int i = 0; i < documentos.size(); i++) {
			
			if(documentos.get(i).documento.equals(documento)){
				return true;
			}
		}
		
		return false;
	}
	
	
	/** Retorna objeto com o TF e posições do termo no documento recebido. */
	public InformacoesTermo getInformacoesTermo(String documento){
		
		for (int i = 0; i < documentos.size(); i++) {
			
			if(documentos.get(i).documento.equals(documento)){
				return documentos.get(i);
			}
		}
		return null;
	}

	
	@Override
	public int compareTo(Termo o) {
		if(this.id < o.id){
			return -1;
		}
		
		if(this.id > o.id){
			return 1;
		}
		
		return 0;
	}

}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	private double IDF;
//	private List<Documento> documentos;
//	
//	public Termo(String termo){
//		this.termo = termo;
//		documentos = new LinkedList<Documento>();
//	}
//	
//	public List<Documento> getDocumentos(){
//		return documentos;
//	}
//	
//	public void setDocumentos(List<Documento> documentos){
//		this.documentos = documentos;
//	}
//	
//	public String getTermo() {
//		return termo;
//	}
//	public void setTermo(String termo) {
//		this.termo = termo;
//	}
//	public double getTF() {
//		return TF;
//	}
//	public void setTF(double tF) {
//		TF = tF;
//	}
//	public double getIDF() {
//		return IDF;
//	}
//	public void setIDF(double iDF) {
//		IDF = iDF;
//	}
//}
