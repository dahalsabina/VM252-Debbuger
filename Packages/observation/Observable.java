package observation;


public interface Observable
{

    void attach(Observer anotherObserver);
        //
        // Register anotherObserver as an observer of this object
        //

    void detach(Observer currentObserver);
        //
        // Unregister currentObserver as an observer of this object
        //

    void announceChange();
        //
        // Announce to all observers of this object that this object has changed state
        //

    }
