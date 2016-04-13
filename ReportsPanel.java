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

public class ReportsPanel extends JPanel implements ActionListener, DocumentListener, MouseListener
{
	JTextField				startDateTF, endDateTF;
	JButton 				startDateButton, endDateButton, runButton, printButton;
	JLabel 					startDateLabel, endDateLabel;
	JPanel					northPanel;
	CalendarPanel			startDateCalendar, endDateCalendar;
	MyTable					table;
	JScrollPane 			scrollPane;
	DefaultTableModel       tableModel;
	String 					SQL_Query, startDate, endDate;
	Statement				statement;
	JComboBox				reportCBox;
	JTextField 				searchTF;

	ReportsPanel(Statement statement, JComboBox reportCBox, JTextField searchTF, JButton printButton)
	{
		this.printButton = printButton;
		this.searchTF = searchTF;
		this.reportCBox = reportCBox;
		this.statement = statement;

		reportCBox.addActionListener(this);
		searchTF.getDocument().addDocumentListener(this);
		printButton.addActionListener(this);
		northPanel = createNorthPanel();
		northPanel.addMouseListener(this);
		northPanel.setVisible(false);
		add(northPanel, BorderLayout.NORTH);
		setUpGUI();
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == printButton)
		{
			if(table != null)
			{
				new CreatePDF(table,"History details from " + startDate + " to " + endDate);
			}
			else
			{
				//JOptionPane.showMessageDialog(this, "Nothing to print", "ERROR" , JOptionPane.ERROR_MESSAGE);
			}
		}
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

				if(date2.after(date1))
				{
					populateTable(startDateCalendar.databaseDate, endDateCalendar.databaseDate);
				}
				else
				{
					JOptionPane.showMessageDialog(this, "Dates are out of range", "ERROR" , JOptionPane.ERROR_MESSAGE);
				}

			}
			else
			{
				JOptionPane.showMessageDialog(this, "Please select a date range", "ERROR" , JOptionPane.ERROR_MESSAGE);
			}
		}

		if(reportCBox.getSelectedIndex() == 0)
		{
			northPanel.setVisible(false);
		}
		else
		{
			northPanel.setVisible(true);
		}
	}

	void populateTable(String startDate, String endDate)
	{
		String strDate = "'"+startDate+"'";
		String lastDate = "'"+endDate+"'";

		String SQL_Query =   " Select v.guest_name as 'Guest Name',v.guest_ID_type as 'ID Type',CONCAT(r.first_name,\" \",r.last_name) as 'Resident Name',"
							+ " r.room_number as 'Room Number', DATE_FORMAT(v.visitation_date,'%m/%d/%Y') as 'Date',TIME_FORMAT(v.time_in, '%h:%i%p')as 'Time in',TIME_FORMAT(v.time_out, '%h:%i%p')as 'Time out',"
							+ " v.overnight_status as 'Overnight', CONCAT(e.first_name,\" \",e.last_name) as 'DM/RA Name' "

							+ " From Visitation_Detail v, Resident r,Employee e"
							+ " Where v.visitation_date between "+strDate+" and "+lastDate+" and v.time_out is not null and v.residentID = r.userID and v.empID = e.userID ;" ;

		createTable(SQL_Query);
	}

	void createTable(String SQL_Query)
	{
		try
		{
			if(scrollPane != null)
			{
				remove(scrollPane);
				repaint();
			}

			table = new MyTable(statement.executeQuery(SQL_Query));
			tableModel = (DefaultTableModel)table.getModel();

			scrollPane = new  JScrollPane(table);
			scrollPane.setMinimumSize(new Dimension(this.getWidth(), this.getHeight()));
			scrollPane.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));

			add(scrollPane, BorderLayout.CENTER);
			repaint();
			searchTF.setVisible(true);
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
				if (table == null)
				{
					System.out.println( "dhdasfdhajfkdshakfasdjkhf " );
				}
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

	public void mouseExited(MouseEvent me){}

	public void mouseEntered(MouseEvent me){}

	public void mouseReleased(MouseEvent me){}

	public void mousePressed(MouseEvent me){}

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
