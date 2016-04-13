import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import javax.swing.Timer;

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

	public void mouseEntered(MouseEvent me)
	{

	}

	public void mouseExited(MouseEvent me)
	{
		setBackground(Color.WHITE);
		day.setForeground(Color.DARK_GRAY);
	}
}