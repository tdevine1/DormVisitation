/* * * * * * * * * * *\
 * AddBannedGuestDialog.java
 * Description: Allows user to enter banned guest information and save it, used for adding and editing banned guests.
 * 				Will validate each text field when user submits. Only the resident director will be able to add, edit or
 *				delete a banned guest.
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

class AddBannedGuestDialog extends JDialog implements ActionListener,DocumentListener
{
	JPanel 						buttonPanel, fieldPanel;
	JButton 					cancelButton, addButton;
	AdminGUI					adminGUI;
	Statement 					myStatement;
	JTextField 					guestNameTF, categoryTF;
	JLabel 						guestNameLabel, genderLabel, dormNameLabel, commentLabel, categoryLabel;
	JComboBox 					genderCBox, dormNameCBox;
	JTextArea					commentArea;
	String						guestID,guestName,gender,location,category,comments;
	int 						row;

	//CONSTRUSTOR ADDING A BANNED GUEST BY BRANDON BALLARD
	public AddBannedGuestDialog(AdminGUI adminGUI,Statement myStatement)
	{
		setTitle("Add Banned Guest");

		this.myStatement = myStatement;
		this.adminGUI = adminGUI;

		//________________________________________________________________Create buttons and button panel

		addButton = new JButton("Add Banned Guest");
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

	//CONSTRUSTOR FOR EDITING A BANNED GUEST BY BRANDON BALLARD
	public AddBannedGuestDialog(AdminGUI adminGUI,Statement myStatement,String guestID , int row)
	{
		setTitle("Edit Banned Guest");

		this.myStatement = myStatement;
		this.adminGUI = adminGUI;
		this.guestID = guestID;
		this.row = row;

		//________________________________________________________________Create buttons and button panel

		addButton = new JButton("Update Banned Guest");
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

		//________________________________________________________________Populate text fields with selected account info

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
			doAddGuest();
		}
		else if(e.getActionCommand().equals("UPDATE"))
		{
			doUpdateGuest();
		}
		else if(e.getActionCommand().equals("CANCEL"))
		{
			this.dispose();
		}
    }

	// Written by Hanif Mirza, this function will add a new banned guest to the database and also add it to the AdminGUI table
    void doAddGuest()
    {
		try
		{
			validateFields();

			// database statement to insert a new banned guest
			String	sql = "INSERT INTO Banned_Guest(guest_name,gender,dorm_name,category,comments,userID_RD)"
					   + " VALUES ("+ "'"+guestName+"'" +","+ "'"+gender+"'" +","+ "'"+location+"'" +","+ "'"+category+"'" +","+ "'"+comments+"'" +","
					   + "'"+adminGUI.userID+"'" +")";

			int confirmationNo = myStatement.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);

			ResultSet rs = myStatement.getGeneratedKeys(); // the Auto Generated Primary Key, which is banned guest ID

			int guestID = 0;
			if (rs.next())
			{
			   guestID = rs.getInt(1);
			}
			rs.close();

			Vector<Object> currentRow = new Vector<Object>();
			currentRow.addElement(guestID);
			currentRow.addElement(guestName);
			currentRow.addElement(gender);
			currentRow.addElement(location);
			currentRow.addElement(category);
			currentRow.addElement(comments);
			adminGUI.tableModel.addRow(currentRow); // add the new banned guest to the table

			if( confirmationNo > 0)
			{
				this.dispose();
				JOptionPane.showMessageDialog(this, "New banned guest "+guestName+" is added ", "successful" , JOptionPane.INFORMATION_MESSAGE);
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
		guestName = guestNameTF.getText().trim();
		category = categoryTF.getText().trim().replaceAll("'", "\\\\'") ;
		gender = genderCBox.getSelectedItem().toString();
		location = dormNameCBox.getSelectedItem().toString();
		comments = commentArea.getText().trim().replaceAll("'", "\\\\'") ;

		if (!Validate.validateFullName(guestName))
		{
			throw new MyException("Please enter first and last name of guest" + "\n" + "No special characters allowed (1@#$%&*)");
		}
		else if (gender.equals("-Select-"))
		{
			throw new MyException("Please select a gender");
		}
		else if (location.equals("-Select-"))
		{
			throw new MyException("Please select a location");
		}
	}

	//Written by Hanif Mirza, This function will use the information selected on table in AdminGUI.java to populate text fields with banned guest info
	void populateAllFields()
	{
		guestNameTF.setText( adminGUI.tableModel.getValueAt(row,1).toString() );
		genderCBox.setSelectedItem( adminGUI.tableModel.getValueAt(row,2).toString() );
		dormNameCBox.setSelectedItem( adminGUI.tableModel.getValueAt(row,3).toString() );
		categoryTF.setText( adminGUI.tableModel.getValueAt(row,4).toString() );
		commentArea.setText( adminGUI.tableModel.getValueAt(row,5).toString() );
	}

	// Written by Hanif Mirza, This function update the information of selected banned guest from the table
	void doUpdateGuest()
    {
		try
		{
			validateFields();

			// database statement to update resident details
			String	sql = "UPDATE Banned_Guest "
						+ "SET guest_name = "+ "'"+guestName+"'" +","+ "gender = "+ "'"+gender +"'"+","
						+ "dorm_name = "+ "'"+location+"'" +","+ "category = "+ "'"+category+"'" +","
						+ "comments = "+ "'"+comments+"'"
						+ " Where guestID = "+ "'"+guestID+"'";

			int confirmationNo = myStatement.executeUpdate(sql);

		   	adminGUI.tableModel.setValueAt(guestName, row,1);
			adminGUI.tableModel.setValueAt(gender, row,2);
			adminGUI.tableModel.setValueAt(location,  row,3);
			adminGUI.tableModel.setValueAt(category,  row,4);
			adminGUI.tableModel.setValueAt(comments,  row,5);

			if( confirmationNo > 0)
			{
				this.dispose();
				JOptionPane.showMessageDialog(this, "Banned guest "+guestName+" has been updated ", "successful" , JOptionPane.INFORMATION_MESSAGE);
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
		guestName = guestNameTF.getText().trim();
		category = categoryTF.getText().trim();

        if( guestName.equals("") || category.equals("") )
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
		guestName = guestNameTF.getText().trim();
		category = categoryTF.getText().trim();

        if( guestName.equals("") || category.equals("") )
		{
			addButton.setEnabled(false);
        }
        else
        {
			addButton.setEnabled(true);
		}
    }

	//Written by Brandon Ballard, sets fields in a group layout
    JPanel setFields()
    {
		GroupLayout layout;
		JPanel p;

		guestNameLabel = new JLabel("Name of guest:");
		guestNameLabel.setForeground(Color.WHITE);
		genderLabel = new JLabel("Gender:");
		genderLabel.setForeground(Color.WHITE);
		dormNameLabel = new JLabel("Location:");
		dormNameLabel.setForeground(Color.WHITE);
		categoryLabel = new JLabel("Category:");
		categoryLabel.setForeground(Color.WHITE);
		commentLabel = new JLabel("Comments:");
		commentLabel.setForeground(Color.WHITE);

		guestNameTF = new JTextField(30);
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
		dormNameCBox.addItem("All Locations");
		categoryTF = new JTextField(30);
		commentArea = new JTextArea();
		commentArea.setRows(4);
		commentArea.setWrapStyleWord(true);

		categoryTF.getDocument().addDocumentListener(this);
		guestNameTF.getDocument().addDocumentListener(this);
		commentArea.getDocument().addDocumentListener(this);

		p = new JPanel();
		p.setBackground(new Color(1, 0.1f, 0.1f).darker().darker());

		layout = new GroupLayout(p);
		p.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup().addComponent(guestNameLabel)
		.addComponent(genderLabel).addComponent(dormNameLabel).addComponent(categoryLabel).addComponent(commentLabel));
		hGroup.addGroup(layout.createParallelGroup().addComponent(guestNameTF)
		.addComponent(genderCBox).addComponent(dormNameCBox).addComponent(categoryTF).addComponent(commentArea));
		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(guestNameLabel).addComponent(guestNameTF));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(genderLabel).addComponent(genderCBox));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(dormNameLabel).addComponent(dormNameCBox));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(categoryLabel).addComponent(categoryTF));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(commentLabel).addComponent(commentArea));

		layout.setVerticalGroup(vGroup);

		return(p);
	}

    void setupMainFrame()
	{
		Dimension   d;
		Toolkit     tk;
		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();
		setSize(d.width/3, d.height/3);
		setLocation(d.width/3, d.height/4 + d.height/20);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
    }
}
