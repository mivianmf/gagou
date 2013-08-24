package crawler;

import java.net.InetAddress;


/** 1 - Verificar se a URL é válida
 *  2 - Resolver DNS
 *  3 - Resolver Robots.txt
 *  4 - http request response
 *  5 - armazenar o documento */
public class Fetcher {
	
	
	private int id;
	
	
	/** Construtor.
	 * @param id: Id do Fetcher
	 * @throws Exception 
	 */
	public Fetcher(int id) throws Exception {
		
		this.id = id;		
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

	public void fetch (String url) throws Exception {
		
		if ( verificarURL(url) ) {
			//url é válida
			
			
			//Resolve DNS
			InetAddress inet = InetAddress.getByName(formatarURL(url));
		    String ip = inet.getHostAddress();
			
			//TODO : como continuar?
		}
	}
	
	public String formatarURL(String url){
		
		String urlFormatada;
		
		//  serviço: provedor:porta   caminho
		
		String [] aux = url.split("/");
		String [] aux2 = aux[2].split(":");
		
		urlFormatada = aux2[0];
		
		return urlFormatada;
		
		
	}
	
}
