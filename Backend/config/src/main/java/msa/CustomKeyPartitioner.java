package msa;

import org.infinispan.distribution.ch.impl.HashFunctionPartitioner;

public class CustomKeyPartitioner extends HashFunctionPartitioner {
    @Override
    public int getSegment(Object key) {
        int routingValue = 0;
        if (key instanceof  String) {
            routingValue = extractIncidentId((String) key);
        }
        if (key instanceof Integer) {
            routingValue = (Integer) key;
        }

        return routingValue % super.numSegments;
    }

    private int extractIncidentId(String key) {
        if (!key.contains("_")) {
            throw new IllegalArgumentException("Invalid incident id: " + key);
        }

        return Integer.parseInt(key.substring(0, key.indexOf('_')));
    }
}
