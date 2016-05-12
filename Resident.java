/* * * * * * * * * * *\
 * Resident.java
 *
 * Description: This class just contains a hashtable. The key of the hashtable is the resident's unique user ID and
 *				the value is the resident's full name. So we map the resident's full name with resident's user ID.
 *				This hashtable will the used when the desk monitor try to finds all the resident in a same room by
 *				menually entering the room number.
 *
 * Date: 5/7/16
 * @author Hanif Mirza
\* * * * * * * * * * */

import java.util.Hashtable;

class Resident
{
	Hashtable<String,String> 	residentHashtable = new Hashtable<String, String>();
}
