package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.AbstractListModel;

public class BrowseContactsWindow extends JFrame {

	private JPanel contentPane;
	private ActionListener action_listener;
	private JList list = null;
	public Vector<String> contacts = new Vector<String>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BrowseContactsWindow frame = new BrowseContactsWindow(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public BrowseContactsWindow(ActionListener action_listener) {
		setTitle("Dodawanie kontaktu");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnDodajKontakt = new JButton("dodaj kontakt");
		btnDodajKontakt.addActionListener(action_listener);
		btnDodajKontakt.setActionCommand("contact_added");
		btnDodajKontakt.setBounds(304, 228, 130, 23);
		contentPane.add(btnDodajKontakt);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 424, 206);
		contentPane.add(scrollPane);
		
		list = new JList();
		scrollPane.setViewportView(list);
	}

	
	public String get_selected()
	{
		return (String)list.getSelectedValue();
	}

	public void add_contact(String user)
	{
		contacts.add(user);
		list.setListData(contacts);
	}
}
