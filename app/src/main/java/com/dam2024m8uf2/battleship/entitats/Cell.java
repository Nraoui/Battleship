package com.dam2024m8uf2.battleship.entitats;

import java.util.Map;

public class Cell {
    private int x, y;
    private boolean hit;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.hit = false;
    }

    public void markHit() {
        this.hit = true;
    }

    public boolean isHit() {
        return hit;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cell cell = (Cell) obj;
        return x == cell.x && y == cell.y;
    }
    // Convert Cell state to a Map for Firestore
    public Map<String, Object> getState() {
        return Map.of(
                "x", x,
                "y", y,
                "hit", hit
        );
    }

}
