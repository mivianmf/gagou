/** Pontifícia Universidade Católica de Minas Gerais
  * materia
  * qual classe e o que faz
  * Mariana Ramos de Brito - 405820
  */



//TODO armazenar melhor - colocar em um BD não relacional

//TODO meta dados - interpretar a tag meta dos arquivos html

//Page Rank 

//link relativo

//Stemming em portugues

//Verificar pontuação entre números (10.050.304 e 10,34)

//Retirar a URL InformaçõesTermo e fazer um MD5 disso

//TODO Compactar o índice como texto e não como obj

//O que é a tolerância utilizada no pagerank? VIDE: http://jung.sourceforge.net/doc/api/edu/uci/ics/jung/algorithms/scoring/AbstractIterativeScorer.html#tolerance
//Minimum change from one step to the next; if all changes are <= tolerance, no further updates will occur. Defaults to 0.001.




package principal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

		
		/** Lista com todas as URLs e suas ligações. */
		ArrayList<LigacoesURL> listaURLs = new ArrayList<LigacoesURL>();
		
		
		Crawler crawler = null;
		try {
			
			
			crawler = new Crawler(2, 5);
			
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
//			indexer.mostrarTela();
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

	
	/** Converte URL da página pro seu MD5.
	  * @param name : string a ser convertida
	  * @return conversão da string pra md5
	 * @throws Exception */
	public static String converteMD5(String name) throws Exception { 
		
		MessageDigest md;
		String md5;
		
		try {
			
			md = MessageDigest.getInstance("MD5");
		  
			BigInteger hash = new BigInteger(1, md.digest(name.getBytes()));  
			
			md5 = hash.toString(16);
		} 
		catch (NoSuchAlgorithmException e) {
			throw new Exception("Deu erro no converte MD5");
		}
		
		return md5;
	}
}

