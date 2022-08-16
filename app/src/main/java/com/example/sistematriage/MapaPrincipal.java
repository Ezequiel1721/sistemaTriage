package com.example.sistematriage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.sistematriage.databinding.ActivityMapaPrincipalBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapaPrincipal extends AppCompatActivity implements OnMapReadyCallback , GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ActivityMapaPrincipalBinding binding;

    private LocationManager locationManager;
    private Location currentLocation;

    JsonObjectRequest jsonObjectRequest;

    private ArrayList<Marker> temporalRealTimeMarkers = new ArrayList<>();
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();

    private Marker marcador;
    private static final float camera_zoom = 15;


    private BottomSheetBehavior mBottomSheetBehavior1;
    LinearLayout tapactionlayout;
    View white_forground_view;
    View bottomSheet;
    TextView txtUbicacion, tvcolor, tvTiempo, tvDistancia;
    ImageView ivBSImagen;
    ImageView btnNavegar, btnNavGoogle, btnDetalles, btnTrazarRuta;


    Double latori, lngori, latdes, lngdes;
    Polyline line;
    Boolean rutaTrazada;
    String tiempo, distancia;
    Integer ultNoPaciente;

    private SharedPreferences prefs;
    BottomNavigationView bottomNavigationView;
    TextView usuario;
    Toolbar toolbar;

    TextView txtNumTotal, txtNumRojo, txtNumAmarillo, txtNumVerde, txtNumNegro;
    Integer cTotal, cRojo, cAmarillo, cVerde, cNegro;

    Marker markerUbicacion;

    View popup;
    TextView tvLat, tvLng, tvAlt;
    ImageView imagenMarker;

    herido herido;
    MarkerOptions markerOptions;
    Marker marcador_;
    JSONObject jsonObject;
    Integer clickCount;
    herido info;
    LocationListener locationListener;
    String url;

    Intent intent;
    String nombre;

    RoundedBitmapDrawable roundedBitmapDrawable;
    JSONArray json;
    List<LatLng> poly;
    Drawable vectorDrawable;
    Bitmap bitmap;
    Canvas canvas;
    RequestQueue requestQueue;
    String status;
    JSONArray routes;
    ArrayList<LatLng> points;
    PolylineOptions polylineOptions;
    JSONArray legs;
    JSONArray steps;
    String polyline;
    List<LatLng> list;
    LatLng position;
    LatLngBounds bounds;
    Point point;
    RetryPolicy retryPolicy;
    LatLng ubicacion;
    CameraPosition cameraPosition;
    Uri gmmIntentUri;
    String NoPaciente;
    String lista;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("sesiones",Context.MODE_PRIVATE);
        editor = preferences.edit();

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(this.getResources().getColor(R.color.white));

        }

        binding = ActivityMapaPrincipalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocalizacion();


        View headerLayout1 = findViewById(R.id.bottomJsoft);
        /*
        imgmarker = (ImageView) findViewById(R.id.ImgMarker);

        txtnombre_local = (TextView) findViewById(R.id.txtNombreLocal);
        txtDireccion = (TextView) findViewById(R.id.txtDireccion);
        txtHorario = (TextView) findViewById(R.id.txtHorario);*/
        lista = "Mapa";
        txtUbicacion = (TextView) findViewById(R.id.txtUbicacion);
        tvTiempo = (TextView) findViewById(R.id.tvTiempo);
        tvDistancia = (TextView) findViewById(R.id.tvKm);
        ivBSImagen = (ImageView) findViewById(R.id.ivBSImagen);
        btnNavegar = (ImageView) findViewById(R.id.ivNavegarMapbox);
        btnNavGoogle = (ImageView) findViewById(R.id.ivNavegar);
        btnDetalles = (ImageView) findViewById(R.id.ivDetalles);
        btnTrazarRuta = (ImageView) findViewById(R.id.ivRuta);
        txtNumTotal = (TextView) findViewById(R.id.txtNumTotal);
        txtNumRojo = (TextView) findViewById(R.id.txtNumRojo);
        txtNumAmarillo = (TextView) findViewById(R.id.txtNumAmarillo);
        txtNumVerde = (TextView) findViewById(R.id.txtNumVerde);
        txtNumNegro = (TextView) findViewById(R.id.txtNumNegro);
        bottomSheet = findViewById(R.id.bottomJsoft);
        bottomSheet.setTranslationY(0);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setPeekHeight(0);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        latori = 0.0;
        lngori = 0.0;
        latdes = 0.0;
        lngdes = 0.0;
        line = null;
        rutaTrazada = false;
        tiempo = "";
        distancia = "";
        ultNoPaciente = 0;
        cTotal = 0;
        cRojo = 0;
        cAmarillo = 0;
        cVerde = 0;
        cNegro = 0;
        markerUbicacion = null;
        popup = null;
        NoPaciente = "";

        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.mapa);
        bottomNavigationView.setItemIconTintList(null);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        usuario = (TextView) findViewById(R.id.NombresUsuario);

        intent = getIntent();
        nombre = intent.getStringExtra("nombre");

        usuario.setText(nombre);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.listaheridos:
                        Intent intent = new Intent(MapaPrincipal.this,ListaHeridos.class);
                        intent.putExtra("nombre",nombre);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    case R.id.registrarpaciente:
                        Intent intent2 = new Intent(MapaPrincipal.this,RegistrarPaciente.class);
                        intent2.putExtra("nombre",nombre);
                        startActivity(intent2);
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    case R.id.mapa:
                        return true;

                    case R.id.historial:
                        Intent intent3 = new Intent(MapaPrincipal.this,HistorialRegistros.class);
                        intent3.putExtra("nombre",nombre);
                        startActivity(intent3);
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                }
                return false;
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //mMap = null;
        jsonObjectRequest.cancel();
        binding = null;
        prefs = null;
        locationManager = null;
        currentLocation = null;
        jsonObjectRequest = null;
        temporalRealTimeMarkers = null;
        realTimeMarkers = null;
        marcador = null;
        mBottomSheetBehavior1 = null;
        tapactionlayout = null;
        white_forground_view = null;
        bottomSheet = null;
        txtUbicacion = null;
        tvcolor = null;
        tvTiempo = null;
        tvDistancia = null;
        ivBSImagen = null;
        btnNavegar = null;
        btnNavGoogle = null;
        btnDetalles = null;
        btnTrazarRuta = null;
        latori = null;
        lngori = null;
        latdes = null;
        lngdes = null;
        line = null;
        rutaTrazada = null;
        tiempo = null;
        distancia = null;
        ultNoPaciente = null;
        bottomNavigationView = null;
        usuario = null;
        toolbar = null;
        txtNumTotal = null;
        txtNumRojo = null;
        txtNumAmarillo = null;
        txtNumVerde = null;
        txtNumNegro = null;
        cTotal = null;
        cRojo = null;
        cAmarillo = null;
        cVerde = null;
        cNegro = null;
        markerUbicacion = null;
        popup = null;
        herido = null;
        tvLat = null;
        tvLng = null;
        tvAlt = null;
        imagenMarker = null;
        markerOptions = null;
        marcador_ = null;
        jsonObject = null;
        info = null;
        mMap.setOnMarkerClickListener(null);
        mMap.setInfoWindowAdapter(null);
        mMap = null;
        locationListener = null;
        url = null;
        intent = null;
        nombre = null;
        roundedBitmapDrawable = null;
        json = null;
        poly = null;
        vectorDrawable = null;
        bitmap = null;
        canvas = null;
        requestQueue = null;
        jsonObjectRequest = null;
        status = null;
        routes = null;
        points = null;
        polylineOptions = null;
        legs = null;
        steps = null;
        polyline = null;
        list = null;
        position = null;
        bounds = null;
        point = null;
        retryPolicy = null;
        ubicacion = null;
        cameraPosition = null;
        gmmIntentUri = null;
        NoPaciente = null;
        lista = null;
        Runtime.getRuntime().gc();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.item2:
                this.logout();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("UsuarioJson");
        editor.apply();
        this.finish();
        this.overridePendingTransition(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim);
         */
        editor.putBoolean("sesion", false);
        editor.apply();
        intent = new Intent(this, MenuPrincipal.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void getLocalizacion() {
        int permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permiso == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter(){
            @Nullable
            @Override
            public View getInfoContents(@NonNull Marker marker) {

                return null;
            }

            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                try {
                    clickCount = (Integer) marker.getTag();
                    return null;

                } catch (Exception ex) {

                    info = new herido();
                    info = (herido) marker.getTag();


                    if (popup == null){
                        popup=getLayoutInflater().inflate(R.layout.popupmarker, null);
                    }

                    tvLat = (TextView) popup.findViewById(R.id.tvLat);
                    tvLng = (TextView) popup.findViewById(R.id.tvLng);
                    tvAlt = (TextView) popup.findViewById(R.id.tvAlt);
                    imagenMarker = (ImageView) popup.findViewById(R.id.icon);

                    if(info.getImagen()!=null) {
                        roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(Resources.getSystem(), info.getImagen());
                        roundedBitmapDrawable.setCircular(true);
                        imagenMarker.setImageDrawable(roundedBitmapDrawable);
                        //holder.imagen.setImageBitmap(listaPersonas.get(position).getImagen());
                    }

                    tvLat.setText(info.getLatitud().toString());
                    tvLng.setText(info.getLongitud().toString());
                    tvAlt.setText(info.getAltitud().toString() + " m");
                    return (popup);

                }
            }
        });

        LatLngBounds adelaideBounds = new LatLngBounds(
                new LatLng(28.051344, -117.368531),
                new LatLng(32.787179, -113.063591)
        );
        mMap.setMinZoomPreference(7.0f);
        mMap.setMaxZoomPreference(26.0f);
        mMap.setLatLngBoundsForCameraTarget(adelaideBounds);
        miUbicacion();
        mMap.setOnMarkerClickListener(this);
        webServiceMarcadores();

    }

    private void miUbicacion(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        locationManager = (LocationManager) MapaPrincipal.this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                if (markerUbicacion != null){
                    markerUbicacion.remove();
                }
                latori = location.getLatitude();
                lngori = location.getLongitude();
                //LatLng miUbicacion = new LatLng(latori, lngori);
                /*markerUbicacion = mMap.addMarker(new MarkerOptions().position(miUbicacion).title("Ubicación actual")
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marcador_ubicacion)));*/
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        int permiso = ContextCompat.checkSelfPermission(MapaPrincipal.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }


    @Override
    public boolean onMarkerClick(Marker marker) {

            //mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);

        try {
            clickCount = (Integer) marker.getTag();
            bottomSheet.setTranslationY(0);
            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
            //  return true;

        } catch (Exception ex) {

            info = new herido();
            info = (herido) marker.getTag();
            NoPaciente = info.getNoPaciente().toString();

            if (ultNoPaciente != info.getNoPaciente()){

                if (rutaTrazada == true)
                {
                    borrarRuta();
                    tvTiempo.setText("");
                    tvDistancia.setText("");
                }

                txtUbicacion.setText(info.getUbicacion());
                latdes = info.getLatitud();
                lngdes = info.getLongitud();

                switch (info.getColor()){
                    case "Rojo":
                        ivBSImagen.setImageDrawable(getResources().getDrawable(R.drawable.ic_icono_marker_rojo));
                        break;
                    case "Amarillo":
                        ivBSImagen.setImageDrawable(getResources().getDrawable(R.drawable.ic_icono_marker_amarillo));
                        break;
                    case "Verde":
                        ivBSImagen.setImageDrawable(getResources().getDrawable(R.drawable.ic_icono_marker_verde));
                        break;
                    case "Negro":
                        ivBSImagen.setImageDrawable(getResources().getDrawable(R.drawable.ic_icono_marker_negro));
                        break;
                }
                bottomSheet.setTranslationY(-230);
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                ultNoPaciente = info.getNoPaciente();

            }


        }

        return false;
    }

    public void CerrarBottomSheet(View view){
        bottomSheet.setTranslationY(0);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        if (rutaTrazada == true)
        {
            borrarRuta();
        }
    }

    private void webServiceMarcadores(){

        //url = "http://192.168.1.12/sistematriage/ConsultarUbicaciones.php";
        url = "http://ec2-54-183-143-71.us-west-1.compute.amazonaws.com/ConsultarUbicaciones.php";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //showToast("Consulta Exitosa");
                herido=null;

                json=response.optJSONArray("paciente");

                try {

                    if (json != null){
                        for (int i=0;i<json.length();i++){
                            herido = new herido();
                            jsonObject=null;
                            jsonObject=json.getJSONObject(i);

                            herido.setNoPaciente(jsonObject.optInt("NoPaciente"));
                            herido.setUbicacion(jsonObject.optString("Ubicacion"));
                            herido.setColor(jsonObject.optString("Color"));
                            herido.setLatitud(jsonObject.optDouble("Latitud"));
                            herido.setLongitud(jsonObject.optDouble("Longitud"));
                            herido.setAltitud(jsonObject.optDouble("Altitud"));
                            herido.setDato(jsonObject.optString("Foto"));

                            markerOptions = new MarkerOptions();
                            marcador_ = null;

                            switch (herido.getColor()){

                                case "Rojo":
                                    markerOptions.position(new LatLng(herido.getLatitud(), herido.getLongitud())).title(herido.getUbicacion()).snippet(herido.getColor())
                                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_icono_marker_rojo));

                                    marcador_ = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(herido.getLatitud(), herido.getLongitud())).title(herido.getUbicacion()).snippet(herido.getColor())
                                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_icono_marker_rojo))
                                    );
                                    cRojo++;
                                    break;
                                case "Amarillo":
                                    markerOptions.position(new LatLng(herido.getLatitud(), herido.getLongitud())).title(herido.getUbicacion()).snippet(herido.getColor())
                                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_icono_marker_amarillo));

                                    marcador_ = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(herido.getLatitud(), herido.getLongitud())).title(herido.getUbicacion()).snippet(herido.getColor())
                                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_icono_marker_amarillo))
                                    );
                                    cAmarillo++;
                                    break;
                                case "Verde":
                                    markerOptions.position(new LatLng(herido.getLatitud(), herido.getLongitud())).title(herido.getUbicacion()).snippet(herido.getColor())
                                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_icono_marker_verde));

                                    marcador_ = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(herido.getLatitud(), herido.getLongitud())).title(herido.getUbicacion()).snippet(herido.getColor())
                                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_icono_marker_verde))
                                    );
                                    cVerde++;
                                    break;
                                case "Negro":
                                    markerOptions.position(new LatLng(herido.getLatitud(), herido.getLongitud())).title(herido.getUbicacion()).snippet(herido.getColor())
                                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_icono_marker_negro));

                                    marcador_ = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(herido.getLatitud(), herido.getLongitud())).title(herido.getUbicacion()).snippet(herido.getColor())
                                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_icono_marker_negro))
                                    );
                                    cNegro++;
                                    break;
                            }

                            marcador_.setTag(herido);
                            temporalRealTimeMarkers.add(mMap.addMarker(markerOptions));

                            if (i == 0){
                                LatLng ultmarcador = new LatLng(herido.getLatitud(), herido.getLongitud());
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(ultmarcador));
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(ultmarcador)
                                        .zoom(14)
                                        .bearing(0)
                                        .tilt(0)
                                        .build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            }

                        }

                        realTimeMarkers.clear();
                        realTimeMarkers.addAll(temporalRealTimeMarkers);

                        cTotal = cRojo + cAmarillo + cVerde + cNegro;
                        txtNumTotal.setText("T: " + cTotal.toString());
                        txtNumRojo.setText(cRojo.toString());
                        txtNumAmarillo.setText(cAmarillo.toString());
                        txtNumVerde.setText(cVerde.toString());
                        txtNumNegro.setText(cNegro.toString());

                    }else{
                        aMiUbicacion();
                        Toast.makeText(MapaPrincipal.this, "No se han encontrado registros", Toast.LENGTH_SHORT).show();
                    }



                } catch (JSONException e) {
                    aMiUbicacion();
                    e.printStackTrace();
                    Toast.makeText(MapaPrincipal.this, "No se ha podido establecer conexión con el servidor" +" "+response, Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                aMiUbicacion();
                Toast.makeText(MapaPrincipal.this, "No se ha podido conectar", Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getIntanciaVolley(this).addToRequestQueue(jsonObjectRequest);

    }

    private List<LatLng> decodePoly(String encoded) {

        poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void direction(){

        requestQueue = Volley.newRequestQueue(this);
        url = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
                .buildUpon()
                .appendQueryParameter("destination", latdes + ", " + lngdes)
                .appendQueryParameter("origin", latori + ", " + lngori)
                .appendQueryParameter("mode", "driving")
                .appendQueryParameter("departure_time", "now")
                .appendQueryParameter("traffic_model", "best_guess")
                .appendQueryParameter("units", "metric")
                .appendQueryParameter("key", "AIzaSyCRzX9PLLtbXtHvvCCOoL-MW_hC_hMM7os")
                .toString();

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    status = response.getString("status");
                    if (status.equals("OK")) {
                        routes = response.getJSONArray("routes");

                        polylineOptions = null;

                        for (int i=0; i<routes.length(); i++){
                            points = new ArrayList<>();
                            polylineOptions = new PolylineOptions();
                            legs = routes.getJSONObject(i).getJSONArray("legs");
                            tiempo = legs.getJSONObject(i).getJSONObject("duration_in_traffic").getString("text");
                            distancia = legs.getJSONObject(i).getJSONObject("distance").getString("text");


                            for (int j=0; j<legs.length(); j++){
                                steps = legs.getJSONObject(j).getJSONArray("steps");

                                for(int k=0; k<steps.length(); k++){
                                    polyline = steps.getJSONObject(k).getJSONObject("polyline").getString("points");
                                    list = decodePoly(polyline);

                                    for (int l=0; l<list.size(); l++){
                                        position = new LatLng((list.get(l)).latitude, (list.get(l)).longitude);
                                        points.add(position);
                                    }
                                }
                            }
                            polylineOptions.addAll(points);
                            polylineOptions.width(10);
                            polylineOptions.color(ContextCompat.getColor(MapaPrincipal.this, R.color.azul));
                            polylineOptions.geodesic(true);
                        }

                        line = mMap.addPolyline(polylineOptions);
                        tvTiempo.setText(tiempo);
                        tvDistancia.setText(distancia);

                        bounds = new LatLngBounds.Builder()
                                .include(new LatLng(latori, lngori))
                                .include(new LatLng(latdes, lngdes)).build();
                        point = new Point();
                        getWindowManager().getDefaultDisplay().getSize(point);
                        //valores por default: height=150, padding=30
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, point.x, 200, 30));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        retryPolicy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(jsonObjectRequest);
    }

    public void trazarRuta(View view){

        try {
            if (rutaTrazada == true)
            {
                borrarRuta();
            }
            direction();
            rutaTrazada = true;
            HabilitarBotonesNav();
        }catch(Exception e){

        }

    }

    private void borrarRuta() {
        line.remove();
        line=null;
        rutaTrazada = false;
        DeshabilitarBotonesNav();
    }

    private void aMiUbicacion(){
        ubicacion = new LatLng(latori, lngori);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion));
        cameraPosition = new CameraPosition.Builder()
                .target(ubicacion)
                .zoom(16)
                .bearing(0)
                .tilt(0)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void ZoomOut(View view){
        mMap.animateCamera(CameraUpdateFactory.zoomOut());
    }

    public void ZoomIn(View view){
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
    }

    public void Navegar(View view){
        gmmIntentUri = Uri.parse("google.navigation:q="+latdes+","+lngdes);
        intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    public void NavegarMapbox(View view){
        Intent intent = new Intent(MapaPrincipal.this,ActividadNavegacion.class);
        intent.putExtra("originLat", latori);
        intent.putExtra("originLng", lngori);
        intent.putExtra("destinationLat", latdes);
        intent.putExtra("destinationLng", lngdes);
        startActivity(intent);
        finish();
    }

    public void HabilitarBotonesNav(){
        btnTrazarRuta.setVisibility(View.GONE);
        btnDetalles.setVisibility(View.GONE);
        tvTiempo.setVisibility(View.VISIBLE);
        tvDistancia.setVisibility(View.VISIBLE);
        btnNavegar.setVisibility(View.VISIBLE);
        btnNavGoogle.setVisibility(View.VISIBLE);
    }

    public void DeshabilitarBotonesNav(){
        tvTiempo.setVisibility(View.GONE);
        tvDistancia.setVisibility(View.GONE);
        btnNavegar.setVisibility(View.GONE);
        btnNavGoogle.setVisibility(View.GONE);
        btnTrazarRuta.setVisibility(View.VISIBLE);
        btnDetalles.setVisibility(View.VISIBLE);
    }

    public void aMiUbicacionBoton(View view){
        aMiUbicacion();
    }

    public void actualizarMarcadores(View view){

        cTotal = 0;
        cRojo = 0;
        cAmarillo = 0;
        cVerde = 0;
        cNegro = 0;
        webServiceMarcadores();

    }

    public void AHeridoSeleccionado(View view){
        intent = new Intent(this,PerfilHerido.class);
        intent.putExtra("NoPaciente", NoPaciente);
        intent.putExtra("nombre", nombre);
        intent.putExtra("lista", lista);
        startActivity(intent);
        finish();
    }

}