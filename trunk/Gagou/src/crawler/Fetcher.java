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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import principal.LigacoesURL;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.BaseRobotsParser;
import crawlercommons.robots.SimpleRobotRulesParser;


/** Pega URLs e salva as páginas html correspondentes em arquivo. */
public class Fetcher {
	
	
	/** Construtor.
	 * @param pageRanking : grafo.
	 * @param id: Id do Fetcher
	 * @throws Exception */
	public Fetcher(int id) throws Exception {
		
	}

	
	public String montarCaminho(String url){
		
//		System.out.println("entrei montar caminho, url = " + url);
		
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
		
		
		
		String cam = url.substring(comeco).trim();
		
//		System.out.println("antes tratamento barra: " + cam);
//		
//		
//		if ((int)cam.charAt(cam.length()-1) != 47) {
//			cam += "/";
//		}
//		
//		
//		System.out.println("vou retornar: " + cam);
		
		return cam;
	}
	
	
	/** Obtém o conteúdo html da página e grava em arquivo. 
	 * @param listaURLs */
	public int fetch (String ip, String urlRemov, Map<String, Boolean> visitado, ArrayList<LigacoesURL> listaURLs) throws Exception {
		
		try {
			
			LigacoesURL lu = new LigacoesURL(urlRemov);
			
//			System.out.println("estou no fetch, url = " + urlRemov);
			
//			System.out.println("\n\nSACI: " + urlRemov + " e " + visitado.get(urlRemov));
			
			if(visitado.get(urlRemov) == null){
				
//				System.out.println("entrei if2");
				
				//Pegar protocolo 
				String [] aux = urlRemov.split(":");
				String protocolo = aux[0];
				
				//Pegar caminho
				String caminho = montarCaminho(urlRemov);
				
//				System.out.println("caminho: " + caminho);
				
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
				
//				System.out.println("consegui conectar com url");
				
				//pegar http header
				Map<String, List<String>> httpHeader = connection.getHeaderFields();
				
				List<String> lista = httpHeader.get("Content-Type");
				
				
				if (lista != null) { //deu certo pegar o http header
					
					String contentType = lista.get(0); //content-type da página

					// TODO - Testar text/html caixa alta OK
					if (contentType.toLowerCase().startsWith("text/html")) { //só olhar páginas em html e em português
						
						//resolver robots
						BaseRobotRules robotRules = resolveRobots(ip, urlRemov, contentType);
						
//						System.out.println("resolve robots de " + urlRemov + " ok");
						
						if ( (robotRules != null) && (robotRules.isAllowed(urlRemov)) ) {
							
							//---------------------------- salvar página em um arquivo dentro de arquivos > fetchedPages
							
//							System.out.println("entreie if de ter robots, vou tentar abrir arquivo...");
							
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
							
							Parser parser = new Parser();
							parser.parse(urlRemov, lu);
							
							listaURLs.add(lu); //adiciona essa url e suas ligações na lista
							
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
		catch (FileNotFoundException e) {
			return 10;
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
