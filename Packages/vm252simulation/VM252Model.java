package vm252simulation;


import observation.Observer;
import observation.SimpleObservable;
import vm252architecturespecifications.VM252ArchitectureSpecifications;


public class VM252Model extends SimpleObservable implements ObservableVM252
{

    public enum StoppedCategory {
        notStopped,
        stopped
        };

    private int myAccumulator;
    private int myProgramCounter;
    private final byte [ ] myMemory
        = new byte [ VM252ArchitectureSpecifications.MEMORY_SIZE_IN_BYTES ];
    private StoppedCategory myStoppedStatus;

    //
    // Public Accessors
    //

        public int accumulator()
        {

            return myAccumulator;

            }

        public int programCounter()
        {

            return myProgramCounter;

            }

        public byte memoryByte(int address) throws IllegalArgumentException
        {

            if (address < 0
                    || VM252ArchitectureSpecifications.MEMORY_SIZE_IN_BYTES <= address)

                throw
                    new IllegalArgumentException(
                        "Attempt to getch memory byte from illegal memory address " + address
                        );

            else

                return myMemory[ address ];

            }

        public StoppedCategory stoppedStatus()
        {

            return myStoppedStatus;

            }

    //
    // Public Mutators
    //

        public void setAccumulator(int other)
        {

            myAccumulator = ((short) other);

            announceAccumulatorChange();

            }

        public void setProgramCounter(int other) throws IllegalArgumentException
        {

            if (other < 0 || VM252ArchitectureSpecifications.MEMORY_SIZE_IN_BYTES <= other)

                throw
                    new IllegalArgumentException(
                        "Attempt to set program counter to illegal memory address " + other
                        );

            else {

                myProgramCounter = other;

                announceProgramCounterChange();

                };

            }

        public void setMemoryByte(int address, byte other) throws IllegalArgumentException
        {

            if (address < 0
                    || VM252ArchitectureSpecifications.MEMORY_SIZE_IN_BYTES <= address)

                throw
                    new IllegalArgumentException(
                        "Attempt to set memory byte at illegal memory address " + address
                        );

            else {

                myMemory[ address ] = other;

                announceMemoryChange(address);

                }

            }

        public void setStoppedStatus(StoppedCategory other)
        {

            myStoppedStatus = other;

            announceStoppedStatusChange();

            }

    //
    // Public Ctors
    //

        public VM252Model()
        {

            setAccumulator(0);
            setProgramCounter(0);
            setStoppedStatus(StoppedCategory.notStopped);

            }

    //
    // Public Implementations of Observable interface methods
    //

        @Override
        public void announceAccumulatorChange()
        {

            for (Observer currentObserver : observers())

                if (currentObserver instanceof VM252Observer)
                    ((VM252Observer) currentObserver).updateAccumulator();

            }

        @Override
        public void announceProgramCounterChange()
        {

            for (Observer currentObserver : observers())

                if (currentObserver instanceof VM252Observer)
                    ((VM252Observer) currentObserver).updateProgramCounter();

            }

        @Override
        public void announceMemoryChange(int changeAddress)
        {

            for (Observer currentObserver : observers())

                if (currentObserver instanceof VM252Observer)
                    ((VM252Observer) currentObserver).updateMemory(changeAddress);

            }

        @Override
        public void announceStoppedStatusChange()
        {

            for (Observer currentObserver : observers())

                if (currentObserver instanceof VM252Observer)
                    ((VM252Observer) currentObserver).updateStoppedStatus();

            }

    }