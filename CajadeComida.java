/**
 * Clase que representa una caja de comida con diferentes propiedades
 * Utilizada en la visualización del algoritmo QuickSort
 */
public class CajadeComida {
    // Propiedades principales de la caja
    public int peso;                    // Peso de la caja (1-100)
    public int x, y;                   // Coordenadas de posición (no utilizadas actualmente)
    public String tipoComida;          // Tipo de comida basado en el peso

    // Propiedades para la visualización del algoritmo
    public boolean esSeleccionado = false;  // Indica si la caja está seleccionada
    public boolean esPivot = false;         // Indica si la caja es el pivot en QuickSort

    /**
     * Constructor que inicializa una caja de comida con un peso específico
     * @param peso El peso de la caja (determina el tipo de comida)
     */
    public CajadeComida(int peso) {
        this.peso = peso;
        asignarTipoComida();  // Asigna automáticamente el tipo basado en el peso
    }

    /**
     * Método privado que asigna el tipo de comida basándose en rangos de peso
     * Rangos:
     * - 1-20kg: Snack
     * - 21-40kg: Fruta
     * - 41-60kg: Sandwich
     * - 61-80kg: Pizza
     * - 81-100kg: Hamburguesa
     */
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

    /**
     * Getter para obtener el tipo de comida
     * @return El tipo de comida como String
     */
    public String getTipoComida() {
        return tipoComida;
    }

    /**
     * Obtiene el icono (carácter) representativo del tipo de comida
     * Usado para mostrar una letra identificativa en la visualización
     * @return Carácter que representa el tipo de comida
     */
    public char getIcono() {
        // Mapeo de tipos de comida a caracteres representativos
        switch (tipoComida) {
            case "Snack": return 'S';       // S de Snack
            case "Fruta": return 'F';       // F de Fruta
            case "Sandwich": return 'W';    // W de sándWich
            case "Pizza": return 'P';       // P de Pizza
            case "Hamburguesa": return 'H'; // H de Hamburguesa
            default: return '?';            // Carácter por defecto en caso de error
        }
    }
}