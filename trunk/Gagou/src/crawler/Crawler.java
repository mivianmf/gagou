/** Pontif�cia Universidade Cat�lica de Minas Gerais
 * T�picos 2
 * Coletor
 * Mariana Ramos de Brito - 405820
 * Mivian
 */

package crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

import org.apache.commons.validator.routines.UrlValidator;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.BaseRobotsParser;
import crawlercommons.robots.SimpleRobotRulesParser;

/** Coletor. */
/**
 * 1 - Verificar se a URL � v�lida 2 - Resolver DNS 3 - Resolver Robots.txt 4 -
 * http request response (dentro do fetcher, no m�todo fetch) 5 - armazenar o
 * documento
 */
public class Crawler {

	/** N�mero de fetchers do coletor */
	private int numFetchers;

	/** Cache com os endere�os */
	public Map<String, String> cache;

	/** Lista de fetchers */
	private List<Fetcher> fetchers;

	/** Lista de URLs semente */
	private List<String> urls;

	/**
	 * Construtor. L� o cache do arquivo de cache salvo em disco.
	 * 
	 * @param numFetchers
	 *            : n�mero de fetchers a serem criados.
	 * @throws Exception
	 */
	public Crawler(int numFetchers) throws Exception {

		this.numFetchers = numFetchers;

		ObjectInputStream in = new ObjectInputStream(new FileInputStream(
				"cache.data"));
		cache = (TreeMap<String, String>) in.readObject();

		fetchers = new ArrayList<Fetcher>();
		for (int i = 0; i < numFetchers; i++) {
			fetchers.add(new Fetcher(i));
		}

		urls = new ArrayList<String>();
		lerSementes();
	}

	/**
	 * L� o arquivo de URLs semente e joga na lista urls.
	 * 
	 * @throws Exception
	 */
	private void lerSementes() throws Exception {

		BufferedReader in = new BufferedReader(new FileReader("SEEDS.txt"));

		while (in.ready()) {
			urls.add(in.readLine());
		}

		in.close();
	}

	/**
	 * M�todo que faz os passos do coletor.
	 * 
	 * @throws Exception
	 */
	public void crawl() throws Exception {

		int fetcherId = 0;
		int i = 0;

		while (!urls.isEmpty()) { // enquanto existirem urls para serem
									// visitadas

			if (i == urls.size()) {
				throw new Exception(
						"Contador ultrapassou tamanho da lista de urls, ela n�o est� crescendo!");
			}

			String urlRemov = urls.get(i++); // primeira url da lista
			String ip = "";

			System.out.println("\nurl = " + urlRemov);

			// verificar se � v�lida
			int resultVerifica��o = verificarURL(urlRemov);

			if (resultVerifica��o != -1) {

				if (resultVerifica��o == 0) { // � um ip

					ip = formatarIP(urlRemov);
				} else { // � um nome

					// precisa resolver dns

					ip = cache.get(urlRemov);

					if (ip == null) { // n�o existe esse dom�nio no cache

						// descobrir ip e salvar no cache

						InetAddress inet = InetAddress.getByName(urlRemov
								.substring(7));
						ip = inet.getHostAddress(); // ip correspondente � url
													// removida da lista

						cache.put(urlRemov, ip); // adicionar no cache
					}
				}

				System.out.println("ip = " + ip);

				fetchers.get(fetcherId).fetch(ip, urlRemov); // mandar ip para
																// um
																// fetcher
																// dispon�vel
				Thread.sleep(3000);

				// --------------------------------------------------------
				// TODO:
				// ver tempo do servidor pra n�o bloquear nosso ip

				// garantir que vai rodar por todos os fetchers
				fetcherId++;
				fetcherId %= numFetchers;
			}

			else {
				// fazer rodar o prox ip?
			}
		}
	}

	/**
	 * Formata a url em forma de ip recebida e retorna apenas o n�mero ip.
	 * 
	 * @param urlRemov
	 * @return
	 */
	private String formatarIP(String urlRemov) {

		// TODO

		return null;
	}

	/** Retorna apenas o dom�nio da url recebida. */
	private String formatarURL(String url) {

		String urlFormatada;

		// servi�o: provedor:porta caminho

		String[] aux = url.split("/");
		String[] aux2 = aux[2].split(":");

		urlFormatada = aux2[0];

		return urlFormatada;
	}

	/**
	 * Verifica se a url recebida � um ip (4 n�meros de no m�ximo 3 d�gitos,
	 * separados por .)
	 * 
	 * @param urlF
	 *            : dom�nio
	 * @return
	 * @throws Exception
	 */
	private boolean eIP(String urlF) throws Exception {

		if (urlF.matches("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})$")) {

			String aux[] = urlF.split("\\.");

			for (String s : aux) {

				if (!((Integer.parseInt(s) >= 0) && (Integer.parseInt(s) <= 255))) { // se
																						// o
																						// endere�o
																						// IP
																						// est�
																						// fora
																						// do
																						// intervalo
					throw new Exception("Endere�o IP fora do intervalo.");
				}
			}

			return true;
		}

		return false;
	}

	/**
	 * Verifica se a url recebida � v�lida (se segue o padr�o ->
	 * servi�o://provedor:porta/caminho)
	 * 
	 * @param url
	 *            : URL recebida
	 * @return 0: se URL � um ip<br />
	 *         1: se URL � um nome
	 *        -1: se URL n�o � cont�m .br
	 * @throws Exception
	 *             : caso URL esteja fora de formato
	 */
	private int verificarURL(String url) throws Exception {

		if (this.formatarURL(url).endsWith(".br")) {

			return -1;

		} else {

			String [] schemes = {"gopher", "mailto", "news", "nntp", "telnet", "wais", "file", "prospero"};
			UrlValidator validar = new UrlValidator(schemes);
			
			if (validar.isValid(url)) {

				// http[s]://w+.w+(.w+)*[:dddd][/w+(/w+)*]

				if (eIP(formatarURL(url))) { // url � um ip
					return 0;
				} else { // url n�o � ip
					return 1;
				}
			} else {
				throw new Exception("URl fora de formato.");
			}
		}
	}
	// crawler tem uma lista de fetchers - ok
	// crawler tem uma lista de urls que vai ficar mandando (lida do arquivo de
	// sementes) - ok

	// quando crawler for chamar o fetcher (parte do dispatcher), ele vai
	// verificar se url que est� mandando ja existe no cache
	// se sim, manda o ip pro fetcher
	// sen�o, descobre ip, salva no cache e manda ip pro fetcher
	// fetcher busca a p�gina e retorna
	// p�gina retornada � salva em arquivo pela classe de lidar com arquivo

}
