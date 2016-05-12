/* * * * * * * * * * *\
 * LoginDialog.java
 * Description: Dialog for logging in as an RD, RA, or DM. If the login is successful, it will
 *				show the frame according to their account type. For RD it will show the AdminGUI and
 *				for RA and DM it will show the DefaultGUI. It will also give an error message
 *				if the login is unsuccessful.
 *
 * Date: 5/6/16
 * @author Brandon Ballard & Hanif Mirza
\* * * * * * * * * * */

import static javax.swing.GroupLayout.Alignment.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.sql.*;

class LoginDialog extends JDialog implements ActionListener,DocumentListener
{
	public static void main(String[] x)
	{
		new LoginDialog();
	}

	JPanel				buttonPanel, fieldPanel, titlePanel;
	JButton 			loginButton, exitButton;
	JTextField 			usernameTF;
	JPasswordField 		passwordTF;
	JLabel 				accountLabel, usernameLabel, passwordLabel, titleLabel;
	JComboBox<String> 	comboTypesList;
	String              username, password;
    Connection 			connection = null;
    Statement 			statement = null;

	//BY BRANDON BALLARD, set up and add components
	public LoginDialog()
	{
		titleLabel = new JLabel();
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setIcon(new ImageIcon("FSU_Logo.png"));

		titlePanel = new JPanel(new FlowLayout());
		titlePanel.setBackground(Color.GRAY);
		titlePanel.add(titleLabel);

		loginButton = new JButton("Login");
		loginButton.setBackground(Color.WHITE);
		loginButton.addActionListener(this);
		getRootPane().setDefaultButton(loginButton);
		loginButton.setEnabled(false);

		exitButton = new JButton("Exit");
		exitButton.setBackground(Color.WHITE);
		exitButton.addActionListener(this);

		buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.setBackground(Color.GRAY);
		buttonPanel.add(loginButton);
		buttonPanel.add(exitButton);

		fieldPanel = setFields();//creates text fields for user input

		getContentPane().add(titlePanel, BorderLayout.NORTH);
		getContentPane().add(fieldPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setupMainFrame();
	}

	//BY BRANDON BALLARD, handles the action events
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == loginButton)
		{
			doLogin();
		}
		else if(e.getSource() == exitButton)
		{
			System.exit(1);
		}
    }

	// Written by Hanif Mirza. This function will perform the login. It will give a warning message if the login is unsuccessful
    void doLogin()
    {
		username = usernameTF.getText().trim().replaceAll("'", "\\\\'") ;
		password = new String(passwordTF.getPassword()).replaceAll("'", "\\\\'");

		try
		{
			 Class.forName( "com.mysql.jdbc.Driver" );
			 connection = DriverManager.getConnection( "jdbc:mysql://localhost/falcon16_dorm", "root", "root");// Hanif's database server
			 //connection = DriverManager.getConnection( "jdbc:mysql://localhost/dorm", "root", "password"); // Brandon's database server
			 statement = connection.createStatement();

			 if(comboTypesList.getSelectedItem().toString().equals("Resident Director"))
			 {
				 if(checkIfValuesExist("RD","userID",username,"password",password))//validate Resident Director
				 {
					 this.dispose();
					 new AdminGUI(statement,username,password);//go to Resident Director main menu
				 }
				 else
				 {
					JOptionPane.showMessageDialog(this, "Login failure:" + "\n" + "Unknown username or bad password");
				 }
			 }
			 else if(comboTypesList.getSelectedItem().toString().equals("Resident Assistant"))
			 {
				 if(checkIfValuesExist("RA","userID",username,"password",password))//validate Resident Assistant
				 {
					String	sql = "INSERT INTO Log_Detail(log_date,login_time,empID) VALUES (CURDATE(),curtime(),"+ "'"+username+"'" +")";
					statement.executeUpdate(sql);	// Add RA's login time and date to the database

					this.dispose();
					new DefaultGUI(statement,username,password,"RA");//go to Resident Assistant's main menu
				 }
				 else
				 {
					JOptionPane.showMessageDialog(this, "Login failure:" + "\n" + "Unknown username or bad password");
				 }
			 }
			 else if(comboTypesList.getSelectedItem().toString().equals("Desk Monitor"))
			 {
				 if(checkIfValuesExist("DM","userID",username,"password",password))//validate Desk Monitor
				 {
					String	sql = "INSERT INTO Log_Detail(log_date,login_time,empID) VALUES (CURDATE(),curtime(),"+ "'"+username+"'" +")";
					statement.executeUpdate(sql); // Add DM's login time and date to the database

					this.dispose();
					new DefaultGUI(statement,username,password,"DM");//go to Desk Monitor main menu
				 }
				 else
				 {
					JOptionPane.showMessageDialog(this, "Login failure:" + "\n" + "Unknown username or bad password");
				 }
			 }
			 else
			 {
				 JOptionPane.showMessageDialog(this, "Login failure:" + "\n" + "Please select an account type");
			 	 connection.close();
			     usernameTF.setText("");
			     passwordTF.setText("");
			 }
		}
		catch (SQLException sqlException)
		{
			JOptionPane.showMessageDialog(this, sqlException.getMessage() );
		}
		catch(Exception exception)
		{
			JOptionPane.showMessageDialog(this, exception.getMessage() );
		}
	}

	// Written by Hanif Mirza. This function will return true if user's username and password match the database and return false otherwise.
    boolean checkIfValuesExist(String tableName,String columnName1,String columnValue1,String columnName2,String columnValue2) throws Exception
    {
		 String SQL_Query = "Select * "+
		 					"From "+tableName+
		 					" WHERE "+columnName1+" LIKE BINARY "+ "'"+columnValue1+"'" + " && " + columnName2 +" LIKE BINARY "+ "'"+columnValue2+"'";

		 ResultSet resultSet = statement.executeQuery(SQL_Query);
		 if(!resultSet.next())
		 {
			 resultSet.close();
			 connection.close();
			 statement.close();
			 usernameTF.setText("");
			 passwordTF.setText("");
			 return false;
		 }
		 else
		 {
			resultSet.close();
			return true;
		 }
    }

	//BY BRANDON BALLARD, adds fields using group layout
    JPanel setFields()
    {
		GroupLayout layout;
		JPanel p;

		String[] comboTypes = { "-Select-" ,"Desk Monitor", "Resident Assistant", "Resident Director" };

		comboTypesList = new JComboBox<>(comboTypes);
		comboTypesList.addActionListener(this);

		accountLabel = new JLabel("Account:");
		accountLabel.setForeground(Color.WHITE);
		usernameLabel = new JLabel("Username:");
		usernameLabel.setForeground(Color.WHITE);
		passwordLabel = new JLabel("Password:");
		passwordLabel.setForeground(Color.WHITE);

	 	usernameTF = new JTextField();
		passwordTF = new JPasswordField();

		usernameTF.getDocument().addDocumentListener(this);
        passwordTF.getDocument().addDocumentListener(this);

		p = new JPanel();
		p.setBackground(new Color(1, 0.1f, 0.1f).darker().darker());

		layout = new GroupLayout(p);
		p.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup().addComponent(accountLabel).addComponent(usernameLabel).addComponent(passwordLabel));
		hGroup.addGroup(layout.createParallelGroup().addComponent(comboTypesList).addComponent(usernameTF).addComponent(passwordTF));
		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(accountLabel).addComponent(comboTypesList));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(usernameLabel).addComponent(usernameTF));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(passwordLabel).addComponent(passwordTF));
		layout.setVerticalGroup(vGroup);

		return(p);
	}

	public void insertUpdate(DocumentEvent de)
	{
	    username = usernameTF.getText().trim();
	    password = new String(passwordTF.getPassword());

	    if(!username.equals("") &&  !password.equals(""))
	    {
	        loginButton.setEnabled(true);
	    }
	}

	public void removeUpdate(DocumentEvent de)
	{
	    username = usernameTF.getText().trim();
	    password = new String(passwordTF.getPassword());

	    if(username.equals("") ||  password.equals(""))
	    {
	        loginButton.setEnabled(false);
	    }
	}

    public void changedUpdate(DocumentEvent de){}

    void setupMainFrame()
	{
		setTitle("Guest Visitation Login");
		Toolkit    tk;
		Dimension   d;

		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();
		setSize(d.width/3, 270);
		setLocation(d.width/3, d.height/3);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setVisible(true);
    }
}
