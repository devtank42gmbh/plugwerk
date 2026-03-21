package io.plugwerk.server.service

class NamespaceNotFoundException(slug: String) : RuntimeException("Namespace not found: $slug")

class NamespaceAlreadyExistsException(slug: String) : RuntimeException("Namespace already exists: $slug")

class PluginNotFoundException(namespaceSlug: String, pluginId: String) :
    RuntimeException("Plugin not found: $pluginId in namespace $namespaceSlug")

class PluginAlreadyExistsException(namespaceSlug: String, pluginId: String) :
    RuntimeException("Plugin already exists: $pluginId in namespace $namespaceSlug")

class ReleaseAlreadyExistsException(pluginId: String, version: String) :
    RuntimeException("Release already exists: $pluginId@$version")

class ReleaseNotFoundException(pluginId: String, version: String) :
    RuntimeException("Release not found: $pluginId@$version")

class ArtifactStorageException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
