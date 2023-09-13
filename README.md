## Task Tracker (java-kanban)
Backend for creating the data model of a task tracker/kanban board.

#### Main Functionality and Classes
- The parent class ```Task``` describes a task.
- The classes ```Subtask``` and ```Epic``` inherit from Task.
- Large tasks (epics) consist of subtasks.

___

Interface ```TaskManager```:
- Create/update/delete tasks
- Retrieve tasks by identifier and lists of tasks
- Get a list of subtasks for an epic
- Display a list of priority tasks (sorted by start date)

Functionality has been implemented to store user data in various ways:
- In-memory storage ```InMemoryTaskManager``` (using data structures ```HashMap``` and ```TreeSet```)
- Writing and reading information from a file on the hard disk at application startup ```FileBackedTaskManager```
- Saving data to a Key-Value server via an HTTP client ```HttpTaskManager```
___

Interface ```HistoryManager```:
- Add a task to the viewing history
- Retrieve the viewing history of tasks

History storage is implemented in two scenarios:
- In-memory storage (```InMemoryHistoryManager```)
- Saving to a Key-Value server via an HTTP client

___
#### Test Coverage
The JUnit library has been used to write unit tests.

___
#### System Requirements
Java Development Kit (JDK) 11
