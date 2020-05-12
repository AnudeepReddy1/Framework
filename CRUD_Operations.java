
//Contains logic for create, display, update and delete record from table.
import java.util.Scanner;
import java.lang.System;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.DriverManager;

class CRUD_Operations
{
	Connection connection = null;
	String tableName;
	String className;
	String insertQuery;
	int countOfColumns; 
	String id;
	ArrayList<String> columnNames = new ArrayList<String>();
	ArrayList<String> primaryKeys = new ArrayList<String>();
	String statusColumn = null;
	Scanner scanner = new Scanner(System.in);

	public CRUD_Operations(String classNam)
	{
		className = classNam;
		try
		{
			MyDBConnection myDbConnection = (MyDBConnection)Class.forName(classNam).newInstance();
			connection = myDbConnection.getMyConnection();
			myDbConnection = null;
			System.gc();
			getColumnNames();
		}
		catch(ClassNotFoundException e)
		{
			System.out.println("Please check if proper database name is passed as argument!");
			System.exit(1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	void getColumnNames()
	{
		System.out.print("Enter table name: ");
		tableName = scanner.next();
		getPrimaryKeys();
		String query = "select * from " + tableName;
		try
		{
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
			countOfColumns = resultSetMetaData.getColumnCount();
			for(int counter = 1; counter <= countOfColumns; counter++)
			{
				String columnName = resultSetMetaData.getColumnName(counter);
				if(!columnName.equals(primaryKeys.get(0)))
				{
					if(columnName.toLowerCase().equals("status"))
					{
						statusColumn = columnName;
					}
					else
					{
						columnNames.add(columnName);	
					}
				}
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}

	void getPrimaryKeys()
	{
		String query;
		if(className.equals("SQLiteDB"))
		{	
			query = "SELECT p.name AS col_name, p.pk AS col_is_pk FROM sqlite_master m LEFT OUTER JOIN pragma_table_info((m.name)) p ON m.name <> p.name WHERE m.type = 'table' and m.name = '" + tableName + "' and p.pk = 1 ";		
		}
		else
		{
			String dataBase = "dbAnudeep";
			query = "select stats.column_name from information_schema.tables as tabs inner join information_schema.statistics as stats on stats.table_schema = tabs.table_schema and stats.table_name = tabs.table_name and stats.index_name = 'primary' where tabs.table_schema = '" + dataBase + "' and tabs.table_type = 'BASE TABLE' and tabs.table_name = '" + tableName +"'";
		}

		try
		{
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			while(resultSet.next())
			{
				primaryKeys.add(resultSet.getString(1));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	void generateInsertQuery()
	{
		String placeHolders = "";
		String parameters = "";
		int countOfColumns = columnNames.size();
		for(int counter = 0; counter < countOfColumns - 1; counter++)
		{
			parameters = parameters + columnNames.get(counter) + ", ";
			placeHolders = placeHolders + "?, ";
		}	
	
		insertQuery = "insert into " + tableName + "(" + parameters + columnNames.get(countOfColumns-1) + ") values (" + placeHolders +"?);";
	}

	void insert()
	{
		String[] returnId = { columnNames.get(1) };
		try
		{
			PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, returnId);
			for(int counter = 0; counter < countOfColumns; counter++)
			{
				String parameter = columnNames.get(counter);
				System.out.print("Enter " + parameter + ": ");
				String value = scanner.next();
				preparedStatement.setString(counter-1, value);
			}
			int rowsEffected = preparedStatement.executeUpdate();
			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			if(resultSet.next() && rowsEffected > 0)
			{
				System.out.println("Record inserted succesfully.");
				System.out.println("Your " + primaryKeys.get(0) + " is " + resultSet.getInt(1) + ".");
			}
			else
			{
				System.out.println("Failed to insert try again.");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	void display()
	{
		String query;
		if(statusColumn != null)
		{
			query = "select * from " + tableName + " where " + statusColumn + " = 1";
		}
		else
		{
			query = "select * from " + tableName;
		}
		try
		{
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			if(resultSet.next())
			{
				System.out.print(String.format("%15s", primaryKeys.get(0)));
				for(int counter = 0; counter < columnNames.size(); counter++)
				{
					System.out.print(String.format("%15s", columnNames.get(counter)));
				}
				System.out.println("\n");
				do
				{
					System.out.print(String.format("%15s", resultSet.getString(primaryKeys.get(0))));
					for(int counter = 0; counter < columnNames.size(); counter++)
					{
						System.out.print(String.format("%15s", resultSet.getString(columnNames.get(counter))));
					}
					System.out.println();
				}while(resultSet.next());
			}
			else
			{
				System.out.println("No records available.");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	void update()
	{
		readId();
		for(int counter = 2; counter < countOfColumns; counter++)
		{
			System.out.println(counter-1 + ". " + columnNames.get(counter));
		}
		System.out.print("Choose any field to update: ");
		try
		{
			int choice = scanner.nextInt();
			if(choice >=1 && choice <= countOfColumns)
			{
				System.out.print("Enter " + columnNames.get(choice+1) + ": ");
				String newValue = scanner.next();
				String query = "update " + tableName + " set " + columnNames.get(choice+1) + " = ? " + "where " + columnNames.get(0) + " = 1 and " + columnNames.get(1) + " = ?";
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, newValue);
				preparedStatement.setString(2, id);
				int rowsEffected = preparedStatement.executeUpdate();
				if(rowsEffected > 0)
				{
					System.out.println("Record updated sucessfully.");
				}
				else
				{
					System.out.println("Record with " + columnNames.get(1) + " = " + id + " does not exists.");
				}
			}
			else
			{
				printInvalidChoice();
			}
		}
		catch(InputMismatchException i)
		{
			scanner.next();
			printInvalidChoice();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	void delete()
	{
		String query;
		if(statusColumn != null)
		{
			query = "update " + tableName + " set " + columnNames.get(0) + " = 0 " + "where " + columnNames.get(0) + " = 1 and " + columnNames.get(1) + " = ?";
		}
		else
		{
			query = "delete from " + tableName + " where " + primaryKeys.get(0) + " = ?";
		}
		try
		{
			readId();
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, id);
			int rowsEffected = preparedStatement.executeUpdate();
			if(rowsEffected > 0)
			{
				System.out.println("Record deleted successfully.");
			}
			else
			{
				System.out.println("Record with " + columnNames.get(1) + " = " + id + " does not exists.");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();	
		}
	}

	void readId()
	{
		System.out.print("Enter " + columnNames.get(1) + ": ");
	 	id = scanner.next();
	}

	void printInvalidChoice()
	{
		System.out.println("Invalid choice.");
	}
}