/** Pontifícia Universidade Católica de Minas Gerais
  * materia
  * qual classe e o que faz
  * Mariana Ramos de Brito - 405820
  */


package principal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map;



import crawler.Crawler;
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
		
		
		
		
		/** Lista com todas as URLs e suas ligações. */
		ArrayList<LigacoesURL> listaURLs = new ArrayList<LigacoesURL>();
		
		
		Crawler crawler = null;
		try {
			
			
			crawler = new Crawler(2, 15);
			
			crawler.crawl(listaURLs);

			
			//tenho a lista de urls e suas ligações, posso rodar algoritmo de pagerank
			criarArqPR(listaURLs);
			
			JungPageRank jpr = new JungPageRank(new File("listaURLs.txt"), 10, 0.1, 0.15);
			Map<String, Double> pageRanking = jpr.compute();
			
			BufferedWriter out = new BufferedWriter(new FileWriter("PageRanking pelo grafo.txt"));
			for (String s : pageRanking.keySet()) {
				out.write(s + "\t" + pageRanking.get(s) + "\n");
			}
			out.flush();
			out.close();
			
			
			BufferedWriter outUrls = new BufferedWriter(new FileWriter("urlsColetadas.txt"));
			for (String string : crawler.urls) {
				outUrls.write(string+"\n");
			}
			outUrls.flush();
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

	/** montar arquivo para pagerank 
	 * @param listaURLs 
	 * @throws Exception */
	private static void criarArqPR(ArrayList<LigacoesURL> listaURLs) throws Exception {
		
		//cada linha tem token, separados por tab
		//primeiro é o source, outros na linha são os destinos
		
		BufferedWriter out = new BufferedWriter(new FileWriter("listaURLs.txt"));
		
		for (LigacoesURL l : listaURLs) {
			
			out.write(l.urlPag);
			
			for (String s : l.aponta) {
				
				out.write("\t" + s);
			}
			
			out.write("\n");
		}
		
		out.flush();
		out.close();
		
	}

}
