package ems

class NotificationSchedulerJob {

    NotificationService notificationService

    static triggers = {
        // Run every day at 9:00 AM
        cron name: 'overdueTaskNotificationTrigger', cronExpression: '0 0 9 * * ?'

        // Alternative: Run every hour during business hours (9 AM to 6 PM)
        // cron name: 'overdueTaskNotificationTrigger', cronExpression: '0 0 9-18 * * ?'
    }

    def execute() {
        log.info("Starting overdue task notification job...")

        try {
            notificationService.checkAndSendOverdueNotifications()
            log.info("Overdue task notification job completed successfully")
        } catch (Exception e) {
            log.error("Error in overdue task notification job: ${e.message}", e)
        }
    }
}