package gasaaf.gsf;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private GoogleMap mMap;
    MarkerOptions mo;
    Marker marker;
    private LocationManager locationManager;

    private Intent serviceIntent;
    Button startService;
    Button stopService;
    Button bindService;
    Button unbindService;
    Button update;
    Button savLacks;

    public MicServiceTheSequel MSTS;
    private boolean isServiceBound;
    private ServiceConnection serviceConnection;

    private TextView dis;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mo = new MarkerOptions().position(new LatLng(0, 0)).title("My Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.badnews));
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        } else requestLocation();
        if (!isLocationEnabled())
            showAlert(1);

        dis = (TextView) findViewById(R.id.textView3);
        serviceIntent = new Intent(getApplicationContext(), MicServiceTheSequel.class);


        startService = (Button)findViewById(R.id.startS);
        startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(serviceIntent);
            }
        });

        stopService = (Button) findViewById(R.id.stopS);
        stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(serviceIntent);
            }
        });

        bindService = (Button) findViewById(R.id.button7);
        bindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.i("Great","Button Bind Clicked");
                if(serviceConnection == null)
                {
                    Log.i("Great","Service Connection is Null");
                    serviceConnection = new ServiceConnection() {
                        @Override
                        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                            MicServiceTheSequel.MicServiceTheSequelBinder myBinder = (MicServiceTheSequel.MicServiceTheSequelBinder)iBinder;
                            MSTS = myBinder.getService();
                            isServiceBound = true;
                            Log.i("Yes", "Service Connected");
                        }

                        @Override
                        public void onServiceDisconnected(ComponentName componentName) {
                            isServiceBound = false;
                            Log.i("No", "Service DisConnected");
                        }
                    };
                }

                bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
            });

        unbindService = (Button) findViewById(R.id.button8);
        unbindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isServiceBound)
                {
                    unbindService(serviceConnection);
                    isServiceBound = false;
                    Log.i("No", "Service DisConnected");
                }
            }
        });

        update = (Button) findViewById(R.id.Update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isServiceBound) {
                    dis.setText("Danger" + MSTS.getNumber());
                }
                else{
                    dis.setText("Service Not bound");
                }
            }
        });

        savLacks = (Button) findViewById(R.id.button4);
        savLacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savLac();
            }
        });





    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        marker =  mMap.addMarker(mo);
    }

    @Override
    public void onLocationChanged(Location location) {


            LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
            marker.setPosition(myCoordinates);
            mMap.animateCamera(CameraUpdateFactory.newLatLng(myCoordinates));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myCoordinates, 25));


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    private void requestLocation() {
        locationManager.requestLocationUpdates("gps", 5000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
    }
    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isPermissionGranted() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED || checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("mylog", "Permission is granted");
            return true;
        } else {
            Log.v("mylog", "Permission not granted");
            return false;
        }
    }
    private void showAlert(final int status) {
        String message, title, btnText;
        if (status == 1) {
            message = "Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                    "use this app";
            title = "Enable Location";
            btnText = "Location Settings";
        } else {
            message = "Please allow this app to access location!";
            title = "Permission access";
            btnText = "Grant";
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        if (status == 1) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                        } else
                            requestPermissions(PERMISSIONS, PERMISSION_ALL);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }

    public void savLac()
    {
        if(serviceConnection != null) {
            Log.i("Yes", "Pass 1");
            Thread saveLocation = new Thread() {
                int counterShot = MSTS.getNumber();

                public void run() {
                    try {
                        sleep(1500);
                        Log.i("Yes", "Pass3 ");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (MSTS.getNumber() > counterShot) {
                        Log.i("Yes", "Pass2");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                int height = 80;
                                int width = 60;
                                BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.dz123);
                                Bitmap b=bitmapdraw.getBitmap();
                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);



                                Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                                MarkerOptions mo2 = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()));
                                Marker threat = mMap.addMarker(mo2);
                                threat.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                            }
                        });


                        counterShot = MSTS.getNumber();
                    }



                }
            };
            saveLocation.start();

        }
    }


}

