package com.github.jonajor.branchguard.settings

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.Action
import javax.swing.JComponent

class InitialSetupDialog(
    project: Project,
    private val settings: BranchGuardSettings,
) : DialogWrapper(project) {
    private val settingsPanel = BranchGuardSettingsPanel()

    init {
        title = "Branch Guard Setup"
        setOKButtonText("Save")
        settingsPanel.reset(settings)
        init()
    }

    override fun createCenterPanel(): JComponent = settingsPanel.panel

    override fun createActions(): Array<Action> = arrayOf(okAction, cancelAction)

    override fun doOKAction() {
        settingsPanel.apply(settings)
        super.doOKAction()
    }

    override fun doCancelAction() {
        settings.setupCompleted = true
        super.doCancelAction()
    }
}
