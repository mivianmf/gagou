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


/** Faz o parse de uma página salva em disco. */
public class Parser {
	
	
	/** Abrir um arquivo de uma página e fazer o parse dela, pegando todos os links e salvando na lista.
	 * @throws Exception */
	public void parse(String url) throws Exception {

		File in = new File("arquivos\\fetchedPages\\" + removeBarra(url) + ".html");
		Document doc = Jsoup.parse(in, null); //abrir o arquivo e fazer o parse nele
		
		
		Elements links = doc.select("a[href]"); //pegar todos os links
		
		for (Element link : links) { //para cada link da lista
			
			String novaURL = link.attr("abs:href");
			
			//validar não precisa, crawler sempre valida antes de passar url pra um fetcher
			
			
			if ( !novaURL.equals("") ) { //nova url não é vazia
				
//				System.out.println("Adicionando nova url na lista: " + novaURL);
				Crawler.urls.add(novaURL);	
				// TODO - Transformar URL (colocar protocolo quando nao tiver)
			}
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
	
}
