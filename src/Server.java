import gui.DBPassword;
import gui.TalkWindow;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import javax.swing.JDialog;

import com.mysql.jdbc.Driver;
 
public class Server {
	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	private LinkedList<ServerSession> server_sessions = null;
	private LinkedList<Message> server_messages = new LinkedList<Message>();
	
	class Message
	{
		String where = new String();
		String msg = new String();
	}
	
	public synchronized LinkedList<String> get_usernames() 
	{
		LinkedList<String> usernames = new LinkedList<String>();
		try 
		{
			rs = stmt.executeQuery("SELECT login FROM raftalk");
		 
			while(rs.next()) 
			{
				String name = rs.getString(1);
				usernames.addFirst(name);
				System.out.println("Uzytkownik: "+name);
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return usernames;
	}
	
	public synchronized boolean add_username(String username)
	{
		try
		{
			stmt.executeUpdate("INSERT INTO raftalk VALUES "+"(\'"+username+"\')");	// !!! 
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return true;
	}
	
	public void connect_to_db() 
	{
		try 
		{
			DBPassword dialog = new DBPassword();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			
			while(dialog.isVisible())
				Thread.sleep(1000);
			
			String password = dialog.get_password();
			new Driver();
			conn = DriverManager.getConnection("jdbc:mysql://mysql.agh.edu.pl/raver", "raver", password);
			stmt = conn.createStatement();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	 class DelayedMessageThread implements Runnable
	    {
	    	public void run()
	    	{
	    		try 
	    		{
	    			while(true)
	    			{
	    				synchronized(server_messages)
	    				{
	    					for(Message msg: server_messages)
	    					{
	    						boolean found = false;
	    						synchronized(server_sessions)
	    						{
	    							for(ServerSession session: server_sessions)
	    							{
	    								if(session.get_username()!=null && session.get_username().equals(msg.where))
	    								{
	    									session.add_message(msg.msg);
	    									found = true;
	    								}
	    							}
	    						}
	    						if(found)
	    							server_messages.remove(msg);
	    					}
	    				}
	    				
	    				Thread.sleep(100);
	    			}
				} catch (Exception e) 
				{
					e.printStackTrace();
				}
	    	}
		}
	
	public void run() 
	{
		connect_to_db();
		//get_usernames();
		
        ServerSocket serverSocket = null;
        try
        {
            serverSocket = new ServerSocket(6666);
        } catch (IOException e) {
            System.out.println("Could not listen on port: 6666");
            System.exit(-1);
        }
 
        server_sessions = new LinkedList<ServerSession>();
        DelayedMessageThread delayed_message = new DelayedMessageThread();
        new Thread(delayed_message).start();
        while(true) 
        {
        	Socket client_socket = null;
        	try 
        	{
        		client_socket = serverSocket.accept();
        	} catch (IOException e) {
        		System.out.println("Accept failed: 6666");
        		System.exit(-1);
        	}
        	ServerSession session = new ServerSession(client_socket, this);
        	session.begin();
        	server_sessions.addFirst(session);
        	if(server_sessions.size()>10)
        	{
        		server_sessions.getLast().stop();
        		server_sessions.removeLast();
        	}
        }
	}
	
	public void send(String msg, String where, String sender)
	{
		System.out.println("probuje cos wyslac: "+where);
		boolean send = false;
		for(ServerSession session: server_sessions)
		{
			synchronized(session)
			{
				if(session.get_username()!=null && session.get_username().equals(where))
				{
					msg = "send "+ sender + " " + msg.substring(4+1+where.length()+1);
					session.add_message(msg);
					System.out.println("Dodaje wiadomosc do sesji "+msg);
					send = true;
					break;
				}
			}	
		}
		if(!send)
		{
			msg = "send "+ sender + " " + msg.substring(4+1+where.length()+1);
			Message m = new Message();
			m.msg = msg;
			m.where = where;
			server_messages.add(m);
		}
	}
	
    public static void main(String[] args) throws IOException 
    {
    	Server server = new Server();
    	server.run();
    }
}

