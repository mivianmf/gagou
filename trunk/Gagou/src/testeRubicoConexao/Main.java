package testeRubicoConexao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

/*TUTORIAL QUE FOI SEGUIDO
 * http://www.guj.com.br/articles/5
 */

public class Main {

	
	public static void main (String [ ] args){
/*	
		try{
			//URL a ser colhida
			String urlString = "http://www.guj.com.br";
			URL url = new URL (urlString);
			
			//Abre a coleção
			URLConnection connection = url.openConnection();
			
			//Só quero entrada de dados não quero enviar nada para o servidor
			connection.setDoInput(true);
			connection.setDoOutput(false);
			
			//Metodo da requisição e e-mail para contato
			connection.setRequestProperty("Request-Method", "GET");
			connection.setRequestProperty("From", "rigagou@gmail.com");
			
			//Conecta com a URL
			connection.connect( );
			
			//Coleto os dados HTML do site
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			//Leio todo o codigo html
			StringBuffer newData = new StringBuffer(10000);  
			String s = "";  
			while (null != ((s = br.readLine()))) {  
			    newData.append(s);
			    newData.append("/n");
			}  
			br.close();
			
			PrintWriter out = new PrintWriter(System.out, true);
			
			// imprime o codigo resultante  
			out.println(new String(newData));  
			  
			// imprime o numero do resultado  
			
			
		}
		catch (Exception e){
			System.out.println(e.getMessage( ));
		}
		*/		  		  

		
	}  
}
