import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Panel principal que visualiza el algoritmo QuickSort con tema de cocina profesional
 * Diseño mejorado para parecer una cocina real con estantes y cajas de comida
 */
public class QuickSortVista extends JPanel {
    // Array de cajas de comida a ordenar
    private CajadeComida[] cajas;

    // Contadores para estadísticas del algoritmo
    private int comparaciones = 0, movimientos = 0;

    // Componentes de la interfaz de usuario
    private JButton mezclarBtn, resolverBtn, terminarBtn, pausarBtn;
    private JComboBox<Integer> cantidadSelector;
    private JSlider velocidadSlider;
    private JLabel comparacionesLabel, movimientosLabel, estadoLabel;

    // Variables de control del algoritmo
    private boolean ordenando = false;
    private boolean pausado = false;

    // Variables para la visualización del algoritmo
    private int pivotIndex = -1;
    private int comparandoI = -1, comparandoJ = -1;

    private Font fuentePersonalizada, fuenteTitulo;

    /**
     * Constructor que inicializa el panel visualizador con tema de cocina mejorado
     */
    public QuickSortVista() {
        setLayout(null);
        setPreferredSize(new Dimension(1200, 800));

        // Inicializar fuentes personalizadas
        try {
            fuentePersonalizada = new Font("Georgia", Font.BOLD, 12);
            fuenteTitulo = new Font("Georgia", Font.BOLD, 16);
        } catch (Exception e) {
            fuentePersonalizada = new Font(Font.SERIF, Font.BOLD, 12);
            fuenteTitulo = new Font(Font.SERIF, Font.BOLD, 16);
        }

        cajas = generarCajas(10);
        inicializarComponentes();
    }

    /**
     * Inicializa todos los componentes con diseño de cocina profesional
     */
    private void inicializarComponentes() {
        // ===== PANEL DE CONTROL DE COCINA (Estilo tablero de chef) =====
        JPanel panelControl = new JPanel();
        panelControl.setBounds(40, 30, 320, 240);
        panelControl.setBackground(new Color(139, 69, 19, 240)); // Madera oscura
        panelControl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panelControl.setLayout(null);
        add(panelControl);

        // Título del panel con estilo elegante
        JLabel titulo = new JLabel("ESTACION DE ORDENAMIENTO");
        titulo.setBounds(10, 5, 300, 25);
        titulo.setFont(fuenteTitulo);
        titulo.setForeground(new Color(255, 248, 220));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelControl.add(titulo);

        // ===== ESTADÍSTICAS DE COCINA =====
        comparacionesLabel = new JLabel(">> Comparaciones: 0");
        comparacionesLabel.setBounds(15, 35, 200, 20);
        comparacionesLabel.setFont(fuentePersonalizada);
        comparacionesLabel.setForeground(Color.WHITE);
        panelControl.add(comparacionesLabel);

        movimientosLabel = new JLabel(">> Movimientos: 0");
        movimientosLabel.setBounds(15, 60, 200, 20);
        movimientosLabel.setFont(fuentePersonalizada);
        movimientosLabel.setForeground(Color.WHITE);
        panelControl.add(movimientosLabel);

        estadoLabel = new JLabel("* Estado: Listo para cocinar");
        estadoLabel.setBounds(15, 85, 250, 20);
        estadoLabel.setFont(fuentePersonalizada);
        estadoLabel.setForeground(new Color(144, 238, 144));
        panelControl.add(estadoLabel);

        // ===== CONTROLES DE CONFIGURACIÓN =====
        JLabel cantidadLabel = new JLabel("+ Cajas de ingredientes:");
        cantidadLabel.setBounds(15, 115, 180, 20);
        cantidadLabel.setFont(fuentePersonalizada);
        cantidadLabel.setForeground(Color.WHITE);
        panelControl.add(cantidadLabel);

        cantidadSelector = new JComboBox<>();
        for (int i = 5; i <= 20; i++) cantidadSelector.addItem(i);
        cantidadSelector.setSelectedItem(10);
        cantidadSelector.setBounds(200, 115, 80, 25);
        cantidadSelector.setBackground(new Color(245, 222, 179));
        panelControl.add(cantidadSelector);

        JLabel velocidadLabel = new JLabel("~ Velocidad de coccion:");
        velocidadLabel.setBounds(15, 150, 180, 20);
        velocidadLabel.setFont(fuentePersonalizada);
        velocidadLabel.setForeground(Color.WHITE);
        panelControl.add(velocidadLabel);

        velocidadSlider = new JSlider(1, 10, 5);
        velocidadSlider.setBounds(15, 175, 200, 25);
        velocidadSlider.setOpaque(false);
        panelControl.add(velocidadSlider);

        // ===== BOTONES DE CONTROL CON ESTILO COCINA =====
        mezclarBtn = crearBotonCocina("~ Mezclar", 40, 290, new Color(101, 67, 33));
        mezclarBtn.addActionListener(e -> {
            if (!ordenando) {
                cajas = generarCajas((int) cantidadSelector.getSelectedItem());
                reiniciarContadores();
                repaint();
            }
        });

        resolverBtn = crearBotonCocina("* Ordenar", 170, 290, new Color(34, 139, 34));
        resolverBtn.addActionListener(e -> {
            if (!ordenando && !pausado) {
                iniciarOrdenamiento();
            }
        });

        pausarBtn = crearBotonCocina("|| Pausar", 300, 290, new Color(255, 140, 0));
        pausarBtn.addActionListener(e -> {
            pausado = !pausado;
            if (pausado) {
                pausarBtn.setText("> Continuar");
                pausarBtn.setBackground(new Color(34, 139, 34));
                estadoLabel.setText("|| Estado: Coccion pausada");
                estadoLabel.setForeground(Color.YELLOW);
            } else {
                pausarBtn.setText("|| Pausar");
                pausarBtn.setBackground(new Color(255, 140, 0));
                estadoLabel.setText("* Estado: Cocinando...");
                estadoLabel.setForeground(Color.ORANGE);
            }
        });
        pausarBtn.setEnabled(false);

        terminarBtn = crearBotonCocina("X Salir", 430, 290, new Color(178, 34, 34));
        terminarBtn.addActionListener(e -> System.exit(0));
    }

    /**
     * Crea botones con estilo de cocina profesional
     */
    private JButton crearBotonCocina(String texto, int x, int y, Color color) {
        JButton boton = new JButton(texto);
        boton.setBounds(x, y, 120, 40);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Arial", Font.BOLD, 11));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Efecto hover
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(color.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(color);
            }
        });

        add(boton);
        return boton;
    }

    /**
     * Genera cajas con pesos únicos aleatorios
     */
    private CajadeComida[] generarCajas(int cantidad) {
        CajadeComida[] nuevo = new CajadeComida[cantidad];
        Random rand = new Random();
        Set<Integer> pesosUsados = new HashSet<>();

        for (int i = 0; i < cantidad; i++) {
            int peso;
            do {
                peso = rand.nextInt(100) + 1;
            } while (pesosUsados.contains(peso));
            pesosUsados.add(peso);
            nuevo[i] = new CajadeComida(peso);
        }
        return nuevo;
    }

    /**
     * Inicia el proceso de ordenamiento
     */
    private void iniciarOrdenamiento() {
        ordenando = true;
        pausarBtn.setEnabled(true);
        resolverBtn.setEnabled(false);
        estadoLabel.setText("* Estado: Cocinando...");
        estadoLabel.setForeground(Color.ORANGE);

        new Thread(() -> {
            quickSort(0, cajas.length - 1);
            SwingUtilities.invokeLater(() -> {
                ordenando = false;
                pausado = false;
                pausarBtn.setEnabled(false);
                pausarBtn.setText("|| Pausar");
                pausarBtn.setBackground(new Color(255, 140, 0));
                resolverBtn.setEnabled(true);
                estadoLabel.setText("+ Estado: Platillo listo!");
                estadoLabel.setForeground(new Color(50, 205, 50));
                limpiarSelecciones();
                repaint();
            });
        }).start();
    }

    // [Métodos del algoritmo QuickSort - sin cambios]
    private void quickSort(int low, int high) {
        if (low < high && !pausado) {
            int pi = partition(low, high);
            if (!pausado) quickSort(low, pi - 1);
            if (!pausado) quickSort(pi + 1, high);
        }
    }

    private int partition(int low, int high) {
        pivotIndex = high;
        int pivot = cajas[high].peso;
        cajas[high].esPivot = true;
        int i = low - 1;

        for (int j = low; j < high && !pausado; j++) {
            while (pausado) {
                try { Thread.sleep(100); } catch (InterruptedException e) {}
            }

            comparandoI = i + 1;
            comparandoJ = j;
            comparaciones++;

            SwingUtilities.invokeLater(() -> {
                actualizarContadores();
                repaint();
            });
            dormir();

            if (cajas[j].peso < pivot) {
                i++;
                swap(i, j);
                SwingUtilities.invokeLater(() -> repaint());
                dormir();
            }
        }

        if (!pausado) {
            swap(i + 1, high);
            SwingUtilities.invokeLater(() -> repaint());
            dormir();
        }

        cajas[high].esPivot = false;
        limpiarSelecciones();
        return i + 1;
    }

    private void swap(int i, int j) {
        CajadeComida temp = cajas[i];
        cajas[i] = cajas[j];
        cajas[j] = temp;
        movimientos++;
    }

    private void limpiarSelecciones() {
        pivotIndex = -1;
        comparandoI = -1;
        comparandoJ = -1;
        for (CajadeComida caja : cajas) {
            caja.esPivot = false;
            caja.esSeleccionado = false;
        }
    }

    private void actualizarContadores() {
        comparacionesLabel.setText(">> Comparaciones: " + comparaciones);
        movimientosLabel.setText(">> Movimientos: " + movimientos);
    }

    private void reiniciarContadores() {
        comparaciones = 0;
        movimientos = 0;
        actualizarContadores();
        estadoLabel.setText("* Estado: Listo para cocinar");
        estadoLabel.setForeground(new Color(144, 238, 144));
        limpiarSelecciones();
    }

    private void dormir() {
        try {
            int velocidad = velocidadSlider.getValue();
            Thread.sleep(1100 - (velocidad * 100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método principal de dibujo con tema de cocina profesional
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        dibujarCocinaCompleta(g2);
        dibujarEstantesProfesionales(g2);
        dibujarCajasIngredientes(g2);
        dibujarMenuRecetas(g2);
    }

    /**
     * Dibuja una cocina profesional completa
     */
    private void dibujarCocinaCompleta(Graphics2D g2) {
        // ===== FONDO DE COCINA PROFESIONAL =====
        GradientPaint fondoCocina = new GradientPaint(
                0, 0, new Color(245, 245, 220),
                0, getHeight(), new Color(210, 180, 140)
        );
        g2.setPaint(fondoCocina);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // ===== AZULEJOS DE PARED =====
        g2.setColor(new Color(248, 248, 255, 100));
        for (int i = 0; i < getWidth(); i += 60) {
            for (int j = 0; j < 400; j += 60) {
                g2.fillRect(i, j, 55, 55);
                g2.setColor(new Color(220, 220, 220, 50));
                g2.drawRect(i, j, 55, 55);
                g2.setColor(new Color(248, 248, 255, 100));
            }
        }

        // ===== SUELO DE COCINA =====
        g2.setColor(new Color(139, 119, 101));
        g2.fillRect(0, getHeight() - 100, getWidth(), 100);

        // Líneas del suelo
        g2.setColor(new Color(160, 140, 122));
        for (int i = 0; i < getWidth(); i += 80) {
            g2.drawLine(i, getHeight() - 100, i, getHeight());
        }

        // ===== VENTANA CON VISTA =====
        g2.setColor(new Color(135, 206, 250, 150));
        g2.fillRoundRect(50, 50, 200, 120, 15, 15);
        g2.setColor(new Color(101, 67, 33));
        g2.setStroke(new BasicStroke(4));
        g2.drawRoundRect(50, 50, 200, 120, 15, 15);

        // Marco cruzado de ventana
        g2.drawLine(150, 50, 150, 170);
        g2.drawLine(50, 110, 250, 110);

        // "Vista" exterior - arbustos con caracteres
        g2.setColor(new Color(34, 139, 34, 80));
        g2.fillOval(180, 60, 30, 30);
        g2.fillOval(200, 70, 25, 25);

        // Texto decorativo en la ventana
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        g2.drawString("VISTA", 145, 115);

        // ===== CAMPANA EXTRACTORA =====
        g2.setColor(new Color(192, 192, 192));
        g2.fillRoundRect(getWidth() - 250, 40, 180, 30, 10, 10);
        g2.setColor(new Color(169, 169, 169));
        g2.fillRoundRect(getWidth() - 240, 70, 160, 15, 8, 8);

        // Rejillas de ventilación
        g2.setColor(new Color(105, 105, 105));
        for (int i = 0; i < 8; i++) {
            g2.fillRect(getWidth() - 220 + (i * 18), 75, 10, 5);
        }
    }

    /**
     * Dibuja estantes de cocina profesional para las cajas
     */
    private void dibujarEstantesProfesionales(Graphics2D g2) {
        if (cajas == null || cajas.length == 0) return;

        int anchoEstante = Math.max(600, cajas.length * 80);
        int xEstante = (getWidth() - anchoEstante) / 2;

        // ===== ESTANTE PRINCIPAL DE MADERA =====
        g2.setColor(new Color(160, 82, 45));
        g2.fillRoundRect(xEstante - 20, 580, anchoEstante + 40, 25, 12, 12);

        // Sombra del estante
        g2.setColor(new Color(0, 0, 0, 40));
        g2.fillRoundRect(xEstante - 15, 585, anchoEstante + 40, 25, 12, 12);

        // Vetas de la madera
        g2.setColor(new Color(139, 69, 19));
        for (int i = 0; i < 5; i++) {
            g2.drawLine(xEstante + (i * 120), 582, xEstante + (i * 120) + 80, 587);
        }

        // ===== SOPORTES DEL ESTANTE =====
        g2.setColor(new Color(105, 105, 105));
        g2.fillRoundRect(xEstante - 10, 570, 15, 35, 8, 8);
        g2.fillRoundRect(xEstante + anchoEstante, 570, 15, 35, 8, 8);

        // ===== ETIQUETAS EN EL ESTANTE =====
        g2.setColor(new Color(255, 248, 220));
        g2.setFont(new Font("Georgia", Font.ITALIC, 10));
        g2.drawString("ESTANTE DE INGREDIENTES ORDENADOS", xEstante + 50, 598);
    }

    /**
     * Dibuja las cajas como contenedores de ingredientes reales
     */
    private void dibujarCajasIngredientes(Graphics2D g2) {
        if (cajas == null || cajas.length == 0) return;

        int anchoPorCaja = Math.min(75, Math.max(40, (getWidth() - 200) / cajas.length));
        int espaciado = Math.max(5, (anchoPorCaja - 40) / 2);
        int xInicio = (getWidth() - (cajas.length * (anchoPorCaja + espaciado))) / 2;
        int yBase = 580;

        for (int i = 0; i < cajas.length; i++) {
            CajadeComida caja = cajas[i];
            int altura = Math.max(40, Math.min(120, caja.peso + 30));
            int x = xInicio + i * (anchoPorCaja + espaciado);
            int y = yBase - altura;

            // ===== SOMBRA REALISTA =====
            g2.setColor(new Color(0, 0, 0, 25));
            g2.fillRoundRect(x + 4, y + 4, anchoPorCaja, altura, 15, 15);

            // ===== CONTENEDOR BASE =====
            Color colorBase = obtenerColorIngrediente(caja, i);
            g2.setColor(colorBase);
            g2.fillRoundRect(x, y, anchoPorCaja, altura, 15, 15);

            // ===== EFECTO 3D Y TEXTURAS =====
            // Luz superior
            g2.setColor(colorBase.brighter().brighter());
            g2.fillRoundRect(x + 2, y + 2, anchoPorCaja - 4, 8, 10, 10);

            // Sombra interna
            g2.setColor(colorBase.darker());
            g2.drawRoundRect(x + 1, y + 1, anchoPorCaja - 2, altura - 2, 13, 13);

            // ===== ETIQUETA DE INGREDIENTE =====
            dibujarEtiquetaIngrediente(g2, caja, x, y, anchoPorCaja, altura);

            // ===== INDICADORES DEL ALGORITMO =====
            if (caja.esPivot) {
                // Corona ASCII para el pivot
                g2.setColor(new Color(255, 215, 0));
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                g2.drawString("^^^", x + anchoPorCaja/2 - 15, y - 10);

                g2.setColor(Color.RED);
                g2.setFont(new Font("Arial", Font.BOLD, 10));
                g2.drawString("CHEF", x + anchoPorCaja/2 - 15, y - 20);
            }

            if (i == comparandoI || i == comparandoJ) {
                // Marcos de fuego con caracteres
                g2.setColor(new Color(255, 69, 0));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(x - 2, y - 2, anchoPorCaja + 4, altura + 4, 15, 15);

                // Caracteres de fuego alrededor
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString("*", x - 10, y + altura/2);
                g2.drawString("*", x + anchoPorCaja + 5, y + altura/2);
            }
        }
    }

    /**
     * Dibuja etiquetas realistas en los contenedores
     */
    private void dibujarEtiquetaIngrediente(Graphics2D g2, CajadeComida caja, int x, int y, int ancho, int altura) {
        // Fondo de etiqueta
        g2.setColor(new Color(255, 255, 240, 220));
        g2.fillRoundRect(x + 5, y + altura - 35, ancho - 10, 30, 8, 8);

        // Borde de etiqueta
        g2.setColor(new Color(139, 69, 19));
        g2.drawRoundRect(x + 5, y + altura - 35, ancho - 10, 30, 8, 8);

        // Icono del ingrediente (más grande y detallado)
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        String icono = getIconoMejorado(caja);
        FontMetrics fm = g2.getFontMetrics();
        int iconoX = x + (ancho - fm.stringWidth(icono)) / 2;
        g2.setColor(obtenerColorIcono(caja));
        g2.drawString(icono, iconoX, y + altura - 20);

        // Peso con estilo elegante
        g2.setFont(new Font("Georgia", Font.BOLD, 11));
        g2.setColor(Color.BLACK);
        String peso = caja.peso + "kg";
        int pesoX = x + (ancho - g2.getFontMetrics().stringWidth(peso)) / 2;
        g2.drawString(peso, pesoX, y + altura - 8);
    }

    /**
     * Obtiene iconos ASCII mejorados para cada tipo de ingrediente
     */
    private String getIconoMejorado(CajadeComida caja) {
        switch (caja.getTipoComida()) {
            case "Snack": return "S";
            case "Fruta": return "F";
            case "Sandwich": return "W";
            case "Pizza": return "P";
            case "Hamburguesa": return "H";
            default: return "?";
        }
    }

    /**
     * Colores más realistas para ingredientes
     */
    private Color obtenerColorIngrediente(CajadeComida caja, int indice) {
        if (caja.esPivot) {
            return new Color(220, 20, 60, 200); // Rojo elegante para pivot
        } else if (indice == comparandoI || indice == comparandoJ) {
            return new Color(30, 144, 255, 200); // Azul brillante para comparación
        } else {
            switch (caja.getTipoComida()) {
                case "Snack": return new Color(210, 180, 140);     // Beige natural
                case "Fruta": return new Color(255, 160, 122);     // Salmón claro
                case "Sandwich": return new Color(244, 164, 96);   // Naranja arena
                case "Pizza": return new Color(255, 140, 0);       // Naranja dorado
                case "Hamburguesa": return new Color(139, 69, 19); // Marrón chocolate
                default: return new Color(210, 180, 140);
            }
        }
    }

    private Color obtenerColorIcono(CajadeComida caja) {
        switch (caja.getTipoComida()) {
            case "Snack": return new Color(101, 67, 33);
            case "Fruta": return new Color(178, 34, 34);
            case "Sandwich": return new Color(184, 134, 11);
            case "Pizza": return new Color(255, 69, 0);
            case "Hamburguesa": return new Color(139, 69, 19);
            default: return Color.BLACK;
        }
    }

    /**
     * Dibuja un menú de recetas como leyenda
     */
    private void dibujarMenuRecetas(Graphics2D g2) {
        // ===== MENÚ EN PERGAMINO =====
        g2.setColor(new Color(245, 245, 220, 240));
        g2.fillRoundRect(getWidth() - 380, 80, 320, 180, 15, 15);

        // Sombra del menú
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillRoundRect(getWidth() - 375, 85, 320, 180, 15, 15);

        // Borde decorativo
        g2.setColor(new Color(139, 69, 19));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(getWidth() - 380, 80, 320, 180, 15, 15);

        // ===== TÍTULO DEL MENÚ =====
        g2.setFont(new Font("Georgia", Font.BOLD, 16));
        g2.setColor(new Color(139, 69, 19));
        g2.drawString("MENU DE INGREDIENTES", getWidth() - 370, 105);

        // ===== LISTA DE INGREDIENTES =====
        g2.setFont(new Font("Georgia", Font.PLAIN, 12));
        g2.setColor(Color.BLACK);
        String[] recetas = {
                "S = Snacks Ligeros (1-20kg)",
                "F = Frutas Frescas (21-40kg)",
                "W = Sandwiches Gourmet (41-60kg)",
                "P = Pizzas Artesanales (61-80kg)",
                "H = Hamburguesas Premium (81-100kg)"
        };

        for (int i = 0; i < recetas.length; i++) {
            g2.drawString(recetas[i], getWidth() - 365, 130 + i * 22);
        }

        // ===== INDICADORES DE ESTADO =====
        g2.setFont(new Font("Georgia", Font.BOLD, 11));
        g2.setColor(new Color(178, 34, 34));
        g2.drawString("Dorado = Ingrediente Principal", getWidth() - 365, 250);

        g2.setColor(new Color(30, 144, 255));
        g2.drawString("Azul = Comparando Sabores", getWidth() - 365, 235);
    }
}