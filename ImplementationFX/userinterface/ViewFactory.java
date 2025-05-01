package userinterface;

import impresario.IModel;

public class ViewFactory {

	public static View createView(String viewName, IModel model) {

		if (viewName.equals("TransactionChoiceView")) {
			return new TransactionChoiceView(model);
		}
		else if (viewName.equals("ScoutView")) {
			return new ScoutView(model);
		}
		else if (viewName.equals("AddScoutView")) {
			return new AddScoutView(model);
		}
		else if (viewName.equals("AddTreeView")) {
			return new AddTreeView(model);
		}
		else if (viewName.equals("ModifyScoutView")) {
			return new ModifyScoutView(model, null);
		}
		else if (viewName.equals("RemoveScoutView")) {
			return new RemoveScoutView(model, null);
		}
		else if (viewName.equals("ScoutCollectionView")) {
			return new ScoutCollectionView(model, null);
		}
		else if (viewName.equals("ScoutSearchView")) {
			return new ScoutSearchView(model); // This is the search view we want to show before displaying the ScoutCollectionView.
		}
		else if (viewName.equals("TreeSearchView")) {
			return new TreeSearchView(model);
		}
		else if (viewName.equals("TreeCollectionView")) {
			return new TreeCollectionView(model, null);
		}
		else if (viewName.equals("ModifyTreeView")) {
			return new ModifyTreeView(model, null);
		}
		else if (viewName.equals("RemoveTreeView")) {
			return new RemoveTreeView(model, null);
		}
		else {
			return null;
		}
	}
}
