/* * * * * * * * * * *\
 * DefaultGUI.java
 * Description: This is the GUI for resident assistants and dorm monitors, there are three main features on this side of the
 *				system including checking in/checking out guests, adding lock outs, and viewing history. Lockout and history
 *				features are password protected. History can be viewed by resident assistants as far as a week in the past and
 *				dorm monitors can view history for the current day.
 *
 * Date: 4/4/16
 * @author Brandon Ballard & Hanif Mirza
\* * * * * * * * * * */

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.Date;
import static javax.swing.GroupLayout.Alignment.*;
import java.util.*;
import javax.swing.table.*;
import java.sql.*;
import java.text.SimpleDateFormat;

class DefaultGUI extends JFrame implements ActionListener, ListSelectionListener, MouseListener, DocumentListener
{
	JButton 					addGuestButton, checkOutButton, lockOutButton, historyButton, logOutButton, editButton;
	public JLabel 				seperate, seperate1, seperate2, seperate3, titleLabel;
	JPanel 						buttonPanel, statusPanel, sidePanel, southPanel;
	Container 					cp;
	GroupLayout 				layout;
    GroupLayout.SequentialGroup hGroup, vGroup;
    JTable 						table;
    DefaultTableModel           tableModel;
  	JScrollPane 				scrollPane, historyScroller;
  	DefaultTableCellRenderer 	centerRenderer;
  	JPopupMenu 					popup;
  	JMenuItem 					editItem, checkOutItem;
  	SimpleDateFormat 			df;
	Statement 					statement;
	String						userID, password, myFullName, accountType;
	Hashtable<String,Resident> 	residentHT = new Hashtable<String, Resident>();
	MyTable 					historyTable;
	MyClock 					clock;
	JLabel 						searchLabel, filterLabel;
	JTextField 					filterTextField;

	//BY BRANDON BALLARD, sets up and adds components
    DefaultGUI(Statement statement, String userID,String password,String accountType)
    {
		this.statement = statement;
		this.userID = userID;
		this.password = password;
		this.accountType = accountType;

		df = new SimpleDateFormat("M/d/yyyy");

		titleLabel = new JLabel("Welcome to Bryant Place - " + df.format(new Date()));
		titleLabel.setForeground(Color.WHITE);

		statusPanel = new JPanel(new FlowLayout());
		statusPanel.setBackground(new Color(1, 0.1f, 0.1f).darker().darker());
		statusPanel.add(titleLabel);

		sidePanel = new JPanel(new FlowLayout());
		sidePanel.setBackground(new Color(1, 0.1f, 0.1f).darker().darker());

		searchLabel = new JLabel("Search:");
		searchLabel.setVisible(false);
		searchLabel.setForeground(Color.WHITE);

		filterTextField = new JTextField(20);
		filterTextField.getDocument().addDocumentListener(this);
		filterTextField.setVisible(false);

		southPanel = new JPanel(new FlowLayout());
		southPanel.setBackground(new Color(1, 0.1f, 0.1f).darker().darker());
		southPanel.add(searchLabel);
		southPanel.add(filterTextField);

		addGuestButton = new JButton("New guest");
		addGuestButton.setBackground(Color.WHITE);
		addGuestButton.addActionListener(this);
		addGuestButton.setActionCommand("NEW_GUEST");
		addGuestButton.setMinimumSize(new Dimension(100,25));
		getRootPane().setDefaultButton(addGuestButton);

		checkOutButton = new JButton("Check out");
		checkOutButton.setBackground(Color.WHITE);
		checkOutButton.addActionListener(this);
		checkOutButton.setActionCommand("CHECK_OUT");
		checkOutButton.setEnabled(false);
		checkOutButton.setMinimumSize(new Dimension(100,25));

		lockOutButton = new JButton("Lock out");
		lockOutButton.setBackground(Color.WHITE);
		lockOutButton.addActionListener(this);
		lockOutButton.setActionCommand("LOCK_OUT");
		lockOutButton.setMinimumSize(new Dimension(100,25));

		historyButton = new JButton("History");
		historyButton.setBackground(Color.WHITE);
		historyButton.addActionListener(this);
		historyButton.setActionCommand("HISTORY");
		historyButton.setMinimumSize(new Dimension(100,25));

		logOutButton = new JButton("Log out");
		logOutButton.setBackground(Color.WHITE);
		logOutButton.addActionListener(this);
		logOutButton.setActionCommand("LOG_OFF");
		logOutButton.setMinimumSize(new Dimension(100,25));

		editButton = new JButton("Edit guest");
		editButton.setBackground(Color.WHITE);
		editButton.addActionListener(this);
		editButton.setActionCommand("EDIT");
		editButton.setEnabled(false);
		editButton.setMinimumSize(new Dimension(100,25));

		buttonPanel = new JPanel();
		buttonPanel.setBackground(new Color(1, 0.1f, 0.1f).darker().darker());
		layout = new GroupLayout(buttonPanel);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		buttonPanel.setLayout(layout);

		seperate = new JLabel("______________");
		seperate.setForeground(Color.WHITE);
		seperate1 = new JLabel("______________");
		seperate1.setForeground(Color.WHITE);
		seperate2 = new JLabel("______________");
		seperate2.setForeground(Color.WHITE);
		seperate3 = new JLabel("______________");
		seperate3.setForeground(Color.WHITE);

		clock = new MyClock();

		hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup()
		.addComponent(clock)
		.addComponent(seperate)
		.addComponent(addGuestButton)
		.addComponent(editButton)
		.addComponent(checkOutButton)
		.addComponent(seperate1)
		.addComponent(lockOutButton)
		.addComponent(seperate2)
		.addComponent(historyButton)
		.addComponent(seperate3)
		.addComponent(logOutButton));
		layout.setHorizontalGroup(hGroup);

		vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(clock));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(seperate));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(addGuestButton));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(editButton));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(checkOutButton));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(seperate1));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(lockOutButton));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(seperate2));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(historyButton));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(seperate3));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(logOutButton));

        layout.setVerticalGroup(vGroup);
        cp = getContentPane();

        editItem = new JMenuItem("Edit");
        editItem.addActionListener(this);
        editItem.setActionCommand("EDIT");

        checkOutItem = new JMenuItem("Check out");
        checkOutItem.addActionListener(this);
        checkOutItem.setActionCommand("CHECK_OUT");

        popup = new JPopupMenu();
        popup.add(editItem);
        popup.add(checkOutItem);

        add(statusPanel, BorderLayout.NORTH);
        add(sidePanel, BorderLayout.WEST);
        add(southPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.EAST);

        addGuestButton.requestFocus();

		try
		{
			constructJTable();
			populateResidentHashtable();
			setupMainFrame();
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

	/* Written by Hanif Mirza. This function will populate the resident hashtable. In the hashtable, room number is the key
	   and Resident object is the value.*/
    void populateResidentHashtable() throws Exception
    {
		ResultSet myResultSet1, myResultSet2;
		String roomNo, residentID, residentName, SQL_Query1, SQL_Query2;

		SQL_Query1 = " Select r.room_number,r.userID,CONCAT(r.first_name,\" \",r.last_name) as 'fullname'"+ " From Resident r";
		myResultSet1 = statement.executeQuery(SQL_Query1);	// get all residents information

		while ( myResultSet1.next() )
		{
			roomNo = ""+myResultSet1.getObject(1);
			residentID = ""+myResultSet1.getObject(2);
			residentName = ""+myResultSet1.getObject(3);

			if ( residentHT.containsKey(roomNo) )
			{
				// if the key (room number) already exists then get the resident object from using the key
				Resident myResident = residentHT.get(roomNo);

				// map residentID and residentName to the residentHashtable of Resident object (in the same room number)
				myResident.residentHashtable.put(residentID,residentName);
			}
			else
			{
				// the key (room number) doesn't exist, so create new Resident object and map it with room number
				Resident myResident = new Resident();
				myResident.residentHashtable.put(residentID,residentName);
				residentHT.put(roomNo, myResident );
			}
		}
		myResultSet1.close();

		SQL_Query2 = " Select CONCAT(e.first_name,\" \",e.last_name) as 'Fullname'"+ " From Employee e"+ " Where e.userID = " + "'"+userID+"'" ;

		myResultSet2 = statement.executeQuery(SQL_Query2);	// get RA or DM's full name
		myResultSet2.first();
		myFullName = myResultSet2.getString(1);
		myResultSet2.close();
	}

	// Written by Hanif Mirza. When the RA or DM logged in, this function will create a table with all the
	// visitation details that aren't checked out yet and show that table on the frame.

	void constructJTable()throws Exception
	{
		ResultSet myResultSet;
		ResultSetMetaData metaData;
		int numberOfColumns;
		Vector<Object> columnNames, rows, currentRow;

		String SQL_Query  =     " Select v.visitationID as 'Serial No',v.guest_name as 'Guest',v.guest_age as 'Guest Age',v.guest_ID_type as 'ID Type',CONCAT(r.first_name,\" \",r.last_name) as 'Resident',"
								+ " r.room_number as 'Room Number', DATE_FORMAT(v.visitation_date,'%c/%e/%Y') as 'Date', TIME_FORMAT(v.time_in, '%l:%i%p')as 'Time in',"
								+ " v.overnight_status as 'Overnight', CONCAT(e.first_name,\" \",e.last_name) as 'DM/RA Name' "

								+ " From Visitation_Detail v, Resident r,Employee e"
								+ " Where v.time_out is null and v.residentID = r.userID and v.empID = e.userID ;" ;

		myResultSet = statement.executeQuery(SQL_Query);
		table = new MyTable( myResultSet );	// Create a table from ResultSet
		tableModel = (DefaultTableModel)table.getModel();
		table.addMouseListener(this);
		table.getSelectionModel().addListSelectionListener(this);
		scrollPane = new  JScrollPane(table);

		centerRenderer = new DefaultTableCellRenderer();
		{
			centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		}

		table.getColumnModel().getColumn(0).setMinWidth(0);
		table.getColumnModel().getColumn(0).setMaxWidth(0);
		table.getColumnModel().getColumn(0).setWidth(0);

		table.getColumnModel().getColumn(2).setCellRenderer( centerRenderer );
		table.getColumnModel().getColumn(5).setCellRenderer( centerRenderer );
		table.getColumnModel().getColumn(6).setCellRenderer( centerRenderer );
		table.getColumnModel().getColumn(7).setCellRenderer( centerRenderer );
		table.getColumnModel().getColumn(8).setCellRenderer( centerRenderer );
		table.getColumnModel().getColumn(1).setMinWidth(100);
		table.getColumnModel().getColumn(4).setMinWidth(100);
		table.getColumnModel().getColumn(5).setMinWidth(90);
		add(scrollPane, BorderLayout.CENTER);
		repaint();
	}

	//BY BRANDON BALLARD, handles action events
    public void actionPerformed(ActionEvent e)
    {
        if(e.getActionCommand().equals("NEW_GUEST"))
        {
			new AddGuestDialog(this,residentHT,statement);
        }
        else if(e.getActionCommand().equals("CHECK_OUT"))
        {
			doCheckOut();
		}
		else if(e.getActionCommand().equals("LOCK_OUT"))
		{
			if (new PasswordDialog(password).isValid)
			{
				new LockOutDialog(this,residentHT,statement);
			}
		}
		else if(e.getActionCommand().equals("HISTORY"))
		{
			doHistory();
		}
		else if(e.getActionCommand().equals("LOG_OFF"))
		{
			doLogout();
		}
		else if(e.getActionCommand().equals("EDIT"))
		{
			int[]  selectionList = table.getSelectedRows();
			int row = table.convertRowIndexToModel(selectionList[0]);
			String visitationID = tableModel.getValueAt( row,0 ).toString();
			AddGuestDialog addGuestDialog = new AddGuestDialog(this,residentHT,statement,visitationID, row);
		}
    }

	// Written by Hanif Mirza. This function will check out a guest when DM clicks the Check Out button
    void doCheckOut()
    {
		try
		{
			int[] selectionList = table.getSelectedRows();
			int row = table.convertRowIndexToModel(selectionList[0]);
			String visitationID = tableModel.getValueAt( row,0 ).toString(); // get the visitationID, which is the key

			String updateKeyQuery = "UPDATE Visitation_Detail"
								 + " SET time_out = curtime()"
								 + " WHERE visitationID = "+ visitationID;

			statement.executeUpdate(updateKeyQuery);
			tableModel.removeRow( row );

			JOptionPane.showMessageDialog(this, "Guest has been checked out", "Check out successful" , JOptionPane.INFORMATION_MESSAGE);
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

	void doHistory()
	{
		if(historyButton.getText().equals("Go back"))
		{
			remove(historyScroller);
			searchLabel.setVisible(false);
			historyTable = null;
			historyButton.setText("History");
			add(scrollPane, BorderLayout.CENTER);
			filterTextField.setVisible(false);
			titleLabel.setText("Welcome to Bryant Place - " + df.format(new Date()));

			addGuestButton.setVisible(true);
			editButton.setVisible(true);
			checkOutButton.setVisible(true);
			lockOutButton.setVisible(true);

			seperate1.setVisible(true);
			seperate2.setVisible(true);
		}
		else if(historyButton.getText().equals("History"))
		{
			if (new PasswordDialog(password).isValid)
			{
				viewHistory();
			}
		}
	}

	// Written by Hanif Mirza. This function will view the visitation history
	void viewHistory()
	{
		try
		{
			String SQL_Query = null;

			if ( accountType.equals("DM") )
			{
				// DM can see the history of last 24 hours
				SQL_Query =   " Select v.visitationID as 'Serial No',v.guest_name as 'Guest Name',v.guest_ID_type as 'ID Type',CONCAT(r.first_name,\" \",r.last_name) as 'Resident Name',"
							+ " r.room_number as 'Room Number', DATE_FORMAT(v.visitation_date,'%c/%e/%Y') as 'Date',TIME_FORMAT(v.time_in, '%l:%i%p')as 'Time in',TIME_FORMAT(v.time_out, '%l:%i%p')as 'Time out',"
							+ " v.overnight_status as 'Overnight', CONCAT(e.first_name,\" \",e.last_name) as 'DM/RA Name' "

							+ " From Visitation_Detail v, Resident r,Employee e"
							+ " Where v.visitation_date = curdate() and v.time_out is not null and v.residentID = r.userID and v.empID = e.userID ;" ;
			}
			else if ( accountType.equals("RA") )
			{
				// RA can see the history of last week
				SQL_Query =   " Select v.visitationID as 'Serial No',v.guest_name as 'Guest Name',v.guest_ID_type as 'ID Type',CONCAT(r.first_name,\" \",r.last_name) as 'Resident Name',"
							+ " r.room_number as 'Room Number', DATE_FORMAT(v.visitation_date,'%c/%e/%Y') as 'Date',TIME_FORMAT(v.time_in, '%l:%i%p')as 'Time in',TIME_FORMAT(v.time_out, '%l:%i%p')as 'Time out',"
							+ " v.overnight_status as 'Overnight', CONCAT(e.first_name,\" \",e.last_name) as 'DM/RA Name' "

							+ " From Visitation_Detail v, Resident r,Employee e"
							+ " Where ( v.visitation_date BETWEEN CURDATE()-INTERVAL 1 WEEK AND CURDATE() ) and v.time_out is not null and v.residentID = r.userID and v.empID = e.userID ;" ;
			}

			remove(scrollPane);
			historyTable = new MyTable(statement.executeQuery(SQL_Query)); // construct the historyTable using the result set
			historyTable.getColumnModel().getColumn(0).setMinWidth(0); //Hide the first column
			historyTable.getColumnModel().getColumn(0).setMaxWidth(0);
			historyTable.getColumnModel().getColumn(0).setWidth(0);

			searchLabel.setVisible(true);
			titleLabel.setText("Showing history as of " + df.format(new Date()));

			historyScroller = new  JScrollPane(historyTable);

			add(historyScroller, BorderLayout.CENTER);
			historyButton.setText("Go back");
			filterTextField.setVisible(true);

			addGuestButton.setVisible(false);
			editButton.setVisible(false);
			checkOutButton.setVisible(false);
			lockOutButton.setVisible(false);

			seperate1.setVisible(false);
			seperate2.setVisible(false);
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

	// Written by Hanif Mirza. This function will perform the log out. It will store their log out time to the database
	void doLogout()
	{
		try
		{
			String updateLogout  = "UPDATE Log_Detail"
								 + " SET logout_time = curtime()"
								 + " WHERE empID = "+ "'"+userID+"'" + " and log_date = curdate() and logout_time is null";
			statement.executeUpdate(updateLogout);
			this.dispose();
			new LoginDialog();
		}
		catch ( Exception exception )
		{
			JOptionPane.showMessageDialog(this, exception.getMessage());
		}
	}

	public void valueChanged(ListSelectionEvent lse)
	{
		editButton.setEnabled(false);
		checkOutButton.setEnabled(false);

		if(table.getSelectedRows().length == 1)
		{
			editButton.setEnabled(true);
			checkOutButton.setEnabled(true);
		}
	}

    public void mouseExited(MouseEvent me){}

	public void mouseEntered(MouseEvent me){}

	public void mouseReleased(MouseEvent me){}

	public void mousePressed(MouseEvent me){}

	public void mouseClicked(MouseEvent me)
	{
		if(SwingUtilities.isRightMouseButton(me))
		{
			int r = table.rowAtPoint(me.getPoint());

			if (r >= 0 && r < table.getRowCount())
			{
				table.setRowSelectionInterval(r, r);
			}
			else
			{
				table.clearSelection();
			}

			popup.show(me.getComponent(),me.getX() ,me.getY());
		}
     	else if (me.getClickCount() == 2)
		{
			int[]  selectionList = table.getSelectedRows();
			int row = table.convertRowIndexToModel(selectionList[0]);
			String visitationID = tableModel.getValueAt( row,0 ).toString();
			AddGuestDialog	addGuestDialog = new AddGuestDialog(this,residentHT,statement,visitationID, row);
		}
	}

	public void insertUpdate(DocumentEvent e)
	{
		try
		{
			String text = filterTextField.getText();
			if (text.trim().length() == 0)
			{
				historyTable.rowSorter.setRowFilter(null);
			}
			else
			{
				historyTable.rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); // sort the historyTable using key word from filterTextField
			}
		}
		catch ( Exception ee )
		{
			System.out.println( "Exception "+ee.getMessage() );
		}
	}

	public void removeUpdate(DocumentEvent e)
	{
		try
		{
			String text = filterTextField.getText();
			if (text.trim().length() == 0)
			{
				historyTable.rowSorter.setRowFilter(null);
			}
			else
			{
				historyTable.rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); // sort the historyTable using key word from filterTextField
			}
		}
		catch ( Exception ee )
		{
			System.out.println( "Exception "+ee.getMessage() );
		}
	}
	public void changedUpdate(DocumentEvent e){}

	void setupMainFrame()
	{
		Toolkit    tk;
		Dimension   d;
		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();
		setSize(d.width/2 + d.width/4, d.height/2 + d.width / 6);
		setLocation((d.width/4 + d.width/4)/4, (d.height/2 + d.width / 6) / 8);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Guest Visitation");
		setVisible(true);
	}
}