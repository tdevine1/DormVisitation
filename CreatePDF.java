// iText allows to add metadata to the PDF which can be viewed in your Adobe Reader under File -> Properties


import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.*;
import javax.swing.*;
import java.sql.*;
import java.io.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class CreatePDF
{

	public static void main(String[] args)
	{
		String	startDate = "'"+"2016/1/1"+"'";
		String  endDate = "'"+"2017/4/10"+"'";

		String SQL_Query =   " Select v.guest_name as 'Guest Name',v.guest_ID_type as 'ID Type',CONCAT(r.first_name,\" \",r.last_name) as 'Resident Name',"
							+ " r.room_number as 'Room Number', DATE_FORMAT(v.visitation_date,'%m/%d/%Y') as 'Date',TIME_FORMAT(v.time_in, '%h:%i%p')as 'Time in',TIME_FORMAT(v.time_out, '%h:%i%p')as 'Time out',"
							+ " v.overnight_status as 'Overnight', CONCAT(e.first_name,\" \",e.last_name) as 'DM/RA Name' "

							+ " From Visitation_Detail v, Resident r,Employee e"
							+ " Where v.visitation_date between "+startDate+" and "+endDate+" and v.time_out is not null and v.residentID = r.userID and v.empID = e.userID ;" ;

		try
		{
			Class.forName( "com.mysql.jdbc.Driver" );
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/dorm", "root", "password");// Hanif's Server
			Statement statement = connection.createStatement();

			MyTable historyTable = new MyTable(statement.executeQuery(SQL_Query));
			new CreatePDF(historyTable,"Visitation Details" );

		}
		catch ( Exception e )
		{
			System.out.println( "Exception "+e.getMessage() );
		}

	}

	JFileChooser 	fileChooser;
	MyTable			table;
	String 			title;
	PdfPTable 		pdfTable;
	PdfPCell 		cell;
	Document 		document;
	Font 			titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
	Font       		tableHeadFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL,BaseColor.WHITE);
	Font       		tableCellFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL,BaseColor.BLACK);
	String 			fileName;

	CreatePDF(MyTable table, String title)
	{
		try
		{
			this.table = table;
			this.title = title;
			fileChooser = new JFileChooser();
    		FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Documents","pdf");
    		fileChooser.setFileFilter(filter);
			int result = fileChooser.showOpenDialog(null);

			if (result == JFileChooser.APPROVE_OPTION)
			{
				File selectedFile = fileChooser.getSelectedFile();
				fileName = selectedFile.getAbsolutePath();

				if ( !fileName.trim().toLowerCase().endsWith(".pdf") )
				{
					fileName = fileName + ".pdf";
				}
				//System.out.println(fileName);
				document = new Document();
				PdfWriter.getInstance(document, new FileOutputStream(fileName));
				document.open();
				addTable();
				document.close();
			}

		}
		catch (Exception e)
		{
			System.out.println( "Exception "+e.getMessage() );
			e.printStackTrace();
		}
	}

	void addTable()throws Exception
	{
		Paragraph titlePara = new Paragraph(title, titleFont);
		titlePara.setAlignment(Element.ALIGN_CENTER);
		document.add(titlePara); // add the title to the document
		document.add(new Paragraph("\n"));	// make a new line

		int columnCount = table.getColumnCount();
		int rowCount = table.getRowCount();

		pdfTable = new PdfPTable(columnCount);
		pdfTable.setWidthPercentage(100);
        //float[] columnWidths = new float[] {30f, 10f, 30f, 10f,15f, 10f, 10f, 8f,30f};
        //pdfTable.setWidths(columnWidths);

		for ( int i = 0; i < columnCount; i++ )
		{
        	cell = new PdfPCell(new Phrase( table.getColumnName(i), tableHeadFont));
        	cell.setBackgroundColor(new BaseColor(1, 0.1f, 0.1f).darker().darker());
			pdfTable.addCell( cell );	// add the column header
		}

		for(int i=0;i<rowCount;i++)
		{
			for ( int j = 0;j < columnCount; j++ )
			{
				String cellValue = table.getModel().getValueAt(i, j).toString();
        		cell = new PdfPCell(new Phrase(cellValue, tableCellFont));
				pdfTable.addCell( cell ); // add every cell in a row
			}
		}

		document.add(pdfTable);
	}

}// end of class