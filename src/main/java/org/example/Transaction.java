package org.example;

import java.util.List;
import java.util.Objects;

public class Transaction {

        String txid;

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Transaction that = (Transaction) o;
                return Objects.equals(txid, that.txid);
        }

        @Override
        public int hashCode() {
                return Objects.hash(txid);
        }

        List<TransactionInput> vin;

        TransactionStatus status;
}
