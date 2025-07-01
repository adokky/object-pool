package dev.dokky.pool

import karamel.utils.ThreadLocal
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline

@JvmInline
value class ThreadLocalObjectPool<T : Any>(
    @PublishedApi
    internal val threadLocal: ThreadLocal<AbstractObjectPool<T>>
)

inline fun <T: Any> threadLocalObjectPool(
    capacity: IntRange,
    crossinline beforeRelease: (T) -> Unit = {},
    crossinline allocate: () -> T
): ThreadLocalObjectPool<T> {
    val tl: ThreadLocal<AbstractObjectPool<T>> = ThreadLocal {
        object : AbstractObjectPool<T>(capacity) {
            override fun allocate(): T = allocate()
            override fun beforeRelease(value: T) = beforeRelease(value)
        }
    }
    return ThreadLocalObjectPool(tl)
}

@OptIn(ExperimentalContracts::class)
inline fun <T: Any, R> ThreadLocalObjectPool<T>.use(body: (T) -> R): R {
    contract {
        callsInPlace(body, InvocationKind.EXACTLY_ONCE)
    }
    return threadLocal.get().use(body)
}