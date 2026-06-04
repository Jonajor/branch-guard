package com.github.jonajor.branchguard.notifications

import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

object BranchGuardNotifications {
    private const val GROUP_ID = "Branch Guard"

    fun success(project: Project, title: String, content: String) {
        notify(project, title, content, NotificationType.INFORMATION)
    }

    fun warning(project: Project, title: String, content: String, action: NotificationAction? = null) {
        notify(project, title, content, NotificationType.WARNING, action)
    }

    fun error(project: Project, title: String, content: String) {
        notify(project, title, content, NotificationType.ERROR)
    }

    private fun notify(
        project: Project,
        title: String,
        content: String,
        type: NotificationType,
        action: NotificationAction? = null,
    ) {
        val notification = NotificationGroupManager.getInstance()
            .getNotificationGroup(GROUP_ID)
            .createNotification(title, content, type)

        if (action != null) {
            notification.addAction(action)
        }

        notification.notify(project)
    }
}
