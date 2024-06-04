This is a personal project to combine the stuff learnt from CS2030S Programming Methodology II and CS2100 Computer Organisation from NUS.
This includes Java Object Oriented Programming from CS2030S and the MIPS architecture from CS2100.

The Word class is an abstraction of a 32 bit long integer represented as a string. Operations will be done by processing these strings. 
Words could represent 32 bit long instructions or 32 bit Integers and are stored in Java ArrayLists in registers and memory in the MIPS class respectively

The MIPS architecture is split into 5 stages represented as Java classes: 
Instruction Fetch, Decode, ALU, Memory, Write Back
Each of these classes have input and output fields, an update and run method.
Fields are private static so that they are immutable from outside
The void update(...params) method takes in the inputs as parameters and stores them in the input fields
The void run() method processes the stored input and stores the result in the output fields
The subsequent stage class can then access the its required input via getter methods for the output fields

The MIPS class brings the 5 stages together.
There are static methods that return a Word instruction, this is used to load the instruction ArrayList
available tested instructions include:  rformat add sub and or srl sll slt
                                        iformat addi andi ori lw sw beq
                                        jump (to be tested)
The void cycle() method processes a single instruction through all five stages and prints ascii representations in the console.
The void pipeline() method will process all stages by 1 step (to be implemented)
Please take a look at the Main method and the source code in the package to see how the MIPS class is used.

The end goal of this project is a dynamic visualisation of the MIPS architecture and how it processes assembly instructions.
Might be hosted on a website