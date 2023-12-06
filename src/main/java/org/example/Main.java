package org.example;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Main {

    ConcurrentHashMap<String, List<String>> transactionGraph = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Integer> inDegreeMap = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Integer> depthMap = new ConcurrentHashMap<>();

    ConcurrentHashMap<String, Transaction> transactionMap = new ConcurrentHashMap<>();


    String hash = null;

    public void updateGraph(String txId, List<String> txIds) {
        for (String id : txIds) {
                inDegreeMap.putIfAbsent(id, 0);
                transactionGraph.putIfAbsent(id, new ArrayList<>());
                transactionGraph.get(id).add(txId);
        }
        inDegreeMap.put(txId, txIds.size());
    }


    public void updateDepth() {
        Queue<String> queue = new LinkedList<>();

        // Initialize the queue with nodes having in-degree of 0
        for (Map.Entry<String, Integer> entry : inDegreeMap.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
                depthMap.put(entry.getKey(), 0); // Initialize depth for the starting nodes
            }
        }

        while (!queue.isEmpty()) {
                String current = queue.poll();
                for (String neighbor : transactionGraph.getOrDefault(current, Collections.emptyList())) {
                    if (inDegreeMap.get(neighbor) == 0) continue;
                    inDegreeMap.put(neighbor, inDegreeMap.get(neighbor) - 1);

                    if (inDegreeMap.get(neighbor) == 0) {
                        queue.add(neighbor);
                        depthMap.put(neighbor, depthMap.get(current) + 1);

                    }
                }
            }
    }

    public void buildGraph() {

        int index = 0;
        List<Transaction> transactions = null;
        do {
            Long currentTime  = System.currentTimeMillis();
             transactions = RestUtils.getTransactions(this.hash, index);
             for (Transaction transaction: transactions) {
                if (this.hash.equals(transaction.status.block_hash)) transactionMap.put(transaction.txid, transaction);
             }
             index += transactions.size();
             System.out.println("Total time takes for index :"+ index + " - " + (System.currentTimeMillis()-currentTime));

        } while(transactions != null  && transactions.size()>0 );

        for (Map.Entry<String, Transaction> entry: transactionMap.entrySet()) {
            List<String> list = new ArrayList<>();
            for (TransactionInput t : entry.getValue().vin) {
                if (transactionMap.containsKey(t.txid)) {
                    list.add(t.txid);
                }
            }
            updateGraph(entry.getKey(), list);
        }

    }

    public  void updateHash() {
        this.hash = RestUtils.getHash();
    }

    public LinkedHashMap<String, Integer> topKAncestorTransaction(long k) {
        LinkedHashMap<String, Integer> sortedDepthMap = depthMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(k)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        return  sortedDepthMap;
    }
    public static void main(String[] args) {
        try {
            Main main = new Main();
            main.updateHash();
            main.buildGraph();
            main.updateDepth();
            System.out.println(main.topKAncestorTransaction(10));
        } catch (Exception ex) {
           ex.printStackTrace();

        }
    }
}