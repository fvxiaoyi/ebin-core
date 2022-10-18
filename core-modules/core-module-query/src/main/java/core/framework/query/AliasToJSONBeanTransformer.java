package core.framework.query;

import core.framework.json.JSON;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ebin
 */
public class AliasToJSONBeanTransformer extends AliasedTupleSubsetResultTransformer {
    private final Class<?> resultClass;

    public AliasToJSONBeanTransformer(Class<?> resultClass) {
        this.resultClass = resultClass;
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Map<Object, Object> map = new HashMap<>(tuple.length);
        for (int i = 0; i < tuple.length; i++) {
            String alias = aliases[i];
            if (alias != null) {
                map.put(alias, tuple[i]);
            }
        }
        return JSON.fromJSON(resultClass, JSON.toJSON(map));
    }

    @Override
    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return false;
    }
}
