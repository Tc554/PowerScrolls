package me.tastycake.itemsCorePowerScrolls

import com.cryptomorin.xseries.XMaterial
import me.tastycake.itemscore.addons.PluginAddon
import me.tastycake.itemscore.addons.api.AddonProvider
import me.tastycake.itemscore.addons.attributes.AddonAttribute
import me.tastycake.itemscore.addons.attributes.AddonAttribute.Update
import me.tastycake.itemscore.editor.ItemEditor
import me.tastycake.itemscore.utils.Gui
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    companion object {
        lateinit var INSTANCE: Main
    }

    override fun onEnable() {
        INSTANCE = this

        getCommand("scrollchamber")!!.setExecutor(OpenChamberCommand())

        // Add a toggle attribute to allow items to be scrolls (different behavior)
        val provider = AddonProvider.get()

        provider.addAddon(
            PluginAddon("PowerScrolls", XMaterial.WRITABLE_BOOK.get())
                .attribute(AddonAttribute("Is a scroll", XMaterial.WRITTEN_BOOK.get(), false,
                    { player, gui, editor, callback ->
                        val current: Boolean = editor.getAttributeSerializableByName("PowerScrolls_Is a scroll").value as Boolean
                        callback.result(!current)
                        editor.createAddonGui(provider.getAddonByName("PowerScrolls")).openInventory(player)
                    }, "&7If true, the item will be used to upgrade", "&7other items and add abilities to them.", "", "&aClick to toggle"))
                .listener(Listener())
        )
    }

    override fun onDisable() {

    }
}
