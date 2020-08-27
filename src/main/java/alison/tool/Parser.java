package alison.tool;

import alison.command.*;
import alison.exception.AlisonException;
import alison.task.Deadline;
import alison.task.Event;
import alison.task.Task;
import alison.task.ToDo;

public class Parser {

    public static Task parseTask(String taskString) throws AlisonException {
        String[] words = taskString.split(" \\| ");
        String type = words[0];
        boolean isDone = Boolean.parseBoolean(words[1]);
        String description = words[2];
        try {
            switch (type) {
            case "T":
                return new ToDo(description, isDone);
            case "E":
                return new Event(description, isDone, words[3]);
            case "D":
                return new Deadline(description, isDone, words[3]);
            default:
                return null;
            }
        } catch (Exception e) {
            throw AlisonException.loadingException();
        }
    }

    public static Command parse(String cmd) throws AlisonException {
        String[] words = cmd.split(" ");
        String command = words[0];
        if (words.length == 1) {
            switch (command) {
            case "bye":
                return new ExitCommand();
            case "list":
                return new ShowCommand();
            case "done", "delete":
                throw AlisonException.operationException();
            case "todo", "deadline", "event":
                throw AlisonException.emptyDescriptionException();
            default:
                throw AlisonException.defaultException();
            }
        }

        String description = cmd.split(" ", 2)[1];
        switch (command) {
        case "done":
            try {
                int doneIndex = Integer.parseInt(description);
                return new DoneCommand(doneIndex);
            } catch (Exception e) {
                throw AlisonException.invalidIndexException();
            }
        case "delete":
            try {
                int deleteIndex = Integer.parseInt(description);
                return new DeleteCommand(deleteIndex);
            } catch (Exception e) {
                throw AlisonException.invalidIndexException();
            }
        case "todo":
            ToDo todo = new ToDo(description);
            return new AddCommand(todo);
        case "deadline":
            String[] contentAnddate = description.split(" /by ");
            if (contentAnddate.length == 1) {
                throw AlisonException.deadlineException();
            } else {
                Deadline ddl = new Deadline(contentAnddate[0], contentAnddate[1]);
                return new AddCommand(ddl);
            }
        case "event":
            String[] contentAndTime = description.split(" /at ");
            if (contentAndTime.length == 1) {
                throw AlisonException.eventException();
            } else {
                Event e = new Event(contentAndTime[0], contentAndTime[1]);
                return new AddCommand(e);
            }
        default:
            return new UnknownCommand();
        }
    }
}