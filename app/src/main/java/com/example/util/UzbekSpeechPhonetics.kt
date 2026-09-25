package com.example.util

import java.util.regex.Pattern

/**
 * Intelligent phonetic normalization engine for Uzbek Text-To-Speech.
 * Converts Roman numerals, dates, numbers, historical abbreviations, and apostrophes
 * into natural, fluid spoken Uzbek words.
 *
 * This dramatically enhances the speech quality from robotic stutters into a poised,
 * human-like documentary narrator.
 */
object UzbekSpeechPhonetics {

    private val ONES = arrayOf(
        "", "bir", "ikki", "uch", "to'rt", "besh",
        "olti", "yetti", "sakkiz", "to'qqiz"
    )

    private val TENS = arrayOf(
        "", "o'n", "yigirma", "o'ttiz", "qirq", "ellik",
        "oltmish", "yetmish", "sakson", "to'qson"
    )

    private val ORDINALS = arrayOf(
        "", "birinchi", "ikkinchi", "uchinchi", "to'rtinchi", "beshinchi",
        "oltinchi", "yettinchi", "sakkizinchi", "to'qqizinchi", "o'ninchi"
    )

    /**
     * Converts a number up to 9999 into spoken Uzbek words.
     */
    fun numberToWords(number: Int, isOrdinal: Boolean = false): String {
        if (number <= 0) return if (isOrdinal) "nolikchi" else "nol"
        if (number > 9999) return number.toString()

        val parts = mutableListOf<String>()

        val thousands = number / 1000
        val hundreds = (number % 1000) / 100
        val remainder = number % 100
        val tens = remainder / 10
        val ones = remainder % 10

        if (thousands > 0) {
            if (thousands == 1) {
                parts.add("bir ming")
            } else {
                parts.add("${ONES[thousands]} ming")
            }
        }

        if (hundreds > 0) {
            if (hundreds == 1) {
                parts.add("bir yuz")
            } else {
                parts.add("${ONES[hundreds]} yuz")
            }
        }

        if (isOrdinal) {
            if (tens == 0 && ones > 0) {
                parts.add(ORDINALS[ones])
            } else if (tens > 0 && ones == 0) {
                val tensOrdinal = when (tens) {
                    1 -> "o'ninchi"
                    2 -> "yigirmanchi"
                    3 -> "o'ttizinchi"
                    4 -> "qirqinchi"
                    5 -> "elliginchi"
                    6 -> "oltmishinchi"
                    7 -> "yetmishinchi"
                    8 -> "saksoninchi"
                    9 -> "to'qsoninchi"
                    else -> TENS[tens] + "inchi"
                }
                parts.add(tensOrdinal)
            } else if (tens > 0 && ones > 0) {
                parts.add(TENS[tens])
                parts.add(ORDINALS[ones])
            } else if (parts.isNotEmpty()) {
                // e.g. 1000th or 100th
                val last = parts.removeAt(parts.lastIndex)
                if (last.endsWith("ming")) parts.add(last + "inchi")
                else if (last.endsWith("yuz")) parts.add(last + "inchi")
                else parts.add(last + "inchi")
            }
        } else {
            if (tens > 0) parts.add(TENS[tens])
            if (ones > 0) parts.add(ONES[ones])
        }

        return parts.joinToString(" ")
    }

    /**
     * Translates Roman century representations like "XIV-XV asrlar" into natural Uzbek speech.
     */
    private fun expandCenturies(input: String): String {
        var text = input

        // Roman numeral ranges: e.g. "XIV-XV asr" -> "o'n to'rtinchi va o'n beshinchi asr"
        val romanMap = mapOf(
            "XXI" to "yigirma birinchi",
            "XX" to "yigirmanchi",
            "XIX" to "o'n to'qqizinchi",
            "XVIII" to "o'n sakkizinchi",
            "XVII" to "o'n yettinchi",
            "XVI" to "o'n oltinchi",
            "XV" to "o'n beshinchi",
            "XIV" to "o'n to'rtinchi",
            "XIII" to "o'n uchinchi",
            "XII" to "o'n ikkinchi",
            "XI" to "o'n birinchi",
            "X" to "o'ninchi",
            "IX" to "to'qqizinchi",
            "VIII" to "sakkizinchi",
            "VII" to "yettinchi",
            "VI" to "oltinchi",
            "V" to "beshinchi",
            "IV" to "to'rtinchi",
            "III" to "uchinchi",
            "II" to "ikkinchi",
            "I" to "birinchi"
        )

        // Pattern for ranges like "XIV-XV asr" or "XIV - XV asrlar"
        for ((r1, w1) in romanMap) {
            for ((r2, w2) in romanMap) {
                text = text.replace(Regex("\\b$r1\\s*[-–—]\\s*$r2[-–—]?\\s*asr", RegexOption.IGNORE_CASE), "$w1 va $w2 asr")
            }
        }

        // Single Roman centuries: e.g. "XIV asr" -> "o'n to'rtinchi asr"
        for ((r, w) in romanMap) {
            text = text.replace(Regex("\\b$r[-–—]?\\s*asr", RegexOption.IGNORE_CASE), "$w asr")
        }

        // Arabic century ranges: e.g. "14-15 asr" -> "o'n to'rtinchi va o'n beshinchi asr"
        text = text.replace(Regex("(\\d{1,2})\\s*[-–—]\\s*(\\d{1,2})[-–—]?\\s*asr", RegexOption.IGNORE_CASE)) { match ->
            val n1 = match.groupValues[1].toIntOrNull() ?: 1
            val n2 = match.groupValues[2].toIntOrNull() ?: 1
            "${numberToWords(n1, true)} va ${numberToWords(n2, true)} asr"
        }

        // Single Arabic century: e.g. "15-asr" or "15 asrda"
        text = text.replace(Regex("(\\d{1,2})[-–—]?\\s*asr", RegexOption.IGNORE_CASE)) { match ->
            val n = match.groupValues[1].toIntOrNull() ?: 1
            "${numberToWords(n, true)} asr"
        }

        return text
    }

    /**
     * Converts years like "1370-yilda" or "1404-yil" into fluid spoken text.
     */
    private fun expandYears(input: String): String {
        var text = input

        // 4-digit years with suffixes: "1370-yilda" -> "bir ming uch yuz yetmishinchi yilda"
        text = text.replace(Regex("\\b(1\\d{3}|20\\d{2})[-–—]?yilda\\b", RegexOption.IGNORE_CASE)) { match ->
            val year = match.groupValues[1].toIntOrNull() ?: return@replace match.value
            "${numberToWords(year, true)} yilda"
        }

        text = text.replace(Regex("\\b(1\\d{3}|20\\d{2})[-–—]?yilgi\\b", RegexOption.IGNORE_CASE)) { match ->
            val year = match.groupValues[1].toIntOrNull() ?: return@replace match.value
            "${numberToWords(year, true)} yilgi"
        }

        text = text.replace(Regex("\\b(1\\d{3}|20\\d{2})[-–—]?yil\\b", RegexOption.IGNORE_CASE)) { match ->
            val year = match.groupValues[1].toIntOrNull() ?: return@replace match.value
            "${numberToWords(year, true)} yil"
        }

        return text
    }

    /**
     * Expands measurements and historical abbreviations into full spoken Uzbek words.
     */
    private fun expandAbbreviationsAndUnits(input: String): String {
        var text = input

        // Abbreviations
        text = text
            .replace(Regex("\\bmil\\.?\\s*avv\\.?\\b", RegexOption.IGNORE_CASE), "miloddan avvalgi")
            .replace(Regex("\\bm\\.a\\.?\\b", RegexOption.IGNORE_CASE), "miloddan avvalgi")
            .replace(Regex("\\bb\\.e\\.?\\b", RegexOption.IGNORE_CASE), "bizning eramizning")
            .replace(Regex("\\bkv\\.?\\s*m\\b", RegexOption.IGNORE_CASE), "kvadrat metr")
            .replace(Regex("\\bkm²\\b"), "kvadrat kilometr")
            .replace(Regex("\\bm²\\b"), "kvadrat metr")
            .replace(Regex("\\bUNESCO\\b", RegexOption.IGNORE_CASE), "Yunesko")
            .replace(Regex("\\bYUNESKO\\b"), "Yunesko")

        // Units following numbers: "142 m" -> "142 metr"
        text = text.replace(Regex("(\\d+)\\s*m\\b")) { match ->
            "${match.groupValues[1]} metr"
        }
        text = text.replace(Regex("(\\d+)\\s*km\\b")) { match ->
            "${match.groupValues[1]} kilometr"
        }
        text = text.replace(Regex("(\\d+)\\s*sm\\b")) { match ->
            "${match.groupValues[1]} santimetr"
        }
        text = text.replace(Regex("(\\d+)\\s*ga\\b")) { match ->
            "${match.groupValues[1]} gektar"
        }

        return text
    }

    /**
     * Master pipeline to prepare Uzbek text for pristine speech narration.
     * Cleans up characters, inserts natural breath pauses, expands abbreviations and dates.
     */
    fun prepareTextForVoice(rawText: String): String {
        if (rawText.isBlank()) return ""

        var processed = rawText

        // Step 1: Historical centuries
        processed = expandCenturies(processed)

        // Step 2: Years and dates
        processed = expandYears(processed)

        // Step 3: Abbreviations and measurement units
        processed = expandAbbreviationsAndUnits(processed)

        // Step 4: Smoothing apostrophes and quotes so TTS does not choke or spell out
        processed = processed
            .replace("o‘", "o").replace("O‘", "O")
            .replace("o'", "o").replace("O'", "O")
            .replace("oʻ", "o").replace("Oʻ", "O")
            .replace("g‘", "g").replace("G‘", "G")
            .replace("g'", "g").replace("G'", "G")
            .replace("gʻ", "g").replace("Gʻ", "G")
            .replace("sh", "sh").replace("ch", "ch")
            .replace("‘", "").replace("’", "")
            .replace("ʻ", "").replace("ʼ", "")
            .replace("`", "").replace("'", "")

        // Step 5: Replace hyphens and dashes with gentle breath pauses
        processed = processed
            .replace(" – ", ", ")
            .replace(" — ", ", ")
            .replace(" - ", ", ")
            .replace("...", ". ")
            .replace("..", ". ")
            .replace(":", ", ")
            .replace(";", ", ")

        // Step 6: Historic and Architectural titles natural solemn cadence
        processed = processed
            .replace(Regex("\\b(Amir Temur)\\b", RegexOption.IGNORE_CASE), "Sohibqiron Amir Temur")
            .replace(Regex("\\b(Mirzo Ulug‘bek|Mirzo Ulug'bek|Mirzo Ulugbek)\\b", RegexOption.IGNORE_CASE), "Mirzo Ulugbek")
            .replace(Regex("\\b(Samarqand)\\b", RegexOption.IGNORE_CASE), "Samarkand")
            .replace(Regex("\\b(Registon)\\b", RegexOption.IGNORE_CASE), "Registon")

        // Step 7: Avoid huge unpunctuated run-on sentences: if a sentence has > 14 words without a comma, add a gentle pause
        val words = processed.split(" ")
        if (words.size > 12) {
            val sb = StringBuilder()
            var count = 0
            for (word in words) {
                sb.append(word).append(" ")
                count++
                if (count >= 8 && !word.endsWith(",") && !word.endsWith(".") && !word.endsWith("!") && !word.endsWith("?")) {
                    sb.append(", ")
                    count = 0
                }
            }
            processed = sb.toString()
        }

        // Clean extra whitespaces
        return processed.replace(Regex("\\s+"), " ").trim()
    }

    /**
     * Adapts Uzbek Latin text into Turkish phonetics when Turkish TTS engine is active.
     * Turkish and Uzbek share Turkic phonology; substituting ş, ç, ğ produces smooth, natural speech.
     */
    fun adaptForTurkishEngine(input: String): String {
        return input
            .replace("sh", "ş").replace("Sh", "Ş").replace("SH", "Ş")
            .replace("ch", "ç").replace("Ch", "Ç").replace("CH", "Ç")
            .replace(Regex("[gG]['ʻ‘ʼ`]"), "ğ")
            .replace(Regex("[oO]['ʻ‘ʼ`]"), "o")
            .replace("q", "k").replace("Q", "K")
            .replace("x", "h").replace("X", "H")
    }

    /**
     * Transcribes Uzbek Latin into Uzbek Cyrillic.
     * Used when the Android device only has a Russian TTS engine installed,
     * allowing Russian speech synthesizer to articulate Uzbek words with natural vowel phonetics
     * instead of mispronouncing or stuttering over Latin characters.
     */
    fun latinToCyrillic(input: String): String {
        var text = input
        // Multi-character conversions first
        val multiMap = listOf(
            "sh" to "ш", "Sh" to "Ш", "SH" to "Ш",
            "ch" to "ч", "Ch" to "Ч", "CH" to "Ч",
            "yo" to "ё", "Yo" to "Ё", "YO" to "Ё",
            "yu" to "ю", "Yu" to "Ю", "YU" to "Ю",
            "ya" to "я", "Ya" to "Я", "YA" to "Я",
            "ye" to "е", "Ye" to "Е", "YE" to "Е"
        )
        for ((latin, cyr) in multiMap) {
            text = text.replace(latin, cyr)
        }

        // Special Uzbek letters: o', g', q, x, h
        text = text.replace(Regex("[oO]['ʻ‘ʼ`]")) { if (it.value[0].isUpperCase()) "Ў" else "ў" }
        text = text.replace(Regex("[gG]['ʻ‘ʼ`]")) { if (it.value[0].isUpperCase()) "Ғ" else "ғ" }
        text = text.replace("q", "қ").replace("Q", "Қ")
        text = text.replace("x", "х").replace("X", "Х")
        text = text.replace("h", "ҳ").replace("H", "Ҳ")

        // Single letter mapping
        val singleMap = mapOf(
            'a' to 'а', 'A' to 'А',
            'b' to 'б', 'B' to 'Б',
            'd' to 'д', 'D' to 'Д',
            'e' to 'э', 'E' to 'Э',
            'f' to 'ф', 'F' to 'Ф',
            'g' to 'г', 'G' to 'Г',
            'i' to 'и', 'I' to 'И',
            'j' to 'ж', 'J' to 'Ж',
            'k' to 'к', 'K' to 'К',
            'l' to 'л', 'L' to 'Л',
            'm' to 'м', 'M' to 'М',
            'n' to 'н', 'N' to 'Н',
            'o' to 'о', 'O' to 'О',
            'p' to 'п', 'P' to 'П',
            'r' to 'р', 'R' to 'Р',
            's' to 'с', 'S' to 'С',
            't' to 'т', 'T' to 'Т',
            'u' to 'у', 'U' to 'У',
            'v' to 'в', 'V' to 'В',
            'y' to 'й', 'Y' to 'Й',
            'z' to 'з', 'Z' to 'З'
        )
        val sb = java.lang.StringBuilder()
        for (ch in text) {
            sb.append(singleMap[ch] ?: ch)
        }
        return sb.toString()
    }
}
