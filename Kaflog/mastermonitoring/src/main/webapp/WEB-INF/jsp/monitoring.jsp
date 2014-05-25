<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="/webjars/bootstrap/3.0.3/css/bootstrap.min.css"/>
    <script type="text/javascript" src="/webjars/jquery/2.0.3/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/gauge.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/monitoring.js"></script>
    <title>Monitoring</title>

    <style type="text/css">
        .height103 {
            line-height: 103px !important;
        }

        .width130 {
            width: 130px;
        }

        .width250 {
            width: 250px;
        }

        .image_holder {
            position: relative;
        }

        .up8px {
            margin-top: -8px;
        }

        .marbot10px {
            margin-bottom: 10px;
        }

        .margin0 {
            margin: 0;
        }

        .offline_info {
            line-height: 15px;
            position: absolute;
            top: 78px;
            left: 0;
            font-style: italic;
        }
    </style>
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
                <li class="active"><a href="/monitoring">Monitoring</a></li>
                <li><a href="/log_stream">Log stream</a></li>
                <li><a href="/statistics">Statistics</a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li><a href="/logout">Log out</a></li>
            </ul>
        </div>
        <!--/.nav-collapse -->
    </div>
</div>

<div style="margin: 70px 10px 10px;">
    <table class="table table-striped table-hover" style="width: 100%; table-layout: fixed;">
        <thead>
        <tr>
            <th class="width130">Status</th>
            <th class="width250">Node</th>
            <th>Uptime</th>
            <th class="width130 text-center">Last minute</th>
            <th class="width130 text-center">Last hour</th>
            <th class="width130 text-center">Last day</th>
            <th class="width130 text-center">Overall</th>
        </tr>
        </thead>
        <%--Monitoring goes here--%>
        <tbody id="main_table">
        <tr style="display: none" id="dummy_row">
            <td class="height103 width130">
                <div class="image_holder">
                    <img class="up8px" src="/images/status_ok.png"/>
                </div>
            </td>

            <td>
                <h3 class="hostname_holder">hostname</h3>
                <h4 class="ip_holder">10.0.0.1</h4>
            </td>

            <td>
                <h4 class="height103 margin0 uptime_holder">00:00:00</h4>
            </td>

            <td class="width130">
                <div class="text-center">
                    <p class="logs_minute marbot10px">0 logs</p>
                    <canvas class="gauge_minute" height="40" width="60"></canvas>
                    <p class="logsps_minute up8px">0 logs/s</p>
                </div>
            </td>

            <td class="width130">
                <div class="text-center">
                    <p class="logs_hour marbot10px">0 logs</p>
                    <canvas class="gauge_hour" height="40" width="60"></canvas>
                    <p class="logsps_hour up8px">0 logs/s</p>
                </div>
            </td>

            <td class="width130">
                <div class="text-center">
                    <p class="logs_day marbot10px">0 logs</p>
                    <canvas class="gauge_day" height="40" width="60"></canvas>
                    <p class="logsps_day up8px">0 logs/s</p>
                </div>
            </td>

            <td class="width130">
                <div class="text-center">
                    <p class="logs_overall marbot10px">0 logs</p>
                    <canvas class="gauge_overall" height="40" width="60"></canvas>
                    <p class="logsps_overall up8px">0 logs/s</p>
                </div>
            </td>
        </tr>
        </tbody>
    </table>
</div>

<script type="text/javascript">
//    function updateMonitoring() {
//        $.ajax({url: 'poll_monitoring'})
//                .fail(function (jqXHR, textStatus, errorThrown) {
//                    clearInterval(id);
//                    $('#main_table').html("<tr><td>Error</td><td>No connection to server</td><td>-</td><td>-</td><td>-</td><td>-</td><td>-</td></tr>");
//                })
//                .done(function (response, textStatus, jqXHR) {
//                    if (response != "") {
//                        console.log(response);
//                        updateAll(response);
//                    }
//                });
//    }
//    //var id = setInterval(updateMonitoring, 5000);
</script>

</body>

</html>