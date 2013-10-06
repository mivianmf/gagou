package indexer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import stemming.ptstemmer.Stemmer;


// stemming - ok
// remove pontuação - ok
// tudo minúsculo - ok

public class Indexer {
	
	/** Vocabulário e lista invertida. */
	private Map<String, Termo> map;
	
	
	
	
	/** Construtor. */
	public Indexer () throws Exception {
		
		map = new HashMap<String, Termo>();
	}

	
	/** Monta o vocabulário e a lista invertida.
	 * @throws Exception   */
	public void montarIndex() throws Exception {
		

		trasformaTextoSimples();
		
		
		File diretorio = new File(".//arquivos//arquivosSimples");
		diretorio.mkdirs();
		File[] arquivos = diretorio.listFiles();
				
		
		int id = 0;
		for (File arquivo : arquivos) { //para cada arquivo na pasta de arquivos
			
			BufferedReader in = new BufferedReader(new FileReader(arquivo)); //abre
			
			StringBuffer texto = new StringBuffer();
			while(in.ready()){
				texto.append(in.readLine() + " ");
			}
			in.close();
			
			
			StringTokenizer tokenizer = new StringTokenizer(texto.toString());
			int posicao = 0;
			
			while(tokenizer.hasMoreTokens()){
				
				String token = tokenizer.nextToken().toLowerCase(); //coloca tudo em minúscula
				
				boolean pontuacao = apenasPontuacao(token); //retorna true se o termo for apenas pontuação (ou seja, pode pular parte de inserir no hash)
				
				
				if (!pontuacao) { //não é apenas pontuação
					
					token = removePontuacao(token); //remove pontuação
					
					token = stemming(token); //stemming
					
					Termo termo = map.get(token); //procurar token no hash
					
					if (termo == null) { //token não existe no hash ainda
						
						termo = new Termo(token, id++);
						
						InformacoesTermo iF = new InformacoesTermo(arquivo.getName(), 1); //nome do arquivo e TF (1, pois é a primeira vez que foi encontrado)
						iF.posicoes.add(posicao); //posição do termo no arquivo
						
						posicao++; //próxima posição
						
						termo.documentos.add(iF); //adicionar na lista de documentos que esse termo aparece
						
						map.put(token, termo); //adicionar no hash
						
					}
					else { //token já existe no hash
						
						if (termo.contains(arquivo.getName())) { //token já tem esse arquivo na sua lista
							
							InformacoesTermo iF = termo.getInformacoesTermo(arquivo.getName());
							
							iF.posicoes.add(posicao); //nova posição
							
							posicao++; //posição do próximo termo
							iF.TF++; //TF aumenta em 1
							
						}
						else { //token não tem esse arquivo na sua lista
							
							InformacoesTermo iF = new InformacoesTermo(arquivo.getName(), 1);
							
							iF.posicoes.add(posicao); //posição do termo
	
							posicao++; //posição do próximo termo
							termo.documentos.add(iF); //adiciona na lista do termo
						}
					}
					
				} //fim if !pontuacao (ignorar tokens que são apenas pontuação)
				
			} //fim while hasMoreT
			
		} // fim para cada arquivo na pasta de arquivos

		
	}
	
	
	/** Lê arquivos html (na pasta arquivos/fetchedPages) e transforma em arquivos de texto simples (na pasta arquivos/arquivosSimples). 
	 * @throws Exception */
	private void trasformaTextoSimples() throws Exception {		
		
		File dir = new File(".//arquivos//fetchedPages");
		File[] arquivos = dir.listFiles();
		
		for (File f : arquivos) { //pra cada arquivo de página na pasta
			
			if (f.isFile()) { //apenas se for arquivo, abrir para leitura				
				
				Document doc = Jsoup.parse(f, "UTF-8");
				
				
				String simples = doc.body().text(); //texto simples da página html
				
				
				BufferedWriter out = new BufferedWriter(new FileWriter("arquivos\\arquivosSimples\\" + f.getName() + ".txt"));
				out.write(simples);
				out.flush();
				out.close();
			}
		}
		
	}


	/** Faz stemming na palavra.
	  * @param token : palavra. 
	 * @throws Exception */
	private String stemming(String token) throws Exception {
		
		Stemmer st = Stemmer.StemmerFactory(Stemmer.StemmerType.ORENGO);
		st.enableCaching(1000);
		
		return st.getWordStem(token);
		
//		System.out.println("\npalavra antes do stemming: " + token + "\npalavra depois do stemming: " + st.getWordStem(token));
	}
	

	/** Remove pontuação do token.
	  * @param token : palavra. 
	  * @return String com pontuação removida. */
	private String removePontuacao(String token) {
		
		if (contemPontuacao(token)) {
			
			//não é apenas pontuação, remover pontuação:
			
			StringBuffer sb = new StringBuffer();
			
			for (int i = 0; i < token.length(); i++) {
				
				if (!ePontuacao(token.charAt(i))) { //não é pontuação, pode append no stringbuffer
					sb.append(token.charAt(i));
				}
			}
			
			return sb.toString(); //termo sem pontuação
		}
		
		return token; //não tem pontuação, retorna original
	}


	/** Verifica se o char recebido é sinal de pontuação.
	  * @param c : char
	  * @return TRUE: se sim. <br/>
	  * 		FALSE: senão. */
	private boolean ePontuacao(char c) {

		if ( (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || (c == 'ç') || 
				 (c == 'á') || (c == 'à') || (c == 'ã') ||
				 (c == 'é') || (c == 'ê') ||
				 (c == 'ó') || (c == 'õ') || (c == 'ô') ||
				 (c == 'í') ||
				 (c == 'ú') || (c == 'ü') ) {
			
			return false;
			
		}
		else { //é pontuação
			
			return true; //tudo que não é letra, número, ou caracteres especiais
		}

	}


	/** Verifica se o token é composto por apenas sinais de pontuação.
	  * @param token : palavra.
	  * @return TRUE: se token contém apenas sinais de pontuação. <br/>
	  * 		FALSE: senão. */
	private boolean apenasPontuacao(String token) {
		
		for (int i = 0; i < token.length(); i++) {
			
			if ( !ePontuacao(token.charAt(i)) ) {
				
				return false; //não é apenas pontuação
			}
		}
		
		return true; //todos caracteres de token eram pontuação
	}


	/** Verifica se a palavra recebida contém pontuação.
	  * @param token : palavra.
	  * @return TRUE: se palavra contém algum sinal de pontuação. <br/>
	  * 		FALSE: senão.*/
	private boolean contemPontuacao(String token) {

		for (int i = 0; i < token.length(); i++) {
			
			if (ePontuacao(token.charAt(i))) {
				
				return true; //contém pontuação
			}
		}
		
		return false; //não contém pontuação
	}
	
	
	/** Exibe o vocabulário e lista invertida na tela. */
	public void mostrarTela() {
		
		//----------- exibir tela:
		System.out.println("----------------------");
		
		Iterator<String> iterator = map.keySet().iterator();
		
		while(iterator.hasNext()){
			
			String termo = iterator.next();
			
			System.out.println(termo + "\t\tDF: " + map.get(termo).documentos.size() + "\n");
			
			List<InformacoesTermo> iF = map.get(termo).documentos;
			
			for (InformacoesTermo iT : iF) {
				
				List<Integer> posicoes = iT.posicoes;
				System.out.print("\tTF: " + posicoes.size() + "\t" + iT.documento + "__posições:\t");
				
				for (Integer in : posicoes) {
					System.out.print(in + "\t");
				}
				
				System.out.println();
			}
			
			System.out.println("\n");
		}
		
	}


	
	
	/** Salva o índice em arquivo. 
	 * @throws Exception */
	public void salvarIndex() throws Exception {
		
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("index.data"));
		out.writeObject(map);
		out.flush();
		out.close();
		
	}

}
