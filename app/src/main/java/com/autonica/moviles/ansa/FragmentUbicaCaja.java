package com.autonica.moviles.ansa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.*;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.sql.*;
import java.util.*;


public class FragmentUbicaCaja extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    // TODO: MIS VARIABLES
    private ListView lstcajas;
    private ResultSet result;
    private Integer r;
    private EditText txtcodigocaja;
    private Spinner cmbproveedorcaja,cmbfacturacaja;
    private TextView lbcontadorcaja,lbtotalcontar;
    BackEnd backEnd = new BackEnd(getActivity());
    private List<Map<String, String>> cajas = new ArrayList<Map<String,String>>();
    private SimpleAdapter AD;

    // TODO: Rename and change types and number of parameters
    public static FragmentUbicaCaja newInstance(String param1, String param2) {
        FragmentUbicaCaja fragment = new FragmentUbicaCaja();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentUbicaCaja() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_ubica_caja, container, false);
        cmbfacturacaja = (Spinner) view.findViewById(R.id.cmbfacturacaja);
        cmbproveedorcaja = (Spinner) view.findViewById(R.id.cmbproveedorcaja);
        txtcodigocaja = (EditText) view.findViewById(R.id.txtcodigocaja);
        lbcontadorcaja = (TextView) view.findViewById(R.id.lbcontadorcaja);
        lbtotalcontar = (TextView) view.findViewById(R.id.lbtotalcaja);
        Button btncajas = (Button) view.findViewById(R.id.btncajas);
        lstcajas = (ListView) view.findViewById(R.id.lstcajas);
        cargarProveedores();
        cargarFacturasCaja("PV-EXT-0001");
        btncajas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarCajasFactura();
            }
        });
       txtcodigocaja.setOnFocusChangeListener(new View.OnFocusChangeListener() {
           @Override
           public void onFocusChange(View v, boolean hasFocus) {
               if (hasFocus) {
                   v.setBackgroundColor(Color.GREEN);
               } else {
                   v.setBackgroundColor(Color.RED);
               }
           }
       });
        txtcodigocaja.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (txtcodigocaja.getText().toString().equals("")) return true;
                    if(txtcodigocaja.getText().toString().equals("RECIBIR")){
                        r = backEnd.execSQLUpdate("UPDATE PL10200 SET TIMERECS = GETDATE(),CASERCV = 1,PRSNRECS = '" + mParam1.toString().toUpperCase().trim() + "' WHERE INVOICE = '" + cmbfacturacaja.getSelectedItem().toString() + "' AND CASERCV <> 1");
                        if (r > 0){enviarCorreo();cargarCajasFactura();}
                    }else{
                        txtcodigocaja.setText(formatCaja(txtcodigocaja.getText().toString().replace("'","-"),cmbproveedorcaja.getSelectedItem().toString()));
                        r = backEnd.execSQLUpdate("UPDATE PL10200 SET TIMERECS = GETDATE(),CASERCV = 1,PRSNRECS = '" + mParam1.toString().toUpperCase().trim() + "' WHERE CASENO = '" + txtcodigocaja.getText().toString() + "' AND INVOICE = '" + cmbfacturacaja.getSelectedItem().toString() + "' AND CASERCV <> 1");
                        if (r != 0) {
                            cajas.clear();
                            cargarCajasFactura();
                            Toast.makeText(getActivity(), "Caja: " + txtcodigocaja.getText().toString() + " confirmada", Toast.LENGTH_LONG).show();
                            if (lbcontadorcaja.getText().toString().trim().equals(lbtotalcontar.getText().toString().trim())) {
                                enviarCorreo();
                            }
                        } else if (r == 0){
                            Toast.makeText(getActivity(), "Caja: " + txtcodigocaja.getText().toString() + " no confirmada o ya se recibio", Toast.LENGTH_LONG).show();
                            final String caja = txtcodigocaja.getText().toString();
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                            builder1.setMessage("Ir A: " + caja);
                            builder1.setCancelable(true);
                            builder1.setPositiveButton("Desempacar Caja", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent act = new Intent(getActivity(), ActivityUbicaCodigo.class);
                                    act.putExtra("caja", caja);
                                    act.putExtra("factura", cmbfacturacaja.getSelectedItem().toString());
                                    act.putExtra("userlog", mParam1.toString());
                                    act.putExtra("location", mParam2.toString());
                                    act.putExtra("proveedor", cmbproveedorcaja.getSelectedItem().toString());
                                    startActivity(act);
                                    dialog.cancel();
                                }
                            });
                            builder1.setNegativeButton("Ubicar Codigos", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent act = new Intent(getActivity(), ActivityUbicaUbica.class);
                                    act.putExtra("caja", caja);
                                    act.putExtra("factura", cmbfacturacaja.getSelectedItem().toString());
                                    act.putExtra("userlog", mParam1.toString().toUpperCase());
                                    act.putExtra("location", mParam2.toString());
                                    act.putExtra("proveedor", cmbproveedorcaja.getSelectedItem().toString());
                                    startActivity(act);
                                    dialog.cancel();
                                }
                            });
                            builder1.create().show();
                        }
                    }
                    txtcodigocaja.setText("");
                    txtcodigocaja.requestFocus();
                    return true;
                }
                return false;
            }
        });

        lstcajas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String caja = cajas.get(position).get("A").toString();
                String estado = cajas.get(position).get("C").toString();
                if (estado.trim().equals("RECIBIDA")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setMessage("Ir A: " + caja);
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("Desempacar Caja", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent act = new Intent(getActivity(), ActivityUbicaCodigo.class);
                            act.putExtra("caja", caja);
                            act.putExtra("factura", cmbfacturacaja.getSelectedItem().toString());
                            act.putExtra("userlog", mParam1.toString());
                            act.putExtra("location", mParam2.toString());
                            act.putExtra("proveedor", cmbproveedorcaja.getSelectedItem().toString());
                            startActivity(act);
                            dialog.cancel();
                        }
                    });
                    builder1.setNegativeButton("Ubicar Codigos", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent act = new Intent(getActivity(), ActivityUbicaUbica.class);
                            act.putExtra("caja", caja);
                            act.putExtra("factura", cmbfacturacaja.getSelectedItem().toString());
                            act.putExtra("userlog", mParam1.toString().toUpperCase());
                            act.putExtra("location", mParam2.toString());
                            act.putExtra("proveedor", cmbproveedorcaja.getSelectedItem().toString());
                            startActivity(act);
                            dialog.cancel();
                        }
                    });
                    builder1.create().show();
                }
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void cargarCajasFactura(){
        try{
            cajas.clear();
            result = backEnd.execSQL("SELECT DISTINCT CASENO,CASE WHEN CASERCV = 0 THEN 'NO RECIBIDA'  WHEN CASERCV = 1 THEN 'RECIBIDA'  END AS ESTADO,/*(SELECT COUNT (*) FROM (SELECT DISTINCT ITEMNMBR FROM PL10200 WHERE CASENO = PLF.CASENO) AS A)*/ 0 AS CANTIDAD," +
                    "(SELECT COUNT(*) FROM (SELECT DISTINCT CASENO FROM PL10200 AS PL1020 WHERE INVOICE = '" + cmbfacturacaja.getSelectedItem().toString() + "' AND CASERCV = 1) AS R) AS RECIBIDAS," +
                    "(SELECT COUNT(*) FROM (SELECT DISTINCT CASENO FROM PL10200 AS PL1020 WHERE INVOICE = '" + cmbfacturacaja.getSelectedItem().toString() + "') AS R) AS TOTALRECIBIR " +
                    "FROM PL10200 AS PLF WHERE INVOICE = '" + cmbfacturacaja.getSelectedItem().toString() + "'  ORDER BY ESTADO ASC");
            if (result == null)  return;
            while(result.next()){
                Map<String, String> datanum = new HashMap<String, String>();
                datanum.put("A", result.getString("CASENO"));
                datanum.put("C", result.getString("ESTADO"));
                datanum.put("B", result.getString("CANTIDAD"));
                cajas.add(datanum);
                lbtotalcontar.setText(" " + result.getString("TOTALRECIBIR"));
                lbcontadorcaja.setText(result.getString("RECIBIDAS") + " ");
            }
            String[] from = {"A","B","C"};
            int[] views = {R.id.txtcodigo,R.id.txtcantidad,R.id.txtestado};
            AD = new SimpleAdapter(getActivity(), cajas, R.layout.listcase, from, views);
            lstcajas.setAdapter(AD);
        }catch (SQLException e){
            Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    private String formatCaja(String caja,String proveedor){
        String fCaja = caja;
        switch (proveedor) {
            case "TOYOTA":  fCaja = caja.substring(0, caja.length() - 1);
                break;
            case "NINGUNO":  fCaja = caja;
                break;
        }
        return fCaja;
    }

    private void cargarFacturasCaja(String idproveedor){
        try {
            result = backEnd.execSQL("SELECT INVOICE FROM PL10100 WHERE INSIDE = 0");
            if (result == null) return;
            List<String> proveedores = new ArrayList<String>();
            while(result.next()){
                proveedores.add( result.getString("INVOICE"));
            }
            result.close();
            cmbfacturacaja.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,proveedores));
        }catch (SQLException e){
            Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    private void cargarProveedores(){
        String[] bodegas = {"NINGUNO", "TOYOTA"};
        cmbproveedorcaja.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, bodegas));
    }

    private  void enviarCorreo(){
        String table = "";
        table += "<table border='1' style='width:30%;'>" +
                 "<tr style='background-color: #444;background: #87CEEB;font-family: Helvetica,Arial;color:#fff;'>" +
                 "<th scope='col' colspan='2'>Marcador de cajas</th>" +
                 "</tr>" +
                 "<tbody>" ;
        try{
            result = backEnd.execSQL("SELECT COUNT(DISTINCT CASENO) AS TOTAL,(SELECT COUNT(DISTINCT CASENO) FROM PL10200 WHERE INVOICE = '" + cmbfacturacaja.getSelectedItem().toString().toUpperCase() + "' AND CASERCV = 1) AS CONTADO," +
                                     "(SELECT COUNT(DISTINCT CASENO) FROM PL10200 WHERE INVOICE = '" + cmbfacturacaja.getSelectedItem().toString().toUpperCase() + "' AND CASERCV = 0) AS CONTADOESPERA," +
                                     "(SELECT DATEDIFF(MINUTE,MIN(TIMERECS),MAX(TIMERECS)) FROM PL10200 WHERE INVOICE = '" + cmbfacturacaja.getSelectedItem().toString().toUpperCase() + "') AS DURACION " +
                                     "FROM PL10200 WHERE INVOICE = '" + cmbfacturacaja.getSelectedItem().toString().toUpperCase() + "'");
            result.next();
            table += "<tr style='background-color: #444;background: #87CEEB;font-family: Helvetica,Arial;color:#fff;'>" +
                     "<th scope='col'>Cajas encontradas</th>" +
                     "<th scope='col'>" +result.getString("CONTADO")+ "</th>" +
                     "</tr>" +
                     "<tr style='background-color: #444;background: #87CEEB;font-family: Helvetica,Arial;color:#fff;'>" +
                     "<th scope='col'>Cajas no encontradas</th>" +
                     "<th scope='col'>" +result.getString("CONTADOESPERA")+ "</th>" +
                     "</tr>" +
                     "</tr>" +
                     "<tr style='background-color: #444;background: #87CEEB;font-family: Helvetica,Arial;color:#fff;'>" +
                     "<th scope='col'>Duracion</th>" +
                     "<th scope='col'>" +result.getString("DURACION")+ " Min(s)</th>" +
                     "</tr>" +
                     "<tr style='background-color: #444;background: #87CEEB;font-family: Helvetica,Arial;color:#fff;'>" +
                     "<th scope='col'>Total cajas en factura</th>" +
                     "<th scope='col'>" +result.getString("TOTAL")+ "</th>" +
                     "</tr>";
        }catch (SQLException e){
            Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_LONG).show();
        }

        table += "</tbody>" +
                 "</table><br><br>";


        table += "<table border='1' style='width:80%;'>" +
                 "<tr style='background-color: #444;background: #87CEEB;font-family: Helvetica,Arial;color:#fff;'>" +
                 "<th scope='col' colspan='6'>Cajas contadas de factura " + cmbfacturacaja.getSelectedItem().toString().toUpperCase() + "</th>" +
                 "</tr>" +
                 "<tr style='background-color: #444;background: #87CEEB;font-family: Helvetica,Arial;color:#fff;'>" +
                 "<th scope='col'>Linea</th>" +
                 "<th scope='col'>Caja</th>" +
                 "<th scope='col'>Piezas</th>" +
                 "<th scope='col'>FOBTT</th>" +
                 "<th scope='col'>Estado</th>" +
                 "<th scope='col'>Desempacador</th>" +
                 "</tr>" +
                 "<tbody>";
        try{
            result = backEnd.execSQL("SELECT  ROW_NUMBER() OVER(ORDER BY CASENO) AS ROW,CASENO,SUM(QTYINVCD) AS QTYINVCD,SUM(EXTDFOB) AS EXTDFOB,CASE WHEN MAX(CASERCV) = 0 THEN 'NO RECIBIDA'  WHEN MAX(CASERCV) = 1 THEN 'RECIBIDA'  END AS ESTADO,MAX(PRSNRECS) AS PRSNRECS  FROM PL10200 WHERE INVOICE = '" + cmbfacturacaja.getSelectedItem().toString() + "' GROUP BY CASENO");
            while(result.next()){
                table += "<tr>" +
                         "<th scope='col'>" +result.getString("ROW")+ "</th>" +
                         "<th scope='col'>" +result.getString("CASENO") + "</th>" +
                         "<th scope='col'> " + result.getString("QTYINVCD")+ "</th>" +
                         "<th scope='col'>" + result.getString("EXTDFOB")+ "</th>" +
                         "<th scope='col'>" + result.getString("ESTADO")+ "</th>" +
                         "<th scope='col'>" + result.getString("PRSNRECS")+ "</th>" +
                         "</tr>";
            }
        }catch (SQLException e){
            Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_LONG).show();
        }

        table += "</tbody></table>";
        //backEnd.sendEmail("servermbs@autonica.com","mauricio.palacios@autonica.com",table,"Los paquetes ya fueron contados en factura " + cmbfacturacaja.getSelectedItem().toString().toUpperCase());
        backEnd.sendEmail("servermbs@autonica.com","sergio_sandino@autonica.com,marcos.incer@autonica.com,jonathan.sevilla@autonica.com,bermer.gonzalez@autonica.com,juan_velasquez@autonica.com,necdaly.villareal@autonica.com",table,"Los paquetes ya fueron contados en factura " + cmbfacturacaja.getSelectedItem().toString().toUpperCase(),"");
    }

}

