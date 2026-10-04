# Teaching Assistant Scheduler (TAS) School Project

# **Current state of the project is still in progress.**

Project descrpition as provided:

    The Teaching Assistant Scheduler (TAS) is a system designed to automate and optimize the assignment of teaching assistants (TAs) to courses, lab sessions, and office hours within an academic institution. Built using object-oriented programming (OOP) principles, the system ensures modularity, scalability, and maintainability. TAS supports administrators and faculty in managing workloads, avoiding scheduling conflicts, and ensuring fair distribution of responsibilities based on qualifications, availability, and preferences.

---

## Key Features

    - Object-Oriented Programming Applicaiton (Inheritance, Polymorphism, Encapsulation).
    - Role-based Access Controls (To be Implemented)

---

## System Architecture Overview

    ScheduleManager.java is the main handler/manager of the Objects within this project.
    User.java is the base class of Users.
        - Admin.java
    Task.java is the base class of Tasks.
        - TeachingTask.java
        - GradingTask.java

---

## Project File Structure

    ├── Main.java                 # Entry point and command-line user interface loop
    ├── ScheduleManager.java      # Core system controller and matching algorithms
    ├── IDCreator.java            # Utility for generating unique identifiers
    ├── users/                    # User-related classes
    │   ├── User.java             # Abstract base class for user accounts
    │   ├── Admin.java            # Administrator role implementation
    │   └── TeachingAssistant.java # Teaching assistant role implementation
    └── tasks/                    # Task-related classes
        ├── Task.java             # Abstract base class for tasks
        ├── TeachingTask.java     # Teaching and lab session task subclass
        └── GradingTask.java      # Grading assignment task subclass

---

## Checklist / TO-DO

- [x] Integrate user preferences into compatibleShifts() logic
- [x] Refactor compatibleShifts() to return aggregated multi-shift arrays rather than printing individual lines
- [ ] Finalize stress and workload tracking architecture
- [ ] Implement Login/Sign-Up workflows for Admin and User roles
- [x] Develop "ID Creator" utility for unique identifier generation
- [ ] Establish local text-based database structure (database.txt)

---

## Recent Improvements

### File Organization
- Reorganized domain classes into meaningful packages:
  - `users/` package: User, Admin, TeachingAssistant classes
  - `tasks/` package: Task, TeachingTask, GradingTask classes
- Added proper package declarations and updated imports

### Enhanced Matching Logic
- **Integrated user preferences**: The system now checks if a Teaching Assistant's shift preferences (e.g., "Morning", "Flexible") match a task's preferred timing before considering them compatible
- **Aggregated output**: Compatible shifts are now aggregated by TA-task pair, showing all matching time slots in a single entry (e.g., "Alice can take Java Tutoring at [Tue 09:00-15:00, Thu 09:00-15:00]")

### ID Generation
- Added `IDCreator.java` utility class for generating unique sequential IDs:
  - Admin IDs: A-001, A-002, ...
  - Teaching Assistant IDs: TA-001, TA-002, ...
  - Teaching Task IDs: TT-001, TT-002, ...
  - Grading Task IDs: GT-001, GT-002, ...
- Replaced hardcoded IDs in Main.java with IDCreator-generated IDs