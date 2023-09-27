package com.example.tictactoe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class GameViewModel : ViewModel() {
    var state by mutableStateOf(GameState())
    var lastestTurn = BoardCellValue.CIRCLE
    val boardItems: MutableMap<Int, BoardCellValue> = mutableMapOf(
        1 to BoardCellValue.NONE,
        2 to BoardCellValue.NONE,
        3 to BoardCellValue.NONE,
        4 to BoardCellValue.NONE,
        5 to BoardCellValue.NONE,
        6 to BoardCellValue.NONE,
        7 to BoardCellValue.NONE,
        8 to BoardCellValue.NONE,
        9 to BoardCellValue.NONE,
    )

    fun onAction(action: UserAction) {
        when (action) {
            is UserAction.BoardTapped -> {
                if(state.currentTurn == BoardCellValue.CIRCLE){
                    addValueToBoard(action.cellNo)
                    computerMove()
                }
            }

            UserAction.PlayAgainButtonClicked -> {
                gameReset()
            }
        }
    }

    private fun gameReset() {
        boardItems.forEach { (i, _) ->
            boardItems[i] = BoardCellValue.NONE
        }
        if(lastestTurn == BoardCellValue.CIRCLE){
            lastestTurn = BoardCellValue.CROSS
            state = state.copy(
                hintText = "Player 'X' turn",
                currentTurn = BoardCellValue.CROSS,
                victoryType = VictoryType.NONE,
                hasWon = false
            )
            computerMove()
        }
        else {
            lastestTurn = BoardCellValue.CIRCLE
            state = state.copy(
                hintText = "Player 'O' turn",
                currentTurn = BoardCellValue.CIRCLE,
                victoryType = VictoryType.NONE,
                hasWon = false
            )
        }
    }

    private fun addValueToBoard(cellNo: Int) {
        if (boardItems[cellNo] != BoardCellValue.NONE) {
            return
        }
        if (state.currentTurn == BoardCellValue.CIRCLE) {
            boardItems[cellNo] = BoardCellValue.CIRCLE
            state = if (checkForVictory(BoardCellValue.CIRCLE)) {
                state.copy(
                    hintText = "Player 'O' Won",
                    playerCircleCount = state.playerCircleCount + 1,
                    currentTurn = BoardCellValue.NONE,
                    hasWon = true
                )
            } else if (hasBoardFull()) {
                state.copy(
                    hintText = "Game Draw",
                    drawCount = state.drawCount + 1
                )
            } else {
                state.copy(
                    hintText = "Player 'X' turn",
                    currentTurn = BoardCellValue.CROSS
                )
            }
        } else if (state.currentTurn == BoardCellValue.CROSS) {
            boardItems[cellNo] = BoardCellValue.CROSS
            state = if (checkForVictory(BoardCellValue.CROSS)) {
                state.copy(
                    hintText = "Player 'X' Won",
                    playerCrossCount = state.playerCrossCount + 1,
                    currentTurn = BoardCellValue.NONE,
                    hasWon = true
                )
            } else if (hasBoardFull()) {
                state.copy(
                    hintText = "Game Draw",
                    drawCount = state.drawCount + 1
                )
            } else {
                state.copy(
                    hintText = "Player 'O' turn",
                    currentTurn = BoardCellValue.CIRCLE
                )
            }
        }
    }

    private fun computerMove(){
        if(state.currentTurn == BoardCellValue.CROSS){
            when{
                canWin()  in 1..9 -> {
                    addValueToBoard(canWin())
                }
                canLose() in 1..9 ->{
                        addValueToBoard(canLose())
                }
                boardItems[5] == BoardCellValue.NONE -> {
                    addValueToBoard(5)
                }
                else -> addValueToBoard(randomPlay())
            }
        }
    }

    private fun randomPlay(): Int {
        while (true) {
            val randomIndex = Random.nextInt(1, 10)

            if (boardItems[randomIndex] == BoardCellValue.NONE) {
                return randomIndex
            }
        }
    }


    private fun canWin() : Int{
        return checkLastChance(BoardCellValue.CROSS)
    }

    private fun canLose() : Int{
        return checkLastChance(BoardCellValue.CIRCLE)
    }

    private fun checkLastChance(boardValue: BoardCellValue) : Int {
        val conditions = listOf(
            Triple(1, 2, 3), Triple(1, 3, 2), Triple(3, 2, 1),
            Triple(5, 6, 3), Triple(4, 6, 5), Triple(4, 5, 6),
            Triple(8, 9, 7), Triple(7, 9, 8), Triple(7, 8, 9),
            Triple(4, 7, 1), Triple(1, 7, 4), Triple(1, 4, 7),
            Triple(5, 8, 2), Triple(2, 8, 5), Triple(2, 5, 8),
            Triple(6, 9, 3), Triple(3, 9, 6), Triple(3, 6, 9),
            Triple(5, 9, 1), Triple(1, 9, 5), Triple(1, 5, 9),
            Triple(3, 7, 5), Triple(5, 7, 3), Triple(3, 5, 7),
        )

        for ((a, b, c) in conditions) {
            if (boardItems[a] == boardValue && boardItems[b] == boardValue && boardItems[c] == BoardCellValue.NONE) {
                return c
            }
        }

        return 0
    }

    private fun checkForVictory(boardValue: BoardCellValue): Boolean {
        when {
            boardItems[1] == boardValue && boardItems[2] == boardValue && boardItems[3] == boardValue -> {
                state = state.copy(victoryType = VictoryType.HORIZONTAL1)
                return true
            }

            boardItems[4] == boardValue && boardItems[5] == boardValue && boardItems[6] == boardValue -> {
                state = state.copy(victoryType = VictoryType.HORIZONTAL2)
                return true
            }

            boardItems[7] == boardValue && boardItems[8] == boardValue && boardItems[9] == boardValue -> {
                state = state.copy(victoryType = VictoryType.HORIZONTAL3)
                return true
            }

            boardItems[1] == boardValue && boardItems[4] == boardValue && boardItems[7] == boardValue -> {
                state = state.copy(victoryType = VictoryType.VERTICAL1)
                return true
            }

            boardItems[2] == boardValue && boardItems[5] == boardValue && boardItems[8] == boardValue -> {
                state = state.copy(victoryType = VictoryType.VERTICAL2)
                return true
            }

            boardItems[3] == boardValue && boardItems[6] == boardValue && boardItems[9] == boardValue -> {
                state = state.copy(victoryType = VictoryType.VERTICAL3)
                return true
            }

            boardItems[1] == boardValue && boardItems[5] == boardValue && boardItems[9] == boardValue -> {
                state = state.copy(victoryType = VictoryType.DIAGONAL1)
                return true
            }

            boardItems[3] == boardValue && boardItems[5] == boardValue && boardItems[7] == boardValue -> {
                state = state.copy(victoryType = VictoryType.DIAGONAL2)
                return true
            }

            else -> return false
        }
    }

    private fun hasBoardFull(): Boolean {
        return !boardItems.containsValue(BoardCellValue.NONE)
    }
}