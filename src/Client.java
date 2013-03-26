import gui.BrowseContactsWindow;
import gui.LoginWindow;
import gui.MainWindow;
import gui.TalkWindow;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JDialog;
 
public class Client implements ActionListener 
{
	private String login = null;
	
	// gui
	private LoginWindow login_window = null;
	private MainWindow main_window = null;
	private BrowseContactsWindow browse_contacts_window = null;
	private LinkedList<TalkWindow> talk_windows = new LinkedList<TalkWindow>();
	
	// sockety
    private Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private boolean logged_in = false;
    
    // kolejki wiadomoœci
    LinkedList<String> in_list = new LinkedList<String>();  // przysy³ane przez serwer 
    LinkedList<String> out_list = new LinkedList<String>(); // wysy³ane na serwer
    
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

    public static String now()
    {
      Calendar cal = Calendar.getInstance();
      SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
      return sdf.format(cal.getTime());

    }
	
    public void read_contacts(String user)
    {
    	try
    	{
    		FileInputStream fstream = new FileInputStream(user+"-contacts.txt");
    		DataInputStream in = new DataInputStream(fstream);
    		BufferedReader br = new BufferedReader(new InputStreamReader(in));
    		String strLine;
    		while ((strLine = br.readLine()) != null)
    		{
    			main_window.add_contact(strLine);
    		}
    		in.close();
    	}catch (Exception e)
    	{
    		///
    	}
    }
    
    public void write_contacts(String user)
    {
    	try
    	{
    		BufferedWriter out = new BufferedWriter(new FileWriter(user+"-contacts.txt"));
    		for(String contact: main_window.contacts)
    		{
    			out.write(contact);
    			out.newLine();
    		}
    		out.close();
    	}
    	catch (IOException e)
    	{
    		///
    	}
    }
    
    public synchronized void add_message(String msg, String where, String user)
    {
    	try
    	{
    		BufferedWriter writer = new BufferedWriter(new FileWriter(user+"-"+where+"-history.txt",true)) ;
    		writer.write(msg);
    		writer.close();
    	} catch(Exception e)
    	{
    		///
    	}
    }
    
    class ServerThread implements Runnable
    {
    	public void run()
    	{
    		try 
    		{
    			while(true)
    			{
    				// jeœli jest coœ do wys³ania, to wyœlij:
    				synchronized(out_list)
    				{
    					while(out_list.isEmpty() == false)
    					{
    						synchronized(out)
    						{
    							out.println(out_list.getFirst());
    						}
    						out_list.removeFirst();
    					}
    				}
    			
    				synchronized(in)
    				{
    					if(in.ready())
    					{
    						String input = in.readLine();
    						String delims = " ";
    						String[] tokens = input.split(delims);
    						System.out.println("dane z serwera: "+input);
    						System.out.println("od: "+tokens[1]);
    						
    						synchronized(talk_windows) 
    						{
    							boolean found = false;
    							for(TalkWindow window: talk_windows)
    							{
    								if(window.get_username().equals(tokens[1]))
    								{
    									String msg = tokens[1]+" ["+now()+"] :\n"+input.substring(4+1+tokens[1].length()+1)+"\n\n";
    									window.add_text(msg);
    									add_message(msg, tokens[1], login);
    									if(window.isVisible()==false)
    										window.setVisible(true);
    									found = true;
    									break;
    								}
    							}
    							if(!found)
    							{
    				    			TalkWindow frame = new_talk_window(tokens[1]);
    				    			frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    				    			frame.setVisible(true);
    				    			talk_windows.addLast(frame);
    				    			String msg = tokens[1]+" ["+now()+"] :\n"+input.substring(4+1+tokens[1].length()+1)+"\n\n";
    				    			frame.add_text(msg);
    				    			add_message(msg, tokens[1], login);
    							}
    						}
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
    
    public TalkWindow new_talk_window(String user)
    {
    	return new TalkWindow(user, this);
    }
    
    private boolean good_username(String username)
    {
    	if(username == null)
    		return false;
    	if(username.length()<2)
    		return false;
    	String validCharset = new String("qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM");
    	
    	for(int i=0; i<username.length(); i++)
    	{
    		boolean found = false;
    		
    		for(int j=0; j<validCharset.length(); j++)
    			if(validCharset.charAt(j) == username.charAt(i))
    			{
    				found = true;
    				break;
    			}
    		
    		if(!found)
    		{
    			return false;
    			
    		}
    	}
    	
    	return true;
    }
    
    private boolean not_logging = false;
    
	public void run() 
	{
		try 
		{
			// po³¹cz z serwerem
			socket = new Socket("localhost", 6666);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			// poka¿ okienko logowania i sprawdŸ czy  uda³o siê zalogowaæ
			while(!logged_in)
			{
				not_logging = true;
				login_window = new LoginWindow(this);
				login_window.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				login_window.setVisible(true);
				
				if(not_logging)
					return;
				
				login = new String(login_window.get_username());
				if(login!=null)
					login = login.toLowerCase();
				
				if(good_username(login))
				{
					out.println("login "+login);
		    	
					String serverResponse = in.readLine();
					System.out.println(serverResponse);
					logged_in =  serverResponse.equals("1");
				}
			}

			// pobieranie listy kontaktow:
			out.println("list_usernames");
			String serverResponse = in.readLine();
			String delims = ";";
			String[] tokens = serverResponse.split(delims);
			
			browse_contacts_window = new BrowseContactsWindow(this);
			
			main_window = new MainWindow(this,login);
			read_contacts(login);
			
			ServerThread serverThread = new ServerThread();
			new Thread(serverThread).start();
			
			main_window.setVisible(true);
			
			while(main_window.isVisible())
				Thread.sleep(1000);
 
            out.close();
            in.close();
            socket.close();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	
    public static void main(String[] args) throws IOException
    {
    	Client client = new Client();
    	client.run();
    }


	public void actionPerformed(ActionEvent e) 
	{
	    if("login".equals(e.getActionCommand())) 
	    {
	    	not_logging = false;
			if(login_window != null)
	    		login_window.dispose();
	    } else
	    if("talk".equals(e.getActionCommand()))
	    {
	    	synchronized(talk_windows)
	    	{
	    		boolean found = false;
	    		String contact = main_window.get_selected_contact();
	    		for(TalkWindow window: talk_windows)
	    		{
	    			if(window.get_username() == contact)
	    			{
	    				window.setVisible(true);
	    				found = true;
	    				break;
	    			}
	    		}
	    		if(found == false)
	    		{
	    			TalkWindow frame = new TalkWindow(contact, this);
	    			frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    			frame.setVisible(true);
	    			talk_windows.addLast(frame);
	    		}
	    	}
	    } else
	    if("send".equals(e.getActionCommand().substring(0, 4)))
	    {
	    	String window_id = e.getActionCommand().substring(4);
	    	System.out.println("window id: "+window_id);
	    	synchronized(talk_windows)
	    	{
	    		for(TalkWindow window: talk_windows)
	    		{
	    			if(window.get_username().equals(window_id))
	    			{
	    				synchronized(out)
	    				{
	    					out.println("send "+window.get_username()+" "+window.get_textpane().getText());
	    				}
	    				String msg = new String(login+" ["+now()+"] :\n"+window.get_textpane().getText()+"\n\n");
	    				window.add_text(msg);
	    				add_message(msg, window.get_username(), login);
	    				window.get_textpane().setText("");
	    			}
	    		}
	    	}
	    } else
	    if("add_contact".equals(e.getActionCommand()))
	    {
	    	synchronized(browse_contacts_window)
	    	{
				// pobieranie listy kontaktow:
	    		synchronized(out)
	    		{
	    			out.println("list_usernames");
	    		}
	    		String serverResponse = null;
	    		synchronized(in)
	    		{
	    			try {
						serverResponse = in.readLine();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
	    		}
				String delims = ";";
				String[] tokens = serverResponse.split(delims);
				
				browse_contacts_window.contacts = new Vector<String>();
				boolean add_any = false;
				for(String user: tokens)
				{
					if(!main_window.has_contact(user))
					{
						browse_contacts_window.add_contact(user);
						add_any = true;
					}
				}
				
	    		if(add_any)
	    		{
	    			browse_contacts_window.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    			browse_contacts_window.setVisible(true);
	    		}
	    	}
	    } else
	    if("contact_added".equals(e.getActionCommand()))
	    {
	    	synchronized(browse_contacts_window)
	    	{
	    		synchronized(main_window)
	    		{
	    			main_window.add_contact(browse_contacts_window.get_selected());
	    			System.out.println("dodaj: "+browse_contacts_window.get_selected());
	    			browse_contacts_window.setVisible(false);
	    			write_contacts(login);
	    		}
	    	}
	    }
	}
}

