/* * * * * * * * * * *\
 * MyStatsPanel.java
 * Description: This panel is the main menu of the admin GUI, it gives an overview of what is going on in bryant place by providing
 *				information such as how many guests are currently in house, how many guests are staying overnight, etc. Also
 *				displays a pie chart showing an average of what trypes of guests are checking in (FSU, Pierpont, Other).
 * Date: 4/4/16
 * @author Brandon Ballard & Hanif Mirza
\* * * * * * * * * * */

import java.awt.*;
import javax.swing.*;
import static javax.swing.GroupLayout.Alignment.*;
import java.sql.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

class MyStatsPanel extends JPanel
{
	JPanel 				clockPanel, northPanel, statsPanel, linePanel, pieChartPanel;
	JLabel 				numGuestsIn, numGuestsOut, checkIns, totalResidents, overnights, lineLabel, pieLabel;
	Statement			statement;
	JComboBox			reportCBox;
	AdminGUI			adminGUI;
	String				totalGuestsIn, todaysCheckIns, todaysCheckOuts, totalOvernights;
	int					FSU = 0, PCTC = 0, DL = 0, OTHERS = 0;
	ResultSet			myResultSet;

	//BY BRANDON BALLARD
	public MyStatsPanel(AdminGUI adminGUI,Statement statement)
	{
		this.adminGUI = adminGUI;
		this.statement = statement;

		pieChartPanel = createPieChartPanel();
		pieChartPanel.setBackground(Color.GRAY);
		pieChartPanel.setPreferredSize(new Dimension(550, 260));

		northPanel = new JPanel(new FlowLayout());
		northPanel.setBackground(Color.GRAY);
		clockPanel = new MyClock("Advanced");
		northPanel.add(clockPanel);

		statsPanel = setFields();

		add(northPanel, BorderLayout.NORTH);
		add(statsPanel, BorderLayout.CENTER);
		add(pieChartPanel, BorderLayout.SOUTH);

		setUpGUI();
	}

	// Written by Hanif Mirza, this function will return a pie chart panel with different guest ID types number
	JPanel createPieChartPanel()
	{
		getIDTypesInfo();

		DefaultPieDataset dataset = new DefaultPieDataset( );
		dataset.setValue( "FSU" , new Double( FSU ) ); // set the value with number of FSU IDs
		dataset.setValue( "PCTC" , new Double( PCTC ) ); // set the value with number of PCTC IDs
		dataset.setValue( "Driving License" , new Double( DL ) );// set the value with number of Driving License IDs
		dataset.setValue( "Other" , new Double( OTHERS ) );// set the value with number of IDs in other category

		JFreeChart chart = ChartFactory.createPieChart3D("Guest ID Types",dataset,true,true,false);
		return new ChartPanel( chart );
	}

	//Adds the text that displays information about the resident hall, BY BRANDON BALLARD
	JPanel setFields()
    {
		getDatabaseInfo();
		GroupLayout layout;
		JPanel p;

		numGuestsIn = new JLabel("Total guests in house .............................");
		numGuestsIn.setFont(new Font("Courier New", Font.PLAIN,20));
		numGuestsIn.setForeground(Color.WHITE);
		checkIns = new JLabel("Todays guest check ins ............................");
		checkIns.setForeground(Color.WHITE);
	   	checkIns.setFont(new Font("Courier New", Font.PLAIN,20));
	    numGuestsOut = new JLabel("Todays guest check outs ...........................");
		numGuestsOut.setFont(new Font("Courier New", Font.PLAIN,20));
		numGuestsOut.setForeground(Color.WHITE);
		overnights = new JLabel("Total overnight guests ............................");
		overnights.setForeground(Color.WHITE);
		overnights.setFont(new Font("Courier New", Font.PLAIN,20));

		JLabel n1 = new JLabel(totalGuestsIn);
		n1.setForeground(Color.WHITE);
		n1.setFont(new Font("Courier New", Font.PLAIN,20));
		JLabel n2 = new JLabel(todaysCheckIns);
		n2.setForeground(Color.WHITE);
		n2.setFont(new Font("Courier New", Font.PLAIN,20));
		JLabel n3 = new JLabel(todaysCheckOuts);
		n3.setForeground(Color.WHITE);
		n3.setFont(new Font("Courier New", Font.PLAIN,20));
		JLabel n4 = new JLabel(totalOvernights);
		n4.setForeground(Color.WHITE);
		n4.setFont(new Font("Courier New", Font.PLAIN,20));

		p = new JPanel();
		p.setBackground(Color.GRAY);

		layout = new GroupLayout(p);
		p.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup().addComponent(numGuestsIn)
		.addComponent(checkIns).addComponent(numGuestsOut).addComponent(overnights));
		hGroup.addGroup(layout.createParallelGroup().addComponent(n1)
		.addComponent(n2).addComponent(n3).addComponent(n4));
		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(numGuestsIn).addComponent(n1));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(checkIns).addComponent(n2));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(numGuestsOut).addComponent(n3));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(overnights).addComponent(n4));

		layout.setVerticalGroup(vGroup);

		return(p);
	}

	// Written by Hanif Mirza, this function will access the database to count the number of FSU, PCTC, DL and Others ID types
	void getIDTypesInfo()
	{
		try
		{
			String SQL_Query =  " Select count(*)"
							  + " From Visitation_Detail v"
							  + " Where v.guest_ID_type = 'FSU' " ;

			myResultSet = statement.executeQuery(SQL_Query);
			myResultSet.first();
			FSU = myResultSet.getInt(1);
			myResultSet.close();

			SQL_Query =  " Select count(*)"
					   + " From Visitation_Detail v"
					   + " Where v.guest_ID_type = 'PCTC' " ;

			myResultSet = statement.executeQuery(SQL_Query);
			myResultSet.first();
			PCTC = myResultSet.getInt(1);
			myResultSet.close();

			SQL_Query =  " Select count(*)"
					   + " From Visitation_Detail v"
					   + " Where v.guest_ID_type = 'DL' " ;

			myResultSet = statement.executeQuery(SQL_Query);
			myResultSet.first();
			DL = myResultSet.getInt(1);
			myResultSet.close();

			SQL_Query =  " Select count(*)"
					   + " From Visitation_Detail v"
					   + " Where v.guest_ID_type = 'Other' " ;

			myResultSet = statement.executeQuery(SQL_Query);
			myResultSet.first();
			OTHERS = myResultSet.getInt(1);
			myResultSet.close();
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

	// Written by Hanif Mirza, this function will access the database to get different statistical informations about the visitation details
	void getDatabaseInfo()
	{
		try
		{
			String SQL_Query =  " Select count(*)"
							  + " From Visitation_Detail v"
							  + " Where v.time_out is null " ;

			myResultSet = statement.executeQuery(SQL_Query);
			myResultSet.first();
			totalGuestsIn = myResultSet.getInt(1) + "";
			myResultSet.close();

			SQL_Query =  " Select count(*)"
					   + " From Visitation_Detail v"
					   + " Where v.visitation_date = curdate() " ;

			myResultSet = statement.executeQuery(SQL_Query);
			myResultSet.first();
			todaysCheckIns = myResultSet.getInt(1) + "";
			myResultSet.close();

			SQL_Query =  " Select count(*)"
					   + " From Visitation_Detail v"
					   + " Where v.visitation_date = curdate() and v.time_out is not null " ;

			myResultSet = statement.executeQuery(SQL_Query);
			myResultSet.first();
			todaysCheckOuts = myResultSet.getInt(1) + "";
			myResultSet.close();

			SQL_Query =  " Select count(*)"
					   + " From Visitation_Detail v"
					   + " Where v.time_out is null and v.overnight_status = 'Yes' " ;

			myResultSet = statement.executeQuery(SQL_Query);
			myResultSet.first();
			totalOvernights = myResultSet.getInt(1) + "";
			myResultSet.close();
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

	public void setUpGUI()
	{
		setBackground(Color.LIGHT_GRAY);
	}
}
