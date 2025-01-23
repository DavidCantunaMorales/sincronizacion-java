package com.espe;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Main {

    // Lista de servidores NTP predeterminados
    private static final String[] NTP_SERVERS = {
            "time.google.com",
            "pool.ntp.org",
            "time.windows.com",
            "time.nist.gov",
            "time.apple.com"
    };

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sincronización de Relojes con NTP");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        // Etiqueta para mostrar la hora sincronizada
        JLabel timeLabel = new JLabel("Hora sincronizada: Cargando...", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Etiqueta para mostrar la latencia (RTT)
        JLabel rttLabel = new JLabel("Latencia (RTT): -", SwingConstants.CENTER);
        rttLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // ComboBox para seleccionar el servidor NTP
        JComboBox<String> ntpServerComboBox = new JComboBox<>(NTP_SERVERS);
        ntpServerComboBox.setSelectedIndex(0); // Selecciona el primer servidor por defecto

        // Botón para sincronizar
        JButton syncButton = new JButton("Sincronizar Hora Local");
        syncButton.addActionListener(e -> {
            try {
                // Obtener el servidor seleccionado
                String selectedServer = (String) ntpServerComboBox.getSelectedItem();
                SyncResult result = getNetworkTime(selectedServer);

                // Mostrar resultados en la interfaz
                timeLabel.setText("Hora sincronizada: " + result.synchronizedTime);
                rttLabel.setText("Latencia (RTT): " + result.roundTripTime + " ms");

                // Ajustar la hora local del sistema
                syncSystemClock(result.synchronizedTime);
            } catch (Exception ex) {
                timeLabel.setText("Error al sincronizar: " + ex.getMessage());
                rttLabel.setText("Latencia (RTT): -");
            }
        });

        // Panel para organizar los componentes
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(ntpServerComboBox, BorderLayout.NORTH);
        panel.add(syncButton, BorderLayout.CENTER);

        frame.setLayout(new BorderLayout());
        frame.add(timeLabel, BorderLayout.CENTER);
        frame.add(rttLabel, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static SyncResult getNetworkTime(String server) throws Exception {
        NTPUDPClient client = new NTPUDPClient();
        client.setDefaultTimeout(3000);
        InetAddress address = InetAddress.getByName(server);

        // Registro del tiempo antes y después de la solicitud
        long t0 = System.currentTimeMillis(); // Tiempo antes de enviar la solicitud
        TimeInfo timeInfo = client.getTime(address);
        long t1 = System.currentTimeMillis(); // Tiempo después de recibir la respuesta

        timeInfo.computeDetails();

        // Calcular la latencia (RTT)
        long roundTripTime = t1 - t0;

        // Obtener la hora sincronizada del servidor
        long serverTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();

        // Ajustar el tiempo sumando la mitad del RTT
        long adjustedTime = serverTime + roundTripTime / 2;

        // Formatear la hora ajustada
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Forzar UTC
        return new SyncResult(sdf.format(new Date(adjustedTime)), roundTripTime);
    }

    private static void syncSystemClock(String utcTime) throws Exception {
        // Convertir UTC a la hora local
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date utcDate = sdf.parse(utcTime);

        SimpleDateFormat localFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        localFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Forzar UTC
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Forzar UTC

        String localTime = localFormat.format(utcDate);
        String localDate = dateFormat.format(utcDate);

        // Comandos para Windows con valores automatizados
        String timeCommand = String.format("cmd /c echo %s | time", localTime);
        String dateCommand = String.format("cmd /c echo %s | date", localDate);

        // Mostrar los comandos en la consola
        System.out.println("Ejecutando comando para cambiar la hora: " + timeCommand);
        System.out.println("Ejecutando comando para cambiar la fecha: " + dateCommand);

        // Ejecutar los comandos
        Process timeProcess = Runtime.getRuntime().exec(timeCommand);
        Process dateProcess = Runtime.getRuntime().exec(dateCommand);

        // Esperar a que los procesos finalicen
        timeProcess.waitFor();
        dateProcess.waitFor();

        // Mostrar el estado de ejecución
        System.out.println("Cambio de hora finalizado con código: " + timeProcess.exitValue());
        System.out.println("Cambio de fecha finalizado con código: " + dateProcess.exitValue());
    }

    // Clase para encapsular los resultados de la sincronización
    private static class SyncResult {
        String synchronizedTime;
        long roundTripTime;

        SyncResult(String synchronizedTime, long roundTripTime) {
            this.synchronizedTime = synchronizedTime;
            this.roundTripTime = roundTripTime;
        }
    }
}
