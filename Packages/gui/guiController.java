package gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import vm252architecturespecifications.VM252ArchitectureSpecifications;
import vm252architecturespecifications.VM252ArchitectureSpecifications.Instruction;
import vm252simulation.VM252Model;
import vm252simulation.VM252Stepper;
import vm252simulation.VM252Model.StoppedCategory;
import vm252utilities.VM252Utilities;


public class guiController
{

    //
    // Private Instance Fields
    //

        private VM252Model myMachineState;
        private VM252Stepper myMachineStepper;
        private lineHighlightPrinter myLineHighlightPrinterObject;
        private boolean simulation_started;
        private boolean simulation_paused;
        public code_display code_display_object;
        private javax.swing.Timer timer;
        private double myRunSpeed;
        
    //
    // Public Accessors
    //

        public lineHighlightPrinter lineHighlighPrinterObject(){
            return myLineHighlightPrinterObject;
        }
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


        public void setRunSpeed(double speed){
            myRunSpeed = speed;
        }

        private void setMachineStepper(VM252Stepper machineStepper)
        {

            myMachineStepper = machineStepper;

            }

    //
    // Public Ctors
    //

        public guiController(VM252Model simulatedMachine, lineHighlightPrinter lineHighlightPrinterObject)
        {
            this.simulation_started = false;
            this.simulation_paused = false;
            setMachineState(simulatedMachine);
            myLineHighlightPrinterObject = lineHighlightPrinterObject;
            myRunSpeed = DebugFrame.getRunSpeedFromSpeedComponent();
            create_timer();

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

        private void display_instruction(){

            if (machineState().stoppedStatus() != VM252Model.StoppedCategory.stopped){
            DebugFrame.instruction_to_be_executed = machineStepper().next_instruction(false, 0);
            DebugFrame.instruction_Display.setText(DebugFrame.instruction_to_be_executed);
            } else {
            DebugFrame.instruction_Display.setText("");
            }
        }

        public void loadFile(
            String objectFileName,
            Scanner machineInputStream,
            PrintStream machineOutputStream
        ) throws IOException{
            byte [ ] objectCode
                = VM252Utilities.readObjectCodeFromObjectFile(objectFileName);

            if (objectCode != null) {

            setMachineStepper(
                new VM252Stepper(machineState(), machineInputStream, machineOutputStream, this)
                    );

                //
                // Copy the object code bytes into the simulated memory bytes
                // of machineState()
                //

                for (int address = 0; address < objectCode.length; ++ address)
                        machineState().setMemoryByte(address, objectCode[ address ]);
           
                code_display_object = this.new code_display();
                code_display_object.display_code_in_human_readable_format();}

        }

        public void stop_timer(){
            timer.stop();
        }

        public void create_timer(){
            timer = new javax.swing.Timer((int) ((2-myRunSpeed) * 1000), new ActionListener() {
                            public void actionPerformed(ActionEvent e) {

                                    if (machineState().stoppedStatus()
                            == VM252Model.StoppedCategory.notStopped){
                                    try {
                                    machineStepper().step();
                                    }   catch (IOException e1) {
                                        System.out.println(e1);
                                }
                                    display_instruction();
                                    lineHighlighPrinterObject().updateHighlighter();
                                }
                                    else stop_timer();}
                    });

        }

        public void run_simulation(
            String objectFileName,
            Scanner machineInputStream,
            PrintStream machineOutputStream,
            String type_of_run
            ) throws IOException
        {
            stop_timer();
            create_timer();

            if (this.simulation_started == false){
    
            this.simulation_started = true;
            this.simulation_paused = false;
                // Simulate execution of the object code until the simulated machine
                // executes a STOP instruction
                //

                if (type_of_run.equals("run")) {
                        timer.start();
                    }

                else if (type_of_run.equals("next")){
                    do_next_instruction();
                    display_instruction();
                    lineHighlighPrinterObject().updateHighlighter();
                }

                }
            else {
                if (type_of_run.equals("run")) {
                    timer.start();
                   }
                else if (type_of_run.equals("next")){
                    do_next_instruction();
                    display_instruction();
                    lineHighlighPrinterObject().updateHighlighter();
                }
                }}

        public void do_next_instruction() throws IOException
            {

                if (machineState().stoppedStatus()
                            == VM252Model.StoppedCategory.notStopped)
                        {machineStepper().step();
                } else {
                }
                }
        
        // nested class
        public class code_display{

            //ctor 
            public code_display(){

            }

            public void display_code_in_human_readable_format(){
                DebugFrame.input_code_area.setText("");
                int programCounter = 0;
                String instruction = machineStepper().next_instruction(true, programCounter);
                Instruction raw_instruction = machineStepper().get_raw_instruction(programCounter);
                int instruction_length_in_bytes = machineStepper().get_instruction_bytes_length(programCounter);

                DebugFrame.input_code_area.append(programCounter + "    " + instruction + "\n");

                // need to deal with LOAD null which is for variable declaration values
                while (raw_instruction.symbolicOpcode() != "STOP"){

                programCounter = VM252ArchitectureSpecifications.nextMemoryAddress(programCounter,
                instruction_length_in_bytes);
                instruction_length_in_bytes = machineStepper().get_instruction_bytes_length(programCounter);
                instruction = machineStepper().next_instruction(true, programCounter);
                raw_instruction = machineStepper().get_raw_instruction(programCounter);

                String tmp_instruction; 
                if (instruction.endsWith("LOAD null")){

                    byte [ ] dataBytes = machineStepper().fetchMemoryBytes(programCounter, 2);
                    int data = ((short) (dataBytes[ 0 ] << 8 | dataBytes[ 1 ] & 0xff));
                    tmp_instruction = VM252Utilities.addressSymbolHashMap.get(programCounter) + ": " + data;

                } 
                else if (VM252Utilities.addressSymbolHashMap.get(programCounter) != null) {
                    tmp_instruction = VM252Utilities.addressSymbolHashMap.get(programCounter) + ": " + instruction;
                } else {
                    tmp_instruction = instruction;
                }

                DebugFrame.input_code_area.append(programCounter + "    " + tmp_instruction + "\n");
                //DebugFrame.input_code_area.append(programCounter + "    " + instruction + "\n");

                }
            }
        }
    }

