package ems

import grails.gorm.transactions.Transactional

@Transactional
class NotificationManagerService {

    def createTaskOverdueNotification(Task task, Employee recipient, int escalationLevel = 1) {
        def message = buildOverdueMessage(task, escalationLevel)

        def notification = new Notification(
                message: message,
                type: 'TASK_OVERDUE',
                recipient: recipient,
                relatedTask: task,
                escalationLevel: escalationLevel
        )

        if (notification.save(flush: true)) {
            log.info("Notification created for ${recipient.firstName} ${recipient.lastName} regarding overdue task: ${task.title}")
            return notification
        } else {
            log.error("Failed to create notification: ${notification.errors}")
            return null
        }
    }

    def checkAndSendOverdueNotifications() {
        def overdueTasks = Task.findAll("FROM Task t WHERE t.deadlineDate < :now AND t.status NOT IN ('COMPLETED', 'CANCELLED')",
                [now: new Date()])

        log.info("Found ${overdueTasks.size()} overdue tasks to process")

        overdueTasks.each { task ->
            processOverdueTask(task)
        }
    }

    private void processOverdueTask(Task task) {
        // Check if we've already sent a notification for this task today
        def today = new Date().clearTime()
        def tomorrow = today + 1

        def existingNotification = Notification.findByRelatedTaskAndSentDateBetween(task, today, tomorrow)

        if (!existingNotification) {
            // First time notification - send to direct supervisor
            def supervisor = task.assignedTo.supervisor
            if (supervisor) {
                createTaskOverdueNotification(task, supervisor, 1)
            }
        } else {
            // Check if notification was sent yesterday and task is still not completed
            def yesterday = today - 1
            def existingYesterdayNotification = Notification.findByRelatedTaskAndSentDateBetween(task, yesterday, today)

            if (existingYesterdayNotification && existingYesterdayNotification.escalationLevel == 1) {
                // Escalate to supervisor's supervisor
                def supervisor = task.assignedTo.supervisor
                if (supervisor?.supervisor) {
                    createTaskOverdueNotification(task, supervisor.supervisor, 2)
                }
            } else if (existingYesterdayNotification && existingYesterdayNotification.escalationLevel == 2) {
                // Further escalation if needed
                def supervisor = task.assignedTo.supervisor?.supervisor
                if (supervisor?.supervisor) {
                    createTaskOverdueNotification(task, supervisor.supervisor, 3)
                }
            }
        }
    }

    private String buildOverdueMessage(Task task, int escalationLevel) {
        def assigneeName = "${task.assignedTo.firstName} ${task.assignedTo.lastName}"
        def taskTitle = task.title
        def deadlineDate = task.deadlineDate.format('dd/MM/yyyy')

        switch (escalationLevel) {
            case 1:
                return "Task '${taskTitle}' assigned to ${assigneeName} is overdue (deadline: ${deadlineDate}). Please follow up."
            case 2:
                return "ESCALATED: Task '${taskTitle}' assigned to ${assigneeName} remains incomplete after supervisor notification (deadline: ${deadlineDate}). Immediate attention required."
            case 3:
                return "CRITICAL ESCALATION: Task '${taskTitle}' assigned to ${assigneeName} is significantly overdue (deadline: ${deadlineDate}). Executive intervention may be required."
            default:
                return "Task '${taskTitle}' assigned to ${assigneeName} requires attention (deadline: ${deadlineDate})."
        }
    }

    def getUnreadNotificationCount(Employee employee) {
        return Notification.countByRecipientAndIsRead(employee, false)
    }

    def getNotificationsForEmployee(Employee employee, Map params = [:]) {
        def criteria = Notification.createCriteria()
        return criteria.list(params) {
            eq('recipient', employee)
            order('sentDate', 'desc')
        }
    }
}