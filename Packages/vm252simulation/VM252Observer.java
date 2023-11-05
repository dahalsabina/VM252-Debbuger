package vm252simulation;


import observation.Observer;


public interface VM252Observer extends Observer
{

    void updateAccumulator();
        //
        // Tell this object that the subject it's observing's accumulator has changed state
        //

    void updateProgramCounter();
        //
        // Tell this object that the subject it's observing's program counter
        // has changed state
        //

    void updateMemory(int changeAddress);
        //
        // Tell this object that the subject it's observing's memory byte at
        // address changeAddress has changed state
        //

    void updateStoppedStatus();
        //
        // Tell this object that the subject it's observing's stopped status
        // has changed
        //

    }
