// specify the package
package model;

// system imports
import java.util.Vector;
import javax.swing.JFrame;

// project imports

/** The class containing the TransactionFactory for the Scout Management System */
//==============================================================
public class TransactionFactory
{
	/**
	 * Create the appropriate transaction based on the transaction type
	 */
	//----------------------------------------------------------
	public static Transaction createTransaction(String transType)
		throws Exception
	{
		Transaction retValue = null;

		if (transType.equals("AddScout") == true)
		{
			retValue = new AddScoutTransaction();
		}
		else if (transType.equals("ModifyScout") == true)
		{
			retValue = new ModifyScoutTransaction();
		}
		else if (transType.equals("RemoveScout") == true)
		{
			retValue = new RemoveScoutTransaction();
		}
		else if (transType.equals("AddTree") == true)
		{
			retValue = new AddTreeTransaction();
		}

		return retValue;
	}
}
