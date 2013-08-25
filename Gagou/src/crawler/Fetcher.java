package crawler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


/** Pega URL e salva a p�gina html correspondente em uma String (retornada). */
public class Fetcher {
	
	
	/** Id do Fetcher. */
	private int id;
	
	
	/** Construtor.
	 * @param id: Id do Fetcher
	 * @throws Exception 
	 */
	public Fetcher(int id) throws Exception {
		
		this.id = id;		
	}

	

	/** Obt�m o conte�do html da p�gina e retorna o mesmo em uma String. */
	public String fetch (String ip) throws Exception {
		
		//nova url com o ip
		URL url = new URL(ip);
		
		//abre a conex�o
		URLConnection connection = url.openConnection();
		
		//S� quero entrada de dados n�o quero enviar nada para o servidor
		connection.setDoInput(true);
		connection.setDoOutput(false);
		
		//M�todo da requisi��o e e-mail para contato
		connection.setRequestProperty("Request-Method", "GET");
		connection.setRequestProperty("From", "rigagou@gmail.com");
		
		//Conecta com a URL
		connection.connect( );
		
		//------------------------------------------------------------------------- TODO: como saber se p�gina � html? http response?
		
		
		//------- pega o html e salva numa string, retorna string pro coletor
		
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		
		//Leio todo o c�digo html
		
		StringBuffer pagina = new StringBuffer();  
		
		String s = "";  
		
		while ((s = br.readLine()) != null) {  
			pagina.append(s);
			pagina.append("\n");
		}  
		
		br.close();
		
		
		return pagina.toString();
		
	}
}
