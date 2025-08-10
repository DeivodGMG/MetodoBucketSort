package selectionsortgui;import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class SelectionSortGUI extends JFrame {

    private final DrawingPanel drawingPanel;
    private final JButton startButton, pauseButton, resumeButton, resetButton;
    private final JLabel statusLabel;
    private volatile boolean isPaused = false;
    private volatile boolean isSorting = false;
    private Thread sortingThread;
    private int[] initialArray;

    public SelectionSortGUI(int[] array) {
        this.initialArray = Arrays.copyOf(array, array.length);

        setTitle("Visualizador de Ordenamiento con Vasos Personalizados");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        statusLabel = new JLabel("Presiona 'Iniciar' para comenzar el ordenamiento.", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statusLabel.setForeground(Color.WHITE);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(25, 25, 112));
        topPanel.add(statusLabel, BorderLayout.CENTER);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(topPanel, BorderLayout.NORTH);

        drawingPanel = new DrawingPanel(array);
        add(drawingPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controles de Animación"));

        startButton = new JButton("Iniciar");
        pauseButton = new JButton("Pausar");
        resumeButton = new JButton("Reanudar");
        resetButton = new JButton("Reiniciar y Mezclar");
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(false);

        startButton.addActionListener(e -> startSorting());
        pauseButton.addActionListener(e -> pauseSorting());
        resumeButton.addActionListener(e -> resumeSorting());
        resetButton.addActionListener(e -> resetSorting());

        JSlider speedSlider = new JSlider(200, 2000, 1000);
        speedSlider.setMajorTickSpacing(400);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener(e -> drawingPanel.setAnimationDuration(speedSlider.getValue()));

        controlPanel.add(new JLabel("Velocidad:"));
        controlPanel.add(speedSlider);
        controlPanel.add(startButton);
        controlPanel.add(pauseButton);
        controlPanel.add(resumeButton);
        controlPanel.add(resetButton);
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    // --- Lógica de control de la animación y del algoritmo (sin cambios) ---
    private void startSorting() { if (!isSorting) { isSorting = true; isPaused = false; setButtonStates(false, true, false, false); sortingThread = new Thread(this::runSelectionSort); sortingThread.start(); } }
    private void pauseSorting() { isPaused = true; setButtonStates(false, false, true, false); statusLabel.setText("Animación pausada."); }
    private void resumeSorting() { isPaused = false; synchronized (sortingThread) { sortingThread.notify(); } setButtonStates(false, true, false, false); }
    private void resetSorting() { if (sortingThread != null && sortingThread.isAlive()) { sortingThread.interrupt(); } drawingPanel.resetItems(initialArray); isSorting = false; isPaused = false; setButtonStates(true, false, false, true); statusLabel.setText("Arreglo reiniciado. Listo para ordenar."); }
    private void setButtonStates(boolean start, boolean pause, boolean resume, boolean reset) { startButton.setEnabled(start); pauseButton.setEnabled(pause); resumeButton.setEnabled(resume); resetButton.setEnabled(reset); }
    private void sleep(int millis) { try { Thread.sleep(millis); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } }
    private void handlePause() { if (isPaused) { synchronized (sortingThread) { try { sortingThread.wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } } } }

    private void runSelectionSort() {
        DrawingPanel.SortableItem[] items = drawingPanel.getItems();
        int n = items.length;
        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            drawingPanel.setTargetSlotIndex(i);
            for (int j = i + 1; j < n; j++) {
                handlePause();
                if (Thread.currentThread().isInterrupted()) return;
                drawingPanel.setComparingIndex(j);
                drawingPanel.setMinFoundIndex(minIndex);
                statusLabel.setText(String.format("Buscando el menor... Comparando valor (%d) con (%d)", items[j].value, items[minIndex].value));
                drawingPanel.repaint();
                sleep(drawingPanel.getAnimationDuration() / 4);
                if (items[j].value < items[minIndex].value) { minIndex = j; }
            }
            drawingPanel.setComparingIndex(-1);
            if (minIndex != i) {
                statusLabel.setText(String.format("¡Menor encontrado! Intercambiando vaso de la posición %d con %d.", i, minIndex));
                drawingPanel.animateSwap(i, minIndex);
                DrawingPanel.SortableItem temp = items[i];
                items[i] = items[minIndex];
                items[minIndex] = temp;
            } else {
                statusLabel.setText(String.format("El vaso en la posición %d ya es el correcto.", i));
                sleep(drawingPanel.getAnimationDuration() / 2);
            }
            drawingPanel.setSortedUntilIndex(i);
            drawingPanel.setTargetSlotIndex(-1);
            drawingPanel.setMinFoundIndex(-1);
            drawingPanel.repaint();
        }
        statusLabel.setText("¡Ordenamiento completado!");
        drawingPanel.setSortedUntilIndex(n);
        drawingPanel.repaint();
        setButtonStates(false, false, false, true);
        isSorting = false;
    }

    // --- Clase Interna para el Panel de Dibujo ---
    private static class DrawingPanel extends JPanel {
        static class SortableItem { int value; int x, y; SortableItem(int v, int x, int y) { this.value = v; this.x = x; this.y = y; } }

        private SortableItem[] items;
        private int animationDuration = 1000;
        private volatile int sortedUntilIndex = -1, targetSlotIndex = -1, minFoundIndex = -1, comparingIndex = -1;
        private volatile boolean showHands = false;
        private Point hand1Pos = new Point(), hand2Pos = new Point();

        // Variable para almacenar la imagen que irá dentro del vaso
        private BufferedImage vaseContentImage;

        public DrawingPanel(int[] array) {
            this.vaseContentImage = loadImage("personahomosexual.jpeg");
            initializeItems(array);
        }
        
        // El método de carga ahora devuelve la imagen o null si no la encuentra
        private BufferedImage loadImage(String fileName) {
            try {
                URL url = getClass().getResource(fileName);
                if (url == null) {
                    throw new IOException("No se encontró el archivo '" + fileName + "' en el classpath.");
                }
                System.out.println("Info: Imagen '" + fileName + "' cargada correctamente.");
                return ImageIO.read(url);
            } catch (IOException e) {
                System.err.println("Advertencia: " + e.getMessage());
                return null;
            }
        }

        private void initializeItems(int[] array) {
            this.items = new SortableItem[array.length];
            int itemWidth = 80, itemHeight = 110, itemSpacing = 50;
            int totalWidth = array.length * (itemWidth + itemSpacing) - itemSpacing;
            int startX = (1200 - totalWidth) / 2, startY = 230;
            for (int i = 0; i < array.length; i++) { items[i] = new SortableItem(array[i], startX + i * (itemWidth + itemSpacing), startY); }
        }

        public void resetItems(int[] initialArray) {
            List<Integer> list = Arrays.stream(initialArray).boxed().collect(Collectors.toList());
            Collections.shuffle(list);
            initializeItems(list.stream().mapToInt(i -> i).toArray());
            sortedUntilIndex = -1; targetSlotIndex = -1; minFoundIndex = -1; comparingIndex = -1;
            repaint();
        }

        public void animateSwap(int index1, int index2) {
            CountDownLatch latch = new CountDownLatch(1);
            SortableItem item1 = items[index1], item2 = items[index2];
            Point start1 = new Point(item1.x, item1.y), end1 = new Point(item2.x, item2.y);
            Point start2 = new Point(item2.x, item2.y), end2 = new Point(item1.x, item1.y);
            int steps = animationDuration / 20;
            showHands = true;
            Timer timer = new Timer(20, null);
            final int[] currentStep = {0};
            timer.addActionListener(e -> {
                currentStep[0]++; float progress = (float) currentStep[0] / steps;
                item1.x = (int) (start1.x + progress * (end1.x - start1.x)); item1.y = (int) (start1.y + progress * (end1.y - start1.y));
                item2.x = (int) (start2.x + progress * (end2.x - start2.x)); item2.y = (int) (start2.y + progress * (end2.y - start2.y));
                double lift = Math.sin(progress * Math.PI) * 80;
                item1.y -= lift; item2.y -= lift;
                hand1Pos.setLocation(item1.x - 20, item1.y + 30); hand2Pos.setLocation(item2.x + 40, item2.y + 30);
                repaint();
                if (currentStep[0] >= steps) { timer.stop(); item1.x = end1.x; item1.y = end1.y; item2.x = end2.x; item2.y = end2.y; showHands = false; latch.countDown(); }
            });
            timer.start();
            try { latch.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(0, 0, new Color(25, 25, 112), 0, getHeight(), Color.BLACK);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            for (int i = 0; i < items.length; i++) {
                SortableItem item = items[i];
                drawVase(g2d, item.x, item.y, item.value);

                g2d.setStroke(new BasicStroke(4));
                if (i <= sortedUntilIndex) g2d.setColor(new Color(60, 179, 113, 220));
                else if (i == targetSlotIndex) g2d.setColor(new Color(0, 123, 255, 220));
                else if (i == minFoundIndex) g2d.setColor(new Color(220, 53, 69, 220));
                else if (i == comparingIndex) g2d.setColor(new Color(255, 193, 7, 220));
                else continue;
                g2d.drawRoundRect(item.x - 5, item.y - 5, 90, 120, 20, 20);
            }
            
            if(showHands){ drawHand(g2d, hand1Pos.x, hand1Pos.y, true); drawHand(g2d, hand2Pos.x, hand2Pos.y, false); }
        }
        
        /**
         * Dibuja un vaso de cristal con la imagen cargada en su interior.
         */
        private void drawVase(Graphics2D g2d, int x, int y, int value) {
            int topWidth = 80, bottomWidth = 50, height = 110;
            int x_offset = (topWidth - bottomWidth) / 2;

            // 1. Define la forma del contenido del vaso
            GeneralPath contentShape = new GeneralPath();
            contentShape.moveTo(x + x_offset, y + height); // Izquierda abajo
            contentShape.lineTo(x, y + 20); // Izquierda arriba
            contentShape.lineTo(x + topWidth, y + 20); // Derecha arriba
            contentShape.lineTo(x + topWidth - x_offset, y + height); // Derecha abajo
            contentShape.closePath();

            // 2. Dibuja el contenido: la imagen o un color por defecto
            if (vaseContentImage != null) {
                // Si la imagen existe, la usamos como textura
                Shape originalClip = g2d.getClip(); // Guarda el estado del recorte
                g2d.clip(contentShape); // Aplica el recorte
                g2d.drawImage(vaseContentImage, x, y, topWidth, height, null); // Dibuja la imagen
                g2d.setClip(originalClip); // Restaura el estado del recorte
            } else {
                // Si no hay imagen, dibuja líquido rojo como antes
                g2d.setColor(new Color(220, 60, 60));
                g2d.fill(contentShape);
            }

            // 3. Dibuja el cuerpo de cristal translúcido por encima
            GeneralPath glassShape = (GeneralPath) contentShape.clone();
            g2d.setColor(new Color(210, 230, 255, 70));
            g2d.fill(glassShape);

            // 4. Dibuja el borde superior (la boca del vaso)
            g2d.setColor(new Color(210, 230, 255, 150));
            g2d.setStroke(new BasicStroke(4));
            g2d.draw(new Ellipse2D.Double(x, y + 15, topWidth, 12));

            // 5. Dibuja un reflejo de luz brillante
            g2d.setColor(new Color(255, 255, 255, 120));
            g2d.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(x + 18, y + 35, x + 18, y + 85);
            
            // 6. Dibuja el número
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 38));
            String valueStr = String.valueOf(value);
            FontMetrics fm = g2d.getFontMetrics();
            // Dibuja una sombra para el texto para que sea más legible
            g2d.setColor(Color.BLACK);
            g2d.drawString(valueStr, x + (topWidth - fm.stringWidth(valueStr)) / 2 + 2, y + 82);
            g2d.setColor(Color.WHITE);
            g2d.drawString(valueStr, x + (topWidth - fm.stringWidth(valueStr)) / 2, y + 80);
        }

        private void drawHand(Graphics2D g2d, int x, int y, boolean isLeft) {
            Color skinColor = new Color(252, 220, 186); Color outlineColor = new Color(199, 169, 143);
            g2d.setStroke(new BasicStroke(2)); g2d.setColor(skinColor); g2d.fillOval(x, y, 50, 50);
            g2d.setColor(outlineColor); g2d.drawOval(x, y, 50, 50); int thumbX = isLeft ? x + 40 : x - 10;
            g2d.setColor(skinColor); g2d.fillOval(thumbX, y + 25, 20, 20);
            g2d.setColor(outlineColor); g2d.drawOval(thumbX, y + 25, 20, 20);
        }
        
        public SortableItem[] getItems() { return items; }
        public int getAnimationDuration() { return animationDuration; }
        public void setAnimationDuration(int d) { this.animationDuration = d; }
        public void setSortedUntilIndex(int i) { this.sortedUntilIndex = i; }
        public void setTargetSlotIndex(int i) { this.targetSlotIndex = i; }
        public void setMinFoundIndex(int i) { this.minFoundIndex = i; }
        public void setComparingIndex(int i) { this.comparingIndex = i; }
    }

    public static void main(String[] args) {
        int[] array = {53, 25, 78, 12, 41, 90, 66};
        SwingUtilities.invokeLater(() -> new SelectionSortGUI(array).setVisible(true));
    }
}