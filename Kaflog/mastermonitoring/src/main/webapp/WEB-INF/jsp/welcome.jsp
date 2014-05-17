<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
<head>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="/webjars/bootstrap/3.0.3/css/bootstrap.min.css" />
    <script type="text/javascript" src="/webjars/jquery/2.0.3/jquery.min.js"></script>
</head>

<body>

<br>
Message: ${message}

<table class="table table-striped table-hover">
    <caption class="text-left">Clients</caption>
    <thead>
        <th>#</th>
        <th>Addres</th>
    </thead>
    <tbody>
    <c:forEach items="${clients}" var="client" varStatus="status">
        <tr>
            <td>${status.index}</td>
            <td>${client}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

</body>

</html>