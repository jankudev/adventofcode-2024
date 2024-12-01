package dev.janku.katas.utils

import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.util.stream.Stream
import kotlin.io.path.Path

class ResourcesUtils {
    companion object {
        fun getResourceFilePath(resourceName : String) : String {
            val resource = {}.javaClass.classLoader.getResource(resourceName)
            if (resource == null) {
                throw FileNotFoundException("Resource file not found: $resourceName")
            }
            return File(resource.file).absolutePath
        }

        fun getResourceAsLinesStream(resourceName : String) : java.util.stream.Stream<String> {
            return Files.lines(Path(ResourcesUtils.getResourceFilePath(resourceName)))
        }
    }
}