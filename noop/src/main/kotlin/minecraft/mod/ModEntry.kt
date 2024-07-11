package minecraft.mod

interface ModEntry {
    fun onInitialize() {}
    fun onClientInitialize() {}
    fun onDedicatedServerInitialize() {}
    fun afterSideInitialized() {}
}