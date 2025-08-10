import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MenuMetodos extends JFrame {

    public MenuMetodos() {
        super("Metodos de Ordenamiento");
        setSize(1000, 800);
        setLayout(new FlowLayout(FlowLayout.CENTER, 50, 20));

        JLabel title = new JLabel("Metodos de Ordenamiento");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        add(title);

        String[] metodos = {
            "Bubble Sort", "Selection Sort", "Insertion Sort", 
            "Merge Sort", "Quick Sort", "Heap Sort", 
            "Counting Sort", "Radix Sort", "Bucket Sort"
        };

       
        ImageIcon espiralIcono = new ImageIcon("ANEXOS/espral.png");

        for (String metodo : metodos) {
            JPanel panelMetodo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            panelMetodo.setPreferredSize(new Dimension(800, 40));

          
            JLabel espiral = new JLabel(espiralIcono);
            panelMetodo.add(espiral);

            JLabel nombreMetodo = new JLabel(metodo);
            nombreMetodo.setFont(new Font("Arial", Font.PLAIN, 24));
            panelMetodo.add(nombreMetodo);

            JLabel link = new JLabel("-> Ver método");
            link.setFont(new Font("Arial", Font.ITALIC, 18));
            link.setForeground(Color.BLUE);
            link.setCursor(new Cursor(Cursor.HAND_CURSOR));

            link.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("Se ha seleccionado el método: " + metodo);
                
                }
            });

            panelMetodo.add(Box.createHorizontalGlue());
            panelMetodo.add(link);
            add(panelMetodo);
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuMetodos());
    }
}