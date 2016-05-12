/* * * * * * * * * * *\
 * ImportExcel.java
 * Description: This class allows the user to populate the resident section in database by importing an excel file. To import excel, the resident
 * 				director can drag and drop the excel file on AdminGUI or can select the file from a file chooser. If the excel file isn't in
 *				correct format than it will give a warning message with required format. Also, any row of an excel file has invalid information than
 *				it will just skip the row and go the next row. At last, it will give you a message saying how many rows were successfully added
 *				to the database.
 *
 * Date: 5/7/16
 * @author Brandon Ballard & Hanif Mirza
\* * * * * * * * * * */

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.Iterator;
import java.io.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImportExcel
{
	String	 			residentID,roomNo, dormName, firstName, lastName, email, phone,gender,lockoutNo, fileName;
	Statement 			statement;
	int 				confirmationNo, totalImports, successfulImports, result, numCols, currRow;
	File 				selectedFile;
	FileInputStream		file;
	long 				longID, longPhone, longRoom, longLockout;
	boolean 			skipResident;
	JFileChooser 		fileChooser;
	XSSFWorkbook		workbook;
	XSSFSheet 			sheet;
	Iterator<Row> 		rowIterator;
	Row 				row;
	Cell 				cell;
	Iterator<Cell> 		cellIterator;

	//Written by Hanif Mirza, constructor for importing excel from FileChooser
	ImportExcel(Statement statement)
	{
		this.statement = statement;
		totalImports = 0;
		successfulImports = 0;
		currRow = 0;
		fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Documents","xlsx");
		fileChooser.setFileFilter(filter);
		result = fileChooser.showOpenDialog(null);

		try
		{
			if(result == JFileChooser.APPROVE_OPTION)
			{
				selectedFile = fileChooser.getSelectedFile();
				fileName = selectedFile.getAbsolutePath();
				file = new FileInputStream(new File(fileName));
				workbook = new XSSFWorkbook (file);
				importExcel(workbook);
			}
		}
		catch(Exception exception)
		{
			JOptionPane.showMessageDialog(null, exception.getMessage());
		}
	}

	//Written by Brandon Ballard, constructor for importing excel file by drag and drop
	ImportExcel(Statement statement, File selectedFile)
	{
		this.statement = statement;
		totalImports = 0;
		successfulImports = 0;
		currRow = 0;

		try
		{
			fileName = selectedFile.getAbsolutePath();
			file = new FileInputStream(new File(fileName));
			workbook = new XSSFWorkbook (file);
			importExcel(workbook);
		}
		catch(Exception exception)
		{
			JOptionPane.showMessageDialog(null, exception.getMessage());
		}
	}

	// Written by Hanif Mirza & Brandon Ballard. This function will add all the residents on excel file to the database
	void importExcel(XSSFWorkbook workbook)
	{
		sheet = workbook.getSheetAt(0); // get the first sheet from excel file
		numCols = sheet.getRow(0).getPhysicalNumberOfCells(); // get the number of columns on the excel file, it should be 9

		if(numCols == 9)
		{
			rowIterator = sheet.iterator();
			rowIterator.next(); // skip the first row, which is column header

			while(rowIterator.hasNext()) // get each row from the excel file
			{
				try
				{
					totalImports++;
					row = rowIterator.next();
					currRow++;
					cellIterator = row.cellIterator();

					while(cellIterator.hasNext())// get each cell from a single row
					{
						cell = cellIterator.next();
						cell.setCellType(Cell.CELL_TYPE_STRING); // set the cell type to string

						try
						{
							switch(cell.getColumnIndex())
							{
								case 0:
										residentID = cell.getStringCellValue().trim();
										if (!Validate.validateNumber2(residentID) )
										{
											throw new IllegalStateException(); // throw exception if it isn't a valid number
										}
										break;
								case 1:
										firstName = cell.getStringCellValue().trim();
										break;
								case 2:
										lastName = cell.getStringCellValue().trim();
										break;
								case 3:
										gender = cell.getStringCellValue().trim();
										break;
								case 4:
										email = cell.getStringCellValue().trim();
										if(!Validate.validateEmail(email))
										{
											throw new IllegalStateException(); // throw exception if it isn't a valid email
										}
										break;
								case 5:
										phone = cell.getStringCellValue().trim();
										break;
								case 6:
										dormName = cell.getStringCellValue().trim();
										break;
								case 7:
										roomNo = cell.getStringCellValue().trim();
										if(!Validate.validateNumber2(roomNo))
										{
											throw new IllegalStateException();// throw exception if it isn't a valid number
										}
										break;
								case 8:
										lockoutNo = cell.getStringCellValue().trim();
										if(!Validate.validateNumber2(lockoutNo))
										{
											throw new IllegalStateException();// throw exception if it isn't a valid number
										}
										break;
								default:
							}
						}
						catch(IllegalStateException ise)
						{
							JOptionPane.showMessageDialog(null, "Row " + (currRow + 1) + ", Column " + (cell.getColumnIndex() + 1) + " in Excel sheet contains an illegal value.", "Skipping Resident" , JOptionPane.ERROR_MESSAGE);
							skipResident = true;
						}
						catch(Exception e)
						{
							JOptionPane.showMessageDialog(null, "Row " + (currRow + 1) + ", Column " + (cell.getColumnIndex() + 1) + " in Excel sheet contains an illegal value.", "Skipping Resident" , JOptionPane.ERROR_MESSAGE);
							skipResident = true;
						}
					}

					if(skipResident == false)
					{
						String	sql = "INSERT INTO Resident(userID,first_name,last_name,gender,email,phone,dorm_name,room_number,number_of_lockouts)"
							 	  + " VALUES ("+ "'"+residentID+"'" +","+ "'"+firstName+"'" +","+ "'"+lastName+"'" +","+ "'"+gender+"'" +","+ "'"+email+"'" +","
								   + "'"+phone+"'" +","+  "'"+dormName+"'" + ","+roomNo+","+lockoutNo+")";

						confirmationNo = statement.executeUpdate(sql); // add the resident to the database if all the fields are valid
						successfulImports++;
					}
					else
					{
						skipResident = false;
					}
				}
				catch(SQLException sqlException)
				{
					JOptionPane.showMessageDialog(null, "Resident with ID: " + residentID + " already exists in database", "Skipping Resident" , JOptionPane.ERROR_MESSAGE);
				}
			}

			JOptionPane.showMessageDialog(null, "Successfully imported " + successfulImports + " out of " + totalImports + " residents.", "Import Complete" , JOptionPane.INFORMATION_MESSAGE);
		}
		else
		{
			//If the imported excel file has an invalid format, a JOptionPane will show the user what the format needs to be.
			JOptionPane.showMessageDialog(null,"This Excel file is using an invalid format \n \n"
			+ "Excel sheet should have 9 columns containg the following: \n"
			+ "      	1) ID number \n"
			+ "      	2) First name \n"
			+ "      	3) Last name \n"
			+ "      	4) Gender \n"
			+ "      	5) Email \n"
			+ "      	6) Phone number \n"
			+ "      	7) Resident hall name \n"
			+ "      	8) Room number \n"
			+ "      	9) Number of lock outs \n"
			+ "", "ERROR",JOptionPane.ERROR_MESSAGE);
		}
	}
}