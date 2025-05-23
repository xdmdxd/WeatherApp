package com.example.weatherapp.data

data class LiveDataEvent<out T>(private val content: T) {

    private var hasBeenHandled = false

    /**
     * Vrátí obsah pouze pokud ještě nebyl zpracován.
     * Jinak vrátí null.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Vrátí obsah i pokud už byl zpracován.
     */
    fun peekContent(): T = content
}
