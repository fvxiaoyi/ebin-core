package core.framework.db.query;

import core.framework.utils.json.JSON;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author ebin
 */
public class AliasToJSONBeanTransformer extends AliasedTupleSubsetResultTransformer {
    private final Class<?> resultClass;
    private final Validator validator;

    public AliasToJSONBeanTransformer(Class<?> resultClass, Validator validator) {
        this.resultClass = resultClass;
        this.validator = validator;
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
        Object returnValue = JSON.fromJSON(resultClass, JSON.toJSON(map));
        if (this.validator != null) {
            Set<ConstraintViolation<Object>> result = this.validator.validate(returnValue);
            if (!result.isEmpty()) {
                throw new ConstraintViolationException(result);
            }
        }
        return returnValue;
    }

    @Override
    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return false;
    }
}
