package crawler;


/** 1 - Verificar se a URL � v�lida
 *  2 - Resolver DNS
 *  3 - Resolver Robots.txt
 *  4 - http request response
 *  5 - armazenar o documento */
public class Fetcher {
	
	/** Construtor.
	 * @param url : URL recebida
	 * @throws Exception 
	 */
	public Fetcher(String url) throws Exception {
		
		
		
		if ( verificarURL(url) ) {
			//url � v�lida
			
			//TODO : como continuar?
		}
	}

	
	/** Verifica se a url recebida � v�lida (se segue o padr�o -> servi�o://provedor:porta/caminho)
	 * @param url : URL recebida
	 * @return TRUE: se URL v�lida
	 * @throws Exception : caso URL esteja fora de formato
	 */
	private boolean verificarURL(String url) throws Exception {
		
		if (url.matches("^(http(s)?://\\w+(\\.\\w+)*\\.\\w+(:\\d{1,4})?(/\\w+(/\\w+)*)?)$")) {
			// http[s]://w+.w+(.w+)*[:dddd][/w+(/w+)*]
			
			return true;
		}
		else {
			throw new Exception("URl fora de formato.");
		}
	}

}
