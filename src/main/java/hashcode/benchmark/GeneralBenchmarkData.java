package hashcode.benchmark;

import hashcode.key.GeneratedKeyDataSupplier;
import hashcode.key.KeyData;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GeneralBenchmarkData {
    private static final int ENTRY_COUNT = 12_000_000;

    private List<KeyData> keyDataList = new LinkedList<>();
    Map<Object, Integer> keyElementMap = new ConcurrentHashMap<>(ENTRY_COUNT);

    private Iterator<KeyData> dataIterator;


    public void generateData() {
        new GeneratedKeyDataSupplier(ENTRY_COUNT)
            .get()
            .forEach(keyData -> {
                keyDataList.add(keyData.getValue());
                keyElementMap.put(createBenchmarkKey(keyData.getValue()), keyData.getKey());
            });
    }

    Object getNextKey() {
        return createBenchmarkKey(nextDataElement());
    }

    protected abstract Object createBenchmarkKey(KeyData keyData);

    private KeyData nextDataElement() {
        if (!dataIterator.hasNext()) {
            resetDataIterator();
        }

        return dataIterator.next();
    }

    public void resetDataIterator() {
        dataIterator = keyDataList.iterator();
    }
}
