/* * * * * * * * * * *\
 * DefaultGUI.java
 * Description: Any feature on the application that we feel needs to be "protected" can use this class which requires the user
 *				to enter the password of the currently signed in account in order to proceed. If the password is incorrect, an
 *				error message is shown and prompts the user to retry
 * Date: 4/4/16
 * @author Brandon Ballard
\* * * * * * * * * * */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

class PasswordDialog extends JDialog implements ActionListener,DocumentListener
{
	JPanel				buttonPanel, fieldPanel, blankPanel;
	JButton 			okButton, exitButton;
	JPasswordField 		passwordTF;
	JLabel 				passwordLabel;
    String              password,empPassword;
    public boolean 		isValid;

	public PasswordDialog(String password)
	{
		this.empPassword = password;
		okButton = new JButton("OK");
		okButton.setBackground(Color.WHITE);
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);
		okButton.setEnabled(false);

		exitButton = new JButton("Cancel");
		exitButton.setBackground(Color.WHITE);
		exitButton.addActionListener(this);

		buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.setBackground(Color.GRAY);
		buttonPanel.add(exitButton);
		buttonPanel.add(okButton);

		blankPanel = new JPanel();
		blankPanel.setBackground(new Color(1, 0.1f, 0.1f).darker().darker());

		passwordLabel = new JLabel("Password:");
		passwordLabel.setForeground(Color.WHITE);

		passwordTF = new JPasswordField(20);
	 	passwordTF.getDocument().addDocumentListener(this);

		fieldPanel = new JPanel(new FlowLayout());
		fieldPanel.setBackground(new Color(1, 0.1f, 0.1f).darker().darker());
		fieldPanel.add(passwordLabel);
        fieldPanel.add(passwordTF);

		//Add components
		getContentPane().add(blankPanel, BorderLayout.NORTH);
		getContentPane().add(fieldPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setupMainFrame();
	}

	public void actionPerformed(ActionEvent e)
	{
		if( e.getSource() == okButton )
		{
			validatePassword();
		}

		else if(e.getSource() == exitButton )
		{
			this.dispose();
		}
    }

	//This method will validate the password
	void validatePassword()
	{

		if(new String(passwordTF.getPassword()).equals(empPassword))
		{
			this.dispose();
			isValid = true;
		}
		else
		{
			JOptionPane.showMessageDialog(this," Password is incorrect", "Validation Failure" , JOptionPane.ERROR_MESSAGE);
			isValid = false;
		}
	}

	public void insertUpdate(DocumentEvent de)
	{
	    password = new String(passwordTF.getPassword());

	    if(!password.equals(""))
	    {
	        okButton.setEnabled(true);
	    }
	}

	public void removeUpdate(DocumentEvent de)
	{
	    password = new String(passwordTF.getPassword());

	    if(password.equals(""))
	    {
	        okButton.setEnabled(false);
	    }
	}

    public void changedUpdate(DocumentEvent de){}

    void setupMainFrame()
	{
		setTitle("Password Required");
		Toolkit    tk;
		Dimension   d;

		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();
		setSize(d.width/3, d.height/6);
		setLocation(d.width/3, d.height/3);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setVisible(true);
    }
}
