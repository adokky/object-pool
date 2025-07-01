Fast and minimalistic object pool with the following properties:
* Allows freeing objects not corresponding to the current pool. Excess objects will be discarded.
* NOT thread safe. Although, there is special `ThreadLocalObjectPool` which is basically `TheadLocal<ObjectPool<T>>`.

## Usage

Simple:

```kotlin
val pool = SimpleObjectPool(capacity = 2..10, ::StringBuilder)

pool.use { stringBuilder ->
    // ...
}
```

With closable/disposable/cleanable objects:

```kotlin
val pool = object: AbstractObjectPool<ArrayList<String>>(capacity = 2..10) {
    override fun allocate() = ArrayList()

    override fun beforeRelease(value: T) {
        value.close()
    }
}

pool.use { list ->
    // ...
}
```