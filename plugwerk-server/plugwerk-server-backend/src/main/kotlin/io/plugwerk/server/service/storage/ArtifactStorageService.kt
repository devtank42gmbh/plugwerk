package io.plugwerk.server.service.storage

import java.io.InputStream

interface ArtifactStorageService {

    fun store(key: String, content: InputStream, contentLength: Long): String

    fun retrieve(key: String): InputStream

    fun delete(key: String)

    fun exists(key: String): Boolean
}
