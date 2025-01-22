package com.espe;

import java.io.*;
import java.net.*;

public class CristianServer {
    public static void main(String[] args) {
        // Creamos un ServerSocket en el puerto 12345 para escuchar conexiones entrantes
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Servidor listo para sincronización...");

            // Ciclo infinito para manejar múltiples clientes
            while (true) {
                try (Socket socket = serverSocket.accept()) { // Aceptamos una conexión de cliente
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                    // Obtenemos la hora actual del servidor en milisegundos
                    long currentTime = System.currentTimeMillis();

                    // Enviamos la hora al cliente
                    dos.writeLong(currentTime);
                    System.out.println("Hora enviada al cliente.");
                }
            }
        } catch (IOException e) {
            // Capturamos y mostramos cualquier error del servidor
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}