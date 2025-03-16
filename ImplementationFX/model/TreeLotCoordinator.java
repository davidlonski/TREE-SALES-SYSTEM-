package model;

// system imports

// project imports
import impresario.IModel;
import impresario.IView;


public class TreeLotCoordinator implements IView, IModel {


    public TreeLotCoordinator() {}


    @Override
    public Object getState(String key) {
        return null;
    }

    @Override
    public void subscribe(String key, IView subscriber) {

    }

    @Override
    public void unSubscribe(String key, IView subscriber) {

    }

    @Override
    public void stateChangeRequest(String key, Object value) {

    }

    @Override
    public void updateState(String key, Object value) {

    }
}
