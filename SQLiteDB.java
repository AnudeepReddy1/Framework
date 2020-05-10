import java.sql.*;
class SQLiteDB implements MyDBConnection
{
	public Connection getMyConnection()
	{
		Connection connection = null;
		String dataBase = "database";
		try
		{
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + dataBase + ".db");
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