/* * * * * * * * * * *\
 * MyStatsPanel
 * Description: Panel to display statistical analysis
 * Date: 4/4/16
 * @author Brandon Ballard & Hanif Mirza
\* * * * * * * * * * */

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import javax.swing.Timer;

import java.awt.event.*;
import static javax.swing.GroupLayout.Alignment.*;
import java.text.*;
import java.sql.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

class MyStatsPanel extends JPanel implements ActionListener
{
	SimpleDateFormat df;
	Timer timer;
	JPanel clockPanel, northPanel, statsPanel, linePanel;
	JLabel numGuestsIn, numGuestsOut, checkIns, totalResidents, overnights, lineLabel, pieLabel;

	public MyStatsPanel()
	{
		lineLabel = new JLabel();
		lineLabel.setIcon(new ImageIcon("line.png"));
		pieLabel = new JLabel();
		pieLabel.setIcon(new ImageIcon("piechart.png"));

		linePanel = new JPanel();
		linePanel.setBackground(Color.GRAY);
		linePanel.add(lineLabel);
		linePanel.add(pieLabel);

		northPanel = new JPanel(new FlowLayout());
		northPanel.setBackground(Color.GRAY);
		clockPanel = new MyClock("Advanced");
		northPanel.add(clockPanel);

		statsPanel = setFields();

		add(northPanel, BorderLayout.NORTH);
		add(statsPanel, BorderLayout.CENTER);
		add(linePanel, BorderLayout.SOUTH);

		setUpGUI();
	}

	public void actionPerformed(ActionEvent ae)
	{
	}

	JPanel setFields()
    {
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

		JLabel n1 = new JLabel("23");
		n1.setForeground(Color.WHITE);
		n1.setFont(new Font("Courier New", Font.PLAIN,20));
		JLabel n2 = new JLabel("63");
		n2.setForeground(Color.WHITE);
		n2.setFont(new Font("Courier New", Font.PLAIN,20));
		JLabel n3 = new JLabel("40");
		n3.setForeground(Color.WHITE);
		n3.setFont(new Font("Courier New", Font.PLAIN,20));
		JLabel n4 = new JLabel("12");
		n4.setForeground(Color.WHITE);
		n4.setFont(new Font("Courier New", Font.PLAIN,20));

		p = new JPanel();
		//p.setBackground(new Color(1, 0.1f, 0.1f).darker().darker());
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

	public void setUpGUI()
	{
		setBackground(Color.LIGHT_GRAY);
	}
}
