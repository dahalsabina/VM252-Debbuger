package vm252utilities;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;



public class VM252Utilities
{
    public static HashMap<Integer, String> addressSymbolHashMap = new HashMap<Integer, String>();

    //
    // Public Class Methods
    //

        //
        // Public Class Method byte [] readObjectCodeFromObjectFile(String objectFileName)
        //
        // Purpose:
        //     Reads the object‑code bytes only from the VM252 object‑code file having a
        //         given name
        //
        // Formals:
        //     objectFileName (in) - the name of the VM252 object-code file to be read
        //
        // Pre-conditions:
        //     none
        //
        // Post-conditions:
        //     none
        //
        // Returns:
        //     an array of byte’s holding bytes of the object-code for the object-code file
        //         having the name in objectFileName, if a file having the name in
        //         objectFileName exists and in a valid VM252 object-code file
        //     null, otherwise
        //
        // Worst-case asymptotic runtime:
        //     O(1 + (the file-length of the file having the name in objectFileName))
        //

        public static byte [] readObjectCodeFromObjectFile(String objectFileName)
        {

            addressSymbolHashMap.clear();
            byte [] objectCode = null;

            try {

                //
                // Let objectFile = a FileInputStream corresponding to the file whose name
                //     is in objectCodeFile
                //

                    final FileInputStream objectFile
                        = new FileInputStream(new File(objectFileName));

                byte [] sourceFileInformation = null;
                byte [] executableSourceLineMap = null;
                byte [] symbolicAddressInformation = null;
                byte [] byteContentMap = null;

                int byte3;
                int byte2;
                int byte1;
                int byte0;

                //
                // Read the content of objectFile into objectCode, sourceFileInformation,
                //     executableSourceLineMap, symbolicAddressInformation, and
                //     byteContentMap, collectively
                //

                    //
                    // Let objectCodeSize = the # of bytes of object code
                    //

                        byte3 = objectFile.read();
                        byte2 = objectFile.read();
                        byte1 = objectFile.read();
                        byte0 = objectFile.read();

                        if (byte0 == -1 || byte1 == -1 || byte2 == -1 || byte3 == -1)
                            throw new IOException();

                        final int objectCodeSize
                            = (byte3 & 0xff) << 24 | (byte2 & 0xff) << 16
                                | (byte1 & 0xff) << 8 | byte0 & 0xff;

                    //
                    // Let sourceFileInformationSize = the # of bytes of source file
                    //     information
                    //

                        byte3 = objectFile.read();
                        byte2 = objectFile.read();
                        byte1 = objectFile.read();
                        byte0 = objectFile.read();

                        if (byte0 == -1 || byte1 == -1 || byte2 == -1 || byte3 == -1)
                            throw new IOException();

                        final int sourceFileInformationSize
                            = (byte3 & 0xff) << 24 | (byte2 & 0xff) << 16
                                | (byte1 & 0xff) << 8 | byte0 & 0xff;

                    //
                    // Let executableSourceLineMapSize
                    //     = the # of bytes of the executable source-line map
                    //

                        byte3 = objectFile.read();
                        byte2 = objectFile.read();
                        byte1 = objectFile.read();
                        byte0 = objectFile.read();

                        if (byte0 == -1 || byte1 == -1 || byte2 == -1 || byte3 == -1)
                            throw new IOException();

                        final int executableSourceLineMapSize
                            = (byte3 & 0xff) << 24 | (byte2 & 0xff) << 16
                                | (byte1 & 0xff) << 8 | byte0 & 0xff;

                    //
                    // Let symbolicAddressInformationSize = the # of bytes of
                    //     symbolic-address information
                    //

                        byte3 = objectFile.read();
                        byte2 = objectFile.read();
                        byte1 = objectFile.read();
                        byte0 = objectFile.read();

                        if (byte0 == -1 || byte1 == -1 || byte2 == -1 || byte3 == -1)
                            throw new IOException();

                        final int symbolicAddressInformationSize
                            = (byte3 & 0xff) << 24 | (byte2 & 0xff) << 16
                                | (byte1 & 0xff) << 8 | byte0 & 0xff;

                    //
                    // Let symbolicAddressInformationSize
                    //     = the # of bytes of symbolic-address information
                    //

                        byte3 = objectFile.read();
                        byte2 = objectFile.read();
                        byte1 = objectFile.read();
                        byte0 = objectFile.read();

                        if (byte0 == -1 || byte1 == -1 || byte2 == -1 || byte3 == -1)
                            throw new IOException();

                        final int byteContentMapSize
                            = (byte3 & 0xff) << 24 | (byte2 & 0xff) << 16
                                | (byte1 & 0xff) << 8 | byte0 & 0xff;

                    if (byteContentMapSize != 0 && objectCodeSize != byteContentMapSize)
                        throw new IOException();

                    //
                    // Let objectCode[ 0 ... objectCodeSize-1 ] = the bytes of object code
                    //

                        objectCode = new byte[ objectCodeSize ];

                        int objectCodeReadStatus = objectFile.read(objectCode);

                        if (objectCodeReadStatus == -1)
                            throw new IOException();

                    //
                    // Let sourceFileInformation[ 0 ... sourceFileInformationSize-1 ]
                    //     = the bytes of source-file information
                    //

                        sourceFileInformation = new byte[ sourceFileInformationSize ];

                        int sourceFileNameReadStatus
                            = objectFile.read(sourceFileInformation);

                        if (sourceFileNameReadStatus == -1)
                            throw new IOException();

                    //
                    // Let executableSourceLineMap[ 0 ... executableSourceLineMapSize-1 ]
                    //     = the bytes of the executable source-line map
                    //

                        executableSourceLineMap
                            = new byte[ executableSourceLineMapSize ];

                        int executableSourceLineMapReadStatus
                            = objectFile.read(executableSourceLineMap);

                        if (executableSourceLineMapReadStatus == -1)
                            throw new IOException();

                    //
                    // Let symbolicAddressInformation[ 0
                    //         ... symbolicAddressInformationSize-1 ]
                    //     = the bytes of symbolic-address information map
                    //

                        symbolicAddressInformation
                            = new byte[ symbolicAddressInformationSize ];

                        int symbolicAddressInformationReadStatus
                            = objectFile.read(symbolicAddressInformation);

                        boolean reading_memory_address = false;
                        int current_line = 1;
                        byte [] memory_address_bytes = new byte [4];
                        String current_symbol = "";

                        // Loop over all the bytes of the symbolic address information
                        for (byte value : symbolicAddressInformation){

                            if (reading_memory_address){
                                    // keep modifying memory_address_bytes till we get the 4 bytes of the address
                                    memory_address_bytes[current_line-1] = value;
                                    // if we have the memory address for a symbol, then we add it to our hash map
                                    if (current_line == 4){
                                        // convert the 32 bit integer from binary to decimal and store that
                                        int address= (memory_address_bytes[0]<<24)&0xff000000|
                                                    (memory_address_bytes[1]<<16)&0x00ff0000|
												      (memory_address_bytes[2]<< 8)&0x0000ff00|
												      (memory_address_bytes[3]<< 0)&0x000000ff;

                                        addressSymbolHashMap.put(address, current_symbol);
                                        reading_memory_address = false;
                                        // We will be reading the next symbol if any
                                        current_symbol = "";
                                    }
                                    current_line +=1;
                            }
                            else if (value == (byte) 0) {
                                // if we encounter 0 byte, it means we are going to get a 32 bit (4 bytes)
                                // integer for the memory address of the symbolic code
                                reading_memory_address = true;
                            } 
                            else {
                                current_line = 1;
                                byte [] value_list = {value};
                                // decode the current byte to it's relevant ASCII and get the current symbol
                                current_symbol += new String(value_list, "UTF-8");
                            }
                        }

                        if (symbolicAddressInformationReadStatus == -1)
                            throw new IOException();

                    //
                    // Let byteContentMap[ 0 ... byteContentMapSize-1 ]
                    //     = the bytes of the byte-content map
                    //

                        byteContentMap = new byte[ byteContentMapSize ];

                        int byteContentMapReadStatus = objectFile.read(byteContentMap);

                        if (byteContentMapReadStatus == -1)
                            throw new IOException();

                    objectFile.close();

                }
                catch (FileNotFoundException exception) {

                    ; // do nothing

                    }
                catch (IOException exception) {

                    ; // do nothing

                    }

            System.out.println(addressSymbolHashMap);
            return objectCode;

            }

    }
