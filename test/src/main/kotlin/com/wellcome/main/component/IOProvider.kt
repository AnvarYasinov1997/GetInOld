package com.wellcome.main.component

import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import com.wellcome.main.util.functions.getMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.*

interface IOProvider {
    fun createImageFolder(imageFolderName: String, parentFolderName: String)
    fun saveNewFile(paths: String, data: String)
    fun updateFile(file: File, newContent: String)
}

@Component
open class DefaultIOProvider @Autowired constructor(
    private val loggerService: LoggerService
) : IOProvider {

    override fun createImageFolder(imageFolderName: String, parentFolderName: String) {
        File(parentFolderName, imageFolderName).also {
            it.setReadable(true, false)
            it.setExecutable(true, false)
            it.setWritable(true, false)
            val isExist = it.exists()
            loggerService.info(LogMessage("Directory is exist: $isExist!"))
            if (!isExist) {
                loggerService.info(LogMessage("Directory created: ${it.mkdirs()}!"))
            }
            loggerService.info(LogMessage("Directory name: ${it.absolutePath}!"))
        }
    }

    override fun saveNewFile(paths: String, data: String) {
        var outputStream: OutputStream? = null
        try {
            outputStream = FileOutputStream(paths)
            outputStream.write(data.toByteArray(Charsets.UTF_8))
        } catch (e: Exception) {
            loggerService.error(LogMessage(e.getMessage()))
            throw RuntimeException("Trouble from file")
        } finally {
            try {
                outputStream?.close()
            } catch (e: IOException) {
                loggerService.error(LogMessage(e.getMessage()))
            }
        }
    }

    override fun updateFile(file: File, newContent: String) {
        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(file)
            fileWriter.write(newContent)
        } catch (e: IOException) {
            loggerService.error(LogMessage(e.getMessage()))
            throw RuntimeException("Trouble with file")
        } finally {
            try {
                fileWriter?.close()
            } catch (e: IOException) {
                loggerService.error(LogMessage(e.getMessage()))
            }
        }
    }

}