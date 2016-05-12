/* * * * * * * * * * *\
 * LockOutDialog.java
 * Description: Used for adding lock outs to resident accounts. user can enter student information or use the swipe card feature.
 *				Once submitted, the class will tell the user is the lockout was successfully processed and will notify the user if
 *				the student is out of free lock outs and needs to be charged.
 * Date: 4/4/16
 * @author Brandon Ballard & Hanif Mirza
\* * * * * * * * * * */

import java.awt.event.*;
import static javax.swing.GroupLayout.Alignment.*;
import java.util.*;
import java.text.*;
import java.sql.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Date;
import java.text.SimpleDateFormat;

class LockOutDialog extends JDialog implements ActionListener,DocumentListener, KeyListener
{
	JPanel 						buttonPanel, fieldPanel;
	JButton 					cancelButton, addButton;
	JTextField 					studentRoomTF, raNameTF;
	JLabel 						studentRoomLabel, studentNameLabel, raNameLabel;
	JComboBox 					residentNameCBox;
	Statement 					myStatement;
	String						residentName, raName, residentRoomNo;
	String						keyString, str = "";
	DateFormat 					dateFormat, timeFormat;
	Hashtable<String,Resident> 	residentHT;
	DefaultGUI					myDefaultGUI;
	int 						keyCount;

	//BY BRANDON BALLARD
	public LockOutDialog(DefaultGUI urDefaultGUI,Hashtable<String,Resident> urHashtable,Statement urStatement)
	{
		keyCount = 0;
		keyString = "";
		this.myStatement = urStatement;
		this.residentHT = urHashtable;
		this.myDefaultGUI = urDefaultGUI;

		addButton = new JButton("Add Lock Out");
		addButton.setBackground(Color.WHITE);
		addButton.addActionListener(this);
		addButton.setActionCommand("ADD");
		addButton.setEnabled(false);

		cancelButton = new JButton("Cancel");
		cancelButton.setBackground(Color.WHITE);
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("CANCEL");

		buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.setBackground(Color.GRAY);
		buttonPanel.add(cancelButton);
		buttonPanel.add(addButton);

		fieldPanel = setFields();

		getContentPane().add(fieldPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setupMainFrame();
	}

	//BY BRANDON BALLARD
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("ADD"))
		{
			doAddLockOut();
		}
		else if(e.getActionCommand().equals("CANCEL"))
		{
			this.dispose();
		}
    }

	// Written by Hanif Mirza, this function will add the lockout details of the resident to the database
    void doAddLockOut()
    {
		ResultSet resultSet;
		int 	  lockoutNum;
		try
		{
			validateFields();
			Resident myResident = residentHT.get(residentRoomNo); // get the Resident object from resident's room number
			String residentID = findKey(residentName,myResident.residentHashtable);//get  resident ID (the key) from resident name (the value)

			String	SQL_Query = "SELECT number_of_lockouts FROM Resident WHERE userID = " + residentID ;
			resultSet = myStatement.executeQuery(SQL_Query);// Query to get the lockout number of the resident
			resultSet.first();
			lockoutNum = resultSet.getInt(1)+1;
			resultSet.close();
			if( lockoutNum > 5)
			{
				JOptionPane.showMessageDialog(this,residentName.split(" ")[0] + " has 0 lockouts remaining", "Warning",JOptionPane.WARNING_MESSAGE);
			}

			// database statement to insert the lockout details
			String insertSQL = "INSERT INTO Lockout_Detail(lockout_date,lockout_time,ra_name,empID,residentID)"
					   			+ " VALUES ("+ "CURDATE(),curtime(),"+ "'"+raName+"'" +","
					   			+ "'"+myDefaultGUI.userID+"'" +","+  "'"+residentID+"'" +")";

			int confirmationNo = myStatement.executeUpdate(insertSQL);

			// database statement to update the lockout details
			String	updateSQL = "UPDATE Resident "
							  + "SET number_of_lockouts = "+ lockoutNum
						      + " Where userID = "+ residentID;
			myStatement.executeUpdate(updateSQL);

			if( confirmationNo > 0)
			{
				this.dispose();
				JOptionPane.showMessageDialog(this,residentName.split(" ")[0] + "'s lockout has been processed", "Lockout successful" , JOptionPane.INFORMATION_MESSAGE);
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
		residentRoomNo = studentRoomTF.getText().trim();
		residentName = residentNameCBox.getSelectedItem().toString();
		raName = raNameTF.getText().trim();
		raName = raName.replaceAll("'", "\\\\'");

		if (!Validate.validateNumber(residentRoomNo))
		{
			throw new MyException("Invalid room number");
		}
		else if (residentName.equals("-Select-"))
		{
			throw new MyException("Please select a student name");
		}
		else if (!Validate.validateFullName(raName))
		{
			throw new MyException("Please enter first and last name of guest" + "\n" + "No special characters allowed (1@#$%&*)");
		}
	}

	// Written by Hanif Mirza, this function will find resident ID (the key) using the resident full name (the value) from the hashtable
	public String findKey(String value, Hashtable HT)
	{
		    String myKey = "";
			Set<String> keys = HT.keySet();
			for(String key: keys)
			{
				if (HT.get(key).equals(value))
				{
				   myKey = key;
				}
			}

		   return myKey;
	}

    public void insertUpdate(DocumentEvent de)
    {
		residentRoomNo = studentRoomTF.getText().trim();
		residentName = residentNameCBox.getSelectedItem().toString();
		raName = raNameTF.getText().trim();

		// check if the source text field is studentRoomTF
		if (de.getDocument() == studentRoomTF.getDocument() )
		{
			residentNameCBox.removeAllItems(); // remove all previous items from the combo box
			DefaultComboBoxModel model = new DefaultComboBoxModel();
			model.addElement( "-Select-" );
			if ( residentHT.containsKey(residentRoomNo) )
			{
				Resident myResident = residentHT.get(residentRoomNo); // get the resident object using the room number (key)
				Set<String> keys = myResident.residentHashtable.keySet();
				for(String key: keys)
				{
					String residentName = myResident.residentHashtable.get(key);//get the resident full name from the resident ID (key)
					model.addElement( residentName );
				}
			}
			residentNameCBox.setModel(model);
		}

        if( residentRoomNo.equals("") ||raName.equals(""))
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
		residentRoomNo = studentRoomTF.getText().trim();
		residentName = residentNameCBox.getSelectedItem().toString();
		raName = raNameTF.getText().trim();

		// check if the source text field is studentRoomTF
		if (de.getDocument() == studentRoomTF.getDocument() )
		{
			residentNameCBox.removeAllItems(); // remove all previous items from the combo box
			DefaultComboBoxModel model = new DefaultComboBoxModel();
			model.addElement( "-Select-" );

			if ( residentHT.containsKey(residentRoomNo) )
			{
				Resident myResident = residentHT.get(residentRoomNo); // get the resident object using the room number (key)
				Set<String> keys = myResident.residentHashtable.keySet();
				for(String key: keys)
				{
					String residentName = myResident.residentHashtable.get(key);//get the resident full name from the resident ID (key)
					model.addElement( residentName );
				}
			}
			residentNameCBox.setModel(model);
		}

        if( residentRoomNo.equals("") || raName.equals(""))
        {
            addButton.setEnabled(false);
        }
        else
        {
			addButton.setEnabled(true);
		}
    }

    public void keyReleased(KeyEvent ke)
    {
	}

    public void keyPressed(KeyEvent ke)
    {
	}

	//BY BRANDON BALLARD, this function will detect when a user swipes a card in the appropriate text field.
    public void keyTyped(KeyEvent ke)
    {
		//All cards start with a '%', so if the function detects a '%' and it hasn't previously detected one or if keyString
		//starts with a '%', then the function adds the text to keyString and moves on to the if statement.
		if((Character.toString(ke.getKeyChar()).equals("%") && keyCount == 0) || keyString.startsWith("%"))
		{
			keyCount++;
			keyString = keyString + Character.toString(ke.getKeyChar());

			//This if statemnt checks to see if keySting contains everything it needs to be considered a card. All cards end in
			//'%HH' so it first checks for that and then makes sure the string is at least 20 characers long, if it is then it
			//makes sure that the string contains at leat one '^' and at least one '?', if everything is good then the function
			//recognizes the string as a card swipe and moves on to the for loop.
			if(keyString.endsWith("%HH") && keyCount > 20 && keyString.contains("^") && keyString.contains("?"))
			{
				int y = 0;
				keyCount = 0;

				//this for loop starts at the end of keyString and backs up until it finds two '%' symbols, if it successfully
				//found two then the function confirms that the user has swiped a card and proceeds.
				for(int x = keyString.length() - 1; x >= 0; x--)
				{
					if(Character.toString(keyString.charAt(x)).equals("%"))
					{
						y++;

						//After a card swipe is detected the function checks to see which text field the swipe took place in
						//and calls the appropriate function.
						if(y == 2)
						{
							residentRoomNo = keyString.substring(x);
							if(this.getFocusOwner() == studentRoomTF)
							{
								System.out.println("4");
								processCardSwipe();
							}
							if(this.getFocusOwner() == raNameTF)
							{
								JOptionPane.showMessageDialog(null, "Could not read card \n Please enter RA's name manually", "Error" , JOptionPane.ERROR_MESSAGE);
								raNameTF.setText("");
							}
							keyString = "";
							x = -1;
						}
					}
				}
			}
		}
	}

	/* Written by Hanif Mirza. This function will process the resident's card swipe. It will parse the resident card ID
	   and access the database with that card ID to get all information of the resident. If it's valid resident then
	   it will populate all the fields of the resident. All this sequence of code must be done in a thread. */
	void processCardSwipe()
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				String studentID = residentRoomNo.substring(residentRoomNo.indexOf("B")+1 , residentRoomNo.indexOf("^"));
				studentRoomTF.setText("");
				try
				{
					String	SQL_Query = "SELECT * FROM Resident WHERE userID = " + studentID ;
					ResultSet rs = myStatement.executeQuery(SQL_Query);// Query to get the lockout number of the resident
					if(!rs.next())
					{
						//show Joptionpane if result set is empty
						JOptionPane.showMessageDialog(null,"Resident could not be found", "Error" , JOptionPane.ERROR_MESSAGE );
						studentRoomTF.setText("");
						studentRoomTF.requestFocus();
					}
					else
					{
						rs.first();//move ResultSet cursor to previous row
						residentRoomNo = rs.getInt(8)+"";
						studentRoomTF.setText(residentRoomNo);

						residentNameCBox.removeAllItems(); // remove all previous items from the combo box
						DefaultComboBoxModel model = new DefaultComboBoxModel();
						if ( residentHT.containsKey(residentRoomNo) )
						{
							Resident myResident = residentHT.get(residentRoomNo);
							Set<String> keys = myResident.residentHashtable.keySet();
							for(String key: keys)
							{
								String residentName = myResident.residentHashtable.get(key);//get the resident full name from the resident ID (key)
								model.addElement( residentName );
								raNameTF.requestFocus();
							}
						}
						residentNameCBox.setModel(model);
					}
					rs.close();
				}
				catch ( SQLException sqlException )
				{
					JOptionPane.showMessageDialog(null, "Resident could not be found", "Error" , JOptionPane.ERROR_MESSAGE );
					studentRoomTF.setText(residentRoomNo);
				}
				catch ( Exception exception )
				{
					JOptionPane.showMessageDialog(null, exception.getMessage(), "Error" , JOptionPane.ERROR_MESSAGE );
					studentRoomTF.setText(residentRoomNo);
				}
			}
		});
	}

	//Adds fields using a group layout, BY BRANDON BALLARD
    JPanel setFields()
    {
		GroupLayout layout;
		JPanel p;

		studentRoomLabel = new JLabel("Room Number:");
		studentRoomLabel.setForeground(Color.WHITE);
		studentNameLabel = new JLabel("Student Name:");
		studentNameLabel.setForeground(Color.WHITE);
		raNameLabel = new JLabel("RA Name:");
		raNameLabel.setForeground(Color.WHITE);

	 	studentRoomTF = new JTextField(30);
	 	studentRoomTF.addKeyListener(this);
	 	studentRoomTF.getDocument().addDocumentListener(this);

	 	raNameTF = new JTextField(30);
	 	raNameTF.getDocument().addDocumentListener(this);
	 	raNameTF.addKeyListener(this);

	 	residentNameCBox = new JComboBox();
		residentNameCBox.addItem("-Select-");
		residentNameCBox.addActionListener(this);

		p = new JPanel();
		p.setBackground(new Color(1, 0.1f, 0.1f).darker().darker());

		layout = new GroupLayout(p);
		p.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup().addComponent(studentRoomLabel).addComponent(studentNameLabel).addComponent(raNameLabel));
		hGroup.addGroup(layout.createParallelGroup().addComponent(studentRoomTF).addComponent(residentNameCBox).addComponent(raNameTF));
		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(studentRoomLabel).addComponent(studentRoomTF));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(studentNameLabel).addComponent(residentNameCBox));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(raNameLabel).addComponent(raNameTF));
		layout.setVerticalGroup(vGroup);

		return(p);
	}

    void setupMainFrame()
	{
		Dimension   d;
		Toolkit     tk;
		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();
		setTitle("Add a lock out");
		setSize(d.width/3, d.height/4);
		setLocation(d.width/3, d.height/4 + d.height/20);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
    }
}
