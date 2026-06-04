package com.github.jonajor.branchguard.branch

data class BranchNameInput(
    val ticket: String,
    val description: String,
    val prefix: String,
    val template: String,
    val ticketRegex: String,
)
