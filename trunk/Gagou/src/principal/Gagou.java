/** Pontifícia Universidade Católica de Minas Gerais
  * materia
  * qual classe e o que faz
  * Mariana Ramos de Brito - 405820
  */


package principal;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import crawler.Crawler;

/** Classe principal da máquina de recuperação de informação. */
public class Gagou {

	/** 
	 * @param args */
	public static void main(String[] args) {

		//TODO
		
		Crawler crawler = null;
		
		
		try {
			
			crawler = new Crawler(2);
			
			crawler.crawl();
			
			
			
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
			} 
			catch (Exception e) {
				System.out.println("\n\nERRO: " + e.getMessage());
//				e.printStackTrace();
			}

		}

	}

}
