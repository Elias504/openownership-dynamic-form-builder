package org.openownership.form_builder.controller

import jakarta.servlet.http.HttpServletResponse
import org.openownership.form_builder.service.FileService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class FileController(
    private val fileService: FileService,
) {
    @PostMapping("/api/files/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun upload(
        @RequestParam("file") file: MultipartFile,
    ): Map<String, String> = mapOf("key" to fileService.upload(file))

    @GetMapping("/api/files/download")
    fun download(
        @RequestParam key: String,
        response: HttpServletResponse,
    ) {
        val filename = key.substringAfterLast('/')
        response.contentType = "application/octet-stream"
        response.setHeader("Content-Disposition", "attachment; filename=\"$filename\"")
        fileService.download(key).use { it.transferTo(response.outputStream) }
    }
}
