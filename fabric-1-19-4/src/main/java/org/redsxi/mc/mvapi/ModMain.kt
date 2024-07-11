package org.redsxi.mc.mvapi

import minecraft.mod.ModEntry

class ModMain: ModEntry {
    override fun onInitialize() {
        println("Hello from MCMVAPI as a Mod!")
    }
}