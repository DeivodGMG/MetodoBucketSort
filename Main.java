import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuMetodos menu = new MenuMetodos();
            menu.setVisible(true);
        });
    }
}

class MenuMetodos extends JFrame {
    // Configuración de la interfaz
    private static final Map<String, ImageIcon> imageCache = new HashMap<>();
    private float rotationAngle = 0;
    private JDesktopPane desktopPane = new JDesktopPane();
    private JPanel mainPanel;

    public MenuMetodos() {
        setTitle("Métodos de Ordenamiento Unificados");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
        startSpinnerAnimation();
    }

    private void initUI() {
        // Panel principal con fondo animado
        mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(30, 30, 70);
                Color color2 = new Color(10, 10, 40);
                g2d.setPaint(new GradientPaint(0, 0, color1, 0, getHeight(), color2));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.CENTER;

        // Título
        JLabel title = new JLabel("MÉTODOS DE ORDENAMIENTO");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(new Color(200, 200, 255));
        mainPanel.add(title, gbc);

        // Tarjetas de métodos
        String[] metodos = {
            "Bucket Sort", "Counting Sort", "Counting Sort (Cartas)", 
            "Heap Sort", "Merge Sort", "Inserción", 
            "Quick Sort", "Selection Sort"
        };
        
        for (String metodo : metodos) {
            mainPanel.add(createMethodCard(metodo), gbc);
        }

        // Configuración del layout usando CardLayout para alternar entre menú y desktop
        setLayout(new CardLayout());
        add(mainPanel, "menu");
        add(desktopPane, "desktop");
    }

    // Método para crear tarjetas
    private JPanel createMethodCard(String nombre) {
        JPanel card = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                AffineTransform oldTransform = g2d.getTransform();
                g2d.rotate(rotationAngle, 30, 30);
                
                // Spinner simulado
                g2d.setColor(new Color(150, 150, 255, 150));
                g2d.fill(new Ellipse2D.Double(10, 10, 40, 40));
                g2d.setColor(new Color(200, 200, 255));
                g2d.fill(new Arc2D.Double(10, 10, 40, 40, 0, 120, Arc2D.PIE));
                
                g2d.setTransform(oldTransform);
            }
        };

        card.setPreferredSize(new Dimension(500, 70));
        card.setBackground(new Color(70, 70, 120, 180));
        card.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 255), 2));

        JLabel nameLabel = new JLabel(nombre);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);

        JButton viewButton = new JButton("Ver Método");
        viewButton.setBackground(new Color(80, 180, 250));
        viewButton.setForeground(Color.WHITE);
        viewButton.setFocusPainted(false);

        card.add(nameLabel, BorderLayout.CENTER);
        card.add(viewButton, BorderLayout.EAST);

        // Acción del botón
        viewButton.addActionListener(e -> {
            desktopPane.removeAll();
            CardLayout cl = (CardLayout) getContentPane().getLayout();
            cl.show(getContentPane(), "desktop");
            
            // Botón para regresar al menú
            JButton backButton = new JButton("← Regresar al Menú");
            backButton.setBounds(10, 10, 150, 30);
            backButton.addActionListener(evt -> {
                cl.show(getContentPane(), "menu");
            });
            desktopPane.add(backButton, JLayeredPane.PALETTE_LAYER);
            
            switch(nombre) {
                case "Bucket Sort":
                    openFrame(new BucketSort());
                    break;
                case "Counting Sort":
                    openFrame(new CountingSort());
                    break;
                case "Counting Sort (Cartas)":
                    openFrame(new CountingSortCartas());
                    break;
                case "Heap Sort":
                    openFrame(new HeapSortAnimation());
                    break;
                case "Merge Sort":
                    openFrame(new MergeSortVisual());
                    break;
                case "Inserción":
                    openFrame(new InsercionSort());
                    break;
                case "Quick Sort":
                    openFrame(new QuickSortVisual());
                    break;
                case "Selection Sort":
                    openFrame(new SelectionSortGUI());
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Método no implementado: " + nombre);
                    break;
            }
            
            desktopPane.revalidate();
            desktopPane.repaint();
        });

        // Efectos hover
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(90, 90, 150, 220));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(new Color(70, 70, 120, 180));
            }
        });

        return card;
    }

    private void openFrame(JInternalFrame frame) {
        frame.setSize(700, 500);
        frame.setLocation(50, 50);
        desktopPane.add(frame);
        frame.setVisible(true);
        frame.toFront();
    }

    private void startSpinnerAnimation() {
        Timer timer = new Timer(50, e -> {
            rotationAngle += 0.1;
            if (rotationAngle > Math.PI * 2) rotationAngle = 0;
            repaint();
        });
        timer.start();
    }

    // =================================================================
    // MÉTODOS DE ORDENAMIENTO INCORPORADOS
    // =================================================================

    // 1. Bucket Sort
    public class BucketSort extends JInternalFrame {
        private JTextArea outputArea = new JTextArea();
        
        public BucketSort() {
            super("Bucket Sort", true, true, true, true);
            setLayout(new BorderLayout());
            
            JPanel controlPanel = new JPanel();
            JButton sortButton = new JButton("Ordenar Ejemplo");
            sortButton.addActionListener(e -> executeSort());
            
            controlPanel.add(sortButton);
            add(controlPanel, BorderLayout.NORTH);
            add(new JScrollPane(outputArea), BorderLayout.CENTER);
            
            outputArea.setText("Ejemplo de Bucket Sort:\n[23, 45, 12, 78, 34, 56]\n\nPresiona 'Ordenar Ejemplo' para ver el proceso");
            outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        }
        
        private void executeSort() {
            int[] arr = {23, 45, 12, 78, 34, 56};
            outputArea.append("\n\n=== PROCESO DE BUCKET SORT ===");
            
            // Mostrar array original
            outputArea.append("\nArray original: " + Arrays.toString(arr));
            
            // Simulación del proceso de buckets
            outputArea.append("\n\nDistribución en buckets:");
            for (int i = 0; i < arr.length; i++) {
                int bucketIndex = arr[i] / 10;
                outputArea.append("\nElemento " + arr[i] + " → Bucket " + bucketIndex);
            }
            
            // Ordenar y mostrar resultado
            Arrays.sort(arr);
            outputArea.append("\n\nResultado final ordenado: " + Arrays.toString(arr));
            outputArea.append("\n\nComplejidad: O(n + k) donde n es el número de elementos y k el número de buckets");
        }
    }

    // 2. Counting Sort
    public class CountingSort extends JInternalFrame {
        private JTextArea outputArea = new JTextArea();
        
        public CountingSort() {
            super("Counting Sort", true, true, true, true);
            setLayout(new BorderLayout());
            
            JPanel controlPanel = new JPanel();
            JButton sortButton = new JButton("Ejecutar Counting Sort");
            sortButton.addActionListener(e -> executeCountingSort());
            
            controlPanel.add(sortButton);
            add(controlPanel, BorderLayout.NORTH);
            add(new JScrollPane(outputArea), BorderLayout.CENTER);
            
            outputArea.setText("Counting Sort - Ordenamiento por conteo\n[4, 2, 2, 8, 3, 3, 1]\n\nPresiona el botón para ver el proceso");
            outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        }
        
        private void executeCountingSort() {
            int[] arr = {4, 2, 2, 8, 3, 3, 1};
            outputArea.append("\n\n=== COUNTING SORT ===");
            outputArea.append("\nArray original: " + Arrays.toString(arr));
            
            // Encontrar el valor máximo
            int max = Arrays.stream(arr).max().getAsInt();
            outputArea.append("\nValor máximo encontrado: " + max);
            
            // Crear array de conteo
            int[] count = new int[max + 1];
            outputArea.append("\nArray de conteo inicializado de tamaño: " + (max + 1));
            
            // Contar frecuencias
            for (int num : arr) {
                count[num]++;
            }
            outputArea.append("\nFrecuencias contadas: " + Arrays.toString(count));
            
            // Reconstruir array ordenado
            int[] result = new int[arr.length];
            int index = 0;
            for (int i = 0; i < count.length; i++) {
                for (int j = 0; j < count[i]; j++) {
                    result[index++] = i;
                }
            }
            
            outputArea.append("\nResultado ordenado: " + Arrays.toString(result));
            outputArea.append("\n\nComplejidad: O(n + k) donde k es el rango de valores");
        }
    }

    // 3. Counting Sort para Cartas
    public class CountingSortCartas extends JInternalFrame {
        private JPanel cardPanel = new JPanel(new GridLayout(2, 5, 10, 10));
        private String[] cartas = {"K♥", "3♠", "A♦", "5♣", "2♥", "J♠", "7♦", "Q♣", "4♥", "9♠"};
        
        public CountingSortCartas() {
            super("Counting Sort (Cartas)", true, true, true, true);
            setLayout(new BorderLayout());
            
            cardPanel.setBorder(BorderFactory.createTitledBorder("Cartas para ordenar"));
            updateCardDisplay();
            
            add(cardPanel, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel();
            JButton sortButton = new JButton("Ordenar Cartas");
            JButton shuffleButton = new JButton("Mezclar");
            
            sortButton.addActionListener(e -> sortCards());
            shuffleButton.addActionListener(e -> shuffleCards());
            
            buttonPanel.add(shuffleButton);
            buttonPanel.add(sortButton);
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
        private void updateCardDisplay() {
            cardPanel.removeAll();
            for (String carta : cartas) {
                JLabel cardLabel = new JLabel(carta, SwingConstants.CENTER);
                cardLabel.setFont(new Font("Arial", Font.BOLD, 18));
                cardLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                cardLabel.setOpaque(true);
                cardLabel.setBackground(Color.WHITE);
                cardLabel.setPreferredSize(new Dimension(60, 80));
                
                // Color según el palo
                if (carta.contains("♥") || carta.contains("♦")) {
                    cardLabel.setForeground(Color.RED);
                } else {
                    cardLabel.setForeground(Color.BLACK);
                }
                
                cardPanel.add(cardLabel);
            }
            cardPanel.revalidate();
            cardPanel.repaint();
        }
        
        private void sortCards() {
            // Ordenar por valor (A=1, J=11, Q=12, K=13)
            Arrays.sort(cartas, (a, b) -> {
                int valA = getCardValue(a.substring(0, a.length()-1));
                int valB = getCardValue(b.substring(0, b.length()-1));
                return Integer.compare(valA, valB);
            });
            updateCardDisplay();
        }
        
        private void shuffleCards() {
            java.util.Collections.shuffle(Arrays.asList(cartas));
            updateCardDisplay();
        }
        
        private int getCardValue(String card) {
            return switch (card) {
                case "A" -> 1;
                case "J" -> 11;
                case "Q" -> 12;
                case "K" -> 13;
                default -> Integer.parseInt(card);
            };
        }
    }

    // 4. Heap Sort
    public class HeapSortAnimation extends JInternalFrame {
        private JTextArea outputArea = new JTextArea();
        private int[] arr = {64, 34, 25, 12, 22, 11, 90};
        
        public HeapSortAnimation() {
            super("Heap Sort Animation", true, true, true, true);
            setLayout(new BorderLayout());
            
            JPanel controlPanel = new JPanel();
            JButton sortButton = new JButton("Ejecutar Heap Sort");
            sortButton.addActionListener(e -> executeHeapSort());
            
            controlPanel.add(sortButton);
            add(controlPanel, BorderLayout.NORTH);
            add(new JScrollPane(outputArea), BorderLayout.CENTER);
            
            outputArea.setText("Heap Sort - Ordenamiento por montículo\n" + 
                             Arrays.toString(arr) + "\n\nPresiona el botón para ver la animación");
            outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        }
        
        private void executeHeapSort() {
            int[] workingArray = arr.clone();
            outputArea.append("\n\n=== HEAP SORT ===");
            outputArea.append("\nArray inicial: " + Arrays.toString(workingArray));
            
            // Construir max heap
            outputArea.append("\n\n--- Construyendo Max Heap ---");
            for (int i = workingArray.length / 2 - 1; i >= 0; i--) {
                heapify(workingArray, workingArray.length, i, true);
            }
            
            // Extraer elementos uno por uno del heap
            outputArea.append("\n\n--- Extrayendo elementos del heap ---");
            for (int i = workingArray.length - 1; i >= 0; i--) {
                // Mover la raíz actual al final
                int temp = workingArray[0];
                workingArray[0] = workingArray[i];
                workingArray[i] = temp;
                
                outputArea.append("\nPaso " + (arr.length - i) + ": " + Arrays.toString(workingArray));
                
                // Llamar heapify en el heap reducido
                heapify(workingArray, i, 0, false);
            }
            
            outputArea.append("\n\nResultado final: " + Arrays.toString(workingArray));
            outputArea.append("\nComplejidad: O(n log n)");
        }
        
        private void heapify(int[] arr, int n, int i, boolean showSteps) {
            int largest = i;
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            
            if (left < n && arr[left] > arr[largest])
                largest = left;
                
            if (right < n && arr[right] > arr[largest])
                largest = right;
                
            if (largest != i) {
                int swap = arr[i];
                arr[i] = arr[largest];
                arr[largest] = swap;
                
                if (showSteps) {
                    outputArea.append("\nHeapify en índice " + i + ": " + Arrays.toString(arr));
                }
                
                heapify(arr, n, largest, showSteps);
            }
        }
    }

    // 5. Merge Sort
    public class MergeSortVisual extends JInternalFrame {
        private JTextArea outputArea = new JTextArea();
        
        public MergeSortVisual() {
            super("Merge Sort Visual", true, true, true, true);
            setLayout(new BorderLayout());
            
            JPanel controlPanel = new JPanel();
            JButton sortButton = new JButton("Ejecutar Merge Sort");
            sortButton.addActionListener(e -> executeMergeSort());
            
            controlPanel.add(sortButton);
            add(controlPanel, BorderLayout.NORTH);
            add(new JScrollPane(outputArea), BorderLayout.CENTER);
            
            outputArea.setText("Merge Sort - Algoritmo divide y vencerás\n[38, 27, 43, 3, 9, 82, 10]\n\nPresiona el botón para ver el proceso");
            outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        }
        
        private void executeMergeSort() {
            int[] arr = {38, 27, 43, 3, 9, 82, 10};
            outputArea.append("\n\n=== MERGE SORT ===");
            outputArea.append("\nArray original: " + Arrays.toString(arr));
            outputArea.append("\n\n--- Proceso de división y fusión ---");
            
            mergeSort(arr, 0, arr.length - 1, 0);
            
            outputArea.append("\n\nResultado final: " + Arrays.toString(arr));
            outputArea.append("\nComplejidad: O(n log n)");
        }
        
        private void mergeSort(int[] arr, int left, int right, int depth) {
            if (left < right) {
                int mid = left + (right - left) / 2;
                
                String indent = "  ".repeat(depth);
                outputArea.append("\n" + indent + "Dividiendo: índices " + left + "-" + right + 
                                " (medio: " + mid + ")");
                
                mergeSort(arr, left, mid, depth + 1);
                mergeSort(arr, mid + 1, right, depth + 1);
                
                merge(arr, left, mid, right, depth);
            }
        }
        
        private void merge(int[] arr, int left, int mid, int right, int depth) {
            int n1 = mid - left + 1;
            int n2 = right - mid;
            
            int[] leftArr = new int[n1];
            int[] rightArr = new int[n2];
            
            System.arraycopy(arr, left, leftArr, 0, n1);
            System.arraycopy(arr, mid + 1, rightArr, 0, n2);
            
            int i = 0, j = 0, k = left;
            
            while (i < n1 && j < n2) {
                if (leftArr[i] <= rightArr[j]) {
                    arr[k] = leftArr[i];
                    i++;
                } else {
                    arr[k] = rightArr[j];
                    j++;
                }
                k++;
            }
            
            while (i < n1) {
                arr[k] = leftArr[i];
                i++;
                k++;
            }
            
            while (j < n2) {
                arr[k] = rightArr[j];
                j++;
                k++;
            }
            
            String indent = "  ".repeat(depth);
            outputArea.append("\n" + indent + "Fusionando: " + 
                            Arrays.toString(Arrays.copyOfRange(arr, left, right + 1)));
        }
    }

    // 6. Insertion Sort
    public class InsercionSort extends JInternalFrame {
        private JTextArea outputArea = new JTextArea();
        
        public InsercionSort() {
            super("Insertion Sort", true, true, true, true);
            setLayout(new BorderLayout());
            
            JPanel controlPanel = new JPanel();
            JButton sortButton = new JButton("Ejecutar Insertion Sort");
            sortButton.addActionListener(e -> executeInsertionSort());
            
            controlPanel.add(sortButton);
            add(controlPanel, BorderLayout.NORTH);
            add(new JScrollPane(outputArea), BorderLayout.CENTER);
            
            outputArea.setText("Insertion Sort - Ordenamiento por inserción\n[12, 11, 13, 5, 6]\n\nPresiona el botón para ver cada paso");
            outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        }
        
        private void executeInsertionSort() {
            int[] arr = {12, 11, 13, 5, 6};
            outputArea.append("\n\n=== INSERTION SORT ===");
            outputArea.append("\nArray original: " + Arrays.toString(arr));
            outputArea.append("\n\n--- Proceso paso a paso ---");
            
            for (int i = 1; i < arr.length; i++) {
                int key = arr[i];
                int j = i - 1;
                
                outputArea.append("\n\nPaso " + i + ":");
                outputArea.append("\n  Elemento a insertar: " + key);
                outputArea.append("\n  Array actual: " + Arrays.toString(arr));
                
                while (j >= 0 && arr[j] > key) {
                    arr[j + 1] = arr[j];
                    j--;
                    outputArea.append("\n  Moviendo " + arr[j + 2] + " hacia la derecha");
                }
                
                arr[j + 1] = key;
                outputArea.append("\n  Insertando " + key + " en posición " + (j + 1));
                outputArea.append("\n  Resultado: " + Arrays.toString(arr));
            }
            
            outputArea.append("\n\nArray final ordenado: " + Arrays.toString(arr));
            outputArea.append("\nComplejidad: O(n²) en el peor caso, O(n) en el mejor caso");
        }
    }

    // 7. Quick Sort
    public class QuickSortVisual extends JInternalFrame {
        private JTextArea outputArea = new JTextArea();
        
        public QuickSortVisual() {
            super("Quick Sort Visual", true, true, true, true);
            setLayout(new BorderLayout());
            
            JPanel controlPanel = new JPanel();
            JButton sortButton = new JButton("Ejecutar Quick Sort");
            sortButton.addActionListener(e -> executeQuickSort());
            
            controlPanel.add(sortButton);
            add(controlPanel, BorderLayout.NORTH);
            add(new JScrollPane(outputArea), BorderLayout.CENTER);
            
            outputArea.setText("Quick Sort - Algoritmo divide y vencerás con pivot\n[10, 7, 8, 9, 1, 5]\n\nPresiona el botón para ver el proceso");
            outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        }
        
        private void executeQuickSort() {
            int[] arr = {10, 7, 8, 9, 1, 5};
            outputArea.append("\n\n=== QUICK SORT ===");
            outputArea.append("\nArray original: " + Arrays.toString(arr));
            outputArea.append("\n\n--- Proceso de particionado ---");
            
            quickSort(arr, 0, arr.length - 1, 0);
            
            outputArea.append("\n\nResultado final: " + Arrays.toString(arr));
            outputArea.append("\nComplejidad promedio: O(n log n), peor caso: O(n²)");
        }
        
        private void quickSort(int[] arr, int low, int high, int depth) {
            if (low < high) {
                String indent = "  ".repeat(depth);
                outputArea.append("\n" + indent + "Ordenando rango [" + low + ", " + high + "]");
                
                int pivotIndex = partition(arr, low, high, depth);
                
                outputArea.append("\n" + indent + "Pivot en posición " + pivotIndex + 
                                ": " + Arrays.toString(Arrays.copyOfRange(arr, low, high + 1)));
                
                quickSort(arr, low, pivotIndex - 1, depth + 1);
                quickSort(arr, pivotIndex + 1, high, depth + 1);
            }
        }
        
        private int partition(int[] arr, int low, int high, int depth) {
            int pivot = arr[high];
            String indent = "  ".repeat(depth);
            outputArea.append("\n" + indent + "Pivot seleccionado: " + pivot);
            
            int i = low - 1;
            
            for (int j = low; j < high; j++) {
                if (arr[j] < pivot) {
                    i++;
                    int temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                    
                    if (i != j) {
                        outputArea.append("\n" + indent + "Intercambiando " + arr[j] + " y " + arr[i]);
                    }
                }
            }
            
            int temp = arr[i + 1];
            arr[i + 1] = arr[high];
            arr[high] = temp;
            
            return i + 1;
        }
    }

    // 8. Selection Sort
    public class SelectionSortGUI extends JInternalFrame {
        private JTextArea outputArea = new JTextArea();
        
        public SelectionSortGUI() {
            super("Selection Sort", true, true, true, true);
            setLayout(new BorderLayout());
            
            JPanel controlPanel = new JPanel();
            JButton sortButton = new JButton("Ejecutar Selection Sort");
            sortButton.addActionListener(e -> executeSelectionSort());
            
            controlPanel.add(sortButton);
            add(controlPanel, BorderLayout.NORTH);
            add(new JScrollPane(outputArea), BorderLayout.CENTER);
            
            outputArea.setText("Selection Sort - Ordenamiento por selección\n[64, 25, 12, 22, 11]\n\nPresiona el botón para ver cada selección");
            outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        }
        
        private void executeSelectionSort() {
            int[] arr = {64, 25, 12, 22, 11};
            outputArea.append("\n\n=== SELECTION SORT ===");
            outputArea.append("\nArray original: " + Arrays.toString(arr));
            outputArea.append("\n\n--- Proceso de selección ---");
            
            for (int i = 0; i < arr.length - 1; i++) {
                int minIndex = i;
                
                outputArea.append("\n\nPaso " + (i + 1) + ":");
                outputArea.append("\n  Buscando el mínimo desde posición " + i);
                outputArea.append("\n  Array actual: " + Arrays.toString(arr));
                
                for (int j = i + 1; j < arr.length; j++) {
                    if (arr[j] < arr[minIndex]) {
                        minIndex = j;
                    }
                }
                
                outputArea.append("\n  Mínimo encontrado: " + arr[minIndex] + " en posición " + minIndex);
                
                if (minIndex != i) {
                    int temp = arr[minIndex];
                    arr[minIndex] = arr[i];
                    arr[i] = temp;
                    
                    outputArea.append("\n  Intercambiando " + arr[minIndex] + " y " + arr[i]);
                } else {
                    outputArea.append("\n  No hay intercambio necesario");
                }
                
                outputArea.append("\n  Resultado: " + Arrays.toString(arr));
            }
            
            outputArea.append("\n\nArray final ordenado: " + Arrays.toString(arr));
            outputArea.append("\nComplejidad: O(n²) en todos los casos");
        }
    }

    // Clase CajadeComida para QuickSort
    public class CajadeComida {
        public int peso;
        public int x, y;
        public String tipoComida;
        public boolean esSeleccionado = false;
        public boolean esPivot = false;

        public CajadeComida(int peso) {
            this.peso = peso;
            asignarTipoComida();
        }
        
        private void asignarTipoComida() {
            if (peso <= 20) {
                tipoComida = "Snack";
            } else if (peso <= 40) {
                tipoComida = "Fruta";
            } else if (peso <= 60) {
                tipoComida = "Sandwich";
            } else if (peso <= 80) {
                tipoComida = "Pizza";
            } else {
                tipoComida = "Hamburguesa";
            }
        }
        
        public String getTipoComida() {
            return tipoComida;
        }
        
        public char getIcono() {
            switch (tipoComida) {
                case "Snack": return 'S';
                case "Fruta": return 'F';
                case "Sandwich": return 'W';
                case "Pizza": return 'P';
                case "Hamburguesa": return 'H';
                default: return '?';
            }
        }
    }
}