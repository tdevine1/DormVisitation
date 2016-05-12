/* * * * * * * * * * *\
 * AddEmployeeDialog.java
 * Description: Allows user to enter new employee information and save it, used for adding and editing employees.
 * 				Validates each field when user submits. Only the resident director will be able to add, edit or
 *				delete an employee.
 *
 * Date: 5/7/16
 * @author Brandon Ballard & Hanif Mirza
\* * * * * * * * * * */

import java.awt.event.*;
import static javax.swing.GroupLayout.Alignment.*;
import java.sql.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

class AddEmployeeDialog extends JDialog implements ActionListener,DocumentListener
{
	JPanel 						buttonPanel, fieldPanel;
	JButton 					cancelButton, addButton;
	AdminGUI					adminGUI;
	Statement 					myStatement;
	JTextField 					usernameTF, firstNameTF, lastNameTF, emailTF, phoneTF;
	JPasswordField				password, rePassword;
	JLabel 						accountLabel, usernameLabel, passwordLabel, rePasswordLabel, firstNameLabel, lastNameLabel, emailLabel, phoneLabel;
	String						username,password1,password2,firstName,lastName,email,phone,myVisitationID,employeeType;
	JComboBox 					accountCBox;
	int 						row;

	//CONSTRUSTOR FOR ADDING AN EMPLOYEE BY BRANDON BALLARD
	public AddEmployeeDialog(AdminGUI adminGUI,Statement myStatement,String employeeType)
	{
		setTitle("Add Employee");

		this.myStatement = myStatement;
		this.adminGUI = adminGUI;
		this.employeeType = employeeType;

		//________________________________________________________________Create buttons and button panel

		addButton = new JButton("Add Employee");
		addButton.setBackground(Color.WHITE);
		addButton.addActionListener(this);
		addButton.setActionCommand("ADD");
		addButton.setEnabled(false);
		getRootPane().setDefaultButton(addButton);

		cancelButton = new JButton("Cancel");
		cancelButton.setBackground(Color.WHITE);
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("CANCEL");

		buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.setBackground(Color.GRAY);
		buttonPanel.add(cancelButton);
		buttonPanel.add(addButton);

		//________________________________________________________________Add text fields for user input

		fieldPanel = setFields();

		//________________________________________________________________Add components to container

		getContentPane().add(fieldPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setupMainFrame();
	}

	//CONSTRUSTOR FOR EDITING AN EMPLOYEE BY BRANDON BALLARD
	public AddEmployeeDialog(AdminGUI adminGUI,Statement myStatement,String employeeType,String username , int row)
	{
		setTitle("Edit Employee");

		this.myStatement = myStatement;
		this.adminGUI = adminGUI;
		this.employeeType = employeeType;
		this.username = username;
		this.row = row;

		//________________________________________________________________Create buttons and button panel

		addButton = new JButton("Update Employee");
		addButton.setBackground(Color.WHITE);
		addButton.addActionListener(this);
		addButton.setActionCommand("UPDATE");
		getRootPane().setDefaultButton(addButton);

		cancelButton = new JButton("Cancel");
		cancelButton.setBackground(Color.WHITE);
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("CANCEL");

		buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.setBackground(Color.GRAY);
		buttonPanel.add(cancelButton);
		buttonPanel.add(addButton);

		//________________________________________________________________Add text fields for user input

		fieldPanel = setFields();

		//________________________________________________________________Populate text fields with selected accounts info

		populateAllFields();

		//________________________________________________________________Add components to container

		getContentPane().add(fieldPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setupMainFrame();
	}

	//BY BRANDON BALLARD
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("ADD"))
		{
			doAddEmployee();
		}
		else if(e.getActionCommand().equals("UPDATE"))
		{
			doUpdateEmployee();
		}
		else if(e.getActionCommand().equals("CANCEL"))
		{
			this.dispose();
		}
    }

	// Written by Hanif Mirza, this function will add a new Employee (RA/DM) to the database and also add it to the AdminGUI table
    void doAddEmployee()
    {
		try
		{
			if(checkIfValuesExist("Employee","userID",username))
			{
				throw new MyException("Username "+username+" is already taken" + "\n" + "Please use a unique username");
			}
			validateFields();

			// database statement to insert a new employee
			String	SQL1 = "INSERT INTO Employee(userID,first_name,last_name,email,phone,userID_RD)"
					   + " VALUES ("+ "'"+username+"'" +","+ "'"+firstName+"'" +","+ "'"+lastName+"'" +","+ "'"+email+"'" +","
					   + "'"+phone+"'" +","+ "'"+adminGUI.userID+"'"+")";

			int confirmationNo = myStatement.executeUpdate(SQL1);

			String	SQL2 = "INSERT INTO "+employeeType+"(userID,password)"
					     + " VALUES ("+ "'"+username+"'" +","+ "'"+password1+"'"+")";
			myStatement.executeUpdate(SQL2);

			Vector<Object> currentRow = new Vector<Object>();
			currentRow.addElement(username);
			currentRow.addElement(password1);
			currentRow.addElement(firstName);
			currentRow.addElement(lastName);
			currentRow.addElement(email);
			currentRow.addElement(phone);
			adminGUI.tableModel.addRow(currentRow); // add the new employee to the table

			if( confirmationNo > 0)
			{
				this.dispose();
				JOptionPane.showMessageDialog(this, "New employee "+firstName +" "+ lastName+" is added ", "successful" , JOptionPane.INFORMATION_MESSAGE);
			}
		}
		catch(MyException myEx)
		{
			JOptionPane.showMessageDialog(this,myEx.getMessage(),"Warning ",JOptionPane.WARNING_MESSAGE);
		}
		catch ( SQLException sqlException )
		{
			JOptionPane.showMessageDialog(this, sqlException.getMessage());
		}
		catch ( Exception exception )
		{
			JOptionPane.showMessageDialog(this, exception.getMessage());
		}
	}

	// Written by Hanif Mirza, this function will validate all the fields, if any field is invalid then it will through exception
	void validateFields() throws Exception
	{
		username = usernameTF.getText().trim();
		password1 = new String(password.getPassword()).trim();
		password2 = new String(rePassword.getPassword()).trim();
		firstName = firstNameTF.getText().trim();
		lastName = lastNameTF.getText().trim();
		email = emailTF.getText().trim();
		phone = phoneTF.getText().trim();

		if (!Validate.validateUsername(username))
		{
			throw new MyException("Username should contain at least 3 characters" + "\n" + "No special characters allowed (@#$%&*)");
		}
		else if (!password1.equals(password2))
		{
			throw new MyException("Passwords mismatched"+ "\n" + "Please re-enter the passwords");
		}
		else if (!Validate.validatePassword(password1))
		{
			throw new MyException("Invalid password" + "\n" + "It should contain at least 5 characters");
		}
		else if (!Validate.validateFirstName(firstName))
		{
			throw new MyException("Please enter a valid first name" + "\n" + "No special characters allowed (1@#$%&*)");
		}
		else if (!Validate.validateLastName(lastName))
		{
			throw new MyException("Please enter a valid last name" + "\n" + "No special characters allowed (1@#$%&*)");
		}
		else if (!Validate.validateEmail(email))
		{
			throw new MyException("Invalid email address");
		}
		else if (!Validate.validatePhoneNumber(phone))
		{
			throw new MyException("Invalid phone number"+ "\n" +"Phone number format xxx-xxx-xxxx ");
		}
	}

	//Written by Hanif Mirza, This function will use the information selected on table in AdminGUI.java to populate text fields with employee info
	void populateAllFields()
	{
		usernameTF.setText( adminGUI.tableModel.getValueAt(row,0).toString() );
		usernameTF.setEditable(false);
		password.setText( adminGUI.tableModel.getValueAt(row,1).toString() );
		rePassword.setText( adminGUI.tableModel.getValueAt(row,1).toString() );
		firstNameTF.setText( adminGUI.tableModel.getValueAt(row,2).toString() );
		lastNameTF.setText( adminGUI.tableModel.getValueAt(row,3).toString() );
		emailTF.setText( adminGUI.tableModel.getValueAt(row,4).toString() );
		phoneTF.setText( adminGUI.tableModel.getValueAt(row,5).toString() );
	}

	// Written by Hanif Mirza, This function update the information of selected employee from the table
	void doUpdateEmployee()
    {
		try
		{
			validateFields();

			// database statement to update employee details
			String	SQL1 = "UPDATE Employee "
						+ "SET first_Name = "+ "'"+firstName+"'" +","+ "last_name = "+ "'"+lastName +"'"+","
						+ "email = "+ "'"+email+"'" +","+"phone = "+ "'"+phone+"'"
						+ " Where userID = "+ "'"+username+"'";

			int confirmationNo = myStatement.executeUpdate(SQL1);

			String	SQL2 = "UPDATE "+employeeType
						 + " SET password = "+ "'"+password1+"'"
						 + " Where userID = "+ "'"+username+"'";

			myStatement.executeUpdate(SQL2);

			adminGUI.tableModel.setValueAt(password1, row,1);
		   	adminGUI.tableModel.setValueAt(firstName, row,2);
			adminGUI.tableModel.setValueAt(lastName, row,3);
			adminGUI.tableModel.setValueAt(email,  row,4);
			adminGUI.tableModel.setValueAt(phone,  row,5);

			if( confirmationNo > 0)
			{
				this.dispose();
				JOptionPane.showMessageDialog(this, "Employee "+firstName +" "+ lastName+" has been updated ", "successful" , JOptionPane.INFORMATION_MESSAGE);
			}
		}
		catch(MyException myEx)
		{
			JOptionPane.showMessageDialog(this,myEx.getMessage(), "Warning ",JOptionPane.WARNING_MESSAGE);
		}
		catch ( SQLException sqlException )
		{
			JOptionPane.showMessageDialog(this, sqlException.getMessage());
		}
		catch ( Exception exception )
		{
			JOptionPane.showMessageDialog(this, exception.getMessage());
		}
	}

    public void insertUpdate(DocumentEvent de)
    {
		username = usernameTF.getText().trim();
		password1 = new String(password.getPassword()).trim();
		password2 = new String(rePassword.getPassword()).trim();
		firstName = firstNameTF.getText().trim();
		lastName = lastNameTF.getText().trim();
		email = emailTF.getText().trim();
		phone = phoneTF.getText().trim();

        if( username.equals("") || password1.equals("") || password2.equals("")|| firstName.equals("") || lastName.equals("") || email.equals("") || phone.equals(""))
		{
			addButton.setEnabled(false);
        }
        else
        {
			addButton.setEnabled(true);
		}
    }
    public void changedUpdate(DocumentEvent de){}

    public void removeUpdate(DocumentEvent de)
    {
		username = usernameTF.getText().trim();
		password1 = new String(password.getPassword()).trim();
		password2 = new String(rePassword.getPassword()).trim();
		firstName = firstNameTF.getText().trim();
		lastName = lastNameTF.getText().trim();
		email = emailTF.getText().trim();
		phone = phoneTF.getText().trim();

        if( username.equals("") || password1.equals("") || password2.equals("")|| firstName.equals("") || lastName.equals("") || email.equals("") || phone.equals(""))
		{
			addButton.setEnabled(false);
        }
        else
        {
			addButton.setEnabled(true);
		}
    }

    //Written by Hanif Mirza, this function will return true if values exist in specific column of a database table
    boolean checkIfValuesExist(String tableName,String columnName,String columnValue) throws Exception
    {
		 String SQL_Query = "Select * "+"From "+tableName+" WHERE "+columnName+" LIKE "+ "'"+columnValue+"'";

		 ResultSet resultSet = myStatement.executeQuery(SQL_Query);// query database
		 if(!resultSet.next())
		 {
			 resultSet.close();
			 return false; // value doesn't exist, so return false
		 }
		 else
		 {
			resultSet.close();
			return true; // value exists, so return true
		 }
    }

	//Written by Brandon Ballard, adds fields using group layout
    JPanel setFields()
    {
		GroupLayout layout;
		JPanel p;

		usernameLabel = new JLabel("Username:");
		usernameLabel.setForeground(Color.WHITE);
		rePasswordLabel = new JLabel("Confirm password:");
		rePasswordLabel.setForeground(Color.WHITE);
		passwordLabel = new JLabel("Password:");
		passwordLabel.setForeground(Color.WHITE);
		firstNameLabel = new JLabel("First Name:");
		firstNameLabel.setForeground(Color.WHITE);
		lastNameLabel = new JLabel("Last Name:");
		lastNameLabel.setForeground(Color.WHITE);
		emailLabel = new JLabel("Email:");
		emailLabel.setForeground(Color.WHITE);
		phoneLabel = new JLabel("Phone Number:");
		phoneLabel.setForeground(Color.WHITE);
		accountLabel = new JLabel("Employee Type:");
		accountLabel.setForeground(Color.WHITE);

		usernameTF = new JTextField(30);
		password = new JPasswordField(30);
		rePassword = new JPasswordField(30);
		firstNameTF = new JTextField(30);
		lastNameTF = new JTextField(30);
		emailTF = new JTextField(30);
		phoneTF = new JTextField(30);

		usernameTF.getDocument().addDocumentListener(this);
		password.getDocument().addDocumentListener(this);
		rePassword.getDocument().addDocumentListener(this);
		emailTF.getDocument().addDocumentListener(this);
		phoneTF.getDocument().addDocumentListener(this);
		firstNameTF.getDocument().addDocumentListener(this);
		lastNameTF.getDocument().addDocumentListener(this);

		p = new JPanel();
		p.setBackground(new Color(1, 0.1f, 0.1f).darker().darker());

		layout = new GroupLayout(p);
		p.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup().addComponent(usernameLabel).addComponent(passwordLabel)
		.addComponent(rePasswordLabel).addComponent(firstNameLabel).addComponent(lastNameLabel)
		.addComponent(emailLabel).addComponent(phoneLabel));
		hGroup.addGroup(layout.createParallelGroup().addComponent(usernameTF).addComponent(password)
		.addComponent(rePassword).addComponent(firstNameTF).addComponent(lastNameTF).addComponent(emailTF).addComponent(phoneTF));
		layout.setHorizontalGroup(hGroup);


		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(usernameLabel).addComponent(usernameTF));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(passwordLabel).addComponent(password));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(rePasswordLabel).addComponent(rePassword));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(firstNameLabel).addComponent(firstNameTF));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(lastNameLabel).addComponent(lastNameTF));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(emailLabel).addComponent(emailTF));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(phoneLabel).addComponent(phoneTF));
		layout.setVerticalGroup(vGroup);

		layout.setVerticalGroup(vGroup);

		return(p);
	}

    void setupMainFrame()
	{
		Dimension   d;
		Toolkit     tk;
		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();
		setSize(d.width/3, d.height/3 + d.height/20);
		setLocation(d.width/3, d.height/4 + d.height/30);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
    }
}