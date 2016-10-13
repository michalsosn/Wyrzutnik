const IconModule = (function() {
    function makeTrashIcon(id) {
        return L.icon({
            iconUrl: 'image/trash/trash-' + id + '.png',
            iconSize: [40, 48],
            popupAnchor: [0, -20]
        });
    }

    const intelligentIcons = [0, 1, 2, 3, 4].map(makeTrashIcon);
    const dumbIcon = makeTrashIcon('dumb');

    function getDumpsterIcon(dumpster) {
        if (dumpster.intelligent) {
            var limit = intelligentIcons.length - 1;
            var category = Math.round(limit * dumpster.used / dumpster.capacity);
            var index = Math.max(0, Math.min(limit, category));
            return intelligentIcons[index];
        } else {
            return dumbIcon;
        }
    }

    return {
        getDumpsterIcon: getDumpsterIcon
    };
})();

const MapModule = (function () {
    function makeMapLayer(type) {
        return L.gridLayer.googleMutant({
            type: type
        });
    }

    const mapLayers = {
        'Road map': makeMapLayer('roadmap'),
        'Satellite': makeMapLayer('satellite'),
        'Terrain': makeMapLayer('terrain'),
        'Hybrid': makeMapLayer('hybrid')
    };

    const map = L.map('map', {
        center: [51.759, 19.456],
        zoom: 12,
        layers: [mapLayers['Road map']]
    });

    L.control.layers(mapLayers).addTo(map);

    return {
        map: map
    };
})();

const MapElementsModule = (function (mapModule, ajaxUtils, iconModule) {
    const Types = Object.freeze({
        DUMPSTER: 'dumpster',
        ROUTE: 'route',
        WEATHER: 'weather'
    });

    function LayerManager(createLayer) {
        var visibleLayers = {};
        var ignoredIds = [];
        var ignoredLayers = {};

        this.updateElements = function(elements) {
            for (var id in visibleLayers) {
                if (visibleLayers.hasOwnProperty(id)) {
                    mapModule.map.removeLayer(visibleLayers[id]);
                }
            }
            visibleLayers = {};
            ignoredLayers = {};

            for (var i = 0; i < elements.length; ++i) {
                const element = elements[i];
                const createdLayer = createLayer(element);

                if (ignoredIds.indexOf(element.id) >= 0) {
                    ignoredLayers[element.id] = createdLayer;
                } else {
                    visibleLayers[element.id] = createdLayer;
                    mapModule.map.addLayer(createdLayer);
                }
            }
        };

        this.ignore = function (id) {
            ignoredIds.push(id);

            const found = visibleLayers[id];
            if (found !== undefined) {
                ignoredLayers[id] = found;
                delete visibleLayers[id];
                mapModule.map.removeLayer(found);
            }
        };

        this.stopIgnoring = function (id) {
            const position = ignoredIds.indexOf(id);
            if (position < 0) {
                return;
            }

            ignoredIds.splice(position, 1);
            const found = ignoredLayers[id];
            if (found !== undefined) {
                visibleLayers[id] = found;
                delete ignoredLayers[id];
                mapModule.map.addLayer(found);
            }
        }
    }

    function dispatchMapElementClick(event, identifier, element) {
        const mapElementClick = new CustomEvent('mapelementclick', {
            detail: {
                clickEvent: event,
                identifier: identifier,
                element: element
            }
        });
        window.dispatchEvent(mapElementClick);
    }

    const dumpsterManager = new LayerManager(function (dumpster) {
        const marker = L.marker(
            [dumpster.lat, dumpster.lng], {
                icon: iconModule.getDumpsterIcon(dumpster)
            }
        );

        var popup;
        if (dumpster.intelligent) {
            popup = 'Used: ' + dumpster.used + ' / ' + dumpster.capacity;
        } else {
            popup = 'Collection period: ' + dumpster.collectionPeriodInDays + ' days';
            if (dumpster.lastCollected !== null) {
                const collected = dumpster.lastCollected;
                popup += '<br>Last collected: ' + new Date(
                    Date.UTC(collected[0], collected[1], collected[2])
                ).toISOString().slice(0, 10);
            }
        }
        if (popup.length === 0) {
            popup = 'No details';
        }
        marker.bindPopup(popup);

        marker.on('click', function (event) {
            dispatchMapElementClick(event, {
                id: dumpster.id,
                type: Types.DUMPSTER
            }, dumpster)
        });
        return marker;
    });

    const routeManager = new LayerManager(function (route) {
        const polyline = L.polyline(
            route.lineString, {
                color: 'red'
            }
        );

        var popup = '';
        if (route.lengthInMeters !== null) {
            popup += 'Length: ' + route.lengthInMeters + ' meters';
        }
        if (route.travelTimeInSeconds !== null) {
            if (popup.length > 0) {
                popup += '<br>';
            }
            popup += 'Travel time: ' + route.travelTimeInSeconds + ' seconds';
        }
        if (popup.length === 0) {
            popup = 'No details';
        }
        polyline.bindPopup(popup);

        polyline.on('click', function (event) {
            dispatchMapElementClick(event, {
                id: route.id,
                type: Types.ROUTE
            }, route)
        });
        return polyline;
    });

    const weatherManager = new LayerManager(function (weather) {
        const polygon = L.polygon(
            weather.polygon, {
                color: 'blue'
            }
        );
        polygon.bindPopup(weather.comment);
        polygon.on('click', function (event) {
            dispatchMapElementClick(event, {
                id: weather.id,
                type: Types.WEATHER
            }, weather)
        });
        return polygon;
    });

    const managers = {
        dumpster: dumpsterManager,
        route: routeManager,
        weather: weatherManager
    };

    function getElements(url, callback) {
        const bounds = mapModule.map.getBounds().pad(0.5);
        const lat0 = bounds.getNorth();
        const lng0 = bounds.getWest();
        const lat1 = bounds.getSouth();
        const lng1 = bounds.getEast();
        const params = {
            lat0: lat0, lng0: lng0, lat1: lat1, lng1: lng1
        };

        ajaxUtils.getAt(url, params, callback);
    }

    function getAllElements() {
        getElements('/rest/dumpster', dumpsterManager.updateElements);
        getElements('/rest/route', routeManager.updateElements);
        getElements('/rest/weather', weatherManager.updateElements);
    }

    function ignore(identifier) {
        managers[identifier.type].ignore(identifier.id)
    }

    function stopIgnoring(identifier) {
        managers[identifier.type].stopIgnoring(identifier.id)
    }

    mapModule.map.on('moveend', getAllElements);

    return {
        Types: Types,
        getAllElements: getAllElements,
        ignore: ignore,
        stopIgnoring: stopIgnoring
    }
})(MapModule, AjaxUtils, IconModule);

const CreationModule = (function (mapModule, mapElementsModule) {
    const State = Object.freeze({
        IDLE: 1,
        CREATE: 2,
        UPDATE: 3,
        DELETE: 4,
        CREATE_ONGOING: 5,
        UPDATE_ONGOING: 6
    });

    function Editor(create, update, changeOnClick, finishCreation, finishUpdate, deleteSelected) {
        const self = this;

        self.state = State.IDLE;
        var editedId = null;
        var edited = null;

        function transit(newState) {
            if (self.state === newState) {
                return;
            }

            switch (newState) {
                case State.UPDATE_ONGOING:
                case State.CREATE_ONGOING:
                    mapModule.map.addLayer(edited);
                    break;
                case State.DELETE:
                case State.UPDATE:
                    window.addEventListener('mapelementclick', handleMapElementClick);
                    break;
                case State.CREATE:
                    mapModule.map.on('click', handleClick);
                    break;
                case State.IDLE:
                    mapModule.map.off('click', handleClick);
                    window.removeEventListener('mapelementclick', handleMapElementClick);
                    break;
            }

            self.state = newState;
        }

        function handleClick(event) {
            switch (self.state) {
                case State.UPDATE_ONGOING:
                case State.CREATE_ONGOING:
                    changeOnClick(event, edited);
                    break;
                case State.CREATE:
                    edited = create(event);
                    transit(State.CREATE_ONGOING);
                    break;
            }
        }

        function handleMapElementClick(event) {
            switch (self.state) {
                case State.UPDATE:
                    edited = update(event);
                    if (edited === null) {
                        break;
                    }
                    const details = event.detail;
                    editedId = details.identifier;
                    mapElementsModule.ignore(editedId);
                    transit(State.UPDATE_ONGOING);
                    break;
                case State.DELETE:
                    deleteSelected(event);
                    break;
            }
        }

        self.startCreation = function () {
            switch (self.state) {
                case State.UPDATE_ONGOING:
                case State.CREATE_ONGOING:
                case State.DELETE:
                case State.UPDATE:
                    self.cancel();
                //  fall through
                case State.IDLE:
                    transit(State.CREATE);
                    break;
            }
        };

        self.startUpdate = function () {
            switch (self.state) {
                case State.UPDATE_ONGOING:
                case State.CREATE_ONGOING:
                case State.DELETE:
                case State.UPDATE:
                    self.cancel();
                case State.IDLE:
                    transit(State.UPDATE);
                    break;
            }
        };

        self.cancel = function () {
            switch (self.state) {
                case State.UPDATE_ONGOING:
                    mapElementsModule.stopIgnoring(editedId);
                    editedId = null;
                //  fall through
                case State.CREATE_ONGOING:
                    mapModule.map.removeLayer(edited);
                    edited = null;
                //  fall through
                case State.DELETE:
                case State.UPDATE:
                case State.CREATE:
                    transit(State.IDLE);
                    break;
            }
        };

        self.delete = function () {
            switch (self.state) {
                case State.UPDATE_ONGOING:
                case State.CREATE_ONGOING:
                case State.UPDATE:
                case State.CREATE:
                    self.cancel();
                case State.IDLE:
                    transit(State.DELETE);
                    break;
            }
        };

        self.finish = function () {
            switch (self.state) {
                case State.UPDATE_ONGOING:
                    finishUpdate(editedId, edited);
                    mapElementsModule.stopIgnoring(editedId);
                    mapModule.map.removeLayer(edited);
                    editedId = null;
                    edited = null;
                    transit(State.IDLE);
                    break;
                case State.CREATE_ONGOING:
                    finishCreation(edited);
                    mapModule.map.removeLayer(edited);
                    edited = null;
                    transit(State.IDLE);
                    break;
                case State.UPDATE:
                case State.CREATE:
                    transit(State.IDLE);
                    break;
            }
        }
    }

    return {
        Editor: Editor
    };
})(MapModule, MapElementsModule);

const RouterModule = (function () {
    function ElementRouter(options) {
        const self = this;
        var select = null;

        var lastSelected = null;

        function getSelected() {
            return select.options[select.selectedIndex].value;
        }

        function handleChange() {
            const selected = getSelected();
            if (lastSelected !== selected) {
                if (lastSelected === null) {
                    for (var option in options) {
                        if (options.hasOwnProperty(option)) {
                            const ids = options[option].elementIds;
                            for (var i = 0; i < ids.length; ++i) {
                                const id = ids[i];
                                document.getElementById(id).classList.add('hidden');
                            }
                        }
                    }
                } else {
                    const ids = options[lastSelected].elementIds;
                    for (var i = 0; i < ids.length; ++i) {
                        const lastId = ids[i];
                        document.getElementById(lastId).classList.add('hidden');
                    }
                    if (options[lastSelected].transitionOff !== undefined) {
                        options[lastSelected].transitionOff();
                    }
                }

                const ids = options[selected].elementIds;
                for (var i = 0; i < ids.length; ++i) {
                    const currentId = ids[i];
                    document.getElementById(currentId).classList.remove('hidden');
                }

                lastSelected = selected;
            }
        }

        self.routeCall = function(callRoutes, defaultCall) {
            return function() {
                const selectedCall = callRoutes[getSelected()];
                if (selectedCall !== undefined) {
                    return selectedCall(arguments);
                } else {
                    return defaultCall(arguments);
                }
            }
        };

        self.bindToSelect = function (selectId) {
            select = document.getElementById(selectId);
            for (var option in options) {
                if (options.hasOwnProperty(option)) {
                    const element = document.createElement('option');
                    element.value = option;
                    element.innerHTML = options[option].html;
                    select.appendChild(element);
                }
            }
            select.addEventListener('change', handleChange);
            handleChange();
        };
    }

    return {
        ElementRouter: ElementRouter
    }
})();

var WyrzutnikCreationModule = (function (ajaxUtils, mapModule, mapElementsModule, editionModule, routerModule) {
    function createDumpster(mouseEvent) {
        return L.marker(mouseEvent.latlng, {
            draggable: true,
            zIndexOffset: 1
        });
    }

    function updateDumpster(elementEvent) {
        const details = elementEvent.detail;
        if (details.identifier.type != mapElementsModule.Types.DUMPSTER) {
            return null;
        }

        const element = details.element;

        const latlng = [element.lat, element.lng];
        document.getElementById('dumpster-intelligent').checked = element.intelligent;
        document.getElementById('dumpster-capacity').value = element.capacity;
        document.getElementById('dumpster-used').value = element.used;
        document.getElementById('dumpster-collection-period-in-days').value = element.collectionPeriodInDays;
        const date = element.lastCollected;
        document.getElementById('dumpster-last-collected').value = date
            ? new Date(Date.UTC(date[0], date[1] - 1, date[2])).toISOString().slice(0,10) : null;

        return L.marker(latlng, {
            draggable: true,
            zIndexOffset: 1
        });
    }

    function changeDumpsterOnClick(mouseEvent, point) {
        point.setLatLng(mouseEvent.latlng);
    }

    function makeDumpsterRequest(point) {
        const latlng = point.getLatLng();
        const intelligent = document.getElementById('dumpster-intelligent').checked;
        if (intelligent) {
            var capacity = document.getElementById('dumpster-capacity').value;
            var used = document.getElementById('dumpster-used').value;
        } else {
            var collectionPeriodInDays = document.getElementById('dumpster-collection-period-in-days').value;
            var lastCollected = document.getElementById('dumpster-last-collected').value;
        }

        return {
            lat: latlng.lat,
            lng: latlng.lng,
            intelligent: intelligent,
            capacity: capacity,
            used: used,
            collectionPeriodInDays: collectionPeriodInDays ,
            lastCollected: lastCollected
        };
    }

    function createRoute(mouseEvent) {
        return L.polyline(
            [mouseEvent.latlng], {
                color: 'black',
                zIndexOffset: 1
            }
        );
    }

    function updateRoute(elementEvent) {
        const details = elementEvent.detail;
        if (details.identifier.type != mapElementsModule.Types.ROUTE) {
            return null;
        }

        const element = details.element;

        const latlngs = element.lineString;
        document.getElementById('route-length-in-meters').value = element.lengthInMeters;
        document.getElementById('route-travel-time-in-seconds').value = element.travelTimeInSeconds;

        return L.polyline(
            latlngs, {
                color: 'black',
                zIndexOffset: 1
            }
        );
    }

    function changeRouteOnClick(mouseEvent, polyline) {
        polyline.addLatLng(mouseEvent.latlng);
    }

    function makeRouteRequest(polyline) {
        const latlngs = polyline.getLatLngs().map(
            function (latlng) { return [latlng.lat, latlng.lng]; }
        );
        const lengthInMeters = document.getElementById('route-length-in-meters').value;
        const travelTimeInSeconds = document.getElementById('route-travel-time-in-seconds').value;

        return {
            lineString: latlngs,
            lengthInMeters: lengthInMeters,
            travelTimeInSeconds: travelTimeInSeconds
        };
    }

    function createWeather(mouseEvent) {
        return L.polygon(
            [[mouseEvent.latlng]], {
                color: 'black',
                zIndexOffset: 1
            }
        );
    }

    function updateWeather(elementEvent) {
        const details = elementEvent.detail;
        if (details.identifier.type != mapElementsModule.Types.WEATHER) {
            return null;
        }

        const element = details.element;

        const latlngs = element.polygon;
        document.getElementById('weather-comment').value = element.comment;

        return L.polygon(
            latlngs, {
                color: 'black',
                zIndexOffset: 1
            }
        );
    }

    function changeWeatherOnClick(mouseEvent, polygon) {
        polygon.addLatLng(mouseEvent.latlng);
    }

    function makeWeatherRequest(polygon) {
        const latlngs = polygon.getLatLngs().map(
            function (latlngs) {
                mappedLatLngs = latlngs.map(
                    function (latlng) { return [latlng.lat, latlng.lng]; }
                );
                if (mappedLatLngs.length > 0) {
                    mappedLatLngs.push(mappedLatLngs[0]);
                }
                return mappedLatLngs;
            }
        );
        const comment = document.getElementById('weather-comment').value;

        return {
            polygon: latlngs,
            comment: comment
        };
    }

    function idToUrl(identifier) {
        return '/rest/' + identifier.type + '/' + identifier.id;
    }

    function finishCreation(requestMaker, type, dumpster) {
        const request = requestMaker(dumpster);
        ajaxUtils.postJson('/rest/' + type, request, mapElementsModule.getAllElements, false);
    }

    function finishUpdate(requestMaker, identifier, dumpster) {
        const request = requestMaker(dumpster);
        ajaxUtils.putJson(idToUrl(identifier), request, mapElementsModule.getAllElements, false);
    }

    function deleteAnyType(elementEvent) {
        const details = elementEvent.detail;
        const identifier = details.identifier;
        ajaxUtils.deleteAt(idToUrl(identifier), mapElementsModule.getAllElements, false);
    }

    const dumpsterEditor = new editionModule.Editor(
        createDumpster, updateDumpster, changeDumpsterOnClick,
        finishCreation.bind(null, makeDumpsterRequest, mapElementsModule.Types.DUMPSTER),
        finishUpdate.bind(null, makeDumpsterRequest),
        deleteAnyType
    );

    const routeEditor = new editionModule.Editor(
        createRoute, updateRoute, changeRouteOnClick,
        finishCreation.bind(null, makeRouteRequest, mapElementsModule.Types.ROUTE),
        finishUpdate.bind(null, makeRouteRequest),
        deleteAnyType
    );

    const weatherEditor = new editionModule.Editor(
        createWeather, updateWeather, changeWeatherOnClick,
        finishCreation.bind(null, makeWeatherRequest, mapElementsModule.Types.WEATHER),
        finishUpdate.bind(null, makeWeatherRequest),
        deleteAnyType
    );

    var router = new routerModule.ElementRouter({
        'dumpster': {
            html: 'Dumpster', elementIds: ['dumpster-form'], transitionOff: dumpsterEditor.cancel
        }, 'route': {
            html: 'Route', elementIds: ['route-form'], transitionOff: routeEditor.cancel
        }, 'weather': {
            html: 'Weather', elementIds: ['weather-form'], transitionOff: weatherEditor.cancel
        }
    });
    router.bindToSelect('edition-select');

    function routed(callName) {
        return router.routeCall({
            'dumpster': dumpsterEditor[callName],
            'route': routeEditor[callName],
            'weather': weatherEditor[callName]
        });
    }

    document.getElementById('creation-start').addEventListener('click', routed('startCreation'));
    document.getElementById('update-start').addEventListener('click', routed('startUpdate'));
    document.getElementById('delete-start').addEventListener('click', routed('delete'));
    document.getElementById('cancel').addEventListener('click', routed('cancel'));
    document.getElementById('finish').addEventListener('click', routed('finish'));
})(AjaxUtils, MapModule, MapElementsModule, CreationModule, RouterModule);

const RouteFinderModule = (function (ajaxUtils, mapModule, mapElementsModule) {
    const url = '/rest/route/optimal';

    function handleClick(mouseEvent) {
        var request = {
            latitude: mouseEvent.latlng.lat,
            longitude: mouseEvent.latlng.lng,
            usedThreshold: 0.8
        };
        ajaxUtils.postJson(url, request, mapElementsModule.getAllElements, false);
        mapModule.map.off('click', handleClick);
    }

    document.getElementById('route-finder-start').addEventListener('click', function () {
        mapModule.map.on('click', handleClick);
    });
    document.getElementById('route-finder-cancel').addEventListener('click', function () {
        mapModule.map.off('click', handleClick);
    });
})(AjaxUtils, MapModule, MapElementsModule);

window.addEventListener('load', function () {
    MapElementsModule.getAllElements();
    LoginModule.showWhenIn('map-editor-panel');
    LoginModule.showWhenIn('optimal-route-panel');
    LoginModule.showWhenOut('login-message-panel');
    LoginModule.refresh();
});

