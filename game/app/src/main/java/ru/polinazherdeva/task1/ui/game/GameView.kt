package ru.polinazherdeva.task1.ui.game

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlinx.coroutines.*
import kotlin.math.hypot
import kotlin.random.Random

// GameView — кастомный View для самой игры (мышь, коты, сыр)
class GameView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    // CoroutineScope для запуска игровых циклов и спавна объектов
    private val scope = CoroutineScope(Dispatchers.Main)
    private var loopJob: Job? = null       // Job игрового цикла
    private var spawnJob: Job? = null      // Job для постепенного появления котов

    // Игровые объекты
    private var mouse = GameObject()                 // мышь
    private val cats = mutableListOf<Cat>()         // список котов
    private val cheeses = mutableListOf<GameObject>() // список сыра

    // Callback для событий
    var onCheeseEaten: ((Int) -> Unit)? = null // вызывается при поедании сыра
    var onGameOver: ((Int) -> Unit)? = null    // вызывается при проигрыше

    private var score = 0          // текущий счёт
    var isPaused = false           // флаг паузы
        private set

    // Битмапы для отрисовки
    private var mouseBmp: Bitmap? = null
    private var catBmp: Bitmap? = null
    private var cheeseBmp: Bitmap? = null

    // Уровень игры
    var level = 1
        set(value) { field = value.coerceIn(1,3) } // ограничиваем уровни 1..3

    // Максимальное число котов в зависимости от уровня
    private val maxCats get() = when(level) { 1 -> 1; 2 -> 2; else -> 4 }

    // Загрузка и масштабирование изображений
    fun setBitmaps(mouse: Bitmap, cat: Bitmap, cheese: Bitmap) {
        mouseBmp = Bitmap.createScaledBitmap(mouse, 200, 200, false)
        catBmp = Bitmap.createScaledBitmap(cat, 300, 300, false)
        cheeseBmp = Bitmap.createScaledBitmap(cheese, 180, 180, false)
    }

    // Запуск игры
    fun startGame() {
        score = 0
        cats.clear()         // очищаем старых котов
        cheeses.clear()      // очищаем старый сыр
        isPaused = false
        mouse = GameObject(width/2f, height/2f, 100f) // мышь стартует в центре
        addCheese()          // добавляем первый сыр
        spawnCatsGradually() // начинаем постепенно добавлять котов
        startLoop()          // запускаем игровой цикл
    }

    // Основной игровой цикл (обновление логики и перерисовка)
    private fun startLoop() {
        loopJob?.cancel() // отменяем предыдущий цикл, если был
        loopJob = scope.launch {
            var last = System.currentTimeMillis()
            while(isActive) {
                val now = System.currentTimeMillis()
                val dt = (now - last)/1000f // вычисляем deltaTime в секундах
                last = now
                if(!isPaused) update(dt) // обновляем объекты только если игра не на паузе
                invalidate()             // перерисовываем view
                delay(16L)               // ~60 FPS
            }
        }
    }

    // Появление котов постепенно, а не сразу
    private fun spawnCatsGradually() {
        spawnJob?.cancel()
        spawnJob = scope.launch {
            var count = 0
            while(isActive && count < maxCats) {
                addCat()
                count++
                delay((500..1500).random().toLong()) // случайная задержка между котами
            }
        }
    }

    // Поставить игру на паузу
    fun pauseGame() { isPaused = true }
    // Продолжить игру
    fun resumeGame() { isPaused = false }

    // Полная остановка игры
    fun stopGame() {
        isPaused = true
        loopJob?.cancel()
        spawnJob?.cancel()
        cats.clear()
        cheeses.clear()
        invalidate() // перерисовываем пустое поле
    }

    // Обновление логики игры
    private fun update(dt: Float) {
        moveCats(dt)      // перемещаем котов
        checkCollisions() // проверяем столкновения мышь-сыр и мышь-кот
        if(cheeses.isEmpty()) addCheese() // добавляем сыр, если его нет
    }

    // Логика перемещения котов
    private fun moveCats(dt: Float) {
        for(cat in cats) {
            val speed = when(level) { 1 -> 180f; 2 -> 250f; else -> 400f } // скорость в зависимости от уровня
            val targetX: Float
            val targetY: Float

            if(level == 1) {
                // Первый уровень: кот идёт прямо за мышью
                targetX = mouse.x
                targetY = mouse.y
            } else {
                // 2 и 3 уровни: коты двигаются хаотично
                if(cat.reachedTarget()) {
                    cat.randomTargetX = Random.nextFloat() * width
                    cat.randomTargetY = Random.nextFloat() * height
                }
                targetX = cat.randomTargetX
                targetY = cat.randomTargetY
            }

            // движение кота к цели
            val dx = targetX - cat.obj.x
            val dy = targetY - cat.obj.y
            val dist = hypot(dx, dy)
            if(dist > 1f) { // если ещё не достигли цели
                cat.obj.x += (dx/dist) * speed * dt
                cat.obj.y += (dy/dist) * speed * dt
            }
        }
    }

    // Добавление нового кота с случайной стороны экрана
    private fun addCat() {
        val side = Random.nextInt(4)
        val (x, y) = when(side) {
            0 -> Random.nextFloat()*width to 0f          // сверху
            1 -> Random.nextFloat()*width to height.toFloat() // снизу
            2 -> 0f to Random.nextFloat()*height        // слева
            else -> width.toFloat() to Random.nextFloat()*height // справа
        }
        cats.add(Cat(GameObject(x,y,120f))) // создаём кота с радиусом
    }

    // Добавление сыра на случайную позицию
    private fun addCheese() {
        val x = Random.nextFloat()*width
        val y = Random.nextFloat()*height
        cheeses.add(GameObject(x,y,60f))
    }

    // Проверка столкновений мышь-сыр и мышь-кот
    private fun checkCollisions() {
        val cheeseIter = cheeses.iterator()
        while(cheeseIter.hasNext()) {
            val cheese = cheeseIter.next()
            if(mouse.collidesWith(cheese)) { // если мышь съела сыр
                cheeseIter.remove()           // удаляем сыр
                score++                       // увеличиваем счёт
                onCheeseEaten?.invoke(score)  // вызываем callback
            }
        }

        for(cat in cats) {
            if(mouse.collidesWith(cat.obj)) { // мышь поймана котом
                onGameOver?.invoke(score)    // вызываем callback конца игры
                pauseGame()
                break
            }
        }
    }

    // Отрисовка игрового поля
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.rgb(240,230,250)) // фон

        // отрисовка сыра
        cheeseBmp?.let { bmp ->
            cheeses.forEach { c ->
                canvas.drawBitmap(bmp, c.x-bmp.width/2, c.y-bmp.height/2, null)
            }
        }

        // отрисовка котов
        catBmp?.let { bmp ->
            cats.forEach { c ->
                canvas.drawBitmap(bmp, c.obj.x-bmp.width/2, c.obj.y-bmp.height/2, null)
            }
        }

        // отрисовка мыши
        mouseBmp?.let { bmp ->
            canvas.drawBitmap(bmp, mouse.x-bmp.width/2, mouse.y-bmp.height/2, null)
        }

        // отрисовка счёта
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 70f
            typeface = Typeface.DEFAULT_BOLD
            style = Paint.Style.FILL
        }
        canvas.drawText("Score: $score", 20f, 100f, paint)
    }

    // Управление мышью через касания
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(isPaused) return false
        when(event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                mouse.x = event.x // мышь следует за пальцем
                mouse.y = event.y
            }
        }
        return true
    }

    // Игровой объект: мышь, сыр или кот
    data class GameObject(var x: Float=0f, var y: Float=0f, var radius: Float=50f) {
        // Проверка столкновения с другим объектом
        fun collidesWith(other: GameObject): Boolean {
            val dx = x-other.x
            val dy = y-other.y
            return dx*dx + dy*dy < (radius+other.radius)*(radius+other.radius)
        }
    }

    // Класс кота
    data class Cat(val obj: GameObject, var randomTargetX: Float = obj.x, var randomTargetY: Float = obj.y) {
        // Проверка, достиг ли кот своей цели
        fun reachedTarget(): Boolean = hypot(randomTargetX-obj.x, randomTargetY-obj.y) < 5f
    }
}
