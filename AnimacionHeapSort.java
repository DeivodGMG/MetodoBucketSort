import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

// Clase principal que crea la animación del algoritmo HeapSort
public class AnimacionHeapSort extends JFrame {

    // Declaro todas las variables que voy a necesitar para la interfaz
    private JTextField campoComparaciones; // Campo donde muestro el número de comparaciones realizadas
    private JTextField campoMovimientos; // Campo donde muestro el número de movimientos realizados
    private JComboBox<Integer> selectorCantidadPesas; // ComboBox para seleccionar cuántas pesas quiero ordenar
    private JButton botonMezclarPesas; // Botón para generar nuevas pesas aleatorias
    private JButton botonResolverOrdenamiento; // Botón para iniciar la animación del HeapSort
    private JButton botonTerminarAnimacion; // Botón para detener la animación en cualquier momento
    private PanelVisualizacionPesas panelDondeDibujo; // Panel personalizado donde dibujo las pesas

    // Variables para mostrar el progreso de la animación
    private JLabel etiquetaFaseActual; // Etiqueta que muestra en qué fase del algoritmo estamos
    private JProgressBar barraProgresoAnimacion; // Barra que muestra el progreso del ordenamiento

    // Variables para el manejo de datos y algoritmo
    private int[] arregloPesas; // Arreglo que contiene los pesos de todas las pesas
    private int cantidadTotalPesas; // Número total de pesas que voy a ordenar
    private int contadorComparaciones; // Contador de cuántas comparaciones he hecho
    private int contadorMovimientos; // Contador de cuántos intercambios he realizado
    private boolean animacionEjecutandose; // Flag para saber si la animación está corriendo
    private Timer temporizadorAnimacion; // Timer que controla la velocidad de la animación

    // Variables para efectos visuales durante la animación
    private int indicePesaSeleccionada1 = -1; // Índice de la primera pesa que estoy comparando
    private int indicePesaSeleccionada2 = -1; // Índice de la segunda pesa que estoy comparando
    private boolean mostrandoIntercambio = false; // Flag para mostrar efectos de intercambio

    // Variables para el control del progreso
    private String nombreFaseActual = "Esperando..."; // Nombre de la fase actual del algoritmo
    private int numeropasoActual = 0; // Paso actual en el que estoy
    private int numeroTotalPasos = 0; // Total de pasos que va a tener el algoritmo

    // Defino todos los colores que voy a usar en la interfaz
    private final Color COLOR_FONDO_PRINCIPAL = new Color(240, 242, 247); // Color de fondo de toda la ventana
    private final Color COLOR_PANELES_BLANCOS = new Color(255, 255, 255); // Color de fondo de los paneles
    private final Color COLOR_SOMBRAS_PANELES = new Color(226, 232, 240); // Color para las sombras de los paneles
    private final Color COLOR_BASE_PESAS = new Color(71, 85, 105); // Color base de las pesas
    private final Color COLOR_DISCO_PESAS = new Color(100, 116, 139); // Color de los discos de las pesas

    // Colores para los efectos visuales durante la animación
    private final Color COLOR_PESA_SELECCIONADA = new Color(255, 20, 20); // Color rojo para la pesa activa
    private final Color COLOR_PESA_COMPARANDO = new Color(20, 100, 255); // Color azul para la pesa que se compara
    private final Color COLOR_BRILLO_DORADO = new Color(255, 215, 0); // Color dorado para efectos especiales

    // Colores para textos y elementos de la interfaz
    private final Color COLOR_TEXTO_PRINCIPAL = new Color(30, 41, 59); // Color del texto principal
    private final Color COLOR_TEXTO_SECUNDARIO = new Color(100, 116, 139); // Color del texto secundario
    private final Color COLOR_ACENTO_VERDE = new Color(16, 185, 129); // Color verde para botones importantes
    private final Color COLOR_ADVERTENCIA_AMARILLO = new Color(245, 158, 11); // Color amarillo para advertencias
    private final Color COLOR_PELIGRO_ROJO = new Color(239, 68, 68); // Color rojo para acciones peligrosas

    // Constructor de la clase principal
    public AnimacionHeapSort() {
        configurarVentanaPrincipal(); // Configuro las propiedades básicas de la ventana
        crearTodaLaInterfaz(); // Creo todos los componentes visuales
        inicializarDatos(); // Inicializo los datos y valores por defecto
    }

    // Método para configurar las propiedades básicas de la ventana
    private void configurarVentanaPrincipal() {
        setTitle("Animación de Ordenamiento Heap Sort"); // Establezco el título de la ventana
        setSize(1000, 800); // Defino el tamaño de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Configuro que se cierre al hacer clic en X
        setLocationRelativeTo(null); // Centro la ventana en la pantalla
        getContentPane().setBackground(COLOR_FONDO_PRINCIPAL); // Establezco el color de fondo
        setLayout(new BorderLayout()); // Uso BorderLayout como diseño principal
    }

    // Método principal para crear toda la interfaz gráfica
    private void crearTodaLaInterfaz() {
        JPanel panelSuperior = crearPanelSuperiorCompleto(); // Creo el panel de arriba con controles
        add(panelSuperior, BorderLayout.NORTH); // Lo agrego en la parte superior

        JPanel panelInferior = crearPanelProgresoInferior(); // Creo el panel de abajo con el progreso
        add(panelInferior, BorderLayout.SOUTH); // Lo agrego en la parte inferior

        // Creo el panel donde se van a dibujar las pesas
        panelDondeDibujo = new PanelVisualizacionPesas();
        panelDondeDibujo.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(20, 20, 10, 20), // Margen exterior
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_SOMBRAS_PANELES, 1), // Borde con color
                        new EmptyBorder(20, 20, 20, 20) // Margen interior
                )
        ));
        add(panelDondeDibujo, BorderLayout.CENTER); // Lo agrego en el centro de la ventana
    }

    // Método para crear el panel inferior que muestra el progreso
    private JPanel crearPanelProgresoInferior() {
        JPanel panelPrincipal = new JPanel(new BorderLayout()); // Uso BorderLayout para organizar
        panelPrincipal.setBackground(COLOR_PANELES_BLANCOS); // Establezco el color de fondo
        panelPrincipal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_SOMBRAS_PANELES), // Borde superior
                new EmptyBorder(20, 30, 20, 30) // Márgenes internos
        ));

        // Creo el título del panel
        JLabel tituloSeccion = new JLabel("Estado del Programa");
        tituloSeccion.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Fuente grande y negrita
        tituloSeccion.setForeground(COLOR_TEXTO_PRINCIPAL); // Color del texto
        panelPrincipal.add(tituloSeccion, BorderLayout.NORTH); // Lo pongo arriba

        // Creo el panel central con la información del progreso
        JPanel panelCentral = new JPanel(new BorderLayout(20, 10));
        panelCentral.setBackground(COLOR_PANELES_BLANCOS);
        panelCentral.setBorder(new EmptyBorder(10, 0, 0, 0)); // Margen superior

        // Creo la etiqueta que muestra la fase actual
        etiquetaFaseActual = new JLabel("Esperando...");
        etiquetaFaseActual.setFont(new Font("Segoe UI", Font.BOLD, 14));
        etiquetaFaseActual.setForeground(COLOR_ACENTO_VERDE); // Color verde para destacar
        etiquetaFaseActual.setHorizontalAlignment(JLabel.CENTER); // Centro el texto
        panelCentral.add(etiquetaFaseActual, BorderLayout.CENTER);

        // Creo la barra de progreso
        barraProgresoAnimacion = new JProgressBar(0, 100);
        barraProgresoAnimacion.setStringPainted(true); // Muestro el porcentaje como texto
        barraProgresoAnimacion.setString("0%"); // Texto inicial
        barraProgresoAnimacion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        barraProgresoAnimacion.setForeground(Color.GREEN); // Color verde para el progreso
        barraProgresoAnimacion.setBackground(COLOR_SOMBRAS_PANELES);
        barraProgresoAnimacion.setBorder(BorderFactory.createLineBorder(COLOR_SOMBRAS_PANELES, 1));
        barraProgresoAnimacion.setPreferredSize(new Dimension(0, 25)); // Altura fija
        panelCentral.add(barraProgresoAnimacion, BorderLayout.SOUTH);

        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        return panelPrincipal;
    }

    // Método para crear todo el panel superior con los controles
    private JPanel crearPanelSuperiorCompleto() {
        JPanel panelPrincipal = new JPanel(new BorderLayout()); // Layout principal
        panelPrincipal.setBackground(COLOR_PANELES_BLANCOS);
        panelPrincipal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_SOMBRAS_PANELES), // Borde inferior
                new EmptyBorder(25, 30, 25, 30) // Márgenes
        ));

        // Creo cada sección del panel superior
        JPanel panelEstadisticas = crearPanelContadores(); // Panel con los contadores
        panelPrincipal.add(panelEstadisticas, BorderLayout.WEST); // Lo pongo a la izquierda

        JPanel panelSelector = crearPanelSelectorCantidad(); // Panel con el selector de cantidad
        panelPrincipal.add(panelSelector, BorderLayout.CENTER); // Lo pongo en el centro

        JPanel panelBotones = crearPanelBotonesAccion(); // Panel con todos los botones
        panelPrincipal.add(panelBotones, BorderLayout.EAST); // Lo pongo a la derecha

        return panelPrincipal;
    }

    // Método para crear el panel que muestra las estadísticas
    private JPanel crearPanelContadores() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 8)); // Uso Grid para organizar en cuadrícula
        panel.setBackground(COLOR_PANELES_BLANCOS);

        // Creo la etiqueta para las comparaciones
        JLabel etiquetaComparaciones = new JLabel("Comparaciones");
        etiquetaComparaciones.setForeground(COLOR_TEXTO_SECUNDARIO);
        etiquetaComparaciones.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(etiquetaComparaciones);

        // Creo el campo que muestra el contador de comparaciones
        campoComparaciones = crearCampoContadorModerno("0", COLOR_ACENTO_VERDE);
        panel.add(campoComparaciones);

        // Creo la etiqueta para los movimientos
        JLabel etiquetaMovimientos = new JLabel("Movimientos");
        etiquetaMovimientos.setForeground(COLOR_TEXTO_SECUNDARIO);
        etiquetaMovimientos.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(etiquetaMovimientos);

        // Creo el campo que muestra el contador de movimientos
        campoMovimientos = crearCampoContadorModerno("0", COLOR_ADVERTENCIA_AMARILLO);
        panel.add(campoMovimientos);

        return panel;
    }

    // Método para crear el panel con el selector de cantidad de pesas
    private JPanel crearPanelSelectorCantidad() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Uso FlowLayout para centrar
        panel.setBackground(COLOR_PANELES_BLANCOS);

        // Creo la etiqueta explicativa
        JLabel etiquetaExplicativa = new JLabel("Número de pesas:");
        etiquetaExplicativa.setForeground(COLOR_TEXTO_PRINCIPAL);
        etiquetaExplicativa.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(etiquetaExplicativa);

        // Creo el selector de cantidad
        selectorCantidadPesas = crearSelectorCantidadModerno();
        panel.add(selectorCantidadPesas);

        return panel;
    }

    // Método para crear el panel con todos los botones de acción
    private JPanel crearPanelBotonesAccion() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0)); // Alineación a la derecha
        panel.setBackground(COLOR_PANELES_BLANCOS);

        // Creo todos los botones con sus respectivos colores
        botonMezclarPesas = crearBotonModerno("Mezclar", COLOR_ACENTO_VERDE);
        botonResolverOrdenamiento = crearBotonModerno("Resolver", COLOR_ADVERTENCIA_AMARILLO);
        botonTerminarAnimacion = crearBotonModerno("Terminar", COLOR_PELIGRO_ROJO);

        // Los agrego al panel
        panel.add(botonMezclarPesas);
        panel.add(botonResolverOrdenamiento);
        panel.add(botonTerminarAnimacion);

        return panel;
    }

    // Método para crear campos de texto que muestran contadores con estilo moderno
    private JTextField crearCampoContadorModerno(String valorInicial, Color colorTema) {
        JTextField campoTexto = new JTextField(valorInicial);
        campoTexto.setEditable(false); // No permito que el usuario escriba
        campoTexto.setHorizontalAlignment(JTextField.CENTER); // Centro el texto
        campoTexto.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Fuente grande y negrita
        campoTexto.setForeground(colorTema); // Color del texto según el tema
        campoTexto.setBackground(COLOR_PANELES_BLANCOS);
        campoTexto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorTema, 2, true), // Borde redondeado con el color del tema
                new EmptyBorder(10, 15, 10, 15) // Padding interno
        ));
        campoTexto.setPreferredSize(new Dimension(90, 45)); // Tamaño fijo
        return campoTexto;
    }

    // Método para crear el ComboBox selector de cantidad con estilo moderno
    private JComboBox<Integer> crearSelectorCantidadModerno() {
        Integer[] opcionesCantidad = {5, 6, 7, 8, 9, 10, 12, 15, 18, 20}; // Opciones disponibles
        JComboBox<Integer> selector = new JComboBox<>(opcionesCantidad);
        selector.setSelectedItem(10); // Selecciono 10 como valor por defecto
        selector.setFont(new Font("Segoe UI", Font.BOLD, 14));
        selector.setBackground(COLOR_PANELES_BLANCOS);
        selector.setForeground(COLOR_TEXTO_PRINCIPAL);
        selector.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_TEXTO_SECUNDARIO, 2, true), // Borde redondeado
                new EmptyBorder(8, 12, 8, 12) // Padding interno
        ));
        selector.setPreferredSize(new Dimension(100, 40)); // Tamaño fijo
        return selector;
    }

    // Método para crear botones con estilo moderno y efectos hover
    private JButton crearBotonModerno(String textoBoton, Color colorTema) {
        JButton boton = new JButton(textoBoton);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Configuración inicial del botón
        boton.setForeground(Color.WHITE);
        boton.setBackground(colorTema);
        boton.setOpaque(true);
        boton.setBorderPainted(true);
        boton.setContentAreaFilled(true);
        boton.setFocusPainted(false); // Quito el foco visual feo
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Cambio el cursor a manita

        // Establezco el borde del botón
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorTema.darker(), 2), // Borde más oscuro
                BorderFactory.createEmptyBorder(10, 18, 10, 18) // Padding interno
        ));

        // Agrego efectos hover para que se vea más interactivo
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (boton.isEnabled()) { // Solo si el botón está habilitado
                    Color colorHover = colorTema.brighter(); // Color más brillante al pasar el mouse
                    boton.setBackground(colorHover);
                    boton.setForeground(Color.black); // Cambio el texto a negro
                    boton.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(colorHover.darker(), 2),
                            BorderFactory.createEmptyBorder(10, 18, 10, 18)
                    ));
                    boton.repaint(); // Redibujo el botón
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Regreso a los colores originales
                boton.setBackground(colorTema);
                boton.setForeground(Color.black);
                boton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(colorTema.darker(), 2),
                        BorderFactory.createEmptyBorder(10, 18, 10, 18)
                ));
                boton.repaint();
            }
        });

        // Conecto el botón con el manejador de eventos
        boton.addActionListener(this::manejarClicBoton);

        // Me aseguro de que el texto se vea negro después de crear el botón
        SwingUtilities.invokeLater(() -> {
            boton.setForeground(Color.black);
            boton.repaint();
            boton.revalidate();
        });

        return boton;
    }

    // Método para actualizar la información de la fase actual y el progreso
    private void actualizarInformacionProgreso(String nombreFase, int pasoActual, int totalPasos) {
        this.nombreFaseActual = nombreFase;
        this.numeropasoActual = pasoActual;
        this.numeroTotalPasos = totalPasos;

        // Uso SwingUtilities para actualizar la interfaz desde cualquier hilo
        SwingUtilities.invokeLater(() -> {
            etiquetaFaseActual.setText(nombreFase); // Actualizo el texto de la fase
            if (totalPasos > 0) { // Si hay pasos que contar
                int porcentajeProgreso = (pasoActual * 100) / totalPasos; // Calculo el porcentaje
                barraProgresoAnimacion.setValue(porcentajeProgreso); // Actualizo la barra
                barraProgresoAnimacion.setString(porcentajeProgreso + "% (" + pasoActual + "/" + totalPasos + ")");
            } else {
                barraProgresoAnimacion.setValue(0); // Si no hay pasos, pongo en cero
                barraProgresoAnimacion.setString("0%");
            }
        });
    }

    // Método que maneja los clics en todos los botones
    private void manejarClicBoton(ActionEvent evento) {
        JButton botonPresionado = (JButton) evento.getSource(); // Obtengo qué botón fue presionado

        // Verifico cuál botón fue presionado y ejecuto la acción correspondiente
        if (botonPresionado == botonMezclarPesas) {
            mezclarPesasAleatorias(); // Genero nuevas pesas aleatorias
        } else if (botonPresionado == botonResolverOrdenamiento) {
            ejecutarAnimacionHeapSort(); // Inicio la animación del HeapSort
        } else if (botonPresionado == botonTerminarAnimacion) {
            detenerAnimacionCompletamente(); // Detengo la animación
        }
    }

    // Método para inicializar todos los datos por primera vez
    private void inicializarDatos() {
        cantidadTotalPesas = (Integer) selectorCantidadPesas.getSelectedItem(); // Obtengo la cantidad seleccionada
        generarPesasAleatoriasPrimeraVez(); // Genero las pesas iniciales
        reiniciarTodosLosContadores(); // Pongo los contadores en cero
        actualizarInformacionProgreso("Esperando...", 0, 0); // Estado inicial

        // Agrego un listener para cuando cambie la selección de cantidad
        selectorCantidadPesas.addActionListener(e -> {
            if (!animacionEjecutandose) { // Solo si no hay animación corriendo
                cantidadTotalPesas = (Integer) selectorCantidadPesas.getSelectedItem();
                generarPesasAleatoriasPrimeraVez(); // Genero nuevas pesas
                actualizarInformacionProgreso("Esperando...", 0, 0);
                panelDondeDibujo.repaint(); // Redibujo el panel
            }
        });
    }

    // Método para generar pesas con pesos aleatorios por primera vez
    private void generarPesasAleatoriasPrimeraVez() {
        arregloPesas = new int[cantidadTotalPesas]; // Creo el arreglo con el tamaño correcto
        Random generadorAleatorio = new Random(); // Creo el generador de números aleatorios

        // Lleno cada posición con un peso aleatorio entre 5 y 100
        for (int indicePesa = 0; indicePesa < cantidadTotalPesas; indicePesa++) {
            arregloPesas[indicePesa] = generadorAleatorio.nextInt(95) + 5; // Peso entre 5 y 99
        }
    }

    // Método que se ejecuta cuando presiono el botón "Mezclar"
    private void mezclarPesasAleatorias() {
        if (animacionEjecutandose) return; // Si hay animación corriendo, no hago nada

        generarPesasAleatoriasPrimeraVez(); // Genero nuevas pesas aleatorias
        reiniciarTodosLosContadores(); // Reinicio los contadores
        limpiarEstadosVisuales(); // Limpio los efectos visuales
        actualizarInformacionProgreso("Listo para ordenar", 0, 0); // Actualizo el estado
        panelDondeDibujo.repaint(); // Redibujo las pesas
    }

    // Método que se ejecuta cuando presiono el botón "Resolver"
    private void ejecutarAnimacionHeapSort() {
        if (animacionEjecutandose) return; // Si ya hay animación, no inicio otra

        // Deshabilito los botones para evitar interferencias
        animacionEjecutandose = true;
        botonResolverOrdenamiento.setEnabled(false);
        botonMezclarPesas.setEnabled(false);
        selectorCantidadPesas.setEnabled(false);

        reiniciarTodosLosContadores(); // Reinicio todos los contadores
        actualizarInformacionProgreso("Iniciando HeapSort...", 0, 0);
        iniciarProcesoHeapSort(); // Inicio el algoritmo de ordenamiento
    }

    // Método que se ejecuta cuando presiono el botón "Terminar"
    private void detenerAnimacionCompletamente() {
        // Detengo el timer si está corriendo
        if (temporizadorAnimacion != null && temporizadorAnimacion.isRunning()) {
            temporizadorAnimacion.stop();
        }

        // Rehabilito todos los controles
        animacionEjecutandose = false;
        botonResolverOrdenamiento.setEnabled(true);
        botonMezclarPesas.setEnabled(true);
        selectorCantidadPesas.setEnabled(true);

        limpiarEstadosVisuales(); // Limpio los efectos visuales
        actualizarInformacionProgreso("Terminado", 0, 0);
        panelDondeDibujo.repaint(); // Redibujo sin efectos
    }

    // Método para poner todos los contadores en cero
    private void reiniciarTodosLosContadores() {
        contadorComparaciones = 0;
        contadorMovimientos = 0;
        actualizarVisualizacionContadores(); // Actualizo la pantalla
    }

    // Método para actualizar los campos de texto de los contadores
    private void actualizarVisualizacionContadores() {
        campoComparaciones.setText(String.valueOf(contadorComparaciones));
        campoMovimientos.setText(String.valueOf(contadorMovimientos));
    }

    // Método para limpiar todos los estados visuales de la animación
    private void limpiarEstadosVisuales() {
        indicePesaSeleccionada1 = -1; // Quito la selección de la primera pesa
        indicePesaSeleccionada2 = -1; // Quito la selección de la segunda pesa
        mostrandoIntercambio = false; // Quito el efecto de intercambio
    }

    // Método principal para ejecutar todo el proceso del HeapSort
    private void iniciarProcesoHeapSort() {
        List<PasoAnimacion> todosLosPasos = generarTodosLosPasosHeapSort(); // Genero todos los pasos
        numeroTotalPasos = todosLosPasos.size(); // Guardo cuántos pasos son en total
        numeropasoActual = 0; // Empiezo desde el paso cero
        ejecutarPasosConTemporizador(todosLosPasos); // Inicio la animación paso a paso
    }

    // Método que genera todos los pasos que va a seguir el algoritmo HeapSort
    private List<PasoAnimacion> generarTodosLosPasosHeapSort() {
        List<PasoAnimacion> listaPasos = new ArrayList<>(); // Lista donde voy a guardar todos los pasos
        int[] copiaArreglo = arregloPesas.clone(); // Hago una copia para no modificar el original
        int tamanioArreglo = copiaArreglo.length;

        // Primera fase: Construcción del heap (de abajo hacia arriba)
        for (int indiceInicio = tamanioArreglo / 2 - 1; indiceInicio >= 0; indiceInicio--) {
            aplicarHeapify(copiaArreglo, tamanioArreglo, indiceInicio, listaPasos, "CONSTRUCCIÓN");
        }

        // Segunda fase: Extracción de elementos (de arriba hacia abajo)
        for (int indiceUltimo = tamanioArreglo - 1; indiceUltimo >= 0; indiceUltimo--) {
            // Muevo el elemento más grande (raíz) al final
            listaPasos.add(new PasoAnimacion(0, indiceUltimo, true, "Extracción", "Moviendo elemento máximo a posición final"));
            intercambiarElementos(copiaArreglo, 0, indiceUltimo);

            // Reorganizo el heap con un elemento menos
            aplicarHeapify(copiaArreglo, indiceUltimo, 0, listaPasos, "Extracción");
        }

        return listaPasos;
    }

    // Método recursivo que mantiene la propiedad de heap (el padre siempre es mayor que los hijos)
    private void aplicarHeapify(int[] arreglo, int tamanioHeap, int indiceRaiz, List<PasoAnimacion> pasos, String nombreFase) {
        int indiceMayor = indiceRaiz; // Asumo que la raíz es el mayor
        int indiceHijoIzquierdo = 2 * indiceRaiz + 1; // Calculo la posición del hijo izquierdo
        int indiceHijoDerecho = 2 * indiceRaiz + 2; // Calculo la posición del hijo derecho

        // Comparo con el hijo izquierdo si existe
        if (indiceHijoIzquierdo < tamanioHeap) {
            pasos.add(new PasoAnimacion(indiceMayor, indiceHijoIzquierdo, false, nombreFase, "Comparando con hijo izquierdo"));
            if (arreglo[indiceHijoIzquierdo] > arreglo[indiceMayor]) {
                indiceMayor = indiceHijoIzquierdo; // El hijo izquierdo es mayor
            }
        }

        // Comparo con el hijo derecho si existe
        if (indiceHijoDerecho < tamanioHeap) {
            pasos.add(new PasoAnimacion(indiceMayor, indiceHijoDerecho, false, nombreFase, "Comparando con hijo derecho"));
            if (arreglo[indiceHijoDerecho] > arreglo[indiceMayor]) {
                indiceMayor = indiceHijoDerecho; // El hijo derecho es mayor
            }
        }

        // Si encontré un hijo mayor que el padre, intercambio y sigo verificando
        if (indiceMayor != indiceRaiz) {
            pasos.add(new PasoAnimacion(indiceRaiz, indiceMayor, true, nombreFase, "Intercambiando para mantener propiedad heap"));
            intercambiarElementos(arreglo, indiceRaiz, indiceMayor);
            // Llamo recursivamente para verificar el subárbol afectado
            aplicarHeapify(arreglo, tamanioHeap, indiceMayor, pasos, nombreFase);
        }
    }

    // Método simple para intercambiar dos elementos del arreglo
    private void intercambiarElementos(int[] arreglo, int indice1, int indice2) {
        int elementoTemporal = arreglo[indice1]; // Guardo temporalmente el primer elemento
        arreglo[indice1] = arreglo[indice2]; // Pongo el segundo en la primera posición
        arreglo[indice2] = elementoTemporal; // Pongo el primero en la segunda posición
    }

    // Método que ejecuta todos los pasos de la animación usando un Timer
    private void ejecutarPasosConTemporizador(List<PasoAnimacion> listaPasos) {
        if (listaPasos.isEmpty()) { // Si no hay pasos que ejecutar
            detenerAnimacionCompletamente(); // Termino la animación
            return;
        }

        final int[] indiceActual = {0}; // Uso un arreglo para poder modificarlo dentro del ActionListener

        // Creo un Timer que ejecuta un paso cada segundo
        temporizadorAnimacion = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (indiceActual[0] >= listaPasos.size()) { // Si ya terminé todos los pasos
                    temporizadorAnimacion.stop(); // Detengo el timer
                    detenerAnimacionCompletamente(); // Limpio todo
                    mostrarMensajeCompletado(); // Muestro mensaje de éxito
                    return;
                }

                PasoAnimacion pasoActual = listaPasos.get(indiceActual[0]); // Obtengo el paso actual
                ejecutarUnPasoAnimacion(pasoActual); // Lo ejecuto
                indiceActual[0]++; // Avanzo al siguiente paso
            }
        });

        temporizadorAnimacion.start(); // Inicio el timer
    }

    // Método que ejecuta un solo paso de la animación
    private void ejecutarUnPasoAnimacion(PasoAnimacion paso) {
        // Establezco cuáles pesas están siendo procesadas
        indicePesaSeleccionada1 = paso.primerIndice;
        indicePesaSeleccionada2 = paso.segundoIndice;
        mostrandoIntercambio = paso.esIntercambio;

        // Creo el texto que se va a mostrar en la interfaz
        String textoFase = "Fase " + paso.nombreFase + ": " + paso.descripcionPaso;
        numeropasoActual++; // Incremento el contador de pasos
        actualizarInformacionProgreso(textoFase, numeropasoActual, numeroTotalPasos);

        // Si es un intercambio, modifico el arreglo y cuento el movimiento
        if (paso.esIntercambio) {
            intercambiarElementos(arregloPesas, paso.primerIndice, paso.segundoIndice);
            contadorMovimientos++; // Incremento el contador de movimientos
        }

        contadorComparaciones++; // Siempre incremento las comparaciones
        actualizarVisualizacionContadores(); // Actualizo los campos en pantalla
        panelDondeDibujo.repaint(); // Redibujo el panel para mostrar los cambios
    }

    // Método que muestra un mensaje cuando se completa el ordenamiento
    private void mostrarMensajeCompletado() {
        SwingUtilities.invokeLater(() -> { // Me aseguro de ejecutar en el hilo de la interfaz
            actualizarInformacionProgreso("¡Completado exitosamente!", numeroTotalPasos, numeroTotalPasos);

            // Creo un mensaje completo con todas las estadísticas
            String mensajeCompleto = String.format(
                    "¡Ordenamiento completado exitosamente!\n\n" +
                            "Estadísticas:\n" +
                            "• Comparaciones realizadas: %d\n" +
                            "• Movimientos ejecutados: %d\n" +
                            "• Elementos ordenados: %d\n\n" +
                            "¡Todas las pesas están ordenadas correctamente!",
                    contadorComparaciones, contadorMovimientos, cantidadTotalPesas
            );

            // Muestro el mensaje en un diálogo
            JOptionPane.showMessageDialog(this, mensajeCompleto, "Ordenamiento Completado",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    // Clase interna que representa un paso individual en la animación
    private static class PasoAnimacion {
        int primerIndice, segundoIndice; // Índices de las dos pesas involucradas
        boolean esIntercambio; // True si es un intercambio, false si es solo comparación
        String nombreFase; // Nombre de la fase actual (construcción o extracción)
        String descripcionPaso; // Descripción de lo que está pasando en este paso

        // Constructor de la clase PasoAnimacion
        public PasoAnimacion(int indice1, int indice2, boolean intercambio, String fase, String descripcion) {
            this.primerIndice = indice1;
            this.segundoIndice = indice2;
            this.esIntercambio = intercambio;
            this.nombreFase = fase;
            this.descripcionPaso = descripcion;
        }
    }

    // Clase interna que se encarga de dibujar todas las pesas y efectos visuales
    private class PanelVisualizacionPesas extends JPanel {

        // Constructor del panel de visualización
        public PanelVisualizacionPesas() {
            setBackground(COLOR_PANELES_BLANCOS); // Establezco el color de fondo
            setPreferredSize(new Dimension(900, 450)); // Defino el tamaño preferido
        }

        // Método principal que dibuja todo en el panel
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Llamo al método padre para limpiar el panel

            if (arregloPesas == null) return; // Si no hay pesas, no dibujo nada

            // Convierto a Graphics2D para tener mejores opciones de dibujo
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Suavizado
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY); // Calidad alta

            dibujarSueloBase(g2d); // Dibujo el suelo donde van las pesas
            dibujarTodasLasPesas(g2d); // Dibujo todas las pesas
            dibujarEfectosEspecialesAnimacion(g2d); // Dibujo los efectos de la animación

            g2d.dispose(); // Libero los recursos del Graphics2D
        }

        // Método para dibujar el suelo donde van apoyadas las pesas
        private void dibujarSueloBase(Graphics2D g2d) {
            int anchoPanel = getWidth(); // Obtengo el ancho del panel
            int altoPanel = getHeight(); // Obtengo la altura del panel
            int alturaLineaSuelo = altoPanel - 60; // Calculo dónde va la línea del suelo

            // Creo un gradiente para el suelo
            GradientPaint gradienteSuelo = new GradientPaint(
                    0, alturaLineaSuelo, COLOR_SOMBRAS_PANELES.brighter(), // Color arriba
                    0, altoPanel, COLOR_SOMBRAS_PANELES // Color abajo
            );
            g2d.setPaint(gradienteSuelo);
            g2d.fillRect(0, alturaLineaSuelo, anchoPanel, altoPanel - alturaLineaSuelo); // Relleno el área

            // Dibujo la línea que separa el suelo del área de las pesas
            g2d.setColor(COLOR_SOMBRAS_PANELES.darker());
            g2d.setStroke(new BasicStroke(2)); // Línea de 2 píxeles de grosor
            g2d.drawLine(0, alturaLineaSuelo, anchoPanel, alturaLineaSuelo);
        }

        // Método para dibujar todas las pesas en el panel
        private void dibujarTodasLasPesas(Graphics2D g2d) {
            int anchoPanel = getWidth();
            int altoPanel = getHeight();
            int margenHorizontal = 40; // Margen en los lados
            int anchoPorCadaPesa = (anchoPanel - 2 * margenHorizontal) / cantidadTotalPesas; // Espacio para cada pesa
            int alturaLineaSuelo = altoPanel - 60; // Donde termina el suelo

            // Dibujo cada pesa individualmente
            for (int indicePesa = 0; indicePesa < cantidadTotalPesas; indicePesa++) {
                int posicionX = margenHorizontal + indicePesa * anchoPorCadaPesa; // Posición horizontal
                int anchoPesa = Math.min(anchoPorCadaPesa - 8, 65); // Ancho máximo de la pesa
                int centroX = posicionX + (anchoPorCadaPesa - anchoPesa) / 2; // Centro la pesa

                // Determino los colores según el estado de la pesa
                Color colorBase = COLOR_BASE_PESAS; // Color por defecto
                Color colorDisco = COLOR_DISCO_PESAS; // Color por defecto
                boolean debeConBrillo = false; // Si debe tener efecto de brillo

                // Si es la primera pesa seleccionada, uso color rojo
                if (indicePesa == indicePesaSeleccionada1) {
                    colorBase = new Color(180, 10, 10);
                    colorDisco = COLOR_PESA_SELECCIONADA;
                    debeConBrillo = true;
                }
                // Si es la segunda pesa seleccionada, uso color azul
                else if (indicePesa == indicePesaSeleccionada2) {
                    colorBase = new Color(10, 50, 180);
                    colorDisco = COLOR_PESA_COMPARANDO;
                    debeConBrillo = true;
                }

                // Si la pesa debe brillar, dibujo el efecto primero
                if (debeConBrillo) {
                    dibujarEfectoBrilloAlrededor(g2d, centroX + anchoPesa / 2, alturaLineaSuelo, anchoPesa, arregloPesas[indicePesa], indicePesa);
                }

                // Dibujo la pesa con todos sus detalles
                dibujarPesaCompletaRealista(g2d, centroX, alturaLineaSuelo, anchoPesa, arregloPesas[indicePesa], colorBase, colorDisco);

                // Dibujo la etiqueta con el peso
                dibujarEtiquetaPeso(g2d, centroX, alturaLineaSuelo + 35, anchoPesa, arregloPesas[indicePesa], debeConBrillo);
            }
        }

        // Método para dibujar el efecto de brillo alrededor de pesas seleccionadas
        private void dibujarEfectoBrilloAlrededor(Graphics2D g2d, int centroX, int yBase, int ancho, int peso, int indicePesa) {
            int alturaTotal = Math.min((peso * 2) + 60, 220); // Calculo la altura total de la pesa
            int yArriba = yBase - alturaTotal; // Posición superior
            int yCentro = yArriba + alturaTotal / 2; // Centro vertical

            // Determino el color del brillo según qué pesa es
            Color colorBrillo;
            if (indicePesa == indicePesaSeleccionada1) {
                colorBrillo = COLOR_PESA_SELECCIONADA; // Rojo para la primera
            } else if (indicePesa == indicePesaSeleccionada2) {
                colorBrillo = COLOR_PESA_COMPARANDO; // Azul para la segunda
            } else {
                return; // Si no es ninguna de las dos, no dibujo brillo
            }

            // Dibujo tres círculos concéntricos para crear el efecto de brillo
            g2d.setColor(new Color(colorBrillo.getRed(), colorBrillo.getGreen(), colorBrillo.getBlue(), 60));
            int radioExterno = ancho / 2 + 25; // Radio más grande
            g2d.fillOval(centroX - radioExterno, yCentro - radioExterno, radioExterno * 2, radioExterno * 2);

            g2d.setColor(new Color(colorBrillo.getRed(), colorBrillo.getGreen(), colorBrillo.getBlue(), 100));
            int radioMedio = ancho / 2 + 15; // Radio medio
            g2d.fillOval(centroX - radioMedio, yCentro - radioMedio, radioMedio * 2, radioMedio * 2);

            g2d.setColor(new Color(colorBrillo.getRed(), colorBrillo.getGreen(), colorBrillo.getBlue(), 140));
            int radioInterno = ancho / 2 + 8; // Radio más pequeño
            g2d.fillOval(centroX - radioInterno, yCentro - radioInterno, radioInterno * 2, radioInterno * 2);
        }

        // Método para dibujar una pesa completa con todos sus detalles
        private void dibujarPesaCompletaRealista(Graphics2D g2d, int x, int yBase, int ancho, int peso, Color colorBase, Color colorDisco) {
            int alturaTotal = Math.min((peso * 2) + 60, 220); // Calculo la altura según el peso
            int y = yBase - alturaTotal; // Posición superior de la pesa

            // Dibujo la sombra de la pesa en el suelo
            g2d.setColor(new Color(0, 0, 0, 60)); // Negro semitransparente
            g2d.fillOval(x + 3, yBase + 2, ancho - 6, 10); // Óvalo pequeño como sombra

            // Calculo las dimensiones de los componentes de la pesa
            int diametroDisco = Math.min(ancho - 12, 50); // Diámetro de los discos
            int grosorDisco = Math.max(diametroDisco / 4, 12); // Grosor de cada disco
            int alturaBarra = alturaTotal - (grosorDisco * 2) - 10; // Altura de la barra central
            int anchoBarra = Math.max(diametroDisco / 8, 8); // Ancho de la barra

            int centroX = x + ancho / 2; // Centro horizontal de la pesa
            int xBarra = centroX - anchoBarra / 2; // Posición de la barra
            int yBarra = y + grosorDisco + 5; // Posición vertical de la barra

            // Dibujo el disco superior
            dibujarDiscoModernoDetallado(g2d, centroX - diametroDisco / 2, y, diametroDisco, grosorDisco, colorDisco, true);

            // Dibujo la barra central con gradiente
            GradientPaint gradienteBarra = new GradientPaint(
                    xBarra, yBarra, new Color(200, 200, 200), // Color claro a la izquierda
                    xBarra + anchoBarra, yBarra, new Color(140, 140, 140) // Color oscuro a la derecha
            );
            g2d.setPaint(gradienteBarra);
            g2d.fillRoundRect(xBarra, yBarra, anchoBarra, alturaBarra, anchoBarra / 2, anchoBarra / 2);

            // Agrego textura a la barra con líneas horizontales
            g2d.setColor(new Color(120, 120, 120));
            g2d.setStroke(new BasicStroke(1));
            for (int i = 1; i < 6; i++) {
                int yTextura = yBarra + (alturaBarra * i) / 6; // Posición de cada línea
                g2d.drawLine(xBarra + 1, yTextura, xBarra + anchoBarra - 1, yTextura);
            }

            // Agrego un reflejo a la barra para hacerla más realista
            g2d.setColor(new Color(220, 220, 220, 150)); // Blanco semitransparente
            g2d.fillRoundRect(xBarra + 1, yBarra, anchoBarra / 3, alturaBarra, anchoBarra / 4, anchoBarra / 4);

            // Dibujo el disco inferior
            int yDiscoInferior = yBarra + alturaBarra;
            dibujarDiscoModernoDetallado(g2d, centroX - diametroDisco / 2, yDiscoInferior, diametroDisco, grosorDisco, colorDisco, false);
        }

        // Método para dibujar un disco de pesa con muchos detalles realistas
        private void dibujarDiscoModernoDetallado(Graphics2D g2d, int x, int y, int diametro, int grosor, Color color, boolean esSuperior) {
            // Creo un gradiente radial para darle volumen al disco
            RadialGradientPaint gradienteRadial = new RadialGradientPaint(
                    x + diametro / 2, y + grosor / 2, diametro / 2, // Centro y radio
                    new float[]{0.0f, 0.6f, 0.9f, 1.0f}, // Posiciones del gradiente
                    new Color[]{
                            color.brighter().brighter(), // Centro muy claro
                            color.brighter(), // Medio claro
                            color, // Color original
                            new Color(50, 50, 50) // Borde oscuro
                    }
            );

            // Relleno el disco con el gradiente
            g2d.setPaint(gradienteRadial);
            g2d.fillOval(x, y, diametro, grosor);

            // Dibujo el borde exterior del disco
            g2d.setColor(new Color(220, 220, 220)); // Color claro para el borde
            g2d.setStroke(new BasicStroke(3)); // Línea gruesa
            g2d.drawOval(x, y, diametro, grosor);

            // Dibujo el agujero central del disco
            int margenInterior = diametro / 6;
            g2d.setColor(new Color(40, 40, 40)); // Color oscuro para el agujero
            g2d.fillOval(x + margenInterior, y + grosor / 4,
                    diametro - 2 * margenInterior, grosor / 2);

            // Dibujo un anillo decorativo alrededor del agujero
            int margenAnillo = diametro / 4;
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(new Color(180, 180, 180));
            g2d.drawOval(x + margenAnillo, y + grosor / 3,
                    diametro - 2 * margenAnillo, grosor / 3);

            // Agrego reflejos diferentes según si es el disco superior o inferior
            if (esSuperior) {
                g2d.setColor(new Color(255, 255, 255, 180)); // Reflejo más brillante arriba
                g2d.fillOval(x + diametro / 4, y + 2, diametro / 2, grosor / 3);
            } else {
                g2d.setColor(new Color(255, 255, 255, 120)); // Reflejo más suave abajo
                g2d.fillOval(x + diametro / 3, y + grosor - grosor / 3, diametro / 3, grosor / 4);
            }

            // Dibujo tornillos decorativos en cuatro posiciones
            g2d.setColor(new Color(100, 100, 100)); // Color base de los tornillos
            for (int i = 0; i < 4; i++) {
                double angulo = i * Math.PI / 2; // Cada 90 grados
                int xTornillo = (int) (x + diametro / 2 + Math.cos(angulo) * diametro / 3);
                int yTornillo = (int) (y + grosor / 2 + Math.sin(angulo) * grosor / 4);
                g2d.fillOval(xTornillo - 3, yTornillo - 2, 6, 4); // Base del tornillo

                // Agrego el reflejo del tornillo
                g2d.setColor(new Color(150, 150, 150));
                g2d.fillOval(xTornillo - 1, yTornillo - 1, 2, 2);
                g2d.setColor(new Color(100, 100, 100)); // Regreso al color base
            }
        }

        // Método para dibujar la etiqueta con el peso de cada pesa
        private void dibujarEtiquetaPeso(Graphics2D g2d, int x, int y, int ancho, int peso, boolean esActiva) {
            String textoPeso = peso + " kg"; // Formato del texto

            // Uso fuente más grande si la pesa está activa
            Font fuente = new Font("Segoe UI", Font.BOLD, esActiva ? 16 : 14);
            g2d.setFont(fuente);
            FontMetrics medidasFuente = g2d.getFontMetrics(); // Para calcular el tamaño del texto

            int anchoTexto = medidasFuente.stringWidth(textoPeso); // Ancho del texto
            int xTexto = x + (ancho - anchoTexto) / 2; // Centro el texto horizontalmente

            // Si la pesa está activa, dibujo un fondo destacado
            if (esActiva) {
                g2d.setColor(new Color(255, 255, 255, 220)); // Fondo blanco semitransparente
                g2d.fillRoundRect(xTexto - 6, y - medidasFuente.getHeight() + 2, anchoTexto + 12, medidasFuente.getHeight(), 8, 8);

                // Determino el color del borde según cuál pesa es
                Color colorBorde = COLOR_PESA_SELECCIONADA; // Por defecto rojo
                int indicePesa = (x - 40) / ((getWidth() - 80) / cantidadTotalPesas); // Calculo el índice
                if (indicePesa == indicePesaSeleccionada2) {
                    colorBorde = COLOR_PESA_COMPARANDO; // Azul para la segunda pesa
                }

                // Dibujo el borde del fondo
                g2d.setColor(colorBorde);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(xTexto - 6, y - medidasFuente.getHeight() + 2, anchoTexto + 12, medidasFuente.getHeight(), 8, 8);
            }

            // Dibujo el texto con el color apropiado
            Color colorTexto = esActiva ? COLOR_TEXTO_PRINCIPAL.darker() : COLOR_TEXTO_PRINCIPAL;
            g2d.setColor(colorTexto);
            g2d.drawString(textoPeso, xTexto, y);
        }

        // Método para dibujar efectos especiales durante los intercambios
        private void dibujarEfectosEspecialesAnimacion(Graphics2D g2d) {
            // Solo dibujo si estoy mostrando un intercambio y tengo pesas válidas
            if (!mostrandoIntercambio || indicePesaSeleccionada1 < 0 || indicePesaSeleccionada2 < 0) return;

            int anchoPanel = getWidth();
            int margenHorizontal = 40;
            int anchoPorCadaPesa = (anchoPanel - 2 * margenHorizontal) / cantidadTotalPesas;

            // Calculo las posiciones horizontales de las dos pesas
            int x1 = margenHorizontal + indicePesaSeleccionada1 * anchoPorCadaPesa + anchoPorCadaPesa / 2;
            int x2 = margenHorizontal + indicePesaSeleccionada2 * anchoPorCadaPesa + anchoPorCadaPesa / 2;
            int yLinea = getHeight() - 200; // Altura de la línea de intercambio

            // Dibujo una línea punteada animada entre las dos pesas
            g2d.setColor(COLOR_ADVERTENCIA_AMARILLO);
            g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    0, new float[]{15, 10}, System.currentTimeMillis() / 100)); // Línea animada
            g2d.drawLine(x1, yLinea, x2, yLinea);

            // Dibujo flechas en ambos extremos
            dibujarFlechaModernaDetallada(g2d, x1, yLinea, 25, COLOR_ADVERTENCIA_AMARILLO);
            dibujarFlechaModernaDetallada(g2d, x2, yLinea, -25, COLOR_ADVERTENCIA_AMARILLO);

            // Dibujo el texto "INTERCAMBIO" en el centro
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
            String textoIntercambio = "INTERCAMBIO";
            FontMetrics medidas = g2d.getFontMetrics();
            int anchoTexto = medidas.stringWidth(textoIntercambio);
            int xTexto = (x1 + x2 - anchoTexto) / 2; // Centro el texto entre las dos pesas

            // Dibujo el fondo del texto
            g2d.setColor(new Color(245, 158, 11, 200)); // Amarillo semitransparente
            g2d.fillRoundRect(xTexto - 12, yLinea - 25, anchoTexto + 24, 20, 12, 12);

            // Dibujo el borde del fondo
            g2d.setColor(COLOR_ADVERTENCIA_AMARILLO.darker());
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(xTexto - 12, yLinea - 25, anchoTexto + 24, 20, 12, 12);

            // Dibujo el texto
            g2d.setColor(Color.WHITE);
            g2d.drawString(textoIntercambio, xTexto, yLinea - 10);
        }

        // Método para dibujar flechas modernas con detalles
        private void dibujarFlechaModernaDetallada(Graphics2D g2d, int x, int y, int direccion, Color color) {
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Dibujo la punta superior de la flecha
            int[] xPuntos = {x, x + direccion, x + direccion * 2 / 3};
            int[] yPuntos = {y, y, y - 12};
            g2d.fillPolygon(xPuntos, yPuntos, 3);

            // Dibujo la línea horizontal de la flecha
            g2d.drawLine(x, y, x + direccion * 2 / 3, y);

            // Dibujo la punta inferior de la flecha
            int[] xPuntos2 = {x, x + direccion, x + direccion * 2 / 3};
            int[] yPuntos2 = {y, y, y + 12};
            g2d.fillPolygon(xPuntos2, yPuntos2, 3);
        }
    }

    // Método main para ejecutar la aplicación
    public static void main(String[] args) {
        try {
            // Intento usar el look and feel del sistema para que se vea nativo
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace(); // Si falla, continúo con el look por defecto
        }

        // Creo y muestro la ventana en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            new AnimacionHeapSort().setVisible(true);
        });
    }
}