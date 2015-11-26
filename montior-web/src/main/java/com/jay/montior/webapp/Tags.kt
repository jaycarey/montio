package com.jay.montior.webapp

class Tags {
    companion object {

        private val NotThin = "[^iIl1\\.,']"

        @JvmStatic fun ellipsis(text: String, max: Int): String {
            if (textWidth(text) <= max) return text
            var end = text.lastIndexOf(' ', max - 3)
            if (end == -1) return text.substring(0, max - 3) + "..."
            var newEnd = end
            do {
                end = newEnd
                newEnd = text.indexOf(' ', end + 1)
                if (newEnd == -1) newEnd = text.length
            } while (textWidth(text.substring(0, newEnd) + "...") < max)
            return text.substring(0, end) + "..."
        }

        private fun textWidth(str: String): Int {
            return (str.length - str.replace(NotThin.toRegex(), "").length / 2).toInt()
        }
    }
}