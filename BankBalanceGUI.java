import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BankBalanceGUI extends JFrame implements ActionListener {

    private final BankAccount account = new BankAccount();

    private final JTextField startingBalanceField = new JTextField(12);
    private final JTextField amountField = new JTextField(12);

    private final JButton startButton = new JButton("Set Starting Balance");
    private final JButton showButton = new JButton("Show Balance");
    private final JButton depositButton = new JButton("Deposit");
    private final JButton withdrawButton = new JButton("Withdraw");
    private final JButton exitButton = new JButton("Exit");

    private final JLabel balanceLabel = new JLabel("Enter a starting balance to begin.");
    private boolean accountStarted = false;

    public BankBalanceGUI() {
        super("Bank Balance Application");

        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.add(new JLabel("Starting balance ($):"));
        inputPanel.add(startingBalanceField);
        inputPanel.add(new JLabel("Transaction amount ($):"));
        inputPanel.add(amountField);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonPanel.add(startButton);
        buttonPanel.add(showButton);
        buttonPanel.add(depositButton);
        buttonPanel.add(withdrawButton);
        buttonPanel.add(exitButton);
        buttonPanel.add(new JLabel(""));

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(balanceLabel, BorderLayout.SOUTH);
        add(panel);

        startButton.addActionListener(this);
        showButton.addActionListener(this);
        depositButton.addActionListener(this);
        withdrawButton.addActionListener(this);
        exitButton.addActionListener(this);

        // Show the final balance even if the user clicks the window's X.
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        setSize(540, 270);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == exitButton) {
            exitApplication();
            return;
        }

        if (source == startButton) {
            try {
                double startingBalance = Double.parseDouble(startingBalanceField.getText().trim());
                if (!Double.isFinite(startingBalance) || startingBalance < 0) {
                    showError("Enter a valid starting balance of $0 or more.");
                    return;
                }
                account.deposit(startingBalance);
                accountStarted = true;
                startingBalanceField.setEditable(false);
                startButton.setEnabled(false);
                showBalance();
            } catch (NumberFormatException ex) {
                showError("Enter a valid number for the starting balance.");
            }
            return;
        }

        if (!accountStarted) {
            showError("Please set your starting balance first.");
            return;
        }

        if (source == showButton) {
            showBalance();
            return;
        }

        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            if (!Double.isFinite(amount) || amount <= 0) {
                showError("Enter a valid transaction amount greater than $0.");
                return;
            }

            if (source == depositButton) {
                account.deposit(amount);
            } else if (source == withdrawButton) {
                if (amount > account.getBalance()) {
                    showError("Insufficient funds for this withdrawal.");
                    return;
                }
                account.withdrawal(amount);
            }

            amountField.setText("");
            showBalance();
        } catch (NumberFormatException ex) {
            showError("Enter a valid number for the transaction amount.");
        }
    }

    private void showBalance() {
        balanceLabel.setText(String.format("Current balance: $%.2f", account.getBalance()));
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    private void exitApplication() {
        String message = accountStarted
                ? String.format("Remaining balance before exit: $%.2f", account.getBalance())
                : "No starting balance was entered.";
        JOptionPane.showMessageDialog(this, message, "Final Balance", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BankBalanceGUI::new);
    }
}
