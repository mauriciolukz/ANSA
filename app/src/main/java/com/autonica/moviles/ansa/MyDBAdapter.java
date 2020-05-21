package com.autonica.moviles.ansa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBAdapter {
    Context context;
    private myDBHelper dbHelper;
    private SQLiteDatabase db;
    private String DATABASE_NAME = "ANSATemp";
    private int DATABASE_VERSION = 3;

    public MyDBAdapter(Context context) {
        this.context = context;
        dbHelper = new myDBHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void openSQLite() {
        db = dbHelper.getWritableDatabase();
    }

    public Cursor execSQLlite(String sql){
        return  db.rawQuery(sql, null);
    }

    public Integer modContentSQLite(String table,ContentValues content,String params,Integer type){
        Integer mod = 0;
        if (type.equals(1)){
            Long mod2;
            mod2 = db.insert(table, null, content);
            mod = Integer.parseInt(mod2.toString());
        } else if (type.equals(2)) {
            mod = db.update(table, content, params,null);
        } else if (type.equals(3)){
            mod = db.delete(table, params, null);
        }
        return mod;
    }

    public void modSQLite(String SQL){
        db.execSQL(SQL);
    }

    public Cursor execSQLlite(String table, String[] columns, String selection,
                              String[] selectionArgs, String groupBy, String having,
                              String orderBy){
        return  db.query(table,columns,selection,selectionArgs,groupBy,having,orderBy);
    }

    public class myDBHelper extends SQLiteOpenHelper {

        public myDBHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE 'CHANGEIV00102' (" +
                        "'ITEMNMBR' TEXT,"+
                        "'ITEMDESC' TEXT,"+
                        "'QTYONHND' INTEGER,"+
                        "'LOCNCODE' TEXT,"+
                        "'BINNMBR'  TEXT,"+
                        "'TYPE'     TEXT,"+
                        "'DEX_ROW_ID' INTEGER PRIMARY KEY AUTOINCREMENT);");

            db.execSQL("CREATE TABLE 'IV00102' (" +
                        "'ITEMNMBR'     TEXT,"+
                        "'ITEMDESC'     TEXT,"+
                        "'ITMSHNAM'     TEXT,"+
                        "'ITMGEDSC'     TEXT,"+
                        "'CURRCOST'     REAL,"+
                        "'ITMCLSCD'     TEXT,"+
                        "'QTYONHND'     INTEGER,"+
                        "'ATYALLOC'     INTEGER,"+
                        "'BINNMBR'      TEXT,"+
                        "'QTYONHNDM'    INTEGER," +
                        "'LOCNCODE'     TEXT," +
                        "'IVSTATUS'     INTEGER," +
                        "'SCANDATET'    TEXT," +
                        "'USERID'       TEXT," +
                        "'DATEDDT'      TEXT,"+
                        "'DEX_ROW_ID'   INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "'AISLE'      TEXT," +
                        "'COLUMN'      TEXT," +
                        "'TXTCOMENT'     TEXT," +
                        "'SCANDATETNOREN'    TEXT);");

            db.execSQL("CREATE TABLE 'TRXS' (" +
                        "'TRXSORCE' TEXT," +
                        "'BACHNUMB' TEXT," +
                        "'IVDOCTYP' INTEGER," +
                        "'IVDOCNBR' TEXT," +
                        "'ITEMNMBR' TEXT," +
                        "'ITEMDESC' TEXT," +
                        "'BINNMBRLOCTN'  TEXT," +
                        "'BINNMBRTRNSTLOC'  TEXT," +
                        "'LNSEQNBR' REAL," +
                        "'UOFM'     TEXT," +
                        "'TRXQTY'   NUMERIC," +
                        "'UNITCOST' NUMERIC," +
                        "'EXTDCOST' NUMERIC," +
                        "'TRXLOCTN' TEXT," +
                        "'TRNSTLOC' TEXT," +
                        "'SSCANTRX'  NUMERIC," +
                        "'SUSERID'   TEXT," +
                        "'SSCANUSERID' TEXT," +
                        "'RSCANTRX'  NUMERIC," +
                        "'RUSERID'   TEXT," +
                        "'RSCANUSERID' TEXT," +
                        "'DATEDET'  TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS CHANGEIV00102;");
            db.execSQL("DROP TABLE IF EXISTS IV00102;");
            db.execSQL("DROP TABLE IF EXISTS TRXS;");
            onCreate(db);
        }
    }
}

