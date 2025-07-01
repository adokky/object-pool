package dev.dokky.pool

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

abstract class AbstractObjectPool<T: Any>(val capacity: IntRange): ObjectPool<T> {
    private var pooled = arrayOfNulls<Any?>(capacity.first)
    private var size = 0

    protected abstract fun allocate(): T

    protected abstract fun beforeRelease(value: T)

    override fun acquire(): T = if (size == 0) {
        doAllocate()
    } else {
        @Suppress("UNCHECKED_CAST")
        pooled[--size] as T
    }

    private fun doAllocate(): T {
        if (pooled.size < capacity.endInclusive) {
            pooled = arrayOfNulls<Any?>(
                size = (pooled.size * 2).coerceAtMost(capacity.endInclusive)
            )
        }

        return allocate()
    }

    override fun release(value: T) {
        if (size >= pooled.size) return
        beforeRelease(value)
        pooled[size] = value
        size++
    }

    fun preAllocate(count: Int) {
        repeat(count.coerceAtMost(capacity.endInclusive)) {
            release(acquire())
        }
    }

    fun shrink(toCapacity: Int = capacity.first) {
        pooled = arrayOfNulls<Any?>(toCapacity)
        size = size.coerceAtMost(pooled.size)
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <T: Any, R> AbstractObjectPool<T>.use(body: (T) -> R): R {
    contract {
        callsInPlace(body, InvocationKind.EXACTLY_ONCE)
    }
    val v = acquire()
    return try {
        body(v)
    } finally {
        release(v)
    }
}