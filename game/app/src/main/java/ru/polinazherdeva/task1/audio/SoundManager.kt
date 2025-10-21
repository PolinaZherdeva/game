package ru.polinazherdeva.task1.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.util.Log
import androidx.annotation.RawRes

class SoundManager(private val ctx: Context) { // ctx — контекст приложения, нужен для загрузки ресурсов (звуков)

    private var bgPlayer: MediaPlayer? = null // проигрыватель для фоновой музыки
    private val soundPool: SoundPool // объект для проигрывания коротких эффектов
    private val soundMap = HashMap<Int, Int>() // карта (коллекция ключ-значение) для связи идентификатора ресурса (R.raw.sound1)
                                               // с id звука в SoundPool (id, который возвращает soundPool.load.)

    init {
        // инициализация soundPool, максимум 4 одновременных звука
        soundPool = SoundPool.Builder().setMaxStreams(4).build()
    }

    // функция для загрузки звуковых эффектов
    fun loadEffects(@RawRes vararg res: Int) { // vararg — позволяет передать любое кол-во ресурсов
        res.forEach { r ->
            // ctx — контекст для доступа к ресурсу
            // id ресурса (R.raw.sound1)
            // приоритет (не используется, но нужен)
            val id = soundPool.load(ctx, r, 1) // загружаем ресурс в soundPool, получаем id
            soundMap[r] = id // сохраняем id в карту, чтобы потом можно было воспроизвести
        }
    }

    // функция для воспроизведения одного эффекта
    fun playEffect(@RawRes res: Int) {
        // если id найден, проигрываем звук:
        // it — id звука
        // 1f, 1f — громкость левого и правого канала
        // приоритет 1,
        // кол-во повторов 0
        // скорость воспроизведения 1f
        soundMap[res]?.let { soundPool.play(it, 1f, 1f, 1, 0, 1f) }
    }

    // функция запуска фоновой музыки
    // looping: Boolean = true — значение по умолчанию: музыка зациклена
    fun startBackground(@RawRes res: Int, looping: Boolean = true) {
        stopBackground() // сначала останавливаем предыдущую музыку, если была
        try {
            bgPlayer = MediaPlayer.create(ctx, res) // создаем MediaPlayer с ресурсом
            bgPlayer?.isLooping = looping  // включаем/выключаем повтор
            bgPlayer?.start() // запускаем воспроизведение
        } catch (e: Exception) { Log.e("SoundManager","bg start err",e) } // ловим ошибки
    }

    // функция остановки фоновой музыки
    fun stopBackground() {
        try {
            bgPlayer?.stop() // останавливаем, безопасно вызывает stop только если bgPlayer не null
            bgPlayer?.release() // освобождаем ресурсы MediaPlayer
            bgPlayer = null // обнуляем переменную-ссыдку
        } catch (_: Exception) {} // игнорируем ошибки при остановке
    }


    // функция для освобождения всех ресурсов звуков
    fun release() {
        stopBackground() // останавливаем музыку
        soundPool.release() // освобождаем ресурсы soundPool, вызывается при закрытии приложения или при выходе из игры
    }
}
