package principal;

import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class BaseDados {
	private String database;
	private MongoClient mongoClient;
	private DB db;
	private DBCollection coll;
	
	public BaseDados(String database) throws ClassNotFoundException, UnknownHostException{
		this.database = database;
		mongoClient = new MongoClient( "localhost" );
	}
	
	public void conectar(String collection){
		db = mongoClient.getDB(database);
		coll = db.getCollection(collection);
	}
	
	public void desconectar ( ){
		this.mongoClient.close();
	}
	
	public String md5 (String entrada){
	    MessageDigest md;
	    try {
	    	md = MessageDigest.getInstance("MD5");	    
	       BigInteger hash = new BigInteger(1, md.digest(entrada.getBytes()));  
	       String sumario = hash.toString(16);  
	       return sumario;  
	    } catch (NoSuchAlgorithmException e) {
	     // TODO Auto-generated catch block
	     e.printStackTrace();
	    }
		return null;
	}
	
	public void inserir (String paginaUrl, String paginaConteudo){
		BasicDBObject doc = new BasicDBObject( );
		doc.put("_id", md5(paginaUrl));
		doc.put("url", paginaUrl);
		doc.put("conteudo", paginaConteudo);
		
		coll.insert(doc);
	}
	
	/*Função criada apenas para dar um exemplo de como fazer um select. criaremos os selects que forem necessários futuramente.*/
	public void pesquisar (String termo){
		BasicDBObject query = new BasicDBObject();
		query.put("name",  java.util.regex.Pattern.compile(" "+termo+" "));
		DBCursor cursosr = coll.find(query);
	}
}
