package ems

import grails.gorm.transactions.Transactional

class BootStrap {

    EmployeeManagerService employeeManagerService

    def init = { servletContext ->
        createRoles()
        createBranch()
        createDefaultEmployees()
    }

    @Transactional
    void createRoles() {
        if (!Role.count()) {
            new Role(authority: 'ROLE_ADMIN').save(flush: true)
            new Role(authority: 'ROLE_DIRECTOR').save(flush: true)
            new Role(authority: 'ROLE_MANAGER').save(flush: true)
            new Role(authority: 'ROLE_STAFF').save(flush: true)
        }
    }

    @Transactional
    void createBranch() {
        if (!Branch.count()) {
            new Branch(name: 'KTM').save(flush: true)
        }
    }

    @Transactional
    void createDefaultEmployees(){
        if (!Employee.count()) {
            def adminRole = Role.findByAuthority('ROLE_ADMIN')
            def directorRole = Role.findByAuthority('ROLE_DIRECTOR')
            def managerRole = Role.findByAuthority('ROLE_MANAGER')
            def branch = Branch.findByName('KTM')

            // Create admin
            def admin = new Employee(
                    firstName: 'John', lastName: 'Doe', email: 'johndoe@gmail.com',
                    phone: '1234567890', branch: branch, supervisor: null, role: adminRole
            )
            admin.username = 'admin'
            admin.password = 'admin123'
            employeeManagerService.save(admin)

            // Create director
            def director = new Employee(
                    firstName: 'Jane', lastName: 'Smith', email: 'jane@gmail.com',
                    phone: '9876543210', branch: branch, supervisor: admin, role: directorRole
            )
            director.username = 'director'
            director.password = 'director123'
            employeeManagerService.save(director)

            // Create manager
            def manager = new Employee(
                    firstName: 'Bob', lastName: 'Johnson', email: 'bob@gmail.com',
                    phone: '5555555555', branch: branch, supervisor: director, role: managerRole
            )
            manager.username = 'manager'
            manager.password = 'manager123'
            employeeManagerService.save(manager)
        }
    }

    def destroy = {
    }
}