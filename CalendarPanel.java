/* * * * * * * * * * *\
 * CalendarPanel.java
 * Description: Used when running reports. Constructed with a JTextField and a JButton. When the button is clicked,
 *				a calendar is shown for user to selcect a date, the selected date populates the passed in text field.
 *
 * Date: 5/7/16
 * @author Brandon Ballard
\* * * * * * * * * * */

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import static javax.swing.GroupLayout.Alignment.*;
import java.util.*;
import java.text.SimpleDateFormat;

class CalendarPanel extends JPanel implements ChangeListener, MouseListener
{
	JPanel 				panel, monthYearPanel, container;
	GroupLayout		 	layout, layout2;
	JSpinner 			monthSpinner, yearSpinner;
	JTextField 			yearTF, field;
	SpinnerListModel 	monthModel;
	SpinnerNumberModel 	yearModel;
	String 				month, currMonth, selectedDate, databaseDate;
	int 				year, currYear, m, dayOfMonth, maxDays, v, selectedDay;
	SimpleDateFormat 	yearFormat, monthFormat, dayFormat;
	Calendar 			calendar;
	JButton 			button;
	String[] 			monthStrings = {"December", "November", "October", "September", "August", "July",
										 "June", "May", "April", "March", "February", "January"};

	CalendarPanel(JTextField field, JButton button)
	{
		this.button = button;//this is the button the user presses to show the calendar
		this.field = field;//this is the text field where the selected date will be placed

		monthFormat = new SimpleDateFormat("MMMM");
		yearFormat = new SimpleDateFormat("yyyy");

		dayOfMonth = 1;
		maxDays = v = selectedDay = 0;
		m = Integer.parseInt(new SimpleDateFormat("M").format(new Date()));

		maxDays = getMaxDays(Integer.parseInt(new SimpleDateFormat("M").format(new Date())));//returns how many days are in current month
		currMonth = monthFormat.format(new Date());
		currYear = Integer.parseInt(yearFormat.format(new Date()));

		monthModel = new SpinnerListModel(monthStrings);
		monthSpinner = new JSpinner(monthModel);
		monthSpinner.addChangeListener(this);

		yearModel = new SpinnerNumberModel(currYear, currYear - 100, currYear + 100, 1);
		yearSpinner = new JSpinner(yearModel);
		yearSpinner.addChangeListener(this);

		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(yearSpinner, "#");
		yearSpinner.setEditor(editor);

		calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, Integer.parseInt(new SimpleDateFormat("M").format(new Date())) - 1);
		calendar.set(Calendar.YEAR, currYear);
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		panel = new JPanel(new GridLayout(7,7));
		panel.setMaximumSize(new Dimension(220, 170));
		panel.setMinimumSize(new Dimension(220, 170));
		panel.setPreferredSize(new Dimension(220, 170));

		panel.add(new DayPanel("Sun"));
		panel.add(new DayPanel("Mon"));
		panel.add(new DayPanel("Tue"));
		panel.add(new DayPanel("Wed"));
		panel.add(new DayPanel("Thu"));
		panel.add(new DayPanel("Fri"));
		panel.add(new DayPanel("Sat"));

		//Add day of month cells to calendar
		for(int x = 0; x < 6 * 7; x++)
		{
			if(x < calendar.get(Calendar.DAY_OF_WEEK) - 1 || dayOfMonth > maxDays)
			{
				panel.add(new CellPanel(""));//Blank cell **there are a total of 42 cells and only 31 days in a month so not all cells can have a value
			}
			else
			{
				panel.add(new CellPanel(dayOfMonth, this, currMonth, (int)yearSpinner.getValue())).addMouseListener(this);//Cell with appropriate day of month
				dayOfMonth++;
			}
		}
		dayOfMonth = 1;

		monthYearPanel = new JPanel();
		monthYearPanel.setBackground(new Color(1, 0.1f, 0.1f).darker().darker());
		layout2 = new GroupLayout(monthYearPanel);
		monthYearPanel.setLayout(layout2);
		layout2.setAutoCreateGaps(true);
		layout2.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup2 = layout2.createSequentialGroup();
		hGroup2.addGroup(layout2.createParallelGroup().addComponent(yearSpinner));
		hGroup2.addGroup(layout2.createParallelGroup().addComponent(monthSpinner));
		layout2.setHorizontalGroup(hGroup2);

		GroupLayout.SequentialGroup vGroup2 = layout2.createSequentialGroup();
		vGroup2.addGroup(layout2.createParallelGroup(BASELINE).addComponent(yearSpinner).addComponent(monthSpinner));
		layout2.setVerticalGroup(vGroup2);

		container = new JPanel(new FlowLayout());
		container.setBackground(Color.GRAY);
		layout = new GroupLayout(container);
		container.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup().addComponent(monthYearPanel).addComponent(panel));
		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(monthYearPanel));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(panel));

		layout.setVerticalGroup(vGroup);

		add(container);

		monthSpinner.setValue(currMonth);

		v++;
		setVisible(false);
	}

	//Called when user changes the month or year on the calendar
	public void stateChanged(ChangeEvent ce)
	{
		month = (String)monthSpinner.getValue();
		year = (int)yearSpinner.getValue();

		if( v >= 1)
		{
			updateCalendar();
		}
	}

	//Returns the total number of days in the passed in month
	int getMaxDays(int month)
	{
		if(month == 9 || month == 4 || month == 6 || month == 11)
		{
			return 30;
		}
		else if(month == 2)
		{
			if(!isLeapYear((int)(yearSpinner.getValue())))
			{
				return 28;
			}
			else
			{
				return 29;
			}
		}
		else
		{
			return 31;
		}
	}

	//Function name says it all, returns true if passed in year is a leap year, else returns false
    public boolean isLeapYear(int year)
    {
		if ((year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0)))
		{
			return true;
		}
		else
		{
			return false;
		}
    }

	//Called anytime the user has changed year or month
	void updateCalendar()
	{
		container.removeAll();
		currMonth = (String)monthSpinner.getValue();

		if(currMonth.equals("January"))
		{
			m = 1;
		}
		else if(currMonth.equals("February"))
		{
			m = 2;
		}
		else if(currMonth.equals("March"))
		{
			m = 3;
		}
		else if(currMonth.equals("April"))
		{
			m = 4;
		}
		else if(currMonth.equals("May"))
		{
			m = 5;
		}
		else if(currMonth.equals("June"))
		{
			m = 6;
		}
		else if(currMonth.equals("July"))
		{
			m = 7;
		}
		else if(currMonth.equals("August"))
		{
			m = 8;
		}
		else if(currMonth.equals("September"))
		{
			m = 9;
		}
		else if(currMonth.equals("October"))
		{
			m = 10;
		}
		else if(currMonth.equals("November"))
		{
			m = 11;
		}
		else if(currMonth.equals("December"))
		{
			m = 12;
		}
		else
		{
			m = 1;
		}

		maxDays = getMaxDays(m);
		currYear = (int)yearSpinner.getValue();
		calendar.set(Calendar.MONTH, m - 1);
		calendar.set(Calendar.YEAR, currYear);
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		panel = new JPanel(new GridLayout(7,7));
		panel.setMaximumSize(new Dimension(220, 170));
		panel.setMinimumSize(new Dimension(220, 170));
		panel.setPreferredSize(new Dimension(220, 170));

		panel.add(new DayPanel("Sun"));
		panel.add(new DayPanel("Mon"));
		panel.add(new DayPanel("Tue"));
		panel.add(new DayPanel("Wed"));
		panel.add(new DayPanel("Thu"));
		panel.add(new DayPanel("Fri"));
		panel.add(new DayPanel("Sat"));

		for(int x = 0; x < 6 * 7; x++)
		{
			if(x < calendar.get(Calendar.DAY_OF_WEEK) - 1 || dayOfMonth > maxDays)
			{
				panel.add(new CellPanel(""));
			}
			else
			{
				panel.add(new CellPanel(dayOfMonth, this, (String)monthSpinner.getValue(), (int)yearSpinner.getValue())).addMouseListener(this);
				dayOfMonth++;
			}
		}
		dayOfMonth = 1;

		layout = new GroupLayout(container);
		container.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup().addComponent(monthYearPanel).addComponent(panel));
		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(monthYearPanel));
		vGroup.addGroup(layout.createParallelGroup(BASELINE).addComponent(panel));
		layout.setVerticalGroup(vGroup);
	}

	public void mouseEntered(MouseEvent me)
	{
	}

	public void mouseExited(MouseEvent me)
	{
	}

	//Populates text field, hides calendar
	public void mouseClicked(MouseEvent me)
	{
		field.setText(m + "/" + selectedDay + "/" + currYear);
		databaseDate = (currYear + "/" + m + "/" + selectedDay);
		setVisible(false);
		button.setVisible(true);
	}

	public void mouseReleased(MouseEvent me)
	{
	}

	public void mousePressed(MouseEvent me)
	{
	}
}