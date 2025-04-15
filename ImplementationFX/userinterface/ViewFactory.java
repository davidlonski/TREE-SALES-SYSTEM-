package userinterface;

import impresario.IModel;

//==============================================================================
public class ViewFactory {

	public static View createView(String viewName, IModel model) {
		if (viewName.equals("ScoutView")) {
			return new ScoutView(model);
		}
		else if (viewName.equals("TransactionChoiceView")) {
			return new TransactionChoiceView(model);
		}
		else if (viewName.equals("AddTreeView")) {
			return new AddTreeView(model);
		}
		else if (viewName.equals("ModifyScoutView")) {
			return new ModifyScoutView(model);
		}
		else if (viewName.equals("RemoveScoutView")) {
			return new RemoveScoutView(model);
		}
		else if (viewName.equals("ScoutCollectionView")) {
			return new ScoutCollectionView(model);
		}
		else if (viewName.equals("TreeView")) {
			return new TreeView(model);
		}
		else {
			return null;
		}

	}
}