# Console UI test record

Java 25; exact output comparison; fresh directory per case.

## TC-01: Start and exit cleanly — PASS

### Input

```text
bye
```

### Actual output

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

## TC-13: Find tasks by keyword — PASS

### Input

```text
todo read book
deadline return book /by 2019-06-06
find BOOK
find magazine
bye
```

### Actual output

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
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Rest your eyes!
____________________________________________________________
```

## TC-02: List an empty task list — PASS

### Input

```text
list
bye
```

### Actual output

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

## TC-03: Reject invalid delete commands without changing the task list — PASS

### Input

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

### Actual output

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

## TC-04: Reject invalid mark and unmark commands without changing task status — PASS

### Input

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

### Actual output

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

## TC-05: Reject malformed deadline and event formats without creating tasks — PASS

### Input

```text
deadline /by Sunday
event /from Monday /to Tuesday
event meeting
event meeting /from Monday /to
list
bye
```

### Actual output

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

## TC-06: Delete a task and renumber the remaining list — PASS

### Input

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
todo join sports club
mark 1
mark 4
todo borrow book
list
delete 3
list
bye
```

### Actual output

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
[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
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
3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
4.[T][X] join sports club
5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
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

## TC-07: Add tasks, list them, and retain their details — PASS

### Input

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
todo join sports club
mark 1
mark 4
todo borrow book
list
deadline return book /by 2019-12-01
event project meeting /from 2019-08-12 /to 2019-08-13
bye
```

### Actual output

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
[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
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
3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
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
[E][ ] project meeting (from: Aug 12 2019 to: Aug 13 2019)
Now you have 7 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. Rest your eyes!
____________________________________________________________
```

## TC-08: Reject malformed commands without changing the task list — PASS

### Input

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

### Actual output

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

## TC-09: Handle blank, unknown, and incomplete commands without changing the task list — PASS

### Input

```text

todo retained task
unknown
list
todo
list
bye
```

### Actual output

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

## TC-10: Reject invalid date format without creating tasks — PASS

### Input

```text
deadline return book /by invalid-date
event team meeting /from 2019-02-30 /to 2019-03-01
list
bye
```

### Actual output

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

## TC-11: List tasks occurring on a specific date — PASS

### Input

```text
todo read book
deadline return book /by 2019-10-15
deadline submit report /by 2019-10-20
event career fair /from 2019-10-14 /to 2019-10-16
list_by_date 2019-10-15
list_by_date 2019-10-20
list_by_date 2019-10-18
bye
```

### Actual output

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
[D][ ] return book (by: Oct 15 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[D][ ] submit report (by: Oct 20 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[E][ ] career fair (from: Oct 14 2019 to: Oct 16 2019)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list that matches the date 2019-10-15:
1.[D][ ] return book (by: Oct 15 2019)
2.[E][ ] career fair (from: Oct 14 2019 to: Oct 16 2019)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list that matches the date 2019-10-20:
1.[D][ ] submit report (by: Oct 20 2019)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list that matches the date 2019-10-18:
____________________________________________________________
____________________________________________________________
Bye. Rest your eyes!
____________________________________________________________
```

## TC-12: Reject invalid arguments for list_by_date — PASS

### Input

```text
list_by_date
list_by_date 2019-13-01
list_by_date invalid-date
bye
```

### Actual output

```text
____________________________________________________________
JAMES THE CHATTY CHATBOT
Hello! I'm James.
I can do anything for you!
____________________________________________________________

____________________________________________________________
OH NO James Doesnt Know What To Do!!!
Please provide a date in the format: yyyy-mm-dd
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
The date format provided is incorrect! Please use the format: yyyy-mm-dd
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
The date format provided is incorrect! Please use the format: yyyy-mm-dd
____________________________________________________________
____________________________________________________________
Bye. Rest your eyes!
____________________________________________________________
```

## TC-14: Undo changes in reverse order — PASS

### Input

```text
undo
todo read book
mark 1
mark 1
find book
undo
list
delete 1
undo
list
undo
undo
bye
```

### Actual output

```text
____________________________________________________________
JAMES THE CHATTY CHATBOT
Hello! I'm James.
I can do anything for you!
____________________________________________________________

____________________________________________________________
Nothing to undo.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
[T][X] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
[T][X] read book
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][X] read book
____________________________________________________________
____________________________________________________________
Undid the last change.
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
[T][ ] read book
Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
Undid the last change.
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Undid the last change.
Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
Nothing to undo.
____________________________________________________________
____________________________________________________________
Bye. Rest your eyes!
____________________________________________________________
```

## TC-15: Reject invalid data and recover — PASS

### Input

```text
  todo   keep  
todo keep
event trip /from 2026-09-16 /to 2026-09-16
event trip /from 2026-09-17 /to 2026-09-16
deadline report /by 2026-02-30
deadline report /by 2026-09-16 /by 2026-09-17
todo bad | record
bye extra
list extra
list
bye
```

### Actual output

```text
____________________________________________________________
JAMES THE CHATTY CHATBOT
Hello! I'm James.
I can do anything for you!
____________________________________________________________

____________________________________________________________
Got it. I've added this task:
[T][ ] keep
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
This task already exists in your list.
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
An event must end after its start date.
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
An event must end after its start date.
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
Formatting of the date is incorrect, try: yyyy-mm-dd
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
Date options must appear once and in order: /by
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
Task descriptions cannot contain | or control characters.
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
BYE does not take arguments.
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
LIST does not take arguments.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] keep
____________________________________________________________
____________________________________________________________
Bye. Rest your eyes!
____________________________________________________________
```

## TC-16: Protect corrupted storage — PASS

### Input

```text
todo keep
undo
bye
```

### Actual output

```text
Warning: Skipping invalid saved task entry: broken record
____________________________________________________________
JAMES THE CHATTY CHATBOT
Hello! I'm James.
I can do anything for you!
____________________________________________________________

____________________________________________________________
OH NO James Doesnt Know What To Do!!!
Saved tasks could not be fully loaded. No changes were made.
Repair the saved file or restore read access, then restart James.
____________________________________________________________
____________________________________________________________
Nothing to undo.
____________________________________________________________
____________________________________________________________
Bye. Rest your eyes!
____________________________________________________________
```

## TC-17: Request stickers without changing tasks — PASS

### Input

```text
random_sticker
random_sticker
random_sticker extra
list
undo
bye
```

### Actual output

```text
____________________________________________________________
JAMES THE CHATTY CHATBOT
Hello! I'm James.
I can do anything for you!
____________________________________________________________

____________________________________________________________
Here's a random sticker!
____________________________________________________________
____________________________________________________________
Here's a random sticker!
____________________________________________________________
____________________________________________________________
OH NO James Doesnt Know What To Do!!!
RANDOM_STICKER does not take arguments.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Nothing to undo.
____________________________________________________________
____________________________________________________________
Bye. Rest your eyes!
____________________________________________________________
```

All 17 cases passed.
