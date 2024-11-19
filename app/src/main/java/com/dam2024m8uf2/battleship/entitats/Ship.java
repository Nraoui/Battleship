package com.dam2024m8uf2.battleship.entitats;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Ship {
    private int size;
    private List<Cell> positions;

    public Ship(int size, List<Cell> positions) {
        this.size = size;
        this.positions = positions;
    }

    public boolean isHit(Cell cell) {
        return positions.contains(cell);
    }

    public boolean isSunk() {
        return positions.stream().allMatch(Cell::isHit);
    }
    // Convert Ship state to a Map for Firestore
    public Map<String, Object> getState() {
        return Map.of(
                "size", size,
                "positions", positions.stream().map(Cell::getState).collect(Collectors.toList())
        );
    }
}
