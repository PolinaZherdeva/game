package ru.polinazherdeva.task1

class Mouse(
    var name: String = "",
    var speed: Int
) {
    fun getMouseInfo(): String {
        return "Мышь по имени $name бежит со скоростью $speed"
    }
}