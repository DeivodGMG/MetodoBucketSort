import javax.swing.*;

/**
 * Clase principal que inicia la aplicación del visualizador QuickSort
 * Crea la ventana principal y muestra el mensaje de bienvenida
 */
public class Main {
    /**
     * Método principal que se ejecuta al iniciar el programa
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        // Ejecutar la interfaz gráfica en el hilo de eventos de Swing
        // Esto es una buena práctica para aplicaciones GUI
        SwingUtilities.invokeLater(() -> {
            // Crear la ventana principal de la aplicación
            JFrame ventana = new JFrame("Visualizador QuickSort - Cocina de DavidSCG");

            // Configurar el comportamiento al cerrar la ventana
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Agregar el panel principal (visualizador) a la ventana
            ventana.add(new QuickSortVista());

            // Ajustar automáticamente el tamaño de la ventana al contenido
            ventana.pack();

            // Centrar la ventana en la pantalla
            ventana.setLocationRelativeTo(null);

            // Mostrar mensaje de bienvenida con instrucciones
            JOptionPane.showMessageDialog(ventana,
                    "Bienvenido a la Cocina de DavidSCG!\n" +
                            "Ordena las cajas de comida por peso usando el algoritmo QuickSort.\n" +
                            "Que disfrutes la simulacion!",
                    "Bienvenida",
                    JOptionPane.INFORMATION_MESSAGE);

            // Hacer visible la ventana (debe ser lo último)
            ventana.setVisible(true);
        });
    }
} 