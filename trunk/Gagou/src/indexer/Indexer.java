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
// remove pontua��o - ok
// tudo min�sculo - ok

public class Indexer {
	
	/** Vocabul�rio e lista invertida. */
	private Map<String, Termo> map;
	
	
	
	
	/** Construtor. */
	public Indexer () throws Exception {
		
		map = new HashMap<String, Termo>();
	}

	
	/** Monta o vocabul�rio e a lista invertida.
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
				
				String token = tokenizer.nextToken().toLowerCase(); //coloca tudo em min�scula
				
				boolean pontuacao = apenasPontuacao(token); //retorna true se o termo for apenas pontua��o (ou seja, pode pular parte de inserir no hash)
				
				
				if (!pontuacao) { //n�o � apenas pontua��o
					
					token = removePontuacao(token); //remove pontua��o
					
					token = stemming(token); //stemming
					
					Termo termo = map.get(token); //procurar token no hash
					
					if (termo == null) { //token n�o existe no hash ainda
						
						termo = new Termo(token, id++);
						
						InformacoesTermo iF = new InformacoesTermo(arquivo.getName(), 1); //nome do arquivo e TF (1, pois � a primeira vez que foi encontrado)
						iF.posicoes.add(posicao); //posi��o do termo no arquivo
						
						posicao++; //pr�xima posi��o
						
						termo.documentos.add(iF); //adicionar na lista de documentos que esse termo aparece
						
						map.put(token, termo); //adicionar no hash
						
					}
					else { //token j� existe no hash
						
						if (termo.contains(arquivo.getName())) { //token j� tem esse arquivo na sua lista
							
							InformacoesTermo iF = termo.getInformacoesTermo(arquivo.getName());
							
							iF.posicoes.add(posicao); //nova posi��o
							
							posicao++; //posi��o do pr�ximo termo
							iF.TF++; //TF aumenta em 1
							
						}
						else { //token n�o tem esse arquivo na sua lista
							
							InformacoesTermo iF = new InformacoesTermo(arquivo.getName(), 1);
							
							iF.posicoes.add(posicao); //posi��o do termo
	
							posicao++; //posi��o do pr�ximo termo
							termo.documentos.add(iF); //adiciona na lista do termo
						}
					}
					
				} //fim if !pontuacao (ignorar tokens que s�o apenas pontua��o)
				
			} //fim while hasMoreT
			
		} // fim para cada arquivo na pasta de arquivos

		
	}
	
	
	/** L� arquivos html (na pasta arquivos/fetchedPages) e transforma em arquivos de texto simples (na pasta arquivos/arquivosSimples). 
	 * @throws Exception */
	private void trasformaTextoSimples() throws Exception {		
		
		File dir = new File(".//arquivos//fetchedPages");
		File[] arquivos = dir.listFiles();
		
		for (File f : arquivos) { //pra cada arquivo de p�gina na pasta
			
			if (f.isFile()) { //apenas se for arquivo, abrir para leitura				
				
				Document doc = Jsoup.parse(f, "UTF-8");
				
				
				String simples = doc.body().text(); //texto simples da p�gina html
				
				
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
	

	/** Remove pontua��o do token.
	  * @param token : palavra. 
	  * @return String com pontua��o removida. */
	private String removePontuacao(String token) {
		
		if (contemPontuacao(token)) {
			
			//n�o � apenas pontua��o, remover pontua��o:
			
			StringBuffer sb = new StringBuffer();
			
			for (int i = 0; i < token.length(); i++) {
				
				if (!ePontuacao(token.charAt(i))) { //n�o � pontua��o, pode append no stringbuffer
					sb.append(token.charAt(i));
				}
			}
			
			return sb.toString(); //termo sem pontua��o
		}
		
		return token; //n�o tem pontua��o, retorna original
	}


	/** Verifica se o char recebido � sinal de pontua��o.
	  * @param c : char
	  * @return TRUE: se sim. <br/>
	  * 		FALSE: sen�o. */
	private boolean ePontuacao(char c) {

		if ( (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || (c == '�') || 
				 (c == '�') || (c == '�') || (c == '�') ||
				 (c == '�') || (c == '�') ||
				 (c == '�') || (c == '�') || (c == '�') ||
				 (c == '�') ||
				 (c == '�') || (c == '�') ) {
			
			return false;
			
		}
		else { //� pontua��o
			
			return true; //tudo que n�o � letra, n�mero, ou caracteres especiais
		}

	}


	/** Verifica se o token � composto por apenas sinais de pontua��o.
	  * @param token : palavra.
	  * @return TRUE: se token cont�m apenas sinais de pontua��o. <br/>
	  * 		FALSE: sen�o. */
	private boolean apenasPontuacao(String token) {
		
		for (int i = 0; i < token.length(); i++) {
			
			if ( !ePontuacao(token.charAt(i)) ) {
				
				return false; //n�o � apenas pontua��o
			}
		}
		
		return true; //todos caracteres de token eram pontua��o
	}


	/** Verifica se a palavra recebida cont�m pontua��o.
	  * @param token : palavra.
	  * @return TRUE: se palavra cont�m algum sinal de pontua��o. <br/>
	  * 		FALSE: sen�o.*/
	private boolean contemPontuacao(String token) {

		for (int i = 0; i < token.length(); i++) {
			
			if (ePontuacao(token.charAt(i))) {
				
				return true; //cont�m pontua��o
			}
		}
		
		return false; //n�o cont�m pontua��o
	}
	
	
	/** Exibe o vocabul�rio e lista invertida na tela. */
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
				System.out.print("\tTF: " + posicoes.size() + "\t" + iT.documento + "__posi��es:\t");
				
				for (Integer in : posicoes) {
					System.out.print(in + "\t");
				}
				
				System.out.println();
			}
			
			System.out.println("\n");
		}
		
	}


	
	
	/** Salva o �ndice em arquivo. 
	 * @throws Exception */
	public void salvarIndex() throws Exception {
		
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("index.data"));
		out.writeObject(map);
		out.flush();
		out.close();
		
	}

}
