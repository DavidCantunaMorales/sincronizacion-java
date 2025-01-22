package com.espe;

import java.util.concurrent.Callable;

public class BerkeleyNode implements Callable<Long> {
    // Tiempo local del nodo (en milisegundos)
    private final long localTime;

    // Constructor que inicializa el nodo con su tiempo local
    public BerkeleyNode(long localTime) {
        this.localTime = localTime;
    }

    // Método que devuelve el tiempo local del nodo cuando es llamado
    @Override
    public Long call() {
        return localTime;
    }
}
