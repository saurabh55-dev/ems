<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'task.label', default: 'Task')}" />
    <title><g:message code="default.show.label" args="[entityName]" /></title>
</head>
<body>
<div id="content" role="main">
    <div class="container">
        <section class="row">
            <a href="#show-task" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
            <div class="nav" role="navigation">
                <ul>
                    <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                    <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                    <sec:ifNotGranted roles="ROLE_STAFF">
                        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
                    </sec:ifNotGranted>
                </ul>
            </div>
        </section>
        <section class="row">
            <div id="show-task" class="col-12 content scaffold-show" role="main">
                <h1><g:message code="default.show.label" args="[entityName]" /></h1>
                <g:if test="${flash.message}">
                    <div class="message" role="status">${flash.message}</div>
                </g:if>

                <div class="property-list task">
                    <g:if test="${this.task?.title}">
                        <div class="property">
                            <span class="property-label">Title:</span>
                            <span class="property-value">${this.task.title}</span>
                        </div>
                    </g:if>

                    <g:if test="${this.task?.description}">
                        <div class="property">
                            <span class="property-label">Description:</span>
                            <span class="property-value">${this.task.description}</span>
                        </div>
                    </g:if>

                    <g:if test="${this.task?.priority}">
                        <div class="property">
                            <span class="property-label">Priority:</span>
                            <span class="property-value">${this.task.priority}</span>
                        </div>
                    </g:if>

                    <g:if test="${this.task?.deadline}">
                        <div class="property">
                            <span class="property-label">Deadline:</span>
                            <span class="property-value"><g:formatDate date="${this.task.deadline}" format="dd-MM-yyyy HH:mm"/></span>
                        </div>
                    </g:if>

                    <g:if test="${this.task?.assignedTo}">
                        <div class="property">
                            <span class="property-label">Assigned To:</span>
                            <span class="property-value">${this.task.assignedTo?.fullName} (${this.task.assignedTo?.role})</span>
                        </div>
                    </g:if>

                    <g:if test="${this.task?.assignedBy}">
                        <div class="property">
                            <span class="property-label">Assigned By:</span>
                            <span class="property-value">${this.task.assignedBy?.fullName}</span>
                        </div>
                    </g:if>

                    <g:if test="${this.task?.status}">
                        <div class="property">
                            <span class="property-label">Status:</span>
                            <span class="property-value">
                                <span class="badge badge-${this.task.status == 'COMPLETED' ? 'success' : this.task.status == 'IN_PROGRESS' ? 'warning' : this.task.status == 'ASSIGNED' ? 'info' : 'secondary'}">
                                    ${this.task.status}
                                </span>
                            </span>
                        </div>
                    </g:if>

                    <g:if test="${this.task?.dateCreated}">
                        <div class="property">
                            <span class="property-label">Date Created:</span>
                            <span class="property-value"><g:formatDate date="${this.task.dateCreated}" format="dd-MM-yyyy HH:mm"/></span>
                        </div>
                    </g:if>

                </div>

                <g:form resource="${this.task}" method="DELETE">
                    <fieldset class="buttons">
                        <g:link class="edit" action="edit" resource="${this.task}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                        <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                    </fieldset>
                </g:form>
            </div>
        </section>
    </div>
</div>
</body>
</html>