import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;


public class ServerSession extends Thread
{
	private boolean is_connected = true;
	private Socket client_socket = null;
	private Server server = null;
	private String username = null;
	private LinkedList<String> out_list = new LinkedList<String>(); 	// wiadomoœci wysy³ane
	
	ServerSession(Socket client_socket, Server server)
	{
		this.client_socket = client_socket;
		this.server = server;
	}
	
	public boolean has_connection()
	{
		return is_connected;
	}
	
	public void begin()
	{
		start();
	}
	
	public String get_username()
	{
		return username;
	}
	
	public void add_message(String str)
	{
		synchronized(out_list)
		{
			out_list.addFirst(str);
		}
	}
	
	public void run() 
	{
		try 
		{
			PrintWriter out = new PrintWriter(client_socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
			
			String inputLine;
			
			boolean done = false;
			
			while(!done)
			{
 
				if(in.ready())
				{
					if((inputLine = in.readLine()) != null) 
					{
						String delims = " ";
						String[] tokens = inputLine.split(delims);
				
						if(tokens[0].equals(new String("list_usernames")))
						{
							LinkedList<String> usernames_list = server.get_usernames();
							String output = new String();
							for(String username : usernames_list)
								output += username + ";";
							out.println(output);
						} else if(tokens[0].equals(new String("create_account"))) 
						{
							server.add_username(tokens[1]);
							out.println(inputLine);
						} else if(tokens[0].equals(new String("login"))) 
						{
							String receivedUsername = tokens[1];
							LinkedList<String> usernames = server.get_usernames();
							boolean found_username = false;
							for(String s: usernames)
							{
								if(s.equals(receivedUsername))
								{
									found_username = true;
									break;
								}
							}
							if(found_username)
							{
								username = receivedUsername;
								out.println("1");
							}
							else 
							{
								server.add_username(receivedUsername);
								out.println("1");
							}
						} else if(tokens[0].equals(new String("send")))
						{
							server.send(inputLine, tokens[1], username);
						}
				
						System.out.println(">SERWER odebra³: "+inputLine);
					} else
					{
						done = true;
					}
				}
				
				synchronized(out_list)
				{
					if(out_list.isEmpty() == false)
					{
						synchronized(out)
						{
							out.println(out_list.getFirst());
						}
						out_list.removeFirst();
					}
				}
				
				Thread.sleep(100);
			}
			
			out.close();
			in.close();
			client_socket.close();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		is_connected = false;
	}
}
