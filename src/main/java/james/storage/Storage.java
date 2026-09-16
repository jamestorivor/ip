package james.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import james.exception.UserInputException;
import james.task.Task;
import james.task.TaskList;

/**
 * Handles reading tasks from and writing tasks to the file system.
 */
public class Storage {
    private static final String INVALID_TASK_WARNING_PREFIX = "Warning: Skipping invalid saved task entry: ";
    private static final String READ_ERROR_WARNING_PREFIX = "Warning: Error reading saved tasks file: ";

    private final Path filePath;
    // Prevents a partial or failed load from replacing the original data.
    private boolean hasLoadErrors;

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
        hasLoadErrors = false;
        try {
            if (Files.notExists(filePath)) {
                return loadedTasks;
            }
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }
                    try {
                        Task task = Task.fromFileString(line);
                        if (loadedTasks.stream().anyMatch(existing -> existing.hasSameDetails(task))) {
                            throw new UserInputException("Duplicate saved task.");
                        }
                        loadedTasks.add(task);
                    } catch (UserInputException e) {
                        hasLoadErrors = true;
                        System.out.println(INVALID_TASK_WARNING_PREFIX + line);
                    }
                }
            }
        } catch (IOException | SecurityException e) {
            hasLoadErrors = true;
            System.out.println(READ_ERROR_WARNING_PREFIX + e.getMessage());
        }
        return loadedTasks;
    }

    /**
     * Reports whether saving is blocked to protect incompletely loaded data.
     *
     * @return True if the storage file needs repair or its access needs restoring.
     */
    public boolean hasLoadErrors() {
        return hasLoadErrors;
    }

    /**
     * Saves all current tasks in the task list to the persistent storage file.
     * Automatically creates any necessary parent directories.
     *
     * @param taskList The TaskList whose tasks are to be saved.
     * @return True if the complete task list was saved successfully.
     */
    public boolean save(TaskList taskList) {
        if (hasLoadErrors) {
            return false;
        }
        Path temporaryFile = null;
        try {
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
        }
    }
}
