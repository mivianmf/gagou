/** Pontif�cia Universidade Cat�lica de Minas Gerais
  * materia
  * qual classe e o que faz
  * Mariana Ramos de Brito - 405820
  */


package principal;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;

import crawler.Crawler;

/** Classe principal da m�quina de recupera��o de informa��o. */
public class Gagou {

	/** Main.
	 * @param args */
	@SuppressWarnings("static-access")
	public static void main(String[] args) {

		//TODO armazenar melhor - colocar em um BD simples
		//TODO meta dados - interpretar a tag meta dos arquivos html
		//TODO Page Rank
		
		//TODO link relativo - ok
		
		Crawler crawler = null;
		try {
			
			crawler = new Crawler(2, 1000);
			
			crawler.crawl();
			BufferedWriter outUrls = new BufferedWriter(new FileWriter("urlsColetadas.txt"));
			for (String string : crawler.urls) {
				outUrls.write(string+"\n");
			}
			outUrls.close();
			
			System.out.println("\n\nTerminei. =D");
			
		} 
		catch (Exception e) {
			System.out.println("\n\nERRO: " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			
			//salvar cache do crawler em arquivo
			
			try {
				
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("cache.data"));
				out.writeObject(crawler.cache);
				out.flush();
				out.close();
				
				BufferedWriter outUrls = new BufferedWriter(new FileWriter("urlsColetadas.txt"));
				for (String string : crawler.urls) {
					outUrls.write(string+"\n");
				}
				outUrls.close();
				
			} 
			catch (Exception e) {
				System.out.println("\n\nERRO: " + e.getMessage());
//				e.printStackTrace();
			}

		}

	}

}