package com.github.monulo.diary

import com.github.monulo.diary.straight.Straight
import com.github.monun.kommand.KommandBuilder
import com.github.monun.kommand.argument.player
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

object KommandDiary {
    private lateinit var schedule: BukkitTask
    fun register(builder: KommandBuilder, plugin: JavaPlugin) {
        builder.apply {
            then("start") {
                then("straight") {
                    executes {
                        Straight().player.sendMessage("직진 시작")
                        plugin.server.run {
                            pluginManager.registerEvents(Straight(), plugin)
                            schedule = scheduler.runTaskTimer(plugin, Straight(), 0L, 1L)
                        }
                        for(player in Bukkit.getOnlinePlayers()) {
                            Diary.fakeEntityServer.addPlayer(player)
                        }
                    }
                }
            }
            then("stop") {
                executes {
                    plugin.server.run {
                        scheduler.cancelTasks(plugin)
                        HandlerList.unregisterAll()
                    }
                }
            }
        }
    }
}