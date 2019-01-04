package hashcode.benchmark;

import hashcode.key.GeneratedKeyDataSupplier;
import hashcode.key.KeyData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GeneralBenchmarkData {
    private static final int ENTRY_COUNT = 12_000_000;

    List<KeyData> keyDataList = new ArrayList<>(ENTRY_COUNT);
    Map<Object, Integer> keyElementMap = new ConcurrentHashMap<>(ENTRY_COUNT);

    private Iterator<KeyData> dataIterator;


    public void generateData() {
        new GeneratedKeyDataSupplier(ENTRY_COUNT)
            .get()
            .forEach(keyData -> {
                keyDataList.add(keyData.getKey(), keyData.getValue());
                keyElementMap.put(createBenchmarkKey(keyData.getValue()), keyData.getKey());
            });
    }


    protected abstract Object createBenchmarkKey(KeyData keyData);


    public void resetDataIterator() {
        dataIterator = keyDataList.iterator();
    }

    public KeyData nextDataElement() {
        if (!dataIterator.hasNext()) {
            resetDataIterator();
        }

        return dataIterator.next();
    }
}
