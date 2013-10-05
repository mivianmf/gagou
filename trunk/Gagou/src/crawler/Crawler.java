/** Pontifícia Universidade Católica de Minas Gerais
 * Tópicos 2
 * Coletor
 * Mariana Ramos de Brito - 405820
 * Mivian
 */

package crawler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.validator.routines.UrlValidator;

/** Coletor. <br/>
 * 	1 - Verificar se a URL é válida <br/>
 * 	2 - Resolver DNS<br/>
 *  3 - Resolver Robots.txt<br/>
 *  4 - http request response (dentro do fetcher, no método fetch)<br/>
 *  5 - armazenar o documento */
public class Crawler {

	/** Número de fetchers do coletor */
	private int numFetchers;

	/** Lista de fetchers */
	private List<Fetcher> fetchers;
	
	/** Limite de páginas a serem coletadas. */
	private int limColetar;

	/** Cache com os endereços */
	public Map<String, String> cache;

	/** Lista de URLs, usada por todos os fetchers. */
	public static List<String> urls;

	/**Conjunto de URLs já visitadas*/
	public Map<String, Boolean> visitado;
	
	
	
	/** Construtor. Lê o cache do arquivo de cache salvo em disco.
	 * @param numFetchers: número de fetchers a serem criados.
	 * @throws Exception */
	public Crawler(int numFetchers, int limCol) throws Exception {

		this.numFetchers = numFetchers;
		this.limColetar = limCol;
		

		ObjectInputStream in = new ObjectInputStream(new FileInputStream("cache.data"));
		cache = (TreeMap<String, String>) in.readObject();
		in.close();
		

		fetchers = new ArrayList<Fetcher>();
		for (int i = 0; i < numFetchers; i++) {
			fetchers.add(new Fetcher(i));
		}

		
		urls = new ArrayList<String>();
		lerSementes();
		
		visitado = new HashMap<String, Boolean>();
	}

	/** Lê o arquivo de URLs semente e joga na lista urls.
	 * @throws Exception */
	private void lerSementes() throws Exception {

		BufferedReader in = new BufferedReader(new FileReader("SEEDS.txt"));

		while (in.ready()) {
			urls.add(in.readLine());
		}

		in.close();
	}

	/** Método que faz os passos do coletor.
	 * @throws Exception */
	public void crawl() throws Exception {

		int fetcherId = 0;
		int i = 0; //andar pela lista de urls
		int pagsColetadas = 0;

		
		while (!urls.isEmpty() && (pagsColetadas != limColetar)) { // enquanto existirem urls para serem visitadas

			if (i == urls.size()) {
				throw new Exception("Contador ultrapassou tamanho da lista de urls, ela não está crescendo!");
			}

			String urlRemov = urls.get(i++); // primeira url da lista
//			System.out.println("\nUrl que tirei da lista: " + urlRemov);
			
			String ip = "";

			
			// verificar se é válida
			int resultVerificação = verificarURL(urlRemov);

			
			if (resultVerificação != -1) {

				if (resultVerificação == 0) { // é um ip

					ip = formatarIP(urlRemov);
					
				} else { // é um nome

					// precisa resolver dns

					ip = cache.get(formatarURL(urlRemov));

					if (ip == null) { // não existe esse domínio no cache

						// descobrir ip e salvar no cache

						InetAddress inet = InetAddress.getByName(formatarURL(urlRemov));
						ip = inet.getHostAddress(); // ip correspondente à url removida da lista

						cache.put(formatarURL(urlRemov), ip); // adicionar no cache
					}
				}

				System.out.println("\nFetching url: " + urlRemov);
								
				if(fetchers.get(fetcherId).fetch(ip, urlRemov, visitado) == 0){
					pagsColetadas++;
					visitado.put(urlRemov, true);
				}
				
				
				System.out.println("Coletei " + pagsColetadas + " páginas");
				Thread.sleep(200);

				// --------------------------------------------------------
				// TODO:
				// ver tempo do servidor pra não bloquear nosso ip

				// garantir que vai rodar por todos os fetchers
				fetcherId++;
				fetcherId %= numFetchers;
			}
		}
	}

	/** Formata a url em forma de ip recebida e retorna apenas o número ip.
	 * @param urlRemov
	 * @return String com apenas o ip. */
	private String formatarIP(String urlRemov) {

		// TODO

		return null;
	}

	/** Retorna apenas o domínio da url recebida. */
	public static String formatarURL(String url) {

		String urlFormatada;

		// serviço: provedor:porta caminho

		String[] aux = url.split("/");
		String[] aux2 = aux[2].split(":");

		urlFormatada = aux2[0];

		return urlFormatada.trim();
	}

	/** Verifica se a url recebida é um ip (4 números de no máximo 3 dígitos, separados por .)
	 * @param urlF: domínio
	 * @return true: se sim<br/>
	 * 			false: senão
	 * @throws Exception */
	private boolean eIP(String urlF) throws Exception {

		if (urlF.matches("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})$")) {

			String aux[] = urlF.split("\\.");

			for (String s : aux) {

				if (!((Integer.parseInt(s) >= 0) && (Integer.parseInt(s) <= 255))) { // se o endereço IP está fora do intervalo
					throw new Exception("Endereço IP fora do intervalo.");
				}
			}

			return true;
		}

		return false;
	}

	/** Verifica se a url recebida é válida (se segue o padrão -> serviço://provedor:porta/caminho)
	 * @param url: URL recebida
	 * @return 0: se URL é um ip<br />
	 *         1: se URL é um nome<br/>
	 *        -1: se URL não termina com .br ou se não é página em português
	 * @throws Exception: caso URL esteja fora de formato */
	private int verificarURL(String url) throws Exception {

		String [] schemes = {"ftp", "http", "https", "gopher", "mailto", "news", "nntp", "telnet", "wais", "file", "prospero"};
		UrlValidator validar = new UrlValidator(schemes);
		
		if (validar.isValid(url)) { //é válida

			// http[s]://w+.w+(.w+)*[:dddd][/w+(/w+)*]

			
			if (formatarURL(url).endsWith(".br") || portugues(url)) { //termina com .br ou é português, pode salvar
			   
				if (eIP(formatarURL(url))) { // url é um ip
					return 0;
				}
				else { // url não é ip
					return 1;
				}           
			}
			else { //não salvar na lista
				return -1;
			}
			
			
//			if (!formatarURL(url).endsWith(".br") && !portugues(url)) { 
//				
//				//não termina com .br e não é português mesmo assim
//				
//				return -1;
//			}
//			
//			
//			if (eIP(formatarURL(url))) { // url é um ip
//				return 0;
//			} else { // url não é ip
//				return 1;
//			}
		}
		else {
			return -1;
		}
	}
	

	/** Pega o header http da página e verifica se a língua é português.
	 * @param url
	 * @return true: se sim</br>
	 * 			false: senão. */
	private boolean portugues(String urlRecebida) {
		
//		//nova url com o ip
//		URL url = new URL("http", ip, "/");
//		
//		//abre a conexão
//		URLConnection connection = url.openConnection();
//		
//		//Só quero entrada de dados não quero enviar nada para o servidor
//		connection.setDoInput(true);
//		connection.setDoOutput(false);
//		
//		//Método da requisição e e-mail para contato
//		connection.setRequestProperty("Request-Method", "GET");
//		connection.setRequestProperty("From", "gagoupuc@gmail.com");
//		
//		//Conecta com a URL
//		connection.connect( );
//		
//		//------------------------------------------------------------------------- TODO: como saber se página é html? http response?
//
//		
//		//pegar http header
//		Map<String, List<String>> httpHeader = connection.getHeaderFields();
//
//		
//		List<String> lista = httpHeader.get("Content-Type");
//		String contentType = lista.get(0); //content-type da página
//		
////				lista = httpHeader.get("Content-Language"); //linguagem
////				String linguagem = lista.get(0);
//		//&& linguagem.equals("pt")
		
		return false;
	}

	
	
	
	
	
	
	
	
	
	
	// quando crawler for chamar o fetcher (parte do dispatcher), ele vai
	// verificar se url que está mandando ja existe no cache
	// se sim, manda o ip pro fetcher
	// senão, descobre ip, salva no cache e manda ip pro fetcher
	// fetcher busca a página e retorna
	// página retornada é salva em arquivo pela classe de lidar com arquivo

}
