package observation;


import java.util.ArrayList;


public class SimpleObservable implements Observable
{

    private ArrayList< Observer > myObservers;

    protected ArrayList< Observer > observers()
    {

        return myObservers;

        }

    public SimpleObservable()
    {

        myObservers = new ArrayList< Observer >();

        }

    @Override
    public void attach(Observer anotherObserver)
    {

        if (anotherObserver != null && ! observers().contains(anotherObserver))
            observers().add(anotherObserver);

        }

    @Override
    public void detach(Observer currentObserver)
    {

        observers().remove(currentObserver);

        }

    @Override
    public void announceChange()
    {

        for (Observer currentObserver : observers())
            currentObserver.update();

        }

    }
