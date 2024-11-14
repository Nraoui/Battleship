package com.dam2024m8uf2.battleship.singleton

import com.dam2024m8uf2.battleship.entitats.Board
import com.dam2024m8uf2.battleship.entitats.Cell
import com.dam2024m8uf2.battleship.entitats.Ship
import java.util.Random

class JocManager private constructor() {
    private val playerBoard: Board = Board(10)
    private val machineBoard: Board = Board(10)
    private val random: Random = Random()

    init {
        setupShips() // Initialize the ships for both boards
    }
    private fun setupShips() {
        // Example ship placements on 10x10 grid. Adjust positions as needed.
        playerBoard.addShip(Ship(2, listOf(Cell(0, 0), Cell(0, 1))))
        playerBoard.addShip(Ship(3, listOf(Cell(1, 0), Cell(1, 1), Cell(1, 2))))
        playerBoard.addShip(Ship(4, listOf(Cell(2, 2), Cell(2, 3), Cell(2, 4), Cell(2, 5))))

        machineBoard.addShip(Ship(2, listOf(Cell(5, 5), Cell(5, 6))))
        machineBoard.addShip(Ship(3, listOf(Cell(6, 4), Cell(6, 5), Cell(6, 6))))
        machineBoard.addShip(Ship(4, listOf(Cell(7, 3), Cell(7, 4), Cell(7, 5), Cell(7, 6))))
    }

    fun playerAttack(x: Int, y: Int): Boolean {
        return machineBoard.attackCell(x, y)
    }

    fun machineAttack(): Boolean {
        val x = random.nextInt(10)
        val y = random.nextInt(10)
        return playerBoard.attackCell(x, y)
    }

    fun isGameOver(): Boolean {
        return playerBoard.allShipsSunk() || machineBoard.allShipsSunk()
    }

    // Get the number of hits for Player 1 (human)
    fun getPlayerHits(): Int {
        return playerBoard.getHits()
    }

    // Get the number of misses for Player 1 (human)
    fun getPlayerMisses(): Int {
        return playerBoard.getMisses()
    }

    // Get the number of hits for Player 2 (machine)
    fun getMachineHits(): Int {
        return machineBoard.getHits()
    }

    // Get the number of misses for Player 2 (machine)
    fun getMachineMisses(): Int {
        return machineBoard.getMisses()
    }
    companion object {
        @Volatile
        private var instance: JocManager? = null
        fun getInstance(): JocManager {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = JocManager()
                    }
                }
            }
            return instance!!
        }
    }

    fun anemAferCoses() = "anem a fer coses"
}
