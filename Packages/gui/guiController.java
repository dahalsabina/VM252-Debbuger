package gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.AbstractDocument;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;

import vm252architecturespecifications.VM252ArchitectureSpecifications;
import vm252simulation.VM252Model;
import vm252simulation.VM252Stepper;
import vm252utilities.VM252Utilities;

public class guiController {
    //
    // Private Instance Fields
    //

    private VM252Model myMachineState;
    private VM252Stepper myMachineStepper;
    private lineHighlightPrinter myLineHighlightPrinterObject;
    private breakpointHandler myBreakpointHandlerObject;
    private boolean simulation_started;
    private boolean simulation_paused;
    public gui.code_display code_display_object;
    private javax.swing.Timer timer;
    private double myRunSpeed;

    //
    // Public Accessors
    //

    public lineHighlightPrinter lineHighlighPrinterObject() {
        return myLineHighlightPrinterObject;
    }

    public breakpointHandler breakpointHandlerObject() {
        return myBreakpointHandlerObject;
    }

    public VM252Model machineState() {

        return myMachineState;

    }

    public VM252Stepper machineStepper() {

        return myMachineStepper;

    }

    //
    // Private Mutators
    //

    private void setMachineState(VM252Model machineState) {

        myMachineState = machineState;

    }

    public void setRunSpeed(double speed) {
        myRunSpeed = speed;
    }

    private void setMachineStepper(VM252Stepper machineStepper) {

        myMachineStepper = machineStepper;

    }

    public void setCodeDisplayObject(gui.code_display code_display) {

        code_display_object = code_display;
    }

    //
    // Public Ctors
    //

    public guiController(VM252Model simulatedMachine, lineHighlightPrinter lineHighlightPrinterObject,
            breakpointHandler breakpointHandlerObject) {
        this.simulation_started = false;
        this.simulation_paused = false;
        setMachineState(simulatedMachine);
        myLineHighlightPrinterObject = lineHighlightPrinterObject;
        myBreakpointHandlerObject = breakpointHandlerObject;
        myRunSpeed = DebugFrame.getRunSpeedFromSpeedComponent();
        create_timer();

    }

    //
    // Public Operations
    //

    //
    // Public Method
    // public void loadAndRun(
    // String objectFileName,
    // Scanner machineInputStream,
    // PrintStream machineOutputStream
    // )
    //
    // Purpose:
    // Reads the object-code portion of a VM252 object file into a VM252Model
    // and then simulates execution of the object program
    //
    // Formals:
    // objectFileName (in) - the name of the VM252 object-code file to be read
    // machineInputStream (in-out) - the input scanner used to read integers from
    // when simulating the execution of INPUT instructions
    // machineOutputStream (in-out) - the output stream used to write integers to
    // when simulating the execution of OUTPUT instructions and to write input
    // prompts to when simulating the execution of INPUT instructions
    //
    // Pre-conditions:
    // machineInputStream is an open the input scanner
    // machineOutputStream is an open output stream
    //
    // Post-conditions:
    // Input may have been read from machineInputStream
    // Output may have been written to machineOutputStream
    //
    // Returns:
    // none
    //

    private void display_instruction() {

        if (machineState().stoppedStatus() != VM252Model.StoppedCategory.stopped) {
            DebugFrame.instruction_to_be_executed = machineStepper().next_instruction(machineState().programCounter());
            DebugFrame.instruction_Display.setText(DebugFrame.instruction_to_be_executed);
        } else {
            DebugFrame.instruction_Display.setText("");
        }
    }

    public void loadFile(
            String objectFileName,
            Scanner machineInputStream,
            PrintStream machineOutputStream) throws IOException {
        byte[] objectCode = VM252Utilities.readObjectCodeFromObjectFile(objectFileName);

        if (objectCode != null) {

            setMachineStepper(
                    new VM252Stepper(machineState(), machineInputStream, machineOutputStream, this));

            //
            // Copy the object code bytes into the simulated memory bytes
            // of machineState()
            //

            for (int address = 0; address < objectCode.length; ++address)
                machineState().setMemoryByte(address, objectCode[address]);
        }
        Random number_generator = new Random();
        for (int address = objectCode.length; address < VM252ArchitectureSpecifications.MEMORY_SIZE_IN_BYTES; ++address) {
            machineState().setMemoryByte(address, (byte) number_generator.nextInt(256));
        }

    }

    public void stop_timer() {
        timer.stop();
    }

    public void create_timer() {
        timer = new javax.swing.Timer((int) ((2 - myRunSpeed) * 1000), new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (machineState().stoppedStatus() == VM252Model.StoppedCategory.notStopped) {
                    try {
                        machineStepper().step();
                    } catch (IOException e1) {
                        System.out.println(e1);
                    }
                    // This will reset the memory_display_2 JTextArea, so need to show highlighter
                    // and breakpoints again if any
                    code_display_object.display_code_in_human_readable_format();
                    display_instruction();
                    check_if_breakpoints_hit();
                    lineHighlighPrinterObject().updateHighlighter();
                    breakpointHandlerObject().addHighlightsBack();
                } else
                    stop_timer();
            }
        });

    }

    public void run_simulation(
            String objectFileName,
            Scanner machineInputStream,
            PrintStream machineOutputStream,
            String type_of_run) throws IOException {
        stop_timer();
        create_timer();

        if (this.simulation_started == false) {

            this.simulation_started = true;
            this.simulation_paused = false;
            // Simulate execution of the object code until the simulated machine
            // executes a STOP instruction
            //

            if (type_of_run.equals("run")) {
                timer.start();
            }

            else if (type_of_run.equals("next")) {
                do_next_instruction();
                check_if_breakpoints_hit();
                display_instruction();
                lineHighlighPrinterObject().updateHighlighter();
                breakpointHandlerObject().addHighlightsBack();
            }

        } else {
            if (type_of_run.equals("run")) {
                timer.start();
            } else if (type_of_run.equals("next")) {
                do_next_instruction();
                check_if_breakpoints_hit();
                display_instruction();
                lineHighlighPrinterObject().updateHighlighter();
                breakpointHandlerObject().addHighlightsBack();
            }
        }
    }

    public void do_next_instruction() throws IOException {

        if (machineState().stoppedStatus() == VM252Model.StoppedCategory.notStopped) {
            machineStepper().step();
            code_display_object.display_code_in_human_readable_format();
        } else {
        }
    }

    public void check_if_breakpoints_hit() {
        ArrayList<Integer> programCounterBreakpoints = breakpointHandlerObject().programCounterBreakpoints;
        // check if any breakpoints have been
        if (programCounterBreakpoints.contains(machineState().programCounter())) {
            // pause the simulator
            machineState().setStoppedStatus(VM252Model.StoppedCategory.paused);
            // remove breakpoint and update event display
            breakpointHandlerObject().processBreakpoints(machineState().programCounter());
        }
    }
}