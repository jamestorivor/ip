package james.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import james.exception.UserInputException;
import james.task.Task;
import james.task.TaskList;

/**
 * Handles reading tasks from and writing tasks to the file system.
 */
public class Storage {
    private static final String READ_ERROR_WARNING_PREFIX = "Warning: Error reading saved tasks file: ";

    private final Path filePath;
    // Prevents a partial or failed load from replacing the original data.
    private boolean hasLoadErrors;
    private final List<String> loadWarnings = new ArrayList<>();
    // Records the exact bytes loaded or last saved; null means the file did not exist.
    private byte[] savedContents;
    private String saveError = "";

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
        loadWarnings.clear();
        try {
            savedContents = readCurrentContents();
            if (savedContents == null) {
                return loadedTasks;
            }
            String contents = StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(savedContents)).toString();
            try (BufferedReader reader = new BufferedReader(new StringReader(contents))) {
                int lineNumber = 0;
                String line;
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
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
                        loadWarnings.add("Warning: Skipping invalid saved task at line " + lineNumber + ".");
                    }
                }
            }
        } catch (IOException | SecurityException e) {
            hasLoadErrors = true;
            loadWarnings.add(READ_ERROR_WARNING_PREFIX + filePath);
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
     * Returns startup diagnostics for either user interface.
     *
     * @return Warning and recovery instructions, or an empty string after a successful load.
     */
    public String getLoadWarning() {
        if (!hasLoadErrors) {
            return "";
        }
        return String.join("\n", loadWarnings) +
                "\nSome saved tasks could not be loaded. Changes are disabled.\n" +
                "Repair the saved file or restore read access, then restart James.";
    }

    /**
     * Returns specific recovery guidance for the latest failed save, when available.
     *
     * @return Save failure guidance, or an empty string for an ordinary I/O failure.
     */
    public String getSaveError() {
        return saveError;
    }

    /**
     * Reads the current disk version without treating an inaccessible file as missing.
     */
    private byte[] readCurrentContents() throws IOException {
        return Files.notExists(filePath) ? null : Files.readAllBytes(filePath);
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
        saveError = "";
        try {
            Path destination = filePath.toAbsolutePath();
            Files.createDirectories(destination.getParent());
            Path lockPath = destination.resolveSibling(destination.getFileName() + ".lock");
            // Keep the lock file in place: deleting it could let writers lock different files.
            try (FileChannel channel = FileChannel.open(lockPath, StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE);
                    FileLock lock = channel.tryLock()) {
                if (lock == null) {
                    saveError = "Another instance is saving tasks. Try again.";
                    return false;
                }
                if (!Arrays.equals(savedContents, readCurrentContents())) {
                    saveError = "The saved file changed outside this instance. Restart James before making changes.";
                    return false;
                }
                return writeTasks(taskList, destination);
            }
        } catch (OverlappingFileLockException e) {
            saveError = "Another instance is saving tasks. Try again.";
            return false;
        } catch (IOException | SecurityException e) {
            return false;
        }
    }

    /**
     * Replaces the task file after writing all contents while holding the writer lock.
     */
    private boolean writeTasks(TaskList taskList, Path destination) throws IOException {
        Path temporaryFile = null;
        try {
            temporaryFile = Files.createTempFile(destination.getParent(), "james-", ".tmp");
            StringBuilder contents = new StringBuilder();
            for (Task task : taskList.getTasks()) {
                contents.append(task.toFileString()).append(System.lineSeparator());
            }
            byte[] bytes = contents.toString().getBytes(StandardCharsets.UTF_8);
            Files.write(temporaryFile, bytes);
            // Replace only after the complete new contents have been written.
            Files.move(temporaryFile, destination, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
            savedContents = bytes;
            return true;
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
