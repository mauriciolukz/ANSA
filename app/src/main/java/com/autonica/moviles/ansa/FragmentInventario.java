package com.autonica.moviles.ansa;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.*;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Environment;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

public class FragmentInventario extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private EditText txtcodigoinvent,txtubicinvent,txtexistinventscan,txttotalinvent,
            txtexistinvent,txtreserinvent,txtdescinvent,txtcodigoinventactual,txtpasillo,txtcolumna,txtitem,txtdescripcioninventarioID;
    private Spinner txtclase;
    private TextView textView11,textView13;
    BackEnd backEnd = new BackEnd(getContext());
    MyDBAdapter dbAdapter;
    ResultSet rs ;
    Rutinas rutinas = new Rutinas();
    private ListView lstcodigosinvent;
    private List<Map<String,String>> inventariar = new ArrayList<Map<String,String>>();
    private OnFragmentInteractionListener mListener;
    private SimpleAdapter AD;
    private int total = 0;


    public static FragmentInventario newInstance(String param1, String param2) {
        FragmentInventario fragment = new FragmentInventario();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentInventario() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventario, container, false);
        Integer r = 0;
        dbAdapter = new MyDBAdapter(getActivity());
        lstcodigosinvent = (ListView) view.findViewById(R.id.lstcodigosinvent);
        txtcodigoinvent = (EditText) view.findViewById(R.id.txtcodigoinvent);
        txtcodigoinventactual = (EditText) view.findViewById(R.id.txtcodigoinventactual);
        txttotalinvent = (EditText) view.findViewById(R.id.txttotalinvent);
        txtubicinvent = (EditText) view.findViewById(R.id.txtubicinvent);
        txtexistinventscan = (EditText) view.findViewById(R.id.txtexistinventscan);
        txtexistinvent = (EditText) view.findViewById(R.id.txtexistinvent);
        txtreserinvent = (EditText) view.findViewById(R.id.txtreserinvent);
        txtdescinvent = (EditText) view.findViewById(R.id.txtdescinvent);
        textView13 = (TextView) view.findViewById(R.id.textView13);
        textView11 = (TextView) view.findViewById(R.id.textView11);
        txtpasillo = (EditText) view.findViewById(R.id.txtpasillo);
        txtcolumna = (EditText) view.findViewById(R.id.txtcolumna);
        Button btnlimpiar = (Button) view.findViewById(R.id.btnlimpiar);
        Button btnsubir = (Button) view.findViewById(R.id.btnsubir);
        Button btnrefrescar = (Button) view.findViewById(R.id.btnrefrescar);
        txtclase = (Spinner) view.findViewById(R.id.txtclase);
        txtitem = (EditText) view.findViewById(R.id.txtitem);
        txtdescripcioninventarioID = (EditText) view.findViewById(R.id.txtdescripcioninventarioID);
        cargarClases();

        btnlimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiar();
                txtubicinvent.requestFocus();
            }
        });

        btnrefrescar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarListaInventario();
            }
        });

        btnsubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarInventariado();
            }
        });

        txtpasillo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    txtcolumna.requestFocus();
                    return true;
                }
                return false;
            }
        });

        txtcolumna.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    //if(txtcolumna.getText().toString().trim().equals("")){txtcolumna.requestFocus(); return true;}
                    borrarListaInventario();
                    obtenerInvetareo();
                    cargarListaInventario();
                    txtubicinvent.requestFocus();
                    return true;
                }
                return false;
            }
        });

        txtubicinvent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if(txtubicinvent.getText().toString().trim().equals("")){txtubicinvent.requestFocus();return true;}
                    obtenerUbicacion();
                    txtcodigoinvent.requestFocus();
                    return true;
                }
                return false;
            }
        });

        txtcodigoinvent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String codigoreal = txtcodigoinvent.getText().toString().trim();
                    if (txtcodigoinvent.getText().toString().equals("")){txtcodigoinvent.requestFocus();return true;}
                    txtcodigoinvent.setText(rutinas.ITEMNMBRFormat(txtcodigoinvent.getText().toString().replace("'", "-")));
                    if (!txtcodigoinvent.getText().toString().equals(txtcodigoinventactual.getText().toString())){
                        txtcodigoinvent.setText(codigoreal);
                        Toast.makeText(getActivity(), "El codigo scan es diferente al de la ubicacion se uso codigo natural... " + txtcodigoinvent.getText(), Toast.LENGTH_LONG).show();
                        actualizarCodigo();
                        txtcodigoinvent.requestFocus();
                        txtcodigoinvent.setText("");
                        return true;
                    }
                    obtenerCodigo();
                    actualizarCodigo();
                    txtcodigoinvent.requestFocus();
                    return true;
                }
                return false;
            }
        });

        return view;
    }

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

    private void cargarListaInventario(){
        inventariar.clear();
        dbAdapter.openSQLite();
        String[] args = new String[] {"0"};

        Cursor cursor = dbAdapter.execSQLlite("IV00102", null,"IVSTATUS = ?", args, null, null, "BINNMBR ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Map<String, String> datanum = new HashMap<String,String>();
                datanum.put("A", cursor.getString(0));
                datanum.put("B", cursor.getString(1));
                //datanum.put("C", cursor.getString(6));
                datanum.put("C", "0");
                datanum.put("D", cursor.getString(8));
                inventariar.add(datanum);
            } while (cursor.moveToNext());
        }
        String[] from = {"A","B","C","D"};
        int[] views = {R.id.txtcodigoinvent2,R.id.txtdescripcioninvent,R.id.txtcantidadinvent,R.id.txtubicinvent2};
        AD = new SimpleAdapter(getActivity(), inventariar, R.layout.listinvent, from, views);
        lstcodigosinvent.setAdapter(AD);

        dbAdapter.openSQLite();
        Cursor cursor2 = dbAdapter.execSQLlite("SELECT COUNT(*) AS FALTANTE,(SELECT COUNT(*) FROM IV00102 WHERE LOCNCODE  = '" + mParam2.toString() + "' AND IVSTATUS = 2) AS REALIZADOS FROM IV00102 WHERE LOCNCODE  = '" + mParam2.toString() + "'");
        if (cursor2 != null && cursor2.moveToFirst()) {
            do {
                textView11.setText(cursor2.getString(1) + " ");
                textView13.setText(cursor2.getString(0));
            } while (cursor2.moveToNext());
        }
    }

    private void borrarListaInventario(){
        dbAdapter.openSQLite();
        dbAdapter.modContentSQLite("IV00102", null, null, 3);
    }

    private void limpiar(){
        txtcodigoinvent .setText("");
        txtcodigoinventactual.setText("");
        txttotalinvent.setText("0");
        txtubicinvent.setText("");
        txtexistinventscan.setText("0");
        txtexistinvent.setText("0");
        txtreserinvent.setText("0");
        txtdescinvent.setText("");
    }

    private void obtenerInvetareo(){
        try {
            String filtro1 = "";
            String filtro2 = "";
            String concepto = "";
            if(!txtcolumna.getText().toString().trim().equals("")){ filtro2 = "-" + txtcolumna.getText().toString().trim();}
            if(!txtpasillo.getText().toString().trim().equals("")){ filtro1 = "IV00102.BINNMBR LIKE '"+ txtpasillo.getText().toString().trim() + filtro2 + "%'";}
            if(!txtclase.getSelectedItem().toString().equals("")) { if (filtro1.trim().length() > 0){filtro1 = filtro1 + " AND ";} filtro1 =  filtro1 + "IV00101.ITMCLSCD = '" + txtclase.getSelectedItem().toString()+"'";}
            if(!txtitem.getText().toString().trim().equals(""))   {
                if (filtro1.trim().length() > 0){filtro1 = filtro1 + " AND ";}
                String existeDocumento = "";
                existeDocumento = obtenerItemsDocumento(txtitem.getText().toString().trim());
                if (existeDocumento.trim().length() > 0){
                    filtro1 =  filtro1 + "IV00101.ITEMNMBR IN (" +  existeDocumento + ")";
                    if (txtitem.getText().toString().trim().substring(0,3).equals("AJT")){ concepto = "AUDITORIA DE AJUSTES " + txtitem.getText().toString().trim(); }
                }else{
                    filtro1 =  filtro1 + "IV00101.ITEMNMBR LIKE '" + txtitem.getText().toString().trim()+"'";
                }
            }
            if(!txtdescripcioninventarioID.getText().toString().trim().equals("")){if (filtro1.trim().length() > 0){filtro1 = filtro1 + " AND ";} filtro1 =  filtro1 + "IV00101.ITEMDESC LIKE '" + txtdescripcioninventarioID.getText().toString().trim() + "'";}

            rs = backEnd.execSQL("SELECT RTRIM(IV00101.ITEMNMBR) AS ITEMNMBR, IV00101.ITEMDESC, IV00101.ITMSHNAM, IV00101.ITMGEDSC, IV00101.CURRCOST, IV00101.ITMCLSCD, IV00102.QTYONHND, IV00102.ATYALLOC," +
                    "RTRIM(IV00102.BINNMBR) AS BINNMBR,0 AS QTYONHNDM,RTRIM(IV00102.LOCNCODE) AS LOCNCODE,0 AS IVSTATUS,GETDATE() AS SCANDATET,'"+mParam1.toString()+"' AS USERID,GETDATE() AS DATEDDT " +
                    "FROM IV00101 INNER JOIN " +
                    "IV00102 ON IV00101.ITEMNMBR = IV00102.ITEMNMBR AND IV00102.QTYONHND <> 0 AND IV00102.LOCNCODE = '" + mParam2.toString() + "' " +
                    "WHERE " + filtro1);
            dbAdapter = new MyDBAdapter(getActivity());
            while(rs.next()){
                dbAdapter.openSQLite();
                ContentValues cv = new ContentValues();
                cv.put("ITEMNMBR", rs.getString("ITEMNMBR"));
                cv.put("ITEMDESC", rs.getString("ITEMDESC"));
                cv.put("ITMSHNAM", rs.getString("ITMSHNAM"));
                cv.put("ITMGEDSC", rs.getString("ITMGEDSC"));
                cv.put("CURRCOST", rs.getInt("CURRCOST"));
                cv.put("ITMCLSCD", rs.getString("ITMCLSCD"));
                cv.put("QTYONHND", rs.getInt("QTYONHND"));
                cv.put("ATYALLOC", rs.getInt("ATYALLOC"));
                cv.put("BINNMBR", rs.getString("BINNMBR"));
                cv.put("QTYONHNDM", rs.getInt("QTYONHNDM"));
                cv.put("LOCNCODE", rs.getString("LOCNCODE"));
                cv.put("IVSTATUS", rs.getString("IVSTATUS"));
                cv.put("SCANDATET", rs.getString("SCANDATET"));
                cv.put("USERID", rs.getString("USERID"));
                cv.put("DATEDDT", rs.getString("DATEDDT"));
                cv.put("AISLE", txtpasillo.getText().toString());
                cv.put("COLUMN", txtcolumna.getText().toString());
                cv.put("TXTCOMENT",concepto.toString());
                cv.put("SCANDATETNOREN",'1990-01-01 00:00:00.000');
                dbAdapter.modContentSQLite("IV00102", cv, null, 1);
            }
        }catch (SQLException e){
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void cargarInventariado(){
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Integer asignado = 0,asignadocorrecto = 0;
        Integer scaneado = 0,scaneadocorrecto = 0;
        Integer linea = 0;
        Integer diferencia = 0,diferenciarrecta = 0;
        Integer cambio = 0;

        dbAdapter.openSQLite();
        Cursor cursor2 = dbAdapter.execSQLlite("SELECT COUNT(*) AS FALTANTE,(SELECT COUNT(*) FROM IV00102 WHERE LOCNCODE  = '" + mParam2.toString() + "' AND IVSTATUS = 2) AS REALIZADOS,CAST((julianday(MAX(SCANDATET)) - julianday(MIN(SCANDATET)))  * 24 AS INTEGER)  AS DURACION,MIN(DATEDDT) AS INICIO,MAX(SCANDATET) AS FIN,MAX(USERID) AS USERID,MAX(TXTCOMENT) AS TXTCOMENT FROM IV00102 WHERE LOCNCODE  = '" + mParam2.toString() + "'");
        cursor2.moveToFirst();
        String sql = "";

        //Nuevo libro de trabajo
        Workbook wb = new HSSFWorkbook();
        Cell c = null;

        //Estilo de celda para la fila de encabezado
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.AQUA.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cs.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //cs.setAlignment();

        //Hoja nueva
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("Inventario Almacen");


        String[] columnaresumen = {"Pasillo", "Columna", "Inventario sistema","Inventario fisico","Diferencia","Inicio","Fin","Usuario","Duracion"};
        String[] columnaresumenresul = {txtpasillo.getText().toString().trim(), txtcolumna.getText().toString().trim(), cursor2.getString(0),cursor2.getString(1),Integer.toString(Integer.parseInt(cursor2.getString(0)) - Integer.parseInt(cursor2.getString(1))),cursor2.getString(3),cursor2.getString(4),cursor2.getString(5),cursor2.getString(2)};
        int lineaExcel = 0;
        for (int i = 0;i < columnaresumen.length;i++){
            //Generar encabezados de columna
            Row row = sheet1.createRow(i);

            c = row.createCell(0);
            c.setCellValue(columnaresumen[i]);
            c.setCellStyle(cs);

            c = row.createCell(1);
            c.setCellValue(columnaresumenresul[i]);
            //c.setCellStyle(cs);
            lineaExcel = i;
        }

        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));
        sheet1.setColumnWidth(3, (15 * 500));


        lineaExcel = lineaExcel + 3;
        sheet1.addMergedRegion(CellRangeAddress.valueOf("A"+lineaExcel+":G" + lineaExcel));
        Row row  = sheet1.createRow(lineaExcel - 1);
        c = row.createCell(0);
        c.setCellValue("Inventario con problemas Almacen MATRIZ Pasillo " + txtpasillo.getText().toString().trim());
        c.setCellStyle(cs);
        //lineaExcel = lineaExcel + 1;

        String[] columnasdetalle = {"Linea", "Modelo", "Codigo","Descripcion","Asignado","Scaneado","Diferencia"};
        row = sheet1.createRow(lineaExcel);
        for (int i = 0;i < columnasdetalle.length;i++){
            c = row.createCell(i);
            c.setCellValue(columnasdetalle[i]);
            c.setCellStyle(cs);
        }

        dbAdapter.openSQLite();
        Cursor cursor = dbAdapter.execSQLlite("IV00102",null,null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Toast.makeText(getActivity(), "Cargando Informacion.... Espere el aviso de confirmacion", Toast.LENGTH_LONG).show();
            do {
                sql += " INSERT INTO UTIL.dbo.InventarioIV00102 (ITEMNMBR, ITEMDESC, ITMSHNAM, ITMGEDSC, CURRCOST, ITMCLSCD, QTYONHND, ATYALLOC, BINNMBR, QTYONHNDM, LOCNCODE, IVSTATUS, SCANDATET,USERID, DATEDDT,AISLE,[COLUMN],TXTCOMENT,SCANDATETNOREN) " +
                        "VALUES ('"+cursor.getString(0)+"','"+cursor.getString(1)+"','"+cursor.getString(2)+"','"+cursor.getString(3)+"','"+Integer.toString(cursor.getInt(4)) +"','"+cursor.getString(5)+"','"+Integer.toString(cursor.getInt(6))+"','"+Integer.toString(cursor.getInt(7))+"','"+cursor.getString(8)+"','"+Integer.toString(cursor.getInt(9))+"','"+cursor.getString(10)+"','"+Integer.toString(cursor.getInt(11))+"','"+cursor.getString(12)+"','"+cursor.getString(13)+"','"+cursor.getString(14)+"','"+cursor.getString(16)+"','"+cursor.getString(17)+"','"+cursor.getString(18)+"','"+cursor.getString(19)+"') ";
            } while(cursor.moveToNext());
            cambio = backEnd.execSQLUpdate(sql);
            if (Integer.toString(cambio).equals("1")){
                Toast.makeText(getActivity(), "Operacion finalizada...", Toast.LENGTH_LONG).show();
                limpiar();
                txtpasillo.setText("");
                txtcolumna.setText("");
            }
        }


        if (Integer.toString(cambio).equals("1")){
            dbAdapter.openSQLite();
            Cursor cursor3 = dbAdapter.execSQLlite("IV00102",null,"(QTYONHND - ATYALLOC) <> QTYONHNDM", null, null, null, null);
            if (cursor3 != null && cursor3.moveToFirst()) {
                do {
                    lineaExcel = lineaExcel + 1;
                    linea = linea + 1;
                    row = sheet1.createRow(lineaExcel);
                    c = row.createCell(0);
                    c.setCellValue(linea);

                    c = row.createCell(1);
                    c.setCellValue(cursor3.getString(2));

                    c = row.createCell(2);
                    c.setCellValue(cursor3.getString(0));

                    c = row.createCell(3);
                    c.setCellValue(cursor3.getString(1));

                    c = row.createCell(4);
                    c.setCellValue(Integer.toString(cursor3.getInt(7)));

                    c = row.createCell(5);
                    c.setCellValue(Integer.toString(cursor3.getInt(9)));

                    c = row.createCell(6);
                    c.setCellValue(( cursor3.getInt(9) - (cursor3.getInt(6) - cursor3.getInt(7))));

                    asignado   =  asignado   + cursor3.getInt(7);
                    scaneado   =  scaneado   + cursor3.getInt(9);
                    diferencia = diferencia + ((cursor3.getInt(6) - cursor3.getInt(7)) - cursor3.getInt(9));
                } while(cursor3.moveToNext());
            }
        }

        lineaExcel = lineaExcel + 1;
        row = sheet1.createRow(lineaExcel);
        c = row.createCell(0);
        c.setCellValue("Totales");
        c = row.createCell(4);
        c.setCellValue(asignado);
        c = row.createCell(5);
        c.setCellValue(scaneado);
        c = row.createCell(6);
        c.setCellValue(diferencia);


        lineaExcel = lineaExcel + 3;
        sheet1.addMergedRegion(CellRangeAddress.valueOf("A"+lineaExcel+":G" + lineaExcel));
        row  = sheet1.createRow(lineaExcel - 1);
        c = row.createCell(0);
        c.setCellValue("Inventario sin problemas Almacen MATRIZ Pasillo " + txtpasillo.getText().toString().trim());
        c.setCellStyle(cs);

        row = sheet1.createRow(lineaExcel);
        for (int i = 0;i < columnasdetalle.length;i++){
            c = row.createCell(i);
            c.setCellValue(columnasdetalle[i]);
            c.setCellStyle(cs);
        }



        if (Integer.toString(cambio).equals("1")){
            dbAdapter.openSQLite();
            Cursor cursor4 = dbAdapter.execSQLlite("IV00102",null,"(QTYONHND - ATYALLOC) = QTYONHNDM", null, null, null, null);
            linea = 0;
            if (cursor4 != null && cursor4.moveToFirst()) {
                do {
                    lineaExcel = lineaExcel + 1;
                    linea = linea + 1;

                    row = sheet1.createRow(lineaExcel);
                    c = row.createCell(0);
                    c.setCellValue(linea);

                    c = row.createCell(1);
                    c.setCellValue(cursor4.getString(2));

                    c = row.createCell(2);
                    c.setCellValue(cursor4.getString(0));

                    c = row.createCell(3);
                    c.setCellValue(cursor4.getString(1));

                    c = row.createCell(4);
                    c.setCellValue(Integer.toString(cursor4.getInt(7)));

                    c = row.createCell(5);
                    c.setCellValue(Integer.toString(cursor4.getInt(9)));

                    c = row.createCell(6);
                    c.setCellValue((cursor4.getInt(9) - (cursor4.getInt(6) - cursor4.getInt(7))));

                    asignadocorrecto =  asignadocorrecto   + cursor4.getInt(7);
                    scaneadocorrecto =  scaneadocorrecto   + cursor4.getInt(9);
                } while(cursor4.moveToNext());
            }
        }

        lineaExcel = lineaExcel + 1;
        row = sheet1.createRow(lineaExcel);
        c = row.createCell(0);
        c.setCellValue("Totales");
        c = row.createCell(4);
        c.setCellValue(asignadocorrecto);
        c = row.createCell(5);
        c.setCellValue(scaneadocorrecto);
        c = row.createCell(6);
        c.setCellValue(diferenciarrecta);



        // Crear una ruta donde colocaremos nuestra Lista de objetos en el almacenamiento externo
        File file = new File(getContext().getExternalFilesDir(null), "InventarioAlmacen.xls");
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Toast.makeText(getContext(), "Writing file" + file, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error writing " + file, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to save file", Toast.LENGTH_SHORT).show();
        } finally {
                try {
                    if (null != os)
                        os.close();
                } catch (Exception ex) {
            }
        }
        backEnd.sendEmail("servermbs@autonica.com","mauricio.palacios@autonica.com","","Inventario Almacen " + mParam2.toString() + " Pasillo " + txtpasillo.getText().toString().trim() + " " + cursor2.getString(6),file.getParent().toString() + "/");
        //backEnd.sendEmail("servermbs@autonica.com","juan_velasquez@autonica.com,necdaly.villareal@autonica.com,sergio_sandino@autonica.com,ramon.rodriguez@autonica.com","","Inventario Almacen " + mParam2.toString() + " Pasillo " + txtpasillo.getText().toString().trim() + " " + cursor2.getString(6),file.getParent().toString() + "/");
    }

    private void obtenerCodigo(){
        dbAdapter.openSQLite();
        Cursor cursor = dbAdapter.execSQLlite("SELECT * FROM IV00102 WHERE ITEMNMBR = '" + txtcodigoinvent.getText().toString().trim() + "' AND LOCNCODE  = '" + mParam2.toString() + "'");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                txtexistinventscan.setText(cursor.getString(9));
            } while (cursor.moveToNext());
        }
    }

    private void obtenerUbicacion(){
        dbAdapter.openSQLite();
        Cursor cursor = dbAdapter.execSQLlite("SELECT * FROM IV00102 WHERE BINNMBR = '" + txtubicinvent.getText().toString().replace("'", "-") + "' AND LOCNCODE  = '" + mParam2.toString() + "'");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                txtcodigoinventactual.setText(cursor.getString(0));
                txtdescinvent.setText(cursor.getString(1));
                //txtexistinvent.setText(cursor.getString(6));
                //txtreserinvent.setText(cursor.getString(7));
                txtubicinvent.setText(cursor.getString(8));
                txtexistinventscan.setText(cursor.getString(9));
                total = (cursor.getInt(6) - cursor.getInt(7));
                //txttotalinvent.setText(Integer.toString(cursor.getInt(6) - cursor.getInt(7)));
            } while (cursor.moveToNext());
        }
    }

    private void actualizarCodigo(){
        Integer mod = 0;
        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ContentValues cv2 = new ContentValues();
        cv2.put("QTYONHNDM", (Integer.parseInt(txtexistinventscan.getText().toString()) + 1));
        cv2.put("SCANDATET", hourdateFormat.format(date));
        cv2.put("USERID",mParam1.toString());
        mod = dbAdapter.modContentSQLite("IV00102", cv2, "ITEMNMBR = '" + txtcodigoinvent.getText().toString().trim() + "' AND BINNMBR = '" + txtubicinvent.getText().toString().trim() + "' AND LOCNCODE = '" + mParam2.toString() + "'", 2);
        if (mod > 0){}else{Toast.makeText(getActivity(), "Codigo no encontrado...", Toast.LENGTH_LONG).show();}
        obtenerCodigo();
        if (txtexistinventscan.getText().toString().trim().equals(Integer.toString(total))){
            cv2.put("IVSTATUS", 2);
            dbAdapter.modContentSQLite("IV00102", cv2, "ITEMNMBR = '" + txtcodigoinvent.getText().toString().trim() + "' AND BINNMBR = '" + txtubicinvent.getText().toString().trim() + "' AND LOCNCODE = '" + mParam2.toString() + "'", 2);
            cargarListaInventario();
        }
        txtcodigoinvent.setText("");
    }

    private void cargarClases(){
        try {
            rs = backEnd.execSQL("SELECT ITMCLSCD FROM ANSA.dbo.IV40400");
            if (rs == null) return;
            List<String> proveedores = new ArrayList<String>();
            proveedores.add("");
            while(rs.next()){
                proveedores.add(rs.getString("ITMCLSCD"));
            }
            rs.close();
            txtclase.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,proveedores));

        }catch (SQLException e){
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private String obtenerItemsDocumento(String Documento){
        String items = "";
        try {
            rs = backEnd.execSQL("SELECT LTRIM(RTRIM(ITEMNMBR)) AS ITEMNMBR  FROM IV30300 WHERE DOCNUMBR = '"+ Documento +"' UNION ALL SELECT ITEMNMBR  FROM IV10001 WHERE IVDOCNBR = '" + Documento + "'");
            if (rs == null) return "";
            while(rs.next()){
                items += "'" + rs.getString(1) + "',";
            }
            rs.close();
        }catch (SQLException e){
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
        }

        if (items.trim().length() > 0){ items = items.substring(0,items.length() - 1); }
        return items;
    }
}
