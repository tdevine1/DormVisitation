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

public class MyTable extends JTable
{
    /*public static void main(String[] x)
    {
		try
		{
			Class.forName( "com.mysql.jdbc.Driver" );
			//Connection connection = DriverManager.getConnection( "jdbc:mysql://localhost/sys", "root", "root");
			Connection connection = DriverManager.getConnection( "jdbc:mysql://johnny.heliohost.org/falcon16_dorm", "falcon16", "fsu2016" );

			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * from Resident ");
			//new TableDialog(resultSet);
			statement.close();
			connection.close();
		}
		catch ( Exception e )
		{
			System.out.println( "Exception"+e.getMessage() );
		}
    }*/

    ResultSet 						myResultSet;
    DefaultTableModel           	tableModel;
    TableRowSorter<TableModel> 		rowSorter;

    MyTable(ResultSet urResultSet) throws Exception
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

		this.setModel(tableModel);
		setRowHeight(22);
		rowSorter = new TableRowSorter<>(this.getModel());
		setRowSorter(rowSorter);

        myResultSet.close();
    }
}

