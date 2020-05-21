package com.autonica.moviles.ansa;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
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
import java.util.ArrayList;
import java.util.List;


public class FragmentCambioUbicacion extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    // TODO: MIS VARIABLES
    private EditText txtcodigocambio,txtubicviejacambio,txtdimenviejacambio,txtcodigo2cambio,txtubicanuevacambio,txtexistenciacambio;
    private Spinner cmbsugerido,cmbpasillosubica;
    private ResultSet result;
    BackEnd backEnd = new BackEnd(getActivity());
    MyDBAdapter dbAdapter;
    Rutinas rutinas = new Rutinas();
    String originalCodigo = "";

    public static FragmentCambioUbicacion newInstance(String param1, String param2) {
        FragmentCambioUbicacion fragment = new FragmentCambioUbicacion();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentCambioUbicacion() {
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
        Toast.makeText(getActivity(), "Esta en inventario " + mParam2.toString(), Toast.LENGTH_LONG).show();
        View view = inflater.inflate(R.layout.fragment_cambio_ubicacion, container, false);
        dbAdapter = new MyDBAdapter(getActivity());
        txtcodigocambio = (EditText) view.findViewById(R.id.txtcodigocambio);
        txtubicviejacambio = (EditText) view.findViewById(R.id.txtubicviejacambio);
        txtdimenviejacambio = (EditText) view.findViewById(R.id.txtdimenviejacambio);
        txtcodigo2cambio = (EditText) view.findViewById(R.id.txtcodigo2cambio);
        txtubicanuevacambio = (EditText) view.findViewById(R.id.txtubicanuevacambio);
        txtexistenciacambio = (EditText) view.findViewById(R.id.txtexistenciacambio);
        cmbsugerido = (Spinner) view.findViewById(R.id.cmbsugerido);
        cmbpasillosubica = (Spinner) view.findViewById(R.id.cmbpasillosubica);
        String[] pasillos = {"A", "B","C","D","E","F","G","H","I","J","K","L","M","N","Ñ","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        cmbpasillosubica.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, pasillos));
        cmbpasillosubica.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sugerenciaUbicacion();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        txtcodigocambio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundColor(Color.GREEN);
                } else {
                    v.setBackgroundColor(Color.RED);
                }
            }
        });

        txtcodigocambio.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_ENTER ) {
                    if( event.getAction() == KeyEvent.ACTION_UP ) {
                        if(txtcodigocambio.getText().toString().trim().equals("")) return true;
                        cargarInformacion();
                        dbAdapter.openSQLite();
                        ContentValues cv = new ContentValues();
                        cv.put("BINNMBR", "SIN UBICACION");
                        dbAdapter.modContentSQLite("CHANGEIV00102", cv, "BINNMBR = ''", 2);
                        Cursor cursor = null;
                        if (txtubicviejacambio.getText().toString().trim().equals("") && txtcodigo2cambio.getText().toString().trim().equals("") && txtubicanuevacambio.getText().toString().trim().equals("") && !txtcodigocambio.getText().toString().trim().equals("LIMPIAR")) {
                            cursor = dbAdapter.execSQLlite("SELECT COUNT(*) AS CODIGO,(SELECT COUNT(*) FROM CHANGEIV00102 WHERE BINNMBR = '" + txtcodigocambio.getText().toString() + "' AND TYPE = 'BINNMBR') AS UBICACION,(SELECT COUNT(*) FROM CHANGEIV00102 WHERE BINNMBR = '" + txtcodigocambio.getText().toString() + "' AND TYPE = 'BINNMBRIV') AS BINNMBRIV FROM CHANGEIV00102 WHERE ITEMNMBR = '" + txtcodigocambio.getText().toString() + "' AND TYPE = 'ITEMNMBR';");
                            if (cursor != null && !cursor.moveToFirst() == false) {
                                if (cursor.getInt(0) == 1) {
                                    cursor.close();
                                    cursor = dbAdapter.execSQLlite("SELECT * FROM CHANGEIV00102 WHERE ITEMNMBR = '" + txtcodigocambio.getText() + "' AND TYPE = 'ITEMNMBR'");
                                    if (cursor.getCount() > 0) txtcodigocambio.setText("");
                                    if (cursor != null && cursor.moveToFirst()) {
                                        txtcodigo2cambio.setText(cursor.getString(0));
                                        txtexistenciacambio.setText(cursor.getString(2));
                                        txtubicviejacambio.setText(cursor.getString(4));
                                    }
                                }else{
                                    Toast.makeText(getActivity(),"CODIGO " +  txtcodigocambio.getText() + " NO EXISTE EN AUTONICA S,A SE INTENTARA CON EL ORIGINAL " +  originalCodigo, Toast.LENGTH_LONG).show();
                                    /*codigo nuevo*/
                                    cursor = dbAdapter.execSQLlite("SELECT COUNT(*) AS CODIGO,(SELECT COUNT(*) FROM CHANGEIV00102 WHERE BINNMBR = '" + originalCodigo + "' AND TYPE = 'BINNMBR') AS UBICACION,(SELECT COUNT(*) FROM CHANGEIV00102 WHERE BINNMBR = '" + originalCodigo + "' AND TYPE = 'BINNMBRIV') AS BINNMBRIV FROM CHANGEIV00102 WHERE ITEMNMBR = '" + originalCodigo + "' AND TYPE = 'ITEMNMBR';");
                                    if (cursor != null && !cursor.moveToFirst() == false) {
                                        if (cursor.getInt(0) == 1) {
                                            cursor.close();
                                            cursor = dbAdapter.execSQLlite("SELECT * FROM CHANGEIV00102 WHERE ITEMNMBR = '" + originalCodigo + "' AND TYPE = 'ITEMNMBR'");
                                            if (cursor.getCount() > 0) txtcodigocambio.setText("");
                                            if (cursor != null && cursor.moveToFirst()) {
                                                txtcodigo2cambio.setText(cursor.getString(0));
                                                txtexistenciacambio.setText(cursor.getString(2));
                                                txtubicviejacambio.setText(cursor.getString(4));
                                            }
                                        }
                                    }
                                    /*codigo nuevo*/
                                    txtcodigocambio.setText("");
                                }
                            }
                            cursor.close();
                            dbAdapter.modContentSQLite("CHANGEIV00102", null, null, 3);
                        }else if(!txtcodigo2cambio.getText().toString().trim().equals("") && !txtcodigocambio.getText().toString().trim().equals("LIMPIAR")){
                            if (txtcodigocambio.getText().toString().equals(txtcodigo2cambio.getText().toString())) {
                                Toast.makeText(getActivity(), "UBICACION NO PUEDE SER IGUAL AL CODIGO...", Toast.LENGTH_LONG).show();
                                return true;
                            }
                            if (txtcodigocambio.getText().toString().equals(txtubicviejacambio.getText().toString())) {
                                Toast.makeText(getActivity(), "UBICACION NO PUEDE SER LA MISMA QUE LA VIEJA...", Toast.LENGTH_LONG).show();
                                return true;
                            }
                            cursor = dbAdapter.execSQLlite("SELECT COUNT(*) AS BINNMBR,(" +
                                    "SELECT COUNT(*) FROM CHANGEIV00102 WHERE BINNMBR = '" + txtcodigocambio.getText().toString().trim() + "' AND TYPE = 'BINNMBR'" +
                                    ") AS BINNMBRIV  FROM CHANGEIV00102 WHERE BINNMBR = '" + txtcodigocambio.getText().toString().trim() + "' AND TYPE = 'BINNMBRIV'");
                            if (cursor != null && cursor.moveToFirst()) {
                                if (cursor.getInt(0) > 0){
                                    cursor.close();
                                    cursor = dbAdapter.execSQLlite("SELECT * FROM CHANGEIV00102 WHERE BINNMBR = '" + txtcodigocambio.getText().toString().trim() + "' AND TYPE = 'BINNMBRIV'");
                                    if (cursor != null && cursor.moveToFirst()) {
                                        do {
                                            Toast.makeText(getActivity(), "UBICACION ESTA ASIGNADA AL CODIGO(S) " + cursor.getString(0) + "(" + cursor.getString(1).toString().trim() + ")", Toast.LENGTH_LONG).show();
                                        } while (cursor.moveToNext());
                                        cursor.close();
                                        txtcodigocambio.setText("");
                                    }
                                }else if(cursor.getInt(0) == 0){
                                    String sql = "";
                                    txtubicanuevacambio.setText(txtcodigocambio.getText().toString().trim());
                                    if (cursor.getInt(1) == 0){
                                        sql += "INSERT INTO  zMAESTRO_BIN (MBN_PASILLO, MBN_BINNMBR, MBN_BINTYPE, LOCNCODE,USERCTD) VALUES (SUBSTRING('" + txtubicanuevacambio.getText().toString() + "',0,4),'" + txtubicanuevacambio.getText().toString() + "','LM','" + mParam2.toString() + "','" + mParam1.toString() + "') ";
                                    }
                                    cursor.close();
                                    sql += "UPDATE IV00102 SET BINNMBR = '" + txtubicanuevacambio.getText().toString().trim() + "' " +
                                    "WHERE ITEMNMBR = '" + txtcodigo2cambio.getText().toString() + "' AND LOCNCODE = '" + mParam2.toString() + "' " +
                                    "UPDATE zActualiz_IV00102 SET USUARIO = '" + mParam1.toString() + "'  WHERE ITEMNMBR = '" + txtcodigo2cambio.getText().toString() + "' AND CAMBIO = (SELECT MAX(CAMBIO) FROM zActualiz_IV00102)";
                                    Integer actualizador = backEnd.execSQLUpdate(sql);
                                    if (actualizador == 1){ Toast.makeText(getActivity(),"CODIGO SE A CAMBIADO DE UBICACION" , Toast.LENGTH_LONG).show(); limpiarCajas();}
                                    else if (actualizador == 0){ Toast.makeText(getActivity(),"CODIGO NO CAMBIO DE UBICACION" , Toast.LENGTH_LONG).show(); txtubicanuevacambio.setText("");}
                                }
                            }
                            dbAdapter.modContentSQLite("CHANGEIV00102", null, null, 3);
                        }else if(txtcodigocambio.getText().toString().trim().equals("LIMPIAR") && !txtcodigo2cambio.getText().toString().trim().equals("")){
                            limpiarCajas();
                        }
                    }
                    txtcodigocambio.requestFocus();
                    return true;
                }
                return false;
            }
        });
        txtcodigocambio.setBackgroundColor(Color.GREEN);
        return  view;
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

    private void limpiarCajas(){
        txtcodigocambio.setText("");
        txtubicviejacambio.setText("");
        txtcodigo2cambio.setText("");
        txtubicanuevacambio.setText("");
        txtdimenviejacambio.setText("");
        txtexistenciacambio.setText("");
    }

    public Integer cargarInformacion(){
        Integer contador = 0;
        originalCodigo = txtcodigocambio.getText().toString().trim().replace("'", "-");
        String FormatoCodigo = rutinas.ITEMNMBRFormat(txtcodigocambio.getText().toString().trim().replace("'", "-"));
        if (!txtcodigo2cambio.getText().toString().equals("")){ txtcodigocambio.setText(originalCodigo);}else {txtcodigocambio.setText(FormatoCodigo);}
        try {
            result = backEnd.execSQL("SELECT UPPER(RTRIM(IV00102.ITEMNMBR)) AS ITEMNMBR,IV00101.ITEMDESC,IV00102.QTYONHND,IV00102.LOCNCODE,RTRIM(IV00102.BINNMBR) AS BINNMBR,'ITEMNMBR' AS TYPE  FROM IV00102" +
                                    " INNER JOIN IV00101 ON IV00102.ITEMNMBR = IV00101.ITEMNMBR" +
                                    " WHERE IV00102.ITEMNMBR IN ('" + txtcodigocambio.getText() + "','" + originalCodigo + "') AND IV00102.LOCNCODE = '" + mParam2.toString() + "'" +
                                    " UNION ALL" +
                                    " SELECT  '' AS ITEMNMBR, '' AS ITEMDESC,0 as QTYONHND, LOCNCODE,RTRIM(MBN_BINNMBR) AS BINNMBR, 'BINNMBR' AS TYPE" +
                                    " FROM zMAESTRO_BIN WHERE MBN_BINNMBR = '" + txtcodigocambio.getText() + "' AND LOCNCODE = '" + mParam2.toString() + "' " +
                                    " UNION ALL" +
                                    " SELECT IV00102.ITEMNMBR,IV00101.ITEMDESC,IV00102.QTYONHND,IV00102.LOCNCODE,RTRIM(IV00102.BINNMBR),'BINNMBRIV' AS TYPE  FROM IV00102" +
                                    " INNER JOIN IV00101 ON IV00102.ITEMNMBR = IV00101.ITEMNMBR" +
                                    " WHERE IV00102.BINNMBR = '" + txtcodigocambio.getText() + "' AND IV00102.LOCNCODE = '" + mParam2.toString()+ "'");
            if (result == null) return 0;
            while(result.next()){
                dbAdapter.openSQLite();
                ContentValues cv = new ContentValues();
                cv.put("ITEMNMBR", result.getString("ITEMNMBR"));
                cv.put("ITEMDESC", result.getString("ITEMDESC"));
                cv.put("QTYONHND", result.getInt("QTYONHND"));
                cv.put("LOCNCODE", result.getString("LOCNCODE"));
                cv.put("BINNMBR", result.getString("BINNMBR"));
                cv.put("TYPE", result.getString("TYPE"));
                contador += dbAdapter.modContentSQLite("CHANGEIV00102", cv, null, 1);
            }
        }catch (SQLException e){
            contador = 0;
            Toast.makeText(getActivity(), e.toString() , Toast.LENGTH_LONG).show();
        }
        return contador;
    }

    private void sugerenciaUbicacion(){
        try {
            result = backEnd.execSQL("SELECT MBN_BINNMBR FROM zMAESTRO_BIN WHERE LOCNCODE = '" + mParam2.toString() + "' AND MBN_BINNMBR NOT IN (SELECT BINNMBR FROM ANSA.dbo.IV00102 WHERE SUBSTRING(BINNMBR,0,2)  = '" + cmbpasillosubica.getSelectedItem().toString() + "' AND LOCNCODE ='" + mParam2.toString() + "')" +
                    " AND SUBSTRING(MBN_BINNMBR,0,2)  = '" + cmbpasillosubica.getSelectedItem().toString() + "' AND ACTIVE = 0");
            if (result == null) return;
            List<String> proveedores = new ArrayList<String>();
            while(result.next()){
                proveedores.add( result.getString("MBN_BINNMBR"));
            }
            result.close();
            cmbsugerido.setAdapter(new ArrayAdapter<String>(
                    getActivity(),android.R.layout.simple_spinner_dropdown_item,proveedores
            ));

        }catch (SQLException e){
            Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_LONG).show();
        }
    }
}
