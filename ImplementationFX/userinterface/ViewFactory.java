package userinterface;

import impresario.IModel;

//==============================================================================
public class ViewFactory {

	public static View createView(String viewName, IModel model) {

		if (viewName.equals("TransactionChoiceView")) {
			return new TransactionChoiceView(model);
		}
		else if (viewName.equals("ScoutView")) {
			return new ScoutView(model);
		}
		else if (viewName.equals("AddTreeView")) {
			return new AddTreeView(model);
		}
		else if (viewName.equals("ModifyScoutView")) {
			return null;//new ModifyScoutView(model);
		}
		else if (viewName.equals("RemoveScoutView")) {
			return null;//new RemoveScoutView(model);
		}
		else if (viewName.equals("ScoutCollectionView")) {
			return new ScoutCollectionView(model);
		}
		else {
			return null;
		}

	}
}