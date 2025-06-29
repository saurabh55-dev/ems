<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <title>Show Employee</title>
</head>
<body>
<div class="container">
    <h1>Employee Details</h1>

    <g:if test="${flash.message}">
        <div class="alert alert-info">${flash.message}</div>
    </g:if>

    <div class="card">
        <div class="card-body">
            <div class="row mb-2">
                <div class="col-2">Name</div>
                <div class="col-10">${employee.firstName} ${employee.lastName}</div>
            </div>

            <div class="row mb-2">
                <div class="col-2">Email</div>
                <div class="col-10">${employee.email}</div>
            </div>

            <div class="row mb-2">
                <div class="col-2">Phone</div>
                <div class="col-10">${employee.phone}</div>
            </div>

            <div class="row mb-2">
                <div class="col-2">Branch</div>
                <div class="col-10">${employee.branch?.name}</div>
            </div>

            <div class="row mb-2">
                <div class="col-2">Role</div>
                <div class="col-10">${employee.role}</div>
            </div>

            <div class="row mb-2">
                <div class="col-2">Supervisor</div>
                <div class="col-10">${employee.supervisor?.fullName ?: 'No Supervisor'}</div>
            </div>
        </div>
    </div>

    <div class="mt-3">
        <g:form resource="${employee}" method="DELETE">
            <g:link class="btn btn-primary" action="edit" resource="${employee}">Edit</g:link>
            <input class="btn btn-danger" type="submit" value="Delete" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
            <g:link class="btn btn-secondary" action="index">Back to List</g:link>
        </g:form>
    </div>
</div>
</body>
</html>