![Maven Central Version](https://img.shields.io/maven-central/v/io.github.adokky/object-pool)
[![javadoc](https://javadoc.io/badge2/io.github.adokky/object-pool/javadoc.svg)](https://javadoc.io/doc/io.github.adokky/object-pool)

Fast and minimalistic object pool with the following properties:
* **NOT thread-safe**. Although, there is special `ThreadLocalObjectPool` which is basically `TheadLocal<ObjectPool<T>>`.
* Allows freeing objects not corresponding to the current pool. Excess objects will be discarded.


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