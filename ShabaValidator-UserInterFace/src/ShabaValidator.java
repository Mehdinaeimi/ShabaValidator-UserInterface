import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Logger;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.ConsoleHandler;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
public class ShabaValidator {
    private static final Logger LOGGER = Logger.getLogger(ShabaValidator.class.getName());

    static {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter() {
            @Override
            public String format(LogRecord record) {
                ZoneId zoneId = ZoneId.of("Asia/Tehran");
                ZonedDateTime zdt = ZonedDateTime.now(zoneId);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss", Locale.US);
                String persianDate = zdt.format(formatter);
                return String.format("[%s] %s%n", persianDate, record.getMessage());
            }
        });
        LOGGER.addHandler(consoleHandler);

        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler("Log.log", true);
            fileHandler.setFormatter(new SimpleFormatter() {
                @Override
                public String format(LogRecord record) {
                    ZoneId zoneId = ZoneId.of("Asia/Tehran");
                    ZonedDateTime zdt = ZonedDateTime.now(zoneId);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss", Locale.US);
                    String persianDate = zdt.format(formatter);
                    return String.format("[%s] %s%n", persianDate, record.getMessage());
                }
            });
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            LOGGER.severe("Error creating file handler: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Shaba Validator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Enter Shaba number:");
        JTextField textField = new JTextField(20);

        JButton validateButton = new JButton("Validate");
        validateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String shaba = textField.getText();
                if (isValidShaba(shaba)) {
                    String bankCode = shaba.substring(2, 5);
                    String accountNumber = shaba.substring(5);
                    String bankName = getBankName(bankCode);
                    String[] messageLines = new String[] {
                            "Shaba is valid:",
                            "Bank Name: " + bankName,
                            "CD: " + bankCode,
                            "BBAN: " + accountNumber
                    };

                    JOptionPane.showMessageDialog(frame, String.join("\n", messageLines));

                    try {
                        writeToFile("valid_shabas.txt", shaba + "," + bankName + "," + bankCode + "," + accountNumber + "\n");
                    } catch (IOException ex) {
                        System.err.println("Error writing to file: " + ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Shaba is invalid");

                    try {
                        writeToFile("invalid_shabas.txt", shaba + "\n");
                    } catch (IOException ex) {
                        System.err.println("Error writing to file: " + ex.getMessage());
                    }
                }

                try {
                    writeToFile("user_input.txt", shaba + "\n");
                } catch (IOException ex) {
                    System.err.println("Error writing to file: " + ex.getMessage());
                }
            }
        });

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((screenSize.getWidth() - frame.getWidth()) / 2.5);
        int y = (int) ((screenSize.getHeight() - frame.getHeight()) / 2.5);
        frame.setLocation(x, y);
        frame.getContentPane().add(label, BorderLayout.NORTH);
        frame.getContentPane().add(textField, BorderLayout.CENTER);
        frame.getContentPane().add(validateButton, BorderLayout.SOUTH);
        frame.setSize(300, 100);
        frame.setVisible(true);
    }

    private static void writeToFile(String fileName, String data) throws IOException {
        FileWriter fileWriter = new FileWriter(fileName, true);
        fileWriter.write(data);
        fileWriter.close();
    }

    private static String getBankName(String bankCode) {
        switch (bankCode) {
            case "020":
                return "بانک توسعه صادرات";
            case "055":
                return "بانک اقتصاد نوین";
            case "054":
                return "بانک پارسیان";
            case "057":
                return "بانک پاسارگاد";
            case "021":
                return "پست بانک ایران";
            case "018":
                return "بانک تجارت";
            case "051":
                return "موسسه اعتباری توسعه";
            case "013":
                return "بانک رفاه";
            case "056":
                return "بانک سامان";
            case "015":
                return "بانک سپه";
            case "058":
                return "بانک سرمایه";
            case "019":
                return "بانک صادرات ایران";
            case "011":
                return "بانک صنعت و معدن";
            case "053":
                return "بانک کارآفرین";
            case "016":
                return "بانک کشاورزی";
            case "010":
                return "بانک مرکزی";
            case "014":
                return "بانک مسکن";
            case "012":
                return "بانک ملت";
            case "017":
                return "بانک ملی";
            default:
                return "بانک نا مشخص";
        }
    }


    private static boolean isValidShaba(String shaba) {
        String bankCode = shaba.substring(2, 5);
        return shaba.length() == 26 && shaba.startsWith("IR") && bankCode.startsWith("0") && shaba.matches("[A-Z0-9]+");
    }

    private static void writeToFile(String fileName, List<String> shabas) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String shaba : shabas) {
                writer.write(shaba + "\n");
            }
        } catch (IOException e) {
            LOGGER.severe("Error writing to file: " + e.getMessage());
        }
    }
}