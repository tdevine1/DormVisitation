/* * * * * * * * * * *\
 * Add Employee Dialog
 * Description: Dialog for adding employees
 * Date: 4/10/16
 * @author Brandon Ballard & Hanif Mirza
\* * * * * * * * * * */

import java.awt.event.*;
import static javax.swing.GroupLayout.Alignment.*;
import java.text.*;
import java.sql.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

class AddEmployeeDialog extends JDialog implements ActionListener,DocumentListener
{
	JPanel 						buttonPanel, fieldPanel;
	JButton 					cancelButton, addButton;
	AdminGUI					adminGUI;
	Statement 					myStatement;
	DateFormat 					dateFormat, timeFormat;
	JTextField 					usernameTF, firstNameTF, lastNameTF, emailTF, phoneTF;
	JPasswordField				password, rePassword;
	JLabel 						accountLabel, usernameLabel, passwordLabel, rePasswordLabel, firstNameLabel, lastNameLabel, emailLabel, phoneLabel;
	String						residentName, raName, residentRoomNo, myVisitationID;
	JComboBox 					accountCBox;
	int row;

	public AddEmployeeDialog(AdminGUI adminGUI,Statement myStatement)
	{
		setTitle("Add Employee");

		this.myStatement = myStatement;
		this.adminGUI = adminGUI;

		dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		timeFormat = new SimpleDateFormat("hh:mma");

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

		fieldPanel = setFields();

		getContentPane().add(fieldPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setupMainFrame();
	}

	public AddEmployeeDialog(AdminGUI adminGUI,Statement myStatement,String myVisitationID , int row)
	{
		setTitle("Edit Employee");

		this.myStatement = myStatement;
		this.adminGUI = adminGUI;
		this.myVisitationID = myVisitationID;
		this.row = row;

		dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		timeFormat = new SimpleDateFormat("hh:mma");

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

		fieldPanel = setFields();

		populateAllFields();

		getContentPane().add(fieldPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setupMainFrame();
	}

	void populateAllFields()
	{
		/*int[]  selectionList = myAdminGUI2.table.getSelectedRows(); // get the selected row
		int row = myDefaultGUI.table.convertRowIndexToModel(selectionList[0]);
		studentRoomTF.setText( myDefaultGUI.tableModel.getValueAt(row,5).toString() );
		guestNameTF.setText( myDefaultGUI.tableModel.getValueAt(row,1).toString() );
		guestAgeTF.setText( myDefaultGUI.tableModel.getValueAt(row,2).toString() );
		guestIDTypes.setSelectedItem( myDefaultGUI.tableModel.getValueAt(row,3).toString() );
		String overnightStatus = myDefaultGUI.tableModel.getValueAt(row,8).toString();
		*/
	}

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

	void doUpdateResident()
    {
	}

    void doAddResident()
    {
		try
		{
			validateFields();
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

	void validateFields() throws Exception
	{
	}

    public void insertUpdate(DocumentEvent de)
    {
    }
    public void changedUpdate(DocumentEvent de){}

    public void removeUpdate(DocumentEvent de)
    {
    }

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
		//setSize(d.width/3, d.height/3 + d.height/15);
		setLocation(d.width/3, d.height/4 + d.height/30);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
    }
}