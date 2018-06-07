package com.company;


import com.sun.rowset.CachedRowSetImpl;
import static java.lang.System.out;


import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

public class Main {

    public static final String DB_NAME = "ExercicioPW1";
    public static final String CONNECTION_STRING = "jdbc:postgresql://localhost/" + DB_NAME;
    public static final String USERNAME = "postgres";
    public static final String PASSWORD = "admin";

    public static final String TABLE_CLIENT = "cliente";

    public static final String COLUMN_ID_CLIENT = "id";
    public static final String COLUMN_NAME = "nome";
    public static final String COLUMN_PHOTO = "imagem";
    public static final String COLUMN_DOCUMENT = "documento";
    public static final String COLUMN_BALANCE = "saldo";
    public static final String COLUMN_ACTIVE = "ativo";

    public static final String TABLE_ORDER = "pedido";

    public static final String COLUMN_ORDER_ID = "id";
    public static final String COLUMN_DATE = "data";
    public static final String COLUMN_CLIENT = "cliente";
    public static final String COLUMN_VALUE = "valor";


    public static void main(String[] args) {


        try {
            Connection conn = DriverManager.getConnection (CONNECTION_STRING, USERNAME, PASSWORD);
            Statement statement = conn.createStatement ( );

            statement.execute ("DROP TABLE IF EXISTS " + TABLE_CLIENT + " CASCADE ");

            statement.execute ("CREATE TABLE IF NOT EXISTS " + TABLE_CLIENT +
                    "(" + COLUMN_ID_CLIENT + " SERIAL PRIMARY KEY, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_DOCUMENT + " TEXT, " +
                    COLUMN_BALANCE + " NUMERIC(6,2) NOT NULL DEFAULT 0.00, " +
                    COLUMN_ACTIVE + " BIT" + ");");

            statement.execute ("DROP TABLE IF EXISTS " + TABLE_ORDER);

            statement.execute ("CREATE TABLE IF NOT EXISTS " + TABLE_ORDER +
                    "(" + COLUMN_ORDER_ID + " SERIAL PRIMARY KEY, " +
                    COLUMN_DATE + " DATE, " +
                    COLUMN_CLIENT + " INTEGER, " +
                    COLUMN_VALUE + " NUMERIC(6,2) NOT NULL DEFAULT 0.00," +
                    "CONSTRAINT FK_CliPed FOREIGN KEY (" + COLUMN_CLIENT + ") REFERENCES " + TABLE_CLIENT + "(" + COLUMN_ID_CLIENT + "))"
            );

            //add na tabela cliente um atributo foto como obrigatório - salvando a url da imagem
            statement.execute ("ALTER TABLE " + TABLE_CLIENT + " ADD " + COLUMN_PHOTO + " TEXT NOT NULL ");


            //PEGANDO UMA IMAGEM PARA SER SALVA NO BANCO DE DADOS

            String file = "https://cc-media-foxit.fichub.com/image/fox-it-mondofox/0d4662a5-7ece-4de9-a136-af1ee54840e8/bazinga-copertina-1-maxw-654.jpg";

            //add CLIENTE

            insertClient (statement, "Carlos", "98465165421", 3200.00, 1, file);

            //add Pedido
            insertOrder (statement, "07/06/2018", 1, 50);


//       Executando comandos SQL DDL com executeUpdate

            String sql = "UPDATE " + TABLE_ORDER + " SET " + COLUMN_VALUE + "= 45 WHERE " + COLUMN_CLIENT + " = 1";
            statement.executeUpdate (sql);

//      	Executando Consultas executeQuery e ResultSet

            ResultSet results = statement.executeQuery ("SELECT * FROM " + TABLE_CLIENT);
            while (results.next ( )) {
                System.out.println ("Nome: " + results.getString (COLUMN_NAME) + "; " +
                        "Documento: " + results.getString (COLUMN_DOCUMENT) + "; " +
                        "Link imagem: " + results.getString (COLUMN_PHOTO) + "; " +
                        "Saldo: " + results.getFloat (COLUMN_BALANCE) + "; " +
                        "Ativo: " + results.getBoolean (COLUMN_ACTIVE));
            }

            results.close ( );
            statement.close ( );
            conn.close ( );


        } catch (SQLException e) {
            e.printStackTrace ( );
        } catch (Exception e) {
            e.printStackTrace ( );
        }

    }
//   JdbcRowSet - exemplo

/*

        RowSetFactory factory = null;
        JdbcRowSet jrs = null;


        try {
            factory = RowSetProvider.newFactory ( );
            jrs = factory.createJdbcRowSet ( );
            jrs.setUrl (CONNECTION_STRING);
            jrs.setUsername (USERNAME);
            jrs.setPassword (PASSWORD);
            String sql = "SELECT * FROM " + TABLE_ORDER;
            jrs.setCommand (sql);
            jrs.execute ( );
            while (jrs.next ( )) {

                System.out.println ("Data: " + jrs.getString (2));
                System.out.println ("Cod. Cliente: " + jrs.getString (3));
                System.out.println ("Saldo: " + jrs.getString (4));
            }
        } catch (SQLException e) {
            e.printStackTrace ( );
        }
    }
*/


//CachedRowSet - exemplo

/*
        try {
            Class.forName ("org.postgresql.Driver");

            Connection conn = DriverManager.getConnection (CONNECTION_STRING, USERNAME, PASSWORD);
            Statement stat = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String sql = "SELECT FROM " + TABLE_ORDER;
            ResultSet rs = stat.executeQuery (sql);
            CachedRowSet wrs = new CachedRowSetImpl ();
            wrs.populate (rs);
            stat.close ();
            conn.close ();

            while(wrs.next()){
                out.printf("%s: %d - %s: %d \n", wrs.getMetaData().getColumnName(1), wrs.getInt(1), wrs.getMetaData().getColumnName(2), wrs.getInt(2));
                wrs.updateFloat (3, 5000);
                wrs.updateRow(); // efetiva a atualização sobre o modelo
            }
            wrs.absolute(2); // posiciona o cursor sobre a segunda linha

            while (wrs.next ( )) {

                System.out.println ("Data: " + wrs.getString (2));
                System.out.println ("Cod. Cliente: " + wrs.getString (3));
                System.out.println ("Saldo: " + wrs.getString (4));
            }


        } catch (SQLException e) {
            e.printStackTrace ( );
        } catch (ClassNotFoundException e) {
            e.printStackTrace ( );
        }


    }*/

        private static void insertClient (Statement statement, String nome, String documento,double saldo,
        int ativo, String imagem) throws Exception {
            statement.execute ("INSERT INTO " + TABLE_CLIENT +
                    " (" + COLUMN_NAME + ", " +
                    COLUMN_PHOTO + ", " +
                    COLUMN_DOCUMENT + ", " +
                    COLUMN_BALANCE + ", " +
                    COLUMN_ACTIVE +
                    " )" +
                    " VALUES ('" + nome + "', '" + imagem + "', '" + documento + "', " + saldo + ", '" + ativo + "')");

        }

        private static void insertOrder (Statement statement, String data,int cod_cliente, double saldo) throws
        SQLException {
            statement.execute ("INSERT INTO " + TABLE_ORDER +
                    " (" + COLUMN_DATE + ", " +
                    COLUMN_CLIENT + ", " +
                    COLUMN_VALUE +
                    ") " +
                    " VALUES ('" + data + "', " + cod_cliente + ", " + saldo + ")");
        }
}
