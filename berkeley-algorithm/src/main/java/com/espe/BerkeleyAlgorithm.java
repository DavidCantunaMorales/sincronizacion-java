package com.espe;

import java.util.*;
import java.util.concurrent.*;

public class BerkeleyAlgorithm {
    public static void main(String[] args) {
        // Crea un pool de hilos con capacidad para manejar 5 nodos
        ExecutorService executor = Executors.newFixedThreadPool(5);
        Random random = new Random();

        // Lista para almacenar los resultados futuros de los nodos
        List<Future<Long>> futures = new ArrayList<>();

        // Inicializa 5 nodos con tiempos locales desajustados aleatoriamente
        for (int i = 0; i < 5; i++) {
            long localTime = System.currentTimeMillis() + random.nextInt(1000) - 500; // +/- 500ms desajuste
            futures.add(executor.submit(new BerkeleyNode(localTime)));
        }

        try {
            long sum = 0;
            List<Long> times = new ArrayList<>();

            // Recupera los tiempos locales de los nodos y calcula la suma
            for (Future<Long> future : futures) {
                long nodeTime = future.get(); // Obtiene el tiempo del nodo
                times.add(nodeTime);
                sum += nodeTime;
            }

            // Calcula el tiempo promedio entre todos los nodos
            long averageTime = sum / times.size();

            // Muestra los relojes locales de los nodos
            System.out.println("Relojes locales: " + times);

            // Muestra la hora promedio calculada
            System.out.println("Hora promedio calculada: " + new Date(averageTime));

            // Calcula y muestra los ajustes necesarios para cada nodo
            System.out.println("Ajustes de los relojes:");
            for (long time : times) {
                System.out.println("Ajuste: " + (averageTime - time) + "ms");
            }
        } catch (Exception e) {
            // Maneja errores en el proceso de sincronización
            System.err.println("Error en la sincronización: " + e.getMessage());
        } finally {
            // Cierra el pool de hilos para liberar recursos
            executor.shutdown();
        }
    }
}
