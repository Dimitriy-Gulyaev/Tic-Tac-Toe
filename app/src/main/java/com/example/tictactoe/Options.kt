package com.example.tictactoe


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_options.*


class Options : Fragment() {

    var fieldSize = 3
    var fieldsToWin = 3

    private val APP_PREFERENCES = "mySettings"
    private val APP_PREFERENCES_FIELD_SIZE = "fieldSize"
    private val APP_PREFERENCES_FIELDS_TO_WIN = "fieldsToWin"
    var mSettings: SharedPreferences? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Получаем объект SharedPreferences
        mSettings = activity!!.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_options, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        textFieldSize.text = "Field Size : $fieldSize"
        textFieldsToWin.text = "Fields to Win : $fieldsToWin"
        seekBarFieldSize.progress = fieldSize - 3
        seekBarFieldsToWin.progress = fieldsToWin - 3

        seekBarFieldSize.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (i + 3 >= fieldsToWin) {
                    textFieldSize.text = "Field Size : ${i + 3}"
                    fieldSize = i + 3
                } else {
                    seekBar.progress = fieldsToWin - 3
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

        seekBarFieldsToWin.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (i + 3 <= fieldSize) {
                    textFieldsToWin.text = "Fields to Win : ${i + 3}"
                    fieldsToWin = i + 3
                } else {
                    seekBar.progress = fieldSize - 3
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

    }

    override fun onStop() {
        super.onStop()
        // Сохраняем наши настройки
        val editor = mSettings?.edit()
        editor?.putInt(APP_PREFERENCES_FIELD_SIZE, fieldSize)
        editor?.putInt(APP_PREFERENCES_FIELDS_TO_WIN, fieldsToWin)
        editor?.apply()
    }
}
