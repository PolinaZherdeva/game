package ru.polinazherdeva.task1.game

data class Cheese(
    // позиция сыра
    var x: Float,
    var y: Float,
    // размер объекта
    var size: Float = 140f,
    // флаг, съеден ли сыр
    var eaten: Boolean = false,
    // прозрачность для анимации исчезновения
    var alpha: Float = 1f
)