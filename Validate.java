import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.lang.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Validate
{
    public static void main(String[] args)
    {
    	System.out.println(Validate.validateFullName("") );
    }

	//==============================================================
	public static boolean validateUsername(String username)
	{
		// Username length should be 3 to 15 with any A-Z or a-z or 0-9 or with _-
		String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";
		return username.matches(USERNAME_PATTERN);
	}

	//=====================================================================
	public static boolean validatePassword(String password)
	{
		// Password length should be 6 to 20 with any A-Z or a-z or 0-9 or @#$%_-
		String PASSWORD_PATTERN = "^[A-Za-z0-9@#$%_-]{6,20}$";
		return password.matches(PASSWORD_PATTERN);
	}
	//=====================================================================
	public static boolean validateFirstName(String firstName)
	{
		// First name should contain only upper and lower case alphabets
		return firstName.matches( "[a-zA-Z]*" );
	}
	//=====================================================================
	public static boolean validateLastName(String lastName)
	{
		// Last name should contain only upper and lower case alphabets also spaces ' - are allowed
		return lastName.matches( "[a-zA-z]+([ '-][a-zA-Z]+)*" );
	}
	//=====================================================================
	public static boolean validateFullName(String fullName)
	{
		if(fullName.split(" ").length < 2)
		{
			return false;
		}
		else
		{
			// Full name should contain only upper and lower case alphabets also spaces ' - are allowed
			return fullName.matches( "[[a-zA-z]+([ '-][a-zA-Z]+)*]{3,20}" );
		}
	}
	//=====================================================================
	public static boolean validateEmail(String email)
	{
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		return email.matches(EMAIL_PATTERN);
	}
	//=====================================================================
	public static boolean validatePhoneNumber(String phoneNo)
	{
		String PHONE_PATTERN1 = "\\d{3}-\\d{3}-\\d{4}"; //only xxx-xxx-xxxx
		String PHONE_PATTERN2 = "\\d{10}"; //only xxxxxxxxxx (10 digits)

		if (phoneNo.matches(PHONE_PATTERN1))
			return true;
		else if (phoneNo.matches(PHONE_PATTERN2))
			return true;
		else
			return false;
	}
	//======================================================================
	public static boolean validateDate(String inDate)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		try
		{
			dateFormat.parse(inDate.trim());
		}
		catch (ParseException pe)
		{
			return false;
		}
		return true;
	}
	//=====================================================================
	public static boolean validateNumber(String number)
	{
		//numeric digits only(minimum 1 and maximum 8 digits)
		if (number.matches("\\d{1,8}"))
			return true;
		else
			return false;
	}

}