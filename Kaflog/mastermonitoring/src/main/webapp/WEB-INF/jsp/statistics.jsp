<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="/webjars/bootstrap/3.0.3/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="http://code.jquery.com/ui/1.9.2/themes/base/jquery-ui.css"/>
    <link rel="stylesheet" type="text/css" href="js/jquery.datetimepicker.css"/>
    <script type="text/javascript" src="/webjars/jquery/2.0.3/jquery.min.js"></script>
    <script src="http://code.jquery.com/ui/1.9.2/jquery-ui.js"></script>
    <script src="js/jquery.datetimepicker.js"></script>
    <script>
        $(function () {
            $("#from_datetime").datetimepicker({
                maxDate: "2014/06/17"
            });
            $("#to_datetime").datetimepicker({
                maxDate: "2014/06/17"
            });
        });
        function getDateTime(str) {
            try {
                var date = str.split(" ")[0].split("/");
                var time = str.split(" ")[1].split(":");
                return new Date(date[0], date[1], date[2], time[0], time[1], 0, 0);
            } catch (err) {
                return null;
            }
        }
        function validateForm() {
            var from = getDateTime(document.forms["data_range"]["from_datetime"].value);
            var to = getDateTime(document.forms["data_range"]["to_datetime"].value);
            if (from == null) {
                alert("Plese fill start date");
                return false;
            }
            if (to == null) {
                alert("Plese fill end date");
                return false;
            }
            if (from >= to) {
                alert("Start date must be before end date");
                return false;
            }
            return true;
        }
    </script>
    <title>Statistics</title>
</head>

<body>

<div class="navbar navbar-default navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/monitoring">Kaflog</a>
        </div>
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li><a href="/monitoring">Monitoring</a></li>
                <li><a href="/log_stream">Log stream</a></li>
                <li class="active"><a href="/statistics">Statistics</a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li><a href="/logout">Log out</a></li>
            </ul>
        </div>
        <!--/.nav-collapse -->
    </div>
</div>


<br>

<div style="display: table; margin: 0 auto;">

    <br/><br/><br/>

    <form name="data_range" onsubmit="return validateForm()" action="statistics" method="GET">
        <div>
            <p>Please select time range to generate raport</p>
        </div>
        <div>
            <span>Start date</span>
            <input name="from" id="from_datetime" type="text"/>
            <span>End date</span>
            <input name="to" id="to_datetime" type="text"/>
            <input type="submit" name="submit" value="Go"/>
        </div>
    </form>
</div>

<br/><br/>

<% if(request.getAttribute("report")!=null) {%>
<% pl.edu.agh.kaflog.master.statistics.Report report = (pl.edu.agh.kaflog.master.statistics.Report)request.getAttribute("report"); %>
<div style="margin: 70px 10px 10px;">
    <table class="table table-striped table-hover" style="width: 100%; table-layout: fixed;">
        <thead>
        <tr>
            <th>HOST</th>
            <% for (pl.edu.agh.kaflog.master.statistics.Pair<String, Long> pair : report.getSeverityData()) {%>
            <th><%= pair.getFirst() %>
            </th>
            <% }%>
            <th>ALL</th>
        </tr>
        </thead>
        <tr>
            <td>Cluster</td>
            <% for (pl.edu.agh.kaflog.master.statistics.Pair<String, Long> pair : report.getSeverityData()) {%>
            <td><%= pair.getSecond() %>
            </td>
            <% }%>
            <td><%= report.getAll() %>
            </td>
        </tr>

        <%
            long all;
            for (java.util.Map.Entry<String, java.util.Map<String, Long>> entry : report.getHostSeverityData().entrySet()) { %>
        <tr>
            <td><%= entry.getKey() %>
            </td>
            <%
                all = 0;
                for (pl.edu.agh.kaflog.master.statistics.Pair<String, Long> pair : report.getSeverityData()) { %>

            <td><%= entry.getValue().containsKey(pair.getFirst()) ?entry.getValue().get(pair.getFirst()):0 %>  </td>
            <%all += entry.getValue().containsKey(pair.getFirst()) ?entry.getValue().get(pair.getFirst()):0;%>
            <% } %>
            <td><%=all%>
            </td>
        </tr>
        <% } %>

    </table>
</div>
<%}%>

</body>
</html>