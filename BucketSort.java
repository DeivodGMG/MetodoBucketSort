import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class BucketSort extends JFrame {
    // Constantes de diseño mejoradas
    private static final Color PRIMARY_COLOR = new Color(0, 150, 255); // Azul vibrante
    private static final Color SECONDARY_COLOR = new Color(230, 0, 126); // Rosa eléctrico
    private static final Color ACCENT_COLOR = new Color(255, 200, 0); // Amarillo brillante
    private static final Color SUCCESS_COLOR = new Color(0, 200, 83); // Verde éxito
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color DISABLED_COLOR = new Color(180, 180, 180);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    // Componentes de la UI
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel controlPanel;
    private JPanel visualizationPanel;
    private JPanel arrayPanel;
    private JPanel bucketsContainerPanel;
    private JTextArea originalArrayArea;
    private JTextArea sortedArrayArea;
    private JButton startButton;
    private JButton generateButton;
    private JButton sortButton;
    private JSpinner arraySizeSpinner;
    private JComboBox<String> sortMethodComboBox;
    private JComboBox<String> themeComboBox;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JLabel titleLabel;

    // Datos y estado
    private double[] originalArray;
    private double[] sortedArray;
    private List<BucketPanel> bucketPanels;
    private Color currentThemeColor = PRIMARY_COLOR;
    private TitledBorder controlPanelBorder;
    private TitledBorder bucketsPanelBorder;

    // Configuración de animación
    private int animationSpeed = 100;
    private boolean darkMode = false;
    private boolean isRunning = false;

    public BucketSort() {
        initUI();
        setupListeners();
        applyTheme();
    }

    private void initUI() {
        setTitle("✨ Bucket Sort Visualizer 3000 PURA UPV ✨");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 750));

        // Panel principal con diseño moderno y responsivo
        mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (!isRunning) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Efecto de gradiente sutil
                    GradientPaint gp = new GradientPaint(0, 0, new Color(255, 255, 255, 50),
                            getWidth(), getHeight(), new Color(200, 230, 255, 30));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);

        createHeaderPanel();
        createControlPanel();
        createVisualizationPanel();
        createStatusBar();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(controlPanel, BorderLayout.WEST);
        mainPanel.add(visualizationPanel, BorderLayout.CENTER);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    private void createHeaderPanel() {
        headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo con gradiente
                GradientPaint gp = new GradientPaint(0, 0, currentThemeColor,
                        getWidth(), 0, currentThemeColor.darker());
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(getWidth(), 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        headerPanel.setOpaque(false);

        titleLabel = new JLabel("BUCKET SORT VISUALIZER");
        titleLabel.setFont(TITLE_FONT.deriveFont(Font.BOLD).deriveFont(24f));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        startButton = new JButton("INICIAR VISUALIZADOR") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Botón con esquinas redondeadas
                int arc = 25;
                g2.setColor(ACCENT_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                // Sin borde, ya que lo dibujamos nosotros
            }
        };
        startButton.setFont(BUTTON_FONT.deriveFont(Font.BOLD).deriveFont(16f));
        startButton.setForeground(Color.BLACK);
        startButton.setContentAreaFilled(false);
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        startButton.setFocusPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(startButton, BorderLayout.EAST);
    }

    private void createControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        // Crear el borde con una referencia que podamos modificar después
        controlPanelBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(currentThemeColor, 2),
                "CONTROLES",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                TITLE_FONT,
                currentThemeColor
        );

        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                controlPanelBorder,
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        controlPanel.setBackground(BACKGROUND_COLOR);
        controlPanel.setPreferredSize(new Dimension(250, getHeight()));

        // Configuración de tamaño del arreglo
        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sizePanel.setBackground(BACKGROUND_COLOR);
        JLabel sizeLabel = new JLabel("Tamaño del arreglo:");
        sizeLabel.setFont(MAIN_FONT);

        arraySizeSpinner = new JSpinner(new SpinnerNumberModel(15, 5, 100, 1));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(arraySizeSpinner, "#");
        arraySizeSpinner.setEditor(editor);
        arraySizeSpinner.setFont(MAIN_FONT);
        arraySizeSpinner.setPreferredSize(new Dimension(80, 30));

        sizePanel.add(sizeLabel);
        sizePanel.add(Box.createHorizontalStrut(10));
        sizePanel.add(arraySizeSpinner);

        // Botón de generación
        generateButton = createStyledButton("Generar Arreglo", PRIMARY_COLOR);
        generateButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Configuración de método de ordenamiento
        JPanel methodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        methodPanel.setBackground(BACKGROUND_COLOR);
        JLabel methodLabel = new JLabel("Método interno:");
        methodLabel.setFont(MAIN_FONT);

        sortMethodComboBox = new JComboBox<>(new String[]{"Insertion Sort", "Selection Sort", "Bubble Sort", "Quick Sort"});
        sortMethodComboBox.setFont(MAIN_FONT);
        sortMethodComboBox.setPreferredSize(new Dimension(150, 30));

        methodPanel.add(methodLabel);
        methodPanel.add(Box.createHorizontalStrut(10));
        methodPanel.add(sortMethodComboBox);

        // Configuración de tema
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        themePanel.setBackground(BACKGROUND_COLOR);
        JLabel themeLabel = new JLabel("Tema:");
        themeLabel.setFont(MAIN_FONT);

        themeComboBox = new JComboBox<>(new String[]{"Azul", "Morado", "Verde", "Rojo", "Oscuro"});
        themeComboBox.setFont(MAIN_FONT);
        themeComboBox.setPreferredSize(new Dimension(150, 30));

        themePanel.add(themeLabel);
        themePanel.add(Box.createHorizontalStrut(10));
        themePanel.add(themeComboBox);

        // Botón de ordenamiento
        sortButton = createStyledButton("Iniciar Ordenamiento", SECONDARY_COLOR);
        sortButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        sortButton.setEnabled(false);

        // Configuración de velocidad
        JPanel speedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        speedPanel.setBackground(BACKGROUND_COLOR);
        JLabel speedLabel = new JLabel("Velocidad:");
        speedLabel.setFont(MAIN_FONT);

        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 10, 500, 100);
        speedSlider.setInverted(true);
        speedSlider.setMajorTickSpacing(100);
        speedSlider.setMinorTickSpacing(50);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.setFont(MAIN_FONT);
        speedSlider.setPreferredSize(new Dimension(180, 50));

        speedSlider.addChangeListener(e -> {
            animationSpeed = speedSlider.getValue();
        });

        speedPanel.add(speedLabel);
        speedPanel.add(Box.createHorizontalStrut(10));
        speedPanel.add(speedSlider);

        // Agregar componentes al panel de control
        controlPanel.add(sizePanel);
        controlPanel.add(Box.createVerticalStrut(15));
        controlPanel.add(generateButton);
        controlPanel.add(Box.createVerticalStrut(20));
        controlPanel.add(methodPanel);
        controlPanel.add(Box.createVerticalStrut(15));
        controlPanel.add(themePanel);
        controlPanel.add(Box.createVerticalStrut(15));
        controlPanel.add(speedPanel);
        controlPanel.add(Box.createVerticalStrut(20));
        controlPanel.add(sortButton);
        controlPanel.add(Box.createVerticalGlue());
    }

    private void createVisualizationPanel() {
        visualizationPanel = new JPanel(new BorderLayout(10, 10));
        visualizationPanel.setBackground(BACKGROUND_COLOR);

        // Panel de arreglos
        arrayPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        arrayPanel.setBackground(BACKGROUND_COLOR);

        originalArrayArea = createTextArea("Arreglo original aparecerá aquí...");
        sortedArrayArea = createTextArea("Arreglo ordenado aparecerá aquí...");

        arrayPanel.add(createArraySubPanel("ARREGLO ORIGINAL", originalArrayArea));
        arrayPanel.add(createArraySubPanel("ARREGLO ORDENADO", sortedArrayArea));

        // Panel de buckets
        bucketsContainerPanel = new JPanel(new BorderLayout());
        bucketsContainerPanel.setBackground(BACKGROUND_COLOR);

        // Crear el borde con una referencia que podamos modificar después
        bucketsPanelBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(currentThemeColor, 2),
                "VISUALIZACIÓN DE BUCKETS",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                TITLE_FONT,
                currentThemeColor
        );

        bucketsContainerPanel.setBorder(BorderFactory.createCompoundBorder(
                bucketsPanelBorder,
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel placeholder = new JLabel("Los buckets se mostrarán aquí durante el ordenamiento", SwingConstants.CENTER);
        placeholder.setFont(MAIN_FONT);
        placeholder.setForeground(DISABLED_COLOR);
        bucketsContainerPanel.add(placeholder, BorderLayout.CENTER);

        // Agregar componentes al panel de visualización
        visualizationPanel.add(arrayPanel, BorderLayout.NORTH);
        visualizationPanel.add(bucketsContainerPanel, BorderLayout.CENTER);
    }

    private void createStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        statusPanel.setBackground(BACKGROUND_COLOR);

        statusLabel = new JLabel("Presiona 'Generar Arreglo' para comenzar");
        statusLabel.setFont(MAIN_FONT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(200, 20));
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        progressBar.setBorderPainted(false);
        progressBar.setUI(new GradientProgressBarUI());

        statusPanel.add(statusLabel, BorderLayout.CENTER);
        statusPanel.add(progressBar, BorderLayout.EAST);

        mainPanel.add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createArraySubPanel(String title, JTextArea textArea) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(MAIN_FONT.deriveFont(Font.BOLD));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JTextArea createTextArea(String placeholder) {
        JTextArea textArea = new JTextArea(placeholder);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(Color.WHITE);
        textArea.setForeground(DISABLED_COLOR);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return textArea;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Botón con esquinas redondeadas y sombra
                    int arc = 20;
                    g2d.setColor(bgColor);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

                    g2d.setColor(bgColor.darker());
                    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

                    g2d.dispose();
                }
                super.paintComponent(g);
            }
        };

        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void setupListeners() {
        startButton.addActionListener(e -> {
            startButton.setVisible(false);
            controlPanel.setVisible(true);
            visualizationPanel.setVisible(true);
            generateButton.setEnabled(true);
            statusLabel.setText("Presiona 'Generar Arreglo' para comenzar");
        });

        generateButton.addActionListener(e -> {
            generateRandomArray();
        });

        sortButton.addActionListener(e -> {
            performBucketSort();
        });

        themeComboBox.addActionListener(e -> {
            String selectedTheme = (String) themeComboBox.getSelectedItem();
            switch (selectedTheme) {
                case "Azul":
                    currentThemeColor = new Color(48, 63, 159);
                    darkMode = false;
                    break;
                case "Morado":
                    currentThemeColor = new Color(74, 20, 140);
                    darkMode = false;
                    break;
                case "Verde":
                    currentThemeColor = new Color(0, 105, 92);
                    darkMode = false;
                    break;
                case "Rojo":
                    currentThemeColor = new Color(198, 40, 40);
                    darkMode = false;
                    break;
                case "Oscuro":
                    currentThemeColor = new Color(33, 33, 33);
                    darkMode = true;
                    break;
            }
            applyTheme();
        });
    }

    private void applyTheme() {
        Color bgColor = darkMode ? new Color(60, 63, 65) : BACKGROUND_COLOR;
        Color textColor = darkMode ? Color.WHITE : TEXT_COLOR;
        Color panelBg = darkMode ? new Color(43, 43, 43) : new Color(240, 240, 240);

        mainPanel.setBackground(bgColor);
        headerPanel.setBackground(bgColor);
        controlPanel.setBackground(panelBg);
        visualizationPanel.setBackground(panelBg);
        arrayPanel.setBackground(panelBg);
        bucketsContainerPanel.setBackground(panelBg);

        titleLabel.setForeground(darkMode ? Color.WHITE : TEXT_COLOR);
        statusLabel.setForeground(darkMode ? Color.WHITE : TEXT_COLOR);

        // Actualización de bordes
        controlPanelBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(currentThemeColor, 2),
                "CONTROLES",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                TITLE_FONT,
                currentThemeColor
        );

        bucketsPanelBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(currentThemeColor, 2),
                "VISUALIZACIÓN DE BUCKETS",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                TITLE_FONT,
                currentThemeColor
        );

        // Crear bordes internos
        Border innerControlBorder = BorderFactory.createEmptyBorder(15, 15, 15, 15);
        Border innerBucketsBorder = BorderFactory.createEmptyBorder(15, 15, 15, 15);

        controlPanel.setBorder(BorderFactory.createCompoundBorder(controlPanelBorder, innerControlBorder));
        bucketsContainerPanel.setBorder(BorderFactory.createCompoundBorder(bucketsPanelBorder, innerBucketsBorder));

        // Actualizar botones con efectos modernos
        generateButton.setBackground(currentThemeColor);
        generateButton.setForeground(Color.WHITE);

        Color sortButtonColor = darkMode ? currentThemeColor.brighter() : SECONDARY_COLOR;
        sortButton.setBackground(sortButtonColor);
        sortButton.setForeground(Color.WHITE);

        // Actualizar áreas de texto
        originalArrayArea.setBackground(darkMode ? new Color(50, 50, 50) : Color.WHITE);
        originalArrayArea.setForeground(textColor);
        sortedArrayArea.setBackground(darkMode ? new Color(50, 50, 50) : Color.WHITE);
        sortedArrayArea.setForeground(textColor);

        revalidate();
        repaint();
    }

    private void generateRandomArray() {
        int size = (Integer) arraySizeSpinner.getValue();
        originalArray = new double[size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            originalArray[i] = Math.round(random.nextDouble() * 100) / 100.0;
        }

        displayArray(originalArrayArea, originalArray, "Original");
        sortedArrayArea.setText("Arreglo ordenado aparecerá aquí...");
        sortedArrayArea.setForeground(DISABLED_COLOR);

        sortButton.setEnabled(true);
        statusLabel.setText("Arreglo generado. ¡Listo para ordenar!");
        statusLabel.setForeground(currentThemeColor);

        // Limpiar visualización anterior
        bucketsContainerPanel.removeAll();
        JLabel placeholder = new JLabel("Los buckets se mostrarán aquí durante el ordenamiento", SwingConstants.CENTER);
        placeholder.setFont(MAIN_FONT);
        placeholder.setForeground(DISABLED_COLOR);
        bucketsContainerPanel.add(placeholder, BorderLayout.CENTER);
        bucketsContainerPanel.revalidate();
        bucketsContainerPanel.repaint();
    }

    private void performBucketSort() {
        if (originalArray == null || isRunning) return;

        isRunning = true;
        sortButton.setEnabled(false);
        generateButton.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        statusLabel.setText("Ordenando usando Bucket Sort...");
        statusLabel.setForeground(currentThemeColor);

        // Animación de inicio
        animateStart();

        // Crear copia para ordenar
        sortedArray = Arrays.copyOf(originalArray, originalArray.length);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                bucketSort(sortedArray);
                return null;
            }

            @Override
            protected void done() {
                displayArray(sortedArrayArea, sortedArray, "Ordenado");
                progressBar.setIndeterminate(false);
                progressBar.setValue(100);
                statusLabel.setText("¡Ordenamiento completado exitosamente!");
                statusLabel.setForeground(SUCCESS_COLOR);
                generateButton.setEnabled(true);
                isRunning = false;

                // Animación de finalización
                animateCompletion();
            }
        };

        worker.execute();
    }

    private void animateStart() {
        // Animación de pulsación en el botón de ordenar
        javax.swing.Timer timer = new javax.swing.Timer(50, new ActionListener() {
            float scale = 1.0f;
            boolean growing = false;
            int count = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (count++ > 10) {
                    ((javax.swing.Timer)e.getSource()).stop();
                    sortButton.setSize(sortButton.getPreferredSize());
                    return;
                }

                if (growing) {
                    scale += 0.05f;
                    if (scale > 1.1f) growing = false;
                } else {
                    scale -= 0.05f;
                    if (scale < 1.0f) growing = true;
                }

                sortButton.setSize((int)(sortButton.getPreferredSize().width * scale),
                        (int)(sortButton.getPreferredSize().height * scale));
                sortButton.revalidate();
            }
        });
        timer.start();
    }

    private void animateCompletion() {
        // Crear panel para animación de confeti
        JPanel confettiPanel = new JPanel() {
            private ArrayList<Confetti> confettis = new ArrayList<>();

            {
                // Crear 50 partículas de confeti
                for (int i = 0; i < 50; i++) {
                    confettis.add(new Confetti(getWidth(), getHeight()));
                }
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                for (Confetti c : confettis) {
                    c.update();
                    g2d.setColor(c.color);
                    g2d.fill(c.shape);
                }
            }
        };

        confettiPanel.setBounds(0, 0, getWidth(), getHeight());
        add(confettiPanel);

        // Animación de confeti
        javax.swing.Timer confettiTimer = new javax.swing.Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confettiPanel.repaint();
            }
        });
        confettiTimer.start();

        // Eliminar después de 3 segundos
        javax.swing.Timer removeTimer = new javax.swing.Timer(3000, e -> {
            confettiTimer.stop();
            remove(confettiPanel);
            repaint();
        });
        removeTimer.setRepeats(false);
        removeTimer.start();
    }

    private class Confetti {
        Shape shape;
        Color color;
        float x, y;
        float speed;
        float angle;
        float rotation;
        float size;

        public Confetti(int width, int height) {
            Random rand = new Random();
            this.x = rand.nextInt(width);
            this.y = -rand.nextInt(100);
            this.speed = 2 + rand.nextFloat() * 3;
            this.angle = rand.nextFloat() * (float)Math.PI * 2;
            this.rotation = rand.nextFloat() * 0.1f - 0.05f;
            this.size = 5 + rand.nextFloat() * 10;

            // Forma aleatoria (cuadrado o círculo)
            if (rand.nextBoolean()) {
                this.shape = new Rectangle2D.Float(-size/2, -size/2, size, size);
            } else {
                this.shape = new Ellipse2D.Float(-size/2, -size/2, size, size);
            }

            // Color aleatorio vibrante
            this.color = new Color(
                    rand.nextFloat() * 0.7f + 0.3f,
                    rand.nextFloat() * 0.7f + 0.3f,
                    rand.nextFloat() * 0.7f + 0.3f,
                    0.9f
            );
        }

        public void update() {
            x += Math.cos(angle) * 0.5;
            y += speed;
            angle += rotation;

            if (y > getHeight()) {
                y = -10;
                x = new Random().nextInt(getWidth());
            }

            // Actualizar posición de la forma
            AffineTransform transform = new AffineTransform();
            transform.translate(x, y);
            transform.rotate(angle);
            shape = transform.createTransformedShape(shape);
        }
    }

    private void bucketSort(double[] array) {
        int n = array.length;
        if (n <= 0) return;

        // Crear buckets
        @SuppressWarnings("unchecked")
        List<Double>[] buckets = new List[n];
        for (int i = 0; i < n; i++) {
            buckets[i] = new ArrayList<>();
        }

        // Distribuir elementos en buckets
        for (double value : array) {
            int bucketIndex = (int) (value * n);
            if (bucketIndex >= n) bucketIndex = n - 1;
            buckets[bucketIndex].add(value);
        }

        // Actualizar UI con los buckets
        updateBucketVisualization(buckets, false);

        // Ordenar cada bucket individualmente
        String selectedMethod = (String) sortMethodComboBox.getSelectedItem();
        for (int i = 0; i < buckets.length; i++) {
            if (!buckets[i].isEmpty()) {
                sortBucket(buckets[i], selectedMethod);
                // Actualizar UI después de ordenar cada bucket
                updateBucketVisualization(buckets, i == buckets.length - 1);
                sleepForAnimation();
            }
        }

        // Concatenar buckets ordenados
        int index = 0;
        for (List<Double> bucket : buckets) {
            for (double value : bucket) {
                array[index++] = value;
                // Actualizar progreso
                final int progress = (int) ((index * 100) / array.length);
                SwingUtilities.invokeLater(() -> {
                    progressBar.setValue(progress);
                });
                sleepForAnimation();
            }
        }
    }

    private void updateBucketVisualization(List<Double>[] buckets, boolean finalStep) {
        SwingUtilities.invokeLater(() -> {
            bucketsContainerPanel.removeAll();

            if (bucketPanels == null || bucketPanels.size() != buckets.length) {
                bucketPanels = new ArrayList<>();
                for (int i = 0; i < buckets.length; i++) {
                    bucketPanels.add(new BucketPanel(i));
                }
            }

            JPanel bucketsGrid = new JPanel(new GridLayout(0, Math.min(5, buckets.length), 10, 10));
            bucketsGrid.setBackground(bucketsContainerPanel.getBackground());

            for (int i = 0; i < buckets.length; i++) {
                BucketPanel panel = bucketPanels.get(i);
                panel.updateBucket(buckets[i], finalStep);
                bucketsGrid.add(panel);
            }

            JScrollPane scrollPane = new JScrollPane(bucketsGrid);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            bucketsContainerPanel.add(scrollPane, BorderLayout.CENTER);

            bucketsContainerPanel.revalidate();
            bucketsContainerPanel.repaint();
        });
    }

    private void sortBucket(List<Double> bucket, String method) {
        if (method == null) method = "Insertion Sort";

        switch (method) {
            case "Insertion Sort":
                insertionSort(bucket);
                break;
            case "Selection Sort":
                selectionSort(bucket);
                break;
            case "Bubble Sort":
                bubbleSort(bucket);
                break;
            case "Quick Sort":
                quickSort(bucket, 0, bucket.size() - 1);
                break;
            default:
                insertionSort(bucket);
                break;
        }
    }

    private void insertionSort(List<Double> list) {
        int n = list.size();
        for (int i = 1; i < n; ++i) {
            double key = list.get(i);
            int j = i - 1;

            while (j >= 0 && list.get(j) > key) {
                list.set(j + 1, list.get(j));
                j = j - 1;
            }
            list.set(j + 1, key);
        }
    }

    private void selectionSort(List<Double> list) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            int min_idx = i;
            for (int j = i + 1; j < n; j++) {
                if (list.get(j) < list.get(min_idx)) {
                    min_idx = j;
                }
            }
            double temp = list.get(min_idx);
            list.set(min_idx, list.get(i));
            list.set(i, temp);
        }
    }

    private void bubbleSort(List<Double> list) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (list.get(j) > list.get(j + 1)) {
                    double temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
    }

    private void quickSort(List<Double> list, int low, int high) {
        if (low < high) {
            int pi = partition(list, low, high);
            quickSort(list, low, pi - 1);
            quickSort(list, pi + 1, high);
        }
    }

    private int partition(List<Double> list, int low, int high) {
        double pivot = list.get(high);
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (list.get(j) < pivot) {
                i++;
                double temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
            }
        }
        double temp = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, temp);

        return i + 1;
    }


    private void displayArray(JTextArea textArea, double[] array, String status) {
        StringBuilder sb = new StringBuilder(status + ": [");
        if (array.length > 0) {
            sb.append(String.format("%.2f", array[0]));
            for (int i = 1; i < array.length; i++) {
                sb.append(", ").append(String.format("%.2f", array[i]));
            }
        }
        sb.append("]");
        textArea.setText(sb.toString());
        textArea.setForeground(TEXT_COLOR);
    }
    
    private void sleepForAnimation() {
        try {
            Thread.sleep(animationSpeed);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private class BucketPanel extends JPanel {
        private int bucketIndex;
        private List<Double> values = new ArrayList<>();
        private boolean isFinal = false;

        public BucketPanel(int index) {
            this.bucketIndex = index;
            setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(currentThemeColor.darker(), 1),
                "Bucket " + index,
                TitledBorder.CENTER,
                TitledBorder.TOP,
                MAIN_FONT,
                currentThemeColor.darker()
            ));
            setBackground(new Color(255, 255, 255, 150));
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        }

        public void updateBucket(List<Double> newValues, boolean finalStep) {
            this.values = newValues;
            this.isFinal = finalStep;
            SwingUtilities.invokeLater(() -> {
                removeAll();
                for (Double value : values) {
                    add(new BarComponent(value, isFinal));
                }
                revalidate();
                repaint();
            });
        }
    }

    private class BarComponent extends JComponent {
        private double value;
        private boolean isFinal;
        private static final int BAR_HEIGHT = 15;
        private static final int BAR_WIDTH = 50;

        public BarComponent(double value, boolean isFinal) {
            this.value = value;
            this.isFinal = isFinal;
            setPreferredSize(new Dimension(BAR_WIDTH, BAR_HEIGHT));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Mapear el valor a un color
            float hue = (float) value;
            Color barColor = isFinal ? SUCCESS_COLOR : Color.getHSBColor(hue, 0.9f, 0.9f);

            // Dibujar la barra
            g2d.setColor(barColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2d.setColor(barColor.darker());
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

            // Dibujar el texto
            g2d.setColor(Color.WHITE);
            g2d.setFont(MAIN_FONT.deriveFont(Font.BOLD, 10f));
            String text = String.format("%.2f", value);
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(text, x, y);

            g2d.dispose();
        }
    }

    private static class GradientProgressBarUI extends BasicProgressBarUI {
        @Override
        protected void paintDeterminate(Graphics g, JComponent c) {
            Graphics2D g2d = (Graphics2D) g;
            int width = progressBar.getWidth();
            int height = progressBar.getHeight();
            int fillWidth = (int)(progressBar.getPercentComplete() * width);
            
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            GradientPaint gp = new GradientPaint(0, 0, new Color(0, 150, 255), 
                                                width, 0, new Color(0, 200, 83));
            g2d.setPaint(gp);
            g2d.fillRoundRect(0, 0, fillWidth, height, 10, 10);
        }

        @Override
        protected void paintIndeterminate(Graphics g, JComponent c) {
            Graphics2D g2d = (Graphics2D) g;
            int width = progressBar.getWidth();
            int height = progressBar.getHeight();
            
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Animación de la barra
            int pos = (int)(getAnimationIndex() % 100 * width / 100.0);
            int fillWidth = width / 4;
            
            GradientPaint gp = new GradientPaint(0, 0, new Color(0, 150, 255, 50), 
                                                width, 0, new Color(0, 200, 83, 50));
            g2d.setPaint(gp);
            g2d.fillRoundRect(0, 0, width, height, 10, 10);
            
            gp = new GradientPaint(pos, 0, new Color(0, 150, 255), 
                                  pos + fillWidth, 0, new Color(0, 200, 83));
            g2d.setPaint(gp);
            g2d.fillRoundRect(pos, 0, fillWidth, height, 10, 10);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BucketSort visualizer = new BucketSort();
            visualizer.setVisible(true);
        });
    }
}