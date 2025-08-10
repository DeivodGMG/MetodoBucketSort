import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;


public class CountingSortCartas extends JFrame {

    // --- SECCIÓN 1: CONSTANTES Y ATRIBUTOS ---

    // --- Constantes de la Aplicación ---
    private static final int MAX_INPUT = 12; // Máximo de números permitidos en la entrada.
    private static final int RANGO = 10;     // Los números de entrada deben estar en el rango [0, 9].
    // Colores temáticos de UNO para el fondo visual.
    private static final Color UNO_ROJO = new Color(255, 87, 87);
    private static final Color UNO_AMARILLO = new Color(255, 217, 26);
    private static final Color UNO_VERDE = new Color(85, 170, 85);
    private static final Color UNO_AZUL = new Color(0, 170, 255);
    // Color para resaltar elementos activos durante la animación.
    private static final Color HIGHLIGHT_COLOR = Color.CYAN;

    // --- Componentes de la Interfaz Gráfica (GUI) ---
    private JTextField inputField; // Campo de texto para que el usuario ingrese los números.
    private JButton ordenarButton, reiniciarButton; // Botones para iniciar y reiniciar la visualización.
    private JPanel cartasPanel; // El panel principal donde se dibuja toda la animación.

    // --- Estructuras de Datos para el Algoritmo y la Visualización ---
    private String[] cartasActuales;    // Arreglo de strings para las cartas de entrada que se van "consumiendo".
    private int[] numerosOriginales;  // Almacena los números tal como los ingresó el usuario.
    private String[] cartasSalida;      // Arreglo de strings para las cartas ya ordenadas que se van colocando.
    private int[] arregloConteo;      // El arreglo 'count' central del algoritmo Counting Sort.

    // --- Control de la Animación ---
    private Timer animacionTimer; // Temporizador que controla el ritmo de la animación, disparando cada paso.
    private final Map<String, ImageIcon> cacheImagenes; // Mapa para almacenar las imágenes de las cartas y mejorar el rendimiento.

    // --- Variables de Estado de la Animación ---
    // Enum para definir los posibles estados del ciclo de la animación. Facilita el control del flujo.
    private enum EstadoAnimacion { INACTIVA, CONTEO, SUMA, COLOCACION, FINALIZADA }
    // Variable que guarda el estado actual de la animación.
    private EstadoAnimacion estadoAnimacion = EstadoAnimacion.INACTIVA;
    // Contador para saber en qué paso de una fase de la animación nos encontramos (ej. qué carta contar).
    private int pasoAnimacion = 0;
    // Índices para saber qué elemento visual resaltar en la pantalla (-1 significa ninguno).
    private int indiceResaltadoEntrada = -1;
    private int indiceResaltadoConteo = -1;

    /**
     * Constructor de la clase. Se encarga de inicializar la ventana,
     * configurar los componentes de la interfaz, cargar las imágenes y
     * asignar los manejadores de eventos a los botones.
     */
    public CountingSortCartas() {
        // --- SECCIÓN 2: CONFIGURACIÓN DE LA VENTANA Y LA INTERFAZ ---

        // Configuración básica de la ventana principal (JFrame).
        setTitle("Visualizador de Counting Sort con Cartas UNO");
        setSize(1200, 750);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null); // Centra la ventana en la pantalla.
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Cierra la aplicación al cerrar la ventana.
        setLayout(new BorderLayout(10, 10)); // Layout principal con espaciado.

        // Pre-carga de las imágenes de las cartas en un mapa (caché) para optimizar el rendimiento.
        // Esto evita leer los archivos de imagen del disco en cada redibujado.
        this.cacheImagenes = new HashMap<>();
        for (int i = 0; i < RANGO; i++) {
            cacheImagenes.put(String.valueOf(i), new ImageIcon("img/" + i + ".png"));
        }
        cacheImagenes.put("back", new ImageIcon("img/back.png"));

        // Creación y configuración del panel superior que contiene los controles.
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Margen interno.

        // Inicialización de los componentes de la interfaz.
        inputField = new JTextField(25);
        inputField.setFont(new Font("Arial", Font.PLAIN, 16));
        ordenarButton = new JButton("Visualizar Ordenamiento");
        reiniciarButton = new JButton("Reiniciar");
        JLabel instructionLabel = new JLabel("Ingresa hasta " + MAX_INPUT + " números del 0 al 9:");
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Se añaden los componentes al panel superior.
        topPanel.add(instructionLabel);
        topPanel.add(inputField);
        topPanel.add(ordenarButton);
        topPanel.add(reiniciarButton);

        // Creación del panel de visualización principal.
        // Se utiliza una clase anónima que hereda de JPanel para sobreescribir el método de dibujo.
        cartasPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); // Llama al método del padre para limpiar el panel.
                Graphics2D g2d = (Graphics2D) g; // Se convierte a Graphics2D para tener más control.
                // Activa el antialiasing para suavizar los bordes de las formas y texto.
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dibuja el fondo temático de 4 colores de UNO.
                int w = getWidth();
                int h = getHeight();
                g2d.setColor(UNO_ROJO);
                g2d.fillRect(0, 0, w / 2, h / 2);
                g2d.setColor(UNO_AMARILLO);
                g2d.fillRect(w / 2, 0, w / 2, h / 2);
                g2d.setColor(UNO_AZUL);
                g2d.fillRect(0, h / 2, w / 2, h / 2);
                g2d.setColor(UNO_VERDE);
                g2d.fillRect(w / 2, h / 2, w / 2, h / 2);

                // Llama al método que se encarga de dibujar todas las secciones del algoritmo.
                dibujarSecciones(g2d);
            }
        };

        // Ensamblaje final de la interfaz.
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.DARK_GRAY);
        mainPanel.add(topPanel, BorderLayout.NORTH); // Controles en la parte superior.
        mainPanel.add(cartasPanel, BorderLayout.CENTER); // Visualización en el centro.
        getContentPane().add(mainPanel); // Se añade el panel principal a la ventana.

        // Asignación de los manejadores de eventos (listeners) a los botones.
        // Se usan expresiones lambda para definir la acción a realizar.
        ordenarButton.addActionListener(e -> procesarEntradaYOrdenar());
        reiniciarButton.addActionListener(e -> reiniciar());
    }

    // --- SECCIÓN 3: MÉTODOS DE DIBUJO ---

    /**
     * Orquesta el dibujo de todas las secciones visuales del algoritmo:
     * entrada, arreglo de conteo y salida.
     * @param g2d El contexto gráfico 2D para dibujar.
     */
    private void dibujarSecciones(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.setColor(Color.WHITE);

        int altoPanel = getHeight();
        int margenTitulo = 50;

        // Define las posiciones verticales (Y) para cada sección.
        int seccionEntradaY = altoPanel / 6;
        int seccionConteoY = altoPanel / 2;
        int seccionSalidaY = altoPanel * 3 / 4;

        // Dibuja el título y las cartas de la sección de entrada.
        g2d.drawString("1. Entrada Original", 20, seccionEntradaY - margenTitulo);
        dibujarCartas(g2d, cartasActuales, seccionEntradaY, indiceResaltadoEntrada);

        // Dibuja el título y el arreglo de conteo.
        g2d.drawString("2. Arreglo de Conteo (Count)", 20, seccionConteoY - margenTitulo);
        dibujarArregloConteo(g2d, seccionConteoY);

        // Dibuja el título y las cartas de la sección de salida ordenada.
        g2d.drawString("3. Resultado Ordenado (Output)", 20, seccionSalidaY - margenTitulo);
        dibujarCartas(g2d, cartasSalida, seccionSalidaY, -1);
    }

    /**
     * Dibuja un arreglo de cartas en una posición vertical específica.
     * @param g2d El contexto gráfico 2D.
     * @param cartas El arreglo de strings que representa las cartas a dibujar.
     * @param yPos La coordenada Y central para dibujar las cartas.
     * @param indiceResaltado El índice de la carta que debe ser resaltada. Si es -1, ninguna se resalta.
     */
    private void dibujarCartas(Graphics2D g2d, String[] cartas, int yPos, int indiceResaltado) {
        if (cartas == null) return; // Si no hay cartas, no dibuja nada.

        int anchoCarta = 60;
        int altoCarta = 90;
        int espacio = 15;
        // Calcula la posición inicial X para centrar el conjunto de cartas.
        int totalAncho = cartas.length * (anchoCarta + espacio) - espacio;
        int inicioX = (getWidth() - totalAncho) / 2;
        int inicioY = yPos - altoCarta / 2;

        for (int i = 0; i < cartas.length; i++) {
            int x = inicioX + i * (anchoCarta + espacio);
            // Dibuja un fondo semitransparente para el espacio de la carta.
            g2d.setColor(new Color(0, 0, 0, 60));
            g2d.fillRoundRect(x, inicioY, anchoCarta, altoCarta, 10, 10);

            if (cartas[i] != null) { // Si la carta existe en esta posición...
                // Si la carta debe ser resaltada, dibuja un borde de color.
                if (i == indiceResaltado) {
                    g2d.setColor(HIGHLIGHT_COLOR);
                    g2d.setStroke(new BasicStroke(4)); // Borde grueso.
                    g2d.drawRoundRect(x - 4, inicioY - 4, anchoCarta + 8, altoCarta + 8, 20, 20);
                }
                // Obtiene la imagen de la carta del caché y la dibuja.
                ImageIcon icono = cacheImagenes.get(cartas[i]);
                if (icono != null) {
                    g2d.drawImage(icono.getImage(), x, inicioY, anchoCarta, altoCarta, this);
                }
            }
        }
    }

    /**
     * Dibuja el arreglo de conteo como una serie de cajas con valores.
     * @param g2d El contexto gráfico 2D.
     * @param yPos La coordenada Y central para dibujar el arreglo.
     */
    private void dibujarArregloConteo(Graphics2D g2d, int yPos) {
        if (arregloConteo == null) return;

        int anchoCaja = 50;
        int altoCaja = 50;
        int espacio = 10;
        // Calcula la posición inicial X para centrar el arreglo.
        int totalAncho = RANGO * (anchoCaja + espacio) - espacio;
        int inicioX = (getWidth() - totalAncho) / 2;
        int inicioY = yPos - altoCaja/2;

        for (int i = 0; i < RANGO; i++) {
            int x = inicioX + i * (anchoCaja + espacio);
            // Dibuja el fondo de la caja.
            g2d.setColor(new Color(0, 0, 0, 120));
            g2d.fillRoundRect(x, inicioY, anchoCaja, altoCaja, 10, 10);

            // Si esta caja debe ser resaltada, dibuja un borde.
            if (i == indiceResaltadoConteo) {
                g2d.setColor(HIGHLIGHT_COLOR);
                g2d.setStroke(new BasicStroke(4));
                g2d.drawRoundRect(x-2, inicioY - 2, anchoCaja + 4, altoCaja + 4, 15, 15);
            }

            // Dibuja el índice (0-9) encima de la caja.
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            String indiceStr = String.valueOf(i);
            g2d.drawString(indiceStr, x + anchoCaja/2 - g2d.getFontMetrics().stringWidth(indiceStr)/2, inicioY - 5);

            // Dibuja el valor (conteo) dentro de la caja.
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            String valorStr = String.valueOf(arregloConteo[i]);
            g2d.drawString(valorStr, x + anchoCaja/2 - g2d.getFontMetrics().stringWidth(valorStr)/2, inicioY + 35);
        }
    }

    // --- SECCIÓN 4: MÉTODOS DE CONTROL DE LA APLICACIÓN Y ANIMACIÓN ---

    /**
     * Inicia la secuencia de animación. Prepara las estructuras de datos,
     * deshabilita los botones e inicia el Timer.
     */
    private void ejecutarAnimacion() {
        if (numerosOriginales == null) return;

        pasoAnimacion = 0;
        ordenarButton.setEnabled(false); // Evita interacciones durante la animación.
        reiniciarButton.setEnabled(false);

        // Prepara los arreglos visuales para el inicio de la animación.
        cartasActuales = new String[numerosOriginales.length];
        for (int i = 0; i < numerosOriginales.length; i++) {
            cartasActuales[i] = String.valueOf(numerosOriginales[i]);
        }
        cartasSalida = new String[numerosOriginales.length];
        arregloConteo = new int[RANGO];
        estadoAnimacion = EstadoAnimacion.CONTEO; // Comienza en la fase de conteo.

        // Crea y arranca el Timer. Cada 700ms, se ejecutará el método pasoDeAnimacion.
        animacionTimer = new Timer(700, e -> pasoDeAnimacion());
        animacionTimer.start();
    }

    /**
     * Contiene la lógica de un único paso de la animación. Es el corazón del
     * visualizador y es llamado repetidamente por el Timer.
     */
    private void pasoDeAnimacion() {
        // Limpia los resaltados anteriores antes de cada paso.
        indiceResaltadoEntrada = -1;
        indiceResaltadoConteo = -1;

        // El switch controla la lógica según la fase actual del algoritmo.
        switch (estadoAnimacion) {
            case CONTEO: // Fase 1: Contar la frecuencia de cada carta.
                if (pasoAnimacion < numerosOriginales.length) {
                    int numero = numerosOriginales[pasoAnimacion];
                    indiceResaltadoEntrada = pasoAnimacion; // Resalta la carta actual.
                    indiceResaltadoConteo = numero;        // Resalta la caja del contador.
                    arregloConteo[numero]++;               // Incrementa el contador.
                    pasoAnimacion++;
                } else { // Si se terminaron de contar todas las cartas...
                    estadoAnimacion = EstadoAnimacion.SUMA; // Pasa a la siguiente fase.
                    pasoAnimacion = 1; // Reinicia el contador de paso para la nueva fase.
                }
                break;
            case SUMA: // Fase 2: Calcular la suma acumulativa en el arreglo de conteo.
                if (pasoAnimacion < RANGO) {
                    indiceResaltadoConteo = pasoAnimacion; // Resalta la caja actual.
                    arregloConteo[pasoAnimacion] += arregloConteo[pasoAnimacion - 1]; // Suma el valor anterior.
                    pasoAnimacion++;
                } else { // Si se terminó de sumar...
                    estadoAnimacion = EstadoAnimacion.COLOCACION; // Pasa a la fase final.
                    pasoAnimacion = numerosOriginales.length - 1; // Se empieza a colocar desde la última carta.
                }
                break;
            case COLOCACION: // Fase 3: Colocar las cartas en su posición de salida.
                if (pasoAnimacion >= 0) {
                    int numero = numerosOriginales[pasoAnimacion];
                    indiceResaltadoEntrada = pasoAnimacion; // Resalta la carta que se va a mover.
                    indiceResaltadoConteo = numero;        // Resalta la caja del contador para ver la posición.
                    int pos = arregloConteo[numero] - 1;   // Calcula la posición de salida.
                    cartasSalida[pos] = String.valueOf(numero); // Coloca la carta en la salida.
                    arregloConteo[numero]--;               // Decrementa el contador.
                    cartasActuales[pasoAnimacion] = null;  // "Elimina" la carta de la entrada.
                    pasoAnimacion--; // Pasa a la carta anterior.
                } else { // Si ya se colocaron todas las cartas...
                    estadoAnimacion = EstadoAnimacion.FINALIZADA;
                    animacionTimer.stop(); // Detiene la animación.
                    ordenarButton.setEnabled(true); // Rehabilita los botones.
                    reiniciarButton.setEnabled(true);
                }
                break;
        }
        cartasPanel.repaint(); // Fuerza el redibujado de la pantalla para mostrar los cambios.
    }

    /**
     * Restaura la aplicación a su estado inicial.
     */
    private void reiniciar() {
        if (animacionTimer != null && animacionTimer.isRunning()) {
            animacionTimer.stop(); // Detiene cualquier animación en curso.
        }
        // Limpia el campo de texto y todas las estructuras de datos.
        inputField.setText("");
        cartasActuales = null;
        cartasSalida = null;
        arregloConteo = null;
        numerosOriginales = null;
        // Resetea las variables de estado.
        indiceResaltadoEntrada = -1;
        indiceResaltadoConteo = -1;
        estadoAnimacion = EstadoAnimacion.INACTIVA;

        cartasPanel.repaint(); // Limpia la pantalla.
        ordenarButton.setEnabled(true);
        reiniciarButton.setEnabled(true);
    }

    /**
     * Lee y valida la entrada del usuario. Si es válida, inicia la animación.
     */
    private void procesarEntradaYOrdenar() {
        String texto = inputField.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa números.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] tokens = texto.split("\\s+"); // Divide la entrada por espacios.
        if (tokens.length > MAX_INPUT) {
            JOptionPane.showMessageDialog(this, "Solo puedes ingresar hasta " + MAX_INPUT + " números.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Convierte los tokens de texto a números enteros.
        this.numerosOriginales = new int[tokens.length];
        try {
            for (int i = 0; i < tokens.length; i++) {
                int n = Integer.parseInt(tokens[i]);
                if (n < 0 || n >= RANGO) { // Valida que el número esté en el rango permitido.
                    throw new NumberFormatException("Número fuera de rango.");
                }
                this.numerosOriginales[i] = n;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error en la entrada. Ingresa solo números entre 0 y 9.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ejecutarAnimacion(); // Si todo es correcto, comienza la visualización.
    }

    /**
     * Método principal que inicia la aplicación.
     * @param args Argumentos de la línea de comandos (no se usan).
     */
    public static void main(String[] args) {
        // Se asegura de que la creación de la GUI se haga en el Event Dispatch Thread (EDT),
        // que es la práctica recomendada para evitar problemas de concurrencia en Swing.
        SwingUtilities.invokeLater(() -> new CountingSortCartas().setVisible(true));
    }
}