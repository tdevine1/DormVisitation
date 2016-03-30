//Brandon Ballard
//COMP 4440 My Clock as of 3/16/16

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import javax.swing.Timer;

class MyClock extends JPanel implements ActionListener
{
	SimpleDateFormat df;
	Timer timer;
	JLabel clockLabel;

	public MyClock()
	{
		setBackground((Color.BLACK));
		setMaximumSize(new Dimension(100, 30));
		clockLabel = new JLabel();
		clockLabel.setForeground(Color.GREEN);
		clockLabel.setFont(new Font("Courier", Font.BOLD,14));
		df = new SimpleDateFormat("h:mm:ss a");
		timer = new Timer(500, this);
		timer.setRepeats(true);
		timer.start();
		clockLabel.setText(df.format(new Date()));
		add(clockLabel, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent ae)
	{
		clockLabel.setText(df.format(new Date()));
	}

	public String getCurrentTimeAsString()
	{
		return(df.format(new Date()));
	}
}
