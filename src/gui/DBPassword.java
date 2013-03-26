package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JPasswordField;

public class DBPassword extends JDialog implements ActionListener {
	private JPasswordField passwordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DBPassword dialog = new DBPassword();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DBPassword() {
		setTitle("Has\u0142o do bazy");
		setBounds(100, 100, 237, 120);
		getContentPane().setLayout(null);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(10, 11, 201, 20);
		getContentPane().add(passwordField);
		
		JButton btnNewButton = new JButton("OK");
		btnNewButton.setBounds(10, 42, 200, 23);
		btnNewButton.addActionListener(this);
		getContentPane().add(btnNewButton);
	}

	public void actionPerformed(ActionEvent arg0) 
	{
		this.dispose();
	}
	
	public String get_password()
	{
		return new String(passwordField.getPassword());
	}
}
