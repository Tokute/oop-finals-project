# Teaching Assistant Scheduler (TAS) School Project

# **Project Status: Complete - Core Functionality Implemented**

Project description as provided:

    The Teaching Assistant Scheduler (TAS) is a system designed to automate and optimize the assignment of teaching assistants (TAs) to courses, lab sessions, and grading assignments within an academic institution. Built using object-oriented programming (OOP) principles, the system ensures modularity, scalability, and maintainability. TAS supports administrators in managing workloads, avoiding scheduling conflicts, and ensuring fair distribution of responsibilities based on qualifications, availability, and preferences.

---

## Key Features

    - Object-Oriented Programming Application (Inheritance, Polymorphism, Encapsulation)
    - Role-based access control (Admin can register/modify/delete users and tasks)
    - Interactive terminal-based menu system
    - Local persistence using text-based database (database.txt)
    - Unique ID generation for all entities
    - Input validation during registration (time formats, numeric fields, etc.)
    - TA-Task matching based on three criteria:
        1. Expertise compatibility
        2. Preference compatibility  
        3. Time schedule overlap availability
    - Immediate persistence after registration/modification/deletion operations
    - Test data generation on first run (when database is empty)
    - Modification and deletion capabilities for existing records
    - Error handling and validation feedback

---

## System Architecture Overview

    ScheduleManager.java is the main controller handling:
        - User and task management (registration, modification, deletion)
        - Compatibility checking between Teaching Assistants and Tasks
        - Data persistence via ScheduleRepository
        - Initialization and test data generation coordination

    User.java is the abstract base class for users with:
        - Admin.java (system administrator role)
        - TeachingAssistant.java (stores expertise, preferences, available time)

    Task.java is the abstract base class for tasks with:
        - TeachingTask.java (teaching/lab sessions with subject)
        - GradingTask.java (grading assignments with total papers)

    TimeUtils.java handles:
        - Time string to minutes conversion
        - Time overlap detection
        - Time detail validation and normalization
        - Interactive time input with validation

    IDCreator.java generates unique sequential IDs:
        - Admin: A-001, A-002, ...
        - Teaching Assistant: TA-001, TA-002, ...
        - Teaching Task: TT-001, TT-002, ...
        - Grading Task: GT-001, GT-002, ...

    RegistrationService.java (used for menu option 4) provides:
        - Interactive registration with field-by-field validation
        - Time detail validation and normalization
        - Support for multiple time periods per user/task
        - Error handling with re-prompts for invalid input
        - Factory methods for creating validated objects

---

## Project File Structure

    ├── Main.java                 # Entry point, menu system, and high-level workflow coordination
    ├── ScheduleManager.java      # Core system controller, persistence, and matching algorithms
    ├── RegistrationService.java  # Interactive registration with input validation (menu option 4)
    ├── ScheduleRepository.java   # Handles loading from and saving to database.txt
    ├── IDCreator.java            # Utility for generating unique sequential identifiers
    ├── TimeUtils.java            # Time conversion, validation, and overlap checking utilities
    ├── users/                    # User-related classes
    │   ├── User.java             # Abstract base class for user accounts
    │   ├── Admin.java            # Administrator role implementation
    │   └── TeachingAssistant.java # Teaching assistant role implementation
    └── tasks/                    # Task-related classes
        ├── Task.java             # Abstract base class for tasks
        ├── TeachingTask.java     # Teaching and lab session task subclass
        └── GradingTask.java      # Grading assignment task subclass

---

## Recent Improvements (as of 2026-10-14)

### Core Matching Algorithm
- Implemented Expertise → Preferences → Time Overlap matching logic exactly as specified
- `calculateAvailableShift()` checks:
  1. Expertise compatibility via `Task.isCompatible(TeachingAssistant)`
  2. Preference compatibility via `ScheduleManager.isPreferenceCompatible()`
  3. Time overlap via `ScheduleManager.evaluateTimeOverlap()` → `TimeUtils.overlaps()`

### Input Validation & Registration
- Added `RegistrationService.java` for menu option 4 with comprehensive validation:
  - Time details: Validates format "Day HH:MM-HH:MM", checks day validity, time ranges, and order
  - Numeric fields: Validates work hours (double) and total papers (integer) with re-prompts
  - Multiple time details: Allows users to enter multiple time periods for a single entity
  - Factory methods: Creates properly-ID'd objects using `IDCreator`
- Updated `TimeUtils.java` to remove internal try-catch in `toMinutes()` and let exceptions propagate for handling in validation layers
- Time validation now occurs during registration, preventing invalid time formats from being stored

### Persistence & Data Management
- Enhanced `ScheduleRepository` for robust loading/saving with backup/temp file handling
- Automatic ID tracking via `IDCreator.observeExistingId()` during database load
- First-run test data generation only when database.txt doesn't exist or is empty
- Immediate persistence after registration/modification/deletion operations
- Fixed potential database corruption issues with atomic save operations

### User Interface & Experience
- Expanded menu options beyond basic CRUD to include modification and deletion
- Clear error messages and re-prompts for invalid input during registration
- Formatted output for compatible shifts showing aggregated time slots per TA-Task pair
- Informative prompts and confirmation messages for all operations
- Proper handling of edge cases (empty data, invalid selections, etc.)

### Code Quality & Maintenance
- Proper package organization (users/, tasks/ packages)
- Consistent use of getters/setters and encapsulation
- Meaningful variable names and code comments
- Handling of null/empty cases throughout the codebase
- Atomic database operations with backup and recovery mechanisms

---

## Core Functionality Verified

✅ **ID Generation**: Unique sequential IDs for all entity types  
✅ **Persistence**: Local database storage with load on startup and save after changes  
✅ **Matching Algorithm**: Expertise → Preferences → Time Overlap compatibility checking  
✅ **Input Validation**: Registration validates time formats, numeric fields, and prevents empty details  
✅ **CRUD Operations**: Create (Register), Read (View), Update (Modify), Delete (Delete)  
✅ **Role-Based Access**: Only Admin can perform registration/modification/deletion operations  
✅ **Error Handling**: Graceful handling of invalid input with user re-prompts  
✅ **Test Data**: Automatic generation of diverse test users/tasks on first run  
✅ **Time Overlap**: Accurate detection of schedule overlaps using minute-based comparison  

The Teaching Assistant Scheduler successfully implements the core OOP principles and scheduling logic as specified in the requirements, with additional enhancements for usability and robustness.