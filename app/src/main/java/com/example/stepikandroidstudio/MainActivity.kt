package com.example.stepikandroidstudio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    /**
     * Activity — это компонент приложения (базовый класс), который содержит все необходимые ф-ии для работы с UI.
     * Создается исключительно системой и нам нужно только переопределять коллбек ф-ии, что бы понять что происходит
     * Activity выдает экран, с которым пользователи могут
     * взаимодействовать для выполнения каких-либо действий, например набрать номер телефона, сделать фото,
     * отправить письмо или просмотреть карту. Каждой операции присваивается окно для прорисовки
     * соответствующего пользовательского интерфейса. Обычно окно отображается во весь экран, однако его
     * размер может быть меньше, и оно может размещаться поверх других окон.
     * https://developer.android.com/guide/components/activities?hl=ru
     */

    /*Переопределенная функция.
        Роль ф-ии - создать само изображение UI;
        первый колбек который будет вызван при открытии нашего экрана;
        коллбек будет вызван гарантированно;
    */

    //делаем переменную класса посмотреть lateinit!!!
    lateinit var vText:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //задает UI который будет изображен на экране
        setContentView(R.layout.activity_main)

        //параметр - имя класса(тип), id из разметки
        //результат работы ф - ссылка на класс TextView
        vText = findViewById<TextView>(R.id.act1_text)

        //задать цвет текста
        vText.setTextColor(0xFFFF0000.toInt())

        //установить перехватчик нажатий на элемент
        vText.setOnClickListener {
            Log.e("tag", "НАЖАТА КНОПКА")

            //код для открытия SecondActivity. создаем intent
            val i = Intent(this, SecondActivity::class.java)

            // передаем с интентом в SecondActivity текст Hello word
            i.putExtra("tag1", vText.text)

            //передаем intent в startActivity. Launch a new activity
            // startActivity (Intent intent,Bundle options)
            //startActivity(i) или если ждем результат от activity2 то
            startActivityForResult(i,0)
        }
        Log.v("tag", "Был запущен onCreate")
    }

    //колбек для возвращаемых данных
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //проверяем вернулось ли что нибудь или нет
        if(data!=null) {
            //извлекаем из данных data строку
            val str=data.getStringExtra("tag2")
            // и применяем ее в наш текст
            //кладем в переменную класса нашу строку
            vText.text=str

        }
    }


    //экран стал виден пользователю (можно проигнорировать в 99% случаях)
    override fun onStart() {
        super.onStart()
    }

    // экран стал активен, работает
    override fun onResume() {
        super.onResume()
    }

    /*при нажатии кнопку дом, назад, телефонный звонок и тд;
       Здесь надо остановить все что запущенно в onResume;
       быть готовым к тому что после этого коллбека ничего не будет вызвано т.к. система может
       молча все убить;
    */
    override fun onPause() {
        super.onPause()
    }

    //аналогично onPause, редко используется, в основном onPause
    override fun onStop() {
        super.onStop()
    }

    //окончательно удаляется экземпляр класса системой
    override fun onDestroy() {
        super.onDestroy()
    }

}
