package james.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;
import james.exception.UserInputException;
import james.task.Task;
import james.task.TaskList;

/**
 * Handles reading tasks from and writing tasks to the file system.
 */
public class Storage {
    private final Path filePath;

    /**
     * Constructs a Storage instance with the given file path.
     *
     * @param filePath path to the persistent storage file
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads tasks from the persistent storage file on disk into memory.
     * If the file does not exist, an empty list is returned.
     * Corrupted lines are skipped gracefully with a warning.
     *
     * @return an ArrayList containing the loaded tasks
     */
    public ArrayList<Task> load() {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        File file = filePath.toFile();
        if (!file.exists()) {
            return loadedTasks;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (!line.trim().isEmpty()) {
                    try {
                        Task task = Task.fromFileString(line);
                        loadedTasks.add(task);
                    } catch (UserInputException e) {
                        System.out.println("Warning: Skipping invalid saved task entry: " + line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // Storage file not found; return empty list
        } catch (Exception e) {
            System.out.println("Warning: Error reading saved tasks file: " + e.getMessage());
        }
        return loadedTasks;
    }

    /**
     * Saves all current tasks in the task list to the persistent storage file.
     * Automatically creates any necessary parent directories.
     *
     * @param taskList the TaskList whose tasks are to be saved
     */
    public void save(TaskList taskList) {
        try {
            File file = filePath.toFile();
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs() && !parentDir.exists()) {
                    System.out.println("Warning: Unable to create storage directory: " + parentDir.getPath());
                    return;
                }
            }
            try (FileWriter writer = new FileWriter(file)) {
                for (Task task : taskList.getTasks()) {
                    writer.write(task.toFileString() + System.lineSeparator());
                }
            }
        } catch (IOException | SecurityException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }
}
