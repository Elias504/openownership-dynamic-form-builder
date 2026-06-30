package org.openownership.form_builder.service

import org.springframework.web.multipart.MultipartFile
import java.io.InputStream

interface FileService {
    fun upload(file: MultipartFile): String

    fun download(key: String): InputStream
}
