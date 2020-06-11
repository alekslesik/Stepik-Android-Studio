package com.example.stepikandroidstudio

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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

    //делаем переменную класса
    lateinit var vText: TextView

    //пееременная для простого отображения данных из массива
    lateinit var vList: LinearLayout
    lateinit var vListView: ListView
    lateinit var vRecView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //задает UI который будет изображен на экране
        setContentView(R.layout.activity_main)
        //параметр - имя класса(тип), id из разметки
        //результат работы ф - ссылка на класс TextView
//        vText = findViewById<TextView>(R.id.act1_text)
//        vList = findViewById<LinearLayout>(R.id.act1_list)
        vRecView = findViewById<RecyclerView>(R.id.act1_recView)
//        vListView = findViewById<ListView>(R.id.act1_listView)
        //задать цвет текста
//        vText.setTextColor(0xFFFF0000.toInt())
        //установить перехватчик нажатий на элемент
//        vText.setOnClickListener {
//            Log.e("tag", "НАЖАТА КНОПКА")

        //код для открытия SecondActivity. создаем intent
//            val i = Intent(this, SecondActivity::class.java)

        // передаем с интентом в SecondActivity текст Hello word
//            i.putExtra("tag1", vText.text)

        //передаем intent в startActivity. Launch a new activity
        //startActivity (Intent intent,Bundle options)
        //startActivity(i) или если ждем результат от activity2 то

//            startActivityForResult(i, 0)
        //после получения результата из сети вызываем next и передаем туда то, что получили из сети
        //it.onNext("qq")

        //в данном случае исполнение будет в каком то заранее созданном потоке io, а результат получим в нашем главном UI потоке
        //оператор flatMap позволяет создать последюущий поток со своим Observable.create
        //оператор zipWith позволяет делать параллельный поток
        val o =
        //создаем реквесть на rss канал и преобразуем его в json через API
            //https://rss2json.com/#rss_url=https%3A%2F%2Fwww.reddit.com%2Fr%2Fgifs.rss
            createRequest("https://api.rss2json.com/v1/api.json?rss_url=http%3A%2F%2Ffeeds.bbci.co.uk%2Frussian%2Ffeatures-50983593%2Frss.xml")
                //преобразовываем полученную json строку в объект Feed
                .map { Gson().fromJson(it, Feed::class.java) }
                //теперь нужно выбрать в каком потоке будем исполнять, а в каком потоке получать результат
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

        //запускаем наш поток где первым передаем лямда ф-ию, в которой получим результат от onNext
        //а вторая лямбда ф-ия обработка исключений
        //записываем результат в переменную request и используем в колбеке onDestroy. Это нужно для предотврощения потери памяти
        request = o.subscribe({
//            showLinearLayout(it.items)
            showListView(it.items)
            for (item in it.items) {
                Log.w("test", "title:${item.title}")
                Log.w("test", "link:${item.link}")
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
//        }
        //Log.v("tag", "Был запущен onCreate")
    }

    //функция для самого простого отображения элементов массива Feed полученного из инет запроса
    fun showLinearLayout(feedList: ArrayList<FeedItem>) {
        //сначала берем контекст в качестве самого активити и берем из него инфлейтер
        //http://developer.alexanderklimov.ru/android/theory/layoutinflater.php
        val inflater = layoutInflater //TODO посмотреть layoutInflater
        for (f in feedList) {
            // и этому инфлейтеру сказать inflate layout list_item vList
            val view = inflater.inflate(R.layout.list_item, vList, false)
            val vTitle = view.findViewById<TextView>(R.id.item_title)
            //в полученный textview задаем текст
            vTitle.text = f.title
            //добавляем его в главную разметку vList
            vList.addView(view)
        }
    }

    fun showListView(feedList: ArrayList<FeedItem>) {
        //создаем адаптер (http://developer.alexanderklimov.ru/android/theory/adapters.php)
        vListView.adapter = Adapter(feedList)
    }

    //для отображения ListView создаем класс Adapter наследуемый TODO BaseAdapter
    class Adapter(val items: ArrayList<FeedItem>) : BaseAdapter() {
        //создаем вьюшку где будет отображаться сам элемент
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            // parent это какая либо вьюшка с контекстом
            //сам адаптер ничего не знает о активити, поэтому мы не можем напрямую сделать inflate
            //но контекст есть в параметре parent, берем из него контекст
            val inflater = LayoutInflater.from(parent!!.context)
            val view = convertView ?: inflater.inflate(R.layout.list_item, parent, false)
            val vTitle = view.findViewById<TextView>(R.id.item_title)

            //в полученный textview задаем текст
            val item = getItem(position) as FeedItem

            vTitle.text = item.title
            Log.w("position, ", "$position")
            //возвращаем созданный вью
            return view
        }

//        адаптер для RecyclerView, наследуется от RecyclerView.Adapter<> в параметр которого нужно
//        нужно указать имя еще одного класса который будет служить контейнером для UI TODO посмотреть
        class RecAdapter:RecyclerView.Adapter<RecHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecHolder, position: Int) {
        TODO("Not yet implemented")
    }

}
//        в парасетр передаем вьюшку
        class RecHolder(view: View):RecyclerView.ViewHolder(view) {

        }

        //возвратить сам элемент
        override fun getItem(position: Int): Any {
            return items[position]
        }

        //идентификатор элемента
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        //возвращаем количество элементов
        override fun getCount(): Int {
            return items.size
        }

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
