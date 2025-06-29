package ems

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException
import org.springframework.dao.DataIntegrityViolationException

class TaskController {

    TaskService taskService
    TaskManagerService taskManagerService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    @Secured(['ROLE_DIRECTOR', 'ROLE_MANAGER', 'ROLE_STAFF'])
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond taskService.list(params), model:[taskCount: taskService.count()]
    }

    @Secured(['ROLE_DIRECTOR', 'ROLE_MANAGER', 'ROLE_STAFF'])
    def show(Long id) {
        respond taskService.get(id)
    }

    @Secured(['ROLE_DIRECTOR', 'ROLE_MANAGER'])
    def create() {
        respond new Task(params), model: [
                employeeList: Employee.list()
        ]
    }

    @Secured(['ROLE_DIRECTOR', 'ROLE_MANAGER'])
    def save() {
        def success = taskManagerService.saveTask(params)
        if (success) {
            flash.message = "Task created successfully"
            redirect(action: "index")
        } else {
            flash.error = "Error saving task"
            render(view: "create")
        }
    }

    @Secured(['ROLE_STAFF', 'ROLE_DIRECTOR', 'ROLE_MANAGER'])
    def edit(Long id) {
        respond taskService.get(id)
    }

    @Secured(['ROLE_STAFF', 'ROLE_DIRECTOR', 'ROLE_MANAGER'])
    def update(Task task) {
        if (task == null) {
            notFound()
            return
        }
        try {
            taskService.save(task)
            flash.message = "Task updated successfully"
            redirect(action: "show", id: task.id)
        } catch (ValidationException e) {
            respond task.errors, view:'edit'
        }
    }

    @Transactional
    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        def task = Task.get(id)
        if (task == null) {
            notFound()
            return
        }

        try {
            // Clear the employee references before deletion
            task.assignedTo = null
            task.assignedBy = null
            task.save(flush: true)

            // Now delete the task
            task.delete(flush: true)

            flash.message = message(code: 'default.deleted.message', args: [message(code: 'task.label', default: 'Task'), id])
            redirect(action: "index")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = "Task cannot be deleted due to database constraints. Please contact administrator."
            redirect(action: "show", id: id)
        }
        catch (Exception e) {
            log.error("Error deleting task ${id}: ${e.message}", e)
            flash.message = "Task could not be deleted: ${e.message}"
            redirect(action: "show", id: id)
        }
    }

    protected void notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'task.label', default: 'Task'), params.id])
        redirect(action: "index")
    }
}