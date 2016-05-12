/* * * * * * * * * * *\
 * AddResidentDialog.java
 * Description: Allows user to enter resident information and save it, used for adding and editing residents.
 * 				Will validate each field when user submits. Only the resident director will be able to add, edit or
 *				delete a resident.
 *
 * Date: 4/10/16
 * @author Brandon Ballard & Hanif Mirza
\* * * * * * * * * * */

import java.awt.event.*;
import static javax.swing.GroupLayout.Alignment.*;
import java.sql.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

class AddResidentDialog extends JDialog implements ActionListener,DocumentListener
{
	JPanel 						buttonPanel, fieldPanel;
	JButton 					cancelButton, addButton;
	AdminGUI					adminGUI;
	Statement 					myStatement;
	JTextField 					residentIDTF,roomNoTF, dormNameTF, firstNameTF, lastNameTF, emailTF, phoneTF,lockoutTF;
	String	 					residentID,roomNo, dormName, firstName, lastName, email, phone,gender,lockoutNo;
	JLabel 						residentIDLabel,roomNoLabel, genderLabel, dormNameLabel, accountLabel, firstNameLabel, lastNameLabel, emailLabel, phoneLabel,lockoutLabel;
	JComboBox 					genderCBox, dormNameCBox;
	int 						row;

	//CONSTRUCTOR FOR ADDING A RESIDENT BY BRANDON BALLARD
	public AddResidentDialog(AdminGUI adminGUI,Statement myStatement)
	{
		setTitle("Add Resident");

		this.myStatement = myStatement;
		this.adminGUI = adminGUI;

		//________________________________________________________________Create buttons and button panel

		addButton = new JButton("Add Resident");
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
		lockoutTF.setText("0");

		//________________________________________________________________Add components to container

		getContentPane().add(fieldPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setupMainFrame();
	}

	//CONSTRUCTOR FOR EDITING A RESIDENT BY BRANDON BALLARD
	public AddResidentDialog(AdminGUI adminGUI,Statement myStatement,String residentID , int row)
	{
		setTitle("Edit Resident");
		this.myStatement = myStatement;
		this.adminGUI = adminGUI;
		this.residentID = residentID;
		this.row = row;

		//________________________________________________________________Create buttons and button panel

		addButton = new JButton("Update Resident");
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

		getContentPane().add(fieldPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setupMainFrame();
	}

	//WRITTEN BY BRANDON BALLARD
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("ADD"))
		{
			doAddResident();
		}
		else if(e.getActionCommand().equals("UPDATE"))
		{
			doUpdateResident();
		}
		else if(e.getActionCommand().equals("CANCEL"))
		{
			this.dispose();
		}
    }

	// Written by Hanif Mirza, this function will add a new resident to the database and also add it to the AdminGUI table
    void doAddResident()
    {
		try
		{
			if(checkIfValuesExist("Resident","userID",residentID))
			{
				throw new MyException("F-number "+residentID+" is taken" + "\n" + "Please use a unique F-number");
			}
			validateFields();

			// database statement to insert a new resident
			String	sql = "INSERT INTO Resident(userID,first_name,last_name,gender,email,phone,dorm_name,room_number,number_of_lockouts)"
					   + " VALUES ("+ "'"+residentID+"'" +","+ "'"+firstName+"'" +","+ "'"+lastName+"'" +","+ "'"+gender+"'" +","+ "'"+email+"'" +","
					   + "'"+phone+"'" +","+  "'"+dormName+"'" + ","+roomNo+","+lockoutNo+")";


			int confirmationNo = myStatement.executeUpdate(sql);

			Vector<Object> currentRow = new Vector<Object>();
			currentRow.addElement(residentID);
			currentRow.addElement(firstName);
			currentRow.addElement(lastName);
			currentRow.addElement(gender);
			currentRow.addElement(email);
			currentRow.addElement(phone);
			currentRow.addElement(dormName);
			currentRow.addElement(roomNo);
			currentRow.addElement(lockoutNo);
			adminGUI.tableModel.addRow(currentRow); // add the new resident to the table

			if( confirmationNo > 0)
			{
				this.dispose();
				JOptionPane.showMessageDialog(this, "New resident "+firstName +" "+ lastName+" is added ", "successful" , JOptionPane.INFORMATION_MESSAGE);
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

	// Written by Hanif Mirza, this function will validate all the fields, if any field is invalid then it will through exception
	void validateFields() throws Exception
	{
		residentID = residentIDTF.getText().trim();
		roomNo = roomNoTF.getText().trim();
		dormName = dormNameCBox.getSelectedItem().toString();
		firstName = firstNameTF.getText().trim();
		lastName = lastNameTF.getText().trim();
		email = emailTF.getText().trim();
		phone = phoneTF.getText().trim();
		gender = genderCBox.getSelectedItem().toString();
		lockoutNo = lockoutTF.getText().trim();

		if (!Validate.validateFirstName(firstName))
		{
			throw new MyException("Please enter a valid first name" + "\n" + "No special characters allowed (1@#$%&*)");
		}
		else if (!Validate.validateLastName(lastName))
		{
			throw new MyException("Please enter a valid last name" + "\n" + "No special characters allowed (1@#$%&*)");
		}
		else if (gender.equals("-Select-"))
		{
			throw new MyException("Please select a gender");
		}
		else if (dormName.equals("-Select-"))
		{
			throw new MyException("Please select a dorm name");
		}
		else if (!Validate.validateNumber(roomNo))
		{
			throw new MyException("Invalid room number");
		}
		else if (!Validate.validateEmail(email))
		{
			throw new MyException("Invalid email address");
		}
		else if (!Validate.validatePhoneNumber(phone))
		{
			throw new MyException("Invalid phone number"+ "\n" +"Phone number format xxx-xxx-xxxx ");
		}
		else if (!Validate.validateNumber(lockoutNo))
		{
			throw new MyException("Invalid lockout number");
		}
	}

	//Written by Hanif Mirza, This function will use the information selected on table in AdminGUI.java to populate text fields with resident info
	void populateAllFields()
	{
		residentIDTF.setText( adminGUI.tableModel.getValueAt(row,0).toString() );
		residentIDTF.setEditable(false);
		firstNameTF.setText( adminGUI.tableModel.getValueAt(row,1).toString() );
		lastNameTF.setText( adminGUI.tableModel.getValueAt(row,2).toString() );
		genderCBox.setSelectedItem( adminGUI.tableModel.getValueAt(row,3).toString() );
		emailTF.setText( adminGUI.tableModel.getValueAt(row,4).toString() );
		phoneTF.setText( adminGUI.tableModel.getValueAt(row,5).toString() );
		dormNameCBox.setSelectedItem( adminGUI.tableModel.getValueAt(row,6).toString() );
		roomNoTF.setText( adminGUI.tableModel.getValueAt(row,7).toString() );
		lockoutTF.setText( adminGUI.tableModel.getValueAt(row,8).toString() );
	}

	// Written by Hanif Mirza, This function update the information of selected resident from the table
	void doUpdateResident()
    {
		try
		{
			validateFields();

			// database statement to update resident details
			String	sql = "UPDATE Resident "
						+ "SET first_name = "+ "'"+firstName+"'" +","+ "last_name = "+ "'"+lastName +"'"+","
						+ "gender = "+ "'"+gender+"'" +","+ "email = "+ "'"+email+"'" +","
						+ "phone = "+ "'"+phone+"'" +","+ "dorm_name = "+ "'"+dormName+"'"+","+ "room_number = "+roomNo+ ","+ "number_of_lockouts = "+lockoutNo
						+ " Where userID = "+ "'"+residentID+"'";

			int confirmationNo = myStatement.executeUpdate(sql);

		   	adminGUI.tableModel.setValueAt(firstName, row,1);
			adminGUI.tableModel.setValueAt(lastName, row,2);
			adminGUI.tableModel.setValueAt(gender,  row,3);
			adminGUI.tableModel.setValueAt(email,  row,4);
			adminGUI.tableModel.setValueAt(phone,  row,5);
			adminGUI.tableModel.setValueAt(dormName,  row,6);
			adminGUI.tableModel.setValueAt(roomNo,  row,7);
			adminGUI.tableModel.setValueAt(lockoutNo,  row,8);

			if( confirmationNo > 0)
			{
				this.dispose();
				JOptionPane.showMessageDialog(this, "Resident "+firstName +" "+ lastName+" has been updated ", "successful" , JOptionPane.INFORMATION_MESSAGE);
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
		residentID = residentIDTF.getText().trim();
		roomNo = roomNoTF.getText().trim();
		firstName = firstNameTF.getText().trim();
		lastName = lastNameTF.getText().trim();
		email = emailTF.getText().trim();
		phone = phoneTF.getText().trim();
		lockoutNo = lockoutTF.getText().trim();

        if( residentID.equals("") || roomNo.equals("") || firstName.equals("")|| lastName.equals("") || email.equals("") || phone.equals("") || lockoutNo.equals(""))
		{
			addButton.setEnabled(false);
        }
        else
        {
			addButton.setEnabled(true);
		}
    }

    public void removeUpdate(DocumentEvent de)
    {
		residentID = residentIDTF.getText().trim();
		roomNo = roomNoTF.getText().trim();
		firstName = firstNameTF.getText().trim();
		lastName = lastNameTF.getText().trim();
		email = emailTF.getText().trim();
		phone = phoneTF.getText().trim();
		lockoutNo = lockoutTF.getText().trim();

        if( residentID.equals("") || roomNo.equals("") || firstName.equals("")|| lastName.equals("") || email.equals("") || phone.equals("")|| lockoutNo.equals("") )
		{
			addButton.setEnabled(false);
        }
        else
        {
			addButton.setEnabled(true);
		}
    }
    public void changedUpdate(DocumentEvent de){}

    //Written by Hanif Mirza, this function will return true if values exist in specific column of a database table
    boolean checkIfValuesExist(String tableName,String columnName,String columnValue) throws Exception
    {
		 String SQL_Query = "Select * "+"From "+tableName+" WHERE "+columnName+" LIKE "+ "'"+columnValue+"'";

		 ResultSet resultSet = myStatement.executeQuery(SQL_Query);// query database
		 if(!resultSet.next())
		 {
			 resultSet.close();
			 return false; // values don't exist, so return false
		 }
		 else
		 {
			resultSet.close();
			return true; // values exist, so return true
		 }
    }

	//Adds fields using a group layout, by BRANDON BALLARD
    JPanel setFields()
    {
		GroupLayout layout;
		JPanel p;

		residentIDLabel = new JLabel("F-number:");
		residentIDLabel.setForeground(Color.WHITE);
		genderLabel = new JLabel("Gender:");
		genderLabel.setForeground(Color.WHITE);
		dormNameLabel = new JLabel("Location:");
		dormNameLabel.setForeground(Color.WHITE);
		genderCBox = new JComboBox();
		genderCBox.addItem("-Select-");
		genderCBox.addItem("Male");
		genderCBox.addItem("Female");
		dormNameCBox = new JComboBox();
		dormNameCBox.addItem("-Select-");
		dormNameCBox.addItem("Bryant Place");
		dormNameCBox.addItem("Prichard Hall");
		dormNameCBox.addItem("Morrow Hall");
		dormNameCBox.addItem("Pence Hall");
		firstNameLabel = new JLabel("First Name:");
		firstNameLabel.setForeground(Color.WHITE);
		lastNameLabel = new JLabel("Last Name:");
		lastNameLabel.setForeground(Color.WHITE);
		emailLabel = new JLabel("Email:");
		emailLabel.setForeground(Color.WHITE);
		phoneLabel = new JLabel("Phone Number:");
		phoneLabel.setForeground(Color.WHITE);
		lockoutLabel = new JLabel("Lockout Number:");
		lockoutLabel.setForeground(Color.WHITE);

		residentIDTF = new JTextField(30);
		firstNameTF = new JTextField(30);
		lastNameTF = new JTextField(30);
		emailTF = new JTextField(30);
		phoneTF = new JTextField(30);
		lockoutTF = new JTextField(30);
		roomNoTF = new JTextField(30);
		roomNoLabel = new JLabel("Room Number:");
		roomNoLabel.setForeground(Color.WHITE);

		residentIDTF.getDocument().addDocumentListener(this);
		emailTF.getDocument().addDocumentListener(this);
		phoneTF.getDocument().addDocumentListener(this);
		roomNoTF.getDocument().addDocumentListener(this);
		lockoutTF.getDocument().addDocumentListener(this);
		firstNameTF.getDocument().addDocumentListener(this);
		lastNameTF.getDocument().addDocumentListener(this);

		p = new JPanel();
		p.setBackground(new Color(1, 0.1f, 0.1f).darker().darker());

		layout = new GroupLayout(p);
		p.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup().addComponent(residentIDLabel).addComponent(firstNameLabel).addComponent(lastNameLabel)
		.addComponent(genderLabel).addComponent(dormNameLabel).addComponent(roomNoLabel)
		.addComponent(emailLabel).addComponent(phoneLabel).addComponent(lockoutLabel));
		hGroup.addGroup(layout.createParallelGroup().addComponent(residentIDTF).addComponent(firstNameTF).addComponent(lastNameTF)
		.addComponent(genderCBox).addComponent(dormNameCBox).addComponent(roomNoTF)
		.addComponent(emailTF).addComponent(phoneTF).addComponent(lockoutTF));
		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(residentIDLabel).addComponent(residentIDTF));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(firstNameLabel).addComponent(firstNameTF));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(lastNameLabel).addComponent(lastNameTF));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(genderLabel).addComponent(genderCBox));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(dormNameLabel).addComponent(dormNameCBox));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(roomNoLabel).addComponent(roomNoTF));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(emailLabel).addComponent(emailTF));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(phoneLabel).addComponent(phoneTF));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(lockoutLabel).addComponent(lockoutTF));
		layout.setVerticalGroup(vGroup);

		layout.setVerticalGroup(vGroup);

		return(p);
	}

	//BY BRANDON BALLARD
    void setupMainFrame()
	{
		Dimension   d;
		Toolkit     tk;
		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();
		setSize(d.width/3, 380);
		setLocation(d.width/3, d.height/4 + d.height/20);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
    }
}