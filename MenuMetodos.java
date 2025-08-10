import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class MenuMetodos extends JFrame {
    private static final Map<String, ImageIcon> imageCache = new HashMap<>();
    private float rotationAngle = 0;
    
    public MenuMetodos() {
        setTitle("Métodos de Ordenamiento Unificados");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
        startSpinnerAnimation();
    }
    
    public static ImageIcon loadImage(String path) {
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }
        
        try {
            ImageIcon icon = new ImageIcon(path);
            imageCache.put(path, icon);
            return icon;
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + path);
            return null;
        }
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Fondo degradado
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
        
        // Métodos de ordenamiento
        String[] metodos = {
            "Bucket Sort", "Counting Sort", "Counting Sort (Cartas)", 
            "Heap Sort", "Merge Sort", "Inserción", 
            "Quick Sort", "Selection Sort"
        };
        
        for (String metodo : metodos) {
            mainPanel.add(createMethodCard(metodo), gbc);
        }
        
        add(mainPanel);
    }
    
    private JPanel createMethodCard(String nombre) {
        JPanel card = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Dibujar spinner
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                AffineTransform oldTransform = g2d.getTransform();
                g2d.rotate(rotationAngle, 30, 30);
                
                ImageIcon spinnerIcon = loadImage("ANEXOS/spinner.png");
                if (spinnerIcon != null) {
                    spinnerIcon.paintIcon(this, g, 10, 10);
                }
                
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
        viewButton.addActionListener(e -> openAlgorithm(nombre));
        
        // Efecto hover
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
    
    private void startSpinnerAnimation() {
        Timer timer = new Timer(20, e -> {
            rotationAngle += 0.1;
            if (rotationAngle > Math.PI * 2) rotationAngle = 0;
            repaint();
        });
        timer.start();
    }
    
    private void openAlgorithm(String nombre) {
        // Aquí implementarías la lógica para abrir cada algoritmo
        JOptionPane.showMessageDialog(this, "Abriendo: " + nombre);
        
        // Ejemplo para MergeSort:
        /*
        if (nombre.equals("Merge Sort")) {
            MergeSort mergeSort = new MergeSort();
            mergeSort.setVisible(true);
        }
        */
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new MenuMetodos().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}