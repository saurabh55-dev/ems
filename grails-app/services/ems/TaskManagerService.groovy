package ems

import grails.gorm.transactions.Transactional
import org.springframework.scheduling.annotation.Scheduled

import java.text.SimpleDateFormat

@Transactional
class TaskManagerService {

    TaskService taskService // this is the GORM service interface

    def assignTask(Employee from, Employee to, String title, String description, Date deadline) {
        if ((from.position == 'Director' && to.position == 'Manager') ||
                (from.position == 'Manager' && to.position == 'Staff')) {

            def task = new Task(
                    title: title,
                    description: description,
                    assignedBy: from,
                    assignedTo: to,
                    deadline: deadline
            )

            return taskService.save(task)  // Use the GORM service to persist
        } else {
            throw new IllegalArgumentException("Invalid task assignment hierarchy.")
        }
    }

    boolean saveTask(Map params) {
        try {
            def sdf = new SimpleDateFormat("yyyy-MM-dd")
            def deadlineDate = sdf.parse(params.deadline)

            def task = new Task()
            task.title = params.title
            task.description = params.description
            task.deadline = deadlineDate
            task.assignedBy = Employee.get(params['assignedBy.id'] as Long)
            task.assignedTo = Employee.get(params['assignedTo.id'] as Long)

            return task.save(flush: true) != null
        } catch (Exception e) {
            log.error("Error while saving task: ${e.message}", e)
            return false
        }
    }

    // Check for overdue tasks and unmanaged notifications every hour
    @Scheduled(cron = "0 0 * * * *")  // Runs every hour
    void checkTasksAndNotifications() {
        checkOverdueTasks()
        checkUnmanagedNotifications()
    }

    private void checkOverdueTasks() {
        def now = new Date()
        def overdueTasks = Task.findAllByDeadlineLessThanAndStatus(now, TaskStatus.PENDING)

        overdueTasks.each { task ->
            // Check if there's any existing notification for this task
            def existingNotification = Notification.findByTask(task)
            if (!existingNotification) {
                // Send first notification to immediate supervisor
                sendNotification(task.assignedTo.supervisor, task)
            }
        }
    }

    private void checkUnmanagedNotifications() {
        def now = new Date()
        def oneDayAgo = now - 1  // Subtract 1 day

        // Find notifications that are:
        // 1. Not managed
        // 2. Created more than 24 hours ago
        // 3. Associated with tasks that aren't completed
        def unmanagedNotifications = Notification.createCriteria().list {
            eq('isManaged', false)
            lt('dateCreated', oneDayAgo)
            task {
                eq('status', TaskStatus.PENDING)
            }
        }

        unmanagedNotifications.each { notification ->
            Task task = notification.task
            Employee currentRecipient = notification.sentTo
            Employee nextLevelSupervisor = currentRecipient.supervisor

            if (nextLevelSupervisor) {
                // Check if we haven't already notified this supervisor
                def existingHigherNotification = Notification.findByTaskAndSentTo(task, nextLevelSupervisor)
                if (!existingHigherNotification) {
                    // Send notification to next level supervisor
                    sendNotification(nextLevelSupervisor, task)

                    // Update task status if needed
                    if (!task.escalatedOnce) {
                        task.escalatedOnce = true
                        task.save(flush: true)
                    } else if (!task.escalatedTwice) {
                        task.escalatedTwice = true
                        task.status = TaskStatus.ESCALATED
                        task.save(flush: true)
                    }
                }
            }
        }
    }

    private void sendNotification(Employee supervisor, Task task) {
        if (!supervisor) return

        // Create and save notification in database
        def notification = new Notification(
                task: task,
                sentTo: supervisor,
                isManaged: false
        )
        notification.save(flush: true)

        // Console output for debugging
        println "Notification sent to ${supervisor.fullName} (ID: ${supervisor.id}) about overdue task '${task.title}' assigned to ${task.assignedTo.fullName}"
    }

    // Method to mark notification as managed
    boolean markNotificationAsManaged(Long notificationId) {
        def notification = Notification.get(notificationId)
        if (notification) {
            notification.isManaged = true

            // Also update the task status if it was escalated
            Task task = notification.task
            if (task.status == TaskStatus.ESCALATED) {
                task.status = TaskStatus.PENDING
                task.save(flush: true)
            }

            return notification.save(flush: true) != null
        }
        return false
    }

    // Get all unmanaged notifications for a supervisor
    List<Notification> getUnmanagedNotificationsForSupervisor(Employee supervisor) {
        Notification.findAllBySentToAndIsManaged(supervisor, false)
    }

}
