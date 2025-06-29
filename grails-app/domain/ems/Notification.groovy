package ems

class Notification {
    String message
    String type // 'TASK_OVERDUE', 'TASK_ESCALATION'
    Date dateCreated
    boolean isRead = false
    Employee recipient
    Task task
    Employee escalatedFrom // The employee who was originally responsible

    static constraints = {
        message nullable: false, maxSize: 1000, blank: false
        type nullable: false, inList: ['TASK_OVERDUE', 'TASK_ESCALATION']
        recipient nullable: false
        task nullable: true
        escalatedFrom nullable: true
        dateCreated nullable: true
    }

    static mapping = {
        message type: 'text'
        sort dateCreated: 'desc'
    }

    String toString() {
        return "${type}: ${message}"
    }
}