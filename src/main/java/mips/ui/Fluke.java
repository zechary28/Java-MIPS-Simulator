package mips.ui;

import mips.MIPS;
import mips.component.Storage;
import mips.component.Ui;
import mips.exception.InvalidInputException;
import mips.util.Word;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO OUTDATED
 * The {@code Luke} class is the main entry point for the task management application.
 * It handles the interactions with the user, parses commands, and updates the task list.
 * It is responsible for managing tasks, saving/loading task data to/from a file,
 * and providing feedback to the user via the {@link Ui} class.
 */
public class Fluke {

    private MIPS mips;
    private final Ui ui;
    private Storage storage;

    private StringBuilder output;

    /**
     * TODO OUTDATED
     * Constructs a new {@code Luke} object which initializes the task list, user interface,
     * and storage system. If the storage system cannot be found, the program will terminate.
     */
    public Fluke() {
        this.mips = new MIPS();
        // TODO READ DATA
        mips.loadMemory(List.of(
                new Word("1000 0000 0000 0000 0000 0000 0000 0000"),
                new Word("0111 1111 1111 1111 1111 1111 1111 1111"),
                new Word("0000 0000 0000 0000 0000 0000 0000 1010"),
                MIPS.toBinary(31)
        ));
        // TODO READ TEXT
        mips.loadInstruction(List.of(
                MIPS.add(2, 1, 1), // 1
                MIPS.add(4, 2, 2), // 2 RAW
                MIPS.sub(3, 4, 1), // 3 RAW
                MIPS.add(7, 3, 4), // 4 RAW
                MIPS.add(5, 2, 3), // 5
                MIPS.add(10, 5, 5), // 6 1010 RAW
                MIPS.addi(12, 10, 2), // 7 1100 RAW
                MIPS.and(8, 10, 12), // 8 and 1000 RAW
                MIPS.or(14, 10, 12) // 9 or 1110
        ));

        this.ui = new Ui();
        try {
            this.storage = new Storage();
        } catch (Exception e) {
            System.out.println("No file for storage found, exiting program");
            System.exit(0);
        }
        this.output = new StringBuilder();
    }

    /**
     * Processes the user input and returns an appropriate response.
     *
     * This method determines the command from the input string and performs the corresponding action,
     * such as adding tasks, marking tasks, deleting tasks, or displaying help information.
     * It also handles system exit commands and other actions.
     *
     * @param input the user's input command as a string.
     * @return a string response based on the command and its execution result.
     */
    public String getResponse(String input) {
        this.output = new StringBuilder();
        if (input == null || input.trim().isEmpty()) {
            return "No input detected";
        }
        // determine command
        String[] inputArr = input.split(" ");
        String command = inputArr[0].toLowerCase();
        if (command.equals("help")) {
            this.output.append("list of commands:\n");
            this.output.append("cycle    : add todo task\n");
            this.output.append("pipeline : add deadline task\n");
            this.output.append("bye      : save list and exit program\n");
        } else if (command.equals("bye")) {
            try {
                writeDataToFile();
            } catch (Exception e) {
                this.output.append("There was a problem writing to the file\n");
            }
            System.exit(0);
        } else if (command.equals("cycle")) {
            this.output.append(mips.cycle(true));
        } else if (command.equals("pipeline")) {
            this.output.append(mips.pipeline(true));
        } else {
            this.output.append("Invalid command");
        }
        return this.output.toString();
    }

    /**
     * Prepares and returns the startup message.
     *
     * This method initializes the response with a welcome message and ensures the
     * task list file is checked and loaded if available.
     *
     * @return the startup message as a string.
     */
    public String getStartUp() {
        this.output = new StringBuilder();
        this.output.append(this.ui.showWelcome());
        checkListFile();
        return this.output.toString();
    }

    /**
     * Prepares and returns the shutdown message.
     *
     * This method writes the current task list to a file and appends the goodbye message
     * to the response.
     *
     * @return the shutdown message as a string.
     */
    public String getShutDown() {
        this.output = new StringBuilder();
        writeDataToFile();
        this.output.append(this.ui.exit()).append("\n");
        return this.output.toString();
    }

    // Parsers for task creation

    /**
     * TODO OUTDATED
     * Parses the input for creating a ToDo task.
     *
     * @param input the user input
     * @return a new {@code ToDo} task
     * @throws InvalidInputException if the input is invalid
     */
    public Word parseWord(String input) throws InvalidInputException {
        // invalid input: [todo] or [todo ]
        if (input.length() < 5 || input.substring(5).trim().isEmpty()) {
            //this.output.append("Todo format: [todo] [name]\n");
            throw new InvalidInputException("Todo format: [todo] [name]");
        }
        String name = input.substring(5);

        assert !name.trim().isEmpty() : "name should not be empty";

        return new Word("0"); // TODO CHANGE TO WORD
    }

//    /**
//     * Parses the input for creating a Deadline task.
//     *
//     * @param input the user input
//     * @return a new {@code Deadline} task
//     * @throws InvalidInputException if the input is invalid
//     */
//    public Task parseDeadline(String input) throws InvalidInputException {
//        // invalid input: [deadline] or [deadline ]
//        if (input.length() < 9 || input.substring(9).trim().isEmpty()) {
//            this.output.append("Deadline format: deadline [name] /by [DD/MM/YYYY HH:MM]\n");
//            throw new InvalidInputException("Deadline format: deadline [name] /by [DD/MM/YYYY HH:MM]\n");
//        }
//        String[] inputArr = input.substring(9).split(" /by ");
//        // invalid input: [deadline *** /by ] or [deadline /by ***]
//        if (inputArr.length < 2) {
//            //this.output.append("Deadline format: deadline [name] /by [DD/MM/YYYY HH:MM]\n");
//            throw new InvalidInputException("Deadline format: deadline [name] /by [DD/MM/YYYY HH:MM]\n");
//        }
//        String name = inputArr[0];
//        String due = inputArr[1];
//
//        assert !name.trim().isEmpty() : "name should not be empty";
//        assert !due.trim().isEmpty() : "deadline should not be empty";
//
//        // invalid input: white spaces for name and deadline
//        return new Deadline(name, false, due);
//    }
//
//    /**
//     * Parses the input for creating an Event task.
//     *
//     * @param input the user input
//     * @return a new {@code Event} task
//     * @throws InvalidInputException if the input is invalid
//     */
//    public Task parseEvent(String input) throws InvalidInputException {
//        // invalid input: [event] or [event ]
//        if (input.length() < 6 || input.substring(6).trim().isEmpty()) {
//            //this.output.append("Event format: event [name] /from [DD/MM/YYYY HH:MM] /to [DD/MM/YYYY HH:MM]\n");
//            throw new InvalidInputException("Event format: event [name] "
//                    + "/from [DD/MM/YYYY HH:MM] /to [DD/MM/YYYY HH:MM]\n");
//        }
//
//        // split [name] /from [rest...]
//        String[] inputArr = input.substring(6).split(" /from ");
//        // invalid input: [event *** /from ] or [event /from ***]
//        if (inputArr.length < 2) {
//            //this.output.append("Event format: event [name] /from [DD/MM/YYYY HH:MM] /to [DD/MM/YYYY HH:MM]\n");
//            throw new InvalidInputException("Event format: event [name] "
//                    + "/from [DD/MM/YYYY HH:MM] /to [DD/MM/YYYY HH:MM]\n");
//        }
//        input = inputArr[1];
//        String name = inputArr[0]; // extract name
//
//        // split [start] /by [end]
//        inputArr = input.split(" /to ");
//        // invalid input: [event *** /from *** /to ] or [event *** /from /to ***]
//        if (inputArr.length < 2) {
//            //this.output.append("Event format: event [name] /from [DD/MM/YYYY HH:MM] /to [DD/MM/YYYY HH:MM]\n");
//            throw new InvalidInputException("Event format: event [name] "
//                    + "/from [DD/MM/YYYY HH:MM] /to [DD/MM/YYYY HH:MM]\n");
//        }
//        String start = inputArr[0];
//        String end = inputArr[1];
//
//        assert !name.trim().isEmpty() : "name should not be empty";
//        assert !start.trim().isEmpty() : "start time should not be empty";
//        assert !end.trim().isEmpty() : "end time should not be empty";
//
//        return new Event(name, false, start, end);
//    }

    // Task management and UI updates
//    /**
//     * Handles the addition of a new task based on the user input.
//     *
//     * This method determines the type of task (ToDo, Deadline, or Event) from the input string
//     * and adds it to the task list after parsing. If the input does not match any recognized
//     * task type or is invalid, an appropriate error message is appended to the output.
//     *
//     * @param input the string input from the user specifying the task type and details.
//     * @throws InvalidInputException if the input is invalid and cannot be parsed into a task.
//     */
//    public void handleAddTask(String input) {
//        String taskType = input.split(" ")[0].toLowerCase();
//        try {
//            if (taskType.equals("todo")) {
//                Task task = parseToDo(input);
//                this.taskList.addTask(task);
//                showTaskUpdates(task);
//            } else if (taskType.equals("deadline")) {
//                Task task = parseDeadline(input);
//                this.taskList.addTask(task);
//                showTaskUpdates(task);
//            } else if (taskType.equals("event")) {
//                Task task = parseEvent(input);
//                this.taskList.addTask(task);
//                showTaskUpdates(task);
//            } else {
//                this.output.append("I don't understand");
//            }
//        } catch (InvalidInputException e) {
//            this.output.append(e.getMessage());
//        }
//    }

//    /**
//     * Updates the UI and displays a message after a task is added.
//     *
//     * @param task the task that was added
//     */
//    public void showTaskUpdates(Task task) {
//        this.output.append("Got it. I've added this task:\n");
//        this.output.append("  " + task + "\n");
//        this.output.append("Now you have " + taskList.getSize() + " tasks in the list.\n");
//    }

//    /**
//     * Checks if the given index is within the bounds of the task list.
//     *
//     * If the index is out of bounds (greater than or equal to the size of the task list),
//     * an error message is appended to the output, and the method returns false.
//     * Otherwise, the method returns true.
//     *
//     * @param i the index to check.
//     * @return {@code true} if the index is valid (i.e., less than the size of the task list),
//     *         {@code false} otherwise.
//     */
//    public boolean checkIndex(int i) {
//        if (i >= taskList.getSize()) {
//            this.output.append("There are only " + taskList.getSize() + " tasks in the list");
//            return false;
//        } else {
//            return true;
//        }
//    }

    /**
     * TODO: CHANGE TO PRINT CURRENT REGISTERS OR INSTRUCTIONS OR STAGE DATA
     * Prints the list of tasks to the UI.
     */
    public void printList(ArrayList<Word> tasks) {
        this.output.append(" Here are the tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            this.output.append(String.format(" %d.%s", i + 1, tasks.get(i)) + "\n");
        }
    }

    /**
     * Checks if a saved task list file exists and loads it, or creates a new list if no file is found.
     */
    public void checkListFile() {
//        boolean isFound = readListFile();
//        if (isFound) {
//            printList(this.taskList.getList());
//        }
    }

    /**
     * TODO: CHANGE TO READ WHOLE FILE INPUT WITH TEXT AND DATA
     * Reads the task list from the storage file and adds tasks to the task list.
     *
     * @throws FileNotFoundException if the storage file is not found
     */
    public boolean readListFile() {
        String header = this.storage.readLine();
        if (header.isEmpty()) {
            return false;
        }
        while (this.storage.hasNext()) {
            String task = this.storage.readLine();
//            try {
//                this.taskList.addTask(readTask(task));
//            } catch (InvalidInputException e) {
//                this.output.append(" There was something wrong with this task.\n");
//                this.output.append(" " + task + "\n");
//            }
        }
        return true;
    }

    /**
     * TODO: HANDLE READ LINE OF FILE INPUT
     * Reads a single task from a line of text and returns the corresponding Task object.
     *
     * @param input the task data in string format
     * @return the corresponding Task object
     * @throws InvalidInputException if the task data is invalid
     */
    public Word readText(String input) throws InvalidInputException {
        String[] taskParts = input.split(" : ");
        String taskType = taskParts[0];
        return new Word("0");
//        if (taskType.equals("T")) {
//            return readToDo(taskParts);
//        } else if (taskType.equals("D")) {
//            return readDeadline(taskParts);
//        } else if (taskType.equals("E")) {
//            return readEvent(taskParts);
//        } else {
//            this.output.append("invalid command\n");
//            throw new InvalidInputException("invalid command\n");
//        }
    }

    /**
     * TODO: CHANGE TO READ TEXT AND DATA
     * Reads input to create a ToDo task.
     *
     * This method parses the input array to extract information required to construct a ToDo task.
     * It checks for the presence of sufficient arguments and throws an exception if the input is invalid.
     *
     * @param inputArr an array of strings representing the input for a ToDo task.
     *                 The expected format is: {"todo", "isDone", "name"}.
     * @return a {@code ToDo} task constructed from the input arguments.
     * @throws InvalidInputException if the input array has insufficient arguments.
     */
//    public Task readToDo(String[] inputArr) throws InvalidInputException {
//        if (inputArr.length < 3) {
//            //this.output.append("len < 3\n");
//            throw new InvalidInputException("insufficient arguments");
//        }
//        boolean isDone = inputArr[1].equals("1");
//        String name = inputArr[2];
//        return new ToDo(name, isDone);
//    }

    /**
     * TODO: CHANGE TO READ TEXT AND DATA
     * Reads input to create a Deadline task.
     *
     * This method parses the input array to extract information required to construct a Deadline task.
     * It checks for the presence of sufficient arguments and throws an exception if the input is invalid.
     *
     * @param inputArr an array of strings representing the input for a Deadline task.
     *                 The expected format is: {"deadline", "isDone", "name", "deadline"}.
     * @return a {@code Deadline} task constructed from the input arguments.
     * @throws InvalidInputException if the input array has insufficient arguments.
     */
//    public Task readDeadline(String[] inputArr) throws InvalidInputException {
//        if (inputArr.length < 4) {
//            //this.output.append("len < 4\n");
//            throw new InvalidInputException("insufficient arguments");
//        }
//        boolean isDone = inputArr[1].equals("1");
//        String name = inputArr[2];
//        String deadline = inputArr[3];
//        return new Deadline(name, isDone, deadline);
//    }

    /**
     * TODO: CHANGE TO READ TEXT AND DATA
     * Reads input to create an Event task.
     *
     * This method parses the input array to extract information required to construct an Event task.
     * It checks for the presence of sufficient arguments and throws an exception if the input is invalid.
     *
     * @param inputArr an array of strings representing the input for an Event task.
     *                 The expected format is: {"event", "isDone", "name", "start", "end"}.
     * @return an {@code Event} task constructed from the input arguments.
     * @throws InvalidInputException if the input array has insufficient arguments.
     */
//    public Task readEvent(String[] inputArr) throws InvalidInputException {
//        if (inputArr.length < 5) {
//            //this.output.append("len < 5\n");
//            throw new InvalidInputException("insufficient arguments");
//        }
//        boolean isDone = inputArr[1].equals("1");
//        String name = inputArr[2];
//        String start = inputArr[3];
//        String end = inputArr[4];
//        return new Event(name, isDone, start, end);
//    }

    /**
     * TODO: CHANGE TO WRITE DATA
     * Writes the current task list to the storage file.
     *
     * @throws IOException if an I/O error occurs while writing to the file
     */
    public void writeDataToFile() {
//        try {
//            if (this.storage.hasWriteFile()) {
//                this.storage.clearFile();
//            }
//            this.output.append("Saving list...\n");
//            storage.writeLine(String.format("list: %d", this.taskList.getSize()));
//            int numTasks = taskList.getSize();
//            for (Task task : taskList.getList()) {
//                if (task instanceof ToDo todo) {
//                    this.storage.writeLine(String.format("T : %s : %s",
//                            todo.getIsDone() ? "1" : "0",
//                            todo.getName()));
//                } else if (task instanceof Deadline deadline) {
//                    this.storage.writeLine(String.format("D : %s : %s : %s",
//                            deadline.getIsDone() ? "1" : "0",
//                            deadline.getName(),
//                            deadline.getDueTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
//                } else if (task instanceof Event event) {
//                    this.storage.writeLine(String.format("E : %s : %s : %s : %s",
//                            event.getIsDone() ? "1" : "0",
//                            event.getName(),
//                            event.getStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
//                            event.getEndTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
//                }
//            }
//            this.output.append("Saved successfully\n");
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
    }
}
