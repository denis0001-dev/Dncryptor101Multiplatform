package ru.morozovit.dncryptor101.base

import kotlin.math.abs

/**
 * A list that infinitely repeats the initializer list.
 * The [get(Int)][InfiniteList.get] method never throws an [IndexOutOfBoundsException].
 * @author denis0001-dev
 * @param T the items type
 * @see List
 * @see Iterable
 * @since 1.0
 * @constructor Creates a new [InfiniteList] instance with the specified initializer.
 */
class InfiniteList<T>(items: List<T>): List<T> {
    init {
        if (items.isEmpty()) throw UnsupportedOperationException("Empty initializer list is not supported")
    }

    /**
     * A convenience constructor.
     */
    constructor(vararg items: T) : this(mutableListOf(*items))

    /**
     * The initializer [List] that will repeat infinitely.
     */
    val backingList: List<T> = items

    /**
     * The [initializer][InfiniteList.backingList] as an [Iterable].
     */
    private val sequence = backingList.asIterable()
    override val size: Int get() = Int.MAX_VALUE

    override operator fun get(index: Int): T {
        return if (index >= 0) {
            sequence.elementAt(index % backingList.size)
        } else {
            get(backingList.size - abs(index))
        }
    }

    override fun isEmpty() = false

    override fun iterator(): MutableIterator<T> {
        TODO("Not yet implemented")
    }

    override fun listIterator(): MutableListIterator<T> {
        TODO("Not yet implemented")
    }

    override fun listIterator(index: Int): MutableListIterator<T> {
        TODO("Not yet implemented")
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        TODO("Not yet implemented")
    }

    override fun lastIndexOf(element: T) = backingList.lastIndexOf(element)

    override fun indexOf(element: T) = backingList.lastIndexOf(element)

    override fun containsAll(elements: Collection<T>) = backingList.containsAll(elements)

    override fun contains(element: T) = backingList.contains(element)
}