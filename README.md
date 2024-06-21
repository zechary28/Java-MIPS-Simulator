This is a personal project to combine the stuff learnt from CS2030S Programming Methodology II and CS2100 Computer Organisation from NUS.
This includes Java Object Oriented Programming from CS2030S and the MIPS architecture from CS2100.

The Word class is an abstraction of a 32 bit long integer represented as a string. Operations will be done by processing these strings. 
Words could represent 32 bit long instructions or 32 bit Integers and are stored in Java ArrayLists in registers and memory in the MIPS class respectively
To instantiate Words, you could use:
- new Word("...")
    where ... is a String of 1s and 0s of any length.
    any non 1 or 0 input will be ignored
    length of input may vary and will be zero-extended to 32
- MIPS.toBinary(int)
    toBinary is a method of the MIPS class and returns a Word 
    the input integer will be represented in 2s complement
- MIPS.instruction(... params)
    the MIPS class has methods named after the assembly instructions that return a Word encoding of the instructions
    the method name and params are exactly in the order of how the assembly instructions they are written,
    eg. destination register as the first param
    r instructions:
        add sub and or slt params: (int rd, int rs, int rt) where rd, rs, rt are the index of the registers
        sll srl            params: (int rd, int rt, int shamt), where shamt is shift amount

    i instructions:
        addi               params: (int rt, int rs, int imm)
        andi ori           params: (int rt, int rs, String imm)
        lw sw beq          params: (int rt, int rs, String imm)

The MIPS architecture is a 5-stage pipeline represented as 6 total Java classes: 
Instruction Fetch, Decode, ControlUnit(not counted as a stage), ALU, Memory, Write Back
Each of these classes have input and output fields, an update and run method.
Fields are private static so that they are immutable from outside
The void update(...params) method takes in the inputs as parameters and stores them in the input fields
The void run() method processes the stored input and stores the result in the output fields
The subsequent stage class can then access the its required input via getter methods for the output fields

The MIPS class brings the 5 stages together.
There are static methods that return a Word instruction, this is used to load the instruction ArrayList
available tested instructions include:  rformat add sub and or slt, (srl sll not tested)
                                        iformat addi andi ori lw sw beq
                                        jump (to be tested)
cycle() method: 
processes a single instruction through all five stages and prints ascii representations in the console.

pipeline(boolean forward, )
The void pipeline() method will process all stages by 1 step

Lecture 21 Pipelining Hazards
Slide 9 : Already works by our implementation. instruction and data are separated into their respective ArrayLists
Slide 11: WriteBackStage will run before DecodeStage

1. can pipeline, raw not detected and will cause issues
2. detect raw, stall at IF stage
3. forwarding

Please take a look at the Main method and the source code in the package gto see how the MIPS class is used.

The end goal of this project is a dynamic visualisation of the MIPS architecture and how it processes assembly instructions.
Might be hosted on a website

In jshell:

import mips.*;
import java.util.List;
MIPS mips = new MIPS();
mips.loadMemory(List.of(new mips.Word("011010"),...));
mips.loadInstruction(List.of(MIPS.add(1,2,3),...));
mips.cycle(true);

