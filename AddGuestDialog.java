/* * * * * * * * * * *\
 * AddGuestDialog.java
 * Description: Allows user to enter guest information and save it, used for checking in and editing guests.
 *				Will validate each field when user submits. This dialog also has the card swipe feature. Rather
 *				than the user typing in the student room# and name and the guests name they can simply
 *				use a swipe card machine along with the student and guests FSU or Pierpont card.
 *
 * Date: 5/7/16
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

class AddGuestDialog extends JDialog implements ActionListener,DocumentListener, KeyListener
{

	JPanel 						buttonPanel, fieldPanel;
	JButton 					cancelButton, addButton;
	JTextField 					studentRoomTF, guestNameTF, guestAgeTF;
	JLabel 						studentRoomLabel, studentNameLabel, guestNameLabel, overnightLabel, guestAgeLabel, titleLabel, guestIDLabel;
	JCheckBox 					overnightCB;
	JScrollPane 				scrollPane;
	JComboBox 					residentNameCBox,guestIDTypes;
	DefaultGUI					myDefaultGUI;
	Statement 					myStatement;
	String						keyString, residentRoomNo, residentName, residentID, guestName, guestIDtype, guestAge, overnightStatus, currTime, currDate, myVisitationID;
	DateFormat 					dateFormat, timeFormat;
	Hashtable<String,Resident> 	residentHT;
	int 						row, keyCount;

	//CONSTRUCTOR FOR ADDING A GUEST BY BRANDON BALLARD
	public AddGuestDialog(DefaultGUI urDefaultGUI,Hashtable<String,Resident> urHashtable,Statement urStatement)
	{
		keyString = "";
		keyCount = 0;

		this.myStatement = urStatement;
		this.residentHT = urHashtable;
		this.myDefaultGUI = urDefaultGUI;
		this.row = row;

		dateFormat = new SimpleDateFormat("M/d/yyyy");
		timeFormat = new SimpleDateFormat("h:mma");

		//________________________________________________________________Create buttons and button panel

		addButton = new JButton("Add Guest");
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

		//________________________________________________________________Add text fields for user input

		fieldPanel = setFields();

	 	studentRoomTF.getDocument().addDocumentListener(this);
		guestNameTF.getDocument().addDocumentListener(this);
		guestAgeTF.getDocument().addDocumentListener(this);

		//________________________________________________________________Add components to container

		getContentPane().add(fieldPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setupMainFrame();
	}

	//CONSTRUSTOR FOR EDITING A GUEST BY BRANDON BALLARD
	public AddGuestDialog(DefaultGUI urDefaultGUI,Hashtable<String,Resident> urHashtable,Statement urStatement,String urVisitationID , int row)
	{
		keyString = "";
		keyCount = 0;

		this.myStatement = urStatement;
		this.residentHT = urHashtable;
		this.myDefaultGUI = urDefaultGUI;
		this.myVisitationID = urVisitationID;
		this.row = row;

		dateFormat = new SimpleDateFormat("M/d/yyyy");
		timeFormat = new SimpleDateFormat("h:mma");

		//________________________________________________________________Create buttons and button panel

		addButton = new JButton("Update Guest");
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

	 	studentRoomTF.getDocument().addDocumentListener(this);
		guestNameTF.getDocument().addDocumentListener(this);
		guestAgeTF.getDocument().addDocumentListener(this);

		//________________________________________________________________Add components to container

		getContentPane().add(fieldPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setupMainFrame();
		setTitle("Edit Guest");
	}
	//Written by Hanif Mirza, This function will use the information selected on table in DefaultGUI.java to populate text fields with guest info
	void populateAllFields()
	{
		studentRoomTF.setText( myDefaultGUI.tableModel.getValueAt(row,5).toString() );
		guestNameTF.setText( myDefaultGUI.tableModel.getValueAt(row,1).toString() );
		guestAgeTF.setText( myDefaultGUI.tableModel.getValueAt(row,2).toString() );
		guestIDTypes.setSelectedItem( myDefaultGUI.tableModel.getValueAt(row,3).toString() );
		String overnightStatus = myDefaultGUI.tableModel.getValueAt(row,8).toString();

	    residentNameCBox.removeAllItems(); // remove all previous items from the combo box
	    residentNameCBox.addItem( myDefaultGUI.tableModel.getValueAt(row,4).toString() );

		if ( overnightStatus.equals("No") )
		{
			overnightCB.setSelected(false);
		}
		else
		{
			overnightCB.setSelected(true);
		}
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

	// Written by Hanif Mirza, This function update the information of currently checked-in guest
	void doUpdateGuest()
    {
		try
		{
			validateFields();
			Resident myResident = residentHT.get(residentRoomNo); // get the Resident object from resident's room number
			String residentID = findKey(residentName,myResident.residentHashtable);//get  resident ID (the key) from resident name (the value)

			// database statement to update the check-out details
			String	sql = "UPDATE Visitation_Detail "
						+ "SET guest_name = "+ "'"+guestName+"'" +","+ "guest_age = "+ guestAge +","
						+ "guest_ID_type = "+ "'"+guestIDtype+"'" +","+ "overnight_status = "+ "'"+overnightStatus+"'" +","
						+ "empID = "+ "'"+myDefaultGUI.userID+"'" +","+ "residentID = "+ "'"+residentID+"'"
						+ " Where visitationID = "+ myVisitationID;

			int confirmationNo = myStatement.executeUpdate(sql);

		    myDefaultGUI.tableModel.setValueAt(guestName, row,1);
			myDefaultGUI.tableModel.setValueAt(guestAge, row,2);
			myDefaultGUI.tableModel.setValueAt(guestIDtype,  row,3);
			myDefaultGUI.tableModel.setValueAt(residentName,  row,4);
			myDefaultGUI.tableModel.setValueAt(residentRoomNo,  row,5);
			myDefaultGUI.tableModel.setValueAt(overnightStatus,  row,8);
			myDefaultGUI.tableModel.setValueAt(myDefaultGUI.myFullName,  row,9);

			if( confirmationNo > 0)
			{
				this.dispose();
				JOptionPane.showMessageDialog(this,"Guest has been updated", "Update successful" , JOptionPane.INFORMATION_MESSAGE );
			}
		}
		catch(MyException myEx)
		{
			JOptionPane.showMessageDialog(this,myEx.getMessage());
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

	// Written by Hanif Mirza, this function will add the checked-in details of new guest to the database and also add it to the DefaultGUI table
    void doAddGuest()
    {
		try
		{
			validateFields();
			Resident myResident = residentHT.get(residentRoomNo); // get the Resident object from resident's room number
			String residentID = findKey(residentName,myResident.residentHashtable);//get  resident ID (the key) from resident name (the value)

			// database statement to insert the check-in details
			String	sql = "INSERT INTO Visitation_Detail(guest_name,guest_age,guest_ID_type,visitation_date,time_in,overnight_status,empID,residentID)"
					   + " VALUES ("+ "'"+guestName+"'" +","+ guestAge +","+ "'"+guestIDtype+"'" +","+ "CURDATE(),curtime(),"+ "'"+overnightStatus+"'" +","
					   + "'"+myDefaultGUI.userID+"'" +","+  "'"+residentID+"'" +")";

			int confirmationNo = myStatement.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);

			ResultSet rs = myStatement.getGeneratedKeys(); // the Auto Generated Primary Key, which is visitation details ID

			int visitationID = 0;
			if (rs.next())
			{
			   visitationID = rs.getInt(1);
			}
			rs.close();

			Vector<Object> currentRow = new Vector<Object>();
			currentRow.addElement(visitationID);
			currentRow.addElement(guestName);
			currentRow.addElement(guestAge);
			currentRow.addElement(guestIDtype);
			currentRow.addElement(residentName);
			currentRow.addElement(residentRoomNo);
			currentRow.addElement(currDate);
			currentRow.addElement(currTime);
			currentRow.addElement(overnightStatus);
			currentRow.addElement(myDefaultGUI.myFullName);
			myDefaultGUI.tableModel.addRow(currentRow); // add the new check-out details to out table

			if( confirmationNo > 0)
			{
				this.dispose();
				JOptionPane.showMessageDialog(this, guestName + " is checked in", "Check in successful" , JOptionPane.INFORMATION_MESSAGE);
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

	// Written by Hanif Mirza, this function will validate all the fields, if any field is invalid then it will through an exception
	void validateFields() throws Exception
	{
		residentRoomNo = studentRoomTF.getText().trim();
		residentName = residentNameCBox.getSelectedItem().toString();
		guestIDtype = guestIDTypes.getSelectedItem().toString();
		guestName = guestNameTF.getText().trim();
		guestName = guestName.replaceAll("'", "\\\\'");
		guestAge = guestAgeTF.getText().trim();
		currDate = dateFormat.format(new Date());
		currTime = timeFormat.format(new Date());

		if(overnightCB.isSelected())
		{
			overnightStatus = "Yes";
		}
		else
		{
			overnightStatus = "No";
		}

		if (!Validate.validateNumber(residentRoomNo))
		{
			throw new MyException("Invalid room number");
		}
		else if (residentName.equals("-Select-"))
		{
			throw new MyException("Please select a student name");
		}
		else if (!Validate.validateFullName(guestName))
		{
			throw new MyException("Please enter first and last name of guest" + "\n" + "No special characters allowed (1@#$%&*)");
		}
		else if(checkIfValuesExist("Banned_Guest","guest_name",guestName))
		{
			throw new MyException(guestName+" has been banned from this location" + "\n" + "Please call campus police and notify RA");
		}
		else if (!Validate.validateNumber(guestAge))
		{
			throw new MyException("Invalid age");
		}
		else if (guestIDtype.equals("-Select-"))
		{
			throw new MyException("Please select an ID type");
		}
		else if (Validate.validateNumber(guestAge))
		{
			if (Integer.parseInt(guestAge) < 18)
			{
				JOptionPane.showMessageDialog(this,"Warning: This guest is under 18 years old, parental consent must be provided");
			}
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

    // Written by Hanif Mirza,this function will return true if values exist in specific column of a database table
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

   	public void insertUpdate(DocumentEvent de)
    {
		residentRoomNo = studentRoomTF.getText().trim();
		residentName = residentNameCBox.getSelectedItem().toString();
		guestIDtype = guestIDTypes.getSelectedItem().toString();
		guestName = guestNameTF.getText().trim();
		guestAge = guestAgeTF.getText().trim();

		// check if the users insert room number on studentRoomTF
		if (de.getDocument() == studentRoomTF.getDocument() )
		{
			residentNameCBox.removeAllItems();
			DefaultComboBoxModel model = new DefaultComboBoxModel();
			model.addElement( "-Select-" );

			if (residentHT.containsKey(residentRoomNo))
			{
				Resident myResident = residentHT.get(residentRoomNo); // get the resident object using the room number (key)
				Set<String> keys = myResident.residentHashtable.keySet();

				for(String key: keys)
				{
					String residentName = myResident.residentHashtable.get(key); //get the resident full name from the resident ID (key)
					model.addElement( residentName );
				}
			}
			residentNameCBox.setModel(model);
		}

        if( residentRoomNo.equals("") || guestName.equals("") || guestAge.equals("") )
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
		guestIDtype = guestIDTypes.getSelectedItem().toString();
		guestName = guestNameTF.getText().trim();
		guestAge = guestAgeTF.getText().trim();

		// check if the users insert room number on studentRoomTF
		if (de.getDocument() == studentRoomTF.getDocument() )
		{
			residentNameCBox.removeAllItems();
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

        if( residentRoomNo.equals("") || guestName.equals("") || guestAge.equals(""))
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
								processCardSwipe();
							}
							else if(this.getFocusOwner() == guestNameTF)
							{
								processGuestCardSwipe();
							}
							else if(this.getFocusOwner() == guestAgeTF)
							{
								JOptionPane.showMessageDialog(null, "Could not read card \n Please enter guest age manually", "Error" , JOptionPane.ERROR_MESSAGE);
								guestAgeTF.setText("");
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
					String	SQL_Query = "SELECT CONCAT(r.first_name,\" \",r.last_name),r.room_number FROM Resident r WHERE r.userID = " + studentID ;
					ResultSet rs = myStatement.executeQuery(SQL_Query);// Query to get the lockout number of the resident
					if(!rs.next())
					{
						//show Joptionpane if result set is empty
						JOptionPane.showMessageDialog(null,"Resident could not be found", "Error" , JOptionPane.ERROR_MESSAGE );
						studentRoomTF.requestFocus();
					}
					else
					{
						rs.first();//move ResultSet cursor to previous row
						String resName = rs.getString(1);
						residentRoomNo = rs.getInt(2)+"";
						studentRoomTF.setText(residentRoomNo);

						residentNameCBox.removeAllItems(); // remove all previous items from the combo box
						DefaultComboBoxModel model = new DefaultComboBoxModel();
						model.addElement( resName );
						residentNameCBox.setModel(model);
						guestNameTF.requestFocus();
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

	/* Written by Hanif Mirza. This function will process the guest's card swipe. It will parse the guest name from the guest ID and
	   set it to the guestNameTF. All this sequence of code must be done in a thread. */

	void processGuestCardSwipe()
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				try
				{
					String studentID = guestName.substring(guestName.indexOf("B")+1 , guestName.indexOf("^"));
					guestName = guestName.substring(guestName.indexOf("^")+1);

					String lastName = guestName.substring(0,guestName.indexOf("/")).trim();
					String part1 = lastName.substring(0,1);
					String part2 = lastName.substring(1).toLowerCase();
					lastName = part1 + part2; // First letter of last name is uppercase and rest of it are lower case

					String firstName = guestName.substring(guestName.indexOf("/")+1,guestName.indexOf("^")).trim();
					if(firstName.split(" ").length > 1)
					{
						firstName = firstName.split(" ")[0];
					}
					String firstPart = firstName.substring(0,1);
					String secPart = firstName.substring(1).toLowerCase();
					firstName = firstPart + secPart; // First letter of first name is uppercase and rest of it are lower case
					guestNameTF.setText(firstName +" "+lastName);
					guestAgeTF.requestFocus();
				}
				catch(Exception e)
				{
					guestNameTF.setText("");
					JOptionPane.showMessageDialog(null, "Could not read card \n Please enter geust name manually", "Error" , JOptionPane.ERROR_MESSAGE);
					guestNameTF.requestFocus();
				}
			}
		});
	}

	//BY BRANDON BALLARD, adds fields using a group layout
    JPanel setFields()
    {
		GroupLayout layout;
		JPanel p;

		studentRoomLabel = new JLabel("Room Number:");
		studentRoomLabel.setForeground(Color.WHITE);
		studentNameLabel = new JLabel("Student Name:");
		studentNameLabel.setForeground(Color.WHITE);
		guestNameLabel = new JLabel("Guest Name:");
		guestNameLabel.setForeground(Color.WHITE);
		guestAgeLabel = new JLabel("Guest Age:");
		guestAgeLabel.setForeground(Color.WHITE);
		overnightLabel = new JLabel("Overnight:");
		overnightLabel.setForeground(Color.WHITE);
		guestIDLabel = new JLabel("ID Type:");
		guestIDLabel.setForeground(Color.WHITE);

	 	studentRoomTF = new JTextField(30);
	 	studentRoomTF.addKeyListener(this);
		guestNameTF = new JTextField();
		guestNameTF.addKeyListener(this);
		guestAgeTF = new JTextField();
		guestAgeTF.addKeyListener(this);

		residentNameCBox = new JComboBox();
		residentNameCBox.addItem("-Select-");
		residentNameCBox.addActionListener(this);

		String[] comboTypes = { "-Select-","FSU", "PCTC", "DL","Other" };
		guestIDTypes = new JComboBox<>(comboTypes);
		guestIDTypes.addActionListener(this);

		overnightCB = new JCheckBox();
		overnightCB.setBackground(new Color(1, 0.1f, 0.1f).darker().darker());

		p = new JPanel();
		p.setBackground(new Color(1, 0.1f, 0.1f).darker().darker());

		layout = new GroupLayout(p);
		p.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup().addComponent(studentNameLabel).addComponent(studentRoomLabel).addComponent(guestNameLabel).addComponent(guestAgeLabel).addComponent(guestIDLabel).addComponent(overnightLabel));
		hGroup.addGroup(layout.createParallelGroup().addComponent(residentNameCBox).addComponent(studentRoomTF).addComponent(guestNameTF).addComponent(guestAgeTF).addComponent(guestIDTypes).addComponent(overnightCB));
		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(studentRoomLabel).addComponent(studentRoomTF));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(studentNameLabel).addComponent(residentNameCBox));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(guestNameLabel).addComponent(guestNameTF));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(guestAgeLabel).addComponent(guestAgeTF));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(guestIDLabel).addComponent(guestIDTypes));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(overnightLabel).addComponent(overnightCB));
		layout.setVerticalGroup(vGroup);

		return(p);
	}

    void setupMainFrame()
	{
		Dimension   d;
		Toolkit     tk;
		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();
		setTitle("Add Guest");
		setSize(d.width/3, d.height/3);
		setSize(d.width/3, 270);
		setLocation(d.width/3, d.height/4 + d.height/20);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
    }
}
