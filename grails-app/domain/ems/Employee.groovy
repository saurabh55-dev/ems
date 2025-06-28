package ems

class Employee {

    String fullName
    String position  // "Director", "Manager", "Staff"
    Branch branch
    Employee supervisor

    static belongsTo = [branch: Branch]
    static hasMany = [subordinates: Employee]

    static constraints = {
        fullName blank: false
        position inList: ['Director', 'Manager', 'Staff']
        supervisor nullable: true
    }

    String toString() {
        "$fullName ($position)"
    }
}
