package com.example.stepikandroidstudio

import io.reactivex.Observable
import java.lang.RuntimeException
import java.net.HttpURLConnection
import java.net.URL

//работа с сетью как это надо черех reactivex
//создаем класс, передаем лямбда ф-ию
fun createRequest(url:String)= Observable.create<String> {
    //в этой лямбда ф-ии делаем нужный нам запрос в сеть
    //что бы сделать запрос нужно создать url у которого открыть коннекшен
    //а результат будет HttpURLConnection
    val urlConnection = URL(url).openConnection() as HttpURLConnection
    try {
        //говорим урлу коннект и тут происходит обращение в сеть
        urlConnection.connect()

        //проверяем что результат 200
        if (urlConnection.responseCode != HttpURLConnection.HTTP_OK)
        //если не 20 то генерируем исключение
            it.onError(RuntimeException(urlConnection.responseMessage))
        else {
            //если равен 200 то берем инпут чтрим, открываем буфер на чтение и читаем его как текст
            val str = urlConnection.inputStream.bufferedReader().readText()
            //и полученный результат отправляем в on.next
            it.onNext(str)
        }
    } finally {
        //делаем дисконнект
        urlConnection.disconnect()
    }
}