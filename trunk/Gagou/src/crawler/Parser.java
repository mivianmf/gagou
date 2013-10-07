/** Pontifícia Universidade Católica de Minas Gerais
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

import principal.LigacoesURL;


/** Faz o parse de uma página salva em disco. */
public class Parser {
	
	
	
	/** Construtor.
	 * @param pageRanking : grafo.
	 */
	public Parser() {
		
	}


	/** Abrir um arquivo de uma página e fazer o parse dela, pegando todos os links e salvando na lista.
	 * @param lu 
	 * @throws Exception */
	public void parse(String url, LigacoesURL lu) throws Exception {

		File in = new File("arquivos\\fetchedPages\\" + removeBarra(url) + ".html");
		Document doc = Jsoup.parse(in, null); //abrir o arquivo e fazer o parse nele
		
		Elements links = doc.select("a[href]"); //pegar todos os links
		
		for (Element link : links) { //para cada link da lista
			
			String novaURL = link.attr("abs:href");
			
			//validar não precisa, crawler sempre valida antes de passar url pra um fetcher
			
			if ( !novaURL.equals("") ) { //nova url não é vazia
			
//				System.out.println("antes de entrar if: " + novaURL);
				
				if (novaURL.contains("..")){
					
//					System.out.println("antes de mudar saci: " + novaURL);
					
					novaURL = voltarPasta (url, novaURL);
					
//					System.out.println("depois de editar: " + novaURL);
				}
//				System.out.println("Adicionando nova url na lista: " + novaURL);
				
				// TODO Transformar URL (colocar protocolo quando não tiver) - OK
				if (!novaURL.startsWith("http") && !novaURL.startsWith("https")&& !novaURL.startsWith("gopher")&&
					!novaURL.startsWith("mailto") && !novaURL.startsWith("news") && !novaURL.startsWith("nntp")&&
					!novaURL.startsWith("telnet") && !novaURL.startsWith("wais") && !novaURL.startsWith("file")&&
					!novaURL.startsWith("prospero") && !novaURL.startsWith("ftp")){
					novaURL = "http://"+novaURL;
				}
			
				Crawler.urls.add(novaURL);
				
				lu.aponta.add(novaURL);
				
				
//				System.out.println("adicionei nova url memso '-'");
//				for (String s : Crawler.urls) {
//					System.out.println(s);
//				}
				
				//inserir nova página no grafo
//				String v1 = removeBarra(url), v2 = removeBarra(novaURL);
//				
//				pageRanking.addEdge(v1 + v2, v1, v2);
				
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
