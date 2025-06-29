package ems

class Task {
    String title
    String description
    String priority // IMMEDIATE, HIGH, MEDIUM, NORMAL
    String status = 'ASSIGNED'
    Date dateCreated
    Date deadline
    Employee assignedTo
    Employee assignedBy

    static constraints = {
        title nullable: false, blank: false
        description nullable: false, blank: false
        priority nullable: false, inList: ['IMMEDIATE', 'HIGH', 'MEDIUM', 'NORMAL']
        status nullable: false, inList: ['ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED']
        deadline nullable: false
        assignedBy nullable: false
        assignedTo nullable: false
    }

    static mapping = {
        description type: 'text'
        sort 'deadline'
    }

    boolean isOverdue() {
        return deadline < new Date() && status != 'COMPLETED' && status != 'CANCELLED'
    }

    String toString() {
        return title
    }
}