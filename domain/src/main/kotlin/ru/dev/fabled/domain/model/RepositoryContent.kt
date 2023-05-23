package ru.dev.fabled.domain.model

sealed class RepositoryContent(val contentName: String) {

    data class Folder(val folderName: String, val path: String) :
        RepositoryContent(contentName = folderName)

    data class File(val fileName: String, val fileUrl: String) :
        RepositoryContent(contentName = fileName)

}