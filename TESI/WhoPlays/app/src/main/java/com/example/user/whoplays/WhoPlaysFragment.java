package com.example.user.whoplays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

/**
 * Created by User on 01/12/2017.
 */

public class WhoPlaysFragment extends Fragment {

    FusedLocationProviderClient mFusedLocationClient;

    Location myPosition;
    DatabaseReference databaseReference;
    ArrayList<String> arrayPlace = new ArrayList();
    ArrayList<String> arrayType = new ArrayList();
    ArrayList<String> arrayDate = new ArrayList();
    ArrayList<String> arrayNumberOfPlayer = new ArrayList();
    ArrayList<String> arrayUser = new ArrayList();
    ArrayList<String> arrayTime = new ArrayList();
    ArrayList<String> arrayKey = new ArrayList();
    ArrayList<HashMap<String, String>> data = new ArrayList<>();
    ArrayList<String> arrayDelete = new ArrayList<>();
    String Ordine;
    String Tipo;
    Integer Distance;
    Boolean flag;
    Float distanceInMeters;
    Double latitude;
    Double longitude;


    ListView listView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        Ordine = getArguments().getString("sort");
        Tipo = getArguments().getString("type");
        Distance = getArguments().getInt("distance", 0);


        if (Ordine == null) {
            Ordine = "date";
        }
        if (Tipo == null) {
            Tipo = "Tutte le partite";
        }
        if (Distance == 0) {
            flag = true;
        } else {
            flag = false;
        }

    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_who_plays, container, false);
        getActivity().setTitle(R.string.app_name);
        listView = view.findViewById(R.id.listViewWhoPlays);




        if (flag) {

            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Partite").orderByChild(Ordine).addChildEventListener(new ChildEventListener() {


                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                    String user = dataSnapshot.child("user").getValue().toString();
                    String place = dataSnapshot.child("place").getValue().toString();
                    String date = dataSnapshot.child("date").getValue().toString();
                    String type = dataSnapshot.child("typeOfMatch").getValue().toString();
                    String numberOfPlayer = dataSnapshot.child("numberOfPlayer").getValue().toString();
                    String time = dataSnapshot.child("time").getValue().toString();
                    String key = dataSnapshot.getKey();
                    String address = dataSnapshot.child("latLng").getValue().toString();

                    Calendar calendar1 = Calendar.getInstance();
                    SimpleDateFormat formatter1 = new SimpleDateFormat("dd/M/yyyy h:mm");
                    String currentDate = formatter1.format(calendar1.getTime());

                    final String dateString = date;
                    final String timeString = time;
                    String datadb = dateString + " " + timeString;

//  Toast.makeText(context,"databse date:-"+datadb+"Current Date :-"+currentDate,Toast.LENGTH_LONG).show();

                    if (currentDate.compareTo(datadb) <= 0) {
                        //creo una hasHmap ad ogni ciclo


                        if (Tipo.equals(type) || Tipo.equals("Tutte le partite")) {


                            HashMap<String, String> map = new HashMap<>();

                            //resource è il layout di come voglio ogni singolo item
                            int resource = R.layout.listview_item_who_plays;

                            //qui salvo una stringa con gli stessi nomi messi nell hashMAp
                            String[] from = {"user", "place", "date", "numberOfPlayer"};

                            //qui salvo un altro array contenenti l id di ogni widget del mio singolo item
                            int[] to = {R.id.itemCreatorWhoPlaysTextView, R.id.itemPlaceWhoPlaysTextView, R.id.itemDateWhoPlaysTextView, R.id.itemTypeWhoPlaysTextView};


                            SimpleAdapter adapter = new SimpleAdapter(getActivity(), data, resource, from, to);
                            listView.setAdapter(adapter);

                            arrayDate.add(date);
                            arrayPlace.add(place);
                            arrayUser.add(user);
                            arrayNumberOfPlayer.add(numberOfPlayer);
                            arrayType.add(type);
                            arrayTime.add(time);
                            arrayKey.add(key);

                            //inserisco i dati nell HashMAp

                            map.put("user", user);
                            map.put("date", date + ", ");
                            map.put("place", place + ", ");
                            if (Integer.parseInt(numberOfPlayer) > 0) {
                                map.put("numberOfPlayer", "Cerco " + numberOfPlayer + " giocatori" + " per " + type);
                            } else {
                                map.put("numberOfPlayer", "La partita é completa");
                            }
                            //inserisco l hashMap nell arrayList
                            data.add(map);
                        }

                    } else {
                        Log.d("TAG", key);
                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                        databaseReference1.child("Partite").child(key).setValue(null);

                    }
                }


                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {

            DatabaseReference mdatabaseReference1 = FirebaseDatabase.getInstance().getReference();
            mdatabaseReference1.child("Partite").orderByChild(Ordine).addChildEventListener(new ChildEventListener() {


                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                    String user = dataSnapshot.child("user").getValue().toString();
                    String place = dataSnapshot.child("place").getValue().toString();
                    String date = dataSnapshot.child("date").getValue().toString();
                    String type = dataSnapshot.child("typeOfMatch").getValue().toString();
                    String numberOfPlayer = dataSnapshot.child("numberOfPlayer").getValue().toString();
                    String time = dataSnapshot.child("time").getValue().toString();
                    String key = dataSnapshot.getKey();
                    String address = dataSnapshot.child("latLng").getValue().toString();


                        if (Tipo.equals(type) || Tipo.equals("Tutte le partite")) {

                            Log.d("TAG", "Entrato2");

                            HashMap<String, String> map = new HashMap<>();

                            //resource è il layout di come voglio ogni singolo item
                            int resource = R.layout.listview_item_who_plays;

                            //qui salvo una stringa con gli stessi nomi messi nell hashMAp
                            String[] from = {"user", "place", "date", "numberOfPlayer"};

                            //qui salvo un altro array contenenti l id di ogni widget del mio singolo item
                            int[] to = {R.id.itemCreatorWhoPlaysTextView, R.id.itemPlaceWhoPlaysTextView, R.id.itemDateWhoPlaysTextView, R.id.itemTypeWhoPlaysTextView};


                            SimpleAdapter adapter = new SimpleAdapter(getActivity(), data, resource, from, to);
                            listView.setAdapter(adapter);



                            try {


                                Geocoder selected_place_geocoder = new Geocoder(getContext());
                                List<Address> address1;
                                address1 = selected_place_geocoder.getFromLocationName(address, 1);
                                if (address1 == null) {
                                    //do nothing

                                } else {
                                    Address location = address1.get(0);

                                    double lat = location.getLatitude();
                                    double lng = location.getLongitude();

                                    Location targetLocation = new Location("");//provider name is unnecessary
                                    targetLocation.setLatitude(lat);//your coords of course
                                    targetLocation.setLongitude(lng);


                                    Location yourPosition = new Location("");
                                    yourPosition.setLatitude(44.403342);
                                    yourPosition.setLongitude(8.958401);
                                    distanceInMeters = yourPosition.distanceTo(targetLocation);
                                }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    }

                            if (distanceInMeters < (Distance * 1000)) {
                                    arrayDate.add(date);
                                    arrayPlace.add(place);
                                    arrayUser.add(user);
                                    arrayNumberOfPlayer.add(numberOfPlayer);
                                    arrayType.add(type);
                                    arrayTime.add(time);
                                    arrayKey.add(key);

                                    //inserisco i dati nell HashMAp

                                    map.put("user", user);
                                    map.put("date", date + ", ");
                                    map.put("place", place + ", ");
                                    if (Integer.parseInt(numberOfPlayer) > 0) {
                                        map.put("numberOfPlayer", "Cerco " + numberOfPlayer + " giocatori" + " per " + type);
                                    } else {
                                        map.put("numberOfPlayer", "La partita é completa");
                                    }
                                    //inserisco l hashMap nell arrayList
                                    data.add(map);
                            }


                        }


                }


                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                Intent intent = new Intent(getContext(), FindPlayerActivity.class);
                intent.putExtra("place",arrayPlace.get(position));
                intent.putExtra("date",arrayDate.get(position));
                intent.putExtra("numberOfPlayer",arrayNumberOfPlayer.get(position));
                intent.putExtra("type",arrayType.get(position));
                intent.putExtra("user", arrayUser.get(position));
                intent.putExtra("time", arrayTime.get(position));
                intent.putExtra("key", arrayKey.get(position));
                startActivity(intent);
            }
        });
            return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.who_plays, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_filter) {
            startActivity(new Intent(getActivity(), FilterActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
