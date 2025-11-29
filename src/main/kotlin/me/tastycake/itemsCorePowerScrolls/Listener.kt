package me.tastycake.itemsCorePowerScrolls

import me.tastycake.itemscore.api.listener.ItemEventResult
import me.tastycake.itemscore.api.listener.ItemsCoreListener
import me.tastycake.itemscore.events.CustomEvent
import me.tastycake.itemscore.events.CustomEventAction
import me.tastycake.itemscore.item.Item
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.scheduler.BukkitRunnable

/**
 * @author TastyCake
 * @date 11/28/2025
 */
class Listener : ItemsCoreListener {
    override fun itemEvent(player: Player, item: Item, action: String, event: Event): ItemEventResult {
        if (item.getAttributeValue("PowerScrolls", "Is a scroll") != null
            && item.getAttributeValue("PowerScrolls", "Is a scroll") as Boolean) {
            return ItemEventResult(true, null)
        }

        if (player.itemInHand == null || player.itemInHand.type == Material.AIR) return ItemEventResult(false, null)

        if (action == null || action.isEmpty() || action == "CE") return ItemEventResult(false, null)

        val access = item.itemsCore.nmsAccess
        var data = access.getString(player.itemInHand, "powerScrolls")
        if (data == null || data.isEmpty()) return ItemEventResult(false, null)

        val items = data.split(";")
        items.forEach { itemName ->
            if (itemName.isNotEmpty()) {
                val i = item.itemsCore.manager.itemManager.getByName(itemName) ?: return@forEach

                try {
                    val field = i.javaClass.getField(action)
                    field.isAccessible = true

                    val value = field.get(i) as? BukkitRunnable ?: return@forEach
                    value.run()
                } catch (ignore: Exception) {

                }
            }
        }

        return ItemEventResult(false, null)
    }
}