// Define la clase que contendrá el método de ordenamiento.
public class CountingSort {


    public int[] ordenar(int[] arreglo) {
        // --- 1. MANEJO DE CASOS ESPECIALES Y PREPARACIÓN ---

        // Si el arreglo de entrada está vacío, no hay nada que ordenar. Se devuelve inmediatamente.
        if (arreglo.length == 0) {
            return arreglo;
        }

        // Se necesita encontrar el valor máximo para saber el tamaño del arreglo de conteo.
        // Se asume inicialmente que el primer elemento es el máximo.
        int max = arreglo[0];
        // Se recorre el resto del arreglo para encontrar el valor máximo real.
        for (int num : arreglo) {
            if (num > max) {
                max = num;
            }
        }

        // --- 2. FASE DE CONTEO DE FRECUENCIAS ---

        // Se crea el arreglo 'conteo' que almacenará la frecuencia de cada número.
        // El tamaño es 'max + 1' para tener índices desde 0 hasta 'max'.
        int[] conteo = new int[max + 1];
        // Se recorre el arreglo original.
        for (int num : arreglo) {
            // Para cada número, se incrementa el contador en la posición correspondiente.
            // Ejemplo: si num es 5, conteo[5] aumenta en 1.
            conteo[num]++;
        }

        // --- 3. FASE DE RECONSTRUCCIÓN DEL ARREGLO ORDENADO ---

        // Se inicializa un índice para llevar la cuenta de la posición actual en el arreglo original.
        int index = 0;
        // Se recorre el arreglo 'conteo' desde el índice 0 hasta el final. 'i' representa el número.
        for (int i = 0; i < conteo.length; i++) {
            // Para cada número 'i', se ejecuta un bucle tantas veces como su frecuencia (almacenada en conteo[i]).
            while (conteo[i] > 0) {
                // Se coloca el número 'i' en la posición actual del arreglo original.
                arreglo[index] = i;
                // Se avanza el índice del arreglo original para la siguiente inserción.
                index++;
                // Se decrementa la frecuencia del número 'i' que acabamos de colocar.
                conteo[i]--;
            }
        }

        // Se devuelve el arreglo original, que ahora ha sido modificado y está ordenado.
        return arreglo;
    }
}