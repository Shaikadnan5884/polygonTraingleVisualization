package DaaPackage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Optimal Polygon Triangulation Application (Java Swing Version)
 * Manual Weight Version: Pixel distances are ignored; user must enter all weights.
 * Updated with interactive colors and integer-style default values.
 */
public class PolygonTriangulationApp extends JFrame {

    private List<Point> points = new ArrayList<>();
    private List<int[]> optimalDiagonals = new ArrayList<>();
    private Map<String, Double> manualWeights = new HashMap<>();
    
    private DrawPanel drawPanel;
    private JPanel weightsPanel;
    private JLabel costDisplay;
    private JTextArea cutsDisplay;
    private JScrollPane weightsScrollPane;

    // Interactive Color Palette
    private final Color COLOR_BG = new Color(241, 245, 249);
    private final Color COLOR_PRIMARY = new Color(79, 70, 229); // Indigo
    private final Color COLOR_DANGER = new Color(225, 29, 72);   // Rose
    private final Color COLOR_SUCCESS = new Color(5, 150, 105); // Emerald
    private final Color COLOR_CARD_COST = new Color(238, 242, 255);
    private final Color COLOR_CARD_CUTS = new Color(240, 253, 244);

    public PolygonTriangulationApp() {
        setTitle("Optimal Polygon Triangulation - Manual Weights");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Layout
        JPanel mainContainer = new JPanel(new BorderLayout(15, 15));
        mainContainer.setBorder(new EmptyBorder(25, 25, 25, 25));
        mainContainer.setBackground(COLOR_BG);

        // --- LEFT SIDE: Canvas & Controls ---
        JPanel leftPanel = new JPanel(new BorderLayout(15, 15));
        leftPanel.setOpaque(false);

        drawPanel = new DrawPanel();
        drawPanel.setPreferredSize(new Dimension(600, 450));
        leftPanel.add(drawPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        btnPanel.setOpaque(false);
        JButton solveBtn = createStyledButton("Calculate Optimal Cuts", COLOR_PRIMARY, Color.WHITE);
        JButton clearBtn = createStyledButton("Reset Workspace", COLOR_DANGER, Color.WHITE);
        
        solveBtn.addActionListener(e -> solve());
        clearBtn.addActionListener(e -> clearAll());
        
        btnPanel.add(solveBtn);
        btnPanel.add(clearBtn);
        leftPanel.add(btnPanel, BorderLayout.SOUTH);

        // --- RIGHT SIDE: Weights Panel ---
        JPanel rightPanel = new JPanel(new BorderLayout(0, 0));
        rightPanel.setPreferredSize(new Dimension(320, 0));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createLineBorder(new Color(199, 210, 254), 1));

        JLabel weightHeader = new JLabel("<html><div style='text-align: center; padding: 15px;'><b>Manual Weight Entry</b><br><font size='3' color='#6366f1'>Enter values for all sides</font></div></html>", SwingConstants.CENTER);
        weightHeader.setOpaque(true);
        weightHeader.setBackground(new Color(245, 243, 255));
        rightPanel.add(weightHeader, BorderLayout.NORTH);

        weightsPanel = new JPanel();
        weightsPanel.setLayout(new BoxLayout(weightsPanel, BoxLayout.Y_AXIS));
        weightsPanel.setBackground(Color.WHITE);
        weightsScrollPane = new JScrollPane(weightsPanel);
        weightsScrollPane.setBorder(null);
        rightPanel.add(weightsScrollPane, BorderLayout.CENTER);

        // --- BOTTOM: Results Panel ---
        JPanel resultsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        resultsPanel.setOpaque(false);
        resultsPanel.setPreferredSize(new Dimension(0, 150));

        JPanel costCard = createResultCard("TOTAL MINIMUM COST", COLOR_CARD_COST, new Color(199, 210, 254));
        costDisplay = new JLabel("0", SwingConstants.CENTER);
        costDisplay.setFont(new Font("Monospaced", Font.BOLD, 32));
        costDisplay.setForeground(new Color(49, 46, 129));
        costCard.add(costDisplay, BorderLayout.CENTER);

        JPanel cutsCard = createResultCard("OPTIMAL TRIANGULATION CUTS", COLOR_CARD_CUTS, new Color(187, 247, 208));
        cutsDisplay = new JTextArea("Add vertices and enter weights to begin...");
        cutsDisplay.setEditable(false);
        cutsDisplay.setLineWrap(true);
        cutsDisplay.setFont(new Font("Monospaced", Font.PLAIN, 14));
        cutsDisplay.setBackground(COLOR_CARD_CUTS);
        JScrollPane cutsScroll = new JScrollPane(cutsDisplay);
        cutsScroll.setBorder(null);
        cutsCard.add(cutsScroll, BorderLayout.CENTER);

        resultsPanel.add(costCard);
        resultsPanel.add(cutsCard);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(640);
        split.setBorder(null);
        split.setOpaque(false);
        
        mainContainer.add(split, BorderLayout.CENTER);
        mainContainer.add(resultsPanel, BorderLayout.SOUTH);
        
        add(mainContainer);
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("SansSerif", Font.BOLD, 15));
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 1),
            BorderFactory.createEmptyBorder(12, 10, 12, 10)
        ));
        return b;
    }

    private JPanel createResultCard(String title, Color bg, Color border) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bg);
        p.setBorder(BorderFactory.createLineBorder(border, 2));
        JLabel l = new JLabel(title);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(bg.darker().darker());
        l.setBorder(new EmptyBorder(10, 15, 5, 15));
        p.add(l, BorderLayout.NORTH);
        return p;
    }

    private String getPairKey(int i, int j) {
        return i < j ? i + "-" + j : j + "-" + i;
    }

    private double getWeight(int i, int j) {
        String key = getPairKey(i, j);
        return manualWeights.getOrDefault(key, 0.0);
    }

    private String formatValue(double val) {
        if (val == (long) val) return String.format("%d", (long) val);
        else return String.format("%.1f", val);
    }

    private void updateWeightsUI() {
        weightsPanel.removeAll();
        int n = points.size();
        if (n < 2) {
            JLabel emptyMsg = new JLabel("Click canvas to add vertices...");
            emptyMsg.setForeground(Color.GRAY);
            emptyMsg.setBorder(new EmptyBorder(20, 10, 0, 10));
            weightsPanel.add(emptyMsg);
        } else {
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    final String key = getPairKey(i, j);
                    boolean isEdge = (j == i + 1) || (i == 0 && j == n - 1);
                    
                    JPanel row = new JPanel(new BorderLayout(15, 0));
                    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
                    row.setBackground(Color.WHITE);
                    row.setBorder(new EmptyBorder(8, 15, 8, 15));
                    
                    String labelText = (isEdge ? "Side " : "Diagonal ") + "(" + i + ", " + j + ")";
                    JLabel lbl = new JLabel(labelText);
                    lbl.setFont(new Font("SansSerif", isEdge ? Font.BOLD : Font.PLAIN, 13));
                    lbl.setForeground(isEdge ? new Color(30, 41, 59) : new Color(100, 116, 139));
                    
                    if (!manualWeights.containsKey(key)) manualWeights.put(key, 0.0);

                    JTextField field = new JTextField(formatValue(manualWeights.get(key)));
                    field.setPreferredSize(new Dimension(80, 28));
                    field.setHorizontalAlignment(JTextField.RIGHT);
                    field.setFont(new Font("Monospaced", Font.PLAIN, 13));
                    field.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
                    
                    field.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            try {
                                manualWeights.put(key, Double.parseDouble(field.getText()));
                                field.setText(formatValue(manualWeights.get(key)));
                            } catch (NumberFormatException ex) {
                                field.setText("0");
                                manualWeights.put(key, 0.0);
                            }
                        }
                    });

                    row.add(lbl, BorderLayout.WEST);
                    row.add(field, BorderLayout.EAST);
                    weightsPanel.add(row);
                }
            }
        }
        weightsPanel.revalidate();
        weightsPanel.repaint();
    }

    private void solve() {
        int n = points.size();
        if (n < 3) {
            JOptionPane.showMessageDialog(this, "Please define a polygon with at least 3 vertices.");
            return;
        }

        double[][] table = new double[n][n];
        int[][] split = new int[n][n];

        for (int L = 3; L <= n; L++) {
            for (int i = 0; i <= n - L; i++) {
                int j = i + L - 1;
                table[i][j] = Double.MAX_VALUE;
                for (int k = i + 1; k < j; k++) {
                    double triangleWeight = getWeight(i, j) + getWeight(j, k) + getWeight(k, i);
                    double currentCost = table[i][k] + table[k][j] + triangleWeight;
                    
                    if (currentCost < table[i][j]) {
                        table[i][j] = currentCost;
                        split[i][j] = k;
                    }
                }
            }
        }

        costDisplay.setText(formatValue(table[0][n - 1]));
        optimalDiagonals.clear();
        StringBuilder sb = new StringBuilder();
        reconstruct(0, n - 1, split, sb);
        cutsDisplay.setText(sb.length() == 0 ? "Polygon is a triangle (no cuts needed)." : sb.toString());
        drawPanel.repaint();
    }

    private void reconstruct(int i, int j, int[][] split, StringBuilder sb) {
        if (j <= i + 1) return;
        int k = split[i][j];
        checkAndAdd(i, k, sb);
        checkAndAdd(k, j, sb);
        if (i != 0 || j != points.size() - 1) checkAndAdd(i, j, sb);
        reconstruct(i, k, split, sb);
        reconstruct(k, j, split, sb);
    }

    private void checkAndAdd(int i, int j, StringBuilder sb) {
        int n = points.size();
        boolean isEdge = Math.abs(i - j) == 1 || (i == 0 && j == n - 1) || (j == 0 && i == n - 1);
        if (!isEdge) {
            for (int[] d : optimalDiagonals) if ((d[0] == i && d[1] == j) || (d[0] == j && d[1] == i)) return;
            optimalDiagonals.add(new int[]{i, j});
            sb.append(String.format("• Cut %d-%d (W: %s)\n", i, j, formatValue(getWeight(i, j))));
        }
    }

    private void clearAll() {
        points.clear();
        optimalDiagonals.clear();
        manualWeights.clear();
        costDisplay.setText("0");
        cutsDisplay.setText("Add vertices and enter weights to begin...");
        updateWeightsUI();
        drawPanel.repaint();
    }

    private class DrawPanel extends JPanel {
        public DrawPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 2));
            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    points.add(e.getPoint());
                    updateWeightsUI();
                    optimalDiagonals.clear();
                    repaint();
                }
            });
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw Optimal Diagonals
            g2.setColor(COLOR_SUCCESS);
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[]{8}, 0));
            for (int[] d : optimalDiagonals) {
                g2.drawLine(points.get(d[0]).x, points.get(d[0]).y, points.get(d[1]).x, points.get(d[1]).y);
            }

            // Draw Polygon Edges
            if (!points.isEmpty()) {
                g2.setStroke(new BasicStroke(3));
                g2.setColor(COLOR_PRIMARY);
                for (int i = 0; i < points.size(); i++) {
                    Point p1 = points.get(i);
                    Point p2 = points.get((i + 1) % points.size());
                    if (points.size() > 1) g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }

            // Draw Vertices
            for (int i = 0; i < points.size(); i++) {
                Point p = points.get(i);
                g2.setColor(COLOR_PRIMARY);
                g2.fillOval(p.x - 7, p.y - 7, 14, 14);
                g2.setColor(new Color(30, 41, 59));
                g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                g2.drawString(String.valueOf(i), p.x + 12, p.y - 12);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new PolygonTriangulationApp().setVisible(true));
    }
}