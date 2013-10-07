package principal;

import java.sql.*;

public class BaseDados {
	private String userName;
	private String userPassword;
	private String database;
	private Connection connection;
	private PreparedStatement query;
	private ResultSet result;
	
	public BaseDados(String userName, String userPassword, String database) throws ClassNotFoundException{
		Class.forName("com.mysql.jdbc.Driver");		
		this.userName = userName;
		this.userPassword = userPassword;
		this.database = database;
	}
	
	public void conectar( ) throws SQLException{
		connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/"+database+"?user="+userName+"&password="+userPassword);
		System.out.println("Conectou!");
		prepararQueryInsercao( );
	}
	
	public void desconectar ( ) throws SQLException{
		connection.close ( );
		System.out.println("Desconectou!");
	}
	
	public void prepararQueryInsercao( ) throws SQLException{
		query = connection.prepareStatement("INSERT INTO Pagina (PaginaUrlHash, PaginaConteudo, PaginaUrl)"
		+"values (md5(?), ?, ?");
	}
	
	public void inserir (String paginaUrl, String paginaConteudo) throws SQLException{
		query.setString(1, paginaUrl);
		query.setString(2, paginaConteudo);
		query.setString(3, paginaUrl);
		query.executeQuery();
	}
	
	/*Função criada apenas para dar um exemplo de como fazer um select. criaremos os selects que forem necessários futuramente.*/
	public void pesquisar ( ) throws SQLException{
		Statement statement = connection.createStatement();
		String query = "SELECT * from Pagina";
		this.result = statement.executeQuery(query);
		
		while (this.result.next()){
			String string = ""+this.result.getInt("PaginaId")+" "+this.result.getString("PaginaUrlHash")+" "+this.result.getString("PaginaConteudo")+" "+this.result.getString("PaginaUrl");				
			System.out.println (string);
		}
	}
}
