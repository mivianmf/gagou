package crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


/** Pega URLs e salva as páginas html correspondentes em arquivo. */
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

	

	/** Obtém o conteúdo html da página e grava em arquivo. */
	public void fetch (String ip, String urlRemov) throws Exception {
		
		//nova url com o ip
		URL url = new URL(ip);
		
		//abre a conexão
		URLConnection connection = url.openConnection();
		
		//Só quero entrada de dados não quero enviar nada para o servidor
		connection.setDoInput(true);
		connection.setDoOutput(false);
		
		//Método da requisição e e-mail para contato
		connection.setRequestProperty("Request-Method", "GET");
		connection.setRequestProperty("From", "rigagou@gmail.com");
		
		//Conecta com a URL
		connection.connect( );
		
		//------------------------------------------------------------------------- TODO: como saber se página é html? http response?
		
		
		//---------------------------- salvar página em uma arquivo dentro de arquivos > fetchedPages
		
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
