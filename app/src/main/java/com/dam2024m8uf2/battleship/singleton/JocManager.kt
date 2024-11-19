package com.dam2024m8uf2.battleship.singleton

import com.dam2024m8uf2.battleship.entitats.Board
import com.dam2024m8uf2.battleship.entitats.Cell
import com.dam2024m8uf2.battleship.entitats.Ship
import java.util.Random

class JocManager private constructor() {
    private val playerBoard: Board = Board(3)
    private val machineBoard: Board = Board(3)
    private val random: Random = Random()

    init {
        setupShips() // Initialize the ships for both boards
    }
    private fun setupShips() {
        // Adjusted ship placements for a 3x3 grid. Only ships of size 1x2 or 1x3 can fit.
        playerBoard.addShip(Ship(2, listOf(Cell(0, 0), Cell(0, 1))))  // 1x2 ship
        playerBoard.addShip(Ship(3, listOf(Cell(1, 0), Cell(1, 1), Cell(1, 2))))  // 1x3 ship

        machineBoard.addShip(Ship(2, listOf(Cell(1, 0), Cell(1, 1))))  // 1x2 ship
        machineBoard.addShip(Ship(3, listOf(Cell(0, 0), Cell(0, 1), Cell(0, 2))))  // 1x3 ship
    }

    fun playerAttack(x: Int, y: Int): Boolean {
        return machineBoard.attackCell(x, y)
    }

    fun machineAttack(): Boolean {
        val x = random.nextInt(3)  // Random x from 0 to 2
        val y = random.nextInt(3)  // Random y from 0 to 2
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
    // Method to serialize the game state to a Map<String, Any>
    fun getGameStateMap(): Map<String, Any> {
        return mapOf(
            "playerBoard" to playerBoard.getState(),
            "machineBoard" to machineBoard.getState(),
            "playerHits" to getPlayerHits(),
            "playerMisses" to getPlayerMisses(),
            "machineHits" to getMachineHits(),
            "machineMisses" to getMachineMisses()
        )
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
