<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <title>Employee List</title>
</head>
<body>
<div class="container">
    <h1>Employee List</h1>

    <g:if test="${flash.message}">
        <div class="alert alert-info">${flash.message}</div>
    </g:if>

    <div class="mb-3">
        <g:link class="btn btn-primary" action="create">Create Employee</g:link>
    </div>

    <table class="table">
        <thead>
        <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Branch</th>
            <th>Role</th>
            <th>Supervisor</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${employeeList}" var="employee">
            <tr>
                <td>${employee.firstName} ${employee.lastName}</td>
                <td>${employee.email}</td>
                <td>${employee.phone}</td>
                <td>${employee.branch?.name}</td>
                <td>${employee.role}</td>
                <td>${employee.supervisor?.fullName ?: 'No Supervisor'}</td>
                <td>
                    <g:link class="btn btn-sm btn-info" action="show" id="${employee.id}">View</g:link>
                    <g:link class="btn btn-sm btn-primary" action="edit" id="${employee.id}">Edit</g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

    <div class="pagination">
        <g:paginate total="${employeeCount ?: 0}" />
    </div>
</div>
</body>
</html>
