package com.espe;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class BerkeleyAlgorithm {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Algoritmo de Berkeley");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 3, 10, 10));

        // Crear componentes para cada nodo
        List<JLabel> nodeLabels = new ArrayList<>();
        List<JLabel> adjustmentLabels = new ArrayList<>();
        List<JLabel> clockImages = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            JLabel nodeLabel = new JLabel("Nodo " + (i + 1) + ": Tiempo local");
            nodeLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel adjustmentLabel = new JLabel("Ajuste: ");
            adjustmentLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Icono dinámico del reloj
            JLabel clockImage = new JLabel();
            clockImage.setHorizontalAlignment(SwingConstants.CENTER);
            clockImage.setIcon(createClockIcon());

            nodeLabels.add(nodeLabel);
            adjustmentLabels.add(adjustmentLabel);
            clockImages.add(clockImage);

            panel.add(nodeLabel);
            panel.add(clockImage);
            panel.add(adjustmentLabel);
        }

        JButton syncButton = new JButton("Sincronizar Relojes");
        syncButton.addActionListener(e -> {
            ExecutorService executor = Executors.newFixedThreadPool(5);
            Random random = new Random();
            List<Future<Long>> futures = new ArrayList<>();

            // Inicializar nodos con tiempos desajustados en un rango más amplio (±2500ms)
            for (int i = 0; i < 5; i++) {
                long localTime = System.currentTimeMillis() + random.nextInt(5000) - 2500; // ±2500ms
                futures.add(executor.submit(new BerkeleyNode(localTime)));
            }

            try {
                long sum = 0;
                List<Long> times = new ArrayList<>();
                List<Long> adjustedTimes = new ArrayList<>();

                // Recuperar tiempos locales y calcular la suma
                for (Future<Long> future : futures) {
                    long nodeTime = future.get();
                    times.add(nodeTime);
                    sum += nodeTime;
                }

                // Calcular el tiempo promedio
                long averageTime = sum / times.size();

                // Calcular ajustes y simular aplicación de ajustes
                for (int i = 0; i < times.size(); i++) {
                    long adjustment = averageTime - times.get(i);
                    long adjustedTime = times.get(i) + adjustment; // Simular aplicación del ajuste
                    adjustedTimes.add(adjustedTime);

                    // Actualizar interfaz con tiempos locales, ajustes y tiempos ajustados
                    nodeLabels.get(i).setText("Nodo " + (i + 1) + ": " + new Date(times.get(i)));
                    adjustmentLabels.get(i).setText("<html>Ajuste: " + adjustment + " ms<br>Nuevo Tiempo: " + new Date(adjustedTime) + "</html>");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error en la sincronización: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                executor.shutdown();
            }
        });

        // Configurar el marco principal
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.add(syncButton, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    /**
     * Método para crear un icono dinámico de reloj
     */
    private static Icon createClockIcon() {
        int size = 50; // Tamaño del icono
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();

        // Dibujar el fondo del reloj (círculo)
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillOval(0, 0, size, size);

        // Dibujar el borde del reloj
        g2d.setColor(Color.BLACK);
        g2d.drawOval(0, 0, size, size);

        // Dibujar las manecillas del reloj
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(size / 2, size / 2, size / 2, size / 4); // Manecilla larga
        g2d.drawLine(size / 2, size / 2, size / 4, size / 2); // Manecilla corta

        g2d.dispose();
        return new ImageIcon(image);
    }
}
