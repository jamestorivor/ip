package james.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;

import james.exception.UserInputException;
import james.task.Task;
import james.task.TaskList;

/**
 * Handles reading tasks from and writing tasks to the file system.
 */
public class Storage {
    private static final String INVALID_TASK_WARNING_PREFIX = "Warning: Skipping invalid saved task entry: ";
    private static final String READ_ERROR_WARNING_PREFIX = "Warning: Error reading saved tasks file: ";
    private static final String DIRECTORY_ERROR_WARNING_PREFIX =
            "Warning: Unable to create storage directory: ";
    private static final String SAVE_ERROR_MESSAGE_PREFIX = "Error saving tasks: ";

    private final Path filePath;

    /**
     * Constructs a Storage instance with the given file path.
     *
     * @param filePath Path to the persistent storage file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads tasks from the persistent storage file on disk into memory.
     * If the file does not exist, an empty list is returned.
     * Corrupted lines are skipped gracefully with a warning.
     *
     * @return An ArrayList containing the loaded tasks.
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
                        System.out.println(INVALID_TASK_WARNING_PREFIX + line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // Storage file not found; return empty list
        } catch (Exception e) {
            System.out.println(READ_ERROR_WARNING_PREFIX + e.getMessage());
        }
        return loadedTasks;
    }

    /**
     * Saves all current tasks in the task list to the persistent storage file.
     * Automatically creates any necessary parent directories.
     *
     * @param taskList The TaskList whose tasks are to be saved.
     * @return True if the complete task list was saved successfully.
     */
    public boolean save(TaskList taskList) {
        Path temporaryFile = null;
        try {
            File file = filePath.toFile();
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs() && !parentDir.exists()) {
                    System.out.println(DIRECTORY_ERROR_WARNING_PREFIX + parentDir.getPath());
                    return;
                }
            }
            try (FileWriter writer = new FileWriter(file)) {
                for (Task task : taskList.getTasks()) {
                    writer.write(task.toFileString() + System.lineSeparator());
                }
            Path destination = filePath.toAbsolutePath();
            Files.createDirectories(destination.getParent());
            temporaryFile = Files.createTempFile(destination.getParent(), "james-", ".tmp");
            StringBuilder contents = new StringBuilder();
            for (Task task : taskList.getTasks()) {
                contents.append(task.toFileString()).append(System.lineSeparator());
            }
            Files.writeString(temporaryFile, contents);
            // Replace only after the complete new contents have been written.
            Files.move(temporaryFile, destination, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException | SecurityException e) {
            return false;
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException | SecurityException e) {
                    // A leftover temporary file does not change the saved task list.
                }
            }
            System.out.println(SAVE_ERROR_MESSAGE_PREFIX + e.getMessage());
        }
    }
}
