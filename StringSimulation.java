/**
 * @author Miłosz Demendecki s24611
 */

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * I have created an example in Excel with chart and data copied from this program
 */
public class StringSimulation extends JFrame {
    private static final ArrayList<Double> x = new ArrayList<>();
    private static final ArrayList<Double> y = new ArrayList<>();
    private static final ArrayList<Double> v = new ArrayList<>();
    private static final ArrayList<Double> a = new ArrayList<>();
    private static final ArrayList<Double> Ek = new ArrayList<>();
    private static final ArrayList<Double> Ep = new ArrayList<>();
    private static final ArrayList<Double> Et = new ArrayList<>();

    public static void main(String[] args) {
        createFrame();
    }

    /**
     * Function that adds records from list specified as parameter to text area,
     * so user can easily copy records and paste it to e.g. excel
     */
    public static void addListToArea(ArrayList<Double> list, String str, JTextArea textArea, JButton button, int rows) {
        for (int i = 0; i < rows; i++) {
            textArea.append(list.get(i).toString().replace(".", ",") + "\n");
            button.setText(str);
        }
    }

    /**
     * Function that helps to write y coordinates of string to file "data.txt"
     * then user can use Get Data function in Excel and paste content of this file into Excel
     */
    public static void writeToFile(String string, boolean append) {
        try(FileWriter fileWriter = new FileWriter("data.txt",append))
        {
            fileWriter.write(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function that calculates Ek, Ep and Et for a simulation of a string
     */
    public static void calculationsForSimulation(int rows, int n, double dt) {
        double l = Math.PI;
        double dx = l / n;

        writeToFile("", false);

        for (int i = 0; i <= n; i++) {
            x.add(i * dx);
            v.add(0.0);
            if (i == 0 || i == n) {
                y.add(0.0);
            } else {
                y.add(Math.sin(x.get(i)));
            }
        }

        for (int i = 0; i <= n; i++) {
            if (i == 0 || i == n) {
                a.add(0.0);
            } else {
                a.add((y.get(i + 1) - 2 * y.get(i) + y.get(i - 1)) / Math.pow(dx, 2));
            }
        }
        Ek.add(0.0);
        for (int i = 0; i <= n; i++) {
            Ek.set(0, Ek.get(0) + dx * (Math.pow(v.get(i), 2)) / 2);
        }
        Ep.add(0.0);
        for (int i = 0; i < n; i++) {
            Ep.set(0, Ep.get(0) + (Math.pow(y.get(i + 1) - y.get(i), 2)) / (2 * dx));
        }

        Et.add(Ep.get(0) + Ek.get(0));

        for (int j = 0; j <= n; j++) {
            writeToFile(y.get(j).toString().replace(".", ",") + " ", true);
        }
        writeToFile("\n", true);

        for (int i = 1; i < rows; i++) {

            ArrayList<Double> y2 = new ArrayList<>();
            for (int j = 0; j <= n; j++) {
                if (j != 0 && j != n) {
                    y2.add(y.get(j) + v.get(j) * dt / 2);
                } else {
                    y2.add(0.0);
                }
            }
            for (int j = 0; j <= n; j++) {
                if (j != 0 && j != n) {
                    double v2 = v.get(j) + a.get(j) * dt / 2;
                    double a2 = (y2.get(j + 1) - 2 * y2.get(j) + y2.get(j - 1)) / Math.pow(dx, 2);
                    y.set(j, y.get(j) + v2 * dt);
                    v.set(j, v.get(j) + a2 * dt);


                }
                writeToFile(y.get(j).toString().replace(".", ",") + " ", true);
            }
            for (int j = 0; j <= n; j++) {
                if (j != 0 && j != n) {
                    a.set(j, (y.get(j + 1) - 2 * y.get(j) + y.get(j - 1)) / Math.pow(dx, 2));
                }

            }
            writeToFile("\n", true);


            Ek.add(0.0);
            for (int j = 0; j <= n; j++) {
                Ek.set(i, Ek.get(i) + dx * (Math.pow(v.get(j), 2)) / 2);
            }
            Ep.add(0.0);
            for (int j = 0; j < n; j++) {
                Ep.set(i, Ep.get(i) + (Math.pow(y.get(j + 1) - y.get(j), 2)) / (2 * dx));
            }

            Et.add(Ep.get(i) + Ek.get(i));
        }
    }

    /**
     * Constructor that takes multiple parameters to calculate all values needed for
     * calculating Ek, Ep and Et
     */
    public StringSimulation(int rows, int n, double dt) {
        calculationsForSimulation(rows, n, dt);
        setTitle("Midpoint");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 300);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        for (int i = 1; i <= 3; i++) {
            JTextArea textArea = new JTextArea();
            JButton copyButton = new JButton();
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            textArea.setEditable(false);
            switch (i) {
                case 1 -> addListToArea(Ek, "Ek", textArea, copyButton, rows);
                case 2 -> addListToArea(Ep, "Ep", textArea, copyButton, rows);
                case 3 -> addListToArea(Et, "Et", textArea, copyButton, rows);
            }
            copyButton.addActionListener(e -> {
                textArea.selectAll();
                textArea.copy();
            });
            JPanel scrollPanel = new JPanel(new BorderLayout());
            scrollPanel.add(copyButton, BorderLayout.NORTH);
            scrollPanel.add(scrollPane, BorderLayout.CENTER);
            panel.add(scrollPanel);
        }
        getContentPane().add(panel);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    /**
     * Function that creates user interface
     */
    public static void createFrame() {
        JFrame startFrame = new JFrame("Choose Calculation");
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label1 = new JLabel("Provide number of rows:");
        JTextField textField = new JTextField();

        JLabel label5 = new JLabel("Provide number of points:");
        JTextField nField = new JTextField();

        JLabel label6 = new JLabel("Provide Δt value:");
        JTextField deltaTField = new JTextField();

        JButton calculateButton = new JButton("<html>Perform calculations using <br> Midpoint method</html>");
        calculateButton.addActionListener(e -> {
            try {
                new StringSimulation(Integer.parseInt(textField.getText()),
                        Integer.parseInt(nField.getText()), Double.parseDouble(deltaTField.getText()));
            } catch (NumberFormatException ex) {
                System.out.println("Try again, remember to pass all arguments");
            }
        });

        startFrame.setResizable(true);
        panel.add(label1);
        panel.add(textField);

        panel.add(label5);
        panel.add(nField);

        panel.add(label6);
        panel.add(deltaTField);

        panel.add(calculateButton);

        startFrame.getContentPane().add(panel);
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.setLocationRelativeTo(null);
        startFrame.setVisible(true);
        startFrame.pack();
    }
}