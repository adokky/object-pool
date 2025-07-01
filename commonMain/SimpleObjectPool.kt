package dev.dokky.pool

import kotlin.jvm.JvmStatic

abstract class SimpleObjectPool<T: Any>(capacity: IntRange) : AbstractObjectPool<T>(capacity) {
    override fun beforeRelease(value: T) {}

    companion object {
        @JvmStatic
        operator inline fun <T: Any> invoke(
            capacity: IntRange,
            crossinline allocate: () -> T
        ): SimpleObjectPool<T> = object : SimpleObjectPool<T>(capacity) {
            override fun allocate(): T = allocate()
        }
    }
}