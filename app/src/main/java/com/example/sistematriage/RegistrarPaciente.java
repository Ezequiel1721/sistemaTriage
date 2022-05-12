package com.example.sistematriage;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegistrarPaciente extends AppCompatActivity {

    private SharedPreferences prefs;
    TextView usuario2;
    Toolbar toolbar;

    EditText ubi, color, usuario, estado;
    Button btnGuardar;

    private final String CARPETA_RAIZ="misImagenesPrueba/";
    private final String RUTA_IMAGEN=CARPETA_RAIZ+"misFotos";

    final int COD_SELECCIONA=10;
    final int COD_FOTO=20;

    ImageView IV;
    String path;
    Bitmap bitmap;

    RequestQueue request;
    StringRequest stringRequest;
    ProgressDialog progress;

    BottomNavigationView bottomNavigationView;

    RadioButton rbRojo, rbAmarillo, rbVerde, rbNegro;

    DateFormat df = new SimpleDateFormat("dd-MM-yyyy, HH:mm");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_paciente);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usuario2 = (TextView) findViewById(R.id.NombresUsuario);

        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombre");

        usuario2.setText(nombre);

        request = Volley.newRequestQueue(this);
        ubi = ((EditText)findViewById(R.id.etUbicacion));

        IV = (ImageView) findViewById(R.id.foto);

        btnGuardar = ((Button)findViewById(R.id.btnGuardar));

        IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onclick();


            }
        });

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.registrarpaciente);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.listaheridos:
                        //startActivity(new Intent(getApplicationContext(),ListaHeridos.class));
                        Intent intent = new Intent(RegistrarPaciente.this,ListaHeridos.class);
                        intent.putExtra("nombre",nombre);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.registrarpaciente:
                        return true;

                    case R.id.mapa:
                        startActivity(new Intent(getApplicationContext(),Mapa.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.historial:
                        startActivity(new Intent(getApplicationContext(),HistorialRegistros.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });

        rbNegro = (RadioButton) findViewById(R.id.rbNegro);
        rbRojo = (RadioButton) findViewById(R.id.rbRojo);
        rbAmarillo = (RadioButton) findViewById(R.id.rbAmarillo);
        rbVerde = (RadioButton) findViewById(R.id.rbVerde);

        if(validaPermisos()){
            IV.setEnabled(true);
        }else{
            IV.setEnabled(false);
        }
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
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private boolean validaPermisos() {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED)&&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
        }

        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                IV.setEnabled(true);
            }else{
                solicitarPermisosManual();
            }
        }

    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones={"si","no"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(RegistrarPaciente.this);
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(RegistrarPaciente.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        dialogo.show();
    }

    public void onclick() {
        cargarImagen();
    }

    private void cargarImagen() {

        final CharSequence[] opciones={"Tomar Foto","Cargar Imagen","Cancelar"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(RegistrarPaciente.this);
        alertOpciones.setTitle("Seleccione una Opción");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Tomar Foto")){
                    tomarFotografia();
                }else{
                    if (opciones[i].equals("Cargar Imagen")){
                        Intent intent=new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicación"),COD_SELECCIONA);
                    }else{
                        dialogInterface.dismiss();
                    }
                }
            }
        });
        alertOpciones.show();

    }

    private void tomarFotografia() {
        File fileImagen=new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        boolean isCreada=fileImagen.exists();
        String nombreImagen="";
        if(isCreada==false){
            isCreada=fileImagen.mkdirs();
        }

        if(isCreada==true){
            nombreImagen=(System.currentTimeMillis()/1000)+".jpg";
        }


        path=Environment.getExternalStorageDirectory()+
                File.separator+RUTA_IMAGEN+File.separator+nombreImagen;


        File imagen=new File(path);
        Intent intent=null;
        intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ////
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
        {
            String authorities=getApplicationContext().getPackageName()+".provider";
            Uri imageUri= FileProvider.getUriForFile(this,authorities,imagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }else
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
        }
        startActivityForResult(intent,COD_FOTO);

        ////
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){

            switch (requestCode){
                case COD_SELECCIONA:
                    Uri miPath=data.getData();
                    IV.setImageURI(miPath);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),miPath);
                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                        roundedBitmapDrawable.setCircular(true);
                        IV.setImageDrawable(roundedBitmapDrawable);
                        //IV.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case COD_FOTO:

                    MediaScannerConnection.scanFile(this, new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("Ruta de almacenamiento","Path: "+path);
                                }
                            });

                    ;
                    bitmap= BitmapFactory.decodeFile(path);


                    ExifInterface exif = null;
                    try {
                        exif = new ExifInterface(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                    int rotationInDegrees = exifToDegrees(rotation);

                    Matrix matrix = new Matrix(); if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}



                    Bitmap adjustedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    bitmap = adjustedBitmap;
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                    roundedBitmapDrawable.setCircular(true);
                    IV.setImageDrawable(roundedBitmapDrawable);
                    //IV.setImageBitmap(bitmap);

                    break;
            }

            bitmap = redimensionarImagen(bitmap, 600, 800);
        }

    }

    private static int exifToDegrees(int exifOrientation) { if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) { return 180; } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) { return 270; } return 0; }


    private void showToast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    public void Guardar(View v){
        PreguntaGuardar();
    }

    private void cargarWebService(){

        progress = new ProgressDialog(this);
        progress.setMessage("Cargando...");
        progress.show();

        String url="http://192.168.0.17/bd/RegistrarPacientes.php";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                ///if (response.trim().equalsIgnoreCase("Registra")) {
                ubi.setText("");


                IV.setImageResource(R.drawable.ic_launcher_background);

                Toast.makeText(RegistrarPaciente.this, response, Toast.LENGTH_LONG).show();
                showToast("Se ha Registrado Exitosamente");
                Intent nuevoform = new Intent(RegistrarPaciente.this, ListaHeridos.class);
                startActivity(nuevoform);
                finish();
                /*}else{
                    showToast("No se puede registrar");
                    Log.i("RESPUESTA: ",""+response);
                }*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(RegistrarPaciente.this,error.getMessage().toString(),Toast.LENGTH_LONG).show();
            }
        }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                String Ubicacion = ubi.getText().toString();
                String Color = obtenerColor();
                String Usuario = "Guillermo Vázquez";
                String Estado = "En espera";
                String Fecha = df.format(Calendar.getInstance().getTime());


                String imagen = convertirImgString(bitmap);

                Map<String,String> parametros = new HashMap<>();

                parametros.put("Ubicacion",Ubicacion);
                parametros.put("Color",Color);
                parametros.put("Usuario",Usuario);
                parametros.put("Estado",Estado);
                parametros.put("Fecha", Fecha);
                parametros.put("imagen",imagen);

                return parametros;
            }
        };
        request.add(stringRequest);
    }

    private String convertirImgString(Bitmap bitmap) {

        ByteArrayOutputStream array=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imagenByte=array.toByteArray();
        String imagenString= Base64.encodeToString(imagenByte,Base64.DEFAULT);

        return imagenString;
    }



    private Bitmap redimensionarImagen(Bitmap bitmap, float anchoNuevo, float altoNuevo) {

        int ancho = bitmap.getWidth();
        int alto = bitmap.getHeight();

        if (ancho > anchoNuevo || alto > altoNuevo) {
            float escalaAncho = anchoNuevo / ancho;
            float escalaAlto = altoNuevo / alto;

            Matrix matrix = new Matrix();
            matrix.postScale(escalaAncho, escalaAlto);

            return Bitmap.createBitmap(bitmap, 0, 0, ancho, alto, matrix, false);

        } else {
            return bitmap;
        }

    }

    private void PreguntaGuardar() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(RegistrarPaciente.this);
        dialogo.setTitle("¿Deseas guardar la información?");
        dialogo.setMessage("Los datos se guardarán");


        dialogo.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cargarWebService();
            }
        });
        dialogo.setNegativeButton(Html.fromHtml("<font color='#696B6A'>Cancelar</font>"), new DialogInterface.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogo.show();

    }

    public void AListaHeridos (View view){
        Intent regr = new Intent(RegistrarPaciente.this,ListaHeridos.class);
        startActivity(regr);
        finish();
    }

    public String obtenerColor() {

        if (rbNegro.isChecked()==true){
            return "Negro";
        }

        else if (rbRojo.isChecked()==true){
            return "Rojo";
        }

        else if (rbAmarillo.isChecked()==true){
            return "Amarillo";
        }

        else if (rbVerde.isChecked()==true){
            return "Verde";
        }

        else{
            return "";
        }
    }

}