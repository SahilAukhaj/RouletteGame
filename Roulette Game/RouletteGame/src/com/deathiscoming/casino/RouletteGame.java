package com.deathiscoming.casino;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sahil Aukhaj
 */
public class RouletteGame {
    private final List<String> gameHistory = new ArrayList<>();
    private JLabel wheelLabel;
    private int finalNumber;
    private Color finalColor;
    private int currentNumber = 0;
    private boolean isWinningNumberPulsing = false;
    private int pulseAlpha = 0;
    private boolean pulseIncreasing = true;

    // Animation variables
    private double ballAngle = 0;
    private double bounceEffect = 0;
    private int animationFinalNumber = -1; // Track the final number during animation

    private final int[] rouletteNumbers = {
            0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36,
            11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9,
            22, 18, 29, 7, 28, 12, 35, 3, 26
    };

    private final Color[] numberColors = {
            new Color(0, 128, 0),    // 0 - Green
            new Color(220, 20, 60),   // 32 - Red
            new Color(30, 30, 30),    // 15 - Black
            new Color(220, 20, 60),   // 19 - Red
            new Color(30, 30, 30),    // 4 - Black
            new Color(220, 20, 60),   // 21 - Red
            new Color(30, 30, 30),    // 2 - Black
            new Color(220, 20, 60),   // 25 - Red
            new Color(30, 30, 30),    // 17 - Black
            new Color(220, 20, 60),   // 34 - Red
            new Color(30, 30, 30),    // 6 - Black
            new Color(220, 20, 60),   // 27 - Red
            new Color(30, 30, 30),    // 13 - Black
            new Color(220, 20, 60),   // 36 - Red
            new Color(30, 30, 30),    // 11 - Black
            new Color(220, 20, 60),   // 30 - Red
            new Color(30, 30, 30),    // 8 - Black
            new Color(220, 20, 60),   // 23 - Red
            new Color(30, 30, 30),    // 10 - Black
            new Color(220, 20, 60),   // 5 - Red
            new Color(30, 30, 30),    // 24 - Black
            new Color(220, 20, 60),   // 16 - Red
            new Color(30, 30, 30),    // 33 - Black
            new Color(220, 20, 60),   // 1 - Red
            new Color(30, 30, 30),    // 20 - Black
            new Color(220, 20, 60),   // 14 - Red
            new Color(30, 30, 30),    // 31 - Black
            new Color(220, 20, 60),   // 9 - Red
            new Color(30, 30, 30),    // 22 - Black
            new Color(220, 20, 60),   // 18 - Red
            new Color(30, 30, 30),    // 29 - Black
            new Color(220, 20, 60),   // 7 - Red
            new Color(30, 30, 30),    // 28 - Black
            new Color(220, 20, 60),   // 12 - Red
            new Color(30, 30, 30),    // 35 - Black
            new Color(220, 20, 60),   // 3 - Red
            new Color(30, 30, 30)     // 26 - Black
    };

    // UI Components
    private JComboBox<String> criteriaComboBox;
    private JComboBox<Object> numberComboBox;
    private JComboBox<Object> numberComboBox2;
    private JTextField betField;
    private JTextField gameResultField;
    private JTextField resultColorField;
    private JLabel numberLabel;
    private JLabel numberLabel2;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RouletteGame().createAndShowGUI());
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("DEATHisCOIMING Casino");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel title = new JLabel("DEATHisCOIMING Casino");
        title.setBounds(30, 20, 350, 30);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(new Color(178, 34, 34));
        frame.add(title);

        JLabel subtitle = new JLabel("Welcome to the Roulette Game");
        subtitle.setBounds(40, 55, 300, 20);
        frame.add(subtitle);

        // Create table
        JScrollPane tableScroll = createTable();
        tableScroll.setBounds(40, 90, 700, 120);
        frame.add(tableScroll);

        JLabel criteriaLabel = new JLabel("Which criteria rule do you choose?");
        criteriaLabel.setBounds(40, 230, 250, 25);
        frame.add(criteriaLabel);

        String[] criteriaOptions = {
                "Select Criteria", "1 - Single Number", "2 - Two Numbers",
                "3 - Red Color", "4 - Black Color", "5 - Odd Number",
                "6 - Even Number", "7 - Low Number (1-18)", "8 - High Number (19-36)"
        };
        criteriaComboBox = new JComboBox<>(criteriaOptions);
        criteriaComboBox.setBounds(280, 230, 200, 25);
        criteriaComboBox.addActionListener(e -> updateNumberInput());
        frame.add(criteriaComboBox);

        // Number input
        numberLabel = new JLabel("Select your bet:");
        numberLabel.setBounds(40, 270, 250, 25);
        frame.add(numberLabel);

        // Number ComboBox - will be updated based on criteria
        numberComboBox = new JComboBox<>();
        numberComboBox.setBounds(280, 270, 100, 25);
        numberComboBox.setEnabled(false);
        frame.add(numberComboBox);

        // Second Number Label (initially hidden)
        numberLabel2 = new JLabel("and second number:");
        numberLabel2.setBounds(40, 300, 150, 25);
        numberLabel2.setVisible(false);
        frame.add(numberLabel2);

        // Second Number ComboBox (initially hidden)
        numberComboBox2 = new JComboBox<>();
        numberComboBox2.setBounds(280, 300, 100, 25);
        numberComboBox2.setVisible(false);
        frame.add(numberComboBox2);

        resultColorField = new JTextField();
        resultColorField.setBounds(400, 270, 50, 25);
        resultColorField.setEditable(false);
        frame.add(resultColorField);

        // Bet input
        JLabel betLabel = new JLabel("Enter your bet amount:");
        betLabel.setBounds(40, 340, 250, 25);
        frame.add(betLabel);

        betField = new JTextField();
        betField.setBounds(280, 340, 110, 25);
        frame.add(betField);

        JButton playButton = new JButton("Play");
        playButton.setBounds(280, 380, 110, 30);
        frame.add(playButton);

        JButton historyButton = new JButton("History");
        historyButton.setBounds(400, 380, 110, 30);
        frame.add(historyButton);

        // Simple Roulette Wheel Display
        wheelLabel = new JLabel("?", SwingConstants.CENTER);
        wheelLabel.setBounds(500, 260, 80, 80);
        wheelLabel.setFont(new Font("Arial", Font.BOLD, 24));
        wheelLabel.setOpaque(true);
        wheelLabel.setBackground(Color.LIGHT_GRAY);
        wheelLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        frame.add(wheelLabel);

        JLabel resultLabel = new JLabel("Game Results:");
        resultLabel.setBounds(40, 420, 100, 25);
        frame.add(resultLabel);

        gameResultField = new JTextField();
        gameResultField.setBounds(150, 420, 400, 25);
        gameResultField.setEditable(false);
        frame.add(gameResultField);

        // Play button master logic
        playButton.addActionListener(e -> {
            if (validateInputs()) {
                playButton.setEnabled(false);
                animationFinalNumber = generateRandomRouletteNumber();
                showRouletteWheelAnimation(frame);
            }
        });

        historyButton.addActionListener(e -> showGameHistory());

        frame.setVisible(true);
    }

    // Helper method to generate random roulette number
    private int generateRandomRouletteNumber() {
        return (int) (Math.random() * 37); // 0-36
    }

    private void updateNumberInput() {
        int selectedIndex = criteriaComboBox.getSelectedIndex();
        numberComboBox.removeAllItems();
        numberComboBox.setEnabled(selectedIndex > 0);

        // Hide second combo box at first
        numberLabel2.setVisible(false);
        numberComboBox2.setVisible(false);
        numberComboBox2.removeAllItems();
        resultColorField.setBounds(400, 270, 50, 25);

        if (selectedIndex == 0) {
            numberLabel.setText("Select your bet:");
            return;
        }

        switch (selectedIndex) {
            case 1: // Single number
                numberLabel.setText("Select first number (0-36):");
                for (int i = 0; i <= 36; i++) {
                    numberComboBox.addItem(i);
                }
                break;
            case 2: // Two numbers
                numberLabel.setText("Select first number (0-36):");
                numberLabel2.setVisible(true);
                numberComboBox2.setVisible(true);
                for (int i = 0; i <= 36; i++) {
                    numberComboBox.addItem(i);
                    numberComboBox2.addItem(i);
                }
                break;
            case 3: // Red color
                numberLabel.setText("Select color:");
                numberComboBox.addItem("Red");
                break;
            case 4: // Black color
                numberLabel.setText("Select color:");
                numberComboBox.addItem("Black");
                break;
            case 5: // Odd number
                numberLabel.setText("Bet on Odd numbers");
                numberComboBox.addItem("Odd Numbers (All)");
                break;
            case 6: // Even number
                numberLabel.setText("Bet on Even numbers");
                numberComboBox.addItem("Even Numbers (All)");
                break;
            case 7: // Low numbers
                numberLabel.setText("Bet on Low numbers (1-18)");
                numberComboBox.addItem("Low Numbers (1-18)");
                break;
            case 8: // High numbers
                numberLabel.setText("Bet on High numbers (19-36)");
                numberComboBox.addItem("High Numbers (19-36)");
                break;
        }
    }

    private boolean validateInputs() {
        // validate criteria
        if (criteriaComboBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "Please select a criteria.");
            return false;
        }

        // validation for two numbers
        if (criteriaComboBox.getSelectedIndex() == 2) {
            if (numberComboBox.getSelectedItem() == null || numberComboBox2.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(null, "Please select two numbers for this bet.");
                return false;
            }
            int num1 = Integer.parseInt(numberComboBox.getSelectedItem().toString());
            int num2 = Integer.parseInt(numberComboBox2.getSelectedItem().toString());
            if (num1 == num2) {
                JOptionPane.showMessageDialog(null, "Please select two different numbers.");
                return false;
            }
        }

        // Validate bet amount
        try {
            double bet = Double.parseDouble(betField.getText().trim());
            if (bet <= 0) {
                JOptionPane.showMessageDialog(null, "Bet amount must be greater than 0.");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid bet amount.");
            return false;
        }

        return true;
    }

    private JScrollPane createTable() {
        String[] columns = {"Criteria", "Description", "Winnings"};
        String[][] data = {
                {"1", "Place bet on single number", "x5"},
                {"2", "Place bet on two numbers", "x3"},
                {"3", "Place bet on red color", "x2"},
                {"4", "Place bet on black color", "x2"},
                {"5", "Place bet on odd number", "x2"},
                {"6", "Place bet on even number", "x2"},
                {"7", "Place bet on low number", "x1.5"},
                {"8", "Place bet on high number", "x1.5"}
        };
        JTable table = new JTable(new DefaultTableModel(data, columns));
        return new JScrollPane(table);
    }

    private Color getColorForNumber(int number) {
        for (int i = 0; i < rouletteNumbers.length; i++) {
            if (rouletteNumbers[i] == number) {
                return numberColors[i];
            }
        }
        return Color.BLACK;
    }

    private String getColorNameForNumber(int number) {
        Color color = getColorForNumber(number);
        if (color.equals(Color.GREEN) || color.equals(new Color(0, 128, 0))) {
            return "Green";
        } else if (color.equals(new Color(220, 20, 60))) {
            return "Red";
        } else {
            return "Black";
        }
    }

    private void showRouletteWheelAnimation(JFrame parentFrame) {
        JDialog wheelDialog = new JDialog(parentFrame, "Roulette Wheel - DEATHisCOIMING", true);
        wheelDialog.setSize(700, 800);
        wheelDialog.setLayout(new BorderLayout());
        wheelDialog.setLocationRelativeTo(parentFrame);

        JPanel wheelPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawRouletteWheel(g, getWidth(), getHeight());
            }
        };
        wheelPanel.setBackground(new Color(0, 50, 0));

        JLabel instructionLabel = new JLabel("Press any key to start slowing down the wheel", JLabel.CENTER);
        instructionLabel.setForeground(Color.WHITE);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 16));

        wheelDialog.add(instructionLabel, BorderLayout.NORTH);
        wheelDialog.add(wheelPanel, BorderLayout.CENTER);

        // Reset animation variables
        ballAngle = 0;
        bounceEffect = 0;
        currentNumber = 0; // Reset current number
        final double[] ballSpeed = {28};
        final double[] baseDeceleration = {0.3};
        final boolean[] isSpinning = {true};
        final boolean[] isDecelerating = {false};

        Timer pulseTimer = new Timer(50, e -> {
            if (isWinningNumberPulsing) {
                if (pulseIncreasing) {
                    pulseAlpha += 12;
                    if (pulseAlpha >= 180) {
                        pulseAlpha = 180;
                        pulseIncreasing = false;
                    }
                } else {
                    pulseAlpha -= 12;
                    if (pulseAlpha <= 0) {
                        pulseAlpha = 0;
                        pulseIncreasing = true;
                    }
                }
                wheelPanel.repaint();
            }
        });

        // Timer for ball animation
        Timer ballTimer = new Timer(40, e -> {
            if (isSpinning[0]) {
                if (isDecelerating[0]) {
                    double dynamicDeceleration = baseDeceleration[0] * (1 + ballSpeed[0] / 50);
                    ballSpeed[0] -= dynamicDeceleration;

                    double segmentSize = 360.0 / rouletteNumbers.length;
                    double segmentPosition = (ballAngle % segmentSize) / segmentSize;

                    if (ballSpeed[0] > 5 && (segmentPosition < 0.05 || segmentPosition > 0.95)) {
                        bounceEffect = (Math.random() * 4 - 2) * (ballSpeed[0] / 30);
                    } else {
                        bounceEffect *= 0.9;
                    }

                    if (ballSpeed[0] < 3) {
                        double wobble = Math.sin(ballAngle * 8) * (3 - ballSpeed[0]) / 8;
                        bounceEffect += wobble;
                    }

                    if (ballSpeed[0] <= 0.3) {
                        ballSpeed[0] = 0;
                        isSpinning[0] = false;

                        // Set finalNumber to animationFinalNumber
                        finalNumber = animationFinalNumber;
                        currentNumber = finalNumber;
                        finalColor = getColorForNumber(finalNumber);

                        isWinningNumberPulsing = true;
                        pulseTimer.start();
                        wheelPanel.repaint();

                        Timer closeTimer = new Timer(3000, ev -> {
                            wheelDialog.dispose();
                            pulseTimer.stop();
                            isWinningNumberPulsing = false;
                            processGameResultAfterAnimation(parentFrame);
                        });
                        closeTimer.setRepeats(false);
                        closeTimer.start();
                    }
                }

                ballAngle = (ballAngle + ballSpeed[0] + bounceEffect) % 360;
                if (ballAngle < 0) ballAngle += 360;

                // Calculate current number based on ball position (for display only)
                double adjustedAngle = (360 - ballAngle + 90) % 360;
                int segmentIndex = (int) ((adjustedAngle / 360.0) * rouletteNumbers.length + 1);
                segmentIndex = segmentIndex % rouletteNumbers.length;
                currentNumber = rouletteNumbers[segmentIndex];
                finalNumber = currentNumber;

                wheelPanel.repaint();
            }
        });

        wheelDialog.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (isSpinning[0] && !isDecelerating[0]) {
                    isDecelerating[0] = true;
                    instructionLabel.setText("Wheel slowing down... Ball may bounce!");
                }
            }
        });

        wheelDialog.setFocusable(true);
        wheelDialog.requestFocus();
        ballTimer.start();
        wheelDialog.setVisible(true);
    }

    private void drawRouletteWheel(Graphics g, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = width / 2;
        int centerY = height / 2;
        int outerRadius = Math.min(width, height) / 2 - 80;
        int numberRadius = outerRadius - 30;
        int innerRadius = outerRadius - 80;
        int centerCircleRadius = outerRadius - 120;

        // Draw number segments
        int segmentCount = rouletteNumbers.length;
        double segmentAngle = 360.0 / segmentCount;

        for (int i = 0; i < segmentCount; i++) {
            double startAngle = 90 - (i * segmentAngle);
            if (startAngle < 0) startAngle += 360;

            // FIXED: Check if this is the winning segment for pulsing effect
            // Use finalNumber for the pulsing, not currentNumber
            boolean isWinningSegment = (rouletteNumbers[i] == currentNumber) && isWinningNumberPulsing;
            Color segmentColor = numberColors[i];

            if (isWinningSegment) {
                int pulseValue = 200 + pulseAlpha / 2;
                segmentColor = new Color(pulseValue, pulseValue, 150 + pulseAlpha / 3);
            }

            GradientPaint segmentGradient = new GradientPaint(
                    centerX, centerY, segmentColor.brighter().brighter(),
                    centerX + (float) (numberRadius * Math.cos(Math.toRadians(startAngle + segmentAngle / 2))),
                    centerY - (float) (numberRadius * Math.sin(Math.toRadians(startAngle + segmentAngle / 2))),
                    segmentColor.darker().darker()
            );
            g2d.setPaint(segmentGradient);

            Arc2D segment = new Arc2D.Double(
                    centerX - numberRadius, centerY - numberRadius,
                    numberRadius * 2, numberRadius * 2,
                    startAngle, segmentAngle, Arc2D.PIE
            );
            g2d.fill(segment);

            g2d.setColor(new Color(255, 255, 255, 200));
            g2d.setStroke(new BasicStroke(2f));
            g2d.draw(segment);

            Color textColor = (segmentColor.equals(Color.GREEN) || segmentColor.equals(new Color(0, 128, 0))) ?
                    Color.WHITE : Color.WHITE;
            g2d.setColor(textColor);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));

            double textAngle = Math.toRadians(startAngle + segmentAngle / 2);
            int textX = centerX + (int) ((numberRadius - 20) * Math.cos(textAngle));
            int textY = centerY - (int) ((numberRadius - 20) * Math.sin(textAngle));

            String numberText = String.valueOf(rouletteNumbers[i]);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(numberText);

            g2d.drawString(numberText, textX - textWidth / 2, textY + fm.getHeight() / 4);
        }

        GradientPaint innerGradient = new GradientPaint(
                centerX - innerRadius, centerY - innerRadius, new Color(255, 215, 0),
                centerX + innerRadius, centerY + innerRadius, new Color(184, 134, 11)
        );
        g2d.setPaint(innerGradient);
        g2d.fillOval(centerX - innerRadius, centerY - innerRadius, innerRadius * 2, innerRadius * 2);

        g2d.setColor(new Color(139, 0, 0));
        g2d.setStroke(new BasicStroke(4f));
        g2d.drawOval(centerX - innerRadius, centerY - innerRadius, innerRadius * 2, innerRadius * 2);

        RadialGradientPaint centerGradient = new RadialGradientPaint(
                centerX, centerY, centerCircleRadius,
                new float[]{0.0f, 1.0f},
                new Color[]{new Color(255, 215, 0), new Color(139, 69, 19)}
        );
        g2d.setPaint(centerGradient);
        g2d.fillOval(centerX - centerCircleRadius, centerY - centerCircleRadius, centerCircleRadius * 2, centerCircleRadius * 2);

        g2d.setColor(new Color(160, 82, 45));
        g2d.setStroke(new BasicStroke(3f));
        g2d.drawOval(centerX - centerCircleRadius, centerY - centerCircleRadius, centerCircleRadius * 2, centerCircleRadius * 2);

        // Draw current number in center
        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        String displayNumber = String.valueOf(currentNumber);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(displayNumber);
        int textHeight = fm.getHeight();

        //g2d.setColor(new Color(0, 0, 0, 150));
        //g2d.fillOval(centerX - textWidth - 10, centerY - textHeight/2 - 5, textWidth * 2 + 20, textHeight * 2 + 10);

        g2d.setColor(getColorForNumber(currentNumber));
        g2d.drawString(displayNumber, centerX - textWidth / 2, centerY + textHeight / 4);

        // Draw ball
        double ballAngleRad = Math.toRadians(ballAngle);
        int ballSize = 20;
        int ballX = centerX + (int) ((numberRadius - 10) * Math.cos(ballAngleRad)) - ballSize / 2;
        int ballY = centerY - (int) ((numberRadius - 10) * Math.sin(ballAngleRad)) - ballSize / 2;

        int shadowOffset = (int) (3 + Math.abs(bounceEffect));
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillOval(ballX + shadowOffset, ballY + shadowOffset, ballSize, ballSize);

        RadialGradientPaint ballGradient = new RadialGradientPaint(
                ballX + ballSize / 2, ballY + ballSize / 2, ballSize,
                new float[]{0.0f, 0.7f, 1.0f},
                new Color[]{Color.WHITE, new Color(200, 200, 255), new Color(150, 150, 200)}
        );
        g2d.setPaint(ballGradient);
        g2d.fillOval(ballX, ballY, ballSize, ballSize);

        g2d.setColor(new Color(255, 255, 255, 220));
        g2d.fillOval(ballX + 5, ballY + 5, ballSize / 3, ballSize / 3);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawOval(ballX, ballY, ballSize, ballSize);

        // Draw casino branding
        drawEnhancedCasinoDecorations(g2d, centerX, centerY, outerRadius);
    }

    private void drawLuxuriousFrame(Graphics2D g2d, int centerX, int centerY, int radius) {
        for (int i = 0; i < 360; i += 10) {
            double angle = Math.toRadians(i);
            int x1 = centerX + (int) (radius * Math.cos(angle));
            int y1 = centerY + (int) (radius * Math.sin(angle));
            int x2 = centerX + (int) ((radius + 25) * Math.cos(angle));
            int y2 = centerY + (int) ((radius + 25) * Math.sin(angle));

//                Color jewelColor;
//                if (i % 30 == 0) {
//                    jewelColor = new Color(220, 20, 60);
//                } else if (i % 30 == 10) {
//                    jewelColor = new Color(0, 128, 0);
//                } else {
//                    jewelColor = new Color(30, 144, 255);
//                }
//
//            g2d.setColor(jewelColor);
//            g2d.setStroke(new BasicStroke(6f));
//            g2d.drawLine(x1, y1, x2, y2);

            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2f));
            int jewelX = centerX + (int) ((radius + 30) * Math.cos(angle));
            int jewelY = centerY + (int) ((radius + 30) * Math.sin(angle));
            g2d.fillOval(jewelX - 3, jewelY - 3, 6, 6);
        }

        g2d.setColor(new Color(255, 215, 0));
        g2d.setStroke(new BasicStroke(2f));
        for (int i = 5; i < 360; i += 10) {
            double angle1 = Math.toRadians(i);
            double angle2 = Math.toRadians(i + 5);

            int x1 = centerX + (int) ((radius + 15) * Math.cos(angle1));
            int y1 = centerY + (int) ((radius + 15) * Math.sin(angle1));
            int x2 = centerX + (int) ((radius + 15) * Math.cos(angle2));
            int y2 = centerY + (int) ((radius + 15) * Math.sin(angle2));

            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    private void drawEnhancedCasinoDecorations(Graphics2D g2d, int centerX, int centerY, int radius) {
        g2d.setColor(new Color(255, 215, 0));

        for (int i = 0; i < 72; i++) {
            double angle = Math.toRadians(i * 5);
            int innerX = centerX + (int) ((radius + 8) * Math.cos(angle));
            int innerY = centerY + (int) ((radius + 8) * Math.sin(angle));
            int outerX = centerX + (int) ((radius + 25) * Math.cos(angle));
            int outerY = centerY + (int) ((radius + 25) * Math.sin(angle));

            g2d.setStroke(new BasicStroke(2f));
            g2d.drawLine(innerX, innerY, outerX, outerY);

            if (i % 3 == 0) {
                g2d.fillOval(outerX - 2, outerY - 2, 4, 4);
            }
        }

        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        g2d.setColor(new Color(178, 34, 34));
        String casinoName = "DEATHisCOMING";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(casinoName);
        g2d.drawString(casinoName, centerX - textWidth / 2, centerY + radius + 60);

        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.setColor(new Color(255, 215, 0));
        String label = "EUROPEAN ROULETTE";
        textWidth = g2d.getFontMetrics().stringWidth(label);
        g2d.drawString(label, centerX - textWidth / 2, centerY + radius + 90);

        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.setStroke(new BasicStroke(1f));
        for (int i = 0; i < 360; i += 45) {
            double angle = Math.toRadians(i);
            int startX = centerX + (int) ((radius - 10) * Math.cos(angle));
            int startY = centerY + (int) ((radius - 10) * Math.sin(angle));
            int endX = centerX + (int) ((radius + 40) * Math.cos(angle));
            int endY = centerY + (int) ((radius + 40) * Math.sin(angle));
            g2d.drawLine(startX, startY, endX, endY);
        }
    }

    private void processGameResultAfterAnimation(JFrame frame) {
        // Re-enable play button
        for (Component comp : frame.getContentPane().getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals("Play")) {
                comp.setEnabled(true);
            }
        }

        wheelLabel.setText(String.valueOf(finalNumber));
        wheelLabel.setBackground(getColorForNumber(finalNumber));
        wheelLabel.setForeground(Color.WHITE);

        processGameResult();
    }

    private void processGameResult() {
        try {
            int criteria = criteriaComboBox.getSelectedIndex();
            double bet = Double.parseDouble(betField.getText().trim());
            Object selectedValue = numberComboBox.getSelectedItem();
            double winnings = 0;
            boolean won = false;

            int roulette = finalNumber; // Use the finalNumber from animation
            String color = getColorNameForNumber(roulette);
            resultColorField.setText(color);

            switch (criteria) {
                case 1:
                    // Single number
                    if (selectedValue != null && Integer.parseInt(selectedValue.toString()) == roulette) {
                        winnings = bet * 5;
                        won = true;
                    }
                    break;
                case 2:
                    // Two numbers
                    if (selectedValue != null && numberComboBox2.getSelectedItem() != null) {
                        int number1 = Integer.parseInt(selectedValue.toString());
                        int number2 = Integer.parseInt(numberComboBox2.getSelectedItem().toString());
                        if (roulette == number1 || roulette == number2) {
                            winnings = bet * 3;
                            won = true;
                        }
                    }
                    break;
                case 3:
                    // Red color
                    if ("Red".equals(selectedValue) && "Red".equals(color)) {
                        winnings = bet * 2;
                        won = true;
                    }
                    break;
                case 4:
                    // Black color
                    if ("Black".equals(selectedValue) && "Black".equals(color)) {
                        winnings = bet * 2;
                        won = true;
                    }
                    break;
                case 5:
                    // Odd number
                    if (roulette % 2 == 1 && roulette != 0) {
                        winnings = bet * 2;
                        won = true;
                    }
                    break;
                case 6:
                    // Even number
                    if (roulette % 2 == 0 && roulette != 0) {
                        winnings = bet * 2;
                        won = true;
                    }
                    break;
                case 7:
                    // Low number (1-18)
                    if (roulette >= 1 && roulette <= 18) {
                        winnings = bet * 1.5;
                        won = true;
                    }
                    break;
                case 8:
                    // High number (19-36)
                    if (roulette >= 19 && roulette <= 36) {
                        winnings = bet * 1.5;
                        won = true;
                    }
                    break;
            }

            String resultMessage;
            if (won) {
                resultMessage = "You WON! Roulette: " + roulette + " (" + color + ") | Won Rs " + winnings;
                gameHistory.add("Bet: Rs " + bet + " | WIN: Rs " + winnings + " | Criteria: " + criteria);
            } else {
                resultMessage = "You LOST. Roulette: " + roulette + " (" + color + ") | Lost Rs " + bet;
                gameHistory.add("Bet: Rs " + bet + " | LOST: Rs " + bet + " | Criteria: " + criteria);
            }


            gameResultField.setText(resultMessage);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error processing game result: " + ex.getMessage());
        }
    }


    private void showGameHistory() {
        if (gameHistory.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No game history available.");
            return;
        }

        JDialog historyDialog = new JDialog();
        historyDialog.setTitle("Game History - DEATHisCOIMING");
        historyDialog.setSize(600, 500);
        historyDialog.setLayout(new BorderLayout());
        historyDialog.getContentPane().setBackground(new Color(0, 80, 0));

        // Create table model with columns
        String[] columns = {"Game #", "Bet Amount", "Criteria", "Result", "Win/Loss", "Running Total"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // NO FRAUD
            }
        };

        // Create table with the model
        JTable historyTable = new JTable(model);
        historyTable.setRowHeight(25);
        historyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        historyTable.getTableHeader().setBackground(new Color(70, 130, 180));
        historyTable.getTableHeader().setForeground(Color.WHITE);
        historyTable.setFont(new Font("Arial", Font.PLAIN, 12));
        historyTable.setGridColor(Color.LIGHT_GRAY);

//        // Center align numeric columns
//        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
//        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
//        for (int i = 0; i < columns.length; i++) {
//            historyTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
//        }

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(13, 154, 150), 2),
                "Game History",
                0, 0,
                new Font("Arial", Font.BOLD, 14),
                new Color(73, 13, 13)
        ));

        // Calculate totals and populate table
        double runningTotal = 0;

        for (int i = 0; i < gameHistory.size(); i++) {
            String history = gameHistory.get(i);

            // Parse the history string to extract information
            String[] parts = history.split(" \\| ");
            String betAmount = "0";
            String criteria = "Unknown";
            String result = "Unknown";
            double winLoss = 0;

            for (String part : parts) {
                if (part.startsWith("Bet: Rs ")) {
                    betAmount = part.substring(8);
                } else if (part.startsWith("WIN: Rs ")) {
                    result = "WIN";
                    winLoss = Double.parseDouble(part.substring(8));

                } else if (part.startsWith("LOST: Rs ")) {
                    result = "LOSS";
                    winLoss = -Double.parseDouble(part.substring(9));
                } else if (part.startsWith("Criteria: ")) {
                    criteria = getCriteriaName(Integer.parseInt(part.substring(10)));
                }
            }

            runningTotal += winLoss;

            // Add row to table
            Object[] rowData = {
                    i + 1,
                    "Rs " + betAmount,
                    criteria,
                    result,
                    String.format("%sRs %.2f",
                            winLoss >= 0 ? "+" : "-",
                            Math.abs( winLoss)
                    ),
                    String.format("%sRs %.2f",
                            runningTotal >= 0 ? "+" : "-",
                            Math.abs( runningTotal)
                    )
                    //winLoss >= 0 ? "+" : "",
                    // runningTotal >= 0 ? "+" :
            };
            model.addRow(rowData);
        }

        // Create bottom panel for totals
        JPanel bottomPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.setBackground(new Color(0, 100, 0));

        // Calculate final totals
        double totalWins = 0;
        double totalLosses = 0;
        int winCount = 0;
        int lossCount = 0;

        for (int i = 0; i < model.getRowCount(); i++) {
            String result = (String) model.getValueAt(i, 3);
            String winLossStr = ((String) model.getValueAt(i, 4)).replace("Rs ", "").replace("+", "");
            double amount = Double.parseDouble(winLossStr);

            if (result.equals("WIN")) {
                totalWins += amount;
                winCount++;
            } else {
                totalLosses += Math.abs(amount);
                lossCount++;
            }
        }

        // Create total labels with styling
        JLabel totalLabel = new JLabel("Overall Total:", JLabel.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalLabel.setForeground(Color.WHITE);

        JLabel totalValue = new JLabel(String.format("%sRs %.2f", runningTotal >= 0 ? "+" : "", runningTotal), JLabel.CENTER);
        totalValue.setFont(new Font("Arial", Font.BOLD, 16));
        totalValue.setForeground(runningTotal >= 0 ? new Color(50, 205, 50) : new Color(255, 69, 0));

        JLabel statsLabel = new JLabel("Games: " + gameHistory.size() +
                " (Win/s: " + winCount + " / Lose/s: " + lossCount + ")", JLabel.CENTER);
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statsLabel.setForeground(Color.WHITE);

        JLabel winLossLabel = new JLabel("Won: Rs " + String.format("%.2f", totalWins) +
                " | Lost: Rs " + String.format("%.2f", totalLosses), JLabel.CENTER);
        winLossLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        winLossLabel.setForeground(Color.WHITE);

        bottomPanel.add(totalLabel);
        bottomPanel.add(totalValue);
        bottomPanel.add(statsLabel);
        bottomPanel.add(winLossLabel);

        // Add components to dialog
        historyDialog.add(scrollPane, BorderLayout.CENTER);
        historyDialog.add(bottomPanel, BorderLayout.SOUTH);

        // Add close button
        JButton closeButton = new JButton("Close History");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setBackground(new Color(178, 34, 34));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> historyDialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0, 80, 0));
        buttonPanel.add(closeButton);

        historyDialog.add(buttonPanel, BorderLayout.NORTH);
        historyDialog.setLocationRelativeTo(null);
        historyDialog.setVisible(true);
    }

    // Helper method to get criteria name from number
    private String getCriteriaName(int criteriaNumber) {
        switch (criteriaNumber) {
            case 1:
                return "Single Number";
            case 2:
                return "Two Numbers";
            case 3:
                return "Red Color";
            case 4:
                return "Black Color";
            case 5:
                return "Odd Numbers";
            case 6:
                return "Even Numbers";
            case 7:
                return "Low (1-18)";
            case 8:
                return "High (19-36)";
            default:
                return "Unknown";
        }
    }
}