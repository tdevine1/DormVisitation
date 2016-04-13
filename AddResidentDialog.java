/* * * * * * * * * * *\
 * Add Resident Dialog
 * Description: Dialog for adding residents
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

class AddResidentDialog extends JDialog implements ActionListener,DocumentListener
{
	JPanel 						buttonPanel, fieldPanel;
	JButton 					cancelButton, addButton;
	AdminGUI					adminGUI;
	Statement 					myStatement;
	DateFormat 					dateFormat, timeFormat;
	JTextField 					roomNoTF, dormNameTF, firstNameTF, lastNameTF, emailTF, phoneTF;
	JPasswordField				password, rePassword;
	JLabel 						roomNoLabel, genderLabel, dormNameLabel, accountLabel, firstNameLabel, lastNameLabel, emailLabel, phoneLabel;
	JComboBox 					genderCBox, dormNameCBox;
	JTextArea					commentArea;
	String						residentName, raName, residentRoomNo, currTime, currDate, myVisitationID;
	int row;

	public AddResidentDialog(AdminGUI adminGUI,Statement myStatement)
	{
		setTitle("Add Resident");

		this.myStatement = myStatement;
		this.adminGUI = adminGUI;

		dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		timeFormat = new SimpleDateFormat("hh:mma");

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

		fieldPanel = setFields();

		getContentPane().add(fieldPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setupMainFrame();
	}

	public AddResidentDialog(AdminGUI adminGUI,Statement myStatement,String myVisitationID , int row)
	{
		setTitle("Edit Resident");

		this.myStatement = myStatement;
		this.adminGUI = adminGUI;
		this.myVisitationID = myVisitationID;
		this.row = row;

		dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		timeFormat = new SimpleDateFormat("hh:mma");

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
		firstNameLabel = new JLabel("First Name:");
		firstNameLabel.setForeground(Color.WHITE);
		lastNameLabel = new JLabel("Last Name:");
		lastNameLabel.setForeground(Color.WHITE);
		emailLabel = new JLabel("Email:");
		emailLabel.setForeground(Color.WHITE);
		phoneLabel = new JLabel("Phone Number:");
		phoneLabel.setForeground(Color.WHITE);
		firstNameTF = new JTextField(30);
		lastNameTF = new JTextField(30);
		emailTF = new JTextField(30);
		phoneTF = new JTextField(30);
		roomNoTF = new JTextField(30);
		roomNoLabel = new JLabel("Room Number:");
		roomNoLabel.setForeground(Color.WHITE);

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
		hGroup.addGroup(layout.createParallelGroup().addComponent(firstNameLabel).addComponent(lastNameLabel)
		.addComponent(genderLabel).addComponent(dormNameLabel).addComponent(roomNoLabel)
		.addComponent(emailLabel).addComponent(phoneLabel));
		hGroup.addGroup(layout.createParallelGroup().addComponent(firstNameTF).addComponent(lastNameTF)
		.addComponent(genderCBox).addComponent(dormNameCBox).addComponent(roomNoTF)
		.addComponent(emailTF).addComponent(phoneTF));
		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(firstNameLabel).addComponent(firstNameTF));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(lastNameLabel).addComponent(lastNameTF));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(genderLabel).addComponent(genderCBox));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(dormNameLabel).addComponent(dormNameCBox));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(roomNoLabel).addComponent(roomNoTF));
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
		//setSize(d.width/3, d.height/3 + d.height/20);
		setSize(d.width/3, 300);
		setLocation(d.width/3, d.height/4 + d.height/20);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
    }
}