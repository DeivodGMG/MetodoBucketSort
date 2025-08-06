import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BucketSortVisualizer extends JFrame {
    // Constantes de diseño
    private static final Color PRIMARY_COLOR = new Color(48, 63, 159);
    private static final Color SECONDARY_COLOR = new Color(74, 20, 140);
    private static final Color ACCENT_COLOR = new Color(255, 213, 0);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Color DISABLED_COLOR = new Color(189, 189, 189);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
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
    private ScheduledExecutorService animationExecutor;
    private Color currentThemeColor = PRIMARY_COLOR;
    private TitledBorder controlPanelBorder;
    private TitledBorder bucketsPanelBorder;
    
    // Configuración de animación
    private int animationSpeed = 100; // ms
    private boolean darkMode = false;
    
    public BucketSortVisualizer() {
        initUI();
        setupListeners();
        applyTheme();
    }
    
    private void initUI() {
        // Configuración principal de la ventana
        setTitle("Bucket Sort Visualizer - Professional Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 700));
        
        // Panel principal con diseño moderno
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Panel de encabezado
        createHeaderPanel();
        
        // Panel de control
        createControlPanel();
        
        // Panel de visualización
        createVisualizationPanel();
        
        // Barra de estado
        createStatusBar();
        
        // Agregar componentes al panel principal
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(controlPanel, BorderLayout.WEST);
        mainPanel.add(visualizationPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Centrar ventana y hacerla visible
        pack();
        setLocationRelativeTo(null);
    }
    
    private void createHeaderPanel() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        titleLabel = new JLabel("BUCKET SORT VISUALIZER");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        startButton = new JButton("INICIAR VISUALIZADOR");
        startButton.setFont(BUTTON_FONT);
        startButton.setBackground(ACCENT_COLOR);
        startButton.setForeground(Color.BLACK);
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        startButton.setFocusPainted(false);
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
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
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
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
        Color bgColor = darkMode ? new Color(66, 66, 66) : BACKGROUND_COLOR;
        Color textColor = darkMode ? Color.WHITE : TEXT_COLOR;
        Color panelBg = darkMode ? new Color(48, 48, 48) : BACKGROUND_COLOR;
        
        mainPanel.setBackground(bgColor);
        headerPanel.setBackground(bgColor);
        controlPanel.setBackground(panelBg);
        visualizationPanel.setBackground(panelBg);
        arrayPanel.setBackground(panelBg);
        bucketsContainerPanel.setBackground(panelBg);
        
        titleLabel.setForeground(textColor);
        statusLabel.setForeground(textColor);
        
        // Actualizar bordes usando las referencias guardadas
        controlPanelBorder.setTitleColor(currentThemeColor);
        controlPanelBorder.setBorder(BorderFactory.createLineBorder(currentThemeColor, 2));
        
        bucketsPanelBorder.setTitleColor(currentThemeColor);
        bucketsPanelBorder.setBorder(BorderFactory.createLineBorder(currentThemeColor, 2));
        
        // Actualizar botones
        generateButton.setBackground(currentThemeColor);
        generateButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(currentThemeColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        Color sortButtonColor = darkMode ? currentThemeColor.brighter() : SECONDARY_COLOR;
        sortButton.setBackground(sortButtonColor);
        sortButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(sortButtonColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        // Actualizar áreas de texto
        originalArrayArea.setBackground(darkMode ? new Color(48, 48, 48) : Color.WHITE);
        originalArrayArea.setForeground(textColor);
        sortedArrayArea.setBackground(darkMode ? new Color(48, 48, 48) : Color.WHITE);
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
        if (originalArray == null) return;
        
        sortButton.setEnabled(false);
        generateButton.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        statusLabel.setText("Ordenando usando Bucket Sort...");
        statusLabel.setForeground(currentThemeColor);
        
        // Crear copia para ordenar
        sortedArray = Arrays.copyOf(originalArray, originalArray.length);
        
        // Ejecutar en un hilo separado para no bloquear la UI
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
            }
        };
        
        worker.execute();
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
        for (int i = 1; i < list.size(); i++) {
            double key = list.get(i);
            int j = i - 1;
            while (j >= 0 && list.get(j) > key) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
            sleepForAnimation();
        }
    }
    
    private void selectionSort(List<Double> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(j) < list.get(minIdx)) {
                    minIdx = j;
                }
            }
            Collections.swap(list, minIdx, i);
            sleepForAnimation();
        }
    }
    
    private void bubbleSort(List<Double> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = 0; j < list.size() - i - 1; j++) {
                if (list.get(j) > list.get(j + 1)) {
                    Collections.swap(list, j, j + 1);
                    sleepForAnimation();
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
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            if (list.get(j) < pivot) {
                i++;
                Collections.swap(list, i, j);
                sleepForAnimation();
            }
        }
        
        Collections.swap(list, i + 1, high);
        sleepForAnimation();
        return i + 1;
    }
    
    private void sleepForAnimation() {
        try {
            Thread.sleep(animationSpeed);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void displayArray(JTextArea textArea, double[] array, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Arreglo %s [%d elementos]:\n", type, array.length));
        
        for (int i = 0; i < array.length; i++) {
            sb.append(String.format("%.2f", array[i]));
            if (i < array.length - 1) {
                sb.append(", ");
            }
            if ((i + 1) % 10 == 0 && i < array.length - 1) {
                sb.append("\n");
            }
        }
        
        SwingUtilities.invokeLater(() -> {
            textArea.setText(sb.toString());
            textArea.setForeground(darkMode ? Color.WHITE : Color.BLACK);
            textArea.setCaretPosition(0);
        });
    }
    
    // Clase interna para visualización de buckets
    private class BucketPanel extends JPanel {
        private final int bucketNumber;
        private JLabel titleLabel;
        private JPanel elementsPanel;
        
        public BucketPanel(int bucketNumber) {
            this.bucketNumber = bucketNumber;
            setLayout(new BorderLayout());
            setBackground(darkMode ? new Color(55, 55, 55) : new Color(240, 240, 240));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(currentThemeColor, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            
            titleLabel = new JLabel("Bucket " + bucketNumber, SwingConstants.CENTER);
            titleLabel.setFont(MAIN_FONT.deriveFont(Font.BOLD));
            titleLabel.setForeground(currentThemeColor);
            
            elementsPanel = new JPanel();
            elementsPanel.setLayout(new BoxLayout(elementsPanel, BoxLayout.Y_AXIS));
            elementsPanel.setBackground(getBackground());
            elementsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            
            add(titleLabel, BorderLayout.NORTH);
            add(new JScrollPane(elementsPanel), BorderLayout.CENTER);
        }
        
        public void updateBucket(List<Double> elements, boolean finalStep) {
            elementsPanel.removeAll();
            
            for (Double element : elements) {
                JLabel elementLabel = new JLabel(String.format("%.2f", element), SwingConstants.CENTER);
                elementLabel.setFont(MAIN_FONT);
                elementLabel.setForeground(darkMode ? Color.WHITE : Color.BLACK);
                elementLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
                elementsPanel.add(elementLabel);
            }
            
            if (finalStep) {
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SUCCESS_COLOR, 2),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
            
            revalidate();
            repaint();
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Usar look and feel por defecto
        }
        
        SwingUtilities.invokeLater(() -> {
            BucketSortVisualizer visualizer = new BucketSortVisualizer();
            visualizer.setVisible(true);
            
            // Ocultar controles inicialmente (mostrar solo el botón de inicio)
            visualizer.controlPanel.setVisible(false);
            visualizer.visualizationPanel.setVisible(false);
        });
    }
}