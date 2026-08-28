# UI Test Plan

## Test environment

- Java version: 25
- Build command: `javac -d out/production/ip src/main/java/*.java`
- Launch command: `java -cp out/production/ip James`
- Comparison: expected output is compared exactly, including line breaks and spaces.
- Isolation: each test case starts a new application session.

## TC-01: Start and exit cleanly

**Aim:** Verify that the application shows its greeting and exit message when the user exits immediately.

**Inputs:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
JAMES THE CHATTY CHATBOT
Hello! I'm James.
I can do anything for you!
____________________________________________________________

____________________________________________________________
Bye. Rest your eyes!
____________________________________________________________
```

## TC-02: List an empty task list

**Aim:** Verify that listing tasks before any task is added displays the empty-list heading without creating a task.

**Inputs:**

```text
list
bye
```

**Expected output:**

```text
____________________________________________________________
JAMES THE CHATTY CHATBOT
Hello! I'm James.
I can do anything for you!
____________________________________________________________

____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Rest your eyes!
____________________________________________________________
```

## TC-03: Reject invalid delete commands without changing the task list

**Aim:** Verify that deletion rejects a missing, non-numeric, zero, and out-of-range task number; the valid task must remain in the list after every error.

**Inputs:**

```text
todo protect task
delete
list
delete one
list
delete 0
list
delete 2
list
bye
```

**Expected output:**

```text
____________________________________________________________
JAMES THE CHATTY CHATBOT
Hello! I'm James.
I can do anything for you!
____________________________________________________________

____________________________________________________________
Got it. I've added this task:
[T][ ] protect task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
James asks that you provide a task number.
Try: delete <task number>
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] protect task
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
James says that the task number must be a whole number.
Try: delete <task number>
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] protect task
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
James says there is no task number 0.
Your list currently has 1 tasks.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] protect task
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
James says there is no task number 2.
Your list currently has 1 tasks.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] protect task
____________________________________________________________
____________________________________________________________
Bye. Rest your eyes!
____________________________________________________________
```

## TC-04: Reject invalid mark and unmark commands without changing task status

**Aim:** Verify that invalid mark and unmark task numbers preserve the task and its incomplete status; valid mark and unmark commands must still work afterwards.

**Inputs:**

```text
todo persistent task
mark
unmark nope
mark 0
unmark 2
list
mark 1
unmark 1
list
bye
```

**Expected output:**

```text
____________________________________________________________
JAMES THE CHATTY CHATBOT
Hello! I'm James.
I can do anything for you!
____________________________________________________________

____________________________________________________________
Got it. I've added this task:
[T][ ] persistent task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
James asks that you provide a task number.
Try: mark <task number>
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
James says that the task number must be a whole number.
Try: unmark <task number>
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
James says there is no task number 0.
Your list currently has 1 tasks.
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
James says there is no task number 2.
Your list currently has 1 tasks.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] persistent task
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
[T][X] persistent task
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
[T][ ] persistent task
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] persistent task
____________________________________________________________
____________________________________________________________
Bye. Rest your eyes!
____________________________________________________________
```

## TC-05: Reject malformed deadline and event formats without creating tasks

**Aim:** Verify that deadlines without a description and events with missing descriptions, `/from`, or `/to` values are rejected; the final list must remain empty.

**Inputs:**

```text
deadline /by Sunday
event /from Monday /to Tuesday
event meeting
event meeting /from Monday /to
list
bye
```

**Expected output:**

```text
____________________________________________________________
JAMES THE CHATTY CHATBOT
Hello! I'm James.
I can do anything for you!
____________________________________________________________

____________________________________________________________
OH NO James Doesnt Know What To Do!!!
A deadline needs a by date.
Try: deadline <description> /by <date>
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
An event needs a description followed by /from.
Try: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
An event needs a description followed by /from.
Try: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
An event needs both a start and end time.
Try: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Rest your eyes!
____________________________________________________________
```

## TC-06: Delete a task and renumber the remaining list

**Aim:** Verify that deleting a valid task removes the selected task, reports the updated task count, and preserves the remaining tasks in order.

**Inputs:**

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-06
todo join sports club
mark 1
mark 4
todo borrow book
list
delete 3
list
bye
```

**Expected output:**

```text
____________________________________________________________
JAMES THE CHATTY CHATBOT
Hello! I'm James.
I can do anything for you!
____________________________________________________________

____________________________________________________________
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[E][ ] project meeting (from: Aug 06 2019 to: Aug 06 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
[T][X] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
[T][X] join sports club
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] borrow book
Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 06 2019)
4.[T][X] join sports club
5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
[E][ ] project meeting (from: Aug 06 2019 to: Aug 06 2019)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[T][X] join sports club
4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Bye. Rest your eyes!
____________________________________________________________
```

## TC-07: Add tasks, list them, and retain their details

**Aim:** Verify that tasks can be added, marked, listed, and that deadline and event details are displayed in the expected format.

**Inputs:**

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-06
todo join sports club
mark 1
mark 4
todo borrow book
list
deadline return book /by 2019-12-01
event project meeting /from 2019-08-12 /to 2019-08-12
bye
```

**Expected output:**

```text
____________________________________________________________
JAMES THE CHATTY CHATBOT
Hello! I'm James.
I can do anything for you!
____________________________________________________________

____________________________________________________________
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[E][ ] project meeting (from: Aug 06 2019 to: Aug 06 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
[T][X] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
[T][X] join sports club
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] borrow book
Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 06 2019)
4.[T][X] join sports club
5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[D][ ] return book (by: Dec 01 2019)
Now you have 6 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[E][ ] project meeting (from: Aug 12 2019 to: Aug 12 2019)
Now you have 7 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. Rest your eyes!
____________________________________________________________
```

## TC-08: Reject malformed commands without changing the task list

**Aim:** Verify that invalid deadline, event, and task-number inputs show the current error message, while valid operations before and after them leave the single task in the expected state.

**Inputs:**

```text
todo keep this task
deadline submit report
list
event team meeting /from Monday 2pm
list
mark two
mark 1
unmark 1
bye
```

**Expected output:**

```text
____________________________________________________________
JAMES THE CHATTY CHATBOT
Hello! I'm James.
I can do anything for you!
____________________________________________________________

____________________________________________________________
Got it. I've added this task:
[T][ ] keep this task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
A deadline needs a by date.
Try: deadline <description> /by <date>
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] keep this task
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
An event needs both a start and end time.
Try: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] keep this task
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
James says that the task number must be a whole number.
Try: mark <task number>
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
[T][X] keep this task
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
[T][ ] keep this task
____________________________________________________________
____________________________________________________________
Bye. Rest your eyes!
____________________________________________________________
```

## TC-09: Handle blank, unknown, and incomplete commands without changing the task list

**Aim:** Verify that a blank command, an unknown command, and a todo without a description report errors; valid commands interleaved between them must preserve the one valid task.

**Inputs:**

```text

todo retained task
unknown
list
todo
list
bye
```

**Expected output:**

```text
____________________________________________________________
JAMES THE CHATTY CHATBOT
Hello! I'm James.
I can do anything for you!
____________________________________________________________

____________________________________________________________
OH NO James Doesnt Know What To Do!!!
No command specified
Try: <command> <arguments:optional>
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] retained task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
James hasn't heard of this command :(
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] retained task
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
The description of a todo cannot be empty.
Try: todo <description>
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] retained task
____________________________________________________________
____________________________________________________________
Bye. Rest your eyes!
____________________________________________________________
```

## TC-10: Reject invalid date format without creating tasks

**Aim:** Verify that deadlines and events with invalid or malformed dates are rejected with formatting guidance; no tasks must be added.

**Inputs:**

```text
deadline return book /by invalid-date
event team meeting /from 2019-02-30 /to 2019-03-01
list
bye
```

**Expected output:**

```text
____________________________________________________________
JAMES THE CHATTY CHATBOT
Hello! I'm James.
I can do anything for you!
____________________________________________________________

____________________________________________________________
OH NO James Doesnt Know What To Do!!!
Formatting of the date is incorrect, try: yyyy-mm-dd
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
Formatting of the date is incorrect, try: yyyy-mm-dd
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Rest your eyes!
____________________________________________________________
```
