package ems

import grails.gorm.transactions.Transactional
import org.springframework.scheduling.annotation.Scheduled
import java.text.SimpleDateFormat

@Transactional
class NotificationManagerService {

    // Run every 2 minutes for testing (change to 1 hour in production)
    @Scheduled(fixedRate = 60000L) // 60000ms = 1 minutes for testing
    void checkOverdueTasks() {
        println "=== SCHEDULER RUNNING: ${new Date()} ==="
        log.info "Running scheduled task: Checking for overdue tasks..."

        // Find all overdue tasks that are not completed or cancelled
        def allTasks = Task.list()
        println "Total tasks in database: ${allTasks.size()}"

        // Find all overdue tasks that are not completed or cancelled
        def overdueTasks = Task.findAll {
            deadline < new Date() &&
                    status in ['ASSIGNED', 'IN_PROGRESS']
        }

        println "Found ${overdueTasks.size()} overdue tasks"
        log.debug "Found ${overdueTasks.size()} overdue tasks"

        overdueTasks.each { task ->
            println "Processing overdue task: ${task.title} (deadline: ${task.deadline}, status: ${task.status})"
            processOverdueTask(task)
        }

        if (overdueTasks.size() == 0) {
            println "No overdue tasks found to process"
        }

        println "=== SCHEDULER COMPLETED ==="
    }

    private void processOverdueTask(Task task) {
        println "  -> Processing overdue task: ${task.title}"
        log.debug "Processing overdue task: ${task.title}"

        // Check if we already sent initial overdue notification
        def existingOverdueNotification = Notification.findByTaskAndType(task, 'TASK_OVERDUE')
        println "  -> Existing notification found: ${existingOverdueNotification != null}"

        if (!existingOverdueNotification) {
            println "  -> Sending initial overdue notification"
            // Send initial notification to supervisor
            sendInitialOverdueNotification(task)
        } else {
            println "  -> Checking for escalation (notification created: ${existingOverdueNotification.dateCreated})"
            // Check if supervisor has taken action within grace period (e.g., 24 hours)
            def graceHours = 24
            def gracePeriodExpired = (new Date().time - existingOverdueNotification.dateCreated.time) > (graceHours * 60 * 60 * 1000)

            println "  -> Grace period expired: ${gracePeriodExpired}"

            if (gracePeriodExpired && task.status in ['ASSIGNED', 'IN_PROGRESS']) {
                println "  -> Escalating to next supervisor"
                // Escalate to next level supervisor
                escalateToNextSupervisor(task, task.assignedTo)
            }
        }
    }

    private void sendInitialOverdueNotification(Task task) {
        def supervisor = task.assignedTo.supervisor
        println "    -> Task assigned to: ${task.assignedTo.fullName}"
        println "    -> Supervisor: ${supervisor?.fullName ?: 'None'}"

        if (supervisor) {
            def dateFormat = new SimpleDateFormat('dd/MM/yyyy')
            def formattedDeadline = dateFormat.format(task.deadline)
            def message = "Task '${task.title}' assigned to ${task.assignedTo.fullName} is overdue (deadline was ${formattedDeadline}). Please take necessary action."

            println "    -> Creating notification for supervisor: ${supervisor.fullName}"

            def success = createNotification(
                    message: message,
                    type: 'TASK_OVERDUE',
                    recipient: supervisor,
                    task: task,
                    escalatedFrom: task.assignedTo
            )

            if (success) {
                println "    -> ✓ Notification created successfully"
                log.info "Sent overdue notification to supervisor: ${supervisor.fullName} for task: ${task.title}"
            } else {
                println "    -> ✗ Failed to create notification"
            }
        } else {
            println "    -> No supervisor found - no notification sent"
            log.info "No supervisor found for employee: ${task.assignedTo.fullName}. No notification sent."
        }
    }

    private void escalateToNextSupervisor(Task task, Employee currentEmployee) {
        def nextSupervisor = currentEmployee.supervisor?.supervisor

        if (nextSupervisor) {
            // Check if we already escalated to this level
            def existingEscalation = Notification.findByTaskAndTypeAndRecipient(task, 'TASK_ESCALATION', nextSupervisor)

            if (!existingEscalation) {
                def message = "Task '${task.title}' assigned to ${task.assignedTo.fullName} remains overdue. The immediate supervisor ${currentEmployee.supervisor.fullName} has not taken action within the grace period. Please review and take necessary action."

                createNotification(
                        message: message,
                        type: 'TASK_ESCALATION',
                        recipient: nextSupervisor,
                        task: task,
                        escalatedFrom: currentEmployee.supervisor
                )

                log.info "Escalated task '${task.title}' to higher supervisor: ${nextSupervisor.fullName}"
            }
        } else {
            log.info "No higher supervisor found for escalation of task: ${task.title}"
        }
    }

    private boolean createNotification(Map params) {
        println "      -> Creating notification with params: ${params}"

        // Add these debug lines to check the objects
        println "      -> Recipient ID: ${params.recipient?.id}, attached: ${params.recipient?.isAttached()}"
        println "      -> Task ID: ${params.task?.id}, attached: ${params.task?.isAttached()}"
        println "      -> EscalatedFrom ID: ${params.escalatedFrom?.id}, attached: ${params.escalatedFrom?.isAttached()}"


        def notification = new Notification(
                message: params.message,
                type: params.type,
                recipient: params.recipient,
                task: params.task,
                escalatedFrom: params.escalatedFrom
        )

        // Add validation before save
        if (!notification.validate()) {
            println "      -> Validation failed:"
            notification.errors.allErrors.each { error ->
                println "        Field: ${error.field}, Code: ${error.code}, Message: ${error.defaultMessage}"
            }
            return false
        }

        println "      -> Notification object created and validated, attempting to save..."


        if (notification.save(flush: true)) {
            println "      -> ✓ Notification saved successfully with ID: ${notification.id}"
            log.debug "Notification created successfully for ${params.recipient.fullName}"
            return true
        } else {
            println "      -> ✗ Failed to save notification. Errors: ${notification.errors}"
            log.error "Failed to save notification: ${notification.errors}"
            notification.errors.allErrors.each { error ->
                println "        Error: ${error}"
            }
            return false
        }
    }

    // Helper method to get notifications for a specific employee
    def getNotificationsForEmployee(Employee employee, Map params = [:]) {
        return Notification.findAllByRecipient(employee, params)
    }

    // Helper method to mark notification as read
    def markAsRead(Long notificationId) {
        def notification = Notification.get(notificationId)
        if (notification) {
            notification.isRead = true
            notification.save(flush: true)
            return true
        }
        return false
    }

    // Helper method to get unread count for an employee
    def getUnreadCountForEmployee(Employee employee) {
        return Notification.countByRecipientAndIsRead(employee, false)
    }

    String toString(){
        "This was called to activate scheduler"
    }
}
