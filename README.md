# Java Quiz Application

This is a desktop-based Java quiz application built using Swing and JFreeChart. It allows users to register, take category-based quizzes, track performance statistics, and export results. The project supports progress saving and resuming, detailed result analysis, and a user-friendly interface.

## Features

- User Registration and Login
- Category-based Quiz System
- Save and Exit Functionality (resume where left off)
- Real-time Question Timer
- Export Results to CSV
- Detailed Explanations after Quiz
- Statistics Dashboard (graphical representation using JFreeChart)
- Persistent User History
- Clean and Modern UI with responsive layout



## How to Run

### Prerequisites

- Java JDK 8 or above
- VS Code / IntelliJ / any IDE or terminal
- [JFreeChart 1.5.6](https://sourceforge.net/projects/jfreechart/) (already placed in `libs/`)

### Compile and Run (Using Terminal)

```bash

javac -cp libs/jfreechart-1.5.6.jar -d out src/main/java/quizapp/*.java
java -cp "out;libs/jfreechart-1.5.6.jar" quizapp.QuizAppGUI

```


> Use `:` instead of `;` on macOS/Linux in classpath.


## Question Format (Example)

Each quiz category is a `.txt` file under `resources/questions/`.  
Format per question:

Q: What is the capital of France?

Berlin

Madrid

Paris

Rome
A: 3
E: Paris is the capital of France.



## Data Storage

- User progress is saved in `progress_<username>.dat`
- Results are exported as `quiz_results_<username>.csv`
- User stats stored persistently via `UserStatsManager` using CSV/serialized formats

