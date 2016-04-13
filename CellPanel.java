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

class CellPanel extends JPanel implements MouseListener
{
	Random random;
	JLabel day;
	int dayNum, currYear;
	CalendarPanel cp;
	boolean active = false;
	boolean today = false;

	public CellPanel(int day, CalendarPanel cp, String currMonth, int currYear)
	{
		this.currYear = currYear;
		this.cp = cp;
		this.day = new JLabel(Integer.toString(day));
		this.day.setForeground(Color.DARK_GRAY);

		active = true;
		dayNum = day;
		random = new Random();
		setBackground(Color.WHITE);
		addMouseListener(this);
		add(this.day);

		if(Integer.parseInt(new SimpleDateFormat("d").format(new Date())) == day && new SimpleDateFormat("MMMMM").format(new Date()).equals(currMonth)&& Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date())) == currYear)
		{
			today = true;
			setBackground(Color.BLUE);
			this.day.setForeground(Color.WHITE);
		}
	}
	public CellPanel(String day)
	{
		this.day = new JLabel("");
		setBackground(Color.WHITE);
	}

	public void mouseEntered(MouseEvent me)
	{
		setBackground(Color.GRAY);
		day.setForeground(Color.WHITE);
	}

	public void mouseExited(MouseEvent me)
	{
		if(!today)
		{
			setBackground(Color.WHITE);
			day.setForeground(Color.DARK_GRAY);
		}
		else
		{
			setBackground(Color.BLUE);
			day.setForeground(Color.WHITE);
		}
	}

	public void mouseClicked(MouseEvent me)
	{
		if(active)
		{
			cp.selectedDay = dayNum;
		}
	}

	public void mouseReleased(MouseEvent me)
	{
	}

	public void mousePressed(MouseEvent me)
	{
	}
}