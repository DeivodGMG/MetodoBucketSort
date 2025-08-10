import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class MergeSort extends JPanel {

    private static final int ANCHO_BOLSA = 48; // Define para el ancho de la imagen de la bolsa
    private static final int ALTO_BOLSA = 64; // Define para la altura de la imagen de la bolsa
    private static final int ESPACIADO_BOLSA = 70; // Define el espaciado de cada bolsa.

    private static final int POSICION_Y_BASE = 350; // Define la posición vertical donde las bolsas se alinean originalmente y al final
    private static final int POSICION_Y_ESPERA = 150; // Define la posición vertical a la que se mueven las bolsas temporalmente durante el proceso

    private static final int ALTO_VENTANA = 500; // Define una constante para la altura de la ventana principal
    private static final int ANCHO_PERSONAJE = 80; // Define el ancho del personaje
    private static final int ALTO_PERSONAJE = 80; // Define la altura del personaje
    private static final int MARGEN_LATERAL = 50; // Define el margen

    private static final int PASOS_ANIMACION = 25; // Define el numero de frames por animacion
    private static final int RETARDO_ANIMACION = 10; // Define el retardo en milisegundos entre cada paso de la animacion

    private Bolsa[] bolsas; // Declara un arreglo para almacenar los objetos de tipo Bolsa que se ordenaran
    private int cantidadBolsas; // Declara una variable para almacenar la cantidad actual de bolsas
    private BufferedImage imagenBolsa, imagenFondo, imagenPersonaje;
    private Thread hiloAnimacion; // Declara un hilo para la animacion
    private int personajeX, personajeY; // Declara las coordenadas X e Y para la posición del personaje

    // Constructor de la clase MergeSort.
    public MergeSort() {
        // Carga las imágenes necesarias que son el fondo, la bolsa y el personaje.
        // Viene a color el personaje en posicion inicial.
        // Prepara la escena por primera vez con 10 bolsas, pero sin empezar a ordenar.
        cargarImagenes();
        this.personajeY = POSICION_Y_ESPERA - ALTO_PERSONAJE - 25;
        reiniciarAnimacion(10, false);
    }

    // Este metodo prepara la animacion
    public void reiniciarAnimacion(int nuevaCantidad, boolean iniciarOrdenamiento) {
        // Detiene cualquier animación anterior
        if (hiloAnimacion != null && hiloAnimacion.isAlive()) {
            hiloAnimacion.interrupt();
        }

        // Despues onfigura el escenario con la nueva cantidad de bolsas
        // Y crea un arreglo de bolsas con valores aleatorios
        this.cantidadBolsas = nuevaCantidad;
        int anchoCalculado = (this.cantidadBolsas * ESPACIADO_BOLSA) + (MARGEN_LATERAL * 2);
        setPreferredSize(new Dimension(anchoCalculado, ALTO_VENTANA));
        this.personajeX = (anchoCalculado - ANCHO_PERSONAJE) / 2;
        bolsas = new Bolsa[this.cantidadBolsas];
        Random aleatorio = new Random();
        for (int i = 0; i < this.cantidadBolsas; i++) {
            int valor = aleatorio.nextInt(100) + 1;
            int x = MARGEN_LATERAL + i * ESPACIADO_BOLSA;
            bolsas[i] = new Bolsa(valor, x, POSICION_Y_BASE, imagenBolsa);
        }

        // Actualiza la pantalla para mostrar las nuevas bolsas
        revalidate();
        repaint();

        // En caso de que el usuario presione el botón para ordenar, crea y lanza un nuevo hilo para la animacion
        // El hilo se encarga de llamar al algoritmo de ordenamiento y de actualizar los colores al final
        // Al terminar, reactiva el botón de "Ordenar"
        if (iniciarOrdenamiento) {
            hiloAnimacion = new Thread(() -> {
                try {
                    Thread.sleep(500);
                    ordenarPorFusion(0, this.cantidadBolsas - 1);
                    for (Bolsa b : bolsas) {
                        b.color = new Color(34, 139, 34); // Color verde al final
                        repaint();
                        Thread.sleep(50);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    SwingUtilities.invokeLater(() -> {
                        JButton boton = encontrarBotonOrdenar();
                        if (boton != null) boton.setEnabled(true);
                    });
                }
            });
            hiloAnimacion.start();
        }
    }

    // Lo siguiente carga los gráficos y pintarlos en pantalla.
    private JButton encontrarBotonOrdenar() {
        Container parent = this.getParent();
        while (parent != null) {
            if (parent instanceof JFrame) {
                Component[] components = ((JFrame) parent).getContentPane().getComponents();
                for (Component comp : components) {
                    if (comp instanceof JPanel) {
                        for(Component sub : ((JPanel) comp).getComponents()){
                            if(sub instanceof JButton){ return (JButton) sub; }
                        }
                    }
                }
            }
            parent = parent.getParent();
        }
        return null;
    }

    // Busca y carga las imagenes correspondientes
    // En caso de que no encuentra una imagen, pone un color de fondo por defecto para que el programa no falle
    private void cargarImagenes() {
        try {
            imagenFondo = ImageIO.read(new File("C:\\Users\\david\\Downloads\\fondo.png"));
        } catch (IOException e) { setBackground(new Color(30, 30, 120)); }
        try {
            imagenPersonaje = ImageIO.read(new File("C:\\Users\\david\\Downloads\\personaje.png"));
        } catch (IOException e) { /**/ }
        try {
            imagenBolsa = ImageIO.read(new File("C:\\Users\\david\\Downloads\\barril.png"));
        } catch (IOException e) { /**/ }
    }

    // Lo siguiente dibuja todo en orden, primero el fondo, luego el personaje, y al final cada una de las bolsas
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondo != null) g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        if (imagenPersonaje != null) g.drawImage(imagenPersonaje, this.personajeX, this.personajeY, ANCHO_PERSONAJE, ALTO_PERSONAJE, this);
        if (bolsas != null) {
            for (Bolsa b : bolsas) {
                if (b != null) b.dibujar(g);
            }
        }
    }

    // Anima el movimiento de una sola bolsa desde su posición hasta su destino
    // lo hace en pequeños pasos para que el movimiento se vea fluido
    private void animarMovimiento(Bolsa bolsa, int destinoX, int destinoY) throws InterruptedException {
        int inicioX = bolsa.x, inicioY = bolsa.y;
        double dx = (double) (destinoX - inicioX) / PASOS_ANIMACION;
        double dy = (double) (destinoY - inicioY) / PASOS_ANIMACION;
        for (int i = 1; i <= PASOS_ANIMACION; i++) {
            bolsa.x = inicioX + (int) (dx * i);
            bolsa.y = inicioY + (int) (dy * i);
            repaint(); // Pide redibujar la pantalla en cada paso
            Thread.sleep(RETARDO_ANIMACION); // Esta una pequeña pausa para la animacion
        }
    }

    // Hace lo del metodo pasado pero para el personaje
    private void animarMovimientoPersonaje(int destinoX) throws InterruptedException {
        int inicioX = this.personajeX;
        double dx = (double) (destinoX - inicioX) / PASOS_ANIMACION;
        for (int i = 1; i <= PASOS_ANIMACION; i++) {
            this.personajeX = inicioX + (int) (dx * i);
            repaint();
            Thread.sleep(RETARDO_ANIMACION);
        }
        this.personajeX = destinoX;
        repaint();
    }

    // Lo que hace divide el arreglo de bolsas a la mitad.
    // se llama a si mismo para ordenar la mitad izquierda.
    // se llama a sí mismo para ordenar la mitad derecha.
    // una vez que las dos mitades estan ordenadas, las "fusiona" en un solo grupo
    private void ordenarPorFusion(int izquierda, int derecha) throws InterruptedException {
        if (izquierda < derecha) {
            int medio = izquierda + (derecha - izquierda) / 2;
            ordenarPorFusion(izquierda, medio);
            ordenarPorFusion(medio + 1, derecha);
            fusionar(izquierda, medio, derecha);
        }
    }

    // Toma dos sub arreglos ya ordenados, el izquierdo y el derecho y los combina
    private void fusionar(int izquierda, int medio, int derecha) throws InterruptedException {
        // Mueve al personaje al centro de la ventana
        int xPixelIzquierda = MARGEN_LATERAL + izquierda * ESPACIADO_BOLSA;
        int xPixelDerecha = MARGEN_LATERAL + derecha * ESPACIADO_BOLSA + ANCHO_BOLSA;
        int xCentroSubArray = (xPixelIzquierda + xPixelDerecha) / 2 - (ANCHO_PERSONAJE / 2);
        animarMovimientoPersonaje(xCentroSubArray);

        // Crea arreglos temporales y levanta las bolsas correspondientes a la zona donde se verifican
        // pinta las bolsas de la izquierda de cian y las de la derecha de naranja para distinguirlas
        int n1 = medio - izquierda + 1, n2 = derecha - medio;
        Bolsa[] arrIzquierdo = new Bolsa[n1];
        Bolsa[] arrDerecho = new Bolsa[n2];
        for (int i = 0; i < n1; i++) {
            arrIzquierdo[i] = bolsas[izquierda + i];
            arrIzquierdo[i].color = Color.CYAN;
            animarMovimiento(arrIzquierdo[i], arrIzquierdo[i].x, POSICION_Y_ESPERA);
        }
        for (int i = 0; i < n2; i++) {
            arrDerecho[i] = bolsas[medio + 1 + i];
            arrDerecho[i].color = Color.ORANGE;
            animarMovimiento(arrDerecho[i], arrDerecho[i].x, POSICION_Y_ESPERA);
        }
        Thread.sleep(300);

        // Compara las bolsas una por una, pinta de rojo las que se están comparando.
        // la que tenga el valor más pequeño, la mueve para abajo, donde se pondra en una nueva posicion.
        // la bolsa movida se pinta de amarillo durante el movimiento y cambia a verde palido cuando llega al lugar.
        int i = 0, j = 0, k = izquierda;
        while (i < n1 && j < n2) {
            arrIzquierdo[i].color = Color.RED;
            arrDerecho[j].color = Color.RED;
            repaint();
            Thread.sleep(400);

            Bolsa bolsaAMover;
            if (arrIzquierdo[i].valor <= arrDerecho[j].valor) {
                bolsaAMover = arrIzquierdo[i]; i++;
            } else {
                bolsaAMover = arrDerecho[j]; j++;
            }
            bolsas[k] = bolsaAMover;
            bolsaAMover.color = Color.YELLOW;
            animarMovimiento(bolsaAMover, MARGEN_LATERAL + k * ESPACIADO_BOLSA, POSICION_Y_BASE);
            bolsaAMover.color = new Color(152, 251, 152);

            if (i < n1) arrIzquierdo[i].color = Color.CYAN;
            if (j < n2) arrDerecho[j].color = Color.ORANGE;
            repaint();
            Thread.sleep(100);
            k++;
        }

        // Aqui si sobran bolsas en alguno de los arreglos temporales, simplemente las baja en orden
        while (i < n1) {
            bolsas[k] = arrIzquierdo[i];
            arrIzquierdo[i].color = Color.YELLOW;
            animarMovimiento(arrIzquierdo[i], MARGEN_LATERAL + k * ESPACIADO_BOLSA, POSICION_Y_BASE);
            arrIzquierdo[i].color = new Color(152, 251, 152);
            i++; k++;
        }
        while (j < n2) {
            bolsas[k] = arrDerecho[j];
            arrDerecho[j].color = Color.YELLOW;
            animarMovimiento(arrDerecho[j], MARGEN_LATERAL + k * ESPACIADO_BOLSA, POSICION_Y_BASE);
            arrDerecho[j].color = new Color(152, 251, 152);
            j++; k++;
        }

        // Al terminar la verificacion de las bolsas, el personaje vuelve al centro de toda la escena.
        int xCentroTotal = (getWidth() - ANCHO_PERSONAJE) / 2;
        animarMovimientoPersonaje(xCentroTotal);
        repaint();
        Thread.sleep(300);
    }

    // Aqui el metodo main inicia toda la animacion
    // crea la ventana, el panel de control y el panel de animacion.
    // lo une y lo hace visible en la ventana
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Animación de Ordenamiento por Fusión");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            MergeSort panelAnimacion = new MergeSort();

            JPanel panelControl = new JPanel();
            panelControl.add(new JLabel("Número de bolsas:"));
            SpinnerNumberModel spinnerModel = new SpinnerNumberModel(10, 2, 15, 1);
            JSpinner spinnerCantidad = new JSpinner(spinnerModel);
            panelControl.add(spinnerCantidad);

            JButton botonOrdenar = new JButton("Ordenar / Reiniciar");
            panelControl.add(botonOrdenar);

            // Aqui define la acción que ocurre al presionar el boton
            // Llama a reiniciarAnimacion, deshabilita el botón y ajusta el tamaño de la ventana.
            botonOrdenar.addActionListener(e -> {
                int cantidadSeleccionada = (int) spinnerCantidad.getValue();
                botonOrdenar.setEnabled(false);
                panelAnimacion.reiniciarAnimacion(cantidadSeleccionada, true);
                SwingUtilities.getWindowAncestor(panelAnimacion).pack();
            });

            frame.setLayout(new BorderLayout());
            frame.add(panelControl, BorderLayout.NORTH);
            frame.add(panelAnimacion, BorderLayout.CENTER);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }

    // Esta es una clase interna que actua como una plantilla para cada bolsa
    // Cada objeto, o sea la bolsa, contiene su propio valor, su posición que viene siendo x e y, su imagen y el color de su indicador.
    static class Bolsa {
        int valor, x, y;
        BufferedImage imagen; Color color = Color.GRAY;
        public Bolsa(int valor, int x, int y, BufferedImage imagen) {
            this.valor = valor; this.x = x; this.y = y; this.imagen = imagen;
        }
        // Aqui dibuja la imagen, el valor de esa imagen y el color que tendra.
        public void dibujar(Graphics g) {
            if (imagen != null) g.drawImage(imagen, x, y, ANCHO_BOLSA, ALTO_BOLSA, null);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            String valorStr = Integer.toString(valor);
            FontMetrics fm = g.getFontMetrics();
            int anchoTexto = fm.stringWidth(valorStr);
            g.drawString(valorStr, x + (ANCHO_BOLSA - anchoTexto) / 2, y - 10);
            g.setColor(color);
            g.fillRect(x + 4, y + ALTO_BOLSA + 2, ANCHO_BOLSA - 8, 10);
            g.setColor(color.darker());
            g.drawRect(x + 4, y + ALTO_BOLSA + 2, ANCHO_BOLSA - 8, 10);
        }
    }
}