package com.dam2024m8uf2.battleship.entitats;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private Cell[][] cells;
    private List<Ship> ships;
    private int hitsCount;  // Count of hits
    private int missesCount;  // Count of misses

    public Board(int size) {
        cells = new Cell[size][size];
        ships = new ArrayList<>();
        hitsCount = 0;
        missesCount = 0;
        initializeCells();
    }

    private void initializeCells() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
    }

    public void addShip(Ship ship) {
        ships.add(ship);
    }

    public boolean attackCell(int x, int y) {
        Cell cell = cells[x][y];
        cell.markHit();
        if (ships.stream().anyMatch(ship -> ship.isHit(cell))) {
            hitsCount++;  // Increment hits if it's a hit
            return true;
        } else {
            missesCount++;  // Increment misses if it's a miss
            return false;
        }
    }

    public boolean allShipsSunk() {
        return ships.stream().allMatch(Ship::isSunk);
    }

    // Method to get the number of hits on the board
    public int getHits() {
        return hitsCount;
    }

    // Method to get the number of misses on the board
    public int getMisses() {
        return missesCount;
    }
}
