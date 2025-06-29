package ems

class Employee {
    String firstName
    String lastName
    String email
    String phone
    Branch branch
    String password
    String username
    Role role
    Employee supervisor

    static transients = ['password', 'username']

    static belongsTo = [branch: Branch, role: Role]

    static constraints = {
        firstName blank: false
        lastName blank: false
        email email: true, blank: false, unique: true
        phone blank: false
        branch nullable: false
        supervisor nullable: true
        role nullable: false
    }

    static mapping = {
        sort firstName: "asc"
    }

    String toString(){
        getFullName() + "(" + role + ")"
    }

    String getFullName() {
        "${firstName} ${lastName}"
    }
}