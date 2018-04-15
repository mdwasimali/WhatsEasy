package yours.aw.whatseasy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_CONTACTS = 123;
    public TextView txtplus;
    public ImageButton imgbtn;
    ArrayList<String> Calllist1 = new ArrayList<>();
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    private long backpressedTime;
    private EditText edtxt;
    private EditText ednum;
    private SwipeButton btn;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgbtn = findViewById(R.id.imgbtn);
        txtplus = findViewById(R.id.txtplus);
        ednum = findViewById(R.id.ednum);
        btn = findViewById(R.id.btn);
        edtxt = findViewById(R.id.edtxt);

        btn.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {

                if (btn.isActive()) {
                    String num = ednum.getText().toString().trim();
                    String text = edtxt.getText().toString();

                    Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + num + "&text=" + text + "");

                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setPackage("com.whatsapp");
                    startActivity(intent);
                    ednum.setText(null);
                    edtxt.setText(null);

                }


            }
        });
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_CALL_LOG)
                        != PackageManager.PERMISSION_GRANTED) {


                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_CALL_LOG},
                            PERMISSIONS_CONTACTS);

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.READ_CALL_LOG)) {

                        Toast.makeText(MainActivity.this, "We need your permission to proceed", Toast.LENGTH_SHORT).show();


                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.


                    }
                } else {
                    // Permission has already been granted
                    getCallDetails();

                }


            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    getCallDetails();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_CALL_LOG},
                            PERMISSIONS_CONTACTS);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        MenuItem itemSwitch = menu.findItem(R.id.mySwitch);
        itemSwitch.setActionView(R.layout.use_switch);
        final Switch sw = menu.findItem(R.id.mySwitch).getActionView().findViewById(R.id.action_switch);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    Intent share = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?text=" + "Send Payment, Text, or Media to anyone on WhatsApp without saving person's WhatsApp number." + "\n" + "DOWNLOAD NOW" + "\n" + "https://play.google.com/store/apps/details?id=yours.aw.whatseasy"));
                    share.setPackage("com.whatsapp");
                    startActivity(share);
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                Intent about = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=5058076751137559980"));
                startActivity(about);


                break;
            case R.id.action_policy:
                Intent policy = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/whatseasy-"));
                startActivity(policy);


                break;
            case R.id.action_update:
                Intent update = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=yours.aw.whatseasy"));
                startActivity(update);

                break;
            case R.id.action_chat:
                Intent chat = new Intent(Intent.ACTION_VIEW, Uri.parse("https://tlk.io/wpeasy-wasimazhar"));
                startActivity(chat);
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void getCallDetails() {

        AlertDialog.Builder build = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.call, null);
        listView = view.findViewById(R.id.listview);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Calllist1);
        listView.setAdapter(arrayAdapter);
        build.setView(view);
        final AlertDialog dialog = build.create();
        dialog.show();


        String strOrder = CallLog.Calls.DATE + " DESC";
        Uri callUri = Uri.parse("content://call_log/calls");
        ContentResolver ca = this.getContentResolver();
        Cursor managedCursor = this.getContentResolver().query(callUri, null, null, null, strOrder);
        final int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);

        while (managedCursor.moveToNext()) {
            final String phNum = managedCursor.getString(number);
            String callTypeCode = managedCursor.getString(type);
            String strname;
            if (managedCursor.getString(name) == null) {
                strname = "unknown";
            } else {
                strname = managedCursor.getString(name);
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    ednum.setText(Calllist1.get(i).replaceAll("[-+.^:,Outgoing,Incoming,Missed]", "").trim());
                    dialog.dismiss();

                }
            });


            int callcode = Integer.parseInt(callTypeCode);
            switch (callcode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callTypeCode = "Outgoing";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    callTypeCode = "Incoming";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callTypeCode = "Missed";
                    break;

            }

            Calllist1.add(phNum + "\n" + callTypeCode);


        }
        managedCursor.close();


    }

    @Override
    public void onBackPressed() {

        if (backpressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(MainActivity.this, "Please press back again to exit", Toast.LENGTH_SHORT).show();
        }
        backpressedTime = System.currentTimeMillis();
    }
}


