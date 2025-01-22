package com.espe;

// Importamos las librerías necesarias
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;
import java.util.Date;

/**
 * Clase principal que se conecta a un servidor NTP para obtener la hora sincronizada.
 */
public class Main {
    public static void main(String[] args) {
        // Nombre del servidor NTP al que nos conectaremos
        String ntpServer = "time.google.com";

        // Creamos un cliente NTPUDP para conectarnos al servidor
        NTPUDPClient client = new NTPUDPClient();

        // Establecemos un tiempo de espera de 10 segundos para la conexión
        client.setDefaultTimeout(10000);

        try {
            // Resolvemos la dirección del servidor NTP
            InetAddress hostAddr = InetAddress.getByName(ntpServer);

            // Solicitamos la información de tiempo al servidor NTP
            TimeInfo timeInfo = client.getTime(hostAddr);

            // Calculamos los detalles del tiempo (puede incluir el retraso de red)
            timeInfo.computeDetails();

            // Obtenemos el tiempo transmitido desde el servidor NTP en milisegundos
            long serverTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();

            // Convertimos el tiempo en milisegundos a un objeto Date para facilitar su manejo
            Date serverDate = new Date(serverTime);

            // Mostramos la hora sincronizada con el servidor NTP
            System.out.println("Hora sincronizada con " + ntpServer + ": " + serverDate);

        } catch (Exception e) {
            // Mostramos cualquier error que pueda ocurrir durante la ejecución
            e.printStackTrace();
        } finally {
            // Cerramos el cliente NTP para liberar los recursos
            client.close();
        }
    }
}