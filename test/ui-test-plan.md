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

## TC-02: Add tasks, list them, and retain their details

**Aim:** Verify that tasks can be added, marked, listed, and that deadline and event details are displayed in the expected format.

**Inputs:**

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
todo join sports club
mark 1
mark 4
todo borrow book
list
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
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
[D][ ] return book (by: June 6th)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
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
2.[D][ ] return book (by: June 6th)
3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
4.[T][X] join sports club
5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[D][ ] return book (by: Sunday)
Now you have 6 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 7 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. Rest your eyes!
____________________________________________________________
```

## TC-03: Reject malformed commands without changing the task list

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

## TC-04: Handle blank, unknown, and incomplete commands without changing the task list

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
James hasn't head of this command :(
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
