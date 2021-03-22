package com.github.monulo.diary

import com.github.monulo.diary.straight.Straight
import com.github.monun.kommand.kommand
import com.github.monun.tap.fake.FakeEntityServer
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class PluginDiary : JavaPlugin() {
    override fun onEnable() {
        Diary.fakeEntityServer = FakeEntityServer.create(this)
        kommand {
            register("game") {
                KommandDiary.register(this, this@PluginDiary)
            }
        }
    }
}