package com.dam2024m8uf2.battleship.entitats;

import java.util.List;

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
}
