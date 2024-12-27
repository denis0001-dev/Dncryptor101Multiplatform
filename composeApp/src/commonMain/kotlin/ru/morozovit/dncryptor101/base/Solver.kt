package ru.morozovit.dncryptor101.base

object Solver {
    const val ALPHABET_RUSSIAN = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"
    const val ALPHABET_ENGLISH = "abcdefghijklmnopqrstuvwxyz"

    val ALPHABET_RUSSIAN_SEQ = InfiniteList(*ALPHABET_RUSSIAN.toCharArray().toTypedArray())
    val ALPHABET_ENGLISH_SEQ = InfiniteList(*ALPHABET_ENGLISH.toCharArray().toTypedArray())

    fun encrypt(inp: String, shift: Int): String {
        var word = ""
        for (letter in inp) {
            word += shift(letter, shift)
        }
        return word
    }

    fun shift(letter: Char, shift: Int): Char {
        var index = ALPHABET_ENGLISH.indexOf(letter.lowercaseChar())
        val print: Char
        if (index == -1) {
            index = ALPHABET_RUSSIAN.indexOf(letter.lowercaseChar())
            print = if (index == -1) {
                letter
            } else {
                ALPHABET_RUSSIAN_SEQ[index + shift].let {
                    if (letter.isUpperCase())
                        it.uppercaseChar()
                    else
                        it
                }
            }
        } else {
            print = ALPHABET_ENGLISH_SEQ[index + shift].let {
                if (letter.isUpperCase())
                    it.uppercaseChar()
                else
                    it
            }
        }
        return print
    }

    fun decrypt(inp: String, shift: Int): String {
        var word = ""
        for (letter in inp) {
            word += shift(letter, shift)
        }
        return word
    }

    fun decrypt(inp: String): List<String> {
        val words = mutableListOf<String>()
        for (shift in -ALPHABET_RUSSIAN.length until ALPHABET_RUSSIAN.length) {
            val word = decrypt(inp, shift)
            if (!words.contains(word) && word != inp) {
                words += word
            }
        }
        return words
    }
}