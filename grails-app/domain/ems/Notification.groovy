package ems

class Notification {

    String message
    String type = 'TASK_OVERDUE' // TASK_OVERDUE, TASK_REMINDER, etc.
    boolean isRead = false
    Date sentDate = new Date()
    Date readDate

    Employee recipient
    Employee sender // System generated notifications can have null sender
    Task relatedTask

    // For escalation tracking
    int escalationLevel = 1 // 1 = first notification, 2 = escalated to supervisor's supervisor, etc.

    static constraints = {
        message nullable: false, blank: false, maxSize: 1000
        type nullable: false
        isRead nullable: false
        sentDate nullable: false
        readDate nullable: true
        recipient nullable: false
        sender nullable: true
        relatedTask nullable: true
        escalationLevel nullable: false, min: 1
    }

    static mapping = {
        message type: 'text'
        sort sentDate: 'desc'
    }

    def markAsRead() {
        isRead = true
        readDate = new Date()
        save(flush: true)
    }

    String toString() {
        return "${type}: ${message}"
    }
}