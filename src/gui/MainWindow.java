package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JButton;

public class MainWindow extends JFrame {

	private JList list = null;
	private DefaultListModel model = null;
	public Vector<String> contacts = new Vector<String>();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow(null, "Nikt");
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
	public MainWindow(ActionListener action_listener, String window_caption) {
		setTitle(window_caption);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 370, 500);
		getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 344, 416);
		getContentPane().add(scrollPane);
		
		model = new DefaultListModel();
		list = new JList(model);
		scrollPane.setViewportView(list);
		
		JButton btnDodaj = new JButton("dodaj nowy");
		btnDodaj.addActionListener(action_listener);
		btnDodaj.setActionCommand("add_contact");
		btnDodaj.setBounds(10, 438, 150, 23);
		btnDodaj.addActionListener(action_listener);
		getContentPane().add(btnDodaj);
		
		JButton btnRozmowa = new JButton("rozmowa");
		btnRozmowa.setBounds(170, 438, 184, 23);
		btnRozmowa.addActionListener(action_listener);
		btnRozmowa.setActionCommand("talk");
		getContentPane().add(btnRozmowa);
	}
	
	public void set_contacts(String []contacts)
	{
		list.setListData(contacts);
	}
	
	public void add_contact(String contact)
	{
		contacts.add(contact);
		list.setListData(contacts);
	}
	
	public String get_selected_contact()
	{
		return (String)list.getSelectedValue();
	}
	
	public boolean has_contact(String user)
	{
		for(String c: contacts)
		{
			if(c.equals(user))
				return true;
		}
		
		return false;
	}
}
