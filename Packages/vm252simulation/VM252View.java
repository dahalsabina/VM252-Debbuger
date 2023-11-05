package vm252simulation;


import observation.SimpleObserver;
import vm252architecturespecifications.VM252ArchitectureSpecifications;


//
// This class is intended solely for use as a superclass from which to derive specialized
// views of VM252Model's.
//


public class VM252View extends SimpleObserver implements VM252Observer
{

    //
    // Public Method public void update()
    //
    // Purpose:
    //     Updates all aspects of a VM252View, including *all* bytes of memory.
    //     This method should rarely, if ever, be called (perhaps only just after a view is
    //     attached to a VM252Model to initialize the view).
    //
    // Formals:
    //     none
    //
    // Pre-conditions:
    //     none
    //
    // Post-conditions:
    //     none
    //
    // Returns:
    //     none
    //
    // Note
    //

    @Override
    public void update()
    {

        updateAccumulator();

        updateProgramCounter();

        for (int address = 0;
                address < VM252ArchitectureSpecifications.MEMORY_SIZE_IN_BYTES;
                ++ address)
            updateMemory(address);

        }

    @Override
    public void updateAccumulator()
    {

        }

    @Override
    public void updateProgramCounter()
    {

        }

    @Override
    public void updateMemory(int changeAddress)
    {

        }

    @Override
    public void updateStoppedStatus()
    {

        }

    }
