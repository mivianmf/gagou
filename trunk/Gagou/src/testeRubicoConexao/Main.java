package testeRubicoConexao;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import crawler.Crawler;

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
		File in = new File("arquivos\\fetchedPages\\" +"-2140274636.html");
		Document doc;
		try {
			doc = Jsoup.parse(in, null);
		 //abrir o arquivo e fazer o parse nele
		
		Elements links = doc.select("meta"); //pegar todos os links
		
		for (Element link : links) { //para cada link da lista
			
			String novaURL = link.attr("name");
			
			//validar não precisa, crawler sempre valida antes de passar url pra um fetcher
			System.out.println(""+novaURL);
			
			if ( !novaURL.equals("") ) { //nova url não é vazia
				
//				System.out.println("Adicionando nova url na lista: " + novaURL);
				//Crawler.urls.add(novaURL);	
				// TODO - Transformar URL (colocar protocolo quando nao tiver)
			}
        }
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}  
}
