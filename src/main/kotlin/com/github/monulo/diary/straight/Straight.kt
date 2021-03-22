package com.github.monulo.diary.straight

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent
import com.github.monulo.diary.Diary
import com.github.monun.tap.fake.FakeEntity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.FallingBlock
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.BlockVector
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector
import java.util.function.Predicate

class Straight : Runnable, Listener {
    var player: Player = Bukkit.getPlayer("sonogram10")?.player!!
    private var blockList = arrayListOf<Block>()
    val near = arrayListOf<Entity>()
    private var blocks = arrayListOf<FakeEntity>()
    override fun run() {
        Diary.fakeEntityServer.update()
        if(player == null) {
            player = Bukkit.getPlayer("sonogram10")?.player!!
        }
        else {
            player.health = 20.0
            player.saturation = 20F
            if(player.activePotionEffects.isNotEmpty()) {
                player.activePotionEffects.forEach { p ->
                    PotionEffectType.getByName(p.type.name)?.let { player.removePotionEffect(it) }
                }
            }
            val filter = Predicate<Entity> { entity ->
                when(entity) {
                    player -> false
                    else -> true
                }
            }
            val y = (player.location.y - 1).toInt()
            for(x in (player.location.x.toDouble() - 1.5).toInt() until (player.location.x.toDouble() + 1.5).toInt()) {
                for(z in (player.location.z.toDouble() - 1.5).toInt() until (player.location.z.toDouble() + 1.5).toInt()) {
                    val block = Bukkit.getServer().worlds.first().getBlockAt(x, y, z)
                    block.type = Material.ICE
                }
            }
            for(y in (player.location.y).toInt() until (player.location.y + 6).toInt()) {
                for(x in (player.location.x - 2.5).toInt() until (player.location.x + 2.5).toInt()) {
                    for(z in (player.location.z - 2.5).toInt() until (player.location.z + 2.5).toInt()) {
                        val block = Bukkit.getServer().worlds.first().getBlockAt(x, y, z)
                        blockList.add(block)
                    }
                }
            }
            for(block in blockList) {
                if(!block.type.isAir) {
                    val loc = BlockVector(block.location.x, block.location.y, block.location.z)
                    loc.add(Vector(0.5, 0.5, 0.5))
                    val up = Diary.fakeEntityServer.spawnFallingBlock(loc.toLocation(player.world), block.blockData).apply {
                        updateMetadata<FallingBlock> {
                            setGravity(false)
                        }
                    }
                    blocks.add(up)
                    block.type = Material.AIR
                }
            }
            for(entity in blocks) {
                entity.move(0.0, 0.5, 0.0)
                if(entity.location.y >= 130) {
                    entity.remove()
                }
            }
            val boundingBox = BoundingBox.of(Location(player.world, player.location.x, player.location.y + 2, player.location.z), 2.0, 2.0, 2.0)
            val entity = Bukkit.getServer().worlds.first().getNearbyEntities(boundingBox, filter)
            near += entity
            near.forEach { e ->
                if(e is Player) {
                    val entity = e as Player
                    entity.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 2, 50, true, false, false))
                    near.remove(entity)
                }
                else if(e is LivingEntity) {
                    e.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 2, 50, true, false, false))
                    near.remove(entity)
                } else {
                    e.teleport(Location(e.world, e.location.x, e.location.y + 0.5, e.location.z))
                }
            }
        }
    }
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        Diary.fakeEntityServer.addPlayer(event.player)
    }
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        Diary.fakeEntityServer.removePlayer(event.player)
    }
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if(event.entity == player) {
            event.isCancelled = true
        }
    }
    @EventHandler
    fun onPlayerKnockBack(event: EntityKnockbackByEntityEvent) {
        if(event.entity == player) {
            event.isCancelled = true
        }
    }
}