package crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.BaseRobotsParser;
import crawlercommons.robots.SimpleRobotRulesParser;
import edu.uci.ics.jung.graph.Graph;


/** Pega URLs e salva as páginas html correspondentes em arquivo. */
public class Fetcher {
	
	
	/** grafo da web. */
	Graph<String, String> pageRanking;
	
	
	/** Construtor.
	 * @param pageRanking : grafo.
	 * @param id: Id do Fetcher
	 * @throws Exception */
	public Fetcher(int id, Graph<String, String> pageRanking) throws Exception {
		this.pageRanking = pageRanking;
	}

	
	public String montarCaminho(String url){
		
		int comeco=0;
		int cont = 0;
		
		for (int i = 0; i < url.length(); i++) {
			
			if((int)url.charAt(i) == 47){
				cont++;
			}	
			if (cont == 3){
				comeco = i;
				i = url.length();
			}
		}
		
		return url.substring(comeco);
	}
	
	
	/** Obtém o conteúdo html da página e grava em arquivo. */
	public int fetch (String ip, String urlRemov, Map<String, Boolean> visitado) throws Exception {
		
		if(visitado.get(urlRemov) == null){
			
			//Pegar protocolo 
			String [] aux = urlRemov.split(":");
			String protocolo = aux[0];
			
			//Pegar caminho
			String caminho = montarCaminho(urlRemov);
			
			//nova url com o ip
			URL url = new URL(protocolo, ip, caminho); // TODO - NEM SEMPRE E HTTP OK
			 
			//abre a conexão
			URLConnection connection = url.openConnection();
			
			//Só quero entrada de dados não quero enviar nada para o servidor
			connection.setDoInput(true);
			connection.setDoOutput(false);
			
			// TODO - Batizar o coletor e passar o nome na HTTP Request
			//Método da requisição e e-mail para contato
			connection.setRequestProperty("Request-Method", "GET");
			connection.setRequestProperty("From", "gagoupuc@gmail.com");
			
			//Conecta com a URL
			try{
				connection.connect( );
			}
			catch (Exception e){
				return 4;
			}
			
			//pegar http header
			Map<String, List<String>> httpHeader = connection.getHeaderFields();
			
			List<String> lista = httpHeader.get("Content-Type");
			
			
			if (lista != null) { //deu certo pegar o http header
				
				String contentType = lista.get(0); //content-type da página

				// TODO - Testar text/html caixa alta OK
				if (contentType.toLowerCase().startsWith("text/html")) { //só olhar páginas em html e em português
					
					//resolver robots
					BaseRobotRules robotRules = resolveRobots(ip, urlRemov, contentType);
					
					if ( (robotRules != null) && (robotRules.isAllowed(urlRemov)) ) {
						
						//---------------------------- salvar página em um arquivo dentro de arquivos > fetchedPages
						
						BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						
						BufferedWriter out = new BufferedWriter(new FileWriter("arquivos\\fetchedPages\\" + removeBarra(urlRemov) + ".html"));

						
						String s = "";  
						
						while ((s = br.readLine()) != null) {  
							out.write(s);
							out.write("\n");
						}  
						
						br.close();
						out.flush();
						out.close();
						
						Parser parser = new Parser(pageRanking);
						parser.parse(urlRemov);
						return 0;
					}
					else
					{
						return 1;
					}
					
				}
				else
				{
					return 2;
				}
			}
			else
			{
				return 3;
			}
		}
		else
		{
			return 5;
		}
	}

	
	private String removeBarra(String urlRemov) {
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < urlRemov.length(); i++) {
			if((int)urlRemov.charAt(i) != 47 && (int)urlRemov.charAt(i) != 58){
				sb.append(urlRemov.charAt(i));
			}
		}
		
		return sb.toString();
	}

	
	/* Formata a url recebida e tira o http://
	 * @param urlRemov
	 * @return String formatada. */
	/*private String formatarURL(String urlRemov) {

		int i;
		
		for (i = 0; i < urlRemov.length(); i++) {
			
			if (urlRemov.charAt(i) == ':') {
				break;
			}
		}
		
		i += 3; //pular duas barras
		
		return urlRemov.substring(i);		
	}
*/
	/** Método que lida com a parte de resolver o robots.txt 
	 * @param ip 
	 * @param urlRemov 
	 * @param contentType 
	 * @throws Exception */
	private BaseRobotRules resolveRobots(String ip, String urlRemov, String contentType) throws Exception {
		
		//---------------------------- pegar robots
		
		
		//abre a conexão
		try {
			
			//nova url para o robots
			URL url = new URL("http", ip, "/robots.txt");
			
			
			URLConnection conRobots = url.openConnection();
			
//			System.out.println("conectei com o robots");
			
			//Só quero entrada de dados não quero enviar nada para o servidor
			conRobots.setDoInput(true);
			conRobots.setDoOutput(false);
			
			//Método da requisição e e-mail para contato
			conRobots.setRequestProperty("Request-Method", "GET");
			conRobots.setRequestProperty("From", "gagoupuc@gmail.com");
			
			//Conecta com a URL do robots
			conRobots.connect();
			
			
			//---------------------------- salvar robots em um arquivo dentro de arquivos > fetchedPages > temp e pegar os raw bytes
			
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(conRobots.getInputStream()));
				BufferedWriter out = new BufferedWriter(new FileWriter("arquivos\\fetchedPages\\temp\\" + removeBarra(urlRemov) + "_robots.txt"));
				
				
				String s = "";  
				
				while (s != null) {  
					out.write(s);
					out.write("\n");
					s = br.readLine();
				}  
				
				br.close();
				out.flush();
				out.close();
				
				FileInputStream fis = new FileInputStream(new File("arquivos\\fetchedPages\\temp\\" + removeBarra(urlRemov) + "_robots.txt")); //abrir arquivo de robots gravado
				int qtdBytes = fis.available();
				
				byte[] content = new byte[qtdBytes];
				fis.read(content);
				
				
				BaseRobotsParser robotsParser = new SimpleRobotRulesParser();
				
				fis.close();
				
				//transforma robots.txt recebido em um objeto de regras
				//url de onde veio o conteúdo, raw bytes do arquivo de robots.txt, content-type do http response, name of crawler 
				return robotsParser.parseContent(urlRemov, content, contentType, "Gagou");
			} 
			catch (FileNotFoundException fnf) {
				return null;
			} 
			catch (IOException io) {
				return null;
			}
			
		}
		catch(MalformedURLException m) {
			return null;
		}
		
	}

}
