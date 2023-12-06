package org.example;

import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class RestUtils {

    public  static  RestTemplate restTemplate = new RestTemplate();
    public static String getHash() {
        String hash = restTemplate.getForObject("https://blockstream.info/api/block-height/680000", String.class);
        return hash;
    }

    public static List<Transaction> getTransactions(String hash, int index) {
        try {
            String jsonString = restTemplate.getForObject("https://blockstream.info/api/block/"+hash+"/txs/"+index, String.class);


            Type transactionListType = new TypeToken<List<Transaction>>() {}.getType();

            // Convert the JSON string to a List<Transaction>
            return new Gson().fromJson(jsonString, transactionListType);
        } catch (Exception ex) {

            return new ArrayList<>();
        }

    }

    public static TransactionStatus getTransactionStatus(String id) {
        String transactionStatus = restTemplate.getForObject("https://blockstream.info/api//tx/"+ id+"/status", String.class);
        return new Gson().fromJson(transactionStatus, TransactionStatus.class);
    }

}
