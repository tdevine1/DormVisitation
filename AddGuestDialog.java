/**
 * Add Guest Dialog
 * Description: Adds a new guest to table in main frame or edits an existing one
 * Date: 03/11/16
 * @author Brandon Ballard & Hanif Mirza
 *
 */

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

class AddGuestDialog extends JDialog implements ActionListener,DocumentListener
{
    public static void main(String[] x)
    {
		try
		{
			Class.forName( "com.mysql.jdbc.Driver" );
			Connection connection = DriverManager.getConnection( "jdbc:mysql://johnny.heliohost.org/falcon16_dorm", "falcon16", "fsu2016" );
			Statement statement = connection.createStatement();
			String	sql = "";

			statement.close();
			connection.close();
		}
		catch ( Exception exception )
		{
			System.out.println(exception.getMessage());
		}
    }

	JPanel 						buttonPanel, fieldPanel;
	JButton 					cancelButton, addButton;
	JTextField 					studentRoomTF, guestNameTF, guestAgeTF;
	JLabel 						studentRoomLabel, studentNameLabel, guestNameLabel, overnightLabel, guestAgeLabel, titleLabel, guestIDLabel;
	JCheckBox 					overnightCB;
	JScrollPane 				scrollPane;
	JComboBox 					residentNameCBox,guestIDTypes;
	DefaultGUI					myDefaultGUI;
	Statement 					myStatement;
	String						residentRoomNo, residentName, residentID, guestName, guestIDtype, guestAge, overnightStatus, currTime, currDate, myVisitationID;
	DateFormat 					dateFormat, timeFormat;
	Hashtable<String,Resident> 	residentHT;

	public AddGuestDialog(DefaultGUI urDefaultGUI,Hashtable<String,Resident> urHashtable,Statement urStatement) //FOR ADDING A GUEST
	{
		this.myStatement = urStatement;
		this.residentHT = urHashtable;
		this.myDefaultGUI = urDefaultGUI;

		dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		timeFormat = new SimpleDateFormat("hh:mma");

		addButton = new JButton("Add");
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

		fieldPanel = setFields();
	 	studentRoomTF.getDocument().addDocumentListener(this);
		guestNameTF.getDocument().addDocumentListener(this);
		guestAgeTF.getDocument().addDocumentListener(this);

		getContentPane().add(fieldPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setupMainFrame();
	}

	public AddGuestDialog(DefaultGUI urDefaultGUI,Hashtable<String,Resident> urHashtable,Statement urStatement,String urVisitationID) //FOR EDITING
	{
		this.myStatement = urStatement;
		this.residentHT = urHashtable;
		this.myDefaultGUI = urDefaultGUI;
		this.myVisitationID = urVisitationID;

		dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		timeFormat = new SimpleDateFormat("hh:mma");

		addButton = new JButton("Update");
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

		fieldPanel = setFields();
		populateAllFields();
	 	studentRoomTF.getDocument().addDocumentListener(this);
		guestNameTF.getDocument().addDocumentListener(this);
		guestAgeTF.getDocument().addDocumentListener(this);

		getContentPane().add(fieldPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setupMainFrame();
		setTitle("Edit Guest");
	}

	void populateAllFields()
	{
		int[]  selectionList = myDefaultGUI.table.getSelectedRows(); // get the selected row
		studentRoomTF.setText( myDefaultGUI.tableModel.getValueAt(selectionList[0],5).toString() );
		guestNameTF.setText( myDefaultGUI.tableModel.getValueAt(selectionList[0],1).toString() );
		guestAgeTF.setText( myDefaultGUI.tableModel.getValueAt(selectionList[0],2).toString() );
		guestIDTypes.setSelectedItem( myDefaultGUI.tableModel.getValueAt(selectionList[0],3).toString() );
		String overnightStatus = myDefaultGUI.tableModel.getValueAt(selectionList[0],8).toString();

	    residentNameCBox.removeAllItems(); // remove all previous items from the combo box
	    residentNameCBox.addItem( myDefaultGUI.tableModel.getValueAt(selectionList[0],4).toString() );

		if ( overnightStatus.equals("No") )
		{
			overnightCB.setSelected(false);
		}
		else
		{
			overnightCB.setSelected(true);
		}
	}

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

			int[]  selectionList = myDefaultGUI.table.getSelectedRows(); // get the selected row
		    myDefaultGUI.tableModel.setValueAt(guestName,selectionList[0],1);
			myDefaultGUI.tableModel.setValueAt(guestAge,selectionList[0],2);
			myDefaultGUI.tableModel.setValueAt(guestIDtype,selectionList[0],3);
			myDefaultGUI.tableModel.setValueAt(residentName,selectionList[0],4);
			myDefaultGUI.tableModel.setValueAt(residentRoomNo,selectionList[0],5);
			myDefaultGUI.tableModel.setValueAt(overnightStatus,selectionList[0],8);
			myDefaultGUI.tableModel.setValueAt(myDefaultGUI.myFullName,selectionList[0],9);

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
			if (sqlException.getMessage().startsWith("Communications") )
			{
				JOptionPane.showMessageDialog(this, "No internet connection, please try again later");
			}
			else
			{
				JOptionPane.showMessageDialog(this, sqlException.getMessage());
			}
		}
		catch ( Exception exception )
		{
			JOptionPane.showMessageDialog(this, exception.getMessage());
		}
	}

    void doAddGuest()
    {
		try
		{
			validateFields();
			Resident myResident = residentHT.get(residentRoomNo); // get the Resident object from resident's room number
			String residentID = findKey(residentName,myResident.residentHashtable);//get  resident ID (the key) from resident name (the value)

			// database statement to insert the check-out details
			String	sql = "INSERT INTO Visitation_Detail(guest_name,guest_age,guest_ID_type,visitation_date,time_in,overnight_status,empID,residentID)"
					   + " VALUES ("+ "'"+guestName+"'" +","+ guestAge +","+ "'"+guestIDtype+"'" +","+ "CURDATE(),curtime(),"+ "'"+overnightStatus+"'" +","
					   + "'"+myDefaultGUI.userID+"'" +","+  "'"+residentID+"'" +")";

			int confirmationNo = myStatement.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);

			ResultSet rs = myStatement.getGeneratedKeys(); // the the Auto Generated Primary Key, which is visitation details ID

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

		}//end try
		catch(MyException myEx)
		{
			JOptionPane.showMessageDialog(this,myEx.getMessage());
		}
		catch ( SQLException sqlException )
		{
			if (sqlException.getMessage().startsWith("Communications") )
			{
				JOptionPane.showMessageDialog(this, "No internet connection! Please try again later!");
			}
			else
			{
				JOptionPane.showMessageDialog(this, sqlException.getMessage());
			}
		}
		catch ( Exception exception )
		{
			JOptionPane.showMessageDialog(this, exception.getMessage());
		}
	}

	// this function will validate all the fields, if any field is invalid then it will through exception
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
			throw new MyException("Please enter first and last name of guest");
		}
		else if(checkIfValuesExist("Banned_Guest","guest_name",guestName))
		{
			throw new MyException(guestName+" has been banned from the resident hall, please call campus police and notify RA");
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

	// this function will find resident ID (the key) using the resident full name (the value) from the hashtable
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

    //this function will return true if values exist in specific column of a database table
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

		// check if the source text field is studentRoomTF
		if (de.getDocument() == studentRoomTF.getDocument() )
		{
			residentNameCBox.removeAllItems(); // remove all previous items from the combo box
			DefaultComboBoxModel model = new DefaultComboBoxModel();
			model.addElement( "-Select-" );
			if ( residentHT.containsKey(residentRoomNo) )
			{
				Resident myResident = residentHT.get(residentRoomNo);
				Set<String> keys = myResident.residentHashtable.keySet();
				for(String key: keys)
				{
					String residentName = myResident.residentHashtable.get(key);//get the resident full name from the resident ID (key)
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

		// check if the source text field is studentRoomTF
		if (de.getDocument() == studentRoomTF.getDocument() )
		{
			residentNameCBox.removeAllItems(); // remove all previous items from the combo box
			DefaultComboBoxModel model = new DefaultComboBoxModel();
			model.addElement( "-Select-" );
			if ( residentHT.containsKey(residentRoomNo) )
			{
				Resident myResident = residentHT.get(residentRoomNo);
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
		guestNameTF = new JTextField();
		guestAgeTF = new JTextField();


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
		setLocation(d.width/3, d.height/4);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
    }
}
