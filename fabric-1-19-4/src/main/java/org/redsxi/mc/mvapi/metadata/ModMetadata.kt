package org.redsxi.mc.mvapi.metadata

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.io.InputStreamReader
import java.net.URLClassLoader

class ModMetadata {
    lateinit var name: String
    lateinit var modId: String
    lateinit var version: String
    lateinit var entry: String

    companion object Loader {
        private const val META_NAME: String = "mc-mod-meta.json"
        private val gson: Gson = GsonBuilder().create()

        fun loadModMetadata(file: File): ModMetadata? {
            val loader = getJarClassLoader(file)
            val stream = loader.getResourceAsStream(META_NAME) ?: return null
            val reader = InputStreamReader(stream)
            return gson.fromJson(reader, ModMetadata::class.java)
        }

        fun getJarClassLoader(file: File): ClassLoader {
            val url = file.toURI().toURL()
            return URLClassLoader(arrayOf(url), null)
        }

        fun loadMCMVAPIMetadata(): ModMetadata {
            val loader = this::class.java.classLoader
            val stream = loader.getResourceAsStream(META_NAME) ?: throw IllegalStateException("No mc-mod-meta.json in MCMVAPI")
            val reader = InputStreamReader(stream)
            return gson.fromJson(reader, ModMetadata::class.java)
        }
    }

    fun hasEntry() = this::entry.isInitialized
}