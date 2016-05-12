/* * * * * * * * * * *\
 * MyClock.java
 * Description: Clock to show time, has two construstors for different clock styles, constructor #1 is meant for a small and
 *				simple clock, it is used in DefaultGui and AdminGUI in the button panels. Constructor #2 is meant to be a more
 *				advanced clock and shows time, day of week, and the date, used in AdminGui's homepage, each clock type is a JPanel
 *				and can be added to the program as a component.
 *
 * Date: 5/7/16
 * @author Brandon Ballard
\* * * * * * * * * * */

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import javax.swing.Timer;

//Simple clock shows time and AM/PM
class MyClock extends JPanel implements ActionListener
{
	JLabel 				timeLabel, dayLabel, dateLabel, clockLabel;
	SimpleDateFormat 	timeFormat, dayFormat, dateFormat, df;
	Timer 				timer;
	boolean 			advanced;

	public MyClock()
	{
		advanced = false;

		df = new SimpleDateFormat("h:mm:ss a");

		clockLabel = new JLabel();
		clockLabel.setForeground(Color.GREEN);
		clockLabel.setFont(new Font("Courier", Font.BOLD,14));
		clockLabel.setText(df.format(new Date()));

		timer = new Timer(500, this);
		timer.setRepeats(true);
		timer.start();

		setUpGUI();

		//Add components
		add(clockLabel, BorderLayout.CENTER);
	}

	//More advanced clock, shows time, day of week, and date
	public MyClock(String s)
	{
		advanced = true;

		timeFormat = new SimpleDateFormat("h:mm:ss a");
		dayFormat = new SimpleDateFormat("EEEE");
		dateFormat = new SimpleDateFormat("M/d/yyyy");

		dateLabel = new JLabel();
		dateLabel.setForeground(Color.GRAY);
		dateLabel.setFont(new Font("Arial", Font.PLAIN,15));
		dateLabel.setText(dateFormat.format(new Date()) + "                                                    ");

		dayLabel = new JLabel();
		dayLabel.setForeground(Color.GRAY);
		dayLabel.setFont(new Font("Arial", Font.PLAIN,15));
		dayLabel.setText("                                                 " + dayFormat.format(new Date()));

		timeLabel = new JLabel();
		timeLabel.setForeground(Color.GRAY);
		timeLabel.setFont(new Font("Arial", Font.PLAIN,50));
		timeLabel.setText(timeFormat.format(new Date()));

		timer = new Timer(500, this);
		timer.setRepeats(true);
		timer.start();

		setUpGUI();

		//Add components
		add(dateLabel, BorderLayout.NORTH);
		add(timeLabel, BorderLayout.CENTER);
		add(dayLabel, BorderLayout.SOUTH);
	}

	//This is where the clocks get updated, the timer will call this method
	public void actionPerformed(ActionEvent ae)
	{
		if(advanced)
		{
			timeLabel.setText(timeFormat.format(new Date()));
			dateLabel.setText(dateFormat.format(new Date()) + "                                                    ");
			dayLabel.setText("                                                 " + dayFormat.format(new Date()));
		}
		else
		{
			clockLabel.setText(df.format(new Date()));
		}
	}

	public void setUpGUI()
	{
		if(advanced)
		{
			setBackground((Color.WHITE));
			setMaximumSize(new Dimension(300, 120));
			setMinimumSize(new Dimension(300, 120));
			setPreferredSize(new Dimension(300, 120));
		}
		else
		{
			setBackground((Color.BLACK));
			setMaximumSize(new Dimension(100, 30));
		}
	}
}
