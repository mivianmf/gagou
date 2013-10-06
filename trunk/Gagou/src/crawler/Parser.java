/** Pontif�cia Universidade Cat�lica de Minas Gerais
  * materia
  * oq faz
  * Mariana Ramos de Brito - 405820
  */
package crawler;

import java.io.File;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.uci.ics.jung.graph.Graph;


/** Faz o parse de uma p�gina salva em disco. */
public class Parser {
	
	/** grafo da web. */
	Graph<String, String> pageRanking;
	
	
	
	/** Construtor.
	 * @param pageRanking : grafo.
	 */
	public Parser(Graph<String, String> pageRanking) {
		this.pageRanking = pageRanking;
	}


	/** Abrir um arquivo de uma p�gina e fazer o parse dela, pegando todos os links e salvando na lista.
	 * @throws Exception */
	public void parse(String url) throws Exception {

		File in = new File("arquivos\\fetchedPages\\" + removeBarra(url) + ".html");
		Document doc = Jsoup.parse(in, null); //abrir o arquivo e fazer o parse nele
		
		Elements links = doc.select("a[href]"); //pegar todos os links
		
		for (Element link : links) { //para cada link da lista
			
			String novaURL = link.attr("abs:href");
			
			//validar n�o precisa, crawler sempre valida antes de passar url pra um fetcher
			
			if ( !novaURL.equals("") ) { //nova url n�o � vazia
			
				if (novaURL.contains("..")){
					novaURL = voltarPasta (url, novaURL);
				}
//				System.out.println("Adicionando nova url na lista: " + novaURL);
				
				// TODO - Transformar URL (colocar protocolo quando n�o tiver) - OK
				if (!novaURL.startsWith("http") || !novaURL.startsWith("https")|| !novaURL.startsWith("gopher")||
					!novaURL.startsWith("mailto") || !novaURL.startsWith("news") || !novaURL.startsWith("nntp")||
					!novaURL.startsWith("telnet") || !novaURL.startsWith("wais") || !novaURL.startsWith("file")||
					!novaURL.startsWith("prospero") || !novaURL.startsWith("ftp")){
					novaURL = "http://"+novaURL;
				}
			
				
				//inserir nova p�gina no grafo
				String v1 = removeBarra(url), v2 = removeBarra(novaURL);
				
				pageRanking.addEdge(v1 + v2, v1, v2);
				
				Crawler.urls.add(novaURL);	
				
			}
		}
	}
	
	
	private String voltarPasta(String url, String novaURL){
		String resposta = "";
		int contPontos = 0;
		int html = 0;
		
		String [] relativo = novaURL.split("/");
		String [] absoluto = url.split("/");
		
		for (int i = 0; i < relativo.length; i++) {
			if(relativo[i].equals("..")){
				contPontos++;	
			}
		}

		if (absoluto[absoluto.length-1].contains(".html")){
			html = 1;
		}
		
		for (int i = 0; i < absoluto.length-contPontos-html; i++) {

			resposta += absoluto[i]+"/";
		}
		
		for (int i = contPontos; i < relativo.length; i++) {
			resposta += relativo[i]+"/";
			
		}
		
		return resposta;
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
	
}
