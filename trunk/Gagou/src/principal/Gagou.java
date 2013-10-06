/** Pontifícia Universidade Católica de Minas Gerais
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
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import indexer.Indexer;


/** Classe principal da máquina de recuperação de informação. */
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
			
			Graph<String, String> pageRanking = new DirectedSparseGraph<String, String>();
			
			
			crawler = new Crawler(2, 300, pageRanking);
			
			crawler.crawl();
			
			
			//page ranking do grafo da web
			PageRank<String, String> rank = new PageRank<String, String>(pageRanking, 0.1);
			
			for (String v : pageRanking.getVertices()) {
				System.out.println(v + ": " + rank.getVertexScore(v));
			}
			
			
			BufferedWriter outUrls = new BufferedWriter(new FileWriter("urlsColetadas.txt"));
			for (String string : crawler.urls) {
				outUrls.write(string+"\n");
			}
			outUrls.close();
			
			
			Indexer indexer = new Indexer();
			indexer.montarIndex();
			indexer.salvarIndex();
			
			
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
