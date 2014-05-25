window.gauges = {};
var maxLogsPerSec = 300;
var gaugeOpts = {
    lines: 12, // The number of lines to draw
    angle: 0.1, // The length of each line
    lineWidth: 0.33, // The line thickness
    pointer: {
        length: 0.79, // The radius of the inner circle
        strokeWidth: 0.057, // The rotation offset
        color: '#000000' // Fill color
    },
    limitMax: true, // If true, the pointer will not go past the end of the gauge
    colorStart: '#6FADCF', // Colors
    colorStop: '#8FC0DA', // just experiment with them
    strokeColor: '#E0E0E0', // to see which ones work best for you
    generateGradient: true
};


function updateAll(data) {
    var rows = data.split('+');
    var counter = 0;
    var updated = [];

    // Tokens are:
    // 0: hostname
    // 1: ipAddress
    // 2: offlineFor
    // 3: uptime
    // 4: logsInLastMin
    // 5: logsInLastHour
    // 6: logsInLastDay
    // 7: logsOverall
    for (var i = 0; i < rows.length; i++) {
        var tokens = rows[i].split(';');
        if ($('#' + tokens[0]).length == 0) {
            createRow(tokens[0], tokens[1]);
        }
        updateRow(tokens[0], tokens[2], tokens[3], tokens[4], tokens[5], tokens[6], tokens[7]);
        updated[counter++] = tokens[0];
    }

    var allRows = $('.monitoring_row');
    for (i = 0; i < allRows.length; i++) {
        var id = $(allRows[i]).attr('id');
        if ($.inArray(id, updated) == -1) {
            $('#' + id).remove();
        }
    }
}


function createRow(rowId, ipAddress) {
    var rowBody = '<tr class="monitoring_row" id="' + rowId + '">' + $('#dummy_row').html() + '</tr>';
    $('#main_table').append(rowBody);
    var currentRow = $('#' + rowId);

    $(currentRow).find('.hostname_holder').html(rowId);
    $(currentRow).find('.ip_holder').html(ipAddress);

    window.gauges[rowId + '_min'] = new Gauge($(currentRow).find('.gauge_minute')[0]).setOptions(gaugeOpts);
    window.gauges[rowId + '_min'].maxValue = maxLogsPerSec;
    window.gauges[rowId + '_min'].animationSpeed = 1;
    window.gauges[rowId + '_min'].set(1);

    window.gauges[rowId + '_hour'] = new Gauge($(currentRow).find('.gauge_hour')[0]).setOptions(gaugeOpts);
    window.gauges[rowId + '_hour'].maxValue = maxLogsPerSec;
    window.gauges[rowId + '_hour'].animationSpeed = 1;
    window.gauges[rowId + '_hour'].set(1);

    window.gauges[rowId + '_day'] = new Gauge($(currentRow).find('.gauge_day')[0]).setOptions(gaugeOpts);
    window.gauges[rowId + '_day'].maxValue = maxLogsPerSec;
    window.gauges[rowId + '_day'].animationSpeed = 1;
    window.gauges[rowId + '_day'].set(1);

    window.gauges[rowId + '_ovr'] = new Gauge($(currentRow).find('.gauge_overall')[0]).setOptions(gaugeOpts);
    window.gauges[rowId + '_ovr'].maxValue = maxLogsPerSec;
    window.gauges[rowId + '_ovr'].animationSpeed = 1;
    window.gauges[rowId + '_ovr'].set(1);
}

function updateRow(rowId, offlineFor, uptime, logsInLastMin, logsInLastHour, logsInLastDay, logsOverall) {
    var row = $('#' + rowId);

    if (offlineFor == 0) {
        $(row).find('.image_holder').html('<img class="up8px" src="/images/status_ok.png"/>');
    } else {
        $(row).find('.image_holder').html(
                '<img class="up8px" src="/images/status_error.png"/>' +
                '<span class="offline_info">Offline: ' + offlineFor + ' s</span>');
    }

    var hours = ~~(uptime / 3600);
    if (hours < 10) hours = '0' + hours;

    var minutes = ~~((uptime % 3600) / 60);
    if (minutes < 10) minutes = '0' + minutes;

    var seconds = ~~(uptime % 60);
    if (seconds < 10) seconds = '0' + seconds;

    $(row).find('.uptime_holder').html(hours + ":" + minutes + ":" + seconds);

    $(row).find('.logs_minute').html(logsInLastMin + ' logs');
    $(row).find('.logsps_minute').html((logsInLastMin / 60).toFixed(2) + ' logs/s');
    window.gauges[rowId + '_min'].set(Math.min(1000, ~~(logsInLastMin / 60) + 1));

    $(row).find('.logs_hour').html(logsInLastHour + ' logs');
    $(row).find('.logsps_hour').html((logsInLastHour / 3600).toFixed(2) + ' logs/s');
    window.gauges[rowId + '_hour'].set(Math.min(1000, ~~(logsInLastHour / 3600) + 1));

    $(row).find('.logs_day').html(logsInLastDay + ' logs');
    $(row).find('.logsps_day').html((logsInLastDay / 86400).toFixed(2) + ' logs/s');
    window.gauges[rowId + '_day'].set(Math.min(1000, ~~(logsInLastDay / 86400) + 1));

    $(row).find('.logs_overall').html(logsOverall + ' logs');
    $(row).find('.logsps_overall').html((logsOverall / uptime).toFixed(2) + ' logs/s');
    window.gauges[rowId + '_ovr'].set(Math.min(1000, ~~(logsOverall / uptime) + 1));
}
