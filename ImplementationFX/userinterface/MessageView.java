// tabs=4
//************************************************************
//	COPYRIGHT 2010/2015 Sandeep Mitra and Students, The
//    College at Brockport, State University of New York. -
//	  ALL RIGHTS RESERVED
//
// This file is the product of The College at Brockport and cannot
// be reproduced, copied, or used in any shape or form without
// the express written consent of The College at Brockport.
//************************************************************
//
// specify the package
package userinterface;

// system imports
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * A UI element for displaying messages to the user.
 * Supports info, success, and error messages in different colors.
 */
public class MessageView extends Text
{
	// Constructor
	public MessageView(String initialMessage)
	{
		super(initialMessage);
		setFont(Font.font("Helvetica", FontWeight.BOLD, 16));
		setFill(Color.BLUE); // Default to info color
		setTextAlignment(TextAlignment.LEFT);
	}

	/**
	 * Display an informational message (blue text).
	 */
	public void displayMessage(String message)
	{
		setFill(Color.BLUE);
		setText(message);
	}

	/**
	 * Display a success message (green text).
	 */
	public void displaySuccessMessage(String message)
	{
		setFill(Color.GREEN);
		setText(message);
	}

	/**
	 * Display an error message (red text).
	 */
	public void displayErrorMessage(String message)
	{
		setFill(Color.RED);
		setText(message);
	}

	/**
	 * Clear the message.
	 */
	public void clearMessage()
	{
		setText("");
	}

	// For compatibility with older code that uses this:
	public void clearErrorMessage()
	{
		clearMessage();
	}
}
