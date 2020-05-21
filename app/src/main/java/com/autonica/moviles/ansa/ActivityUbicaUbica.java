package com.autonica.moviles.ansa;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import java.sql.*;
import java.util.*;

public class ActivityUbicaUbica extends AppCompatActivity {
    private EditText txtcodigoubica,txtqtyubica,txtexistenciaubica,txtubicaahora,txtubicaantes,txtcantidadubicaSCAN;
    private GridView lstubicaciones;
    private Spinner cmbfacturaubica,cmbcajaubica,cmbtipoubic,cmbpasillosubica,cmbubicacionesubica;
    private ResultSet result;
    private Integer r = 0;
    BackEnd backEnd = new BackEnd(ActivityUbicaUbica.this);
    Rutinas rutinas = new Rutinas();
    private List<Map<String,String>> ubicaciones = new ArrayList<Map<String,String>>();
    private SimpleAdapter AD;
    private String mParam1,mParam2,proveedor;
    private TextView lbcontadorubicacion,lbtotalubicacion,lbcodigoubicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubica_ubica);

        cmbfacturaubica = (Spinner) findViewById(R.id.cmbfacturaubica);
        cmbpasillosubica = (Spinner) findViewById(R.id.cmbpasillosubica);
        cmbubicacionesubica = (Spinner) findViewById(R.id.cmbubicacionesubica);
        cmbtipoubic = (Spinner) findViewById(R.id.cmbtipoubic);
        txtcodigoubica = (EditText) findViewById(R.id.txtcodigoubica);
        txtcantidadubicaSCAN = (EditText) findViewById(R.id.txtcantidadubicaSCAN);
        cmbcajaubica = (Spinner) findViewById(R.id.cmbcajaubica);
        txtqtyubica = (EditText) findViewById(R.id.txtqtyubica);
        txtexistenciaubica = (EditText) findViewById(R.id.txtcantidadubica);
        txtubicaahora = (EditText) findViewById(R.id.txtubicaahora);
        txtubicaantes = (EditText) findViewById(R.id.txtubicaantes);
        lstubicaciones = (GridView) findViewById(R.id.lstubicaciones);
        lbcontadorubicacion = (TextView) findViewById(R.id.lbcontadorubicacion);
        lbcodigoubicion = (TextView) findViewById(R.id.lbcodigoubicion);
        lbtotalubicacion =(TextView) findViewById(R.id.lbtotalubicacion);
        mParam1 = getIntent().getExtras().getString("userlog");
        mParam2 = getIntent().getExtras().getString("location");
        proveedor = getIntent().getExtras().getString("proveedor");
        cargarFacturasCodigos("PV-EXT-0001");

        String[] tipoubicacion = {"UBICACION", "SIN UBICACION"};
        cmbtipoubic.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, tipoubicacion));
        String[] pasillos = {"A", "B","C","D","E","F","G","H","I","J","K","L","M","N","Ñ","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        cmbpasillosubica.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, pasillos));

        ArrayAdapter myAdapinvoice = (ArrayAdapter) cmbfacturaubica.getAdapter();
        if (myAdapinvoice != null) {
            int spinnerPositioninvoice = myAdapinvoice.getPosition(getIntent().getExtras().getString("factura"));
            cmbfacturaubica.setSelection(spinnerPositioninvoice);
            cargarCajasCodigos();
            ArrayAdapter myAdapcase = (ArrayAdapter) cmbcajaubica.getAdapter();
            if (myAdapcase != null) {
                int spinnerPositioncase = myAdapcase.getPosition(getIntent().getExtras().getString("caja"));
                cmbcajaubica.setSelection(spinnerPositioncase);
            }
        }

        txtcodigoubica.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundColor(Color.GREEN);
                } else {
                    v.setBackgroundColor(Color.RED);
                }
            }
        });
        txtubicaahora.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundColor(Color.GREEN);
                } else {
                    v.setBackgroundColor(Color.RED);
                }
            }
        });
        cmbcajaubica.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarCodigosUbica();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cmbpasillosubica.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarUbicacionesVacias();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cmbtipoubic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarCodigosUbica();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txtcodigoubica.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (txtcodigoubica.getText().toString().trim().equals("")) return true;
                    if (cmbfacturaubica.getCount() <= 0) return true;
                    if (cmbcajaubica.getCount() <= 0) return true;
                    if (txtcodigoubica.getText().toString().trim().toUpperCase().equals("LIMPIAR")) {
                        txtcodigoubica.setText("");
                        txtubicaahora.setText("");
                        txtubicaantes.setText("");
                        txtqtyubica.setText("");
                        txtexistenciaubica.setText("");
                        txtcodigoubica.requestFocus();
                    } else {
                        if (proveedor.toString().equals("NINGUNO")){
                            txtcodigoubica.setText(txtcodigoubica.getText().toString().trim().replace("'", "-"));
                        }else{
                            txtcodigoubica.setText(rutinas.ITEMNMBRFormat(txtcodigoubica.getText().toString().trim().replace("'", "-")));
                        }
                        lbcodigoubicion.setText(txtcodigoubica.getText().toString().trim());
                        cargarCodigoFactura();
                        backEnd.execSQLUpdate("UPDATE PL10200 SET QTYLNC = '" +  (Integer.parseInt(txtcantidadubicaSCAN.getText().toString()) + 1) + "',PRSNLNC = '" + mParam1.toString().toUpperCase() + "' WHERE  INVOICE = '" + cmbfacturaubica.getSelectedItem().toString() + "' AND ITEMNMBR = '" + txtcodigoubica.getText().toString().toUpperCase().trim() + "'  AND  CASENO = '" + cmbcajaubica.getSelectedItem().toString() + "'");
                        cargarCodigoFactura();
                    }
                    txtcodigoubica.setText("");
                    txtcodigoubica.requestFocus();
                    return true;
                }
                return false;
            }
        });

        txtubicaahora.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if(txtubicaahora.getText().equals("")) return true;
                    if (cmbfacturaubica.getCount() <= 0) return true;
                    if (cmbcajaubica.getCount() <= 0) return true;
                    txtubicaahora.setText(txtubicaahora.getText().toString().replace("'", "-"));

                    if (txtubicaahora.getText().toString().trim().equals("OTRO ALMACEN")){
                        r = backEnd.execSQLUpdate("UPDATE PL10200 SET POLNESTA = 5,BINNMBR = 'OTRO ALMACEN',TIMELNC = GETDATE(),PRSNLNC = '" + mParam1.toString().toUpperCase() + "',QTYLNC = " + txtqtyubica.getText().toString().trim() + ",QTYONHND = " + txtexistenciaubica.getText().toString().trim() + " WHERE  INVOICE = '" + cmbfacturaubica.getSelectedItem().toString() + "' AND ITEMNMBR = '" + lbcodigoubicion.getText() + "' AND QTYLNC > 0 AND POLNESTA IN (3,4)");
                        if (r >= 1) {
                            Toast.makeText(ActivityUbicaUbica.this, "Se realizo cambio de almacen del codigo " + txtcodigoubica.getText().toString() + " .", Toast.LENGTH_LONG).show();
                            txtqtyubica.setText("");
                            txtexistenciaubica.setText("");
                            txtubicaantes.setText("");
                            txtubicaahora.setText("");
                            txtcodigoubica.setText("");
                            cargarCodigosUbica();
                            verifscan();
                            r = 0;
                            txtcodigoubica.requestFocus();
                            return false;
                        }
                    }


                    if (txtubicaahora.getText().toString().trim().equals("")){txtubicaahora.requestFocus();return true;}
                    else if (!txtubicaantes.getText().toString().trim().equals(txtubicaahora.getText().toString().trim())){
                        if (!txtubicaantes.getText().toString().trim().equals("") && !txtubicaantes.getText().toString().trim().equals("AGRUPAR")){
                            Toast.makeText(ActivityUbicaUbica.this, "Ubicaciones diferentes", Toast.LENGTH_LONG).show();
                            txtubicaahora.setText("");
                            txtubicaahora.requestFocus();
                            return true;
                        }
                    }

                    if (txtcantidadubicaSCAN.getText().toString().trim().equals("0")){txtubicaahora.requestFocus();return true;}

                    String ubicacion_reservada = obtenercodigodeubicacion(txtubicaahora.getText().toString().trim());
                    if (!lbcodigoubicion.getText().toString().trim().toUpperCase().equals(ubicacion_reservada) && !ubicacion_reservada.equals("")){
                        if (txtubicaantes.getText().toString().trim().equals("") || txtubicaantes.getText().toString().trim().equals("AGRUPAR")){
                            Toast.makeText(ActivityUbicaUbica.this, "Ubicacion ya esta asiganada a " + ubicacion_reservada + "....", Toast.LENGTH_LONG).show();
                            txtubicaahora.setText("");
                            txtubicaahora.requestFocus();
                            return true;
                        }
                    }

                    r = backEnd.execSQLUpdate("UPDATE PL10200 SET POLNESTA = 5,TIMELNC = GETDATE(),PRSNLNC = '" + mParam1.toString().toUpperCase() + "',QTYLNC = " + txtcantidadubicaSCAN.getText().toString().trim() + ",QTYONHND = " + txtexistenciaubica.getText().toString().trim() + ",BINNMBRLNC = '" + txtubicaahora.getText().toString().toUpperCase() + "' WHERE CASENO = '" + cmbcajaubica.getSelectedItem().toString() + "' AND INVOICE = '" + cmbfacturaubica.getSelectedItem().toString() + "' AND ITEMNMBR = '" + lbcodigoubicion.getText() + "' AND QTYLNC > 0 AND POLNESTA IN(3,4)");
                    if (r >= 1) {
                        Toast.makeText(ActivityUbicaUbica.this, "Se Ubico la cantidad " + txtcantidadubicaSCAN.getText().toString() + " del codigo " + lbcodigoubicion.getText() + " .", Toast.LENGTH_LONG).show();
                        txtqtyubica.setText("");
                        txtexistenciaubica.setText("");
                        txtubicaantes.setText("");
                        txtubicaahora.setText("");
                        txtcodigoubica.setText("");
                        txtubicaahora.setText("");
                        cargarCodigosUbica();
                        verifscan();
                        txtcodigoubica.requestFocus();
                        return false;
                    } else {
                        Toast.makeText(ActivityUbicaUbica.this, "No se Ubico la cantidad " + txtcantidadubicaSCAN.getText().toString() + " del codigo " + lbcodigoubicion.getText() + " con ubicacion.", Toast.LENGTH_LONG).show();
                        txtubicaahora.setText("");
                        txtubicaahora.requestFocus();
                        return true;
                    }
                    //txtubicaahora.requestFocus();
                    //return true;
                }
                return false;
            }
        });
        txtcodigoubica.setBackgroundColor(Color.GREEN);
        cargarCodigosUbica();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_ubica_ubica, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void cargarCodigosUbica(){
        if (cmbfacturaubica.getCount() <= 0) return;
        if (cmbcajaubica.getCount() <= 0) return;
        try{
            ubicaciones.clear();
            if (cmbtipoubic.getSelectedItem().toString().equals("SIN UBICACION")){
                result = backEnd.execSQL("SELECT ITEMNMBR,SUM(QTYINVCD) AS QTYINVCD,MAX(ITEMDESC) AS ITEMDESC,(SELECT (QTYONHND - ATYALLOC) AS QTYONHND  FROM IV00102 WHERE ITEMNMBR = PL10200.ITEMNMBR AND LOCNCODE = '" + mParam2.toString() + "') AS QTYONHND,(SELECT BINNMBR AS BINNMBR  FROM IV00102 WHERE ITEMNMBR = PL10200.ITEMNMBR AND LOCNCODE = '" + mParam2.toString() + "') AS BINNMBR," +
                        "(SELECT COUNT(*) FROM (SELECT DISTINCT ITEMNMBR  FROM PL10200  WHERE  INVOICE = '" + cmbfacturaubica.getSelectedItem().toString() + "' AND CASENO = '" + cmbcajaubica.getSelectedItem().toString() + "' AND BINNMBR IN ('','AGRUPAR')) AS A) AS TOTALUBICAR," +
                        "(SELECT COUNT(*) FROM (SELECT DISTINCT ITEMNMBR  FROM PL10200  WHERE  INVOICE = '" + cmbfacturaubica.getSelectedItem().toString() + "' AND CASENO = '" + cmbcajaubica.getSelectedItem().toString() + "' AND QTYLNC <> 0) AS B) AS CONTADORUBICA," +
                        "(SELECT COUNT(*) FROM (SELECT DISTINCT ITEMNMBR  FROM PL10200  WHERE  INVOICE = '"+cmbfacturaubica.getSelectedItem().toString()+"' AND CASENO = '"+cmbcajaubica.getSelectedItem().toString()+"') AS A) AS TOTAlTOTAL"+
                        "  FROM PL10200   WHERE  INVOICE = '" + cmbfacturaubica.getSelectedItem().toString() + "' AND CASENO = '" + cmbcajaubica.getSelectedItem().toString() + "' AND POLNESTA IN(3,4) AND (BINNMBR IN ('','AGRUPAR')) GROUP BY ITEMNMBR");
            }else if (cmbtipoubic.getSelectedItem().toString().equals("UBICACION")){
                result = backEnd.execSQL("SELECT ITEMNMBR,SUM(QTYINVCD) AS QTYINVCD,MAX(ITEMDESC) AS ITEMDESC,(SELECT (QTYONHND - ATYALLOC) AS QTYONHND  FROM IV00102 WHERE ITEMNMBR = PL10200.ITEMNMBR AND LOCNCODE = '" + mParam2.toString() + "') AS QTYONHND,(SELECT BINNMBR AS BINNMBR  FROM IV00102 WHERE ITEMNMBR = PL10200.ITEMNMBR AND LOCNCODE = '" + mParam2.toString() + "') AS BINNMBR," +
                        "(SELECT COUNT(*) FROM (SELECT DISTINCT ITEMNMBR  FROM PL10200  WHERE  INVOICE = '" + cmbfacturaubica.getSelectedItem().toString() + "' AND CASENO = '" + cmbcajaubica.getSelectedItem().toString() + "' AND BINNMBR NOT IN ('','AGRUPAR')) AS A) AS TOTALUBICAR," +
                        "(SELECT COUNT(*) FROM (SELECT DISTINCT ITEMNMBR  FROM PL10200  WHERE  INVOICE = '" + cmbfacturaubica.getSelectedItem().toString() + "' AND CASENO = '" + cmbcajaubica.getSelectedItem().toString() + "' AND QTYLNC <> 0) AS B) AS CONTADORUBICA," +
                        "(SELECT COUNT(*) FROM (SELECT DISTINCT ITEMNMBR  FROM PL10200  WHERE  INVOICE = '"+cmbfacturaubica.getSelectedItem().toString()+"' AND CASENO = '"+cmbcajaubica.getSelectedItem().toString()+"') AS A) AS TOTAlTOTAL"+
                        "  FROM PL10200   WHERE  INVOICE = '" + cmbfacturaubica.getSelectedItem().toString() + "' AND CASENO = '" + cmbcajaubica.getSelectedItem().toString() + "'  AND POLNESTA IN(3,4) AND (BINNMBR NOT IN ('','AGRUPAR')) GROUP BY ITEMNMBR");
            }

            if (result == null) return;
            while(result.next()){
                Map<String, String> datanum = new HashMap<String,String>();
                datanum.put("A", result.getString("ITEMNMBR"));
                datanum.put("B", "0");
                //datanum.put("B", result.getString("QTYINVCD"));
                datanum.put("C", result.getString("ITEMDESC"));
                datanum.put("D", result.getString("QTYONHND"));
                datanum.put("E", result.getString("BINNMBR"));
                lbcontadorubicacion.setText(result.getString("CONTADORUBICA") + " (Ubicado general) ");
                lbtotalubicacion.setText(" " + result.getString("TOTALUBICAR") + " Pendiente de " + cmbtipoubic.getSelectedItem().toString() + " Total caja " + result.getString("TOTALTOTAL"));
                ubicaciones.add(datanum);
            }
            result.close();
            String[] from = {"A","B","C","D","E"};
            int[] views = {R.id.txtcodigo,R.id.txtcantidad,R.id.txtdescripcion,R.id.txtexistencia,R.id.txtubicacion};
            AD = new SimpleAdapter(this, ubicaciones, R.layout.listubic, from, views);
            lstubicaciones.setAdapter(AD);
        }catch (SQLException e){
            //Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void cargarFacturasCodigos(String idproveedor){
        try {
            result = backEnd.execSQL("SELECT INVOICE FROM PL10100 WHERE INSIDE = 0");
            if (result == null) return;
            List<String> proveedores = new ArrayList<String>();
            while(result.next()){
                proveedores.add( result.getString("INVOICE"));
            }
            result.close();
            cmbfacturaubica.setAdapter(new ArrayAdapter<String>(
                    this,android.R.layout.simple_spinner_dropdown_item,proveedores
            ));

        }catch (SQLException e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void cargarUbicacionesVacias(){
        try {
            result = backEnd.execSQL("SELECT MBN_BINNMBR FROM zMAESTRO_BIN WHERE LOCNCODE = '" + mParam2.toString() + "' AND MBN_BINNMBR NOT IN (SELECT BINNMBR FROM ANSA.dbo.IV00102 WHERE SUBSTRING(BINNMBR,0,2)  = '" + cmbpasillosubica.getSelectedItem().toString() + "' AND LOCNCODE ='" + mParam2.toString() + "') AND zMAESTRO_BIN.ACTIVE = 0 " +
                    " AND SUBSTRING(MBN_BINNMBR,0,2)  = '" + cmbpasillosubica.getSelectedItem().toString() + "' AND ACTIVE = 0 ORDER BY SUBSTRING(MBN_BINNMBR,0,4),SUBSTRING(MBN_BINNMBR,5,3)");
            if (result == null) return;
            List<String> ubicaciones = new ArrayList<String>();
            while(result.next()){
                ubicaciones.add(result.getString("MBN_BINNMBR"));
            }
            result.close();
            cmbubicacionesubica.setAdapter(new ArrayAdapter<String>(
                    this,android.R.layout.simple_spinner_dropdown_item,ubicaciones
            ));

        }catch (SQLException e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void cargarCajasCodigos(){
        try {
            result = backEnd.execSQL("SELECT DISTINCT CASENO FROM PL10200 WHERE INVOICE = '" + cmbfacturaubica.getSelectedItem().toString() + "' ORDER BY CASENO ASC");
            if (result == null) return;
            List<String> cajas = new ArrayList<String>();
            while(result.next()){
                cajas.add(result.getString("CASENO"));
            }
            result.close();
            cmbcajaubica.setAdapter(new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_dropdown_item, cajas
            ));
        }catch (SQLException e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void verifscan(){
        if (Integer.toString(lstubicaciones.getAdapter().getCount()).equals("0")){
            try {
                result = backEnd.execSQL("SELECT COUNT(*) AS TOTALUBICAR,(SELECT COUNT(*) FROM PL10200 WHERE QTYLNC <> 0 AND INVOICE = '" + cmbfacturaubica.getSelectedItem().toString() + "' AND CASENO = '" + cmbcajaubica.getSelectedItem().toString() + "') AS TUBICADO FROM PL10200 WHERE INVOICE = '" + cmbfacturaubica.getSelectedItem().toString() + "' AND CASENO = '" + cmbcajaubica.getSelectedItem().toString() + "'");
                if(result.next()){
                    if (result.getInt("TUBICADO") == result.getInt("TOTALUBICAR")) {enviarCorreo();}
                }
            }catch (SQLException e){
                Toast.makeText(ActivityUbicaUbica.this,e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private  void enviarCorreo(){
        String mensajecajas =   "" +
                "<table border='1' style='width:80%;'>" +
                    "<tr style='background-color: #444;background: #87CEEB;font-family: Helvetica,Arial;color:#fff;'>" +
                        "<th scope='col' colspan='11'>Caja ubicada " + cmbcajaubica.getSelectedItem().toString() + " de factura "+ cmbfacturaubica.getSelectedItem().toString().toUpperCase() + "</th>" +
                    "</tr>" +
                    "<tr style='background-color: #444;background: #87CEEB;font-family: Helvetica,Arial;color:#fff;'>" +
                        "<th scope='col'>Linea</th>" +
                        "<th scope='col'>Factura</th>" +
                        "<th scope='col'>Caja</th>" +
                        "<th scope='col'>Item</th>" +
                        "<th scope='col'>Piezas Factura</th>" +
                        "<th scope='col'>Piezas Recibio</th>" +
                        "<th scope='col'>Piezas Ubico</th>" +
                        "<th scope='col'>HoraPaquete</th>" +
                        "<th scope='col'>HoraDesempaco</th>" +
                        "<th scope='col'>HoraUbico</th>" +
                        "<th scope='col'>Ubicador</th>" +
                    "</tr>" +
                "<tbody>";
        try{

            result = backEnd.execSQL("SELECT ROW_NUMBER() OVER(ORDER BY ITEMNMBR) AS ROW,MAX(INVOICE) AS INVOICE,MAX(CASENO) AS CASENO,ITEMNMBR,SUM(QTYINVCD) AS QTYINVCD,MAX(QTYREC) AS QTYREC," +
                    "MAX(QTYLNC) AS QTYLNC,MAX(PRSNLNC) AS PRSNLNC," +
                    "CAST(MAX(TIMERECS) AS TIME(0)) AS TIMERECS,CAST(MAX(TIMEUNPK) AS TIME(0)) AS TIMEUNPK,CAST(MAX(TIMELNC) AS TIME(0)) AS TIMELNC,PRSNLNC" +
                    " FROM PL10200 WHERE INVOICE = '" + cmbfacturaubica.getSelectedItem().toString().toUpperCase() + "' AND CASENO = '" + cmbcajaubica.getSelectedItem().toString() + "' GROUP BY ITEMNMBR,PRSNLNC");

            //if(result.next()) {
                while (result.next()) {
                    mensajecajas += "" +
                            "<tr>" +
                            "<th scope='col'>" + result.getString("ROW") + "</th>" +
                            "<th scope='col'>" + result.getString("INVOICE") + "</th>" +
                            "<th scope='col'> " + result.getString("CASENO") + "</th>" +
                            "<th scope='col'>" + result.getString("ITEMNMBR") + "</th>" +
                            "<th scope='col'>" + result.getString("QTYINVCD") + "</th>" +
                            "<th scope='col'>" + result.getString("QTYREC") + "</th>" +
                            "<th scope='col'>" + result.getString("QTYLNC") + "</th>" +
                            "<th scope='col'>" + result.getString("TIMERECS") + "</th>" +
                            "<th scope='col'>" + result.getString("TIMEUNPK") + "</th>" +
                            "<th scope='col'>" + result.getString("TIMELNC") + "</th>" +
                            "<th scope='col'>" + result.getString("PRSNLNC") + "</th>" +
                            "</tr>";
                }
            //}
        }catch (SQLException e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        mensajecajas += "</tbody></table>";

       backEnd.sendEmail("servermbs@autonica.com","sergio_sandino@autonica.com,marcos.incer@autonica.com,jonathan.sevilla@autonica.com,bermer.gonzalez@autonica.com,juan_velasquez@autonica.com,necdaly.villareal@autonica.com",mensajecajas,"Caja: " + cmbcajaubica.getSelectedItem().toString() + " / " + cmbfacturaubica.getSelectedItem().toString() + " piezas " + cmbtipoubic.getSelectedItem().toString() + " ya se ubico!","");
       //backEnd.sendEmail("servermbs@autonica.com","mauricio.palacios@autonica.com",mensajecajas,"Caja: " + cmbcajaubica.getSelectedItem().toString() + " / " + cmbfacturaubica.getSelectedItem().toString() + " ya se ubico!");
    }

    private String obtenercodigodeubicacion(String ubicacion){
        String codigoinfo = "";
        try {
            result = backEnd.execSQL("SELECT UPPER(LTRIM(RTRIM(ITEMNMBR))) AS ITEMNMBR FROM IV00102 WHERE RTRIM(BINNMBR) = '" + ubicacion.trim() + "' AND RTRIM(LOCNCODE) = '" + mParam2.toString().trim() + "'");
            if(result.next()){
                codigoinfo = result.getString("ITEMNMBR");
            }
        }catch (SQLException e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            codigoinfo = e.toString();
        }
        return codigoinfo;
    }

    private void cargarCodigoFactura(){
        try {
            result = backEnd.execSQL("SELECT ITEMNMBR,SUM(QTYINVCD) AS QTYINVCD,MAX(ITEMDESC) AS ITEMDESC,ISNULL((SELECT (QTYONHND - ATYALLOC) AS QTYONHND  FROM IV00102 WHERE ITEMNMBR = PL10200.ITEMNMBR AND LOCNCODE = '" + mParam2.toString() + "'),0) AS QTYONHND,MAX(BINNMBR) AS BINNMBR,ISNULL((SELECT BINNMBR  FROM IV00102 WHERE ITEMNMBR = PL10200.ITEMNMBR AND LOCNCODE = '" + mParam2.toString() + "'),'') AS UBICACIONREAL,MAX(QTYLNC) AS QTYLNC  FROM PL10200  WHERE  INVOICE = '" + cmbfacturaubica.getSelectedItem().toString() + "' AND CASENO = '" + cmbcajaubica.getSelectedItem().toString() + "' AND ITEMNMBR = '" + txtcodigoubica.getText().toString().toUpperCase().trim() + "' GROUP BY ITEMNMBR");
            if(result.next()) {
                txtqtyubica.setText(result.getString("QTYINVCD"));
                txtexistenciaubica.setText(result.getString("QTYONHND"));
                txtubicaantes.setText(result.getString("BINNMBR"));
                txtcantidadubicaSCAN.setText(result.getString("QTYLNC"));

                if (!result.getString("BINNMBR").trim().toUpperCase().equals(result.getString("UBICACIONREAL").trim().toUpperCase())){
                    Toast.makeText(ActivityUbicaUbica.this, "El item " + txtcodigoubica.getText().toString().trim() + " tuvo un cambio de ubicacion!.Ubicacion real " + result.getString("UBICACIONREAL").trim(), Toast.LENGTH_LONG).show();
                    r = backEnd.execSQLUpdate("UPDATE PL10200 SET BINNMBR = '"+result.getString("UBICACIONREAL").trim().toUpperCase()+"' WHERE  INVOICE = '" + cmbfacturaubica.getSelectedItem().toString() + "' AND ITEMNMBR = '" + txtcodigoubica.getText().toString().toUpperCase().trim() + "' AND QTYLNC = 0");
                    if (r >= 1) {
                        txtqtyubica.setText("");
                        txtexistenciaubica.setText("");
                        txtubicaantes.setText("");
                        txtubicaahora.setText("");
                        txtcodigoubica.setText("");
                        Toast.makeText(ActivityUbicaUbica.this, "Cargar nuevamente el item " + txtcodigoubica.getText().toString().trim(), Toast.LENGTH_LONG).show();
                        txtcodigoubica.setText("");
                        txtcantidadubicaSCAN.setText("");
                        txtcodigoubica.requestFocus();
                    }
                }
            }
        } catch (SQLException e) {
            Toast.makeText(ActivityUbicaUbica.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
