// specify the package
package model;

// system imports
import java.util.Hashtable;
import java.util.Properties;
import javafx.stage.Stage;
import javafx.scene.Scene;

// project imports
import event.Event;
import impresario.*;
import userinterface.MainStageContainer;
import userinterface.View;
import userinterface.WindowPosition;

/** The abstract base class for all Transactions in the Scout Management System */
//==============================================================
abstract public class Transaction implements IView, IModel
{
	// For Impresario
	protected Properties dependencies;
	protected ModelRegistry myRegistry;

	protected Stage myStage;
	protected Hashtable<String, Scene> myViews;

	// Common fields for all transactions
	protected String transactionErrorMessage;
	protected String successMessage;

	/**
	 * Constructor for this class.
	 */
	//----------------------------------------------------------
	protected Transaction()
	{
		myStage = MainStageContainer.getInstance();
		myViews = new Hashtable<String, Scene>();

		myRegistry = new ModelRegistry("Transaction");
		if(myRegistry == null)
		{
			new Event(Event.getLeafLevelClassName(this), "Transaction",
				"Could not instantiate Registry", Event.ERROR);
		}
		setDependencies();
	}

	//----------------------------------------------------------
	protected abstract void setDependencies();

	//---------------------------------------------------------
	protected abstract Scene createView();

	/**
	 * Template method for executing the transaction
	 */
	//---------------------------------------------------------
	protected void doYourJob()
	{
		try
		{
			Scene newScene = createView();
			swapToView(newScene);
		}
		catch (Exception ex)
		{
			new Event(Event.getLeafLevelClassName(this), "doYourJob",
				"Error in creating View", Event.ERROR);
		}
	}

	/** Called via the IView relationship */
	//----------------------------------------------------------
	public void updateState(String key, Object value)
	{
		stateChangeRequest(key, value);
	}

	/** Register objects to receive state updates. */
	//----------------------------------------------------------
	public void subscribe(String key, IView subscriber)
	{
		myRegistry.subscribe(key, subscriber);
	}

	/** Unregister previously registered objects. */
	//----------------------------------------------------------
	public void unSubscribe(String key, IView subscriber)
	{
		myRegistry.unSubscribe(key, subscriber);
	}

	//-----------------------------------------------------------------------------
	public void swapToView(Scene newScene)
	{
		if (newScene == null)
		{
			System.out.println("Transaction.swapToView(): Missing view for display");
			new Event(Event.getLeafLevelClassName(this), "swapToView",
				"Missing view for display ", Event.ERROR);
			return;
		}

		myStage.setScene(newScene);
		myStage.sizeToScene();
		
		//Place in center
		WindowPosition.placeCenter(myStage);
	}

	// Abstract methods that must be implemented by subclasses
	//-----------------------------------------------------------
	public abstract Object getState(String key);

	//-----------------------------------------------------------
	public abstract void stateChangeRequest(String key, Object value);
}

