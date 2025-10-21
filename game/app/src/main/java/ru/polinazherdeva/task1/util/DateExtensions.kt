package ru.polinazherdeva.task1.util

fun String.formatServerDateToDisplay(): String {
    // Расширение для String: позволяет вызывать "2025-10-22".formatServerDateToDisplay()
    // Предполагается вход в формате "YYYY-MM-DD".
    // Функция возвращает строку в формате "DD.MM.YYYY" или исходную строку при ошибке.

    // ожидаем YYYY-MM-DD
    return try {
        val parts = this.split("-")
        // Разбиваем исходную строку по дефису на части [year, month, day]
        if (parts.size >= 3) {
            // Проверяем, что есть по крайней мере 3 части.
            val (y, m, d) = parts
            // Деструктурируем в переменные y, m, d (год, месяц, день).
            "${d.padStart(2, '0')}.${m.padStart(2, '0')}.${y}"
            // Формируем строку "DD.MM.YYYY":
            // padStart(2,'0') — если день или месяц имеют 1 цифру, добавляет ведущий ноль.
        } else this
        // Если формат неожиданно другой (мало частей), возвращаем исходную строку.
    } catch (e: Exception) {
        this
        // В случае любой исключительной ситуации (например, null хотя это расширение на String) — возвращаем исходную строку.
    }
}
