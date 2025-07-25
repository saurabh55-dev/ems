package ems

import grails.gorm.transactions.Transactional

import java.text.SimpleDateFormat

class BootStrap {

    EmployeeManagerService employeeManagerService
    NotificationManagerService notificationManagerService

    def init = { servletContext ->
        createRoles()
        createBranch()
        createDefaultEmployees()
        createSampleTasks()
        createTestNotifications()
        notificationManagerService.toString()
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
            new Branch(name: 'NPJ').save(flush: true)
        }
    }

    @Transactional
    void createDefaultEmployees(){
        if (!Employee.count()) {
            def adminRole = Role.findByAuthority('ROLE_ADMIN')
            def directorRole = Role.findByAuthority('ROLE_DIRECTOR')
            def managerRole = Role.findByAuthority('ROLE_MANAGER')
            def staffRole = Role.findByAuthority('ROLE_STAFF')
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

            // Create staff
            def staff = new Employee(
                    firstName: 'Alice', lastName: 'Wilson', email: 'alice@gmail.com',
                    phone: '4444444444', branch: branch, supervisor: manager, role: staffRole
            )
            staff.username = 'staff'
            staff.password = 'staff123'
            employeeManagerService.save(staff)
        }
    }

    // Helper method to subtract days from date
    private Date subtractDays(Date date, int days) {
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        cal.add(Calendar.DAY_OF_MONTH, -days)
        return cal.getTime()
    }

    // Helper method to add days to date
    private Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        cal.add(Calendar.DAY_OF_MONTH, days)
        return cal.getTime()
    }

    @Transactional
    void createSampleTasks() {
        if (!Task.count()) {
            def manager = Employee.findByFirstNameAndLastName('Bob', 'Johnson')
            def staff = Employee.findByFirstNameAndLastName('Alice', 'Wilson')

            if (manager && staff) {
                // Create an overdue task for testing
                def overdueTask = new Task(
                        title: 'Complete Monthly Report',
                        description: 'Prepare and submit the monthly performance report',
                        dateCreated: subtractDays(new Date(), 10),
                        deadline: subtractDays(new Date(), 2), // 2 days overdue
                        status: 'IN_PROGRESS',
                        priority: 'HIGH',
                        assignedTo: staff,
                        assignedBy: manager
                )
                overdueTask.save(flush: true)

                // Create a current task
                def currentTask = new Task(
                        title: 'Review Client Proposals',
                        description: 'Review and approve client proposals for next quarter',
                        dateCreated: new Date(),
                        deadline: addDays(new Date(), 7), // Due in 7 days
                        status: 'ASSIGNED',
                        priority: 'MEDIUM',
                        assignedTo: staff,
                        assignedBy: manager
                )
                currentTask.save(flush: true)

                // Create another overdue task for more testing
                def anotherOverdueTask = new Task(
                        title: 'Update System Documentation',
                        description: 'Update the system documentation with recent changes',
                        dateCreated: subtractDays(new Date(), 15),
                        deadline: subtractDays(new Date(), 5), // 5 days overdue
                        status: 'ASSIGNED',
                        priority: 'MEDIUM',
                        assignedTo: staff,
                        assignedBy: manager
                )
                anotherOverdueTask.save(flush: true)
            }
        }
    }

    @Transactional
    void createTestNotifications() {
        if (!Notification.count()) {
            def manager = Employee.findByFirstNameAndLastName('Bob', 'Johnson')
            def staff = Employee.findByFirstNameAndLastName('Alice', 'Wilson')
            def overdueTask = Task.findByTitle('Complete Monthly Report')

            if (manager && staff && overdueTask) {
                // Create a sample notification
                // Format the date properly
                def dateFormat = new SimpleDateFormat('dd/MM/yyyy')
                def formattedDeadline = dateFormat.format(overdueTask.deadline)

                def notification = new Notification(
                        message: "Task '${overdueTask.title}' assigned to ${staff.fullName} is overdue (deadline was ${formattedDeadline}). Please take necessary action.",
                        type: 'TASK_OVERDUE',
                        recipient: manager,
                        task: overdueTask,
                        escalatedFrom: staff,
                        dateCreated: new Date(),
                        isRead: false
                )
                notification.save(flush: true)

                log.info "Created test notification for manager"
            }
        }
    }

    def destroy = {
    }
}