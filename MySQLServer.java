//Creates coonection object for MySQL serever.
import java.sql.*;
class MySQLServer implements MyDBConnection
{
	public Connection getMyConnection()
	{
		Connection connection = null;
		String dataBase = "dbAnudeep";
		String userName = "rooty";
		String password = "pwd";
		String url = "jdbc:mysql://165.22.14.77/" + dataBase + "?AautoReconnect=true&useSSL=false";
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(url, userName, password);
			System.out.println("Connection established.");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		finally
		{
			return connection;
		}
	}
}