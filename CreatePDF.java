/* * * * * * * * * * *\
 * CreatePDF.java
 * Description: This class will print a JTable to pdf. In the constructor we passed the JTable subclass and title of the pdf file.
 *				It will open a file chooser and let the user enter the pdf file name and save it anywhere on the computer.
 *				The external jar file was used from iText. It provided all the methods to convert a JTable to pdf.
 *				iText also allows to add metadata to the PDF which can be viewed in your Adobe Reader under File -> Properties.
 *
 * Date: 5/7/16
 * @author Hanif Mirza
\* * * * * * * * * * */


import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javax.swing.*;
import java.io.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class CreatePDF
{
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

	// In the constructor we passed the JTable subclass and title of the pdf file
	CreatePDF(MyTable table, String title)
	{
		try
		{
			this.table = table;
			this.title = title;
			fileChooser = new JFileChooser(); // let the user choose a file name for the pdf
    		FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Documents","pdf");
    		fileChooser.setFileFilter(filter);
			int result = fileChooser.showOpenDialog(null);

			if (result == JFileChooser.APPROVE_OPTION)
			{
				File selectedFile = fileChooser.getSelectedFile();
				fileName = selectedFile.getAbsolutePath();

				if ( !fileName.trim().toLowerCase().endsWith(".pdf") )
				{
					fileName = fileName + ".pdf"; // if file name doesn't contain .pdf at last then append it with .pdf
				}
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

		// The following loop will add all the column headers
		for ( int i = 0; i < columnCount; i++ )
		{
        	cell = new PdfPCell(new Phrase( table.getColumnName(i), tableHeadFont));
        	cell.setBackgroundColor(new BaseColor(1, 0.1f, 0.1f).darker().darker());
			pdfTable.addCell( cell );	// add each cell of column headers
		}

		// The following loop will add every rows in the table
		for(int i=0;i<rowCount;i++)
		{
			for ( int j = 0;j < columnCount; j++ )
			{
				int row = table.convertRowIndexToModel(i);
				String cellValue = table.getModel().getValueAt(row, j).toString();
        		cell = new PdfPCell(new Phrase(cellValue, tableCellFont));
				pdfTable.addCell( cell ); // add each cell of a row
			}
		}
		document.add(pdfTable);
	}
}