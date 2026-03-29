/*
 * Plugwerk — Plugin Marketplace for the PF4J Ecosystem
 * Copyright (C) 2026 devtank42 GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package io.plugwerk.client

import io.plugwerk.spi.extension.PlugwerkMarketplace
import org.pf4j.Plugin
import java.util.concurrent.ConcurrentHashMap

/**
 * PF4J plugin entry point for the Plugwerk Client SDK.
 *
 * This class is referenced in `MANIFEST.MF` via `Plugin-Class`. PF4J instantiates it
 * when the SDK JAR is loaded as a plugin.
 *
 * A single plugin instance can manage connections to **multiple Plugwerk servers**
 * simultaneously. Each server is identified by a string ID chosen by the host application.
 *
 * **Single server (convenience):**
 * ```kotlin
 * plugin.configure(config)
 * val marketplace = plugin.marketplace()
 * ```
 *
 * **Multiple servers:**
 * ```kotlin
 * plugin.configure("production", prodConfig)
 * plugin.configure("staging", stagingConfig)
 *
 * val prodCatalog = plugin.marketplace("production").catalog()
 * val stagingInstaller = plugin.marketplace("staging").installer()
 * ```
 *
 * **Java:**
 * ```java
 * PlugwerkMarketplacePlugin plugin = (PlugwerkMarketplacePlugin)
 *     pluginManager.getPlugin("plugwerk-client").getPlugin();
 * plugin.configure("production", prodConfig);
 * plugin.configure("staging", stagingConfig);
 *
 * PlugwerkMarketplace prod = plugin.marketplace("production");
 * PlugwerkMarketplace staging = plugin.marketplace("staging");
 * ```
 */
class PlugwerkMarketplacePlugin : Plugin() {

    private data class ServerEntry(val config: PlugwerkConfig, var marketplace: PlugwerkMarketplaceImpl? = null)

    private val servers = ConcurrentHashMap<String, ServerEntry>()

    /**
     * Registers a server configuration under the given [serverId].
     *
     * If a server with this ID was already configured, the old marketplace instance is
     * closed and replaced on the next [marketplace] call.
     *
     * @param serverId identifier chosen by the host application (e.g. `"production"`, `"vendor-a"`)
     * @param config server URL, namespace, credentials, and plugin directory
     */
    fun configure(serverId: String, config: PlugwerkConfig) {
        val old = servers.put(serverId, ServerEntry(config))
        old?.marketplace?.close()
    }

    /**
     * Convenience overload that registers the config under [DEFAULT_SERVER_ID].
     *
     * Equivalent to `configure(DEFAULT_SERVER_ID, config)`.
     */
    fun configure(config: PlugwerkConfig) {
        configure(DEFAULT_SERVER_ID, config)
    }

    /**
     * Returns the [PlugwerkMarketplace] facade for the given [serverId], creating it lazily.
     *
     * @throws IllegalStateException if no server with this ID has been configured.
     * @throws IllegalStateException if [PlugwerkConfig.pluginDirectory] is not set.
     */
    fun marketplace(serverId: String): PlugwerkMarketplace {
        val entry = servers[serverId] ?: throw IllegalStateException(
            "No server configured with ID '$serverId'. " +
                "Call plugin.configure(\"$serverId\", config) first.",
        )
        entry.marketplace?.let { return it }

        val instance = PlugwerkMarketplaceImpl.create(entry.config)
        entry.marketplace = instance
        return instance
    }

    /**
     * Convenience overload that returns the marketplace for [DEFAULT_SERVER_ID].
     *
     * Equivalent to `marketplace(DEFAULT_SERVER_ID)`.
     */
    fun marketplace(): PlugwerkMarketplace = marketplace(DEFAULT_SERVER_ID)

    /** Returns an immutable snapshot of all registered server IDs. */
    fun serverIds(): Set<String> = servers.keys.toSet()

    /**
     * Removes the server entry for the given [serverId] and closes its HTTP client.
     *
     * @return `true` if a server with this ID was registered, `false` otherwise.
     */
    fun remove(serverId: String): Boolean {
        val entry = servers.remove(serverId)
        entry?.marketplace?.close()
        return entry != null
    }

    /** Removes all server entries and closes their HTTP clients. */
    fun removeAll() {
        val entries = servers.values.toList()
        servers.clear()
        entries.forEach { it.marketplace?.close() }
    }

    override fun stop() {
        removeAll()
    }

    companion object {
        /** Default server ID used by the single-server convenience methods. */
        const val DEFAULT_SERVER_ID = "default"
    }
}
