package userinterface;

import impresario.IModel;
import userinterface.ScoutViews.*;
import userinterface.TreeViews.*;
import userinterface.TreeTypeViews.*;

public class ViewFactory {

	public static View createView(String viewName, IModel model) {

		View view = null;

		if (viewName.equals("TransactionChoiceView")) 		
			view = new TransactionChoiceView(model);
		else if (viewName.equals("AddScoutView")) 
			view = new AddScoutView(model);
		else if (viewName.equals("AddTreeView")) 
			view = new AddTreeView(model);
		else if (viewName.equals("AddTreeTypeView")) 
			view = new AddTreeTypeView(model);
		else if (viewName.equals("ModifyScoutView")) 
			view = new ModifyScoutView(model, null);
		else if (viewName.equals("RemoveScoutView")) 
			view = new RemoveScoutView(model, null);
		else if (viewName.equals("ScoutCollectionView")) 
			view = new ScoutCollectionView(model, null);
		else if (viewName.equals("ScoutSearchView")) 
			view = new ScoutSearchView(model);
		else if (viewName.equals("TreeSearchView")) 
			view = new TreeSearchView(model);
		else if (viewName.equals("TreeCollectionView")) 
			view = new TreeCollectionView(model, null);
		else if (viewName.equals("ModifyTreeView")) 
			view = new ModifyTreeView(model, null);
		else if (viewName.equals("RemoveTreeView")) 
			view = new RemoveTreeView(model, null);
		else 
			throw new IllegalArgumentException("Invalid view name: " + viewName);

		return view;
	}
}
