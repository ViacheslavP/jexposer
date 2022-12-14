package org.viapivov.exposer.advice;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.viapivov.exposer.container.WeakCollection;

import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtField.Initializer;
import javassist.Modifier;
import javassist.NotFoundException;

class TargetClassTransformer implements ClassFileTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TargetClassTransformer.class);

    static final String RPC_INSTANCES = "__$RPC_INSTANCES__";
    private static final Class<?> TARGET_COLLECTION = WeakCollection.class;
    private final String fqn;

    public TargetClassTransformer(String fqn) {
        this.fqn = fqn;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (!fqn.replace('.', '/').equals(className)) {
            return null;
        }
        LOGGER.debug("[" + fqn + "]" + "...transformation begins");

        ClassPool pool = ClassPool.getDefault();
        CtClass klass;
        CtClass collectionType;
        try {
            if (classfileBuffer != null) {
                pool.appendClassPath(new ByteArrayClassPath(fqn, classfileBuffer));
            } else if (classBeingRedefined != null) {
                pool.appendClassPath(new ClassClassPath(classBeingRedefined));
            }
            klass = pool.get(fqn);
            pool.appendClassPath(new ClassClassPath(TARGET_COLLECTION));
            collectionType = pool.get(TARGET_COLLECTION.getName());
        } catch (NotFoundException nfe) {
            LOGGER.error("[" + fqn + "]" + "transformation breaks ", nfe);
            throw new RuntimeException("Class " + fqn + " not found", nfe);
        }

        CtField collectionField;
        try {
            collectionField = new CtField(collectionType, RPC_INSTANCES, klass);
            collectionField.setModifiers(Modifier.STATIC | Modifier.PUBLIC);
            klass.addField(collectionField, Initializer.byNew(collectionType));
        } catch (CannotCompileException cce) {
            LOGGER.error("[" + fqn + "]" + "transformation breaks ", cce);

            throw new AdviceInternalError(cce);
        }

        try {
            for (CtConstructor constructor : klass.getConstructors()) {
                String constructorString = String.format("%s.add(this);", RPC_INSTANCES);
                constructor.insertAfter(constructorString);
            }
        } catch (CannotCompileException cce) {
            LOGGER.error("[" + fqn + "]" + "transformation breaks ", cce);

            throw new AdviceInternalError(cce);
        }
        byte[] newBytes;
        try {
            newBytes = klass.toBytecode();
        } catch (IOException | CannotCompileException cce) {
            LOGGER.error("[" + fqn + "]" + "transformation breaks ", cce);
            throw new AdviceInternalError(cce);
        }
        LOGGER.debug("[" + fqn + "]" + "...transformation complete");
        return newBytes;
    }
}
