/* * * * * * * * * * *\
 * Add Banned Guest Dialog
 * Description: Dialog for adding banned guests
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

class AddBannedGuestDialog extends JDialog implements ActionListener,DocumentListener
{
	JPanel 						buttonPanel, fieldPanel;
	JButton 					cancelButton, addButton;
	AdminGUI					adminGUI;
	Statement 					myStatement;
	DateFormat 					dateFormat, timeFormat;
	JTextField 					guestNameTF, categoryTF;
	JLabel 						guestNameLabel, genderLabel, dormNameLabel, commentLabel, categoryLabel;
	JComboBox 					genderCBox, dormNameCBox;
	JTextArea					commentArea;
	String						myVisitationID;;
	int row;

	public AddBannedGuestDialog(AdminGUI adminGUI,Statement myStatement)
	{
		setTitle("Add Banned Guest");

		this.myStatement = myStatement;
		this.adminGUI = adminGUI;

		dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		timeFormat = new SimpleDateFormat("hh:mma");

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

		fieldPanel = setFields();

		getContentPane().add(fieldPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setupMainFrame();
	}

	public AddBannedGuestDialog(AdminGUI adminGUI,Statement myStatement,String myVisitationID , int row)
	{
		setTitle("Edit Banned Guest");

		this.myStatement = myStatement;
		this.adminGUI = adminGUI;
		this.myVisitationID = myVisitationID;
		this.row = row;

		dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		timeFormat = new SimpleDateFormat("hh:mma");

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

		fieldPanel = setFields();

		populateAllFields();

		getContentPane().add(fieldPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setupMainFrame();
		setTitle("Edit Banned Guest");
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
	}

    void doAddGuest()
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
		dormNameCBox.addItem("All Locations");
		categoryTF = new JTextField(30);
		commentArea = new JTextArea();
		commentArea.setRows(4);
		commentArea.setWrapStyleWord(true);

		categoryTF.getDocument().addDocumentListener(this);
		guestNameTF.getDocument().addDocumentListener(this);

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
