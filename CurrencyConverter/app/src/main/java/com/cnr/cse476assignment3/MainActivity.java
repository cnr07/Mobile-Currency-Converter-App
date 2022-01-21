package com.cnr.cse476assignment3;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    double amount;
    TextView resultCurrency;
    EditText leftListSearch,rightListSearch,amountCurrency;
    String resultOfSymbolsXML="";
    String resultOfConvert="";
    static int choice=0;
    ListView l, l2;int indexOfCurrencies=0;
    ArrayAdapter<String> arr,arr1;

    List<String> Currencies = new ArrayList<>();
    /*String CurrenciesArr[]
            = { "Algorithms", "Data Structures",
            "Languages", "Interview Corner",
            "GATE", "ISRO CS",
            "UGC NET CS", "CS Subjects",
            "Web Technologies", "CS Subjects", "CS Subjects", "CS Subjects", "CS Subjects", "CS Subjects" };*/





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        resultCurrency=findViewById(R.id.resultCurrency);
        amountCurrency=findViewById(R.id.amountCurrency);
        leftListSearch=findViewById(R.id.leftListSearch);
        rightListSearch=findViewById(R.id.rightListSearch);
        l2=findViewById(R.id.list2);
        l = findViewById(R.id.list);



        ////


        if(checkNetworkConnection()){
            //new HTTPAsyncTask().execute("http://www.persystlab.org/");

            new HTTPAsyncTask().execute("https://api.exchangerate.host/symbols?format=xml");


        }













    }
    ///////////


    public void XMLParser () throws XmlPullParserException, IOException{

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp =factory.newPullParser();

        if(choice==1){
            xpp.setInput(new StringReader(resultOfConvert));

        }
        else{
            xpp.setInput(new StringReader(resultOfSymbolsXML));

        }



        int eventType = xpp.getEventType();
        String result="";String checker="";
        while(eventType!=XmlPullParser.END_DOCUMENT){

            if(eventType==XmlPullParser.START_DOCUMENT){
                //result="Document Begining";

            }
            else if(eventType==XmlPullParser.END_DOCUMENT){
                //result+=System.getProperty("line.separator")+"End of Document";
            }
            else if(eventType==XmlPullParser.START_TAG){
                /*result+=System.getProperty("line.separator")+"Start tag: "+xpp.getName();
                String checker = xpp.getName();
                if(checker.equals("code")){
                    String comment = xpp.getAttributeValue(null,"comment");
                    result+=System.getProperty("line.separator")+"Comment: "+comment;
                }*/
                checker = xpp.getName();


            }
            else if(eventType==XmlPullParser.END_TAG){
                //result+=System.getProperty("line.separator")+"End tag: "+xpp.getName();
            }
            else if(eventType==XmlPullParser.TEXT){
                //result+=System.getProperty("line.separator")+"Text: "+xpp.getText();
                if(checker.equals("code")){
                    String code = xpp.getText();
                    int isEq=0;
                    for(int i=0;i<indexOfCurrencies;i++){
                        if(Currencies.contains(code)){
                            isEq=1;
                            break;
                        }
                    }
                    if(isEq==0){
                        Currencies.add(code);
                        indexOfCurrencies++;
                        //System.out.println(code+" "+indexOfCurrencies);

                    }
                    //result+=System.getProperty("line.separator")+"Code: "+code;
                    //System.out.println(result);
                }
                else if(checker.equals("result")){
                    String resultt=xpp.getText();
                    String s="";
                    if (resultt.contains(",")){
                        s=resultt.replaceAll(",",".");
                    }else{
                        s=resultt;
                    }
                    try {
                        double dresult=Double.parseDouble(s);
                        dresult*=amount;
                        System.out.println(dresult+"Bibakalım");
                        String ss=Double.toString(dresult);
                        resultCurrency.setText(ss);
                        amountCurrency.setText("");
                        amount=0;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }


                }
            }

            eventType=xpp.next();
        }
        //tv1.setText(result);

        choice=1;
    }



    ///////////

    public boolean checkNetworkConnection(){
        ConnectivityManager connMgr=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if(networkInfo!=null && (isConnected=networkInfo.isConnected())){
            //connected
        } else{
            //not connected
        }
        return isConnected;
    }

    private class HTTPAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {

            try {
                return HTTPGet(urls[0]);
            }catch (IOException e){
                return "Unable to retrieve web page. URL may be invalid.";

            }

        }

        @Override
        protected void onPostExecute(String result){
            /* result: aldığım xml*/
            if(resultOfSymbolsXML==""){
                //choice=1;
                resultOfSymbolsXML=result;
                System.out.println(resultOfSymbolsXML);
                try {
                    XMLParser();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String CurrenciesArr[] = new String[indexOfCurrencies];
                for(int i=0;i<indexOfCurrencies;i++){
                    int m= i+1;
                    System.out.println(Currencies.get(i)+" "+m);
                    CurrenciesArr[i]=Currencies.get(i);
                }

                arr = new ArrayAdapter<String>(
                        MainActivity.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        CurrenciesArr);
                arr1 = new ArrayAdapter<String>(
                        MainActivity.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        CurrenciesArr);

                l.setAdapter(arr);
                l2.setAdapter(arr1);

                l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //System.out.println(tutorials[i]);
                        System.out.println(arr.getItem(i));
                        leftListSearch.setText(arr.getItem(i));
                    }
                });
                l2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        System.out.println(arr1.getItem(i));
                        rightListSearch.setText(arr1.getItem(i));
                    }
                });




                leftListSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        arr.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }

                });

                rightListSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        arr1.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }

                });





            }
            else{
                resultOfConvert=result;
                System.out.println(resultOfConvert);
                try {
                    XMLParser();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



        }
    }

    private String HTTPGet(String myUrl) throws IOException{
        InputStream inputStream = null;
        String result="";

        URL url = new URL(myUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.connect();

        inputStream=conn.getInputStream();

        if(inputStream!=null){
            result=convertInputStreamToString(inputStream);
        }
        else{
            result="Did not work!";
        }
        return result;

    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line="";
        String result = "";
        while((line=bufferedReader.readLine()) != null){
            result+=line;
        }
        inputStream.close();
        return result;
    }

    public void convertCurrency(View view) {


        String from=leftListSearch.getText().toString();
        String to=rightListSearch.getText().toString();
        int isLeftOkk=0;int isRightOkk=0;
        for(int m=0;m<indexOfCurrencies;m++){
            String left=from.toUpperCase();
            String right=to.toUpperCase();
            if(Currencies.contains(left)){
                isLeftOkk=1;
            }
            if(Currencies.contains(right)){
                isRightOkk=1;
            }
        }



        String add="https://api.exchangerate.host/convert?from="+from+"&to="+to+"&format=xml";

        //new HTTPAsyncTask().execute("https://api.exchangerate.host/convert?from=EUR&to=TRY&format=xml");
        try {
            String s=amountCurrency.getText().toString();
            String ss="";
            if(s.contains(",")){
                ss=s.replaceAll(",",".");
            }else{
                ss=s;
            }
            amount=Double.parseDouble(ss);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Invalid Amount Currency Input!!", Toast.LENGTH_SHORT).show();

        }

        if(isLeftOkk==1 && isRightOkk==1){
            new HTTPAsyncTask().execute(add);
        }else{
            Toast.makeText(getApplicationContext(),"Invalid Currency Input!!", Toast.LENGTH_SHORT).show();
        }



        //resultCurrency.setText(amount);
        System.out.println(amount);

    }
}