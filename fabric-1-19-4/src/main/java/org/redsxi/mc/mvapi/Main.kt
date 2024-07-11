package org.redsxi.mc.mvapi

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Main {
    private val logger: Logger = LoggerFactory.getLogger("MCMVAPI-Main")

    fun initialize() {
        logger.info("Loading Minecraft Multi-Version API!")
    }

    fun initializeClient() {
        logger.info("Environment: Client")
        runLoader(true)
    }

    fun initializeServer() {
        logger.info("Environment: Dedicated Server")
        runLoader(false)
    }

    private fun runLoader(isClient: Boolean) {
        val thread = Thread(ModLoader::loaderThread, isClient)
        thread.start()
    }

    private fun Thread(func: (Boolean) -> Unit, bool: Boolean): Thread {
        val runnable: () -> Unit = {func(bool)}
        return Thread(null, runnable, "MultiVersionAPI-ModLoader")
    }
}