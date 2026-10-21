
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Locale;

public class Main {

    static Frame mainFrame;

    static class Member {
        String name;
        double paid = 0;
        double balance = 0;

        Member(String name) {
            this.name = name;
        }
    }

    static class Expense {
        String description;
        double amount;
        Member payer;

        Expense(String description, double amount, Member payer) {
            this.description = description;
            this.amount = amount;
            this.payer = payer;
        }
    }

    static ArrayList<Member> members = new ArrayList<>();
    static ArrayList<Expense> expenses = new ArrayList<>();

    static TextField memberField = new TextField(15);
    static TextField descriptionField = new TextField(12);
    static TextField amountField = new TextField(8);

    static Choice payerChoice = new Choice();
    static TextArea output = new TextArea("", 15, 60,
            TextArea.SCROLLBARS_VERTICAL_ONLY);

    static String money(double amount) {
        return String.format(Locale.US, "Rs. %.2f", amount);
    }

    static Member findMember(String name) {
        for (Member m : members) {
            if (m.name.equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }

    static void refreshDisplay() {
        StringBuilder text = new StringBuilder();

        text.append("========== SPLITEASE ==========\n");
        text.append("Smart Bill Splitting System\n\n");

        text.append("GROUP MEMBERS\n");
        if (members.isEmpty()) {
            text.append("No members added yet.\n");
        }

        for (Member m : members) {
            text.append("- ").append(m.name).append("\n");
        }

        text.append("\nEXPENSE HISTORY\n");
        if (expenses.isEmpty()) {
            text.append("No expenses added yet.\n");
        }

        for (Expense e : expenses) {
            text.append("- ").append(e.description)
                .append(" | ").append(money(e.amount))
                .append(" | Paid by: ").append(e.payer.name)
                .append("\n");
        }

        output.setText(text.toString());
    }

    static void addMember() {
        String name = memberField.getText().trim();

        if (name.isEmpty()) {
            showMessage("Please enter a member name.");
            return;
        }

        if (findMember(name) != null) {
            showMessage("This member already exists.");
            return;
        }

        Member member = new Member(name);
        members.add(member);
        payerChoice.add(name);

        memberField.setText("");
        refreshDisplay();
        showMessage("Member added: " + name);
    }

    static void addExpense() {
        if (members.isEmpty()) {
            showMessage("Please add a member first.");
            return;
        }

        String description = descriptionField.getText().trim();
        String amountText = amountField.getText().trim();

        if (description.isEmpty() || amountText.isEmpty()) {
            showMessage("Enter the description and amount.");
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(amountText);

            if (!Double.isFinite(amount) || amount <= 0) {
                showMessage("Amount must be greater than zero.");
                return;
            }

            amount = Math.round(amount * 100.0) / 100.0;

        } catch (NumberFormatException ex) {
            showMessage("Please enter a valid amount.");
            return;
        }

        Member payer = findMember(payerChoice.getSelectedItem());

        if (payer == null) {
            showMessage("Please select who paid.");
            return;
        }

        expenses.add(new Expense(description, amount, payer));
        payer.paid += amount;

        descriptionField.setText("");
        amountField.setText("");

        refreshDisplay();
        showMessage("Expense added successfully!");
    }

    static void calculateBalances() {
        if (members.isEmpty()) {
            showMessage("Add members before calculating balances.");
            return;
        }

        if (expenses.isEmpty()) {
            showMessage("Add at least one expense first.");
            return;
        }

        double total = 0;

        for (Expense e : expenses) {
            total += e.amount;
        }

        double share = total / members.size();

        for (Member m : members) {
            m.balance = m.paid - share;
        }

        StringBuilder result = new StringBuilder();

        result.append("========== BILL SUMMARY ==========\n\n");
        result.append("Total expenditure: ").append(money(total)).append("\n");
        result.append("Number of members: ").append(members.size()).append("\n");
        result.append("Equal share per person: ")
              .append(money(share)).append("\n\n");

        result.append("MEMBER BALANCES\n");
        result.append("----------------------------------\n");

        for (Member m : members) {
            result.append(m.name)
                  .append(" | Paid: ").append(money(m.paid))
                  .append(" | Share: ").append(money(share))
                  .append(" | Balance: ")
                  .append(m.balance >= 0 ? "+" : "-")
                  .append(money(Math.abs(m.balance)))
                  .append(m.balance > 0.005 ? " (gets money)" :
                          m.balance < -0.005 ? " (owes money)" : " (settled)")
                  .append("\n");
        }

        // Prepare balances in integer paise to avoid
        // floating-point errors during settlement.
        long[] balances = new long[members.size()];

        for (int i = 0; i < members.size(); i++) {
            balances[i] = Math.round(members.get(i).balance * 100);
        }

        result.append("\nSUGGESTED SETTLEMENTS\n");
        result.append("----------------------------------\n");

        int payments = 0;
        int debtIndex = 0;
        int creditIndex = 0;

        while (true) {
            while (debtIndex < balances.length && balances[debtIndex] >= 0) {
                debtIndex++;
            }

            while (creditIndex < balances.length && balances[creditIndex] <= 0) {
                creditIndex++;
            }

            if (debtIndex >= balances.length ||
                creditIndex >= balances.length) {
                break;
            }

            long transfer = Math.min(
                -balances[debtIndex],
                balances[creditIndex]
            );

            result.append(members.get(debtIndex).name)
                  .append(" pays ")
                  .append(members.get(creditIndex).name)
                  .append(": ")
                  .append(money(transfer / 100.0))
                  .append("\n");

            balances[debtIndex] += transfer;
            balances[creditIndex] -= transfer;
            payments++;
        }

        if (payments == 0) {
            result.append("Everyone is settled!\n");
        }

        result.append("\nNote: Expenses are split equally among all members.");

        output.setText(result.toString());
    }

    
    static void showMessage(String message) {
        Dialog dialog = new Dialog(mainFrame, "SplitEase", true);
        dialog.setLayout(new BorderLayout(10, 10));

        Label messageLabel = new Label(message, Label.CENTER);
        Button okButton = new Button("OK");

        Panel buttonPanel = new Panel(new FlowLayout());
        buttonPanel.add(okButton);

        dialog.add(messageLabel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        okButton.addActionListener(e -> dialog.dispose());

        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dialog.dispose();
            }
        });

        dialog.setSize(350, 120);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {

        Frame frame = new Frame("SplitEase - Smart Bill Splitting System");
        mainFrame = frame;
        frame.setLayout(new BorderLayout(8, 8));

        Label title = new Label(
                "SPLITEASE - Smart Bill Splitting System",
                Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        Panel memberPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        memberPanel.add(new Label("Member name:"));
        memberPanel.add(memberField);

        Button addMemberButton = new Button("Add Member");
        memberPanel.add(addMemberButton);

        Panel expensePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        expensePanel.add(new Label("Description:"));
        expensePanel.add(descriptionField);
        expensePanel.add(new Label("Amount:"));
        expensePanel.add(amountField);
        expensePanel.add(new Label("Paid by:"));
        expensePanel.add(payerChoice);

        Button addExpenseButton = new Button("Add Expense");
        expensePanel.add(addExpenseButton);

        Panel topPanel = new Panel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);

        Panel formsPanel = new Panel(new GridLayout(2, 1));
        formsPanel.add(memberPanel);
        formsPanel.add(expensePanel);
        topPanel.add(formsPanel, BorderLayout.CENTER);

        Button calculateButton = new Button("Calculate Balances");
        Button clearButton = new Button("Clear All Data");

        Panel buttonsPanel = new Panel(new FlowLayout());
        buttonsPanel.add(calculateButton);
        buttonsPanel.add(clearButton);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(output, BorderLayout.CENTER);
        frame.add(buttonsPanel, BorderLayout.SOUTH);

        addMemberButton.addActionListener(e -> addMember());
        addExpenseButton.addActionListener(e -> addExpense());
        calculateButton.addActionListener(e -> calculateBalances());

        clearButton.addActionListener(e -> {
            members.clear();
            expenses.clear();
            payerChoice.removeAll();
            memberField.setText("");
            descriptionField.setText("");
            amountField.setText("");
            refreshDisplay();
        });

        frame.setSize(850, 550);
        frame.setLocationRelativeTo(null);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                frame.dispose();
            }
        });

        refreshDisplay();
        frame.setVisible(true);
    }
}
