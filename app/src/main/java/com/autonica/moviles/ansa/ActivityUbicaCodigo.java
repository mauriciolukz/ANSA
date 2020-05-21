package com.autonica.moviles.ansa;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import java.sql.*;
import java.util.*;

public class ActivityUbicaCodigo extends AppCompatActivity {
    private EditText txtcodigocodigo,txtqtycodigo,txtcantidadcodigo,txtdesccodigo,txtubiccodigo;
    private TextView lbcontadorcodigo,lbtotalcodigo;
    private Spinner cmbfacturacodigo,cmbcajacodigo;
    private ListView lstcodigos;
    private ResultSet result;
    private Integer r = 0;
    BackEnd backEnd = new BackEnd(ActivityUbicaCodigo.this);
    Rutinas rutinas = new Rutinas();
    private List<Map<String,String>> codigos = new ArrayList<Map<String,String>>();
    private SimpleAdapter AD;
    private String mParam1,mParam2,proveedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubica_codigo);

        cmbfacturacodigo = (Spinner) findViewById(R.id.cmbfacturacodigo);
        txtcodigocodigo = (EditText) findViewById(R.id.txtcodigocodigo);
        txtdesccodigo = (EditText) findViewById(R.id.txtdesccodigo);
        txtubiccodigo = (EditText) findViewById(R.id.txtubiccodigo);
        cmbcajacodigo = (Spinner) findViewById(R.id.cmbcajacodigo);
        txtqtycodigo = (EditText) findViewById(R.id.txtqtycodigo);
        txtcantidadcodigo = (EditText) findViewById(R.id.txtcantidadcodigo);
        lstcodigos = (ListView) findViewById(R.id.lstcodigos);
        mParam1 = getIntent().getExtras().getString("userlog");
        mParam2 = getIntent().getExtras().getString("location");
        proveedor = getIntent().getExtras().getString("proveedor");
        lbcontadorcodigo = (TextView) findViewById(R.id.lbcontadorcodigo);
        lbtotalcodigo =(TextView) findViewById(R.id.lbtotalcodigo);
        cargarFacturasCodigos("PV-EXT-0001");

        ArrayAdapter myAdapinvoice = (ArrayAdapter) cmbfacturacodigo.getAdapter();
        if (myAdapinvoice != null) {
            int spinnerPositioninvoice = myAdapinvoice.getPosition(getIntent().getExtras().getString("factura"));
            cmbfacturacodigo.setSelection(spinnerPositioninvoice);
            cargarCajasCodigos();
            ArrayAdapter myAdapcase = (ArrayAdapter) cmbcajacodigo.getAdapter();
            if (myAdapcase != null) {
            int spinnerPositioncase = myAdapcase.getPosition(getIntent().getExtras().getString("caja"));
            cmbcajacodigo.setSelection(spinnerPositioncase);
            }
        }

        txtcodigocodigo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundColor(Color.GREEN);
                } else {
                    v.setBackgroundColor(Color.RED);
                }
            }
        });

       cmbcajacodigo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               cargarCodigosFactura();
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });
        txtcodigocodigo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (cmbfacturacodigo.getCount() <= 0) return true;
                    if (cmbcajacodigo.getCount() <= 0) return true;
                    if (txtcodigocodigo.getText().toString().trim().equals("")) return true;
                    if (proveedor.toString().equals("NINGUNO")){
                        txtcodigocodigo.setText(txtcodigocodigo.getText().toString().trim().replace("'", "-"));
                    }else{
                        txtcodigocodigo.setText(rutinas.ITEMNMBRFormat(txtcodigocodigo.getText().toString().trim().replace("'", "-")));
                    }
                    //txtcodigocodigo.setText(rutinas.ITEMNMBRFormat(txtcodigocodigo.getText().toString().trim().replace("'", "-")));
                    cargarCodigoFactura();
                    if (Integer.parseInt(txtqtycodigo.getText().toString()) != 0) {
                        r = backEnd.execSQLUpdate("UPDATE PL10200 SET POLNESTA = 3,QTYREC = " + (Integer.parseInt(txtcantidadcodigo.getText().toString()) + 1) + " WHERE CASENO = '" + cmbcajacodigo.getSelectedItem().toString() + "' AND INVOICE = '" + cmbfacturacodigo.getSelectedItem().toString() + "' AND UPPER(ITEMNMBR) = '" + txtcodigocodigo.getText().toString().trim().toUpperCase() + "' AND NOT POLNESTA IN (/*4,*/5) AND CASERCV = 1");
                        if (r != 0) {
                            cargarCodigoFactura();
                            if (txtqtycodigo.getText().toString().equals(txtcantidadcodigo.getText().toString()) || Integer.parseInt(txtqtycodigo.getText().toString()) <  Integer.parseInt(txtcantidadcodigo.getText().toString())) {
                                r = backEnd.execSQLUpdate("UPDATE PL10200 SET POLNESTA = 4, PRSNUNPK = '" + mParam1.toString().toUpperCase() + "',TIMEUNPK = GETDATE() WHERE CASENO = '" + cmbcajacodigo.getSelectedItem().toString() + "' AND INVOICE = '" + cmbfacturacodigo.getSelectedItem().toString() + "' AND UPPER(ITEMNMBR) = '" + txtcodigocodigo.getText().toString().toUpperCase().trim() + "' AND NOT POLNESTA IN (/*4,*/5)");
                                //Toast.makeText(ActivityUbicaCodigo.this, "Se Recepciono la cantidad " + txtcantidadcodigo.getText().toString() + " del codigo " + txtcodigocodigo.getText().toString() + " .", Toast.LENGTH_LONG).show();
                                //txtqtycodigo.setText("0");
                                //txtcantidadcodigo.setText("0");
                                //txtdesccodigo.setText("");
                                //txtubiccodigo.setText("");
                                cargarCodigosFactura();
                                if (AD.isEmpty()) {
                                    try {
                                        result = backEnd.execSQL("SELECT COUNT(*) AS TOTAL,(SELECT COUNT(*) FROM PL10200 WHERE CASENO = '" + cmbcajacodigo.getSelectedItem().toString() + "' AND INVOICE = '" + cmbfacturacodigo.getSelectedItem().toString() + "' AND QTYREC <> 0) AS RECIBIDO FROM PL10200 WHERE CASENO = '" + cmbcajacodigo.getSelectedItem().toString() + "' AND INVOICE = '" + cmbfacturacodigo.getSelectedItem().toString() + "'");
                                        result.next();
                                        lbcontadorcodigo.setText(result.getString("RECIBIDO") + " ");
                                        lbtotalcodigo.setText(" " + result.getString("TOTAL"));
                                        if (lbcontadorcodigo.getText().toString().trim().equals(lbtotalcodigo.getText().toString().trim())) {
                                            enviarCorreo();
                                        }
                                    }catch(SQLException e){

                                    }
                                }
                            }
                        }
                    }
                    txtcodigocodigo.setText("");
                    txtcodigocodigo.requestFocus();
                    return true;
                }
                return false;
            }
        });
        txtcodigocodigo.setBackgroundColor(Color.GREEN);
        txtcodigocodigo.requestFocus();
    }

    private void cargarCodigosFactura(){
        if (cmbfacturacodigo.getCount() <= 0) return;
        if (cmbcajacodigo.getCount() <= 0) return;
        try{
            codigos.clear();
            result = backEnd.execSQL("SELECT ITEMNMBR,SUM(QTYINVCD) AS QTYINVCD,MAX(ITEMDESC) AS ITEMDESC, " +
            "(SELECT COUNT(*) FROM (SELECT DISTINCT ITEMNMBR FROM PL10200 WHERE INVOICE = '" + cmbfacturacodigo.getSelectedItem().toString() + "' AND CASENO = '" + cmbcajacodigo.getSelectedItem().toString() + "' AND POLNESTA IN (4,5,6)) AS A) AS CODCONTADOS," +
            "(SELECT COUNT(*) FROM (SELECT DISTINCT ITEMNMBR FROM PL10200 WHERE INVOICE = '" + cmbfacturacodigo.getSelectedItem().toString() + "' AND CASENO = '" + cmbcajacodigo.getSelectedItem().toString() + "') AS C) AS CODTOTAL " +
            "FROM PL10200  WHERE INVOICE = '" + cmbfacturacodigo.getSelectedItem().toString() + "' AND CASENO = '" + cmbcajacodigo.getSelectedItem().toString() + "' AND POLNESTA NOT IN (4,5,6) GROUP BY ITEMNMBR");
            if (result == null) return;
            while(result.next()){
                Map<String, String> datanum = new HashMap<String,String>();
                datanum.put("A", result.getString("ITEMNMBR"));
                //datanum.put("B", result.getString("QTYINVCD"));
                datanum.put("B", "0");
                datanum.put("C", result.getString("ITEMDESC"));
                lbcontadorcodigo.setText(result.getString("CODCONTADOS") + " ");
                lbtotalcodigo.setText(" " + result.getString("CODTOTAL"));
                codigos.add(datanum);
            }
            result.close();
            String[] from = {"A","B","C"};
            int[] views = {R.id.txtcodigo,R.id.txtcantidad,R.id.txtdescripcion};
            AD = new SimpleAdapter(this, codigos, R.layout.listcode, from, views);
            lstcodigos.setAdapter(AD);
        }catch (SQLException e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void cargarFacturasCodigos(String idproveedor){
        try {
            result = backEnd.execSQL("SELECT INVOICE FROM PL10100 WHERE INSIDE = 0");
            if (result == null) return;
            List<String> proveedores = new ArrayList<String>();
            while(result.next()){proveedores.add(result.getString("INVOICE"));}
            result.close();
            cmbfacturacodigo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, proveedores));
        }catch (SQLException e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void cargarCajasCodigos(){
        try {
            result = backEnd.execSQL("SELECT DISTINCT CASENO FROM PL10200 WHERE INVOICE = '" + cmbfacturacodigo.getSelectedItem().toString() + "' ORDER BY CASENO ASC");
            if (result == null) return;
            List<String> cajas = new ArrayList<String>();
            while(result.next()){cajas.add(result.getString("CASENO"));}
            result.close();
            cmbcajacodigo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, cajas));
        }catch (SQLException e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void cargarCodigoFactura(){
        if (cmbfacturacodigo.getCount() <= 0) return;
        if (cmbcajacodigo.getCount() <= 0) return;
        try {
            result = backEnd.execSQL("SELECT ITEMNMBR,(SELECT BINNMBR FROM IV00102 WHERE LOCNCODE = '" + mParam2.toString() + "'  AND ITEMNMBR = '" + txtcodigocodigo.getText().toString().trim() + "') AS BINNMBR,SUM(QTYINVCD) AS QTYINVCD,MAX(QTYREC) AS QTYREC,MAX(ITEMDESC) AS ITEMDESC FROM PL10200  WHERE INVOICE = '" + cmbfacturacodigo.getSelectedItem().toString() + "' AND CASENO = '" + cmbcajacodigo.getSelectedItem().toString() + "'  AND ITEMNMBR = '" + txtcodigocodigo.getText().toString().trim() + "' AND POLNESTA NOT IN (/*4,*/5,6) GROUP BY ITEMNMBR");
            if (result == null) return;
            result.next();
            txtqtycodigo.setText(result.getString("QTYINVCD"));
            txtcantidadcodigo.setText(result.getString("QTYREC"));
            txtdesccodigo.setText(result.getString("ITEMDESC"));
            txtubiccodigo.setText(result.getString("BINNMBR"));
            result.close();
        } catch (SQLException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private  void enviarCorreo(){
        String duracion = "";
        String table =  "<table border='1' style='width:80%;'>" +
                        "<tr style='background-color: #444;background: #87CEEB;font-family: Helvetica,Arial;color:#fff;'>" +
                        "<th scope='col' colspan='9'>Caja Desempacada " + cmbcajacodigo.getSelectedItem().toString() + " de factura " + cmbfacturacodigo.getSelectedItem().toString().toUpperCase() + "</th>" +
                        "</tr>" +
                        "<tr style='background-color: #444;background: #87CEEB;font-family: Helvetica,Arial;color:#fff;'>" +
                        "<th scope='col'>Linea</th>" +
                        "<th scope='col'>Factura</th>" +
                        "<th scope='col'>Caja</th>" +
                        "<th scope='col'>Item</th>" +
                        "<th scope='col'>Piezas Factura</th>" +
                        "<th scope='col'>Piezas Recibio</th>" +
                        "<th scope='col'>Desempacador</th>" +
                        "<th scope='col'>HoraPaquete</th>" +
                        "<th scope='col'>HoraDesempaco</th>" +
                        "</tr>" +
                        "<tbody>";
        try{
                result = backEnd.execSQL("SELECT ROW_NUMBER() OVER(ORDER BY ITEMNMBR) AS ROW,MAX(INVOICE) AS INVOICE,MAX(CASENO) AS CASENO,ITEMNMBR,SUM(QTYINVCD) AS QTYINVCD,MAX(QTYREC) AS QTYREC," +
                                         "MAX(PRSNUNPK) AS PRSNUNPK,CAST(MAX(TIMERECS) AS TIME(0)) AS TIMERECS,CAST(MAX(TIMEUNPK) AS TIME(0)) AS TIMEUNPK," +
                                         "(SELECT DATEDIFF(MINUTE,MIN(TIMEUNPK),MAX(TIMEUNPK)) FROM PL10200 WHERE INVOICE = '"+cmbfacturacodigo.getSelectedItem().toString().toUpperCase()+"' AND CASENO = '"+cmbcajacodigo.getSelectedItem().toString()+"') AS DURACION " +
                                         "FROM PL10200 WHERE INVOICE = '" + cmbfacturacodigo.getSelectedItem().toString().toUpperCase() + "' AND CASENO = '" + cmbcajacodigo.getSelectedItem().toString() + "' GROUP BY ITEMNMBR,CASENO");

            while(result.next()){
                table +=    "<tr>" +
                            "<th scope='col'>" + result.getString("ROW")     + "</th>" +
                            "<th scope='col'>" + result.getString("INVOICE") + "</th>" +
                            "<th scope='col'> "+ result.getString("CASENO")  + "</th>" +
                            "<th scope='col'>" + result.getString("ITEMNMBR")+ "</th>" +
                            "<th scope='col'>" + result.getString("QTYINVCD")+ "</th>" +
                            "<th scope='col'>" + result.getString("QTYREC")  + "</th>" +
                            "<th scope='col'>" + result.getString("PRSNUNPK")+ "</th>" +
                            "<th scope='col'>" + result.getString("TIMERECS")+ "</th>" +
                            "<th scope='col'>" + result.getString("TIMEUNPK")+ "</th>" +
                            "</tr>";
                duracion = result.getString("DURACION");
            }
            table += "<tr><th colspan='8'>Total duracion</th><th>"+ duracion +"</th></tr></tbody></table>";
        }catch (SQLException e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        backEnd.sendEmail("servermbs@autonica.com","sergio_sandino@autonica.com,marcos.incer@autonica.com,jonathan.sevilla@autonica.com,bermer.gonzalez@autonica.com,juan_velasquez@autonica.com,necdaly.villareal@autonica.com",table,"Caja: " + cmbcajacodigo.getSelectedItem().toString() + " / " + cmbfacturacodigo.getSelectedItem().toString() + " ya se desempaco!","");
        //backEnd.sendEmail("servermbs@autonica.com","mauricio.palacios@autonica.com",table,"Caja: " + cmbcajacodigo.getSelectedItem().toString() + " / " + cmbfacturacodigo.getSelectedItem().toString() + " ya se desempaco!");
    }
}
