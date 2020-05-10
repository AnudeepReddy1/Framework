//Framework
class Framework
{
	public static void main(String[] args)
	{
		CRUD_Operations crud_operations = new CRUD_Operations(args[0]);
		// crud_operations.update();
		// crud_operations.insert();
		// crud_operations.display();
		int choice;
		while(true)
		{
			try
			{
				System.out.print("1. Insert a record\n2. Display\n3. Update\n4. Delete\n5. Exit\nEnter your choice: ");
				choice = crud_operations.scanner.nextInt();
				switch(choice)
				{
					case 1 : crud_operations.insert();
							 break;
					case 2 : crud_operations.display();
							 break;
					case 3 : crud_operations.update();
							 break;
					case 4 : crud_operations.delete();
							 break;
					case 5 : crud_operations.connection.close();
							 System.exit(1);
					default : crud_operations.printInvalidChoice();
				}
			}
			catch(Exception e)
			{
				crud_operations.scanner.nextLine();
				crud_operations.printInvalidChoice();
			}
		}
	}
}