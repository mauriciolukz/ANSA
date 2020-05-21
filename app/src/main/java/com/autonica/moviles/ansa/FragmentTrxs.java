package com.autonica.moviles.ansa;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FragmentTrxs extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    BackEnd backEnd = new BackEnd(getActivity());
    private List<Map<String, String>> trxs = new ArrayList<Map<String,String>>();
    private SimpleAdapter AD;
    private ListView listrxs;
    private Spinner cmbtrxs;
    private Button  button;
    ResultSet rs ;
    MyDBAdapter dbAdapter;
    private OnFragmentInteractionListener mListener;

    public FragmentTrxs() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentTrxs newInstance(String param1, String param2) {
        FragmentTrxs fragment = new FragmentTrxs();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trxs, container, false);
        dbAdapter = new MyDBAdapter(getActivity());
        listrxs = (ListView) view.findViewById(R.id.listrxs);
        String[] tipotrx = {"Envio", "Recepcion"};
        cmbtrxs = (Spinner) view.findViewById(R.id.cmbtrxs);
        cmbtrxs.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, tipotrx));
        button = (Button) view.findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Descargando actualizacion....", Toast.LENGTH_LONG).show();
                Integer clearList = 0;
                clearList = borrarListaTrxs();
                if (clearList == 0){
                    //Toast.makeText(getActivity(), "Es 0...." + Integer.toString(clearList), Toast.LENGTH_LONG).show();
                    clearList = borrarListaTrxs();
                    clearList = borrarListaTrxs();
                }
                obteneterTrxs();
                llenargridTrxs();
            }
        });

        listrxs.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent act = new Intent(getActivity(), ActivityTrx.class);
                act.putExtra("trx", trxs.get(position).get("C").toString());
                act.putExtra("userid", mParam1);
                act.putExtra("location", mParam2);
                act.putExtra("type", cmbtrxs.getSelectedItem().toString());
                startActivity(act);
                return false;
            }
        });



        cmbtrxs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                llenargridTrxs();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
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
        void onFragmentInteraction(Uri uri);
    }

    private void obteneterTrxs(){
        try {
            if (cmbtrxs.getSelectedItem().toString().equals("Envio")) {
                rs = backEnd.execSQL("SELECT Rellenos.TRXSORCE, Rellenos.BACHNUMB, Rellenos.IVDOCTYP, Rellenos.IVDOCNBR, Rellenos.ITEMNMBR, Rellenos.ITEMDESC, Rellenos.BINNMBRLOCTN, Rellenos.BINNMBRTRNSTLOC," +
                                    "Rellenos.LNSEQNBR, Rellenos.UOFM, Rellenos.TRXQTY, Rellenos.UNITCOST, Rellenos.EXTDCOST, Rellenos.TRXLOCTN, Rellenos.TRNSTLOC, ISNULL(RellenoIV00102.SSCANTRX,0) AS SSCANTRX, ISNULL(RellenoIV00102.SUSERID,'') AS SUSERID," +
                                    "ISNULL(RellenoIV00102.SSCANUSERID,'') AS SSCANUSERID, ISNULL(RellenoIV00102.RSCANTRX,0) AS RSCANTRX, ISNULL(RellenoIV00102.RUSERID,'') AS RUSERID, ISNULL(RellenoIV00102.RSCANUSERID,'') AS RSCANUSERID, ISNULL" +
                                    "(RellenoIV00102.DATEDET,GETDATE()) AS DATEDET,(Rellenos.TRXQTY - ISNULL(SSCANTRX,0)) AS FALTANTE FROM Rellenos LEFT OUTER JOIN (" +
                                    "SELECT IVDOCNBR, ITEMNMBR, MAX(ITEMDESC) AS ITEMDESC, SUM(SSCANTRX) AS SSCANTRX,MAX(SUSERID) AS SUSERID, MAX(SSCANUSERID) AS SSCANUSERID, SUM(RSCANTRX) AS RSCANTRX, MAX(RUSERID) AS RUSERID, MAX(RSCANUSERID) AS RSCANUSERID, MAX(DATEDET) AS DATEDET " +
                                    "FROM UTIL.dbo.RellenoIV00102 " +
                                    "GROUP BY IVDOCNBR, ITEMNMBR) AS RellenoIV00102 ON Rellenos.ITEMNMBR = RellenoIV00102.ITEMNMBR AND Rellenos.IVDOCNBR = RellenoIV00102.IVDOCNBR " +
                                    "WHERE (Rellenos.TRXLOCTN IN ('"+mParam2.toString().trim()+"','DAN','5','EXIBICION') AND (Rellenos.TRXQTY > ISNULL(RellenoIV00102.SSCANTRX, 0)))");
            }else if(cmbtrxs.getSelectedItem().toString().equals("Recepcion")){
                rs = backEnd.execSQL("SELECT * FROM UTIL.dbo.RellenoIV00102 WHERE TRNSTLOC = '"+mParam2.toString()+"' AND SSCANTRX > RSCANTRX ");
            }
            if (rs == null) return;
            dbAdapter = new MyDBAdapter(getActivity());
            while (rs.next()) {
                dbAdapter.openSQLite();
                ContentValues cv = new ContentValues();
                cv.put("TRXSORCE", rs.getString("TRXSORCE"));
                cv.put("BACHNUMB", rs.getString("BACHNUMB"));
                cv.put("IVDOCTYP", rs.getInt("IVDOCTYP"));
                cv.put("IVDOCNBR", rs.getString("IVDOCNBR"));
                cv.put("ITEMNMBR", rs.getString("ITEMNMBR"));
                cv.put("ITEMDESC", rs.getString("ITEMDESC"));

                if (rs.getString("BINNMBRLOCTN").toString().trim().equals("")){
                    cv.put("BINNMBRLOCTN", rs.getString("ITEMNMBR"));
                }else{
                    cv.put("BINNMBRLOCTN", rs.getString("BINNMBRLOCTN"));
                }

                cv.put("BINNMBRTRNSTLOC", rs.getString("BINNMBRTRNSTLOC"));
                cv.put("LNSEQNBR", rs.getInt("LNSEQNBR"));
                cv.put("UOFM", rs.getString("UOFM"));
                cv.put("TRXQTY", rs.getInt("TRXQTY"));
                cv.put("UNITCOST", rs.getInt("UNITCOST"));
                cv.put("EXTDCOST", rs.getInt("EXTDCOST"));
                cv.put("TRXLOCTN", rs.getString("TRXLOCTN"));
                cv.put("TRNSTLOC", rs.getString("TRNSTLOC"));
                cv.put("SSCANTRX", rs.getInt("SSCANTRX"));
                cv.put("SUSERID", rs.getString("SUSERID"));
                cv.put("SSCANUSERID", rs.getString("SSCANUSERID"));
                cv.put("RSCANTRX", rs.getInt("RSCANTRX"));
                cv.put("RUSERID", rs.getString("RUSERID"));
                cv.put("RSCANUSERID", rs.getString("RSCANUSERID"));
                cv.put("DATEDET", rs.getString("DATEDET"));
                dbAdapter.modContentSQLite("TRXS", cv, null, 1);
            }
        }catch (SQLException e){
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private Integer borrarListaTrxs(){
        dbAdapter.openSQLite();
        return dbAdapter.modContentSQLite("TRXS", null, null, 3);
    }

    private void llenargridTrxs(){
        String filtro = "";
        if (cmbtrxs.getSelectedItem().toString().equals("Recepcion")){ filtro = "WHERE TRNSTLOC = '"+mParam2.toString().trim()+"'";}else if (cmbtrxs.getSelectedItem().toString().equals("Envio")){ filtro = "WHERE TRXLOCTN = '"+ mParam2.toString().trim() +"'"; }
        trxs.clear();
        dbAdapter.openSQLite();
        Cursor cursor = dbAdapter.execSQLlite("SELECT DISTINCT TRXSORCE,BACHNUMB,IVDOCTYP,IVDOCNBR FROM TRXS " +  filtro + " ORDER BY IVDOCNBR");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Map<String, String> datanum = new HashMap<String,String>();
                datanum.put("A", cursor.getString(0));
                datanum.put("B", cursor.getString(1));
                datanum.put("C", cursor.getString(3));
                trxs.add(datanum);
            } while (cursor.moveToNext());
        }
        String[] from = {"A","B","C"};
        int[] views = {R.id.ivtfrtrxs,R.id.lotetrxs,R.id.trxs};
        AD = new SimpleAdapter(getActivity(), trxs, R.layout.layout, from, views);
        listrxs.setAdapter(AD);
    }
}
