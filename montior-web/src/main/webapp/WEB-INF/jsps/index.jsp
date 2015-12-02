<%@ page contentType="text/html" pageEncoding="UTF-8" language="java" session="false" %>
<jsp:useBean id="it" scope="request" type="com.jay.montior.common.OverviewView"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="u" uri="http://www.montio.jay/util" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="root" value="${pageContext.request.contextPath}"/>
<c:set var="restRoot" value="${pageContext.request.contextPath}/${paths['Rest']}"/>

<t:genericpage>

    <jsp:attribute name="script">
        <script>
            var path = '${pageContext.request.contextPath}/${paths['Rest']}' + window.location.search;

            Array.prototype.unique = function() {
                var o = {}, i, l = this.length, r = [];
                for(i=0; i<l;i+=1) o[this[i]] = this[i];
                for(i in o) r.push(o[i]);
                return r;
            };

            var montiorApp = angular.module('montior', ['ngCookies']);

            montiorApp.controller('SettingsController', ['$scope', '$cookieStore',
                function ($scope, $cookieStore) {
                    // Transient.
                    $scope.showSettings = false;
                    $scope.showSettingsIcon = true;

                    // Settings.
                    $scope.columns = $cookieStore.get('columns') || 3;
                    $scope.rows = $cookieStore.get('rows') || 4;
                    $scope.boxSpacing = $cookieStore.get('boxSpacing') || 10;
                    $scope.fontSize = $cookieStore.get('fontSize') || 1.5;
                    $scope.showProjectName = $cookieStore.get('showProjectName') || false;
                    $scope.showHistory = $cookieStore.get('showHistory');
                    if ($scope.showHistory == undefined) $scope.showHistory = 5;
                    $scope.showRunning = $cookieStore.get('showRunning') || true;
                    $scope.showText = $cookieStore.get('showText') || true;
                    $scope.lie = $cookieStore.get('lie') || true;
                    $scope.monospaced = $cookieStore.get('monospaced') || true;

                    $scope.$watch('[columns,rows,boxSpacing,fontSize,showProjectName,showHistory,showRunning,showText]', function (value) {
                        $cookieStore.put('columns', $scope.columns);
                        $cookieStore.put('rows', $scope.rows);
                        $cookieStore.put('boxSpacing', $scope.boxSpacing);
                        $cookieStore.put('fontSize', $scope.fontSize);
                        $cookieStore.put('showProjectName', $scope.showProjectName);
                        $cookieStore.put('showHistory', $scope.showHistory);
                        $cookieStore.put('showRunning', $scope.showRunning);
                        $cookieStore.put('monospaced', $scope.monospaced);
                        $cookieStore.put('lie', $scope.showText);
                    });

                    // Derived.
                    $scope.boxWidth = function () { return (window.innerWidth - 100) / $scope.columns - $scope.boxSpacing; };
                    $scope.boxHeight = function () { return (window.innerHeight - 50) / $scope.rows - $scope.boxSpacing; };

                    $scope.toggleSettings = function () { $scope.showSettings = !$scope.showSettings;};
                    var hideSettingsTimeout = null;
                    $scope.movement = function () {
                        $scope.showSettingsIcon = true;
                        if (hideSettingsTimeout) clearTimeout(hideSettingsTimeout);
                        hideSettingsTimeout = setTimeout(function () {$scope.showSettingsIcon = $scope.showSettings;}, 3000);
                    };
                    $scope.movement();
                }]);

            montiorApp.controller('StatusesController', ['$scope', '$http', '$timeout',
                function ($scope, $http, $timeout) {
                    $scope.intervalFunction = function () {
                        $http.get(path).success(function (data) {
                            $scope.spinnerDisplay = 'none';
                            $scope.statuses = data;
                            $scope.projects = data.builds.map(function(build) {return build.buildType.projectName; }).unique();
                        }).error(function (error) {
                            $scope.spinnerDisplay = 'block';
                        });
                        $timeout($scope.intervalFunction, 1000);
                    };
                    $scope.intervalFunction();
                }]);

        </script>
    </jsp:attribute>

    <jsp:body>
        <div ng-controller="SettingsController" ng-mousemove="movement()" ng-class="{'monospace': monospaced}">
            <div id="mainDiv" class="row" ng-controller="StatusesController">

                <div class="grid-container col" ng-repeat="build in statuses.builds track by build.buildType.id"
                     ng-style="{'width': boxWidth() + 'px', 'height': boxHeight() + 'px', 'padding': boxSpacing / 2 + 'px'}">
                    <div class="build-status {{lie ? 'success' : build.latestFinished.status}} transformable-cubic">
                        <div class="transformable-cubic" title="{{build.buildType.projectName}} :: {{build.buildType.name}}">
                            <div ng-if="showText">
                                <h3 style="overflow-y: hidden" ng-style="{'font-size': fontSize + 'em', 'height': (fontSize * 3 * 0.9) + 'em'}">
                                    <a href="{{build.buildType.webUrl}}">
                                        <span ng-if="showProjectName">{{build.buildType.projectName}} ::</span>
                                        {{build.buildType.name}}
                                    </a>
                                </h3>
                            </div>

                            <div ng-if="showRunning && build.latestRunning">
                                <a href="{{build.latestRunning.webUrl}}">
                                    <div class="build-running {{build.latestRunning.status}}">
                                        <div ng-style="{'width': build.latestRunning.percentageComplete + '%'}" class="transformable-linear-slow">
                                            <div>{{build.latestRunning.percentageComplete}}%</div>
                                        </div>
                                    </div>
                                </a>
                            </div>

                            <div class="history" ng-if="build.lastTwenty.slice(0, showHistory).length > 0">
                                <i ng-repeat="status in build.lastTwenty.slice(0, showHistory) track by status.first" class="fa"
                                   ng-class="{'fa-check': status.second == 'success', 'fa-times': status.second != 'success'}"></i>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="spinner-container">

                    <div id="wrapper" style="text-align: center">
                        <div id="yourdiv" style="display: inline-block;">
                            <div class="spinner" ng-style="{ 'display': spinnerDisplay }">
                                <img src="${root}/img/loading.gif"/>

                                <h1>Disconnected</h1>
                            </div>

                        </div>
                    </div>
                </div>
            </div>

            <div class="settings transformable" ng-class="{'expanded':showSettings, 'invisible': !showSettingsIcon}">
                <div class="settings-inner">
                    <div class="form-group inline">
                        <label for="rowsInput">Rows</label>
                        <input id="rowsInput" ng-model="rows" type="number" min="1" max="20">
                    </div>
                    <div class="form-group inline">
                        <label for="columnsInput">Cols</label>
                        <input id="columnsInput" ng-model="columns" type="number" min="1" max="20">
                    </div>
                    <div class="form-group ">
                        <label for="boxSpacingInput">Spacing</label>
                        <input id="boxSpacingInput" ng-model="boxSpacing" type="number" min="1" max="100">
                    </div>
                    <div class="form-group ">
                        <label for="fontSizeInput">Font Size</label>
                        <input id="fontSizeInput" ng-model="fontSize" type="number" min="0.1" max="100" step="0.1">
                    </div>
                    <div class="form-group ">
                        <label for="showHistoryInput">History</label>
                        <input id="showHistoryInput" ng-model="showHistory" type="number" min="0" max="20">
                    </div>
                    <div class="form-group">
                        <input id="showTextInput" ng-model="showText" type="checkbox">
                        <label for="showTextInput">Show Name</label>
                    </div>
                    <div class="form-group">
                        <input id="showProjectName" ng-disabled="!showText" ng-model="showProjectName" type="checkbox">
                        <label for="showProjectName">Include Project Name</label>
                    </div>
                    <div class="form-group">
                        <input id="showRunning" ng-model="showRunning" type="checkbox">
                        <label for="showRunning">Show Running Builds</label>
                    </div>
                    <div class="form-group">
                        <input id="monospaced" ng-model="monospaced" type="checkbox">
                        <label for="monospaced">Monospaced Font</label>
                    </div>
                    <div class="form-group">
                        <input id="lie" ng-model="lie" type="checkbox">
                        <label for="lie" title="makes everything green!">Lie mode</label>
                    </div>
                </div>
                <div class="toggle transformable">
                    <a class="list-group-item" href="" ng-click="toggleSettings()">
                        <i class="fa fa-cog fa-3x"></i>
                    </a>
                </div>
            </div>

        </div>
    </jsp:body>

</t:genericpage>
