package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.event.KeyEvent;

public class TalkWindow extends JFrame {

	private String username = null;
	private JTextPane talkPane = null;
	private JTextField textField;
	
	public JTextField get_textpane()
	{
		return textField;
	}
	
	public JTextPane get_talkpane()
	{
		return talkPane;
	}
	
	public String get_username()
	{
		return username;
	}
	
	public void add_text(String text)
	{
		talkPane.setText(talkPane.getText()+text);
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TalkWindow frame = new TalkWindow(null, null);
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
	public TalkWindow(String user, ActionListener action_listener) {
		username = user;
		setTitle(user==null?"Nikt":user);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 500);
		getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 424, 385);
		getContentPane().add(scrollPane);
		
		talkPane = new JTextPane();
		talkPane.setEditable(false);
		scrollPane.setViewportView(talkPane);
		
		JButton btnWylij = new JButton("wy\u015Blij");
		btnWylij.setMnemonic(KeyEvent.VK_ENTER);
		btnWylij.setBounds(345, 438, 89, 23);
		btnWylij.setActionCommand("send"+user);
		btnWylij.addActionListener(action_listener);
		getContentPane().add(btnWylij);
		
		textField = new JTextField();
		textField.setFocusTraversalPolicyProvider(true);
		textField.setFocusCycleRoot(true);
		textField.setBounds(10, 407, 424, 20);
		textField.setActionCommand("send"+user);
		textField.addActionListener(action_listener);
		getContentPane().add(textField);
		textField.setColumns(10);
	}
}
