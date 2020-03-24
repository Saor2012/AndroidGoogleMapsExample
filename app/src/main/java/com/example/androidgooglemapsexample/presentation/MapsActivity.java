package com.example.androidgooglemapsexample.presentation;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.example.androidgooglemapsexample.R;
import com.example.androidgooglemapsexample.data.EntityDB;
import com.example.androidgooglemapsexample.databinding.ActivityMapsBinding;
import com.facebook.CallbackManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MapsActivity extends BaseActivity<ActivityMapsBinding> implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, IMapsPresenter.View {

    private IMapsPresenter.Presenter presenter;

    private Boolean isLocationPermissionGranted = false;
    //    private LocationListener locationListener;
//    private LocationManager locationManager;
//    private Context appContext = getApplicationContext();
//    private final int REQUEST_CODE = 101;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private static final String TAG = "MapsActivity";
    private static final LatLng Post1 = new LatLng(48.527657,35.015481);
    private static final LatLng Post2 = new LatLng(48.525476, 35.033480);
    private LatLng LastDiviceLocation = null;
    private List<Marker> mListPosts;
    private List<EntityDB> entityDBList;
//    private Marker mPost1;
//    private Marker mPost2;
    private GoogleMap mMap;

    private FusedLocationProviderClient fusedLocationProviderClient = null;

    private static final float DEFAULT_MAP_ZOOM = 15f;

    private GeoApiContext geoApiContext = null;

    private CallbackManager callbackManager;

    public MapsActivity() {}
    /*
* Прокласти маршрут за точками, тощо.
* Створити базу даних, що перехоплювати точки на мапі та розмалювувати їх, тобто зберігати остані значення координа.
* Якщо до БД щось додалося, тоді відобразити їх.
* Організувати роботу маршруту.
* https://aqicn.org/faq/2015-09-18/map-web-service-real-time-air-quality-tile-api/ru/ додати зв'язок з показниками
* */
    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        getLocationPermission();
//        setContentView(R.layout.activity_maps);

        init();
        // Facebook autontification button used
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);
//
////        callbackManager = CallbackManager.Factory.create();
////
////        loginButton = (LoginButton) findViewById(R.id.login_button);
////        loginButton.setReadPermissions("email");
////        // If using in a fragment
////        loginButton.setFragment(this);
////
////        // Callback registration
////        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
////            @Override
////            public void onSuccess(LoginResult loginResult) {
////                // App code
////            }
////
////            @Override
////            public void onCancel() {
////                // App code
////            }
////
////            @Override
////            public void onError(FacebookException exception) {
////                // App code
////            }
////        });
//
//        callbackManager = CallbackManager.Factory.create();
//
//        LoginManager.getInstance().registerCallback(callbackManager,
//            new FacebookCallback<LoginResult>() {
//                @Override
//                public void onSuccess(LoginResult loginResult) {
//                    // App code
//                    Timber.e("onSuccess start");
//                }
//
//                @Override
//                public void onCancel() {
//                    // App code
//                    Timber.e("onCancel start");
//                }
//
//                @Override
//                public void onError(FacebookException exception) {
//                    // App code
//                    Timber.e("onError start %s", exception.getMessage());
//                }
//            });
//
//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
//        Timber.e("isLoggedIn = %s", isLoggedIn);

    }*/

    @Override
    protected void initView() {
        presenter = new MapsPresenter();
        initMap();
        getLocationPermission();
        init();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_maps;
    }

    @Override
    protected void onStartView() {
        presenter.startView(this);
        presenter.query();
    }

    @Override
    protected void onDestroyView() {
        if (presenter != null) {
            presenter.stopView();
            presenter = null;
        }
    }

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private void init() {
        getBinding().editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
           if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == event.ACTION_DOWN || event.getAction() == event.KEYCODE_ENTER) {
               Timber.e("On edit");
               getLocate();
           }
            return false;
        });
        getBinding().icGps.setOnClickListener(v -> {
            getDeviceLocation();
        });
        getBinding().imageViewSearch.setOnClickListener(v -> {
            if (!getBinding().editTextSearch.getText().toString().equals("") || !getBinding().editTextSearch.getText().toString().equals("My location")) {
                Timber.e("On search");
                getLocate();
            }
        });
        getBinding().icRoute.setOnClickListener(v -> {
//            getRoute();
        });

        hideSoftKeyboard();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        Timber.e("onActivityResult start");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap == null) {
            toast("Map is null");
            return;
        }
        mMap = googleMap;

//        mPost1 = mMap.addMarker(new MarkerOptions()
//                .position(Post1)
//                .title("Post1"));
//        mPost1.setTag(0);
//
//        mPost2 = mMap.addMarker(new MarkerOptions()
//                .position(Post2)
//                .title("Post2"));
//        mPost2.setTag(0);
        if (isLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false); //Disable UI buttons
        }
//        String url = getURL(mPost1.getPosition(), mPost2.getPosition(), "driving");
//        new FetchURL(MapsActivity.this).exucute(url, "driving");
//

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Post1, 12f));
        mMap.setOnMarkerClickListener(this);
        init();
        if (entityDBList != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(entityDBList.get(entityDBList.size() - 1).getLatitude(), entityDBList.get(entityDBList.size() - 1).getLongitude()), DEFAULT_MAP_ZOOM));
        } else {
            Timber.tag(TAG).e("Dao entityDBList of posts is null");
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            toast(marker.getTitle() + " has been clicked " + clickCount + " times.");
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null){
            mapFragment.getMapAsync(MapsActivity.this);
            if (geoApiContext == null) {
               geoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
            }
        } else {
            Timber.tag(TAG).e(": initMap() - map fragment is null");
        }
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION )== PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        isLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            isLocationPermissionGranted = false;
                            return;
                        }
                    }
                    isLocationPermissionGranted = true;
                }
            }
        }
    }

    private void getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            Task<Location> taskLocation = fusedLocationProviderClient.getLastLocation();
            taskLocation.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Location currentLocation = (Location) task.getResult();
                    if (currentLocation != null) {
                        LastDiviceLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        noveCameraToDevice(LastDiviceLocation, DEFAULT_MAP_ZOOM, "My location");
                    } else {
                        Timber.tag(TAG).e(": getDeviceLocation() - device location is null");
                    }
                } else {
                    Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (SecurityException e) {
            Timber.tag(TAG).e(": getDeviceLocation() - %s", e.getMessage());
        }
    }

    private void getRoute() {
        //Define entityDBList to get all latlng for the route
        List<LatLng> path = new ArrayList();

        //Execute Directions API request
        DirectionsApiRequest req = DirectionsApi.getDirections(geoApiContext, Post1.toString(), Post2.toString());
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs !=null) {
                    for(int i=0; i<route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j=0; j<leg.steps.length;j++){
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length >0) {
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to entityDBList of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to entityDBList of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception ex) {
            Timber.tag(TAG).e(": calculateDirections() - %s", ex.getMessage());
            Timber.tag(TAG).e(": calculateDirections() - %s", ex.getLocalizedMessage());
        }

        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
            mMap.addPolyline(opts);
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Post1, 6));
    }

    private void getLocate() {
        String searchString = getBinding().editTextSearch.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Timber.tag(TAG).e(": getLocate() - %s", e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            Timber.tag(TAG).e( ": getLocate() - %s", address.toString());
            noveCameraToDevice(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_MAP_ZOOM, address.getAddressLine(0));
        }
    }

    private void noveCameraToDevice(LatLng latLng, float zoom, String title) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My location")) {
            MarkerOptions options = new MarkerOptions().position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }
        hideSoftKeyboard();
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void handleGPS() {
        if (mMap != null) {
            Timber.e("On get device location");
            getDeviceLocation();
        } else {
            toast("Map is null");
        }
    }

    @Override
    public void handleSearch() {
        if (mMap != null) {
            if (!getBinding().editTextSearch.getText().toString().equals("") || !getBinding().editTextSearch.getText().toString().equals("My location")) {
                Timber.e("On search");
                getLocate();
            }
        } else {
            toast("Map is null");
        }
    }

    @Override
    public void handleRoute() {
        if (mMap != null) {
            Timber.e("On route");
            //getRoute();
        } else {
            toast("Map is null");
        }
    }

    @Override
    public void getList(List<EntityDB> list) {
        entityDBList = list;
        if (mListPosts == null) {
            mListPosts = new ArrayList<Marker>();
        }
        if (entityDBList != null && entityDBList.size() > 0) {
            entityDBList.forEach(post -> {
                mListPosts.add(mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(post.getLatitude(), post.getLongitude()))
                        .title(post.getTitle())));
            });

            if (mListPosts.size() > 0) {
                mListPosts.forEach(post -> {
                    post.setTag(0);
                });
            }
        }
    }

//    private void  calculateDirections(Marker marker) {
//        LatLng destination = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
//
//        DirectionsApiRequest directionsApiRequest = new DirectionsApiRequest(geoApiContext);
//        directionsApiRequest.alternatives(true);
//        directionsApiRequest.origin(String.valueOf(new LatLng(LastDiviceLocation.latitude, LastDiviceLocation.longitude)));
//        Timber.tag(TAG).e(": calculateDirections() - destination %s", directionsApiRequest.toString());
//
//        directionsApiRequest.destination(String.valueOf(destination)).setCallback(new PendingResult.Callback<DirectionsResult>() {
//            @Override
//            public void onResult(DirectionsResult result) {
//                Timber.tag(TAG).e(": calculateDirections(): route %s", result.routes[0].toString());
//                Timber.tag(TAG).e(": calculateDirections(): duration %s", result.routes[0].legs[0].duration);
//                Timber.tag(TAG).e(": calculateDirections(): distance %s", result.routes[0].legs[0].distance);
//                Timber.tag(TAG).e(": calculateDirections(): geocodeWayPoints %s", result.geocodedWaypoints[0].toString());
//            }
//
//            @Override
//            public void onFailure(Throwable e) {
//                Timber.tag(TAG).e(": calculateDirections() - %s", e.getMessage());
//            }
//        });
//    }

//    private String getURL(LatLng start, LatLng end, String directionMode) {
//        String string1 = "origin" + start.latitude + "," + start.longitude;
//        String string2 = "destination" + end.latitude + "," + end.longitude;
//        String mode = "mode=" + directionMode;
//        String parametes = string1 + "&" + string2 + "&" + mode;
//        String output = "json";
//
//        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parametes + "&key=" + getString(R.string.google_maps_key);
//    }
/**/
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        LatLng barcelona = new LatLng(41.385064,2.173403);
//        mMap.addMarker(new MarkerOptions().position(barcelona).title("Marker in Barcelona"));
//
//        LatLng madrid = new LatLng(40.416775,-3.70379);
//        mMap.addMarker(new MarkerOptions().position(madrid).title("Marker in Madrid"));
//
//        LatLng zaragoza = new LatLng(41.648823,-0.889085);
//
//        //Define entityDBList to get all latlng for the route
//        List<LatLng> path = new ArrayList();
//
//
//        //Execute Directions API request
//        GeoApiContext context = new GeoApiContext.Builder()
//                .apiKey("YOUR_API_KEY")
//                .build();
//        DirectionsApiRequest req = DirectionsApi.getDirections(context, "41.385064,2.173403", "40.416775,-3.70379");
//        try {
//            DirectionsResult res = req.await();
//
//            //Loop through legs and steps to get encoded polylines of each step
//            if (res.routes != null && res.routes.length > 0) {
//                DirectionsRoute route = res.routes[0];
//
//                if (route.legs !=null) {
//                    for(int i=0; i<route.legs.length; i++) {
//                        DirectionsLeg leg = route.legs[i];
//                        if (leg.steps != null) {
//                            for (int j=0; j<leg.steps.length;j++){
//                                DirectionsStep step = leg.steps[j];
//                                if (step.steps != null && step.steps.length >0) {
//                                    for (int k=0; k<step.steps.length;k++){
//                                        DirectionsStep step1 = step.steps[k];
//                                        EncodedPolyline points1 = step1.polyline;
//                                        if (points1 != null) {
//                                            //Decode polyline and add points to entityDBList of route coordinates
//                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
//                                            for (com.google.maps.model.LatLng coord1 : coords1) {
//                                                path.add(new LatLng(coord1.lat, coord1.lng));
//                                            }
//                                        }
//                                    }
//                                } else {
//                                    EncodedPolyline points = step.polyline;
//                                    if (points != null) {
//                                        //Decode polyline and add points to entityDBList of route coordinates
//                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
//                                        for (com.google.maps.model.LatLng coord : coords) {
//                                            path.add(new LatLng(coord.lat, coord.lng));
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } catch(Exception ex) {
//            Log.e(TAG, ex.getLocalizedMessage());
//        }
//
//        //Draw the polyline
//        if (path.size() > 0) {
//            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
//            mMap.addPolyline(opts);
//        }
//
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zaragoza, 6));
//    }

}
