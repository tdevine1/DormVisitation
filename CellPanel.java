/* * * * * * * * * * *\
 * CellPanel.java
 * Description: Used by CalendarPanel.java, represents each individual "day" cell on the calendar. When mouse hovers over a cell
 *				the background color changes to gray. If the cells value is the same as the current date, the cell is highlited
 *				blue. Also has another constructor to create a blank cell. If a cell is clicked and not blank, the value is
 *				sent to CalendarPanel.java where it gets handled.
 *
 * Date: 5/7/16
 * @author Brandon Ballard
\* * * * * * * * * * */

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import java.text.*;

class CellPanel extends JPanel implements MouseListener
{
	JLabel 			day;
	int 			dayNum, currYear;
	CalendarPanel 	cp;
	boolean 		active = false, today = false;

	//Constructor for creating a cell with value
	public CellPanel(int day, CalendarPanel cp, String currMonth, int currYear)
	{
		this.currYear = currYear;
		this.cp = cp;
		this.day = new JLabel(Integer.toString(day));
		this.day.setForeground(Color.DARK_GRAY);

		active = true;
		dayNum = day;
		setBackground(Color.WHITE);
		addMouseListener(this);
		add(this.day);

		//Check to see if cell value matches current date, if so, make it blue
		if(Integer.parseInt(new SimpleDateFormat("d").format(new Date())) == day && new SimpleDateFormat("MMMMM").format(new Date()).equals(currMonth)&& Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date())) == currYear)
		{
			today = true;
			setBackground(Color.BLUE);
			this.day.setForeground(Color.WHITE);
		}
	}

	//Constructor for creating a blank cell
	public CellPanel(String day)
	{
		this.day = new JLabel("");
		setBackground(Color.WHITE);
	}

	//Toggle cell colors
	public void mouseEntered(MouseEvent me)
	{
		setBackground(Color.GRAY);
		day.setForeground(Color.WHITE);
	}

	//Toggle cell colors
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