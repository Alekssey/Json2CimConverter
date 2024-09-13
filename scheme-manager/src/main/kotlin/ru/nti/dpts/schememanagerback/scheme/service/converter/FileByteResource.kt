package ru.nti.dpts.schememanagerback.scheme.service.converter

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class FileByteResource(val data: ByteArray, val fileName: String)
fun zip(resultZipName: String, vararg files: FileByteResource): FileByteResource {
    val byteArrayOutputStream = ByteArrayOutputStream()
    ZipOutputStream(byteArrayOutputStream).use { output ->
        files.forEach { file ->
            ByteArrayInputStream(file.data).use { input ->
                BufferedInputStream(input).use { origin ->
                    val entry = ZipEntry(file.fileName)
                    output.putNextEntry(entry)
                    origin.copyTo(output)
                }
            }
        }
    }
    return FileByteResource(byteArrayOutputStream.toByteArray(), resultZipName)
}
