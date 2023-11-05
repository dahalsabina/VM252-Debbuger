package vm252simulation;


import observation.Observable;


interface ObservableVM252 extends Observable
{

    void announceAccumulatorChange();
        //
        // Announce to all observers that this object's accumulator has changed
        //

    void announceProgramCounterChange();
        //
        // Announce to all observers that this object's program counter has changed
        //

    void announceMemoryChange(int addressOfChangedByte);
        //
        // Announce to all observers that the cintents of this object's memory cell at
        // address addressOfChangedByte has changed
        //

    void announceStoppedStatusChange();
        //
        // Announce to all observers of this object that this object's stopped status
        // has changed
        //

    }
