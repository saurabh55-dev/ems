package ems

class Branch {

    String name

    static hasMany = [employees: Employee]

    static constraints = {
        name blank:false, unique:true
    }

    String toString(){
        name
    }
}
