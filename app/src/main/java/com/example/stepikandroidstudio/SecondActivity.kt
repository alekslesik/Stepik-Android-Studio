package com.example.stepikandroidstudio

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import kotlinx.android.synthetic.main.second_activity.*

class SecondActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //задает UI который будет изображен на экране
        setContentView(R.layout.second_activity)
        // находим эдиттекст из secondactivity.xml
        val vEdit = findViewById<EditText>(R.id.act2_edit)
        // принимаем с интентом данные от mainactivity
        val str = intent.getStringExtra("tag1")
        // добавляем строку str d в эдиттекст
        vEdit.setText(str)

        // ищем кнопку
        findViewById<Button>(R.id.act2_button).setOnClickListener {
            //достаем из эдита что там написано
            val newStr = vEdit.text.toString()
            // создаем интент
            val i = Intent()
            //кладем в интент наши данные
            i.putExtra("tag2", newStr)
            //!!!посмотеть функцию
            setResult(0, i)
            // по нажатию кнопки ок закрываем активити
            finish()
        }
    }
}