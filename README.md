# SplitEase – Smart Bill Splitting System

SplitEase is a Java desktop application built with **Java AWT**. It helps a group record shared expenses, calculate equal shares, view member balances, and generate suggested settlements.

## Features

- Add group members.
- Record an expense description, amount, and payer.
- Calculate total expenditure and equal share per member.
- Show whether each member should receive money or owes money.
- Suggest payments to settle balances.
- Validate common input errors.
- Clear the current session's data.

## Technologies

- Java (JDK 25 used during development)
- Java AWT for the graphical user interface
- Visual Studio Code for development

## Requirements

- A Java Development Kit (JDK) installed.
- A graphical desktop environment.
- A terminal or IDE capable of compiling and running Java.

## Run the application

Open a terminal in the folder containing `Main.java`, then run:

```bash
javac Main.java
java Main
```

If `java` or `javac` is not recognized, install a JDK and configure your environment variables.

## How to use

1. Add each group member.
2. Enter an expense description and amount.
3. Select the member who paid.
4. Click **Add Expense**.
5. Click **Calculate Balances** to view shares and suggested settlements.
6. Click **Clear All Data** to reset the current session.

## Calculation

- **Total expenditure** = sum of all recorded expenses.
- **Equal share** = total expenditure divided by the number of members.
- **Member balance** = amount paid by the member minus their equal share.

A positive balance means the member should receive money. A negative balance means the member owes money.

## Current limitations

- Expenses are divided equally among all members.
- Data is stored in memory and is lost when the application closes.
- Amounts that cannot be divided evenly may produce small currency-rounding discrepancies.
- No login, database, or online synchronization is implemented.

## Project structure

```text
SplitEase/
├── Main.java
├── README.md
├── .gitignore
└── requirements.txt
```

## Academic purpose

This project demonstrates Java classes and objects, `ArrayList`, input validation, exception handling, AWT components, and event handling.
