package ru.polinazherdeva.task1

import org.junit.Assert.assertEquals
import org.junit.Test

class MouseTest {

    @Test
    fun testGetMouseInfo() {
        val mouse = Mouse(name = "Джерри", speed = 10)
        val expected = "Мышь по имени Джерри бежит со скоростью 10"
        assertEquals(expected, mouse.getMouseInfo())
    }

    @Test
    fun testGetMouseInfoWithEmptyName() {
        val mouse = Mouse(speed = 5)
        val expected = "Мышь по имени  бежит со скоростью 5"
        assertEquals(expected, mouse.getMouseInfo())
    }
}