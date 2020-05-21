package com.autonica.moviles.ansa;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.widget.Toast;

import java.io.File;
import java.sql.*;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;


public class BackEnd {
    Connection connect = null;
    Context context;

    public BackEnd(Context context) {
        this.context = context;
        openConnectionSQL();
    }

    public void openConnectionSQL() {
        //if(compruebaConexion(context)){
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                this.connect =  DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.0.97:1433;loginTimeout=20;databaseName=ANSA;user=lrodriguez;password=Jack2016;");
                //this.connect =  DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.1.45:1433;loginTimeout=10;inactivity-timeout=10;databaseName=ANSA;user=sa;password=D1t2020;");
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        //}else{
            //Toast.makeText(context.getApplicationContext(),"No hay conexion a internet, revice su conecion antes de realizar otra accion", Toast.LENGTH_SHORT).show();
        //}
    }

    public  void closeConnectionSQL() throws SQLException {
        if (connect != null && !connect.isClosed()) {
            connect.close();
        }else{
            openConnectionSQL();
        }
    }

    public ResultSet execSQL(String SQL) throws SQLException{
        ResultSet rs = null;
        openConnectionSQL();
        try {
            if(connect != null){
                Statement statement = connect.createStatement();
                statement.setQueryTimeout(8);
                rs = statement.executeQuery(SQL);
            }else{
                Toast.makeText(context.getApplicationContext(),"Registro de conexion invalido, verifique su conexion de datos.", Toast.LENGTH_SHORT).show();
            }
        }catch (SQLException e) {
            try{
                e.printStackTrace();
                closeConnectionSQL();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        return rs;
    }

    public Integer execSQLUpdate(String SQL){
        Integer  r = 0;
        openConnectionSQL();
        try {
            if(connect != null){
                PreparedStatement preparedStatement = connect.prepareStatement(SQL);
                preparedStatement.setQueryTimeout(8);
                r = preparedStatement.executeUpdate();
            }else{
                Toast.makeText(context.getApplicationContext(),"Registro de conexion invalido, verifique su conexion de datos.", Toast.LENGTH_SHORT).show();
            }
        }catch (SQLException e) {
            try{
                e.printStackTrace();
                closeConnectionSQL();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        return r;
    }

    public void sendEmail(String de,String para,String mensaje,String asunto,String archivo){

        try
        {
            Properties props = new Properties();
            props.setProperty("mail.smtp.host", "192.168.0.134");
            props.setProperty("mail.smtp.starttls.enable", "false");
            props.setProperty("mail.smtp.port", "25");
            props.setProperty("mail.smtp.user", "mauricio.palacios@autonica.com");
            props.setProperty("mail.smtp.auth", "true");

            Session session = Session.getDefaultInstance(props, null);

            BodyPart texto = new MimeBodyPart();
            texto.setContent(mensaje, "text/html; charset=ISO-8859-1");


            MimeMultipart multiParte = new MimeMultipart();

            File dir = new File(archivo);
            if (dir.exists()){
                File[] ficheros = dir.listFiles();
                for (int x=0;x<ficheros.length;x++){
                    String g = ficheros[x].getName();
                    BodyPart adjunto = new MimeBodyPart();
                    adjunto.setDataHandler(new DataHandler(new FileDataSource(archivo+g)));
                    adjunto.setFileName(g);
                    multiParte.addBodyPart(adjunto);
                }
            }

            multiParte.addBodyPart(texto);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(de));
            message.setRecipients(Message.RecipientType.TO, para);
            message.setSubject(asunto);
            message.setContent(multiParte);

            Transport t = session.getTransport("smtp");
            t.connect("mauricio.palacios@autonica.com", "palacios2014");
            t.sendMessage(message, message.getAllRecipients());
            t.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static boolean compruebaConexion(Context context) {
        boolean connected = false;
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] redes = connec.getAllNetworkInfo();
        for (int i = 0; i < redes.length; i++) {
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }
}

