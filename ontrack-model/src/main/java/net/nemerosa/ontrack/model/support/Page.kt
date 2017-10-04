package net.nemerosa.ontrack.model.support

class Page
@JvmOverloads
constructor(
        var offset: Int = 0,
        var count: Int = 100
) {

    /**
     * Extracts a sublist using pagination information
     *
     * @param list Initial list
     * @param <T>  Type of elements in the list
     * @return Sub list
    </T> */
    fun <T> extract(list: List<T>): List<T> {
        val toIndex = Math.min(
                offset + count,
                list.size
        )
        return list.subList(
                offset,
                toIndex
        )
    }
}
