package io.netty.handler.codec.serialization;

class StaticClassResolver implements ClassResolver {

    StaticClassResolver(Object loaderToken) {
    }

    @Override
    public Class<?> resolve(String className) throws ClassNotFoundException {
        throw new ClassNotFoundException(className);
    }

}
