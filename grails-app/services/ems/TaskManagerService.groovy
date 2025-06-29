package ems

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.web.servlet.mvc.GrailsParameterMap
import java.text.SimpleDateFormat

@Transactional
class TaskManagerService {

    SpringSecurityService springSecurityService

    def saveTask(GrailsParameterMap params) {
        try {
            // Parse the deadline date from the form (Grails datePicker creates structured params)
            def deadlineDate = null
            if (params.deadline) {
                try {
                    // Handle Grails datePicker structure
                    if (params.deadline instanceof Date) {
                        deadlineDate = params.deadline
                    } else if (params.deadline_year && params.deadline_month && params.deadline_day) {
                        // Grails datePicker creates separate year, month, day parameters
                        def year = params.deadline_year as Integer
                        def month = (params.deadline_month as Integer) - 1 // Calendar months are 0-based
                        def day = params.deadline_day as Integer

                        Calendar cal = Calendar.getInstance()
                        cal.set(year, month, day, 0, 0, 0)
                        cal.set(Calendar.MILLISECOND, 0)
                        deadlineDate = cal.time
                    } else if (params.deadline instanceof String) {
                        // If it's a string, try to parse it
                        def dateFormat = new SimpleDateFormat("yyyy-MM-dd")
                        deadlineDate = dateFormat.parse(params.deadline)
                    }
                } catch (Exception e) {
                    log.error("Error parsing deadline date: ${e.message}")
                    return false
                }
            }

            // Get current user and their associated Employee
            def currentUser = springSecurityService.currentUser
            def currentEmployee = null

            if (currentUser.employeeId) {
                currentEmployee = Employee.get(currentUser.employeeId)
            }

            if (!currentEmployee) {
                log.error("Current user is not associated with an Employee record")
                return false
            }

            def task = new Task(
                    title: params.title,
                    description: params.description,
                    priority: params.priority,
                    assignedTo: Employee.get(params.assignedTo?.toLong()),
                    assignedBy: currentEmployee,  // Use Employee instead of User
                    deadline: deadlineDate,
                    status: 'ASSIGNED'  // Start with NEW status
            )

            if (!task.save(flush: true)) {
                log.error("Task validation failed: ${task.errors}")
                return false
            }
            return true
        } catch (Exception e) {
            log.error("Error while saving task: ${e.message}", e)
            return false
        }
    }
}