package dev.dokky.pool

interface ObjectPool<T: Any> {

    fun acquire(): T

    fun release(value: T)
}