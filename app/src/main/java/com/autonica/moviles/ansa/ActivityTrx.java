package com.autonica.moviles.ansa;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityTrx extends AppCompatActivity {
    private String mParam1,mParam2,mParam3,mParam4;
    private EditText txttrx,txtubictrx,txtcodigotrx,txtcantsoltrx,txtscantrx,txtdesctrx,txtcodigotrxmuestreo;
    private TextView textView8,textView10;
    private ListView lsttrx;
    MyDBAdapter dbAdapter;
    BackEnd backEnd = new BackEnd(ActivityTrx.this);
    private List<Map<String, String>> trx = new ArrayList<Map<String,String>>();
    private SimpleAdapter AD;
    private Button btnsubir,btnlimpiar;
    private Integer r = 0;
    Rutinas rutinas = new Rutinas();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trx);
        mParam1 = getIntent().getExtras().getString("trx");
        mParam2 = getIntent().getExtras().getString("userid");
        mParam3 = getIntent().getExtras().getString("location");
        mParam4 = getIntent().getExtras().getString("type");

        txttrx =        (EditText) findViewById(R.id.txttrx);
        txtubictrx =    (EditText) findViewById(R.id.txtubictrx);
        txtcodigotrx =  (EditText) findViewById(R.id.txtcodigotrx);
        txtcantsoltrx = (EditText) findViewById(R.id.txtcantsoltrx);
        txtscantrx =    (EditText) findViewById(R.id.txtscantrx);
        txtdesctrx =    (EditText) findViewById(R.id.txtdesctrx);
        txtcodigotrxmuestreo =    (EditText) findViewById(R.id.txtcodigotrxmuestreo);
        lsttrx =        (ListView) findViewById(R.id.lsttrx);
        textView10 =     (TextView) findViewById(R.id.textView10);
        textView8 =     (TextView) findViewById(R.id.textView8);
        btnsubir  =     (Button)findViewById(R.id.btnsubir);
        btnlimpiar  =     (Button)findViewById(R.id.btnlimpiar);
        txttrx.setText(mParam1.toString());
        dbAdapter = new MyDBAdapter(getApplicationContext());
        cargardetalletrx();

        btnlimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiar();
                txtubictrx.requestFocus();
            }
        });

        txtubictrx.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (txtubictrx.getText().toString().equals("")) {txtubictrx.requestFocus();return true;}
                    txtubictrx.setText(txtubictrx.getText().toString().trim().replace("'","-"));
                    cargardetalleubicacion();
                    txtcodigotrx.requestFocus();
                    return true;
                }
                return false;
            }
        });

        txtcodigotrx.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (txtcodigotrx.getText().toString().equals("")){ txtcodigotrx.requestFocus(); return true;}
                    txtcodigotrx.setText(rutinas.ITEMNMBRFormat(txtcodigotrx.getText().toString().trim().replace("'","-")));
                    actualizarscancodigo();
                    cargardetalleubicacion();
                    if (Integer.parseInt(txtcantsoltrx.getText().toString()) == Integer.parseInt(txtscantrx.getText().toString())){
                        Toast.makeText(getApplicationContext(),"Recolectado totalmente..." + txtcodigotrx.getText().toString().trim(), Toast.LENGTH_LONG).show();
                        cargardetalletrx();
                        limpiar();
                        txtubictrx.requestFocus();
                        return true;
                    }
                    txtcodigotrx.setText("");
                    txtcodigotrx.requestFocus();
                }
                return false;
            }
        });


        btnsubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subirTrx();
                limpiar();
            }
        });

        txtubictrx.requestFocus();
    }

    private void cargardetalletrx(){
        trx.clear();
        dbAdapter.openSQLite();
        Cursor cursor = null;
        if (mParam4.toString().equals("Envio")){
             cursor = dbAdapter.execSQLlite("SELECT * FROM TRXS WHERE IVDOCNBR = '"+txttrx.getText().toString().trim()+"' AND TRXQTY   > SSCANTRX AND TRXLOCTN = '"+mParam3.toString()+"' ORDER BY BINNMBRLOCTN");
        }else if(mParam4.toString().equals("Recepcion")){
             cursor = dbAdapter.execSQLlite("SELECT * FROM TRXS WHERE IVDOCNBR = '"+txttrx.getText().toString().trim()+"' AND SSCANTRX > RSCANTRX AND TRNSTLOC = '"+mParam3.toString()+"' ORDER BY BINNMBRTRNSTLOC");
        }
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Map<String, String> datanum = new HashMap<String,String>();
                datanum.put("A", cursor.getString(4));
                datanum.put("B", cursor.getString(5));
                if (mParam4.toString().equals("Envio")) {
                    datanum.put("C", cursor.getString(6));
                    datanum.put("D", cursor.getString(10));
                    datanum.put("E", cursor.getString(14));
                }else if(mParam4.toString().equals("Recepcion")){
                    datanum.put("C", cursor.getString(7));
                    datanum.put("D", cursor.getString(15));
                    datanum.put("E", cursor.getString(13));
                }
                trx.add(datanum);
            } while (cursor.moveToNext());
        }
        String[] from = {"A","B","C","D","E"};
        int[] views = {R.id.txtcodigotrx,R.id.txtdesctrx,R.id.txtubicaciontrx,R.id.txtsolicitadotrx,R.id.txtdestinotrx};
        AD = new SimpleAdapter(getApplicationContext(), trx, R.layout.listdetalletrx, from, views);
        lsttrx.setAdapter(AD);

        dbAdapter.openSQLite();
        Cursor cursor2 = null;
        if (mParam4.toString().equals("Envio")){
            cursor2 = dbAdapter.execSQLlite("SELECT COUNT(*) AS FALTANTE,(SELECT COUNT(*) FROM TRXS WHERE IVDOCNBR = '"+txttrx.getText().toString().trim()+"' AND TRXLOCTN = '"+mParam3.toString()+"') AS TOTAL FROM TRXS WHERE IVDOCNBR = '"+txttrx.getText().toString().trim()+"' AND TRXQTY   <= SSCANTRX  AND TRXLOCTN = '"+mParam3.toString()+"'");
        }else if(mParam4.toString().equals("Recepcion")){
            cursor2 = dbAdapter.execSQLlite("SELECT COUNT(*) AS FALTANTE,(SELECT COUNT(*) FROM TRXS WHERE IVDOCNBR = '"+txttrx.getText().toString().trim()+"' AND TRNSTLOC = '"+mParam3.toString()+"') AS TOTAL FROM TRXS WHERE IVDOCNBR = '"+txttrx.getText().toString().trim()+"' AND SSCANTRX <= RSCANTRX  AND TRNSTLOC = '"+mParam3.toString()+"'");
        }
        if (cursor2 != null && cursor2.moveToFirst()) {
            do {
                textView8.setText(cursor2.getString(0) + " ");
                textView10.setText(cursor2.getString(1));
            } while (cursor2.moveToNext());
        }
    }

    private void cargardetalleubicacion(){
        if (txtubictrx.getText().toString().trim().equals("")){return;}
        dbAdapter.openSQLite();
        Cursor cursor = null;
        if (mParam4.toString().equals("Envio")) {
            cursor = dbAdapter.execSQLlite("SELECT * FROM TRXS WHERE IVDOCNBR = '" + txttrx.getText().toString().trim() + "' AND BINNMBRLOCTN = '" + txtubictrx.getText().toString().trim() + "'");
        }else if(mParam4.toString().equals("Recepcion")){
            cursor = dbAdapter.execSQLlite("SELECT * FROM TRXS WHERE IVDOCNBR = '" + txttrx.getText().toString().trim() + "' AND BINNMBRTRNSTLOC = '" + txtubictrx.getText().toString().trim() + "'");
        }
        if (cursor != null && cursor.moveToFirst()) {
            do {
                txtcodigotrxmuestreo.setText(cursor.getString(4));
                txtdesctrx.setText(cursor.getString(5));
                if (mParam4.toString().equals("Envio")) {
                    txtubictrx.setText(cursor.getString(6));
                    txtcantsoltrx.setText(cursor.getString(10));
                    txtscantrx.setText(cursor.getString(15));
                }else if(mParam4.toString().equals("Recepcion")){
                    txtubictrx.setText(cursor.getString(7));
                    txtcantsoltrx.setText(cursor.getString(15));
                    txtscantrx.setText(cursor.getString(18));
                }
            } while (cursor.moveToNext());
        }
    }

    private void actualizarscancodigo(){
        Integer act = 0;
        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ContentValues cv2 = new ContentValues();

        if (mParam4.toString().equals("Envio")) {
            cv2.put("SSCANTRX", (Integer.parseInt(txtscantrx.getText().toString()) + 1));
            cv2.put("SSCANUSERID", hourdateFormat.format(date));
            cv2.put("SUSERID", mParam2.toString());
            act = dbAdapter.modContentSQLite("TRXS", cv2, "ITEMNMBR = '" + txtcodigotrx.getText().toString().trim() + "' AND BINNMBRLOCTN = '" + txtubictrx.getText().toString().trim() + "' AND TRXLOCTN = '" + mParam3.toString().trim() + "' AND IVDOCNBR = '" + txttrx.getText().toString().trim() + "'", 2);
        }else if(mParam4.toString().equals("Recepcion")){
            cv2.put("RSCANTRX", (Integer.parseInt(txtscantrx.getText().toString()) + 1));
            cv2.put("RSCANUSERID", hourdateFormat.format(date));
            cv2.put("RUSERID", mParam2.toString());
            act = dbAdapter.modContentSQLite("TRXS", cv2, "ITEMNMBR = '" + txtcodigotrx.getText().toString().trim() + "' AND BINNMBRTRNSTLOC = '" + txtubictrx.getText().toString().trim() + "' AND TRNSTLOC = '" + mParam3.toString().trim() + "' AND IVDOCNBR = '" + txttrx.getText().toString().trim() + "'", 2);
        }

        if (act != 0){
            //Toast.makeText(getApplicationContext(),"Recolectando..." + txtcodigotrx.getText().toString().trim(), Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(),"Item no encontrado..." + txtcodigotrx.getText().toString().trim(), Toast.LENGTH_LONG).show();
        }
    }

    private void subirTrx(){
        String sql = "";
        dbAdapter.openSQLite();
        Cursor cursor = null;

        if (mParam4.toString().equals("Envio")) {
            cursor = dbAdapter.execSQLlite("SELECT * FROM TRXS WHERE IVDOCNBR = '" + txttrx.getText().toString().trim() + "' AND TRXLOCTN = '" + mParam3.toString().trim() + "' AND SSCANTRX <> 0");
            if (cursor != null && cursor.moveToFirst()) {
                Toast.makeText(getApplicationContext(), "Iniciando proceso...", Toast.LENGTH_LONG).show();
                do { //Insertar en server envio...
                    sql += "INSERT INTO  UTIL.dbo.RellenoIV00102 (TRXSORCE, BACHNUMB, IVDOCTYP, IVDOCNBR, ITEMNMBR, ITEMDESC, BINNMBRLOCTN,BINNMBRTRNSTLOC, LNSEQNBR, UOFM, TRXQTY, UNITCOST, EXTDCOST, TRXLOCTN, TRNSTLOC, SSCANTRX, SUSERID, SSCANUSERID, RSCANTRX, RUSERID, RSCANUSERID, DATEDET) VALUES " +
                            "('" + cursor.getString(0) + "','" + cursor.getString(1) + "','" + cursor.getString(2) + "','" + cursor.getString(3) + "','" + cursor.getString(4) + "','" + cursor.getString(5) + "','" + cursor.getString(6) + "','"+cursor.getString(7)+"','" + cursor.getString(8) + "','" + cursor.getString(9) + "'," +
                            "'" + cursor.getString(10) + "','" + cursor.getString(11) + "','" + cursor.getString(12) + "','" + cursor.getString(13) + "','" + cursor.getString(14) + "','" + cursor.getString(15) + "','" + cursor.getString(16) + "','" + cursor.getString(17) + "','" + cursor.getString(18) + "'," +
                            "'" + cursor.getString(19) + "','" + cursor.getString(20) + "','" + cursor.getString(21) + "')";
                    dbAdapter.modContentSQLite("TRXS",null,"ITEMNMBR = '" + cursor.getString(4) + "' AND BINNMBRLOCTN = '" + cursor.getString(6) + "' AND TRXLOCTN = '" + cursor.getString(13) + "' AND IVDOCNBR = '" + cursor.getString(3) + "'", 3);
                } while (cursor.moveToNext());
                r = backEnd.execSQLUpdate(sql);
                if (r > 0) {
                    Toast.makeText(getApplicationContext(), "Actualizado correctamente...", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Actualizado fallo...", Toast.LENGTH_LONG).show();
                }
            }
        }else if(mParam4.toString().equals("Recepcion")){
            cursor = dbAdapter.execSQLlite("SELECT * FROM TRXS WHERE IVDOCNBR = '" + txttrx.getText().toString().trim() + "' AND TRNSTLOC = '" + mParam3.toString().trim() + "' AND RSCANTRX <> 0");
            if (cursor != null && cursor.moveToFirst()) {
                Toast.makeText(getApplicationContext(), "Iniciando proceso...", Toast.LENGTH_LONG).show();
                do { //Actualizar recepcion...
                    sql += "UPDATE UTIL.dbo.RellenoIV00102 SET RSCANTRX = '" + cursor.getString(18) + "',RUSERID = '"+cursor.getString(19)+"',RSCANUSERID = '"+cursor.getString(20)+"' WHERE ITEMNMBR = '" + cursor.getString(4) + "' AND BINNMBRTRNSTLOC = '" + cursor.getString(7) + "' AND TRNSTLOC = '" + cursor.getString(14) + "' AND IVDOCNBR = '" + cursor.getString(3) + "'";
                    dbAdapter.modContentSQLite("TRXS",null,"ITEMNMBR = '" + cursor.getString(4) + "' AND BINNMBRTRNSTLOC = '" + cursor.getString(7) + "' AND TRNSTLOC = '" + cursor.getString(14) + "' AND IVDOCNBR = '" + cursor.getString(3) + "'", 3);
                } while (cursor.moveToNext());
                r = backEnd.execSQLUpdate(sql);
                if (r > 0) {
                    Toast.makeText(getApplicationContext(), "Actualizado correctamente...", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Actualizado fallo...", Toast.LENGTH_LONG).show();
                }
            }
        }
        cargardetalletrx();
    }

    private void limpiar(){
        txtubictrx.setText("");
        txtcodigotrx.setText("");
        txtcantsoltrx.setText("0");
        txtscantrx.setText("0");
        txtdesctrx .setText("");
        txtcodigotrxmuestreo.setText("");
    }
}
