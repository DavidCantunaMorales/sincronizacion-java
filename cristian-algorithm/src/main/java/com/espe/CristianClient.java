package com.espe;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CristianClient {
    private static final String[] SERVERS = {
            "localhost", // Puedes añadir más servidores si lo deseas
    };

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cliente - Algoritmo de Cristian");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        JLabel timeLabel = new JLabel("Hora ajustada: Cargando...", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel rttLabel = new JLabel("Latencia (RTT): -", SwingConstants.CENTER);
        rttLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel portLabel = new JLabel("Puerto:", SwingConstants.RIGHT);
        JTextField portField = new JTextField("12345", 10);

        JComboBox<String> serverComboBox = new JComboBox<>(SERVERS);
        serverComboBox.setSelectedIndex(0);

        JButton syncButton = new JButton("Sincronizar");
        syncButton.addActionListener(e -> {
            String serverAddress = (String) serverComboBox.getSelectedItem();
            int port;
            try {
                port = Integer.parseInt(portField.getText());
            } catch (NumberFormatException ex) {
                timeLabel.setText("Error: Puerto inválido.");
                return;
            }

            try {
                SyncResult result = getAdjustedTime(serverAddress, port);
                timeLabel.setText("Hora ajustada: " + result.adjustedTime);
                rttLabel.setText("Latencia (RTT): " + result.roundTripDelay + " ms");
            } catch (IOException ex) {
                timeLabel.setText("Error: " + ex.getMessage());
                rttLabel.setText("Latencia (RTT): -");
            }
        });

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Servidor:", SwingConstants.RIGHT));
        inputPanel.add(serverComboBox);
        inputPanel.add(portLabel);
        inputPanel.add(portField);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(syncButton, BorderLayout.CENTER);

        frame.setLayout(new BorderLayout());
        frame.add(timeLabel, BorderLayout.CENTER);
        frame.add(rttLabel, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static SyncResult getAdjustedTime(String serverAddress, int port) throws IOException {
        try (Socket socket = new Socket(serverAddress, port)) {
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            long t0 = System.currentTimeMillis();
            long serverTime = dis.readLong();
            long t1 = System.currentTimeMillis();

            long roundTripDelay = t1 - t0;
            long adjustedTime = serverTime + roundTripDelay / 2;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return new SyncResult(sdf.format(new Date(adjustedTime)), roundTripDelay);
        }
    }

    private static class SyncResult {
        String adjustedTime;
        long roundTripDelay;

        SyncResult(String adjustedTime, long roundTripDelay) {
            this.adjustedTime = adjustedTime;
            this.roundTripDelay = roundTripDelay;
        }
    }
}
