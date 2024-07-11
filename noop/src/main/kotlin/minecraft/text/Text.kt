package minecraft.text

interface Text {
    companion object {
        fun literal(text: String): Text {
            throw AssertionError("Try to use ")
        }
    }
}