package demonstration;


import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;


import vm252simulation.VM252Model;
import vm252simulation.VM252Stepper;
import vm252utilities.VM252Utilities;


public class DemonstrationController
{

    //
    // Private Instance Fields
    //

        private VM252Model myMachineState;
        private VM252Stepper myMachineStepper;

    //
    // Public Accessors
    //

        public VM252Model machineState()
        {

            return myMachineState;

            }

        public VM252Stepper machineStepper()
        {

            return myMachineStepper;

            }

    //
    // Private Mutators
    //

        private void setMachineState(VM252Model machineState)
        {

            myMachineState = machineState;

            }

        private void setMachineStepper(VM252Stepper machineStepper)
        {

            myMachineStepper = machineStepper;

            }

    //
    // Public Ctors
    //

        public DemonstrationController(VM252Model simulatedMachine)
        {

            setMachineState(simulatedMachine);

            }

    //
    // Public Operations
    //


        //
        // Public Method
        //     public void loadAndRun(
        //         String objectFileName,
        //         Scanner machineInputStream,
        //         PrintStream machineOutputStream
        //         )
        //
        // Purpose:
        //     Reads the object-code portion of a VM252 object file into a VM252Model
        //     and then simulates execution of the object program
        //
        // Formals:
        //     objectFileName (in) - the name of the VM252 object-code file to be read
        //     machineInputStream (in-out) - the input scanner used to read integers from
        //         when simulating the execution of INPUT instructions
        //     machineOutputStream (in-out) - the output stream used to write integers to
        //         when simulating the execution of OUTPUT instructions and to write input
        //         prompts to when simulating the execution of INPUT instructions
        //
        // Pre-conditions:
        //     machineInputStream is an open the input scanner
        //     machineOutputStream is an open output stream
        //
        // Post-conditions:
        //     Input may have been read from machineInputStream
        //     Output may have been written to machineOutputStream
        //
        // Returns:
        //     none
        //

        public void loadAndRun(
            String objectFileName,
            Scanner machineInputStream,
            PrintStream machineOutputStream
            ) throws IOException
        {

            byte [ ] objectCode
                = VM252Utilities.readObjectCodeFromObjectFile(objectFileName);

            if (objectCode != null) {

                setMachineStepper(
                    new VM252Stepper(machineState(), machineInputStream, machineOutputStream)
                    );

                //
                // Copy the object code bytes into the simulated memory bytes
                // of machineState()
                //

                    for (int address = 0; address < objectCode.length; ++ address)
                        machineState().setMemoryByte(address, objectCode[ address ]);

                //
                // Simulate execution of the object code until the simulated machine
                // executes a STOP instruction
                //

                    while (machineState().stoppedStatus()
                            == VM252Model.StoppedCategory.notStopped)
                        machineStepper().step();

                }

            }

    }
