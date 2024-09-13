package ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.builder

class ObjectsCounter {
    private val counter = mutableMapOf<String, Int>()
    fun increaseAndGet(key: String = "key"): Int {
        val count = (counter[key] ?: 0) + 1
        counter[key] = count
        return count
    }
}
