package crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


/** Pega URLs e salva as p�ginas html correspondentes em arquivo. */
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

	

	/** Obt�m o conte�do html da p�gina e grava em arquivo. */
	public void fetch (String ip, String urlRemov) throws Exception {
		
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
		
		
		//---------------------------- salvar p�gina em uma arquivo dentro de arquivos > fetchedPages
		
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		BufferedWriter out = new BufferedWriter(new FileWriter("arquivos\\fetchedPages\\" + urlRemov + ".html"));
		
		
		String s = "";  
		
		while ((s = br.readLine()) != null) {  
			out.write(s);
			out.write("\n");
		}  
		
		br.close();
		out.flush();
		out.close();
		
	}
}
