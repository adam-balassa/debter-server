package hu.balassa.debter.handler

import org.yaml.snakeyaml.Yaml
import java.io.InputStream
import java.lang.Exception

class ApplicationProperties(resourceFile: String) {
    private val applicationProperties = Yaml().run {
        val file: InputStream? = Thread.currentThread().contextClassLoader.getResourceAsStream(resourceFile)
        load<Map<String, Any>>(file!!)
    }

    @Suppress("UNCHECKED_CAST")
    private fun valueFromPath (map: Map<String, Any>, path: List<String>): String =
        map[path[0]]?.let {
            return if (path.size == 1) it as String
            else valueFromPath(
                it as? Map<String, Any> 
                    ?: throw Exception("Value not found at path ${path[0]}.${path[1]}"),
                path.subList(1, path.size)
            )
        } ?: throw Exception("Value not found at path ${path[0]}")


    operator fun invoke(query: String) = valueFromPath(applicationProperties, query.split("."))
}