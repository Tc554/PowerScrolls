package me.tastycake.itemsCorePowerScrolls

import com.cryptomorin.xseries.XMaterial
import me.tastycake.itemscore.ItemsCore
import me.tastycake.itemscore.api.ItemsCoreAPI
import me.tastycake.itemscore.utils.Chat
import me.tastycake.itemscore.utils.Gui
import me.tastycake.itemscore.utils.PlayerRunnable
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * @author TastyCake
 * @date 11/28/2025
 */
object PowerScrollChamber : Listener {
    init {
        Bukkit.getServer().pluginManager.registerEvents(this, Main.INSTANCE)
    }

    public fun create(): Gui {
        val api = ItemsCore.getItemsCoreAPI()

        val gui = Gui("Power Scroll Chamber", 27, Main.INSTANCE)
        gui.isMove = true
        gui.setBackGroundColor(7)

        gui.createGuiItem(
            ItemStack(XMaterial.OAK_SIGN.get()!!),
            4,
            "&eInfo",
            { _, _ -> },
            "&7On one slot put your item and",
            "&7on the second slot put your scroll",
            "&7and chain them together!")
        gui.createGuiItem(ItemStack(Material.AIR), 11)
        gui.createGuiItem(ItemStack(Material.AIR), 15)

        gui.createGuiItem(
            ItemStack(Material.EMERALD), 22, "&aChain",
            { player, slot ->
                val firstStack = gui.gui.getItem(11)
                val secondStack = gui.gui.getItem(15)

                if (isNull(firstStack) || isNull(secondStack)) {
                    player.sendMessage(Chat.color("&cYou must insert items first!"))
                    return@createGuiItem
                }

                val firstItem = api.getItemByName(api.getItemName(firstStack))
                val secondItem = api.getItemByName(api.getItemName(secondStack))

                if (firstItem == null || secondItem == null) {
                    player.sendMessage(Chat.color("&cYou must insert valid items!"))
                    return@createGuiItem
                }

                val firstIsScroll = firstItem.getAttributeValue(
                    "PowerScrolls",
                    "Is a scroll"
                ) != null && firstItem.getAttributeValue("PowerScrolls", "Is a scroll") as Boolean
                val secondIsScroll =
                    secondItem.getAttributeValue("PowerScrolls", "Is a scroll") != null && secondItem.getAttributeValue(
                        "PowerScrolls",
                        "Is a scroll"
                    ) as Boolean

                if (firstIsScroll && secondIsScroll) {
                    player.sendMessage(Chat.color("&cYou can't chain 2 scrolls together!"))
                    return@createGuiItem
                }

                if (!firstIsScroll && !secondIsScroll) {
                    player.sendMessage(Chat.color("&cYou can't chain 2 items together!"))
                    return@createGuiItem
                }

                val stackToModify = if (firstIsScroll) secondStack else firstStack
                val scrollItem = if (firstIsScroll) firstItem else secondItem

                val access = firstItem.itemsCore.nmsAccess

                var new = access.getString(stackToModify, "powerScrolls")

                if (new == null) new = ""

                val split = new.split(";")

                if (split.any { it == scrollItem.name }) {
                    player.sendMessage(Chat.color("&cThis item already has this scroll!"))
                    return@createGuiItem
                }

                new += scrollItem.name + ";"

                val newStack = access.setString(stackToModify, "powerScrolls", new)

                gui.gui.setItem(11, ItemStack(Material.AIR))
                gui.gui.setItem(15, ItemStack(Material.AIR))
                player.closeInventory()
                player.sendMessage(Chat.color("&aScroll has been chained to your item!"))

                val meta = newStack.itemMeta

                val lore = meta!!.lore!!
                lore.add("")
                lore.add(Chat.color("&a● &r" + scrollItem.fancyName))
                meta.lore = lore

                newStack.itemMeta = meta

                player.inventory.addItem(newStack)

            }, "&7Click to chain your item with a scroll"
        )
        return gui
    }

    public fun isNull(stack: ItemStack?): Boolean {
        return stack == null || stack.type == Material.AIR
    }

    @EventHandler
    fun InventoryCloseEvent.on() {
        if (view.title == "Power Scroll Chamber") {
            val firstStack = inventory.getItem(11)
            val secondStack = inventory.getItem(15)

            if (firstStack != null) {
                player.inventory.addItem(firstStack)
            }

            if (secondStack != null) {
                player.inventory.addItem(secondStack)
            }
        }
    }
}