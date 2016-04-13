/* * * * * * * * * * *\
 * AdminGUI
 * Description: The administrators GUI for managing accounts, viewing history, and printing reports
 * Date: 4/11/16
 * @author Brandon Ballard & Hanif Mirza
\* * * * * * * * * * */

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import static javax.swing.GroupLayout.Alignment.*;
import javax.swing.table.*;
import java.sql.*;

class AdminGUI extends JFrame implements ActionListener, ListSelectionListener, MouseListener, DocumentListener
{
	public static void main(String[] x)
	{
		try
		{
			Class.forName( "com.mysql.jdbc.Driver" );
			Connection connection = DriverManager.getConnection( "jdbc:mysql://localhost/dorm", "root", "password");
			Statement statement = connection.createStatement();
			new AdminGUI(statement,"admin","password");
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			System.out.println( "Exception " + e.getMessage());
		}
	}

	JButton 					printButton, homeButton, viewHistoryButton, manageAccountsButton, newAccountButton, deleteAccountButton, editAccountButton, logOutButton;
	JLabel 						searchLabel, seperate, seperate3, seperate4, seperate5, seperate6, accountTypeLabel;
	JPanel 						statsPanel, buttonPanel, northPanel, menuButtonPanel, southPanel;
	GroupLayout 				layout, layout2;
    GroupLayout.SequentialGroup hGroup, vGroup, hGroup2, vGroup2;
    DefaultTableModel          	tableModel;
  	JScrollPane 				scrollPane;
  	JComboBox 					accountCBox, reportCBox;
  	JPopupMenu 					popup;
  	JMenuItem 					editItem, checkOutItem;
	Statement 					statement;
	String						userID, password, accountType, SQL_Query;
	MyTable 					table;
	MyClock 					clock;
	JTextField 					searchTF, searchReportTF;
	ReportsPanel 				reportsPanel;

    AdminGUI(Statement statement, String userID,String password)
    {
		this.statement = statement;
		this.userID = userID;
		this.password = password;

		//____________________________________________________________________________NORTH PANEL

		accountTypeLabel = new JLabel("Select an account type:");
		accountTypeLabel.setForeground(Color.WHITE);

		accountCBox = new JComboBox();
		accountCBox.addItem("-Select-");
		accountCBox.addItem("Resident");;
		accountCBox.addItem("Banned Guest");
		accountCBox.addItem("RA");
		accountCBox.addItem("DM");
		accountCBox.addActionListener(this);

		reportCBox = new JComboBox();
		reportCBox.addItem("-Select-");
		reportCBox.addItem("History Report");
		reportCBox.setVisible(false);
		reportCBox.addActionListener(this);

		northPanel = new JPanel(new FlowLayout());
		northPanel.setBackground(new Color(1, 0.1f, 0.1f).darker().darker());
		northPanel.add(accountTypeLabel);
		northPanel.add(accountCBox);
		northPanel.add(reportCBox);

		//______________________________________________________________________________SOUTH PANEL

		searchLabel = new JLabel("Search:");
		searchLabel.setVisible(false);
		searchLabel.setForeground(Color.WHITE);

		searchTF = new JTextField(20);
		searchTF.getDocument().addDocumentListener(this);
		searchTF.setVisible(false);

		searchReportTF = new JTextField(20);
		searchReportTF.setVisible(false);

		southPanel = new JPanel(new FlowLayout());
		southPanel.setBackground(new Color(1, 0.1f, 0.1f).darker().darker());
		southPanel.add(searchLabel);
		southPanel.add(searchTF);
		southPanel.add(searchReportTF);

		//______________________________________________________________________________//POP UP (RIGHT CLICK ON A TABLE)

        editItem = new JMenuItem("Edit");
        editItem.addActionListener(this);
        editItem.setActionCommand("EDIT");

        checkOutItem = new JMenuItem("Delete");
        checkOutItem.addActionListener(this);
        checkOutItem.setActionCommand("DELETE");

        popup = new JPopupMenu();
        popup.add(editItem);
        popup.add(checkOutItem);

        //______________________________________________________________________________SET UP BUTTONS

        createMenuButtonPanel();
		createAccountMangerButtonPanel();

		//______________________________________________________________________________SET UP RAMAINING PANELS AND PANES

		scrollPane = new JScrollPane();

		statsPanel = new MyStatsPanel();
		reportsPanel = new ReportsPanel(statement, reportCBox, searchReportTF, printButton);

		//______________________________________________________________________________ADD COMPONENTS AND SET UP MAIN FRAME

		add(buttonPanel, BorderLayout.EAST);
        add(northPanel, BorderLayout.NORTH);
       	add(menuButtonPanel, BorderLayout.WEST);
        add(southPanel, BorderLayout.SOUTH);
        add(statsPanel, BorderLayout.CENTER);

        setupMainFrame();

        setMenuButtonsVisible(true);
		setAccountManagerButtonsVisible(false);
    }

    public void actionPerformed(ActionEvent e)
    {
		//________________________________________________________________ACTION COMMANDS FOR MAIN MENU BUTTONS
		if(e.getActionCommand().equals("ACCOUNTS"))
		{
			setTitle("Welcome to Bryant Place - Account Manager");
			remove(statsPanel);
			add(scrollPane, BorderLayout.CENTER);
			accountTypeLabel.setText("Select account type:");
			setMenuButtonsVisible(false);
			setAccountManagerButtonsVisible(true);
		}
		else if(e.getActionCommand().equals("HISTORY"))
		{
			reportsPanel = new ReportsPanel(statement, reportCBox, searchReportTF, printButton);
			setTitle("Welcome to Bryant Place - Reports");
			remove(statsPanel);
			setMenuButtonsVisible(false);
			setAccountManagerButtonsVisible(true);
			accountCBox.setVisible(false);
			reportCBox.setVisible(true);
			accountTypeLabel.setText("Select report type:");
			add(reportsPanel, BorderLayout.CENTER);
		}
		else if(e.getActionCommand().equals("LOG_OFF"))
		{
			doLogout();
		}

		//________________________________________________________________ACTION COMMANDS FOR ACCOUNT MANAGEMENT BUTTONS
        if(e.getActionCommand().equals("NEW"))
        {
			doAdd();
        }
		else if(e.getActionCommand().equals("EDIT"))
		{
			doEdit();
		}
		else if(e.getActionCommand().equals("DELETE"))
		{
			doDelete();
		}

		//________________________________________________________________ACTION COMMANDS FOR RETURNING TO MAIN MENU
		if(e.getActionCommand().equals("HOME"))
		{
			setTitle("Welcome to Bryant Place - Main Menu");
			remove(scrollPane);
			remove(reportsPanel);
			add(statsPanel, BorderLayout.CENTER);
			accountCBox.setSelectedIndex(0);
			setMenuButtonsVisible(true);
			reportCBox.setVisible(false);
			reportCBox.setSelectedIndex(0);
			setAccountManagerButtonsVisible(false);
		}

		//________________________________________________________________ACTION COMMANDS FOR SWITCHING BETWEEN ACCOUNT
		//                                                                TYPES WHEN ACCOUNT MANAGER IS OPEN
		if(accountCBox.getSelectedIndex() == 0)
		{
			scrollPane.setVisible(false);
			searchLabel.setVisible(false);
			searchTF.setVisible(false);
			newAccountButton.setVisible(false);
			deleteAccountButton.setVisible(false);
			editAccountButton.setVisible(false);
		}
		else if(accountCBox.getSelectedIndex() == 1)
		{
			accountType = "Resident";
			showAccountListTable();
			searchLabel.setVisible(true);
			searchTF.setVisible(true);
			newAccountButton.setVisible(true);
			deleteAccountButton.setVisible(true);
			editAccountButton.setVisible(true);
			seperate.setVisible(true);
			searchReportTF.setVisible(false);
			newAccountButton.setText("New Resident");
			deleteAccountButton.setText("Delete Resident");
			editAccountButton.setText("Edit Resident");
		}
		else if(accountCBox.getSelectedIndex() == 2)
		{
			accountType = "BannedGuest";
			showAccountListTable();

			searchLabel.setVisible(true);
			searchTF.setVisible(true);
			newAccountButton.setVisible(true);
			deleteAccountButton.setVisible(true);
			editAccountButton.setVisible(true);
			seperate.setVisible(true);
			searchReportTF.setVisible(false);
			newAccountButton.setText("New Banned Guest");
			deleteAccountButton.setText("Delete Banned Guest");
			editAccountButton.setText("Edit Banned Guest");
		}
		else if(accountCBox.getSelectedIndex() == 3)
		{
			accountType = "RA";
			showAccountListTable();

			searchLabel.setVisible(true);
			searchTF.setVisible(true);
			newAccountButton.setVisible(true);
			deleteAccountButton.setVisible(true);
			editAccountButton.setVisible(true);
			seperate.setVisible(true);
			searchReportTF.setVisible(false);
			newAccountButton.setText("New Employee");
			deleteAccountButton.setText("Delete Employee");
			editAccountButton.setText("Edit Employee");
		}
		else if(accountCBox.getSelectedIndex() == 4)
		{
			accountType = "DM";
			showAccountListTable();

			newAccountButton.setVisible(true);
			deleteAccountButton.setVisible(true);
			editAccountButton.setVisible(true);
			seperate.setVisible(true);
			searchLabel.setVisible(true);
			searchTF.setVisible(true);
			searchReportTF.setVisible(false);
			newAccountButton.setText("New Employee");
			deleteAccountButton.setText("Delete Employee");
			editAccountButton.setText("Edit Employee");
		}

		if(reportCBox.getSelectedIndex() == 1)
		{
			printButton.setVisible(true);
			searchReportTF.setVisible(true);
			searchTF.setVisible(false);
			searchLabel.setVisible(true);
		}
		else if(reportCBox.getSelectedIndex() == 0)
		{
			printButton.setVisible(false);
			searchReportTF.setVisible(false);
		}
    }

    void doAdd()
    {
		if(accountType.equals("BannedGuest"))
		{
			new AddBannedGuestDialog(this,statement);
		}
		else if(accountType.equals("Resident"))
		{
			new AddResidentDialog(this,statement);
		}
		else if(accountType.equals("RA"))
		{
			new AddEmployeeDialog(this,statement);
		}
		else if(accountType.equals("DM"))
		{
			new AddEmployeeDialog(this,statement);
		}
	}

    void doEdit()
    {
		int[]  selectionList = table.getSelectedRows();
		int row = table.convertRowIndexToModel(selectionList[0]);
		String visitationID = tableModel.getValueAt(row,0).toString();

		if(accountType.equals("BannedGuest"))
		{
			new AddBannedGuestDialog(this,statement,visitationID, row);
		}
		else if(accountType.equals("Resident"))
		{
			new AddResidentDialog(this,statement,visitationID, row);
		}
		else if(accountType.equals("RA"))
		{
			new AddEmployeeDialog(this,statement,visitationID, row);
		}
		else if(accountType.equals("DM"))
		{
			new AddEmployeeDialog(this,statement,visitationID, row);
		}
	}

	void doDelete()
    {
		int[]  selectionList = table.getSelectedRows();
		int row = table.convertRowIndexToModel(selectionList[0]);
		String visitationID = tableModel.getValueAt(row,0).toString();
	}

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

	void showAccountListTable()
	{
		if(accountType.equals("BannedGuest"))
		{
			SQL_Query =   " Select v.visitationID as 'Serial No',v.guest_name as 'Guest Name',v.guest_ID_type as 'ID Type',CONCAT(r.first_name,\" \",r.last_name) as 'Resident Name',"
							+ " r.room_number as 'Room Number', DATE_FORMAT(v.visitation_date,'%m/%d/%Y') as 'Date',TIME_FORMAT(v.time_in, '%h:%i%p')as 'Time in',TIME_FORMAT(v.time_out, '%h:%i%p')as 'Time out',"
							+ " v.overnight_status as 'Overnight', CONCAT(e.first_name,\" \",e.last_name) as 'DM/RA Name' "

							+ " From Visitation_Detail v, Resident r,Employee e"
							+ " Where ( v.visitation_date BETWEEN CURDATE()-INTERVAL 1 WEEK AND CURDATE() ) and v.time_out is not null and v.residentID = r.userID and v.empID = e.userID ;" ;
			createTable(SQL_Query);
		}
		else if(accountType.equals("Resident"))
		{
			String SQL_Query  =     " Select v.visitationID as 'Serial No',v.guest_name as 'Guest',v.guest_age as 'Guest Age',v.guest_ID_type as 'ID Type',CONCAT(r.first_name,\" \",r.last_name) as 'Resident',"
							+ " r.room_number as 'Room Number', DATE_FORMAT(v.visitation_date,'%m/%d/%Y') as 'Date', TIME_FORMAT(v.time_in, '%h:%i%p')as 'Time in',"
							+ " v.overnight_status as 'Overnight', CONCAT(e.first_name,\" \",e.last_name) as 'DM/RA Name' "

							+ " From Visitation_Detail v, Resident r,Employee e"
							+ " Where v.time_out is null and v.residentID = r.userID and v.empID = e.userID ;" ;
			createTable(SQL_Query);
		}
		else if(accountType.equals("RA"))
		{
			SQL_Query =   " Select v.visitationID as 'Serial No',v.guest_name as 'Guest Name',v.guest_ID_type as 'ID Type',CONCAT(r.first_name,\" \",r.last_name) as 'Resident Name',"
							+ " r.room_number as 'Room Number', DATE_FORMAT(v.visitation_date,'%m/%d/%Y') as 'Date',TIME_FORMAT(v.time_in, '%h:%i%p')as 'Time in',TIME_FORMAT(v.time_out, '%h:%i%p')as 'Time out',"
							+ " v.overnight_status as 'Overnight', CONCAT(e.first_name,\" \",e.last_name) as 'DM/RA Name' "

							+ " From Visitation_Detail v, Resident r,Employee e"
							+ " Where v.visitation_date = curdate() and v.time_out is not null and v.residentID = r.userID and v.empID = e.userID ;" ;
			createTable(SQL_Query);
		}
		else if(accountType.equals("DM"))
		{
			SQL_Query =   " Select v.visitationID as 'Serial No',v.guest_name as 'Guest Name',v.guest_ID_type as 'ID Type',CONCAT(r.first_name,\" \",r.last_name) as 'Resident Name',"
							+ " r.room_number as 'Room Number', DATE_FORMAT(v.visitation_date,'%m/%d/%Y') as 'Date',TIME_FORMAT(v.time_in, '%h:%i%p')as 'Time in',TIME_FORMAT(v.time_out, '%h:%i%p')as 'Time out',"
							+ " v.overnight_status as 'Overnight', CONCAT(e.first_name,\" \",e.last_name) as 'DM/RA Name' "

							+ " From Visitation_Detail v, Resident r,Employee e"
							+ " Where v.visitation_date = curdate() and v.time_out is not null and v.residentID = r.userID and v.empID = e.userID ;" ;
			createTable(SQL_Query);
		}
	}

	void createTable(String SQL_Query)
	{
		try
		{
			remove(scrollPane);

			table = new MyTable(statement.executeQuery(SQL_Query));
			table.getSelectionModel().addListSelectionListener(this);
			tableModel = (DefaultTableModel)table.getModel();
			table.addMouseListener(this);

			scrollPane = new  JScrollPane(table);

			add(scrollPane, BorderLayout.CENTER);
			searchLabel.setVisible(true);
			searchTF.setVisible(true);
		}
		catch ( SQLException sqlException )
		{
			if (sqlException.getMessage().startsWith("Communications")	)
			{
				JOptionPane.showMessageDialog(this, "No internet connection, please try again later.");
			}
			else
			{
				JOptionPane.showMessageDialog(this, sqlException.getMessage());
				sqlException.printStackTrace();
			}
		}
		catch ( Exception exception )
		{
			JOptionPane.showMessageDialog(this, exception.getMessage());
			exception.printStackTrace();
		}
	}

	public void valueChanged(ListSelectionEvent lse)
	{
		editAccountButton.setEnabled(false);
		deleteAccountButton.setEnabled(false);

		if(table.getSelectedRows().length == 1)
		{
			editAccountButton.setEnabled(true);
			deleteAccountButton.setEnabled(true);
		}
	}

	void createAccountMangerButtonPanel()
	{
		newAccountButton = new JButton("New Account");
		newAccountButton.setBackground(Color.WHITE);
		newAccountButton.addActionListener(this);
		newAccountButton.setActionCommand("NEW");
		newAccountButton.setMinimumSize(new Dimension(120,25));
		getRootPane().setDefaultButton(newAccountButton);

		printButton = new JButton("Print to PDF");
		printButton.setVisible(false);
		printButton.setBackground(Color.WHITE);
		printButton.setMinimumSize(new Dimension(120,25));

		deleteAccountButton = new JButton("Delete Account");
		deleteAccountButton.setBackground(Color.WHITE);
		deleteAccountButton.addActionListener(this);
		deleteAccountButton.setActionCommand("DELETE");
		deleteAccountButton.setEnabled(false);
		deleteAccountButton.setMinimumSize(new Dimension(120,25));

		editAccountButton = new JButton("Edit Account");
		editAccountButton.setBackground(Color.WHITE);
		editAccountButton.addActionListener(this);
		editAccountButton.setActionCommand("EDIT");
		editAccountButton.setEnabled(false);
		editAccountButton.setMinimumSize(new Dimension(120,25));

		homeButton = new JButton("Main Menu");
		homeButton.setBackground(Color.WHITE);
		homeButton.addActionListener(this);
		homeButton.setActionCommand("HOME");
		homeButton.setMinimumSize(new Dimension(120,25));

		buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.setBackground(new Color(1, 0.1f, 0.1f).darker().darker());
		layout = new GroupLayout(buttonPanel);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		buttonPanel.setLayout(layout);

		seperate = new JLabel("_________________");
		seperate.setForeground(Color.WHITE);

		clock = new MyClock();

		hGroup2 = layout.createSequentialGroup();
		hGroup2.addGroup(layout.createParallelGroup()
		.addComponent(clock)
		.addComponent(seperate)
		.addComponent(newAccountButton)
		.addComponent(editAccountButton)
		.addComponent(deleteAccountButton)
		.addComponent(printButton)
		.addComponent(homeButton));
		layout.setHorizontalGroup(hGroup2);

		vGroup2 = layout.createSequentialGroup();
		vGroup2.addGroup(layout.createParallelGroup(BASELINE).addComponent(clock));
		vGroup2.addGroup(layout.createParallelGroup(BASELINE).addComponent(seperate));
		vGroup2.addGroup(layout.createParallelGroup(BASELINE).addComponent(newAccountButton));
		vGroup2.addGroup(layout.createParallelGroup(BASELINE).addComponent(editAccountButton));
		vGroup2.addGroup(layout.createParallelGroup(BASELINE).addComponent(deleteAccountButton));
		vGroup2.addGroup(layout.createParallelGroup(BASELINE).addComponent(printButton));
		vGroup2.addGroup(layout.createParallelGroup(BASELINE).addComponent(homeButton));
        layout.setVerticalGroup(vGroup2);
	}

	void createMenuButtonPanel()
	{
		viewHistoryButton = new JButton("Reports");
		viewHistoryButton.setBackground(Color.WHITE);
		viewHistoryButton.addActionListener(this);
		viewHistoryButton.setActionCommand("HISTORY");
		viewHistoryButton.setMinimumSize(new Dimension(140,25));

		manageAccountsButton = new JButton("Account Manager");
		manageAccountsButton.setBackground(Color.WHITE);
		manageAccountsButton.addActionListener(this);
		manageAccountsButton.setActionCommand("ACCOUNTS");
		manageAccountsButton.setMinimumSize(new Dimension(140,25));

		logOutButton = new JButton("Log out");
		logOutButton.setBackground(Color.WHITE);
		logOutButton.addActionListener(this);
		logOutButton.setActionCommand("LOG_OFF");
		logOutButton.setMinimumSize(new Dimension(140,25));

		seperate4 = new JLabel("____________________");
		seperate4.setForeground(Color.WHITE);
		seperate5 = new JLabel("____________________");
		seperate5.setForeground(Color.WHITE);

		menuButtonPanel = new JPanel(new FlowLayout());
		menuButtonPanel.setBackground(new Color(1, 0.1f, 0.1f).darker().darker());
		layout2 = new GroupLayout(menuButtonPanel);
		layout2.setAutoCreateGaps(true);
		layout2.setAutoCreateContainerGaps(true);
		menuButtonPanel.setLayout(layout2);

		hGroup = layout2.createSequentialGroup();
		hGroup.addGroup(layout2.createParallelGroup()
		.addComponent(viewHistoryButton)
		.addComponent(seperate4)
		.addComponent(manageAccountsButton)
		.addComponent(seperate5)
		.addComponent(logOutButton));
		layout2.setHorizontalGroup(hGroup);

		vGroup = layout2.createSequentialGroup();
		vGroup.addGroup(layout2.createParallelGroup(BASELINE).addComponent(viewHistoryButton));
		vGroup.addGroup(layout2.createParallelGroup(BASELINE).addComponent(seperate4));
		vGroup.addGroup(layout2.createParallelGroup(BASELINE).addComponent(manageAccountsButton));
		vGroup.addGroup(layout2.createParallelGroup(BASELINE).addComponent(seperate5));
		vGroup.addGroup(layout2.createParallelGroup(BASELINE).addComponent(logOutButton));

        layout2.setVerticalGroup(vGroup);
	}

    void setAccountManagerButtonsVisible(boolean b)
    {
		if(b)
		{
			accountTypeLabel.setVisible(true);
			accountCBox.setVisible(true);
			clock.setVisible(true);
			seperate.setVisible(true);
			homeButton.setVisible(true);
			searchTF.setVisible(true);
			searchLabel.setVisible(true);
			newAccountButton.setVisible(true);
			editAccountButton.setVisible(true);
			deleteAccountButton.setVisible(true);
		}
		else
		{
			accountTypeLabel.setVisible(false);
			accountCBox.setVisible(false);
			clock.setVisible(false);
			seperate.setVisible(false);
			homeButton.setVisible(false);
			searchTF.setVisible(false);
			searchLabel.setVisible(false);
			newAccountButton.setVisible(false);
			editAccountButton.setVisible(false);
			deleteAccountButton.setVisible(false);
		}
	}

    void setMenuButtonsVisible(boolean b)
    {
		if(b)
		{
			viewHistoryButton.setVisible(true);
			manageAccountsButton.setVisible(true);
			logOutButton.setVisible(true);
			seperate4.setVisible(true);
			seperate5.setVisible(true);
		}
		else
		{
			viewHistoryButton.setVisible(false);
			manageAccountsButton.setVisible(false);
			logOutButton.setVisible(false);
			seperate4.setVisible(false);
			seperate5.setVisible(false);
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
			doEdit();
		}
	}

	public void insertUpdate(DocumentEvent e)
	{
		try
		{
			String text = searchTF.getText();
			if (text.trim().length() == 0)
			{
				table.rowSorter.setRowFilter(null);
			}
			else
			{
				table.rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
			}
		}
		catch ( Exception ee )
		{
			ee.printStackTrace();
			System.out.println( "Exception "+ee.getMessage());
		}
	}

	public void removeUpdate(DocumentEvent e)
	{
		try
		{
			String text = searchTF.getText();
			if (text.trim().length() == 0)
			{
				table.rowSorter.setRowFilter(null);
			}
			else
			{
				table.rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
			}
		}
		catch ( Exception ee )
		{
			ee.printStackTrace();
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
		//setSize(d.width/2 + d.width/4, d.height/2 + d.width / 6);
		//setSize(d.width/2 + d.width/4, d.height/2 + d.width / 5);
		setSize(d.width/2 + d.width/4, 600);
		setLocation((d.width/4 + d.width/4)/4, (d.height/2 + d.width / 40) / 8);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Welcome to Bryant Place - Main Menu");
		setVisible(true);
	}
}