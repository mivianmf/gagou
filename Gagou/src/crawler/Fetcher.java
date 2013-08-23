package crawler;


/** 1 - Verificar se a URL é válida
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
			//url é válida
			
			//TODO : como continuar?
		}
	}

	
	/** Verifica se a url recebida é válida (se segue o padrão -> serviço://provedor:porta/caminho)
	 * @param url : URL recebida
	 * @return TRUE: se URL válida
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
