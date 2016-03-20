package com.steeve.steeveapp;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class JSONDecoder {

    /*public static String [] getTransactionsData(String inputString, Integer position) throws  JSONException {

        //JSONParser parser = new JSONParser();
        String s = inputString;
        Object obj = null;

        try {
            obj = parser.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONArray array = (JSONArray) obj;
        JSONObject obj2 = (JSONObject)array.get(position);
        String user = (String) obj2.get("user");
        String debtAmount = (String) obj2.get("debtAmount");
        String receiver = (String) obj2.get("receiver");
        String [] transactionData = new String[] {user, debtAmount, receiver};
        return transactionData;
    }*/

    public static String [] getTransactionUsersData (String inputString) throws  JSONException {

        //JSONParser parser = new JSONParser();
        String [] userArray;
        Integer transactionsCount;
        JSONArray reader;

        reader = new JSONArray(inputString);
        transactionsCount = reader.length();
        userArray = new String[transactionsCount];
        for (Integer i=0; i<transactionsCount; i++) {
            JSONObject obj2 = (JSONObject)reader.get(i);
            String user = (String) obj2.get("user");
            userArray[i] = user;
        }
        return userArray;
    }

    public static String [] getTransactionReceiversData (String inputString) throws  JSONException {

        //JSONParser parser = new JSONParser();
        String [] receiverArray;
        Integer transactionsCount;
        JSONArray reader;

        reader = new JSONArray(inputString);
        transactionsCount = reader.length();
        receiverArray = new String[transactionsCount];
        for (Integer i=0; i<transactionsCount; i++) {
            JSONObject obj2 = (JSONObject)reader.get(i);
            String user = (String) obj2.get("receiver");
            receiverArray[i] = user;
        }
        return receiverArray;
    }

    public static String [] getTransactionDebtAmountsData (String inputString) throws  JSONException {

        //JSONParser parser = new JSONParser();
        String [] debtAmountsArray;
        Integer transactionsCount;
        JSONArray reader;

        reader = new JSONArray(inputString);
        transactionsCount = reader.length();
        debtAmountsArray = new String[transactionsCount];
        for (Integer i=0; i<transactionsCount; i++) {
            JSONObject obj2 = (JSONObject)reader.get(i);
            String user = (String) obj2.get("debtAmount");
            debtAmountsArray[i] = user;
        }
        return debtAmountsArray;
    }

    public static String [] getTransactionDateStampsData (String inputString) throws  JSONException {

        //JSONParser parser = new JSONParser();
        String [] dateStampsArray;
        Integer transactionsCount;
        JSONArray reader;

        reader = new JSONArray(inputString);
        transactionsCount = reader.length();
        dateStampsArray = new String[transactionsCount];
        for (Integer i=0; i<transactionsCount; i++) {
            JSONObject obj2 = (JSONObject)reader.get(i);
            String user = (String) obj2.get("time");
            dateStampsArray[i] = user;
        }
        return dateStampsArray;
    }

    public static Float [] getUserSummary(int userId, String dbDataSummary) throws JSONException {
        JSONArray reader;

        reader = new JSONArray(dbDataSummary);
        String s3 = reader.toString();
        Log.v("Parsed SummaryJSON", "Parsed string: " + s3);
        JSONObject obj2 = (JSONObject) reader.get(userId);
        Float pandoAmount = Float.parseFloat((String) obj2.get("Pando"));
        Float rimoAmount =  Float.parseFloat((String) obj2.get("Rimo"));
        Float neriAmount =  Float.parseFloat((String) obj2.get("Neri"));
        Float romanAmount = Float.parseFloat((String) obj2.get("Roman"));
        return new Float[]{pandoAmount, rimoAmount, neriAmount, romanAmount};
    }

    public static String[] getUserShoppingData(int userId, String dbShoppingData) throws JSONException {
        JSONArray reader;

        reader = new JSONArray(dbShoppingData);
        String s3 = reader.toString();
        Log.v("Parsed shopping data", "Parsed string: " + s3);
        JSONObject obj2 = (JSONObject) reader.get(userId);
        String user = (String) obj2.get("User");
        String need = (String) obj2.get("Need");
        String amount = (String) obj2.get("Amount");
        return new String[]{user, need, amount};
    }

    public static String[] getTokenSet( String retrievedTokenSetString) throws JSONException {

            //JSONParser parser = new JSONParser();
        String [] tokenSet;
            Integer tokenCount;
            JSONArray reader;

            reader = new JSONArray(retrievedTokenSetString);
            tokenCount = reader.length();
            tokenSet = new String[tokenCount];
            for (Integer i=0; i<tokenCount; i++) {
                JSONObject obj2 = (JSONObject)reader.get(i);
                String token = (String) obj2.get("RegID");
                tokenSet[i] = token;
            }
            return tokenSet;
        }

    public static String [] getRemoteApkVersion ( String retrievedVersionCodeString) throws JSONException {
        String remoteVersionCode;
        String changeLogText;
        JSONArray reader;

        reader = new JSONArray(retrievedVersionCodeString);
        JSONObject obj2 = (JSONObject)reader.get(0);
        remoteVersionCode = (String) obj2.get("version_code");
        changeLogText = (String) obj2.get("changelog");

        return new String [] {remoteVersionCode, changeLogText};
    }
}