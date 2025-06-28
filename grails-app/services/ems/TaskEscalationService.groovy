package ems

import grails.gorm.transactions.Transactional

@Transactional
class TaskEscalationService {

    def escalateTasks(){
        def now = new Date()

        Task.findAllByStatus(TaskStatus.PENDING).each{task ->
            if( task.deadline < now){
                if( !task.escalatedOnce){
                    notifySupervisor(task)
                    task.escalatedOnce = true
                    task.status = TaskStatus.ESCALATED
                    task.save()
                } else if( !task.escalatedTwice){
                    def upper = task.assignedTo?.supervisor?.supervisor
                    if(upper){
                        println "Second escalation: Notify ${upper.fullName}"
                        task.escalatedTwice = true
                        task.save()
                    }
                }
            }
        }
    }

    private void notifySupervisor(Task task){
        def supervisor = task.assignedTo?.supervisor
        if(supervisor){
            println"First escalation: Notify ${supervisor.fullName} about task '${task.title}'"
        }
    }
}
