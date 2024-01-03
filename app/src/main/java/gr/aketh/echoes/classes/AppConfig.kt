package gr.aketh.echoes.classes

import java.util.Properties

object AppConfig {
    private val properties = Properties()

    init {
        // Load properties from the file
        val inputStream = AppConfig::class.java.classLoader?.getResourceAsStream("local.properties")
        inputStream?.use { properties.load(it) }
    }

    fun getUsername(): String {
        return properties.getProperty("FTP_USERNAME", "")
    }

    fun getPassword(): String {
        return properties.getProperty("FTP_PASSWORD", "")
    }
}