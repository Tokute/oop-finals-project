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
    ├── User.java                 # Abstract base class for user accounts
    ├── Admin.java                # Administrator role implementation
    ├── TeachingAssistant.java    # Teaching assistant role implementation
    ├── Task.java                 # Abstract base class for tasks
    ├── TeachingTask.java         # Teaching and lab session task subclass
    └── GradingTask.java          # Grading assignment task subclass

## Checklist / TO-DO

- [ ] Integrate user preferences into compatibleShifts() logic
- [ ] Refactor compatibleShifts() to return aggregated multi-shift arrays rather than printing individual lines
- [ ] Finalize stress and workload tracking architecture
- [ ] Implement Login/Sign-Up workflows for Admin and User roles
- [ ] Develop "ID Creator" utility for unique identifier generation
- [ ] Establish local text-based database structure (database.txt)