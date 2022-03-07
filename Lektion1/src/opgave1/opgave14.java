package opgave1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.sql.*;

public class opgave14 {
    /**
     * @param args
     */
    static Connection minConnection;
    static Statement stmt;
    static BufferedReader inLine;

    public static void selectudenparm() {
        try {
            // Laver sql-sætning og får den udført
            String sql = "select navn,stilling from person";
            System.out.println("SQL-streng er "+sql);
            ResultSet res=stmt.executeQuery(sql);
            // gennemløber svaret
            while (res.next()) {
                String s;
                s = res.getString("navn");
                System.out.println(s + "    " + res.getString(2));
            }
            // pæn lukning
            if (!minConnection.isClosed())
                minConnection.close();
        }
        catch (Exception e) {
            System.out.println("fejl:  "+e.getMessage());
        }
    }

    public static void selectmedparm() {
        try {
            // Indlæser søgestreng
            System.out.println("Indtast søgestreng");
            String inString = inLine.readLine();
            // Laver sql-sætning og får den udført
            String sql = "select aarstal,init, plac from placering where init like '" + inString + "%'";
            System.out.println("SQL-streng er "+ sql);
            ResultSet res=stmt.executeQuery(sql);
            //gennemløber svaret
            while (res.next()) {
                if(res.getString(3)!=null){
                    System.out.println(res.getString(1) + "    " + res.getString(2) + "    " + res.getString(3));
                }else{
                    System.out.println(res.getString(1) + "    " + res.getString(2) + "    " + "UDGÅET");
                }
            }
            // pæn lukning
            if (!minConnection.isClosed()) minConnection.close();
        }
        catch (Exception e) {
            System.out.println("fejl:  "+e.getMessage());
        }
    }

    public static void insertmedstring() {
        try {
            // indlæsning
            System.out.println("Vi vil nu oprette et nyt resultat for en rytter");
            System.out.println("Indtast aarstal (VM skal være oprettet på forhånd");
            String aarstalstr=inLine.readLine();
            System.out.println("Indtast init (rytter skal være oprettet på forhånd");
            String initstr=inLine.readLine();
            System.out.println("Indtast placering, er han/hun udgået indtast null");
            String placeringstr = inLine.readLine();


            // sender insert'en til db-serveren
            String sql = "insert into placering values ('" + aarstalstr + "','" + initstr + "'," + placeringstr +")";
            System.out.println("SQL-streng er "+ sql);
            stmt.execute(sql);
            // pænt svar til brugeren
            System.out.println("Resultatet er nu registreret");
            if (!minConnection.isClosed()) minConnection.close();
        }
        catch (SQLException e) {
            switch (e.getErrorCode())
            // fejl-kode 547 svarer til en foreign key fejl
            { case 547 : {if (e.getMessage().contains("rytterinitconstraint"))
                System.out.println("rytter er ikke oprettet");
            else
            if (e.getMessage().contains("aarstalconstraint"))
                System.out.println("vm er ikke oprettet");
            else
                System.out.println("ukendt fremmednøglefejl");
                break;
            }
            // fejl-kode 2627 svarer til primary key fejl
                case 2627: {System.out.println("den pågældende resultat er allerede oprettet");
                    break;
                }
                default: System.out.println("fejlSQL:  "+e.getMessage());
            };
        }
        catch (Exception e) {
            System.out.println("fejl:  "+e.getMessage());
        }
    };

    public static void insertprepared() {
        try {
            // indl�sning
            System.out.println("Vi vil nu oprette et nyt resultat");
            System.out.println("Indtast aarstal (VM skal være oprettet på forhånd");
            String aarstalstr=inLine.readLine();
            System.out.println("Indtast initialer (Rytter skal være oprettet på forhånd");
            String initstr=inLine.readLine();
            System.out.println("Indtast placering, er han/hun udgået indtast null");
            String placeringint =inLine.readLine();
            // Anvendelse af prepared statement
            String sql = "insert into placering values (?,?,?)";
            PreparedStatement prestmt = minConnection.prepareStatement(sql);
            prestmt.clearParameters();
            prestmt.setString(1,aarstalstr);
            prestmt.setString(2,initstr);
            if(!placeringint.equals("null")){
                prestmt.setInt(3,Integer.parseInt(placeringint));
            }else{
                prestmt.setNull(3, Types.INTEGER);
            }

            // Udf�rer s�tningen
            prestmt.execute();
            // p�nt svar til brugeren
            System.out.println("Resultatet er nu registreret");
            if (!minConnection.isClosed()) minConnection.close();
        }
        catch (SQLException e) {
            switch (e.getErrorCode())
            // fejl-kode 547 svarer til en foreign key fejl
            { case 547 : {if (e.getMessage().contains("rytterinitconstraint"))
                System.out.println("rytter er ikke oprettet");
            else
            if (e.getMessage().contains("aarstalconstraint"))
                System.out.println("vm er ikke oprettet");
            else
                System.out.println("ukendt fremmednøglefejl");
                break;
            }
            // fejl-kode 2627 svarer til primary key fejl
                case 2627: {System.out.println("den pågældende resultat er allerede oprettet");
                    break;
                }
                default: System.out.println("fejlSQL:  "+e.getMessage());
            };
        }
        catch (Exception e) {
            System.out.println("fejl:  "+e.getMessage());
        }
    };

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try {
            inLine = new BufferedReader(new InputStreamReader(System.in));
            //generel opsætning
            //via native driver
            String server="localhost\\SQLEXPRESS"; //virker måske hos dig
            //virker det ikke - prøv kun med localhost
            String dbnavn="cykelrytter";            //virker måske hos dig
            String login="sa";                      //skal ikke ændres
            String password="123";            		//skal ændres
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            minConnection = DriverManager.getConnection("jdbc:sqlserver://"+server+";databaseName="+dbnavn+
                    ";user=" + login + ";password=" + password + ";");
            //minConnection = DriverManager.getConnection("jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=eksempeldb;user=sa;password=torben07;");
            stmt = minConnection.createStatement();
            //Indlæsning og kald af den rigtige metode
            System.out.println("Indtast  ");
            System.out.println("s for select uden parameter  ");
            System.out.println("sp for select med parameter  ");
            System.out.println("i for insert med strengmanipulation");
            System.out.println("ps for insert med prepared statement ");
            String in=inLine.readLine();
            switch (in)
            {case "s"  : {selectudenparm();break;}
                case "sp" : {selectmedparm();break;}
                case "i"  : {insertmedstring();break;}
                case "ps"  : {insertprepared();break;}
                default : System.out.println("ukendt indtastning");
            }
        }
        catch (Exception e) {
            System.out.println("fejl:  "+e.getMessage());
        }
    }

}
