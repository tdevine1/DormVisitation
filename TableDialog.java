/**
 * Dorm visitation TableDialog
 * Description: This class will view the visitation history in a table
 * Date: 03/11/16
 * @author Brandon Ballard & Hanif Mirza
 *
 */

import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.table.TableRowSorter;

public class TableDialog extends JDialog implements ActionListener,DocumentListener
{
    public static void main(String[] x)
    {
		try
		{
			Class.forName( "com.mysql.jdbc.Driver" );
			//Connection connection = DriverManager.getConnection( "jdbc:mysql://localhost/sys", "root", "root");
			Connection connection = DriverManager.getConnection( "jdbc:mysql://johnny.heliohost.org/falcon16_dorm", "falcon16", "fsu2016" );

			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * from Resident ");
			new TableDialog(resultSet);
			statement.close();
			connection.close();
		}
		catch ( Exception e )
		{
			System.out.println( "Exception"+e.getMessage() );
		}

    }
	//----------------------------------------------------------------------------------------------------------
    JButton                     	orderButton;
    JButton                     	cancelButton;
    JTextField 						filterTextField;
    JLabel							filterLabel;
    Container                   	contentPane;
    JTable                      	myJTable;
    JScrollPane                 	scroller;
    JPanel							southButtonPanel;
    JPanel							textFieldPanel;
    ResultSet 						myResultSet;
    DefaultTableModel           	tableModel;
    TableRowSorter<TableModel> 		rowSorter;

    //==================================================================================
    // This constructor is just to view the table and the table is non-editable
    TableDialog(ResultSet urResultSet) throws Exception
    {
		myResultSet = urResultSet;
		ResultSetMetaData metaData = myResultSet.getMetaData();// process query results
		int numberOfColumns = metaData.getColumnCount();

		Vector<Object> columnNames = new Vector<Object>();// columnNames holds the column names of the query result
		Vector<Object> rows = new Vector<Object>();//rows is a vector of vectors, each vector is a vector of values representing a certain row of the query result

		for ( int i = 1; i <= numberOfColumns; i++ )
		{
			columnNames.addElement(metaData.getColumnLabel(i));
	    }

		while ( myResultSet.next() )
		{
			Vector<Object> currentRow = new Vector<Object>();
			for ( int i = 1; i <= numberOfColumns; i++ )
			{
			   currentRow.addElement(myResultSet.getObject(i));
			}
			rows.addElement(currentRow);
		}
		tableModel = new DefaultTableModel(rows, columnNames)
		                 {
							 @Override //Override the method to make all cells non-editable
							 public boolean isCellEditable(int row, int col)
							 {
							 	 return false;
							 }
                         };

		myJTable = new JTable(tableModel);//create new jtable
        myJTable.setFont(new Font("Courier", Font.BOLD,12));
        myJTable.setMinimumSize(new Dimension(10, 10));
        scroller = new  JScrollPane(myJTable);
        myJTable.setPreferredScrollableViewportSize(new Dimension(420, 60));
        myJTable.setRowHeight(22);
        myJTable.getColumnModel().getColumn(1).setMinWidth(100);
		rowSorter = new TableRowSorter<>(myJTable.getModel());
		myJTable.setRowSorter(rowSorter);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

 		filterTextField = new JTextField(20);
 		filterTextField.getDocument().addDocumentListener(this);
    	filterLabel = new JLabel("Search table with any keywords:");
		textFieldPanel = new JPanel(new FlowLayout());
		textFieldPanel.add(filterLabel);
        textFieldPanel.add(filterTextField);

        southButtonPanel=new JPanel(new FlowLayout());
        southButtonPanel.add(cancelButton);
        contentPane=getContentPane();

        contentPane.add( textFieldPanel,BorderLayout.NORTH);
        contentPane.add(scroller,BorderLayout.CENTER);
        contentPane.add(southButtonPanel,BorderLayout.SOUTH);
        setupMainFrame();
        myResultSet.close();
    }
    //============================================================================================================
    public void actionPerformed(ActionEvent e)
    {

		if(e.getSource() == cancelButton)
		{
			this.dispose();//dispose the dialog
		}

	}

	@Override
	public void insertUpdate(DocumentEvent e)
	{
		try
		{
			String text = filterTextField.getText();

			if (text.trim().length() == 0)
			{
				rowSorter.setRowFilter(null);
			}
			else
			{
				rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
			}
		}
		catch ( Exception ee )
		{
			System.out.println( "Exception "+ee.getMessage() );

		}
	}

	@Override
	public void removeUpdate(DocumentEvent e)
	{
		try
		{
			String text = filterTextField.getText();

			if (text.trim().length() == 0)
			{
				rowSorter.setRowFilter(null);
			}
			else
			{
				rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
			}
		}
		catch ( Exception ee )
		{
			System.out.println( "Exception "+ee.getMessage() );
		}
	}


	public void changedUpdate(DocumentEvent e)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}



    //======================================================================================================================
    void setupMainFrame()
    {
        Toolkit tk;
        Dimension d;
        tk = Toolkit.getDefaultToolkit();
        d = tk.getScreenSize();
       	setSize(d.width/2 + d.width/4, d.height/2 + d.width / 6);
	    setLocation((d.width/4 + d.width/4)/4, (d.height/2 + d.width / 6) / 8);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Dorm Visitation");
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setVisible(true);
    }

}//end of class TableDialog

