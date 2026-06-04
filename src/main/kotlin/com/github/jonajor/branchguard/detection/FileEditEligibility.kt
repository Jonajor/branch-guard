package com.github.jonajor.branchguard.detection

import com.intellij.ide.scratch.ScratchFileService
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VirtualFile

object FileEditEligibility {
    fun isEligible(project: Project, file: VirtualFile): Boolean {
        if (!file.isValid || file.isDirectory) return false
        if (ScratchFileService.getInstance().getRootType(file) != null) return false
        if (FileTypeRegistry.getInstance().isFileIgnored(file)) return false

        val fileIndex = ProjectFileIndex.getInstance(project)
        if (!fileIndex.isInContent(file)) return false
        if (fileIndex.isInLibrary(file)) return false
        if (fileIndex.isExcluded(file)) return false

        return true
    }
}
