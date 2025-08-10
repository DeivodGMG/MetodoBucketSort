import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

 //Esta es la clase principal que crea y muestra la ventana de la aplicación.
 //Organiza los botones, el control de velocidad y el panel de animación.

public class MetodoDeInsercion {

    // Define cuántas imágenes se van a ordenar.
    private static final int CANTIDAD_DE_IMAGENES = 10;

    // Estos son los componentes que se ven en la ventana.
    private JFrame ventana;           // La ventana principal.
    private PanelDeAnimacion panelDeAnimacion;    // El panel donde ocurre la animación.
    private JButton botonOrdenar;     // El botón para empezar a ordenar.
    private JButton botonReiniciar;    // El botón para barajar las imágenes de nuevo.

     //El constructor es lo que se ejecuta cuando creamos el programa.
     // Se encarga de preparar y configurar toda la ventana.

    public MetodoDeInsercion() {
        ventana = new JFrame("Visualizador de Ordenamiento Animado");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLayout(new BorderLayout());

        // Crea el panel de animación y lo pone en el centro de la ventana.
        panelDeAnimacion = new PanelDeAnimacion(CANTIDAD_DE_IMAGENES);
        ventana.add(panelDeAnimacion, BorderLayout.CENTER);

        // Crea un panel para los botones y controles en la parte de abajo.
        JPanel panelInferior = new JPanel();
        botonOrdenar = new JButton("Ordenar");
        botonReiniciar = new JButton("Reiniciar");

        // Añade todos los controles al panel inferior.
        panelInferior.add(botonOrdenar);
        panelInferior.add(botonReiniciar);

        // Pone el panel inferior en la parte de abajo de la ventana.
        ventana.add(panelInferior, BorderLayout.SOUTH);

        // Define qué pasa cuando se hace clic en los botones.
        botonOrdenar.addActionListener(e -> iniciarOrdenamiento());
        botonReiniciar.addActionListener(e -> {
            panelDeAnimacion.generarElementosAleatorios();
            botonOrdenar.setEnabled(true);
        });

        // Si la imagen se cargó bien, prepara y muestra la ventana.
        if (panelDeAnimacion.imagenCargada()) {
            ventana.pack(); // Ajusta el tamaño de la ventana al contenido.
            panelDeAnimacion.generarElementosAleatorios(); // Crea las imágenes iniciales.
            ventana.setLocationRelativeTo(null); // Centra la ventana en la pantalla.
            ventana.setVisible(true); // Hace visible la ventana.
        } else {
            ventana.dispose(); // Si no hay imagen, cierra el programa.
        }
    }

     //Este método empieza el proceso de ordenamiento en un hilo separado
     // para que la ventana no se congele.

    private void iniciarOrdenamiento() {
        botonOrdenar.setEnabled(false);
        botonReiniciar.setEnabled(false);

        // SwingWorker realiza la tarea pesada en segundo plano.
        new SwingWorker<Void, Void>() {

             //Contiene la lógica del algoritmo de ordenamiento por inserción.

            @Override
            protected Void doInBackground() throws Exception {
                ImagenMovil[] elementos = panelDeAnimacion.obtenerElementos();

                for (int i = 1; i < elementos.length; i++) {
                    // 1. Tomamos una imagen para ordenarla y dejamos un "agujero" en su lugar.
                    ImagenMovil elementoClave = elementos[i];
                    elementos[i] = null;

                    // 2. Le decimos al panel que esta es la imagen activa y la animamos para que suba.
                    panelDeAnimacion.establecerElementoActual(elementoClave);
                    panelDeAnimacion.animarElevacion(elementoClave, true);
                    // Esperamos a que la animación de "subir" termine.
                    synchronized (panelDeAnimacion.obtenerCandadoAnimacion()) {
                        panelDeAnimacion.obtenerCandadoAnimacion().wait();
                    }

                    int j = i - 1;
                    // 3. Comparamos la imagen con las que ya están ordenadas (a la izquierda).
                    while (j >= 0 && elementos[j].valor > elementoClave.valor) {
                        panelDeAnimacion.establecerIndiceDeComparacion(j);
                        // Movemos la imagen de la izquierda hacia el "agujero" a la derecha.
                        panelDeAnimacion.animarMovimiento(j, j + 1);
                        synchronized (panelDeAnimacion.obtenerCandadoAnimacion()) {
                            panelDeAnimacion.obtenerCandadoAnimacion().wait();
                        }

                        // Actualizamos el arreglo para reflejar el movimiento.
                        elementos[j + 1] = elementos[j];
                        elementos[j] = null;

                        j = j - 1;
                    }

                    // 4. Encontramos el lugar correcto. Insertamos la imagen en el "agujero" final.
                    elementos[j + 1] = elementoClave;
                    panelDeAnimacion.animarInsercion(elementoClave, j + 1);
                    synchronized (panelDeAnimacion.obtenerCandadoAnimacion()) {
                        panelDeAnimacion.obtenerCandadoAnimacion().wait();
                    }

                    // Limpiamos los indicadores visuales.
                    panelDeAnimacion.establecerIndiceDeComparacion(-1);
                    panelDeAnimacion.establecerElementoActual(null);
                }
                return null;
            }

             //Se ejecuta cuando el ordenamiento ha terminado.

            @Override
            protected void done() {
                botonReiniciar.setEnabled(true);
                JOptionPane.showMessageDialog(ventana, "¡Ordenamiento completado!");
            }
        }.execute();
    }

     // El punto de entrada del programa. Crea y ejecuta la aplicación.

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MetodoDeInsercion::new);
    }
}


 // Esta clase representa una sola imagen con su valor y posición en la pantalla.
 //Es como un "sprite" en un videojuego.

class ImagenMovil {
    int valor;           // El número que se ordena.
    BufferedImage imagen; // La imagen a dibujar.
    double x, y;         // Sus coordenadas en la pantalla para un movimiento suave.

    ImagenMovil(int valor, BufferedImage imagen, double x, double y) {
        this.valor = valor;
        this.imagen = imagen;
        this.x = x;
        this.y = y;
    }
}


 // Esta clase es el lienzo donde toda la magia visual ocurre.
 //Se encarga de dibujar y animar las imágenes.

class PanelDeAnimacion extends JPanel {

    // --- Constantes de configuración visual ---
    private static final int ALTO_IMAGEN = 100; // Alto de cada imagen.
    private static final int ESPACIO_ENTRE_IMAGENES = 10;             // Espacio entre imágenes.
    private static final int ALTURA_ELEVACION = 120;          // Cuánto sube una imagen al ser seleccionada.

    //Colores
    private static final Color COLOR_FONDO = new Color(45, 52, 54);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_SOMBRA_TEXTO = Color.BLACK;

    //Variables de estado
    private ImagenMovil[] elementos;          // El arreglo de todas las imágenes que se ordenan.
    private ImagenMovil elementoActual;      // La imagen que está "flotando" o siendo ordenada.
    private int indiceDeComparacion = -1;       // La posición de la imagen con la que se compara.
    private final int cantidadDeElementos;           // El número total de imágenes.

    private BufferedImage imagenFuente;     // La imagen cargada desde el archivo.
    private int anchoImagen;         // El ancho calculado de la imagen para mantener su forma.

    private final Object candadoAnimacion = new Object(); // Un "candado" para sincronizar la animación.

     // El constructor prepara el panel. Carga la imagen desde internet y define el tamaño del lienzo.

    public PanelDeAnimacion(int tamano) {
        this.cantidadDeElementos = tamano;
        setBackground(COLOR_FONDO);

        try {
            //Enlace de la imagen
            String enlaceImagen = "https://static.vecteezy.com/system/resources/previews/014/041/670/non_2x/cartoon-money-paper-cash-banknotes-or-golden-coins-vector.jpg"; // Enlace a una imagen de moneda.

            URL urlImagen = new URL(enlaceImagen);
            imagenFuente = ImageIO.read(urlImagen); // Lee la imagen desde el enlace de internet.

            // Calcula el ancho para que la imagen no se deforme.
            double proporcionAspecto = (double) imagenFuente.getWidth() / imagenFuente.getHeight();
            this.anchoImagen = (int) (ALTO_IMAGEN * proporcionAspecto);

        } catch (IOException e) {
            imagenFuente = null;
            JOptionPane.showMessageDialog(this, "No se pudo cargar la imagen desde la URL.\nVerifica tu conexión a internet o el enlace.", "Error de Red", JOptionPane.ERROR_MESSAGE);
        }

        // Si la imagen se cargó, define el tamaño de todo el panel.
        if (imagenCargada()) {
            int anchoTotal = (cantidadDeElementos * anchoImagen) + ((cantidadDeElementos + 1) * ESPACIO_ENTRE_IMAGENES) + 200;
            int altoTotal = ALTO_IMAGEN + ALTURA_ELEVACION + 150;
            setPreferredSize(new Dimension(anchoTotal, altoTotal));
        }
    }

     //Crea un nuevo conjunto de imágenes con valores y posiciones aleatorias.

    public void generarElementosAleatorios() {
        this.elementos = new ImagenMovil[this.cantidadDeElementos];
        Random random = new Random();

        if (getWidth() == 0) return; // No hacer nada si el panel no tiene tamaño todavía.

        // Calcula las posiciones iniciales para centrar todo el conjunto.
        int posicionXInicial = (getWidth() - (cantidadDeElementos * anchoImagen + (cantidadDeElementos - 1) * ESPACIO_ENTRE_IMAGENES)) / 2;
        int posicionY = (getHeight() - ALTO_IMAGEN) / 2 + ALTURA_ELEVACION / 2;

        for (int i = 0; i < this.cantidadDeElementos; i++) {
            int valor = random.nextInt(900) + 100; // Un número aleatorio de 3 dígitos.
            int posicionX = posicionXInicial + i * (anchoImagen + ESPACIO_ENTRE_IMAGENES);
            elementos[i] = new ImagenMovil(valor, imagenFuente, posicionX, posicionY);
        }
        elementoActual = null;
        indiceDeComparacion = -1;
        repaint(); // Pide al panel que se redibuje.
    }

     //Inicia la animación para que una imagen suba o baje.

    public void animarElevacion(ImagenMovil elemento, boolean haciaArriba) {
        double destinoY = haciaArriba ? elemento.y - ALTURA_ELEVACION : elemento.y + ALTURA_ELEVACION;
        crearAnimacionSimple(elemento, elemento.x, destinoY);
    }

     //Inicia la animación para que una imagen se mueva de un lugar a otro en la fila.

    public void animarMovimiento(int indiceOrigen, int indiceDestino) {
        ImagenMovil elemento = elementos[indiceOrigen];
        double destinoX = obtenerPosicionXParaIndice(indiceDestino);
        crearAnimacionSimple(elemento, destinoX, elemento.y);
    }

     // Inicia la animación para que una imagen "flotante" baje y se inserte en la fila.

    public void animarInsercion(ImagenMovil elemento, int indiceDestino) {
        double destinoX = obtenerPosicionXParaIndice(indiceDestino);
        double destinoY = (getHeight() - ALTO_IMAGEN) / 2 + ALTURA_ELEVACION / 2;
        crearAnimacionSimple(elemento, destinoX, destinoY);
    }

     //Este es el motor de la animación. Un Timer actualiza la posición de la imagen
     // en pequeños pasos hasta que llega a su destino.

    private void crearAnimacionSimple(ImagenMovil elemento, double destinoX, double destinoY) {
        Timer timer = new Timer(10, null);
        timer.addActionListener(e -> {
            // Calcula la distancia que falta por recorrer.
            double dx = destinoX - elemento.x;
            double dy = destinoY - elemento.y;

            // Mueve la imagen un pequeño porcentaje de la distancia restante.
            elemento.x += dx * 0.15;
            elemento.y += dy * 0.15;

            // Si ya está muy cerca, la coloca en el punto exacto y detiene la animación.
            if (Math.abs(dx) < 1.0 && Math.abs(dy) < 1.0) {
                elemento.x = destinoX;
                elemento.y = destinoY;
                timer.stop();
                // Avisa al hilo de ordenamiento que puede continuar.
                synchronized (candadoAnimacion) {
                    candadoAnimacion.notifyAll();
                }
            }
            repaint(); // Vuelve a dibujar el panel en la nueva posición.
        });
        timer.start();
    }

    // --- Métodos de ayuda para comunicar con otras clases ---
    public boolean imagenCargada() { return imagenFuente != null; }
    public ImagenMovil[] obtenerElementos() { return elementos; }
    public Object obtenerCandadoAnimacion() { return candadoAnimacion; }
    public void establecerElementoActual(ImagenMovil elemento) { this.elementoActual = elemento; }
    public void establecerIndiceDeComparacion(int i) { this.indiceDeComparacion = i; }

     // Calcula la coordenada X horizontal para una posición (índice) en la fila.

    private double obtenerPosicionXParaIndice(int indice) {
        int posicionXInicial = (getWidth() - (cantidadDeElementos * anchoImagen + (cantidadDeElementos - 1) * ESPACIO_ENTRE_IMAGENES)) / 2;
        return posicionXInicial + indice * (anchoImagen + ESPACIO_ENTRE_IMAGENES);
    }

     //Este es el método más importante para dibujar. Se ejecuta cada vez
     // que se llama a repaint().

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (elementos == null || !imagenCargada()) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibuja las imágenes que están en la fila (en el arreglo).
        for (ImagenMovil elemento : elementos) {
            if (elemento == null) continue; // No dibuja el "agujero".
            dibujarImagenMovil(g2d, elemento);
        }

        // Dibuja la imagen que está "flotando" encima de las demás.
        if (elementoActual != null) {
            dibujarImagenMovil(g2d, elementoActual);
        }
    }

     // Un método de ayuda para dibujar una sola imagen con su número.

    private void dibujarImagenMovil(Graphics2D g2d, ImagenMovil elemento) {
        g2d.drawImage(elemento.imagen, (int) elemento.x, (int) elemento.y, anchoImagen, ALTO_IMAGEN, null);

        // Dibuja el número con una sombra para que se lea mejor.
        String valor = String.valueOf(elemento.valor);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics medidasLetra = g2d.getFontMetrics();
        int textoX = (int) elemento.x + (anchoImagen - medidasLetra.stringWidth(valor)) / 2;
        int textoY = (int) elemento.y + (ALTO_IMAGEN - medidasLetra.getHeight()) / 2 + medidasLetra.getAscent();

        g2d.setColor(COLOR_SOMBRA_TEXTO);
        g2d.drawString(valor, textoX + 2, textoY + 2);
        g2d.setColor(COLOR_TEXTO);
        g2d.drawString(valor, textoX, textoY);
    }
}