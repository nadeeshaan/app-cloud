/*
 *
 *   Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on anselectedRevision
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

var selectedRevisionLogMap = {};
var selectedRevisionReplicaList = [];
var selectedReplica;
var editor;
var isLogsAvailable = false;
var timerId;
$(document).ready(function () {
    editor = CodeMirror.fromTextArea(document.getElementById("build-logs"), {
        styleActiveLine: true,
        lineNumbers: true,
        readOnly: true,
        searchonly: true,
        lineWrapping: true,
        theme:'icecoder'
    });
    initData(selectedRevision, true);
    timerId = setInterval(function(){ initData(selectedRevision, false); }, 3000);
});

function regerateReplicasList(selectedRevisionReplicaList) {
    $('#replicas').empty();
    for (var i = 0; i < selectedRevisionReplicaList.length; i++) {
        var $option = $('<option value="' + selectedRevisionReplicaList[i] + '">' + selectedRevisionReplicaList[i] + '</option>');
        if (i == 0) {
            selectedReplica = selectedRevisionReplicaList[i];
            $option.attr('selected', 'selected');
        }
        $('#replicas').append($option);
    }
}

function setLogArea(logVal, isFirstRequest){
    $('#build-logs').empty();
    editor.setValue("");
    if(!isFirstRequest) {
        var currentLog = $('#build-logs').val();
        logVal = currentLog + logVal;
    }
    $('#build-logs').val(logVal);
    editor.setValue(logVal);
    var scroller = editor.getScrollInfo();
    editor.scrollTo(0, scroller.height);
    $('.log-search').focus();
}

function initData(selectedRevision, isFirstRequest){
    if(isFirstRequest){
        $('#replicas').empty();
        setLogArea("Loading...", true);
    }
    jagg.post("../blocks/runtimeLogs/ajax/runtimeLogs.jag", {
        action:"getSnapshotLogs",
        applicationKey:applicationKey,
        selectedRevision:selectedRevision,
        isFirstRequest:isFirstRequest
    },function (result) {
        initelements();
        result = result.replace(/\t+/g, "    ");
        selectedRevisionLogMap = jQuery.parseJSON(result);
        if(!jQuery.isEmptyObject(selectedRevisionLogMap)){
            $("#log-download").removeClass("btn-action btn disabled").addClass("btn-action");
            selectedRevisionReplicaList = Object.keys(selectedRevisionLogMap);
            if(isFirstRequest){
                regerateReplicasList(selectedRevisionReplicaList);
            }
            setLogArea(selectedRevisionLogMap[selectedRevisionReplicaList[0]], isFirstRequest);
        } else {
            //Check for application revision status and display correct message
            jagg.post("../blocks/runtimeLogs/ajax/runtimeLogs.jag", {
                action: "getApplicationRevisionStatus",
                applicationKey: applicationKey,
                selectedRevision: selectedRevision
            }, function(result) {
                result = result.trim();
                var revisionStatus = result;
                if (revisionStatus == "stopped") {
                clearInterval(timerId);
                    jagg.message({
                        content: "Application is currently stopped, logs will be available after restarting.",
                        type: 'information',
                        id: 'view_log',
                        timeout: '20000'
                    });
                    setLogArea("Application is stopped, Since logs are currently not available.", true);
                } else {
                    jagg.message({
                        content: "Deployment in progress. Please wait",
                        type: 'information',
                        id: 'view_log',
                        timeout: '8000'
                    });
                }
            }, function(jqXHR, textStatus, errorThrown) {
                jagg.message({
                    content: "Error occurred while getting application revision status.",
                    type: 'error',
                    id: 'view_log'
                });
            });
        }
    },function (jqXHR, textStatus, errorThrown) {
        $('#revision').prop("disabled", false);
        jagg.message({content: "Error occurred while loading the logs.", type: 'error', id:'view_log'});
    });
}

function initelements(){
    //Url text loaded from the span element
    var urlText = $('.version-url a span').text();

    //Maximum character limit is 90. further than that the text would not show and the title would!
    if(urlText.length > 90){
        $('.version-url a').prop('title',urlText).find('span').text(urlText);
    }else{
        $('.version-url a span').text(urlText);
    }

    var revisionElement = $('#revision');
    revisionElement.prop("disabled", false);

    revisionElement.on('change', function (e) {
        selectedRevision = this.value;
        $(this).prop("disabled", true);
        initData(selectedRevision, true);
    });

    $('#replicas').on('change', function (e) {
        selectedReplica = this.value;
        setLogArea(selectedRevisionLogMap[selectedReplica],true);
    });

    $('#log-download').off('click').on('click', downloadLogs);
}

function downloadLogs(e) {
    $('#log-download').off('click');
    $("#log-download").removeClass("btn-action").addClass("btn-action btn disabled");
    jagg.post("../blocks/runtimeLogs/ajax/runtimeLogs.jag", {
        action:"downloadLogs",
        applicationKey:applicationKey,
        selectedRevision:selectedRevision
    },function (result) {
        result = result.replace(/\t+/g, "    ");
        selectedRevisionLogMap = jQuery.parseJSON(result);
        if(!jQuery.isEmptyObject(selectedRevisionLogMap)){
            selectedRevisionReplicaList = Object.keys(selectedRevisionLogMap);
            saveTextAsFile(selectedRevisionLogMap[selectedRevisionReplicaList[0]]);
        }
    },function (jqXHR, textStatus, errorThrown) {
        $('#revision').prop("disabled", false);
        jagg.message({content: "Error occurred while downloading the logs.", type: 'error', id:'view_log'});
    });
}

function saveTextAsFile(textToWrite) {
    var textFileAsBlob = new Blob([textToWrite], {type:'text/plain'});
    var fileNameToSaveAs = applicationKey + "-" + selectedRevision + ".log";

    var downloadLink = document.createElement("a");
    downloadLink.download = fileNameToSaveAs;
    downloadLink.innerHTML = "Download File";
    downloadLink.href = window.URL.createObjectURL(textFileAsBlob);
    downloadLink.onclick = function (e){downloadLink.remove();};
    downloadLink.style.display = "none";
    document.body.appendChild(downloadLink);

    downloadLink.click();
}
