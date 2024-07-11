package org.redsxi.mc.mvapi

import minecraft.mod.ModEntry
import org.redsxi.mc.mvapi.metadata.ModMetadata
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URLClassLoader

object ModLoader {

    fun loaderThread(isClient: Boolean) {
        loadMods()
        printMods()
        initModEntries()
        runModEntry(isClient)
    }

    private val logger = LoggerFactory.getLogger("MVAPI-ModLoader")
    private val loadedModMetadata = HashMap<String, ModMetadata>()
    private val loadedModClassLoaders = HashMap<String, ClassLoader>()
    private val modEntries = HashMap<String, ModEntry>()

    private fun loadMCMVAPIAsMod() {
        loadedModMetadata["mcmvapi"] = ModMetadata.loadMCMVAPIMetadata()
        loadedModClassLoaders["mcmvapi"] = this::class.java.classLoader
    }

    private fun loadMods() {
        logger.debug("Reading MCMVAPI mod data")
        loadMCMVAPIAsMod()
        logger.debug("Start loading mod metadata")
        val modsDir = File("mods")
        if(modsDir.isFile) {
            logger.error("\"mods\" is not a directory.")
            throw IllegalStateException("\"mods\" is not a directory.")
        }
        modsDir.listFiles { it ->
            it?.name?.endsWith(".jar") ?: false
        }?.forEach { file ->
            logger.debug("Found a jar file {}", file)
            val metadata = ModMetadata.loadModMetadata(file) ?: return
            logger.debug("Loaded mod metadata, mod id ${metadata.modId} version ${metadata.version}")
            val modId = metadata.modId
            loadedModMetadata[modId] = metadata
            loadedModClassLoaders[modId] = getJarClassLoader(file)
        }
    }

    fun getJarClassLoader(file: File): ClassLoader {
        val url = file.toURI().toURL()
        return URLClassLoader(arrayOf(url), this::class.java.classLoader)
    }

    private fun printMods() {
        logger.info("Found ${loadedModMetadata.entries.size} mod(s):")
        loadedModMetadata.entries.forEach {
            logger.info("Mod \"${it.value.name}\"(${it.key}) version ${it.value.version}")
        }
    }

    private fun getModEntry(modId: String): ModEntry {
        val metadata = loadedModMetadata[modId]
        val classLoader = loadedModClassLoaders[modId]
        if(metadata == null || classLoader == null) {
            logger.error("INNER ERROR: Cannot find mod {}", modId)
            throw Error("INNER ERROR: Cannot find mod $modId")
        }

        if(!metadata.hasEntry()) return object: ModEntry {
            override fun onInitialize() {
            }
        }
        val entryName = metadata.entry
        logger.debug("Trying to load entry \"$entryName\" for $modId")
        try {
            val clazz = classLoader.loadClass(entryName)
            val constructor = clazz.getConstructor()
            val obj = constructor.newInstance()
            val entry = obj as ModEntry
            return entry
        } catch (e: Exception) {
            throw IllegalAccessException("Cannot access entry \"entryName\" of $modId. Not found/Cannot access?")
        }
    }

    private fun initModEntries() {
        loadedModMetadata.entries.forEach {
            modEntries[it.key] = getModEntry(it.key)
            logger.debug("Loaded \"${it.key}\" entry")
        }
    }

    private fun runModEntry(isClient: Boolean) {
        logger.debug("Started run mod entry")
        logger.debug(" ----- I N I T I A L I Z E -----")
        modEntries.entries.forEach {
            try {
                it.value.onInitialize()
            } catch (e: Throwable) {
                throw Exception("Cannot load mod \"${it.key}\" at INITIALIZE", e)
            }
        }
        if(isClient) {
            logger.debug(" ----- C L I E N T -----")
            modEntries.entries.forEach {
                try {
                    it.value.onClientInitialize()
                } catch (e: Throwable) {
                    throw Exception("Cannot load mod \"${it.key}\" at CLIENT", e)
                }
            }
        } else {
            logger.debug(" ----- S E R V E R -----")
            modEntries.entries.forEach {
                try {
                    it.value.onDedicatedServerInitialize()
                } catch (e: Throwable) {
                    throw Exception("Cannot load mod \"${it.key}\" at SERVER", e)
                }
            }
        }
        logger.debug(" ----- A F T E R S I D E -----")
        modEntries.entries.forEach {
            try {
                it.value.afterSideInitialized()
            } catch (e: Throwable) {
                throw Exception("Cannot load mod \"${it.key}\" at AFTERSIDE", e)
            }
        }
    }
}