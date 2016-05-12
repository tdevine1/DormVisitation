/* * * * * * * * * * *\
 * MyException.java
 * Description: This is a subclass of Exception, we will construct it with a error message/warning message string.
 *              Then we can get the string from subclass object's getMessage() method.
 *
 * Date: 5/7/16
 * @author Hanif Mirza
\* * * * * * * * * * */

public class MyException extends Exception
{
    public MyException(String message)
    {
        super(message);
    }
}