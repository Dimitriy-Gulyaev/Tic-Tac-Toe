package com.example.tictactoe

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_game.*


class Game : AppCompatActivity(), View.OnClickListener {

    // Настройки игры (переменные для SharedPreferences)
    private val APP_PREFERENCES = "mySettings"
    private val APP_PREFERENCES_FIELD_SIZE = "fieldSize"
    private val APP_PREFERENCES_FIELDS_TO_WIN = "fieldsToWin"
    var mSettings: SharedPreferences? = null

    // Основные переменные игры
    private lateinit var buttonField: ArrayList<String?> // Игровое поле (текстовые символы)
    private var currentButton = -1 // Текущая кнопка
    private var fieldSize = 3 // Размер игрового поля (по осям, поле квадратное)
    private var fieldsToWin = 3 // Количество полей, которые нужно покрыть одним символом
    private lateinit var buttons: Array<Array<Button?>>   // Массив кнопок поля
    private var playerTurn: Boolean = true // Ход игрока (true - 1-й игрок, false - 2-й)
    private var roundCountMax = fieldSize * fieldSize // Количество игровых полей
    private var roundCount = 0 // Счётчик полей для ничьи
    private var player1Score: Int = 0 // Счёт 1-го игрока
    private var player2Score: Int = 0 // Счёт 2-го игрока

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Получаем объект SharedPreferences
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

        // Если сохранённого значения нет, устанавливаем 3 поля
        if (with(mSettings) { this?.contains(APP_PREFERENCES_FIELD_SIZE) }!!) {
            fieldSize = with(mSettings) {
                this!!.getInt(
                    APP_PREFERENCES_FIELD_SIZE,
                    3
                )
            }
        }
        if (with(mSettings) { this?.contains(APP_PREFERENCES_FIELDS_TO_WIN) }!!) {
            fieldsToWin = with(mSettings) {
                this!!.getInt(
                    APP_PREFERENCES_FIELDS_TO_WIN,
                    3
                )
            }
        }

        buttons =
            Array(
                fieldSize
            ) { arrayOfNulls<Button>(fieldSize) }
        roundCountMax = fieldSize * fieldSize

        // Кнопка новой игры
        newGame.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Предупреждение")
                .setMessage("Вы действительно хотите начать новую игру?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(
                    "Да"
                ) { _, _ ->
                    player1Score = 0
                    player2Score = 0
                    score.text = "SCORE: $player1Score:$player2Score"
                    resetBoard()
                }
                .setNegativeButton(
                    "Нет"
                ) { _, _ -> }.show()
        }

        // Родительский контейнер для помещения "строк" поля - виджетов LinearLayout
        val parent = findViewById<LinearLayout>(R.id.parent)

        // Динамическая отрисовка
        for (i in 0 until fieldSize) {
            // Создаём новый контейнер LinearLayout - "строку" игрового поля
            val layout = LinearLayout(applicationContext)
            layout.id = i
            // Параметры контейнера
            val layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, 1.0f)
            layout.layoutParams = layoutParams
            layout.orientation = HORIZONTAL
            layout.setPadding(-5, -10, -5, -10)
            // Добавляем новый контейнер в родительский
            parent.addView(layout)
            for (j in 0 until fieldSize) {
                // Создаём новую кнопку - ячейку игрового поля
                val button = Button(applicationContext)
                // Автоскалирование текста, complex unit: Value is Device Independent Pixels.
                button.setAutoSizeTextTypeUniformWithConfiguration(
                    1,
                    80,
                    1,
                    TypedValue.COMPLEX_UNIT_DIP
                )
                button.setPadding(-3, -3, -3, -3)
                button.id = i * 100 + j
                // Добалвяем кнопку в массив кнопок
                buttons[i][j] = button
                // Создаём слушатель для кнопки
                buttons[i][j]!!.setOnClickListener(this)
                // Параметры кнопки
                val buttonParams = LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT, 1.0f)
                button.layoutParams = buttonParams
                buttonParams.setMargins(-6, 0, -6, 0)
                // Добавляем кнопку в созданный контейнер
                layout.addView(button, buttonParams)
            }
        }

        // Восстановление активности
        if (savedInstanceState != null) {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            buttonField = savedInstanceState.getStringArrayList("buttons")
            for (i in 0 until fieldSize)
                for (j in 0 until fieldSize)
                    buttons[i][j]?.text = if (buttonField[i]?.get(j).toString() == "_") ""
                    else buttonField[i]?.get(j).toString()

            playerTurn = savedInstanceState.getBoolean("playerTurn")
            roundCount = savedInstanceState.getInt("roundCount")
            player1Score = savedInstanceState.getInt("player1Score")
            player2Score = savedInstanceState.getInt("player2Score")
            currentButton = savedInstanceState.getInt("currentButton")
        }

        // Кнопка отмены текущего хода
        if (currentButton == -1) {
            undo.visibility = View.INVISIBLE
        } else {
            undo.visibility = View.VISIBLE
        }
        undo.setOnClickListener {
            if (currentButton != -1) {
                val i = currentButton / 100
                val j = currentButton % 100
                buttons[i][j]?.text = ""
                roundCount--
                playerTurn = !playerTurn
                turn.text = "TURN: ${if (playerTurn) "X" else "O"}"
                currentButton = -1
                undo.visibility = View.INVISIBLE
            }
        }

        score.text = "SCORE: $player1Score:$player2Score"
        turn.text = "TURN: ${if (playerTurn) "X" else "O"}"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        buttonField = ArrayList(fieldSize)
        for (i in 0 until fieldSize) {
            val stringBuilder = StringBuilder()
            for (j in 0 until fieldSize) {
                stringBuilder.append(
                    if (buttons[i][j]?.text == "") "_"
                    else buttons[i][j]?.text
                )
            }
            buttonField.add(stringBuilder.toString())
        }
        outState.putStringArrayList("buttons", buttonField)
        outState.putBoolean("playerTurn", playerTurn)
        outState.putInt("roundCount", roundCount)
        outState.putInt("player1Score", player1Score)
        outState.putInt("player2Score", player2Score)
        outState.putInt("currentButton", currentButton)
    }

    override fun onBackPressed() {
        if (roundCount == 0 && player1Score == 0 && player2Score == 0) {
            super.onBackPressed()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Предупреждение")
                .setMessage("Вы действительно хотите покинуть игру?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(
                    "Да"
                ) { _, _ -> finish() }
                .setNegativeButton(
                    "Нет"
                ) { _, _ -> }.show()
        }
    }

    // Слушатель для кнопки - ячейки поля
    override fun onClick(button: View?) {
        if ((button as Button).text.toString() != "") {
            return
        }

        if (playerTurn) {
            button.text = "X"
            turn.text = "TURN: O"
        } else {
            button.text = "O"
            turn.text = "TURN: X"
        }

        currentButton = button.id
        undo.visibility = View.VISIBLE
        roundCount++

        if (checkForWin()) {
            if (playerTurn) {
                player1Wins()
            } else {
                player2Wins()
            }
        } else if (roundCount == roundCountMax) { // Условие ничьей
            draw()
        } else {
            playerTurn = !playerTurn // Ход другого игрока
        }
    }

    // Проверка условий победы
    private fun checkForWin(): Boolean {
        val field = buttons.map { bArr -> bArr.map { it!!.text } }
        var count: Int
        val ci = currentButton / 100 // Индекс строки текущей кнопки в массиве
        val cj = currentButton % 100 // Индекс столбца текущей кнопки в массиве
        // Проход по строке, в которой находится текущая кнопка
        count = 1
        for (j in 0 until fieldSize - 1) {
            if (field[ci][j] != "" && field[ci][j] == field[ci][j + 1]) {
                count++
                if (count == fieldsToWin) return true
            } else count = 1
        }
        // Проход по столбцу
        count = 1
        for (i in 0 until fieldSize - 1) {
            if (field[i][cj] != "" && field[i][cj] == field[i + 1][cj]) {
                count++
                if (count == fieldsToWin) return true
            } else count = 1
        }
        // Проход по главной диагонали области, в которой находится текущая кнопка
        count = 1
        var i = ci
        var j = cj
        while (i > 0 && j > 0) {
            i--
            j--
        }
        while (i < fieldSize - 1 && j < fieldSize - 1) {
            if (field[i][j] != "" && field[i][j] == field[i + 1][j + 1]) {
                count++
                if (count == fieldsToWin) return true
            } else count = 1
            i++
            j++
        }
        // Проход по побочной диагонали
        count = 1
        i = ci
        j = cj
        while (i > 0 && j < fieldSize - 1) {
            i--
            j++
        }
        while (i < fieldSize - 1 && j > 0) {
            if (field[i][j] != "" && field[i][j] == field[i + 1][j - 1]) {
                count++
                if (count == fieldsToWin) return true
            } else count = 1
            i++
            j--
        }
        return false
    }

    // Победа первого игрока. Оповещение, изменение счёта, сброс игрового поля
    private fun player1Wins() {
        Toast.makeText(this, "\"X\" WINS THIS ROUND", Toast.LENGTH_SHORT).show()
        resetBoard()
        player1Score++
        score.text = "SCORE: $player1Score:$player2Score"
    }

    // Победа второго игрока
    private fun player2Wins() {
        Toast.makeText(this, "\"O\" WINS THIS ROUND", Toast.LENGTH_SHORT).show()
        resetBoard()
        player2Score++
        score.text = "SCORE: $player1Score:$player2Score"
    }

    // Ничья
    private fun draw() {
        Toast.makeText(this, "DRAW", Toast.LENGTH_SHORT).show()
        resetBoard()
    }

    // Функция сброса игрового поля
    private fun resetBoard() {
        for (i in 0 until fieldSize) {
            for (j in 0 until fieldSize) {
                buttons[i][j]!!.text = ""
            }
        }
        roundCount = 0
        playerTurn = true
        turn.text = "TURN: X"
        currentButton = 0
        undo.visibility = View.INVISIBLE
    }
}
