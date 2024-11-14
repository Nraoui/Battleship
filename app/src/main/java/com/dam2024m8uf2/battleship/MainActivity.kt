package com.dam2024m8uf2.battleship

import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dam2024m8uf2.battleship.singleton.JocManager

class MainActivity : AppCompatActivity() {
    private lateinit var jocManager: JocManager
    private lateinit var tvGameStatus: TextView
    private lateinit var player1Grid: GridLayout
    private lateinit var player2Grid: GridLayout
    private lateinit var tvPlayer1Hits: TextView
    private lateinit var tvPlayer1Misses: TextView
    private lateinit var tvPlayer2Hits: TextView
    private lateinit var tvPlayer2Misses: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Initialize UI elements
        tvGameStatus = findViewById(R.id.tvGameStatus)
        player1Grid = findViewById(R.id.player1Grid)
        player2Grid = findViewById(R.id.player2Grid)
        tvPlayer1Hits = findViewById(R.id.tvPlayer1Hits)
        tvPlayer1Misses = findViewById(R.id.tvPlayer1Misses)
        tvPlayer2Hits = findViewById(R.id.tvPlayer2Hits)
        tvPlayer2Misses = findViewById(R.id.tvPlayer2Misses)


        jocManager = JocManager.getInstance() // Initialize the game manager

        // Set up grids for player 1 and player 2
        inicialitzarTaulers()
    }
    private fun inicialitzarTaulers() {
        // Set up player 1 grid (this could be the player’s ships board)
        configurarGrid(player1Grid, true)
        // Set up player 2 grid (this could be the attack grid)
        configurarGrid(player2Grid, false)
    }

    private fun configurarGrid(grid: GridLayout, esTaulerJugador: Boolean) {
        // Iterate through the rows and columns of the grid and add buttons
        for (i in 0 until grid.rowCount) {
            for (j in 0 until grid.columnCount) {
                val botoCella = Button(this).apply {
                    val x = i
                    val y = j
                    setOnClickListener {
                        if (esTaulerJugador) {
                            // For player grid: Provide feedback that this is the player's board
                            Toast.makeText(this@MainActivity, "Aquest és el tauler del jugador.", Toast.LENGTH_SHORT).show()
                        } else {
                            // For attack grid: Perform an attack
                            val toc = jocManager.playerAttack(x, y) // Player attacks machine
                            text = if (toc) "Tocat!" else "Aigua"
                            actualitzarStats()

                            // Check if the game is over
                            comprovarJocAcabat()

                            // Let the machine attack after the player
                            if (!jocManager.isGameOver()) {
                                machineAttack()
                            }
                        }
                    }
                }
                grid.addView(botoCella)
            }
        }
    }

    private fun comprovarJocAcabat() {
        if (jocManager.isGameOver()) {
            tvGameStatus.text = "Joc Acabat!"
            Toast.makeText(this, "Joc Acabat!", Toast.LENGTH_LONG).show()
        }
    }

    private fun machineAttack() {
        val toc = jocManager.machineAttack() // Machine attacks player
        Toast.makeText(this, if (toc) "La màquina ha tocat!" else "La màquina ha fallat.", Toast.LENGTH_SHORT).show()
        actualitzarStats()
        comprovarJocAcabat()
    }

    private fun actualitzarStats() {
        // Update the hit/miss stats for Player 1 and Player 2 using the new methods
        tvPlayer1Hits.text = "Hits: ${jocManager.getPlayerHits()}"
        tvPlayer1Misses.text = "Misses: ${jocManager.getPlayerMisses()}"
        tvPlayer2Hits.text = "Hits: ${jocManager.getMachineHits()}"
        tvPlayer2Misses.text = "Misses: ${jocManager.getMachineMisses()}"
    }

    fun reiniciarJoc() {
        jocManager = JocManager.getInstance() // Restart the game logic (reinitialize singleton)
        inicialitzarTaulers() // Reinitialize the UI with fresh boards
        tvGameStatus.text = "Player 1's Turn" // Reset game status

        // Reset stats display after restarting the game
        tvPlayer1Hits.text = "Hits: 0"
        tvPlayer1Misses.text = "Misses: 0"
        tvPlayer2Hits.text = "Hits: 0"
        tvPlayer2Misses.text = "Misses: 0"
    }
}