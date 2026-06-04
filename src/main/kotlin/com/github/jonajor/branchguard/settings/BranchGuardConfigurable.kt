package com.github.jonajor.branchguard.settings

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class BranchGuardConfigurable : Configurable {
    private var settingsPanel: BranchGuardSettingsPanel? = null

    override fun getDisplayName(): String = "Branch Guard"

    override fun createComponent(): JComponent {
        val panel = BranchGuardSettingsPanel()
        settingsPanel = panel
        panel.reset(BranchGuardSettings.getInstance())
        return panel.panel
    }

    override fun isModified(): Boolean =
        settingsPanel?.isModified(BranchGuardSettings.getInstance()) ?: false

    override fun apply() {
        settingsPanel?.apply(BranchGuardSettings.getInstance())
    }

    override fun reset() {
        settingsPanel?.reset(BranchGuardSettings.getInstance())
    }

    override fun disposeUIResources() {
        settingsPanel = null
    }
}
