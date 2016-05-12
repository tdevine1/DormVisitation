/* * * * * * * * * * *\
 * MyTable.java
 *
 * Description: This is a JTable subclass. We construct it with the ResultSet from the database query.
 *				The ResultSet contains all the column headers and all the rows. With the column headers vector
 * 				and rows vector we construct a DefaultTableModel. And then we set the DefaultTableModel to our Table.
 *				This JTable subclass will be constructed from other classes, whenever we need a JTable object from a
 *				ResultSet.
 *
 * Date: 5/7/16
 * @author Hanif Mirza
\* * * * * * * * * * */

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import java.sql.*;

public class MyTable extends JTable
{
    ResultSet 						myResultSet;
    DefaultTableModel           	tableModel;
    TableRowSorter<TableModel> 		rowSorter;

    MyTable(ResultSet urResultSet) throws Exception
    {
		myResultSet = urResultSet;
		ResultSetMetaData metaData = myResultSet.getMetaData();	// get the mata data from the ResultSet
		int numberOfColumns = metaData.getColumnCount();

		Vector<Object> columnNames = new Vector<Object>();// columnNames holds the column names of the query result
		Vector<Object> rows = new Vector<Object>();//rows is a vector of vectors, each vector is a vector of values representing a certain row of the query result

		// the following loop will add all the column headers to a columnNames vector
		for ( int i = 1; i <= numberOfColumns; i++ )
		{
			columnNames.addElement(metaData.getColumnLabel(i));
	    }

		// the following loop will add all the rows to a rows vector
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
		setRowSorter(rowSorter); // set the RowSorter

        myResultSet.close();
    }
}

