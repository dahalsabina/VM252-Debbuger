package vm252architecturespecifications;


import java.util.HashMap;
import java.util.NoSuchElementException;


public class VM252ArchitectureSpecifications
{

    //
    // Public Class Constants
    //

        public static final int ADDRESS_SIZE_IN_BITS = 13;
        public static final int CONSTANT_OPERAND_SIZE_IN_BITS = 12;
        public static final int MEMORY_SIZE_IN_BYTES = 8192;

        public static final int LOAD_OPCODE = 0;
        public static final int STORE_OPCODE = 1;
        public static final int ADD_OPCODE = 2;
        public static final int SUBTRACT_OPCODE = 3;
        public static final int JUMP_OPCODE = 4;
        public static final int JUMP_ON_ZERO_OPCODE = 5;
        public static final int JUMP_ON_POSITIVE_OPCODE = 6;
        public static final int SET_OPCODE = 14;
        public static final int INPUT_OPCODE = 60;
        public static final int OUTPUT_OPCODE = 61;
        public static final int NO_OP_OPCODE = 62;
        public static final int STOP_OPCODE = 63;

    //
    // Public Class Methods
    //

        public static int nextMemoryAddress(int address)
        {

            return (address + 1) % MEMORY_SIZE_IN_BYTES;

            }


        public static int nextMemoryAddress(int address, int increment)
        {

            return (address + increment) % MEMORY_SIZE_IN_BYTES;

            }

    //
    // Private Inner Classes
    //

        private enum InstructionCategory {
            instructionWithAddressOperand,
            instructionWithConstantOperand,
            instructionWithNoOperand
            }

        private static class InstructionInfo
        {

            public String symbolicOpcode;
            public Integer numericOpcode;
            public InstructionCategory category;

            public InstructionInfo(
                    String initialSymbolicOpcode,
                    Integer initialNumericOpcode,
                    InstructionCategory initialCategory
                    )
            {

                symbolicOpcode = initialSymbolicOpcode;
                numericOpcode = initialNumericOpcode;
                category = initialCategory;

                }

            }

    //
    // Public Inner Classes
    //

        public static class Instruction
        {

            //
            // Private Class Methods
            //

                private static byte [] numericInstructionToInstructionBytes(
                    int numericOpcode,
                    int numericOperand
                    )
                {

                    byte [] instructionBytes = new byte[ 2 ];

                    InstructionInfo info
                        = numericOpcodeToInstructionInfoMap.get(numericOpcode);

                    if (info.category == InstructionCategory.instructionWithAddressOperand) {

                        //
                        // Let instructionBytes[ 0 ] = the 3-bit numericOpcode and
                        //     the 5 most-significant bits of the numericOperand
                        // Let instructionBytes[ 1 ] = the 8 least-significant bits
                        //     of the numericOperand
                        //

                            instructionBytes[ 0 ]
                                = ((byte) (numericOpcode << 5 | numericOperand >> 8 & 0x1f));
                            instructionBytes[ 1 ] = ((byte) (numericOperand & 0xff));

                        }

                    else {

                        //
                        // Let instructionBytes[ 0 ] = the 4-bit numericOpcode and
                        //     the 5 most-significant bits of the numericOperand
                        // Let instructionBytes[ 1 ] = the 8 least-significant bits
                        //     of the numericOperand
                        //

                            instructionBytes[ 0 ]
                                = ((byte) (numericOpcode << 4 | numericOperand >> 8 & 0xf));
                            instructionBytes[ 1 ] = ((byte) (numericOperand & 0xff));

                            }

                    return instructionBytes;

                    }

                private static byte [] numericInstructionToInstructionBytes(
                    int numericOpcode
                    )
                {

                    byte [] instructionBytes = new byte[ 1 ];

                    //
                    // Let instructionBytes[ 0 ] = the 6-bit numericOpcode and 2 0 bits
                    //

                        instructionBytes[ 0 ] = ((byte) (numericOpcode << 2 & 0xff));

                    return instructionBytes;

                    }

            //
            // Private Fields
            //

                private int myNumericOpcode;
                private int myNumericOperand;
                private String  mySymbolicOpcode;
                private byte [] myInstructionBytes;

                private static HashMap<String, InstructionInfo>
                    symbolicOpcodeToInstructionInfoMap
                        = new HashMap<String, InstructionInfo>();

                private static HashMap<Integer, InstructionInfo>
                    numericOpcodeToInstructionInfoMap
                        = new HashMap<Integer, InstructionInfo>();

                //
                // Initialization of symbolicOpcodeToInstructionInfoMap
                //

                static {

                    symbolicOpcodeToInstructionInfoMap.put(
                        "LOAD",
                        new InstructionInfo(
                            "LOAD", 0, InstructionCategory.instructionWithAddressOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "load",
                        new InstructionInfo(
                            "LOAD", 0, InstructionCategory.instructionWithAddressOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "STORE",
                        new InstructionInfo(
                            "STORE", 1, InstructionCategory.instructionWithAddressOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "store",
                        new InstructionInfo(
                            "STORE", 1, InstructionCategory.instructionWithAddressOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "ADD",
                        new InstructionInfo(
                            "ADD", 2, InstructionCategory.instructionWithAddressOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "add",
                        new InstructionInfo(
                            "ADD", 2, InstructionCategory.instructionWithAddressOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "SUB",
                        new InstructionInfo(
                            "SUB", 3, InstructionCategory.instructionWithAddressOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "sub",
                        new InstructionInfo(
                            "SUB", 3, InstructionCategory.instructionWithAddressOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "JUMP",
                        new InstructionInfo(
                            "JUMP", 4, InstructionCategory.instructionWithAddressOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "jump",
                        new InstructionInfo(
                            "JUMP", 4, InstructionCategory.instructionWithAddressOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "JUMPZ",
                        new InstructionInfo(
                            "JUMPZ", 5, InstructionCategory.instructionWithAddressOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "jumpz",
                        new InstructionInfo(
                            "JUMPZ", 5, InstructionCategory.instructionWithAddressOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "JUMPP",
                        new InstructionInfo(
                            "JUMPP", 6, InstructionCategory.instructionWithAddressOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "jumpp",
                        new InstructionInfo(
                            "JUMPP", 6, InstructionCategory.instructionWithAddressOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "SET",
                        new InstructionInfo(
                            "SET", 14, InstructionCategory.instructionWithConstantOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "set",
                        new InstructionInfo(
                            "SET", 14, InstructionCategory.instructionWithConstantOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "INPUT",
                        new InstructionInfo(
                            "INPUT", 60, InstructionCategory.instructionWithNoOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "input",
                        new InstructionInfo(
                            "INPUT", 60, InstructionCategory.instructionWithNoOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "OUTPUT",
                        new InstructionInfo(
                            "OUTPUT", 61, InstructionCategory.instructionWithNoOperand
                                )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "output",
                        new InstructionInfo(
                            "OUTPUT", 61, InstructionCategory.instructionWithNoOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "NOOP",
                        new InstructionInfo(
                            "NOOP", 62, InstructionCategory.instructionWithNoOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "noop",
                        new InstructionInfo(
                            "NOOP", 62, InstructionCategory.instructionWithNoOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "STOP",
                        new InstructionInfo(
                            "STOP", 63, InstructionCategory.instructionWithNoOperand
                            )
                        );
                    symbolicOpcodeToInstructionInfoMap.put(
                        "stop",
                        new InstructionInfo(
                            "STOP", 63, InstructionCategory.instructionWithNoOperand
                            )
                        );

                    };

                //
                // Initialization of symbolicOpcodeToInstructionInfoMap
                //

                static {

                    numericOpcodeToInstructionInfoMap.put(
                        0,
                        new InstructionInfo(
                            "LOAD", 0, InstructionCategory.instructionWithAddressOperand
                            )
                        );
                    numericOpcodeToInstructionInfoMap.put(
                        1,
                        new InstructionInfo(
                            "STORE", 1, InstructionCategory.instructionWithAddressOperand
                            )
                        );
                    numericOpcodeToInstructionInfoMap.put(
                        2,
                        new InstructionInfo(
                            "ADD", 2, InstructionCategory.instructionWithAddressOperand
                            )
                        );
                    numericOpcodeToInstructionInfoMap.put(
                        3,
                        new InstructionInfo(
                            "SUB", 3, InstructionCategory.instructionWithAddressOperand
                            )
                        );
                    numericOpcodeToInstructionInfoMap.put(
                        4,
                        new InstructionInfo(
                            "JUMP", 4, InstructionCategory.instructionWithAddressOperand
                            )
                        );
                    numericOpcodeToInstructionInfoMap.put(
                        5,
                        new InstructionInfo(
                            "JUMPZ", 5, InstructionCategory.instructionWithAddressOperand
                            )
                        );
                    numericOpcodeToInstructionInfoMap.put(
                        6,
                        new InstructionInfo(
                            "JUMPP", 6, InstructionCategory.instructionWithAddressOperand
                            )
                        );
                    numericOpcodeToInstructionInfoMap.put(
                        14,
                        new InstructionInfo(
                            "SET", 14, InstructionCategory.instructionWithConstantOperand
                            )
                        );
                    numericOpcodeToInstructionInfoMap.put(
                        60,
                        new InstructionInfo(
                            "INPUT", 60, InstructionCategory.instructionWithNoOperand
                            )
                        );
                    numericOpcodeToInstructionInfoMap.put(
                        61,
                        new InstructionInfo(
                            "OUTPUT", 61, InstructionCategory.instructionWithNoOperand
                            )
                        );
                    numericOpcodeToInstructionInfoMap.put(
                        62,
                        new InstructionInfo(
                            "NOOP", 62, InstructionCategory.instructionWithNoOperand
                            )
                        );
                    numericOpcodeToInstructionInfoMap.put(
                        63,
                        new InstructionInfo(
                            "STOP", 63, InstructionCategory.instructionWithNoOperand
                            )
                        );

                    }

            //
            // Public Ctors
            //

                public Instruction(int numericOpcode, int numericOperand)
                    throws IllegalArgumentException
                {

                    InstructionInfo info
                        = numericOpcodeToInstructionInfoMap.get(numericOpcode);

                    if (info == null)

                        throw
                            new IllegalArgumentException(
                                "Illegal numeric opcode " + numericOpcode
                                );

                    else if (info.category == InstructionCategory.instructionWithNoOperand)

                        throw
                            new IllegalArgumentException(
                                "Attempt to build 16-bit Instruction with 8-bit numeric opcode "
                                    + numericOpcode
                                );

                    //
                    // Construct a 16-bit instruction having a 13-bit address operand
                    //

                        else if (info.category
                                == InstructionCategory.instructionWithAddressOperand) {

                            setNumericOpcode(numericOpcode);
                            setNumericOperand(numericOperand & 0x1fff);
                            setSymbolicOpcode(info.symbolicOpcode);
                            setInstructionBytes(
                                numericInstructionToInstructionBytes(
                                    numericOpcode, numericOperand
                                    )
                                );

                            }

                    //
                    // Construct a 16-bit instruction having a 12-bit signed constant
                    // operand

                        else {

                            setNumericOpcode(numericOpcode);
                            setNumericOperand((numericOperand & 0xfff) << 20 >> 20);
                            setSymbolicOpcode(info.symbolicOpcode);
                            setInstructionBytes(
                                numericInstructionToInstructionBytes(
                                    numericOpcode, numericOperand
                                    )
                                );

                            }

                    }

                public Instruction(int numericOpcode)
                    throws IllegalArgumentException
                {

                    InstructionInfo info
                        = numericOpcodeToInstructionInfoMap.get(numericOpcode);

                    if (info == null)

                        throw
                            new IllegalArgumentException(
                                "Illegal numeric opcode " + numericOpcode
                                );

                    else if (info.category != InstructionCategory.instructionWithNoOperand)

                        throw
                            new IllegalArgumentException(
                                "Attempt to build 8-bit Instruction with 16-bit numeric opcode "
                                    + numericOpcode
                                );

                    else {

                        setNumericOpcode(numericOpcode);
                        setSymbolicOpcode(info.symbolicOpcode);
                        setInstructionBytes(
                            numericInstructionToInstructionBytes(numericOpcode)
                            );

                        }

                    }

                public Instruction(String symbolicOpcode, int numericOperand)
                    throws IllegalArgumentException
                {

                    InstructionInfo info
                        = symbolicOpcodeToInstructionInfoMap.get(symbolicOpcode);

                    if (info == null)

                        throw
                            new IllegalArgumentException(
                                "Illegal symbolic opcode " + symbolicOpcode
                                );

                    else if (info.category == InstructionCategory.instructionWithNoOperand)

                        throw
                            new IllegalArgumentException(
                                "Attempt to build 16-bit Instruction with 8-bit symbolic opcode "
                                    + symbolicOpcode
                                );

                    //
                    // Construct a 16-bit instruction having a 13-bit address operand
                    //

                        else if (info.category
                                == InstructionCategory.instructionWithAddressOperand) {

                            setNumericOpcode(info.numericOpcode);
                            setNumericOperand(numericOperand & 0x1fff);
                            setSymbolicOpcode(symbolicOpcode);
                            setInstructionBytes(
                                numericInstructionToInstructionBytes(
                                    info.numericOpcode,
                                    numericOperand
                                    )
                                );

                            }

                    //
                    // Construct a 16-bit instruction having a 12-bit signed constant
                    // operand

                        else {

                            setNumericOpcode(info.numericOpcode);
                            setNumericOperand((numericOperand & 0xfff) << 20 >> 20);
                            setSymbolicOpcode(symbolicOpcode);
                            setInstructionBytes(
                                numericInstructionToInstructionBytes(
                                    info.numericOpcode,
                                    numericOperand
                                    )
                                );

                            }

                    }

                public Instruction(String symbolicOpcode)
                    throws IllegalArgumentException
                {

                    InstructionInfo info
                        = symbolicOpcodeToInstructionInfoMap.get(symbolicOpcode);

                    if (info == null)

                        throw
                            new IllegalArgumentException(
                                "Illegal symbolic opcode " + symbolicOpcode
                                );

                    else if (info.category != InstructionCategory.instructionWithNoOperand)

                        throw
                            new IllegalArgumentException(
                                "Attempt to build 8-bit Instruction with 16-bit symbolic opcode "
                                    + symbolicOpcode
                                );

                    else {

                        setNumericOpcode(info.numericOpcode);
                        setSymbolicOpcode(symbolicOpcode);
                        setInstructionBytes(
                            numericInstructionToInstructionBytes(info.numericOpcode)
                            );

                        }

                    }

                public Instruction(byte [] instructionBytes)
                    throws IllegalArgumentException
                {

                //
                // If instruction bytes could not be a valid binary encoding
                // of a VM252 instruction, throw an exception
                //

                    if (instructionBytes == null)

                        throw
                            new IllegalArgumentException(
                                "Attempt to build Instruction with null byte array"
                                );

                    else if (instructionBytes.length < 1 || instructionBytes.length > 2)

                        throw
                            new IllegalArgumentException(
                                "Attempt to build Instruction with byte array having "
                                    + instructionBytes.length
                                    + " bytes"
                                );

                //
                // Otherwise, extract the numeric opcode and (for some opcodes)
                // the numeric operand from the bytes
                //

                    else {

                        switch (instructionBytes[ 0 ] >> 5 & 0x7) {

                            //
                            // If the 3 most-significant bits of the byte holding the encoded
                            // opcode denote an instruction having a 3-bit opcode and a
                            // 13-bit unsigned integer operand, extract the 3-bit numeric
                            // opcode and the 13-bit unsigned numeric operand
                            //

                                case LOAD_OPCODE :
                                case STORE_OPCODE :
                                case ADD_OPCODE :
                                case SUBTRACT_OPCODE :
                                case JUMP_OPCODE :
                                case JUMP_ON_ZERO_OPCODE :
                                case JUMP_ON_POSITIVE_OPCODE : {

                                    if (instructionBytes.length == 1)

                                        throw new IllegalArgumentException(
                                            "Attempt to build 16-bit Instruction from a single byte"
                                            );

                                    else {

                                        //
                                        // Let the numeric opcode be the 3 most-significant
                                        //     bits of instructionBytes[ 0 ]
                                        // Let the numeric operand be the 5 least-significant
                                        //     bits of instructionBytes[ 0 ] concatenated
                                        //     with instructionBytes[ 1 ]
                                        //

                                            setNumericOpcode(
                                                instructionBytes[ 0 ] >> 5 & 0x7
                                                );
                                            setNumericOperand(
                                                instructionBytes[ 0 ] << 8 & 0x1f00
                                                    | instructionBytes[ 1 ] & 0xff
                                                );

                                        }

                                    break;

                                    }

                            default :

                                switch (instructionBytes[ 0 ] >> 4 & 0xf) {

                                    //
                                    // If the 4 most-significant bits of the byte holding the
                                    // encoded opcode denote an instruction having a 4-bit
                                    // opcode and a 12-bit signed integer operand, extract
                                    // the 4-bit numeric opcode and the 12-bit signed numeric
                                    // operand
                                    //

                                        case SET_OPCODE : {

                                            if (instructionBytes.length == 1)

                                                throw new IllegalArgumentException(
                                                    "Attempt to build 16-bit Instruction from a single byte"
                                                    );

                                            else {

                                                //
                                                // Let the numeric opcode be
                                                //     the 4 most-significant bits of
                                                //     instructionBytes[ 0 ]
                                                // Let the numeric operand be
                                                //     the 4 least-significant bits of
                                                //     instructionBytes[ 0 ] concatenated
                                                //     with instructionBytes[ 1 ]
                                                //     WITH SIGN EXTENSION
                                                //

                                                    setNumericOpcode(
                                                        instructionBytes[ 0 ] >> 4 & 0xf
                                                        );
                                                    setNumericOperand(
                                                        instructionBytes[ 0 ] << 28 >> 20
                                                            | instructionBytes[ 1 ] & 0xff
                                                        );

                                                }

                                            break;

                                        }

                                    //
                                    // Otherwise, the 6 most-significant bits of the byte
                                    // holding the encoded necessarily opcode denote an
                                    // instruction having a 6-bit opcode and no operand, so
                                    // extract only the 6-bit opcode
                                    //

                                        default : {

                                            if (instructionBytes.length == 2)

                                                throw new IllegalArgumentException(
                                                    "Attempt to build 8-bit Instruction from two bytes"
                                                    );

                                            else

                                                setNumericOpcode(
                                                    instructionBytes[ 0 ] >> 2 & 0x3f
                                                    );

                                            }

                                    }

                            }

                        }

                        InstructionInfo info
                            = numericOpcodeToInstructionInfoMap.get(numericOpcode());

                        setSymbolicOpcode(info.symbolicOpcode);
                        setInstructionBytes(instructionBytes);

                    }

            //
            // Public Accessors
            //

                public int numericOpcode()
                {

                    return myNumericOpcode;

                    }

                public int numericOperand()
                    throws NoSuchElementException
                {

                    if (instructionBytes().length < 2)

                        throw new NoSuchElementException(
                            "16-bit Instruction with numeric opcode "
                            + numericOpcode()
                            + " has no operand"
                            );

                    else

                        return myNumericOperand;

                    }

                public String symbolicOpcode()
                {

                    return mySymbolicOpcode;

                    }

                    private void setNumericOpcode(int other)
                    {

                        myNumericOpcode = other;

                        }

                public byte [] instructionBytes()
                {

                    return myInstructionBytes;

                    }

            //
            // Private Mutators
            //

                private void setNumericOperand(int other)
                {

                    myNumericOperand = other;

                    }

                private void setSymbolicOpcode(String other)
                {

                    mySymbolicOpcode = other.toUpperCase();

                    }

                private void setInstructionBytes(byte [] other)
                {

                    myInstructionBytes = other;

                    }

            }

    }
