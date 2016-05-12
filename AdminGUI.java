/* * * * * * * * * * *\
 * AdminGUI.java
 * Description: The administrators GUI for managing accounts, viewing history, and printing reports, the home screen or
 *				main menu is created from MyStatsPanel.java and displays general information about the resident hall
 *
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
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.*;

class AdminGUI extends JFrame implements ActionListener, ListSelectionListener, MouseListener, DocumentListener, DropTargetListener
{
	JButton 					importButton, printButton, homeButton, viewHistoryButton, manageAccountsButton, newAccountButton, deleteAccountButton, editAccountButton, logOutButton;
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
	DropTarget 					dropTarget;

	//BY BRANDON BALLARD
    AdminGUI(Statement statement, String userID,String password)
    {
		this.statement = statement;
		this.userID = userID;
		this.password = password;

		//____________________________________________________________________________CREATE NORTH PANEL

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
		reportCBox.addItem("Lock Out Report");
		reportCBox.setVisible(false);
		reportCBox.addActionListener(this);

		northPanel = new JPanel(new FlowLayout());
		northPanel.setBackground(new Color(1, 0.1f, 0.1f).darker().darker());
		northPanel.add(accountTypeLabel);
		northPanel.add(accountCBox);
		northPanel.add(reportCBox);

		//______________________________________________________________________________CREATE SOUTH PANEL

		searchLabel = new JLabel("Search:");
		searchLabel.setVisible(false);
		searchLabel.setForeground(Color.WHITE);

		searchTF = new JTextField(20);
		searchTF.getDocument().addDocumentListener(this);
		searchTF.setVisible(false);

		searchReportTF = new JTextField(20);
		searchReportTF.getDocument().addDocumentListener(this);
		searchReportTF.setVisible(false);

		southPanel = new JPanel(new FlowLayout());
		southPanel.setBackground(new Color(1, 0.1f, 0.1f).darker().darker());
		southPanel.add(searchLabel);
		southPanel.add(searchTF);
		southPanel.add(searchReportTF);

		//______________________________________________________________________________//CREATE POP UP (RIGHT CLICK ON A TABLE)

		//When a user right clicks on a table in the account management feature, a popup will allow the user to either
		//edit or delete the selected account
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
		statsPanel = new MyStatsPanel(this, statement);
		reportsPanel = new ReportsPanel(this,statement, reportCBox);

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

	//BY BRANDON BALLARD
    public void actionPerformed(ActionEvent e)
    {
		//________________________________________________________________ACTION COMMANDS FOR MAIN MENU BUTTONS

		//Shows the account management buttons and panels and hides the rest
		if(e.getActionCommand().equals("ACCOUNTS"))
		{
			setTitle("Welcome to Bryant Place - Account Manager");
			remove(statsPanel);
			add(scrollPane, BorderLayout.CENTER);
			accountTypeLabel.setText("Select account type:");
			setMenuButtonsVisible(false);
			setAccountManagerButtonsVisible(true);
		}
		//Shows the account reports buttons and panels and hides the rest
		else if(e.getActionCommand().equals("HISTORY"))
		{
			reportsPanel.removeAll();
			reportsPanel = new ReportsPanel(this,statement, reportCBox);
			setTitle("Welcome to Bryant Place - Reports");
			remove(statsPanel);
			setMenuButtonsVisible(false);
			setAccountManagerButtonsVisible(true);
			accountCBox.setVisible(false);
			reportCBox.setVisible(true);
			accountTypeLabel.setText("Select report type:");
			add(reportsPanel, BorderLayout.CENTER);
		}
		//Logs the user out of the system
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
		else if(e.getActionCommand().equals("IMPORT_EXCEL"))
		{
			new ImportExcel(statement);
			accountType = "Resident";
			showAccountListTable();
		}
		else if(e.getSource() == printButton)
		{
			if(reportsPanel.table != null)
			{
				if(reportCBox.getSelectedIndex() == 1)
				{
					new CreatePDF(reportsPanel.table,"History details from " + reportsPanel.startDate + " to " + reportsPanel.endDate);
				}
				else if(reportCBox.getSelectedIndex() == 2)
				{
					new CreatePDF(reportsPanel.table,"Lock out details from " + reportsPanel.startDate + " to " + reportsPanel.endDate);
				}
			}
			else
			{
				JOptionPane.showMessageDialog(this, "Nothing to print", "ERROR" , JOptionPane.ERROR_MESSAGE);
			}
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
			importButton.setVisible(false);
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
			importButton.setVisible(false);
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
			importButton.setVisible(true);
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
			importButton.setVisible(false);
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
			importButton.setVisible(false);
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
			importButton.setVisible(false);
			newAccountButton.setText("New Employee");
			deleteAccountButton.setText("Delete Employee");
			editAccountButton.setText("Edit Employee");
		}
		//________________________________________________________________ACTION COMMANDS FOR SWITCHING BETWEEN REPORT
		//                                                                TYPES WHEN REPORTS IS OPEN
		if(reportCBox.getSelectedIndex() == 1  || reportCBox.getSelectedIndex() == 2)
		{
			printButton.setVisible(true);

			if(reportsPanel.table != null)
			{
				searchReportTF.setVisible(true);
				searchLabel.setVisible(true);
			}
			else
			{
				searchReportTF.setVisible(false);
				searchLabel.setVisible(false);
			}
		}
		else if(reportCBox.getSelectedIndex() == 0)
		{
			printButton.setVisible(false);
			searchReportTF.setVisible(false);
			if(reportsPanel.scrollPane != null)
			{
				reportsPanel.remove(reportsPanel.scrollPane);
				reportsPanel.scrollPane = null;
				reportsPanel.table = null;
			}
		}
    }

    void doAdd()//___________________________________________ADD A NEW ACCOUNT
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
			new AddEmployeeDialog(this,statement,accountType);
		}
		else if(accountType.equals("DM"))
		{
			new AddEmployeeDialog(this,statement,accountType);
		}
	}

    void doEdit()//___________________________________________EDIT AN ACCOUNT
    {
		int[]  selectionList = table.getSelectedRows();

		if (selectionList.length == 1)
		{
			int row = table.convertRowIndexToModel(selectionList[0]);
			String accountID = tableModel.getValueAt(row,0).toString();

			if(accountType.equals("BannedGuest"))
			{
				new AddBannedGuestDialog(this,statement,accountID, row);
			}
			else if(accountType.equals("Resident"))
			{
				new AddResidentDialog(this,statement,accountID, row);
			}
			else if(accountType.equals("RA"))
			{
				new AddEmployeeDialog(this,statement,accountType,accountID, row);
			}
			else if(accountType.equals("DM"))
			{
				new AddEmployeeDialog(this,statement,accountType,accountID, row);
			}
		}
		else
		{
			JOptionPane.showMessageDialog(this,"Please select a row from the table to edit", "Warning ",JOptionPane.WARNING_MESSAGE);
		}
	}

	// Written by Hanif Mirza. This function will delete an account (Resident, RA,DM,or Banned Guest). It depends on what account admin selects
	void doDelete()
    {
		String	sql;
		int 	confirmationNo = 0;
		int 	accountsDeleted = 0;
		int[]  	selectionList = table.getSelectedRows();

		if(selectionList.length >= 1)
		{
			for(int i = selectionList.length; i > 0; i--)
			{
				int row = table.convertRowIndexToModel(selectionList[i - 1]);
				String accountID = tableModel.getValueAt(row,0).toString(); // First column will have the accountID, which is the key

				try
				{
					if(accountType.equals("BannedGuest"))
					{
						sql = "DELETE FROM Banned_Guest WHERE guestID = "+ "'"+accountID+"'";
						confirmationNo = statement.executeUpdate(sql); // delete the account
					}
					else if(accountType.equals("Resident"))
					{
						sql = "DELETE FROM Resident WHERE userID = "+ "'"+accountID+"'";
						confirmationNo = statement.executeUpdate(sql); // delete the account
					}
					else if(accountType.equals("RA"))
					{
						sql = "DELETE FROM Employee WHERE userID = "+ "'"+accountID+"'";
						confirmationNo = statement.executeUpdate(sql); // delete the account

						sql = "DELETE FROM RA WHERE userID = "+ "'"+accountID+"'";
						statement.executeUpdate(sql);
					}
					else if(accountType.equals("DM"))
					{
						sql = "DELETE FROM Employee WHERE userID = "+ "'"+accountID+"'";
						confirmationNo = statement.executeUpdate(sql);// delete the account

						sql = "DELETE FROM DM WHERE userID = "+ "'"+accountID+"'";
						statement.executeUpdate(sql);

					}

					tableModel.removeRow(row);

					if( confirmationNo > 0)
					{
						accountsDeleted++;
					}
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

			if(accountsDeleted > 1)
			{
				JOptionPane.showMessageDialog(this, accountsDeleted + " accounts have been deleted", "Success" , JOptionPane.INFORMATION_MESSAGE );
			}
			else
			{
				JOptionPane.showMessageDialog(this, "Account has been deleted", "Success" , JOptionPane.INFORMATION_MESSAGE );
			}
		}
		else
		{
			JOptionPane.showMessageDialog(this,"Please select a row from the table to delete", "Warning ",JOptionPane.WARNING_MESSAGE);
		}
	}

	// Written by Hanif Mirza. This function will perform the log out. It will store Resident Director's log out time to the database
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
		catch(Exception exception)
		{
			JOptionPane.showMessageDialog(this, exception.getMessage());
		}
	}

	// Written by Hanif Mirza. This function will create a table with selected account type, such as Resident, RA, DM, and Banned Guest. It depends on what account type admin selects
	void showAccountListTable()
	{
		if(accountType.equals("BannedGuest"))
		{
			String SQL_Query  = " Select b.guestID as 'Guest ID',b.guest_name as 'Full Name',b.gender as 'Gender',"
							  + " b.dorm_name as 'Location',b.category as 'Category',b.comments as 'Comments'"
							  + " From Banned_Guest b";
			createTable(SQL_Query);
		}
		else if(accountType.equals("Resident"))
		{
			String SQL_Query  = " Select r.userID as 'Resident ID',r.first_name as 'First Name',r.last_name as 'Last Name',r.gender as 'Gender',"
							  + " r.email as 'Email', r.phone as 'Phone', r.dorm_name as 'Location',"
							  + " r.room_number as 'Room', r.number_of_lockouts as 'Lockout'"
							  + " From Resident r";
			createTable(SQL_Query);
		}
		else if(accountType.equals("RA"))
		{
			String SQL_Query  = " Select r.userID as 'Username',r.password as 'Password',e.first_name as 'First Name',e.last_name as 'Last Name',"
							  + " e.email as 'Email', e.phone as 'Phone'"

							  + " From Employee e, RA r"
							  + " Where e.userID = r.userID " ;
			createTable(SQL_Query);
		}
		else if(accountType.equals("DM"))
		{
			String SQL_Query  = " Select d.userID as 'Username',d.password as 'Password',e.first_name as 'First Name',e.last_name as 'Last Name',"
							  + " e.email as 'Email', e.phone as 'Phone'"

							  + " From Employee e, DM d"
							  + " Where e.userID = d.userID " ;
			createTable(SQL_Query);
		}
	}

	// Written by Hanif Mirza. This function will create a table with selected account type, such as Resident, RA, DM, and Banned Guest and show the table on AdminGUI
	void createTable(String SQL_Query)
	{
		try
		{
			remove(scrollPane);

			table = new MyTable(statement.executeQuery(SQL_Query)); // Construct a table from ResultSet
			table.getSelectionModel().addListSelectionListener(this);
			tableModel = (DefaultTableModel)table.getModel();
			table.addMouseListener(this);

			scrollPane = new  JScrollPane(table);
			dropTarget = new DropTarget(scrollPane, this);

			add(scrollPane, BorderLayout.CENTER);
			searchLabel.setVisible(true);
			searchTF.setVisible(true);
		}
		catch(SQLException se)
		{
			JOptionPane.showMessageDialog(this, se.getMessage());
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	public void valueChanged(ListSelectionEvent lse)
	{
		editAccountButton.setEnabled(false);

		if(table.getSelectedRows().length == 1)
		{
			editAccountButton.setEnabled(true);
			deleteAccountButton.setEnabled(true);
		}
	}

	//BY BRANDON BALLARD, creates and adds the account management components using a group layout
	void createAccountMangerButtonPanel()
	{
		newAccountButton = new JButton("New Account");
		newAccountButton.setBackground(Color.WHITE);
		newAccountButton.addActionListener(this);
		newAccountButton.setActionCommand("NEW");
		newAccountButton.setMinimumSize(new Dimension(120,25));

		printButton = new JButton("Print to PDF");
		printButton.addActionListener(this);
		printButton.setVisible(false);
		printButton.setBackground(Color.WHITE);
		printButton.setMinimumSize(new Dimension(120,25));

		importButton = new JButton("Import Excel");
		importButton.setVisible(false);
		importButton.addActionListener(this);
		importButton.setActionCommand("IMPORT_EXCEL");
		importButton.setBackground(Color.WHITE);
		importButton.setMinimumSize(new Dimension(120,25));

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
		.addComponent(importButton)
		.addComponent(homeButton));
		layout.setHorizontalGroup(hGroup2);

		vGroup2 = layout.createSequentialGroup();
		vGroup2.addGroup(layout.createParallelGroup(BASELINE).addComponent(clock));
		vGroup2.addGroup(layout.createParallelGroup(BASELINE).addComponent(seperate));
		vGroup2.addGroup(layout.createParallelGroup(BASELINE).addComponent(newAccountButton));
		vGroup2.addGroup(layout.createParallelGroup(BASELINE).addComponent(editAccountButton));
		vGroup2.addGroup(layout.createParallelGroup(BASELINE).addComponent(deleteAccountButton));
		vGroup2.addGroup(layout.createParallelGroup(BASELINE).addComponent(printButton));
		vGroup2.addGroup(layout.createParallelGroup(BASELINE).addComponent(importButton));
		vGroup2.addGroup(layout.createParallelGroup(BASELINE).addComponent(homeButton));
        layout.setVerticalGroup(vGroup2);
	}

	//BY BRANDON BALLARD, creates and adds the main menu components using a group layout
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

	//BY BRANDON BALLARD, toggles between different features on the system
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

	//BY BRANDON BALLARD, toggles between different features on the system
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

	//BY BRANDON BALLARD, handles the drag and drop file feature, if the account management feature is activated and
	//the resident account type is selected, you can drop an excel file and populate the resident list in the database.
	//The system will not allow illegal files
	public void drop(DropTargetDropEvent dtde)
	{
		if(accountCBox.getSelectedIndex() == 1)
		{
			java.util.List<File> fileList;
			Transferable transferableData;
			File file;

			transferableData = dtde.getTransferable();

			try
			{
				if(transferableData.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
				{
					dtde.acceptDrop(DnDConstants.ACTION_COPY);

					fileList = (java.util.List<File>)(transferableData.getTransferData(DataFlavor.javaFileListFlavor));

					for(int n = 0; n < fileList.size(); n++)
					{
						file = fileList.get(n);
						new ImportExcel(statement, file);
						accountType = "Resident";
						showAccountListTable();
					}
				}
				else
				{
					System.out.println("File list flavor not supported");
				}
			}
			catch(UnsupportedFlavorException ufe)
			{
				System.out.println("Unsopported flavor found");
				ufe.printStackTrace();
			}
			catch(IOException ioe)
			{
				System.out.println("IOException found getting transferable data.");
				ioe.printStackTrace();
			}
		}
	}

	public void dragExit(DropTargetEvent dte)
	{
	}

	public void dropActionChanged(DropTargetDragEvent dtde)
	{
	}

	public void dragOver(DropTargetDragEvent dtde)
	{
	}

	public void dragEnter(DropTargetDragEvent dtde)
	{
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
			if (e.getDocument() == searchTF.getDocument() )
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
			else if (e.getDocument() == searchReportTF.getDocument() )
			{
				String text = searchReportTF.getText();
				if (text.trim().length() == 0)
				{
					reportsPanel.table.rowSorter.setRowFilter(null);
				}
				else
				{
					reportsPanel.table.rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				}
			}
		}
		catch ( Exception ee )
		{
			System.out.println( "Exception "+ee.getMessage());
		}
	}

	public void removeUpdate(DocumentEvent e)
	{
		try
		{
			if (e.getDocument() == searchTF.getDocument() )
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
			else if (e.getDocument() == searchReportTF.getDocument() )
			{
				String text = searchReportTF.getText();
				if (text.trim().length() == 0)
				{
					reportsPanel.table.rowSorter.setRowFilter(null);
				}
				else
				{
					reportsPanel.table.rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				}
			}
		}
		catch ( Exception ee )
		{
			System.out.println( "Exception "+ee.getMessage());
		}
	}
	public void changedUpdate(DocumentEvent e){}

	void setupMainFrame()
	{
		Toolkit    tk;
		Dimension   d;
		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();
		setSize(d.width/2 + d.width/4, 600);
		setLocation((d.width/4 + d.width/4)/4, (d.height/2 + d.width / 40) / 8);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Welcome to Bryant Place - Main Menu");
		setVisible(true);
	}
}