/** Pontif�cia Universidade Cat�lica de Minas Gerais
  * materia
  * oq faz
  * Mariana Ramos de Brito - 405820
  */
package principal;

import java.util.ArrayList;


/** Guarda p�gina e p�ginas apontadas por ela. */
public class LigacoesURL {
	
	/** URL dessa p�gina. */
	public String urlPag;
	
	/** Lista com URLs das p�ginas que essa p�gina aponta. */
	public ArrayList<String> aponta = new ArrayList<String>();

	
	
	/** Construtor.
	 * @param urlPag
	 * @param aponta
	 */
	public LigacoesURL(String urlPag) {
		this.urlPag = urlPag;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		String s = urlPag + "\n";
		
		for (String s1 : aponta) {
			
			s += (s1 + "\n");
		}
		
		return s;
	}
	
}
