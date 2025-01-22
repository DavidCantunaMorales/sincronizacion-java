package com.espe;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CristianClient {
    public static void main(String[] args) {
        // Dirección del servidor al que nos conectaremos
        String serverAddress = "localhost";

        try (Socket socket = new Socket(serverAddress, 12345)) { // Conexión al servidor en el puerto 12345
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            // Registramos el tiempo antes de recibir la hora del servidor
            long t0 = System.currentTimeMillis();

            // Leemos la hora enviada por el servidor
            long serverTime = dis.readLong();

            // Registramos el tiempo después de recibir la hora del servidor
            long t1 = System.currentTimeMillis();

            // Calculamos el retraso de ida y vuelta
            long roundTripDelay = (t1 - t0) / 2;

            // Ajustamos la hora usando el retraso calculado
            long adjustedTime = serverTime + roundTripDelay;

            // Convertimos la hora ajustada a un objeto Date para facilitar la visualización
            Date adjustedDate = new Date(adjustedTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // Mostramos la hora ajustada
            System.out.println("Hora ajustada usando algoritmo de Cristian: " + sdf.format(adjustedDate));
        } catch (IOException e) {
            // Capturamos y mostramos cualquier error del cliente
            System.err.println("Error en el cliente: " + e.getMessage());
        }
    }
}
