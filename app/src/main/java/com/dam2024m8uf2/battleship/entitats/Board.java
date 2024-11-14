package com.dam2024m8uf2.battleship.entitats;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private Cell[][] cells;
    private List<Ship> ships;

    public Board(int size) {
        cells = new Cell[size][size];
        ships = new ArrayList<>();
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
        return ships.stream().anyMatch(ship -> ship.isHit(cell));
    }

    public boolean allShipsSunk() {
        return ships.stream().allMatch(Ship::isSunk);
    }
}
