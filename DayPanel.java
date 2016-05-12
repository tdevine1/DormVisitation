/* * * * * * * * * * *\
 * DayPanel.java
 * Description: Used by CalendarPanel.java, helps create the "days of the week" bar at the top of the calendar
 * Date: 5/7/16
 * @author Brandon Ballard
\* * * * * * * * * * */

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

class DayPanel extends JPanel
{
	JLabel day;

	public DayPanel(String s)
	{
		day = new JLabel(s);
		day.setForeground(Color.WHITE);
		setBackground(new Color(1, 0.1f, 0.1f).darker().darker());
		add(day);
	}
}