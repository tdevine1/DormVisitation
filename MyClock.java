/* * * * * * * * * * *\
 * MyClock
 * Description: Clock to show time, has two construstors for different clock styles
 * Date: 4/4/16
 * @author Brandon Ballard & Hanif Mirza
\* * * * * * * * * * */

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import javax.swing.Timer;

class MyClock extends JPanel implements ActionListener
{
	JLabel timeLabel, dayLabel, dateLabel, clockLabel;
	SimpleDateFormat timeFormat, dayFormat, dateFormat, df;
	Timer timer;
	boolean advanced;

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

		add(clockLabel, BorderLayout.CENTER);
	}

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

		add(dateLabel, BorderLayout.NORTH);
		add(timeLabel, BorderLayout.CENTER);
		add(dayLabel, BorderLayout.SOUTH);
	}

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
