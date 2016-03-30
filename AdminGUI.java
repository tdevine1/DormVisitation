/**
 * RD's GUI
 * Date: 03/11/16
 * @author Brandon Ballard & Hanif Mirza
 *
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.event.*;
import java.sql.*;
import java.util.Random;

public class AdminGUI extends JFrame implements ActionListener,ChangeListener
{
	public static void main(String[] x)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		new AdminGUI();
	}

	JTabbedPane 	jtp;
	Statement 		myStatement;
	String 			myUsername;

	AdminGUI()
	{

		jtp = new JTabbedPane();
		jtp.addChangeListener(this);

		jtp.addTab("Visitation History", new HistoryPanel() );
		jtp.addTab("Add Users", new JPanel() );
		jtp.addTab("Edit/Delete Users", new JPanel() );

		setJMenuBar(newMenuBar());
		add(jtp);
		setupMainFrame();
	}

	//=====================================================================
    public void stateChanged(ChangeEvent e)
    {
		JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
		int tabIndex = tabbedPane.getSelectedIndex();
		if(tabIndex == 2)//if it is the tab for EditInfoPanel
		{
			boolean tabIsVisible = tabbedPane.isEnabledAt(tabIndex);
			if ( tabIsVisible )
			{
				//if the EditInfoPanel is visible then populate EditInfoPanel's all textfields with member's info
			}
		}
    }

	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("SEQUELS"))
		{

		}
		else if(e.getActionCommand().equals("HISTORY"))
		{

		}
		else if(e.getActionCommand().equals("LOGOUT"))
		{
			this.dispose();//dispode the AdminGUI frame
		}
	}
	//====================================================================================
	//Function to create JMenuBar
	private JMenuBar newMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		JMenu subMenu = new JMenu("Menu");
		subMenu.getAccessibleContext().setAccessibleDescription("View the menu");

        JMenuItem historyMenuItem = newItem("View Log History","HISTORY",this,KeyEvent.VK_H,KeyEvent.VK_H,"View detailed history of logs");
        historyMenuItem.setIcon(new ImageIcon("iconOrderHis.png"));
		subMenu.add(historyMenuItem);

        JMenuItem logoutMenuItem = newItem("Logout","LOGOUT",this,KeyEvent.VK_L,KeyEvent.VK_L,"Log out and exit");
        logoutMenuItem.setIcon(new ImageIcon("iconLogout.png"));
		subMenu.add(logoutMenuItem);
		menuBar.add(subMenu);
		return menuBar;
	}
	//===========================================================================================================
	//Function to create JMenuItem
	private JMenuItem newItem(String label,String actionCommand,ActionListener menuListener,int mnemonic, int keyEvent,String toolTipText)
	{
		JMenuItem m;
		m=new JMenuItem(label,mnemonic);
		m.setAccelerator(KeyStroke.getKeyStroke(keyEvent,ActionEvent.ALT_MASK));
		m.getAccessibleContext().setAccessibleDescription(toolTipText);
		m.setToolTipText(toolTipText);
		m.setActionCommand(actionCommand);
		m.addActionListener(menuListener);
		return m;
	}

	//===================================================================================
	void setupMainFrame()
	{
		Toolkit tk;
		Dimension d;
		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();
		setSize(d.width/2 + d.width/6, d.height/2 + d.width / 6);
		setLocation((d.width/4 + d.width/4)/4, (d.height/2 + d.width / 6) / 8);
		//setSize(750, 400);
		//setResizable(false);
		//setLocation(d.width/10, d.height/4);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);      //program terminates when closed
		setTitle("Welcome to Bryant Place");
		setVisible(true);									 //Now we can see the window.
	}
}//end of class
