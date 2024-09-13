package ru.nti.dtps.cimconverter.utils

import java.io.File
import java.io.FileInputStream

fun readSchemeAsText(path: String): String = File(path).readText(Charsets.UTF_8)

fun getFileInputStream(path: String): FileInputStream = File(path).inputStream()
