package ru.polinazherdeva.task1.game

data class Cat(
    // x, y - позиция на экране
    var x: Float,
    var y: Float,
    // vx, vy — скорость движения
    var vx: Float,
    var vy: Float,
    // radius — область, в которой кот может «касаться» сыра или мыши
    var radius: Float = 100f
)