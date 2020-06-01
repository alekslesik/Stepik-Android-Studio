package com.example.stepikandroidstudio

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.rxkotlin.zipWith
import java.lang.RuntimeException
import java.net.HttpURLConnection
import java.net.URL
import com.google.gson.Gson

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

    //создаем переменную для потоков reactivex
    var request: Disposable? = null

    //делаем переменную класса посмотреть lateinit!!!
    lateinit var vText: TextView

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
//            val i = Intent(this, SecondActivity::class.java)

            // передаем с интентом в SecondActivity текст Hello word
//            i.putExtra("tag1", vText.text)

            //передаем intent в startActivity. Launch a new activity
            //startActivity (Intent intent,Bundle options)
            //startActivity(i) или если ждем результат от activity2 то

//            startActivityForResult(i, 0)

            //работа с сетью как это надо черех reactivex
            //создаем класс, передаем лямбда ф-ию

            //после получения результата из сети вызываем next и передаем туда то, что получили из сети
            //it.onNext("qq")
            //теперь нужно выбрать в каком потоке будем исполнять, а в каком потоке получать результат
            //в данном случае исполнение будет в каком то заранее созданном потоке io, а результат получим в нашем главном UI потоке
            //оператор flatMap позволяет создать последюущий поток со своим Observable.create
            //оператор zipWith позволяет делать параллельный поток
            val o =
                createRequest("https://api.rss2json.com/v1/api.json?rss_url=http%3A%2F%2Ffeeds.bbci.co.uk%2Fnews%2Frss.xml")
                    //преобразовываем полученную строку в объект Feed
                    .map { Gson().fromJson(it, Feed::class.java) }
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

            //запускаем наш поток где первым передаем лямда ф-ию, в которой получим результат от ooNext
            //а вторая лямбда ф-ия обработка исключений
            //записываем результат в переменную request и используем в колбеке onDestroy. Это нужно для предотврощения потери памяти
            request = o.subscribe({
                for (item in it.items) {
                    Log.w("test", "title:${item.title}")
                }
            }, {
                //тут обрабатываем ошибки
                Log.e("test", "", it)

            })


            //Классический метод создания потока (так делать не надо).Создаем поток threat
//            val t = object : Thread() {
//                //!!посмотреть run
//                override fun run() {
//                    //TODO обращение в сеть
//                    //после того как сделаем обращение в сеть, возвращаемся в UI поток
//                    this@MainActivity.runOnUiThread {
//                        //тут мы можем вернуть в UI полученные данные из сети и тправить в activity
//                    }
//                }
//            }
//            //запускаем thread
//            t.start()
//            //см класс AT
//            AT(this).execute()
        }
        Log.v("tag", "Был запущен onCreate")
    }

    //колбек для возвращаемых данных
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //проверяем вернулось ли что нибудь или нет
        if (data != null) {
            //извлекаем из данных data строку
            val str = data.getStringExtra("tag2")
            // и применяем ее в наш текст
            //кладем в переменную класса нашу строку
            vText.text = str
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
        //в результате вызова dispose() наша цепочка вызовов потоков будет оборвано и все подчистится
        request?.dispose()
        super.onDestroy()
    }
}

class Feed(val items: ArrayList<FeedItem>)

class FeedItem(
    val title: String,
    val link: String,
    val thumbnail: String,
    val description: String
)


////упрощенный способ создания потока от гугла(так тоже делать не надо)
//class AT(val act:MainActivity): AsyncTask<String, Int, String>() {
//    //внутри сначала переопределяем doInBackground, который открывает поток в не UI
//    override fun doInBackground(vararg params: String?): String {
//        //обращаемся в сеть в поток не UI и что то возвращаем
//        return ""
//    }
//    //теперь переопределяем колбек который принимает результат из doInBackground и отправляет в UI
//    override fun onPostExecute(result: String?) {
//        super.onPostExecute(result)
//        //тут уже можем обращаться к полям mainactivity
//    }
//
//}
