/**
 * Visitation History Panel for RD
 * Date: 03/11/16
 * @author Brandon Ballard & Hanif Mirza
 *
 */
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.Date;
import java.sql.*;
import javax.swing.ImageIcon;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import net.sourceforge.jdatepicker.JDatePanel;
import net.sourceforge.jdatepicker.JDatePicker;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class HistoryPanel extends JPanel implements ActionListener
{
	JTextField				searchField,keywordField;
	JCheckBox 				awardButton,previouslyViewedButton;
	JButton 				searchButton;
	JLabel 					headerLabel,startLebel,endLebel;
	UtilDateModel 			startDateModel,endDateModel;
	JDatePanelImpl 			startDatePanel,endDatePanel;
	JDatePickerImpl 		startDatePicker,endDatePicker;
	SimpleDateFormat 		dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	HistoryPanel()
	{
		headerLabel = new JLabel("Search Visitation History by Selecting a Date Range");
		headerLabel.setBounds(320,10,300,20);

		startLebel = new JLabel("Start Date:");
		startLebel.setBounds(130,40,200,20);

		startDateModel = new UtilDateModel();
		startDatePanel = new JDatePanelImpl(startDateModel);
		startDatePicker = new JDatePickerImpl(startDatePanel);
		startDatePicker.setBounds(200, 40, 200, 40);

		endLebel = new JLabel("End Date:");
		endLebel.setBounds(420,40,200,20);

		endDateModel = new UtilDateModel();
		endDatePanel = new JDatePanelImpl(endDateModel);
		endDatePicker = new JDatePickerImpl(endDatePanel);
		endDatePicker.setBounds(480, 40, 200, 40);

		Date selectedDate = (Date) startDatePicker.getModel().getValue();

		String date = dateFormat.format(selectedDate);
		System.out.println( date);

		this.setLayout(null);
		this.add(headerLabel);
		this.add(startLebel);
		this.add(startDatePicker);
		this.add(endLebel);
		this.add(endDatePicker);

	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == searchButton)
		{

		}
	}

}//end of class
