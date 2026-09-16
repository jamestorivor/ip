# James the ぱんどろぼう User Guide

James is a mischievous bread thief who keeps your tasks in his bread basket.
He speaks mostly English, with the occasional Japanese cheer or apology.

// Product screenshot goes here

// Product intro goes here

## Adding deadlines

// Describe the action and its outcome.

// Give examples of usage

Example: `keyword (optional arguments)`

// A description of the expected outcome goes here

```
expected output
```

## Feature ABC

// Feature details


## Feature XYZ

// Feature details

### Undoing a change: `undo`

Reverses the most recent successful task change. Enter `undo` repeatedly to
reverse up to 20 changes from the current session. Adding tasks, deleting tasks,
and changing completion status can be undone. Deleted tasks return to their
original positions with their details and completion status intact.

Listing, searching, invalid commands, and marking a task that is already done
(or unmarking one that is already incomplete) do not consume undo history.
Restored tasks are saved automatically. Undo history clears when James closes;
redo and selecting an older command directly are not supported.

Example: `todo read book`, `mark 1`, `undo` leaves the task incomplete;
a second `undo` removes it. When history is empty, James says `Not a crumb to retrace. Nothing to undo.`
