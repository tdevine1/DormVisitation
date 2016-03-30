/**
 * Dorm visitation login dialog
 * Date: 03/11/16
 * @author Brandon Ballard & Hanif Mirza
 *
 */

import static javax.swing.GroupLayout.Alignment.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.UIManager;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.sql.*;
import java.sql.SQLException;

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
	String              username;
    String              password;
    Connection 			connection = null;  // manages connection
    Statement 			statement = null;   // query statement


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

		fieldPanel = setFields();

		getContentPane().add(titlePanel, BorderLayout.NORTH);
		getContentPane().add(fieldPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setupMainFrame();
	}

    public void insertUpdate(DocumentEvent de)
    {
        username = usernameTF.getText().trim();
        password = new String(passwordTF.getPassword());

        if( !username.equals("") &&  !password.equals(""))
        {
			//if both username and password aren't empty then enable login button
            loginButton.setEnabled(true);
        }
    }

    public void changedUpdate(DocumentEvent de){}

    public void removeUpdate(DocumentEvent de)
    {
        username = usernameTF.getText().trim();
        password = new String(passwordTF.getPassword());

        if( username.equals("") ||  password.equals(""))
        {
			//disable login button if username or password is empty
            loginButton.setEnabled(false);
        }
    }

	public void actionPerformed(ActionEvent e)
	{
		if( e.getSource() == loginButton )
		{
			doLogin();
		}

		else if(e.getSource() == exitButton )
		{
			System.exit(1);
		}
    }

    void doLogin()
    {
		username = usernameTF.getText().trim().replaceAll("'", "\\\\'") ;
		password = new String(passwordTF.getPassword()).replaceAll("'", "\\\\'");

		try
		{
			 Class.forName( "com.mysql.jdbc.Driver" ); // load database driver class

			 // Remote database server connection,...getConnection( DATABASE_URL, USERNAME, PASSWORD );
			 connection = DriverManager.getConnection( "jdbc:mysql://johnny.heliohost.org/falcon16_dorm", "falcon16", "fsu2016" );
			 // Hanif's local database server
			 //connection = DriverManager.getConnection( "jdbc:mysql://localhost/falcon16_dorm", "root", "root");

			 statement = connection.createStatement();// create Statement for querying database

			 if ( comboTypesList.getSelectedItem().toString().equals("Resident Director") )
			 {
				 if( checkIfValuesExist("RD","userID",username,"password",password))
				 {
					 new AdminGUI(); //show RD's GUI
					 this.dispose();
				 }
				 else
				 {
					JOptionPane.showMessageDialog(this, "Login failure:" + "\n" + "Unknown username or bad password");
				 }
			 }

			 else if ( comboTypesList.getSelectedItem().toString().equals("Resident Assistant") )
			 {
				 if( checkIfValuesExist("RA","userID",username,"password",password))
				 {
					 String	sql = "INSERT INTO Log_Detail(log_date,login_time,empID) VALUES (CURDATE(),curtime(),"+ "'"+username+"'" +")";
					 statement.executeUpdate(sql); // adding RA's log details to database table

					 new DefaultGUI(statement,username,password,"RA");
					 this.dispose();
				 }
				 else
				 {
					JOptionPane.showMessageDialog(this, "Login failure:" + "\n" + "Unknown username or bad password");
				 }
			 }

			 else if ( comboTypesList.getSelectedItem().toString().equals("Desk Monitor") )
			 {
				 if( checkIfValuesExist("DM","userID",username,"password",password))
				 {
					 String	sql = "INSERT INTO Log_Detail(log_date,login_time,empID) VALUES (CURDATE(),curtime(),"+ "'"+username+"'" +")";
					 statement.executeUpdate(sql); // adding DM's log details to database table

					 new DefaultGUI(statement,username,password,"DM");
					 this.dispose();
				 }
				 else
				 {
					JOptionPane.showMessageDialog(this, "Login failure:" + "\n" + "Unknown username or bad password");
				 }
			 }
			 else
			 {
				 JOptionPane.showMessageDialog(this, "Login failure:" + "\n" + "Please select an account type");
			 	 connection.close(); // unsuccessful login, so close the connection
			     usernameTF.setText("");
			     passwordTF.setText("");
			 }

		}// end try

		catch ( SQLException sqlException )
		{
			if (sqlException.getMessage().startsWith("Communications") )
			{
				JOptionPane.showMessageDialog(this, "No internet connection! Please try again later!");
			}
			else
			{
				JOptionPane.showMessageDialog(this, sqlException.getMessage() );
			}
		}
		catch ( Exception exception )
		{
			JOptionPane.showMessageDialog(this, exception.getMessage() );
		}
	}

    //this function will return true if values exist in specific columns of a database table
    boolean checkIfValuesExist(String tableName,String columnName1,String columnValue1,String columnName2,String columnValue2) throws Exception
    {
		 String SQL_Query = "Select * "+
		 					"From "+tableName+
		 					" WHERE "+columnName1+" LIKE BINARY "+ "'"+columnValue1+"'" + " && " + columnName2 +" LIKE BINARY "+ "'"+columnValue2+"'";

		 ResultSet resultSet = statement.executeQuery(SQL_Query);// query database
		 if(!resultSet.next())
		 {
			 resultSet.close();
			 connection.close(); // unseccessful login, so close the connection
			 statement.close();
			 usernameTF.setText("");
			 passwordTF.setText("");
			 return false; // values don't exist, so return false
		 }
		 else
		 {
			resultSet.close();
			return true; // values exist, so return true
		 }
    }

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

    void setupMainFrame()
	{
		setTitle("Guest Visitation Login");
		Toolkit    tk;
		Dimension   d;

		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();
		setSize(d.width/3, d.height/3);
		setLocation(d.width/3, d.height/3);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setVisible(true);
    }

}// end of LoginDialog class
