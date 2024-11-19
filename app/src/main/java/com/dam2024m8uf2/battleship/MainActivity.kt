package com.dam2024m8uf2.battleship

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dam2024m8uf2.battleship.singleton.JocManager
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var jocManager: JocManager
    private lateinit var tvGameStatus: TextView
    private lateinit var player1Grid: GridLayout
    private lateinit var player2Grid: GridLayout
    private lateinit var tvPlayer1Hits: TextView
    private lateinit var tvPlayer1Misses: TextView
    private lateinit var tvPlayer2Hits: TextView
    private lateinit var tvPlayer2Misses: TextView

    private var gameId: String = ""
    private var playerName: String = "Player 1" // Player 1 or Player 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        FirebaseApp.initializeApp(this) // Initialize Firebase
        db = FirebaseFirestore.getInstance() // Get Firestore instance

        // Initialize UI elements
        tvGameStatus = findViewById(R.id.tvGameStatus)
        player1Grid = findViewById(R.id.player1Grid)
        player2Grid = findViewById(R.id.player2Grid)
        tvPlayer1Hits = findViewById(R.id.tvPlayer1Hits)
        tvPlayer1Misses = findViewById(R.id.tvPlayer1Misses)
        tvPlayer2Hits = findViewById(R.id.tvPlayer2Hits)
        tvPlayer2Misses = findViewById(R.id.tvPlayer2Misses)

        playerName = "Player 1" // Set dynamically as needed

        if (playerName == "Player 1") {
            createNewGame()
        } else {
            gameId = intent.getStringExtra("GAME_ID") ?: ""
            if (gameId.isEmpty()) {
                Toast.makeText(this, "Game ID is required to join.", Toast.LENGTH_SHORT).show()
                return
            }
            joinGame(gameId)
            loadGameStatus()
        }

        jocManager = JocManager.getInstance()


        // Set up grids for player 1 and player 2
        initializeGrids()
    }

    private fun createNewGame() {
        val newGame = hashMapOf(
            "player1" to hashMapOf(
                "name" to playerName,
                "hits" to 0,
                "misses" to 0
            ),
            "player2" to null,  // Player 2 is null initially
            "turn" to "player1",
            "gameStatus" to "waiting"
        )

        // Create a new game in Firestore
        db.collection("games").add(newGame)
            .addOnSuccessListener { documentReference ->
                gameId = documentReference.id
                tvGameStatus.text = "Game created! Share this ID: $gameId"
                Toast.makeText(this, "Game created! Share this ID to play.", Toast.LENGTH_SHORT).show()

                saveGameStatus() // Save game status after creating the game
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error creating game: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun joinGame(gameId: String) {
        val gameRef = db.collection("games").document(gameId)

        gameRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists() && documentSnapshot.get("player2") == null) {
                // Player 2 joins the game
                val gameData = hashMapOf("player2" to hashMapOf("name" to playerName, "hits" to 0, "misses" to 0))
                gameRef.update(gameData as Map<String, Any>)
                    .addOnSuccessListener {
                        tvGameStatus.text = "Joined as Player 2!"
                        Toast.makeText(this, "Joined as Player 2", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Game is full or doesn't exist.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Save the game status to Firestore
    fun saveGameStatus() {
        val gameStatus = hashMapOf(
            "player1Hits" to jocManager.getPlayerHits(),
            "player1Misses" to jocManager.getPlayerMisses(),
            "player2Hits" to jocManager.getMachineHits(),
            "player2Misses" to jocManager.getMachineMisses()
        )

        db.collection("games").document(gameId) // Save to specific game document
            .set(gameStatus)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Game status saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving game status: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Load the game status from Firestore
    fun loadGameStatus() {
        if (gameId.isEmpty()) {
            Toast.makeText(this, "Error: Game ID is not set!", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("games").document(gameId) // Use gameId to refer to the correct document
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Extract data safely
                    val player1Hits = documentSnapshot.getLong("player1Hits") ?: 0
                    val player1Misses = documentSnapshot.getLong("player1Misses") ?: 0
                    val player2Hits = documentSnapshot.getLong("player2Hits") ?: 0
                    val player2Misses = documentSnapshot.getLong("player2Misses") ?: 0

                    // Update UI elements
                    tvPlayer1Hits.text = "Hits: $player1Hits"
                    tvPlayer1Misses.text = "Misses: $player1Misses"
                    tvPlayer2Hits.text = "Hits: $player2Hits"
                    tvPlayer2Misses.text = "Misses: $player2Misses"
                } else {
                    Toast.makeText(this, "Game does not exist!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading game status: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Set up the grids for player 1 and player 2
    private fun initializeGrids() {
        setupGrid(player1Grid, true)  // Player 1's grid
        setupGrid(player2Grid, false) // Player 2's grid (this could be the attack grid)
    }

    // Set up individual grid
    private fun setupGrid(grid: GridLayout, isPlayerGrid: Boolean) {
        for (i in 0 until grid.rowCount) {
            for (j in 0 until grid.columnCount) {
                val button = Button(this).apply {
                    val x = i
                    val y = j
                    setOnClickListener {
                        if (isPlayerGrid) {
                            Toast.makeText(this@MainActivity, "This is your ship's grid", Toast.LENGTH_SHORT).show()
                        } else {
                            val hit = jocManager.playerAttack(x, y) // Player attacks
                            text = if (hit) "Hit!" else "Miss!"
                            updateStats()
                            saveGameStatus() // Save game status after player attack

                            if (jocManager.isGameOver()) {
                                checkGameOver()
                            } else {
                                machineAttack() // Let the machine attack after the player
                            }
                        }
                    }
                }
                grid.addView(button)
            }
        }
    }

    // Check if the game is over
    private fun checkGameOver() {
        if (jocManager.isGameOver()) {
            tvGameStatus.text = "Game Over!"
            Toast.makeText(this, "Game Over!", Toast.LENGTH_LONG).show()
        }
    }

    // Machine attacks after player
    private fun machineAttack() {
        val hit = jocManager.machineAttack() // Machine attacks player
        Toast.makeText(this, if (hit) "Machine hit!" else "Machine missed.", Toast.LENGTH_SHORT).show()
        updateStats()
        saveGameStatus() // Save game status after machine attack
        checkGameOver()
    }

    // Update the stats in the UI
    private fun updateStats() {
        tvPlayer1Hits.text = "Hits: ${jocManager.getPlayerHits()}"
        tvPlayer1Misses.text = "Misses: ${jocManager.getPlayerMisses()}"
        tvPlayer2Hits.text = "Hits: ${jocManager.getMachineHits()}"
        tvPlayer2Misses.text = "Misses: ${jocManager.getMachineMisses()}"
    }

    // Reset the game
    fun resetGame(view: View) {
        jocManager = JocManager.getInstance() // Reset the game logic (reinitialize singleton)
        tvGameStatus.text = "Player 1's Turn" // Reset game status

        // Reset the stats display after restarting the game
        tvPlayer1Hits.text = "Hits: 0"
        tvPlayer1Misses.text = "Misses: 0"
        tvPlayer2Hits.text = "Hits: 0"
        tvPlayer2Misses.text = "Misses: 0"
    }
}
