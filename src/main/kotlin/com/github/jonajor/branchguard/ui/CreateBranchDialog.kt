package com.github.jonajor.branchguard.ui

import com.github.jonajor.branchguard.branch.BranchNameGenerator
import com.github.jonajor.branchguard.branch.BranchNameInput
import com.github.jonajor.branchguard.settings.BranchGuardSettings
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import javax.swing.Action
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.event.DocumentEvent

class CreateBranchDialog(
    project: Project,
    private val currentBranch: String,
    private val settings: BranchGuardSettings,
) : DialogWrapper(project) {
    private val ticketField = JBTextField().apply {
        emptyText.text = "ABC-123"
    }
    private val descriptionField = JBTextField().apply {
        emptyText.text = "fix-login-error"
    }
    private val previewLabel = JBLabel()

    private var continueAnyway = false

    init {
        title = "Create Feature Branch?"
        setOKButtonText("Create Branch")
        init()
        updatePreview()
    }

    fun branchName(): String = currentResult().branchName

    fun continuedAnyway(): Boolean = continueAnyway

    override fun createCenterPanel(): JComponent {
        val listener = object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                updatePreview()
            }
        }
        ticketField.document.addDocumentListener(listener)
        descriptionField.document.addDocumentListener(listener)

        val messagePanel = JPanel(BorderLayout()).apply {
            border = JBUI.Borders.emptyBottom(8)
            add(
                JBLabel("<html>You are currently working on: <b>$currentBranch</b><br>Create a feature branch before continuing?</html>"),
                BorderLayout.CENTER,
            )
        }

        val form = FormBuilder.createFormBuilder()
            .addComponent(messagePanel)
            .addLabeledComponent("Ticket", ticketField)
            .addLabeledComponent("Description", descriptionField)
            .addLabeledComponent("Live Preview", previewLabel)
            .panel

        return JPanel(BorderLayout()).apply {
            border = JBUI.Borders.empty(8)
            add(form, BorderLayout.CENTER)
        }
    }

    override fun createActions(): Array<Action> =
        arrayOf(okAction, ContinueAnywayAction(), cancelAction)

    override fun doValidate(): ValidationInfo? {
        val result = currentResult()
        return when {
            !result.isTicketValid -> ValidationInfo("Ticket must match ${settings.ticketRegex}", ticketField)
            result.branchName.isBlank() -> ValidationInfo("Branch name cannot be blank", descriptionField)
            else -> null
        }
    }

    private fun updatePreview() {
        previewLabel.text = currentResult().branchName
    }

    private fun currentResult() = BranchNameGenerator.generate(
        BranchNameInput(
            ticket = ticketField.text,
            description = descriptionField.text,
            prefix = settings.branchPrefix,
            template = settings.branchTemplate,
            ticketRegex = settings.ticketRegex,
        ),
    )

    private inner class ContinueAnywayAction : DialogWrapperAction("Stay on Branch") {
        override fun doAction(e: java.awt.event.ActionEvent?) {
            continueAnyway = true
            close(OK_EXIT_CODE)
        }
    }
}
