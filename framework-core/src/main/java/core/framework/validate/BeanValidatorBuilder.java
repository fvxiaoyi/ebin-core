package core.framework.validate;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.lang.reflect.InvocationTargetException;

/**
 * @author ebin
 */
public class BeanValidatorBuilder {
    private static ClassPool pool;

    static {
        pool = new ClassPool(null);
        pool.appendSystemPath();
    }

    private final CtClass classBuilder;
    private final Class<?> beanClass;

    public BeanValidatorBuilder(Class<?> beanClass) {
        this.beanClass = beanClass;
        String className = BeanValidator.class.getName() + "&" + beanClass.getSimpleName();
        this.classBuilder = pool.makeClass(className);
        try {
            classBuilder.addInterface(pool.get(BeanValidator.class.getName()));
            CtConstructor constructor = new CtConstructor(null, classBuilder);
            constructor.setBody(";");
            classBuilder.addConstructor(constructor);
        } catch (NotFoundException | CannotCompileException e) {
            throw new Error(e);
        }
    }

    public BeanValidator build() {
        BeanValidationSource source = new BeanValidationSource(this.beanClass);
        source.getFields().forEach(field -> {
            try {
                classBuilder.addField(CtField.make(field, classBuilder));
            } catch (CannotCompileException e) {
                throw new Error(e);
            }
        });

        source.getMethods().forEach(method -> {
            try {
                classBuilder.addMethod(CtMethod.make(method, classBuilder));
            } catch (CannotCompileException e) {
                throw new Error(e);
            }
        });

        try {
            Class<BeanValidator> targetClass = (Class<BeanValidator>) classBuilder.toClass(BeanValidator.class);
            classBuilder.detach();
            return targetClass.getConstructor().newInstance();
        } catch (CannotCompileException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new Error(e);
        }
    }
}
