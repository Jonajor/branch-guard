package com.github.jonajor.branchguard.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

@Service(Service.Level.APP)
@State(name = "BranchGuardSettings", storages = [Storage("branchGuard.xml")])
class BranchGuardSettings : PersistentStateComponent<BranchGuardSettings.State> {
    data class State(
        var protectedBranches: MutableList<String> = mutableListOf("main", "master", "develop"),
        var branchPrefix: String = "feature",
        var branchTemplate: String = "{prefix}/{ticket}-{description}",
        var ticketRegex: String = "[A-Z]+-\\d+",
        var setupCompleted: Boolean = false,
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
        if (this.state.protectedBranches.isEmpty()) {
            this.state.protectedBranches = mutableListOf("main", "master", "develop")
        }
    }

    var protectedBranches: MutableList<String>
        get() = state.protectedBranches
        set(value) {
            state.protectedBranches = value
        }

    var branchPrefix: String
        get() = state.branchPrefix
        set(value) {
            state.branchPrefix = value
        }

    var branchTemplate: String
        get() = state.branchTemplate
        set(value) {
            state.branchTemplate = value
        }

    var ticketRegex: String
        get() = state.ticketRegex
        set(value) {
            state.ticketRegex = value
        }

    var setupCompleted: Boolean
        get() = state.setupCompleted
        set(value) {
            state.setupCompleted = value
        }

    fun protectedBranchSet(): Set<String> =
        protectedBranches.map { it.trim() }.filter { it.isNotEmpty() }.toSet()

    companion object {
        fun getInstance(): BranchGuardSettings = service()
    }
}
