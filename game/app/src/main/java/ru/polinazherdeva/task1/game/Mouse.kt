package ru.polinazherdeva.task1.game

data class Mouse(
    // позиция
    var x: Float = 0f,
    var y: Float = 0f,
    // область вщаимодействия с котом/сыром
    var radius: Float = 80f
)