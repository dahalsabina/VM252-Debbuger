package demonstration;


import java.io.IOException;
import java.util.Scanner;


import vm252simulation.VM252Model;
import vm252simulation.VM252View;


class AccumulatorPrinter extends VM252View
{
    
    private final VM252Model myModel;
    
    public AccumulatorPrinter(VM252Model model)
    {       
        myModel = model;       
        }
    
    @Override
    public void updateAccumulator()
    {       
        System.out.println("accumulator is now " + myModel.accumulator());        
        }
    
    }


class ProgramCounterPrinter extends VM252View
{
    
    private final VM252Model myModel;
    
    public ProgramCounterPrinter(VM252Model model)
    {        
        myModel = model;        
        }
    
    @Override
    public void updateProgramCounter()
    {        
        System.out.println("program counter is now " + myModel.programCounter());        
        }
    
    }


class MemoryBytePrinter extends VM252View
{
    
    private final VM252Model myModel;
    
    public MemoryBytePrinter(VM252Model model)
    {        
        myModel = model;        
        }
    
    @Override
    public void updateMemory(int address)
    {        
        System.out.printf("memory byte at address %d is now %02x\n", address, myModel.memoryByte(address));        
        }
    
    }


class StopAnnouncer extends VM252View
{
    
    private final VM252Model myModel;
    
    public StopAnnouncer(VM252Model model)
    {        
        myModel = model;        
        }
    
    @Override
    public void updateStoppedStatus()
    {        
        System.out.printf(
            "machine stops with accumulator %d and program counter %d\n",
                myModel.accumulator(),
                myModel.programCounter()
            );        
        }
    
    }


public class Demonstration
{

    public static void main(String [ ] commandLineArguments) throws IOException
    {

        Scanner inputStream = new Scanner(System.in);
        
        VM252Model simulatedMachine = new VM252Model();
        
        simulatedMachine.attach(new AccumulatorPrinter(simulatedMachine));
        simulatedMachine.attach(new ProgramCounterPrinter(simulatedMachine));
        simulatedMachine.attach(new MemoryBytePrinter(simulatedMachine));
        simulatedMachine.attach(new StopAnnouncer(simulatedMachine));

        DemonstrationController simulator = new DemonstrationController(simulatedMachine);

        String objectFileName;

        System.out.println("Enter the name of a VM252 object file to run:");
        objectFileName = inputStream.next();

         simulator.loadAndRun(objectFileName, inputStream, System.out);

        }

    }
