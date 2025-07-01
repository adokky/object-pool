package dev.dokky.pool

import kotlin.test.Test
import kotlin.test.assertEquals

class ObjectPoolTest {
    companion object {
        private val initialCounter = 10
    }

    private class ExampleObject { var counter = initialCounter }

    @Test
    fun simple() {
        val pool = object : AbstractObjectPool<ExampleObject>(2..3) {
            override fun allocate() = ExampleObject()
            override fun beforeRelease(value: ExampleObject) { value.counter = 0 }
        }

        assertEquals(2..3, pool.capacity)

        val l1 = pool.acquire()
        val l2 = pool.acquire()
        val l3 = pool.acquire()
        val l4 = pool.acquire()

        assertEquals(listOf(l1, l2, l3, l4), setOf(l1, l2, l3, l4).toList(),
            "pool allocated the same object multiple times")

        assertEquals(initialCounter, l1.counter)
        assertEquals(initialCounter, l2.counter)
        assertEquals(initialCounter, l3.counter)
        assertEquals(initialCounter, l4.counter)

        l1.counter = 1
        l2.counter = 2
        l3.counter = 3
        l4.counter = 4

        pool.release(l2)
        pool.acquire().let { l2new ->
            assertEquals(l2, l2new)
            assertEquals(0, l2new.counter)
        }

        pool.release(l1)
        pool.release(l2)
        pool.release(l4)
        pool.release(l3) // should be dropped, because capacity=3

        setOf(
            pool.acquire(),
            pool.acquire(),
            pool.acquire()
        ).let { acuired ->
            assertEquals(setOf(l1, l2, l4), acuired)
            acuired.forEach { assertEquals(0, it.counter, "pool returned dirty object") }
        }

        pool.acquire().let { it !in listOf(l1, l2, l3, l4) }
    }
}