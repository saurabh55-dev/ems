package ems

class Notification {
    Task task
    Employee sentTo
    Date dateCreated
    Boolean isManaged = false
    
    static constraints = {
        task nullable: false
        sentTo nullable: false
        isManaged nullable: false
    }
}