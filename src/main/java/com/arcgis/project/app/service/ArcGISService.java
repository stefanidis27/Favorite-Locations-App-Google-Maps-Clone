package com.arcgis.project.app.service;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.CoordinateFormatter;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.popup.PopupDefinition;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;
import com.esri.arcgisruntime.tasks.networkanalysis.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ArcGISService {

    public static final Point initialLocation = new Point(-122.41942, 37.77493, SpatialReferences.getWgs84());
    public static final double initialScale = 144447.638572;
    public static double currentLatitude = 37.77493;
    public static double currentLongitude = -122.41942;

    private static Point currentLocation = initialLocation;
    private static TextField searchBox;
    private static LocatorTask locatorTask;
    private static GeocodeParameters geocodeParameters;
    private static final GraphicsOverlay graphicsOverlayPopularLocations = new GraphicsOverlay();
    private static final GraphicsOverlay graphicsOverlayDatabaseUserFavoriteLocations = new GraphicsOverlay();
    private static final GraphicsOverlay graphicsOverlayPOIs = new GraphicsOverlay();
    private static final GraphicsOverlay graphicsOverlayCurrentLocation = new GraphicsOverlay();
    private static final GraphicsOverlay graphicsOverlayRouting = new GraphicsOverlay();

    private static Graphic routeGraphic;
    private static RouteTask routeTask;
    private static RouteParameters routeParameters;
    private static final ObservableList<Stop> routeStops = FXCollections.observableArrayList();
    private static final ListView<String> directionsList = new ListView<>();


    public static void performRouting(MapView mapView, StackPane stackPane) {
        directionsList.setMaxSize(300, 150);
        stackPane.getChildren().add(directionsList);
        StackPane.setAlignment(directionsList, Pos.TOP_RIGHT);

        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(graphicsOverlay);
        routeTask = new RouteTask(
                "https://route-api.arcgis.com/arcgis/rest/services/World/Route/NAServer/Route_World"
        );
        ListenableFuture<RouteParameters> routeParametersFuture = routeTask.createDefaultParametersAsync();
        routeParametersFuture.addDoneListener(() -> {
            try {
                routeParameters = routeParametersFuture.get();
                routeParameters.setReturnDirections(true);
                directionsList.getItems().add("Right click to add two points to find a route.");
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, e.toString());
                alert.show();
                e.printStackTrace();
            }
        });
        solveMouseClicked(mapView);

        routeStops.addListener((ListChangeListener<Stop>) e -> {
            int routeStopsSize = routeStops.size();
            if (routeStopsSize == 0) {
                return;
            } else if (routeStopsSize == 1) {
                graphicsOverlayRouting.getGraphics().clear();
                if (!directionsList.getItems().isEmpty())
                    directionsList.getItems().clear();
                directionsList.getItems().add("Right click to add two points to find a route.");
            }
            SimpleMarkerSymbol stopMarker = new SimpleMarkerSymbol(
                    SimpleMarkerSymbol.Style.CIRCLE,
                    0xff92302d,
                    10
            );
            SimpleLineSymbol whiteOutlineSymbol = new SimpleLineSymbol(
                    SimpleLineSymbol.Style.SOLID,
                    0xFFFFFFFF,
                    2
            );
            stopMarker.setOutline(whiteOutlineSymbol);
            Geometry routeStopGeometry = routeStops.get(routeStopsSize - 1).getGeometry();
            graphicsOverlayRouting.getGraphics().add(new Graphic(routeStopGeometry, stopMarker));
            if (routeStopsSize == 2) {
                mapView.setOnMouseClicked(null);
                routeParameters.setStops(routeStops);
                ListenableFuture <RouteResult> routeResultFuture = routeTask.solveRouteAsync(routeParameters);
                routeResultFuture.addDoneListener(() -> {
                    try {
                        RouteResult result = routeResultFuture.get();
                        List <Route> routes = result.getRoutes();
                        if (!routes.isEmpty()) {
                            Route route = routes.get(0);
                            Geometry shape = route.getRouteGeometry();
                            routeGraphic = new Graphic(shape, new SimpleLineSymbol(
                                    SimpleLineSymbol.Style.SOLID,
                                    0xff92302d,
                                    1
                            ));
                            graphicsOverlayRouting.getGraphics().add(routeGraphic);
                            route.getDirectionManeuvers().forEach(step -> directionsList.getItems().add(
                                    step.getDirectionText())
                            );
                            routeStops.clear();
                            solveMouseClicked(mapView);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
        });
    }

    public static void solveMouseClicked(MapView mapView) {
        mapView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                graphicsOverlayCurrentLocation.getGraphics().clear();
                Point2D mapPoint = new Point2D(
                        event.getX(),
                        event.getY()
                );
                SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(
                        SimpleMarkerSymbol.Style.CIRCLE,
                        0xFF379a37,
                        10
                );
                SimpleLineSymbol whiteOutlineSymbol = new SimpleLineSymbol(
                        SimpleLineSymbol.Style.SOLID,
                        0xFFFFFFFF,
                        2
                );
                simpleMarkerSymbol.setOutline(whiteOutlineSymbol);
                currentLocation = mapView.screenToLocation(mapPoint);
                updateCurrentLatitudeAndLongitude();
                Graphic pointGraphic = new Graphic(currentLocation, simpleMarkerSymbol);
                graphicsOverlayCurrentLocation.getGraphics().add(pointGraphic);
            }
            if (event.isStillSincePress() && event.getButton() == MouseButton.SECONDARY) {
                Point2D mapPoint = new Point2D(event.getX(), event.getY());
                routeStops.add(new Stop(mapView.screenToLocation(mapPoint)));
            }
        });
    }

    private static void updateCurrentLatitudeAndLongitude() {
        String coordinates = CoordinateFormatter.toLatitudeLongitude(
                currentLocation,
                CoordinateFormatter.LatitudeLongitudeFormat.DECIMAL_DEGREES,
                5
        );
        String[] coordinatesData = coordinates.split("[ ]");

        currentLatitude = Double.parseDouble(coordinatesData[0].substring(0, coordinatesData[0].length() - 1));
        if (coordinatesData[0].charAt(coordinatesData[0].length() - 1) == 'S') {
            currentLatitude *= -1;
        }
        currentLongitude = Double.parseDouble(coordinatesData[1].substring(0, coordinatesData[1].length() - 1));
        if (coordinatesData[1].charAt(coordinatesData[1].length() - 1) == 'W') {
            currentLongitude *= -1;
        }
    }

    public static void performLocalSearchByName(String POIName) {
        LocatorTask locatorTask = new LocatorTask(
                "https://geocode-api.arcgis.com/arcgis/rest/services/World/GeocodeServer"
        );
        GeocodeParameters geocodeParameters = new GeocodeParameters();
        geocodeParameters.setPreferredSearchLocation(currentLocation);
        geocodeParameters.getResultAttributeNames().add("PlaceName");
        geocodeParameters.getResultAttributeNames().add("Place_addr");
        ListenableFuture<List<GeocodeResult>> geocodeResultsFuture = locatorTask.geocodeAsync(
                POIName,
                geocodeParameters
        );

        geocodeResultsFuture.addDoneListener(()-> {
            try {
                List<GeocodeResult> geocodeResults = geocodeResultsFuture.get();
                if (geocodeResults.size() > 0) {
                    displayPOIs(geocodeResults);
                }
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    public static void displayPOIs(List<GeocodeResult> geocodeResults) {
        graphicsOverlayPOIs.getGraphics().clear();
        for (GeocodeResult geocodeResult : geocodeResults) {
            Point point = new Point(
                    geocodeResult.getDisplayLocation().getX(),
                    geocodeResult.getDisplayLocation().getY(),
                    SpatialReferences.getWgs84()
            );
            SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(
                    SimpleMarkerSymbol.Style.X,
                    0xFF000000,
                    11
            );
            SimpleLineSymbol whiteOutlineSymbol = new SimpleLineSymbol(
                    SimpleLineSymbol.Style.SOLID,
                    0xFFFFFFFF,
                    1
            );
            simpleMarkerSymbol.setOutline(whiteOutlineSymbol);
            Graphic pointGraphic = new Graphic(point, simpleMarkerSymbol);
            graphicsOverlayPOIs.getGraphics().add(pointGraphic);
        }
    }

    public static void displayPopularLocations(List<Point> popularLocations) {
        graphicsOverlayPopularLocations.getGraphics().clear();
        for (Point point : popularLocations) {
            SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(
                    SimpleMarkerSymbol.Style.DIAMOND,
                    0xFFdaaa4a,
                    12
            );
            SimpleLineSymbol whiteOutlineSymbol = new SimpleLineSymbol(
                    SimpleLineSymbol.Style.SOLID,
                    0xFFFFFFFF,
                    2
            );
            simpleMarkerSymbol.setOutline(whiteOutlineSymbol);
            Graphic pointGraphic = new Graphic(point, simpleMarkerSymbol);
            graphicsOverlayPopularLocations.getGraphics().add(pointGraphic);
        }
    }

    public static void displayDatabaseLocations(List<Point> databaseLocations) {
        graphicsOverlayDatabaseUserFavoriteLocations.getGraphics().clear();
        for (Point point : databaseLocations) {
            SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(
                    SimpleMarkerSymbol.Style.DIAMOND,
                    0xFF0e4e74,
                    12
            );
            SimpleLineSymbol whiteOutlineSymbol = new SimpleLineSymbol(
                    SimpleLineSymbol.Style.SOLID,
                    0xFFFFFFFF,
                    2
            );
            simpleMarkerSymbol.setOutline(whiteOutlineSymbol);
            Graphic pointGraphic = new Graphic(point, simpleMarkerSymbol);
            graphicsOverlayDatabaseUserFavoriteLocations.getGraphics().add(pointGraphic);
        }
    }

    public static void performGlobalSearch(MapView mapView, StackPane stackPane) {
        solveMouseClicked(mapView);
        setupTextField();
        createLocatorTaskAndDefaultParameters(mapView);
        searchBox.setOnAction(event -> {
            String address = searchBox.getText();
            if (!address.isBlank()) {
                performGeocode(address, mapView);
            }
        });

        stackPane.getChildren().add(searchBox);
        StackPane.setAlignment(searchBox, Pos.TOP_LEFT);
        StackPane.setMargin(searchBox, new Insets(10, 0, 0, 10));
    }

    public static void setGraphicLayers(MapView mapView) {
        mapView.getGraphicsOverlays().add(graphicsOverlayPopularLocations);
        mapView.getGraphicsOverlays().add(graphicsOverlayDatabaseUserFavoriteLocations);
        mapView.getGraphicsOverlays().add(graphicsOverlayPOIs);
        mapView.getGraphicsOverlays().add(graphicsOverlayCurrentLocation);
        mapView.getGraphicsOverlays().add(graphicsOverlayRouting);
    }

    private static void setupTextField() {
        searchBox = new TextField();
        searchBox.setMaxWidth(350);
        searchBox.setPromptText("Search for a place and press enter.");
    }

    private static void createLocatorTaskAndDefaultParameters(MapView mapView) {
        locatorTask = new LocatorTask("https://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");

        geocodeParameters = new GeocodeParameters();
        geocodeParameters.getResultAttributeNames().add("*");
        geocodeParameters.setMaxResults(1);
        geocodeParameters.setOutputSpatialReference(mapView.getSpatialReference());
    }

    private static void performGeocode(String address, MapView mapView) {
        ListenableFuture<List<GeocodeResult>> geocodeResults = locatorTask.geocodeAsync(address, geocodeParameters);

        geocodeResults.addDoneListener(() -> {
            try {
                List<GeocodeResult> geocodes = geocodeResults.get();
                if (geocodes.size() > 0) {
                    GeocodeResult result = geocodes.get(0);
                    mapView.setViewpointCenterAsync(result.getDisplayLocation());
                    currentLocation = result.getDisplayLocation();
                } else {
                    new Alert(Alert.AlertType.INFORMATION, "No results found.").show();
                }
            } catch (InterruptedException | ExecutionException e) {
                new Alert(Alert.AlertType.ERROR, "Error getting result.").show();
                e.printStackTrace();
            }
        });
    }

}
