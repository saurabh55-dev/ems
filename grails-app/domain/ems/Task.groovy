package ems

enum TaskStatus { PENDING, COMPLETED, ESCALATED }

class Task {
    String title
    String description
    Employee assignedTo
    Employee assignedBy
    TaskStatus status = TaskStatus.PENDING
    Date assignedDate = new Date()
    Date deadline
    boolean escalatedOnce = false
    boolean escalatedTwice = false

    static constraints = {
        title blank: false
        description maxSize: 1000
        deadline nullable: false
    }

    String toString() {
        "$title (${status})"
    }
}
