/* * * * * * * * * * *\
 * ReportsPanel.java
 * Description: Panel to display report of visitation and lockout history, once this feature is activated, the screen will be
 *				empty with the exception of a JCombo box, this combo box will contain a list of reports that the user can run.
 *				When the user selects a report type two text fields appear prompting the user to select a start and end date.
 *				A calendar button to the right of each text field will show a calendar when pressed allowing the user to easily
 *				select the desired date range. When a valid date range is selected the user can cick "run". This will populate
 *				a JTable showing the appropriate data for the selected date range. The user then has the option to filter the
 *				data by using the smart search field at the bottom and can print the report to a PDF file by clicking "Print
 *				to PDF".
 *
 * Date: 4/4/16
 * @author Brandon Ballard & Hanif Mirza
\* * * * * * * * * * */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.sql.*;
import java.awt.*;
import javax.swing.*;
import static javax.swing.GroupLayout.Alignment.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.event.*;

public class ReportsPanel extends JPanel implements ActionListener, MouseListener
{
	JTextField				startDateTF, endDateTF;
	JButton 				startDateButton, endDateButton, runButton;
	JLabel 					startDateLabel, endDateLabel;
	JPanel					northPanel;
	CalendarPanel			startDateCalendar, endDateCalendar;
	MyTable					table;
	JScrollPane 			scrollPane;
	DefaultTableModel       tableModel;
	String 					SQL_Query, startDate, endDate;
	Statement				statement;
	JComboBox				reportCBox;
	AdminGUI				adminGUI;

	//BY BRANDON BALLARD
	ReportsPanel(AdminGUI adminGUI,Statement statement, JComboBox reportCBox)
	{
		this.adminGUI = adminGUI;
		this.reportCBox = reportCBox;
		this.statement = statement;

		reportCBox.addActionListener(this);
		northPanel = createNorthPanel();
		northPanel.addMouseListener(this);
		northPanel.setVisible(false);
		add(northPanel, BorderLayout.NORTH);
		setUpGUI();
	}

	//BY BRANDON BALLARD
	public void actionPerformed(ActionEvent e)
	{
		//Shows calendar for user to select a date and hides the other calendar if
		//it is visible
		if(e.getSource() == startDateButton)
		{
			startDateCalendar.setVisible(true);
			startDateButton.setVisible(false);

			if(endDateCalendar.isVisible())
			{
				endDateCalendar.setVisible(false);
				endDateButton.setVisible(true);
			}
		}

		//Shows calendar for user to select a date and hides the other calendar if
		//it is visible
		if(e.getSource() == endDateButton)
		{
			endDateCalendar.setVisible(true);
			endDateButton.setVisible(false);

			if(startDateCalendar.isVisible())
			{
				startDateCalendar.setVisible(false);
				startDateButton.setVisible(true);
			}
		}

		//Validates start and end date and calls appropriate methods to populate the table
		if(e.getSource() == runButton)
		{
			startDateCalendar.setVisible(false);
			startDateButton.setVisible(true);
			endDateCalendar.setVisible(false);
			endDateButton.setVisible(true);

			startDate = startDateTF.getText().trim();
			endDate = endDateTF.getText().trim();

			if(!startDate.equals("") && !endDate.equals(""))
			{
				Date date1 = new Date(startDateCalendar.databaseDate);
				Date date2 = new Date(endDateCalendar.databaseDate);

				if( date1.compareTo(date2) == 0 )
				{
					if(reportCBox.getSelectedIndex() == 1)
					{
						populateTable(startDateCalendar.databaseDate, endDateCalendar.databaseDate);
					}
					else if(reportCBox.getSelectedIndex() == 2)
					{
						populateLockoutTable(startDateCalendar.databaseDate, endDateCalendar.databaseDate);
					}
				}
				else if(date1.compareTo(date2) < 0)
				{
					if(reportCBox.getSelectedIndex() == 1)
					{
						populateTable(startDateCalendar.databaseDate, endDateCalendar.databaseDate);
					}
					else if(reportCBox.getSelectedIndex() == 2)
					{
						populateLockoutTable(startDateCalendar.databaseDate, endDateCalendar.databaseDate);
					}
				}
				else if( date1.compareTo(date2) > 0 )
				{
					JOptionPane.showMessageDialog(this, "Dates are out of range", "ERROR" , JOptionPane.ERROR_MESSAGE);
				}

			}
			else
			{
				JOptionPane.showMessageDialog(this, "Please select a date range", "ERROR" , JOptionPane.ERROR_MESSAGE);
			}
		}

		//Clears the screen if no report type is selected from the combo box
		if(reportCBox.getSelectedIndex() == 0)
		{
			northPanel.setVisible(false);
		}
		else
		{
			northPanel.setVisible(true);
		}
	}

	// Written by Hanif Mirza. This function will create and view a visitation history table within a certain date range.
	void populateTable(String startDate, String endDate)
	{
		String strDate = "'"+startDate+"'";
		String lastDate = "'"+endDate+"'";

		String SQL_Query =   " Select v.guest_name as 'Guest Name',v.guest_ID_type as 'ID Type',CONCAT(r.first_name,\" \",r.last_name) as 'Resident Name',"
							+ " r.room_number as 'Room Number', DATE_FORMAT(v.visitation_date,'%c/%e/%Y') as 'Date',TIME_FORMAT(v.time_in, '%l:%i%p')as 'Time in',TIME_FORMAT(v.time_out, '%l:%i%p')as 'Time out',"
							+ " v.overnight_status as 'Overnight', CONCAT(e.first_name,\" \",e.last_name) as 'DM/RA Name' "

							+ " From Visitation_Detail v, Resident r,Employee e"
							+ " Where v.visitation_date between "+strDate+" and "+lastDate+" and v.time_out is not null and v.residentID = r.userID and v.empID = e.userID ;" ;
		try
		{
			if(scrollPane != null)
			{
				remove(scrollPane);
				repaint();
			}

			table = new MyTable(statement.executeQuery(SQL_Query)); // construct a table using ResultSet
			tableModel = (DefaultTableModel)table.getModel();

			scrollPane = new  JScrollPane(table);
			scrollPane.setMinimumSize(new Dimension(this.getWidth(), this.getHeight()));
			scrollPane.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));

			add(scrollPane, BorderLayout.CENTER);
			repaint();
			adminGUI.searchReportTF.setVisible(true);
			adminGUI.searchLabel.setVisible(true);
		}
		catch ( SQLException sqlException )
		{
			JOptionPane.showMessageDialog(this, sqlException.getMessage());
			sqlException.printStackTrace();
		}
		catch ( Exception exception )
		{
			JOptionPane.showMessageDialog(this, exception.getMessage());
			exception.printStackTrace();
		}
	}

	// Written by Hanif Mirza. This function will create and view a lockout history table within a certain date range.
	void populateLockoutTable(String startDate, String endDate)
	{
		String strDate = "'"+startDate+"'";
		String lastDate = "'"+endDate+"'";

		String SQL_Query =   " Select CONCAT(r.first_name,\" \",r.last_name) as 'Resident Name',"
							+ " r.room_number as 'Room Number', DATE_FORMAT(l.lockout_date,'%c/%e/%Y') as 'Date',TIME_FORMAT(l.lockout_time, '%l:%i%p')as 'Time',"
							+ " l.ra_name as 'RA Responded' "

							+ " From Lockout_Detail l, Resident r"
							+ " Where l.lockout_date between "+strDate+" and "+lastDate+" and l.residentID = r.userID ;" ;
		try
		{
			if(scrollPane != null)
			{
				remove(scrollPane);
				repaint();
			}

			table = new MyTable(statement.executeQuery(SQL_Query)); // construct a table using ResultSet
			tableModel = (DefaultTableModel)table.getModel();

			scrollPane = new  JScrollPane(table);
			scrollPane.setMinimumSize(new Dimension(this.getWidth(), this.getHeight()));
			scrollPane.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));

			add(scrollPane, BorderLayout.CENTER);
			repaint();
			adminGUI.searchReportTF.setVisible(true);
			adminGUI.searchLabel.setVisible(true);
		}
		catch ( SQLException sqlException )
		{
			JOptionPane.showMessageDialog(this, sqlException.getMessage());
			sqlException.printStackTrace();
		}
		catch ( Exception exception )
		{
			JOptionPane.showMessageDialog(this, exception.getMessage());
			exception.printStackTrace();
		}
	}

	//Creates the north panel and adds button and combo box using group layout, BY BRANDON BALLARD
	JPanel createNorthPanel()
    {
		GroupLayout layout;
		JPanel p;

		runButton = new JButton("Run");
		runButton.setBackground(Color.WHITE);
		runButton.addActionListener(this);

		startDateButton = new JButton(new ImageIcon("calendaricon.png"));
		startDateButton.setMinimumSize(new Dimension(27, 27));
		startDateButton.setMaximumSize(new Dimension(27, 27));
		startDateButton.setPreferredSize(new Dimension(27, 27));
		startDateButton.setBackground(Color.WHITE);
		startDateButton.addActionListener(this);

		endDateButton = new JButton(new ImageIcon("calendaricon.png"));
		endDateButton.setMinimumSize(new Dimension(27, 27));
		endDateButton.setMaximumSize(new Dimension(27, 27));
		endDateButton.setPreferredSize(new Dimension(27, 27));
		endDateButton.setBackground(Color.WHITE);
		endDateButton.addActionListener(this);

		startDateLabel = new JLabel("Start Date: ");
		startDateLabel.setForeground(Color.WHITE);

		endDateLabel = new JLabel("End Date: ");
		endDateLabel.setForeground(Color.WHITE);

		startDateTF = new JTextField(8);
		startDateTF.setEditable(false);
		startDateTF.setText("");

		endDateTF = new JTextField(8);
		endDateTF.setEditable(false);
		endDateTF.setText("");

		startDateCalendar = new CalendarPanel(startDateTF, startDateButton);
		startDateCalendar.setBackground(Color.GRAY);

		endDateCalendar = new CalendarPanel(endDateTF, endDateButton);
		endDateCalendar.setBackground(Color.GRAY);

		p = new JPanel();
		p.setBackground(Color.GRAY);

		layout = new GroupLayout(p);
		p.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup().addComponent(startDateLabel));
		hGroup.addGroup(layout.createParallelGroup().addComponent(startDateTF));
		hGroup.addGroup(layout.createParallelGroup().addComponent(startDateButton));
		hGroup.addGroup(layout.createParallelGroup().addComponent(startDateCalendar));
		hGroup.addGroup(layout.createParallelGroup().addComponent(endDateLabel));
		hGroup.addGroup(layout.createParallelGroup().addComponent(endDateTF));
		hGroup.addGroup(layout.createParallelGroup().addComponent(endDateButton));
		hGroup.addGroup(layout.createParallelGroup().addComponent(endDateCalendar));
		hGroup.addGroup(layout.createParallelGroup().addComponent(runButton));
		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(startDateLabel).addComponent(startDateTF).addComponent(startDateButton)
		.addComponent(startDateCalendar).addComponent(endDateLabel).addComponent(endDateTF).addComponent(endDateButton).addComponent(endDateCalendar)
		.addComponent(runButton));

		layout.setVerticalGroup(vGroup);

		return(p);
	}

	public void mouseExited(MouseEvent me){}

	public void mouseEntered(MouseEvent me){}

	public void mouseReleased(MouseEvent me){}

	public void mousePressed(MouseEvent me){}

	//Hides any visible calendars
	public void mouseClicked(MouseEvent me)
	{
		startDateCalendar.setVisible(false);
		endDateCalendar.setVisible(false);
		startDateButton.setVisible(true);
		endDateButton.setVisible(true);
	}

	void setUpGUI()
	{
		setBackground(Color.LIGHT_GRAY);
	}
}
