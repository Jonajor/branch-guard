package com.github.jonajor.branchguard.settings

import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import javax.swing.DefaultListModel
import javax.swing.JPanel
import javax.swing.ListSelectionModel

class BranchGuardSettingsPanel {
    private val branchModel = DefaultListModel<String>()
    private val branchList = JBList(branchModel).apply {
        emptyText.text = "No protected branches configured"
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        visibleRowCount = 5
    }

    private val prefixField = JBTextField()
    private val templateField = JBTextField()
    private val ticketRegexField = JBTextField()

    val panel: JPanel = JPanel(BorderLayout()).apply {
        border = JBUI.Borders.empty(8)
        add(
            FormBuilder.createFormBuilder()
                .addLabeledComponent(
                    JBLabel("Protected Branches"),
                    ToolbarDecorator.createDecorator(branchList)
                        .setAddAction {
                            val value = com.intellij.openapi.ui.Messages.showInputDialog(
                                this,
                                "Branch name:",
                                "Add Protected Branch",
                                null,
                            )?.trim()
                            if (!value.isNullOrEmpty() && value !in branches()) {
                                branchModel.addElement(value)
                            }
                        }
                        .setRemoveAction {
                            val index = branchList.selectedIndex
                            if (index >= 0) {
                                branchModel.remove(index)
                            }
                        }
                        .createPanel(),
                    true,
                )
                .addLabeledComponent("Branch Prefix", prefixField)
                .addLabeledComponent("Branch Template", templateField)
                .addLabeledComponent("Ticket Regex", ticketRegexField)
                .addComponentFillVertically(JPanel(), 0)
                .panel,
            BorderLayout.CENTER,
        )
    }

    fun reset(settings: BranchGuardSettings) {
        branchModel.clear()
        settings.protectedBranches.forEach(branchModel::addElement)
        prefixField.text = settings.branchPrefix
        templateField.text = settings.branchTemplate
        ticketRegexField.text = settings.ticketRegex
    }

    fun apply(settings: BranchGuardSettings) {
        settings.protectedBranches = branches()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .toMutableList()
        settings.branchPrefix = prefixField.text.trim().ifEmpty { "feature" }
        settings.branchTemplate = templateField.text.trim().ifEmpty { "{prefix}/{ticket}-{description}" }
        settings.ticketRegex = ticketRegexField.text.trim().ifEmpty { "[A-Z]+-\\d+" }
        settings.setupCompleted = true
    }

    fun isModified(settings: BranchGuardSettings): Boolean =
        branches() != settings.protectedBranches ||
            prefixField.text != settings.branchPrefix ||
            templateField.text != settings.branchTemplate ||
            ticketRegexField.text != settings.ticketRegex

    private fun branches(): List<String> =
        (0 until branchModel.size()).map { branchModel.getElementAt(it) }
}
