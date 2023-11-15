package vm252simulation;


import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JOptionPane;

import gui.DebugFrame;

import java.lang.Math;


import vm252architecturespecifications.VM252ArchitectureSpecifications;
import vm252architecturespecifications.VM252ArchitectureSpecifications.Instruction;
import vm252utilities.VM252Utilities;


public class VM252Stepper
{

    //
    // Private Fields
    //

        private VM252Model myMachineState;
        private Scanner myMachineInputStream;
        private PrintStream myMachineOutputStream;

    //
    // Public Accessors
    //

        public VM252Model machineState()
        {

            return myMachineState;

            }

        public Scanner machineInputStream()
        {

            return myMachineInputStream;

            }

        public PrintStream machineOutputStream()
        {

            return myMachineOutputStream;

            }

        private byte [ ] fetchMemoryBytes(int memoryAddress, int numberOfBytes)
        {

            byte [ ] memoryBytes = new byte [ Math.max(0, numberOfBytes) ];

            for (int byteNumber = 0;
                    numberOfBytes > 0;
                    ++ byteNumber, -- numberOfBytes,
                    memoryAddress
                        = VM252ArchitectureSpecifications.nextMemoryAddress(memoryAddress)
                    )

                memoryBytes[ byteNumber ] = machineState().memoryByte(memoryAddress);

            return memoryBytes;

            }

        public int fetchMemoryData(int memoryAddress)
        {

            byte [ ] dataBytes = fetchMemoryBytes(memoryAddress, 2);

            return ((short) (dataBytes[ 0 ] << 8 | dataBytes[ 1 ] & 0xff));

            }

    //
    // Private Mutators
    //

        private void storeMemoryData(int memoryAddress, int data)
        {


            machineState().setMemoryByte(memoryAddress, ((byte) (data >> 8 & 0xff)));
            machineState().setMemoryByte(
                VM252ArchitectureSpecifications.nextMemoryAddress(memoryAddress),
                ((byte) (data & 0xff))
                );

            }

    //
    //  Public Ctor
    //

        public VM252Stepper(
                VM252Model machineState,
                Scanner machineInputStream,
                PrintStream machineOutputStream
                )
        {

            myMachineState = machineState;
            myMachineInputStream = machineInputStream;
            myMachineOutputStream = machineOutputStream;
            }

    //
    // Public Operations
    //

        //
        // Public Method void step()
        //
        // Purpose:
        //     Simulates the execution of a single VM252 instruction
        //
        // Formals:
        //     none
        //
        // Pre-conditions:
        //     none
        //
        // Post-conditions:
        //     The simulated accumulator, program counter, and memory contents of
        //     machineState() has been altered to reflect the execution of the instruction
        //     whose encoding resided in simulated memory at the address in the simulated
        //     program counter
        //
        // Returns:
        //     none
        //

        public String next_instruction(
            boolean specificProgramCounterValue,
            int ProgramCounterValue
        ){

            Instruction currentInstruction;
            Instruction nextInstruction;
            byte [] memory_bytes_next_instruction;
            int instruction_length;

            try {
                if (!specificProgramCounterValue) memory_bytes_next_instruction = fetchMemoryBytes(machineState().programCounter(), 2);
                else memory_bytes_next_instruction = fetchMemoryBytes(ProgramCounterValue, 2);
                currentInstruction
                    = new VM252ArchitectureSpecifications.Instruction(
                    memory_bytes_next_instruction
                        );
                nextInstruction = currentInstruction;
                instruction_length = 2;

            } catch(IllegalArgumentException e) {
                if (!specificProgramCounterValue) memory_bytes_next_instruction = fetchMemoryBytes(machineState().programCounter(), 1);
                else memory_bytes_next_instruction = fetchMemoryBytes(ProgramCounterValue, 1);
                currentInstruction
                    = new VM252ArchitectureSpecifications.Instruction(
                    memory_bytes_next_instruction
                        );
                nextInstruction = currentInstruction;
                instruction_length = 1;
                if (nextInstruction.symbolicOpcode() == "STOP"){
                    return nextInstruction.symbolicOpcode();
                }
            } 
            System.out.println(currentInstruction.symbolicOpcode());

                if (instruction_length == 2) {
                    String symbolic_address = VM252Utilities.addressSymbolHashMap.get((int) memory_bytes_next_instruction[1]);
                    if (symbolic_address == null && nextInstruction.symbolicOpcode() == "SET"){

                           symbolic_address = DebugFrame.simulatedMachine.memoryByte(memory_bytes_next_instruction[1]) + "";
		               // int value= fetchMemoryData(1);
		               // return currentInstruction.symbolicOpcode() + " " + value;
		               // need to get what the value is just like SET something: Able to get the memory adress 
		               // but need to get what is actually stored there
                    }
                    return nextInstruction.symbolicOpcode() + " " + symbolic_address;

                } else{

                    return nextInstruction.symbolicOpcode(); 

        }
    }
                        
        


        public void step() throws IOException
        {

            if (machineState().stoppedStatus() != VM252Model.StoppedCategory.stopped && machineState().stoppedStatus() != VM252Model.StoppedCategory.paused) {

                Instruction currentInstruction;
                int data;
                boolean suppressProgramCounterIncrement;

                //
                // Let currentInstruction be the the instruction whose encoding resides in
                //     simulated memory at the address in the simulated program counter
                //

                    try {

                        currentInstruction
                            = new VM252ArchitectureSpecifications.Instruction(
                               fetchMemoryBytes(machineState().programCounter(), 2)
                               );

                        }
                    catch (IllegalArgumentException exception) {

                        currentInstruction
                            = new VM252ArchitectureSpecifications.Instruction(
                               fetchMemoryBytes(machineState().programCounter(), 1)
                               );

                        }

                //
                // Simulate the execution of currentInstruction
                //

                    suppressProgramCounterIncrement = false;

                    switch (currentInstruction.numericOpcode()) {

                        case VM252ArchitectureSpecifications.LOAD_OPCODE :
                            machineState().setAccumulator(
                                fetchMemoryData(currentInstruction.numericOperand())
                                );
                            break;

                        case VM252ArchitectureSpecifications.SET_OPCODE :
                            machineState().setAccumulator(
                                currentInstruction.numericOperand()
                                );
                            break;

                        case VM252ArchitectureSpecifications.STORE_OPCODE :
                            storeMemoryData(
                                currentInstruction.numericOperand(),
                                machineState().accumulator()
                                );
                            break;

                        case VM252ArchitectureSpecifications.ADD_OPCODE :
                            data = fetchMemoryData(currentInstruction.numericOperand());
                            machineState().setAccumulator(
                                machineState().accumulator() + data
                                );
                            break;

                        case VM252ArchitectureSpecifications.SUBTRACT_OPCODE :
                            data = fetchMemoryData(currentInstruction.numericOperand());
                            machineState().setAccumulator(
                                machineState().accumulator() - data
                                );
                            break;

                        case VM252ArchitectureSpecifications.JUMP_OPCODE :
                            machineState().setProgramCounter(
                                currentInstruction.numericOperand()
                                );
                            suppressProgramCounterIncrement = true;
                            break;

                        case VM252ArchitectureSpecifications.JUMP_ON_ZERO_OPCODE :
                            if (machineState().accumulator() == 0) {
                                machineState().setProgramCounter(
                                    currentInstruction.numericOperand()
                                    );
                                suppressProgramCounterIncrement = true;
                                }
                            break;

                        case VM252ArchitectureSpecifications.JUMP_ON_POSITIVE_OPCODE :
                            if (machineState().accumulator() > 0) {
                                machineState().setProgramCounter(
                                    currentInstruction.numericOperand()
                                    );
                                suppressProgramCounterIncrement = true;
                                }
                            break;

                       case VM252ArchitectureSpecifications.INPUT_OPCODE :

                            boolean valid_input_received = false;
                            Integer int_value = 0;
                            while (!valid_input_received){
                            try {
                                int_value = Integer.parseInt(JOptionPane.showInputDialog(null,"INPUT :"));
                                valid_input_received = true;
                            } catch (NumberFormatException e) {
                                // run the loop again till we get a valid input
                            }
                        }
                            // show the input provided in the events of the gui
                            machineState().setAccumulator(
                                int_value
                                    );
                            DebugFrame.update_event_display();
                            break;

                        case VM252ArchitectureSpecifications.OUTPUT_OPCODE :
                            DebugFrame.output_text.setText(DebugFrame.output_text.getText() + 
                                "OUTPUT: " + machineState().accumulator() + "\n"
                                );
                            machineOutputStream().println(
                                "OUTPUT: " + machineState().accumulator()
                            );
                            machineOutputStream().println();
                            machineOutputStream().flush();
                            break;

                        case VM252ArchitectureSpecifications.NO_OP_OPCODE :
                            break;

                        case VM252ArchitectureSpecifications.STOP_OPCODE :
                            machineState().setStoppedStatus(
                                VM252Model.StoppedCategory.stopped
                                );
                            suppressProgramCounterIncrement = true;
                            break;

                        }

                    if (! suppressProgramCounterIncrement)
                        machineState().setProgramCounter(
                            VM252ArchitectureSpecifications.nextMemoryAddress(
                                machineState().programCounter(),
                                currentInstruction.instructionBytes().length
                                )
                            );

                    }

            }

            public int get_instruction_bytes_length(int programCounter){

                    Instruction currentInstruction;
                    try {

                        currentInstruction
                            = new VM252ArchitectureSpecifications.Instruction(
                               fetchMemoryBytes(programCounter, 2)
                               );

                        }
                    catch (IllegalArgumentException exception) {

                        currentInstruction
                            = new VM252ArchitectureSpecifications.Instruction(
                               fetchMemoryBytes(programCounter, 1)
                               );
                        }

                    return currentInstruction.instructionBytes().length;

            }

    }
