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

		if (transType.equals("AddScoutTransaction"))
		{
			retValue = new AddScoutTransaction();
		}
		else if (transType.equals("ModifyScoutTransaction"))
		{
			retValue = new ModifyScoutTransaction();
		}
		else if (transType.equals("RemoveScoutTransaction"))
		{
			retValue = new RemoveScoutTransaction();
		}
		else if (transType.equals("AddTreeTransaction"))
		{
			retValue = new AddTreeTransaction();
		}
		else if (transType.equals("AddTreeTypeTransaction"))
		{
			retValue = new AddTreeTypeTransaction();
		}
		else if (transType.equals("ModifyTreeTransaction"))
		{
			retValue = new ModifyTreeTransaction();
		}
		else if (transType.equals("RemoveTreeTransaction"))
		{
			retValue = new RemoveTreeTransaction();
		}
		else if (transType.equals("AddTreeTypeTransaction"))
		{
			//retValue = new AddTreeTypeTransaction();
		}

		return retValue;
	}
}
