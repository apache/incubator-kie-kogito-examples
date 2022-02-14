/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.acme.travel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.avro.reflect.ReflectData;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.dataformat.avro.AvroMapper;
import com.fasterxml.jackson.dataformat.avro.AvroSchema;

@ApplicationScoped
public class AvroUtils {

    @Inject
    AvroMapper avroMapper;

    public byte[] writeObject(Object obj) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            avroMapper.writer(getSchema(obj.getClass())).writeValue(out, obj);
            out.flush();
            return out.toByteArray();
        } catch (IOException io) {
            throw new IllegalArgumentException(io);
        }
    }

    public <T> T readObject(byte[] payload, Class<T> outputClass, Class<?>... parametrizedClasses) throws IOException {
        final JavaType type = Objects.isNull(parametrizedClasses) ? avroMapper.getTypeFactory().constructType(outputClass)
                : avroMapper.getTypeFactory().constructParametricType(outputClass, parametrizedClasses);
        return avroMapper.readerFor(type)
                .with(getSchema(outputClass))
                .readValue(payload);
    }

    private AvroSchema getSchema(Class<?> clazz) {
        return new AvroSchema(ReflectData.get().getSchema(clazz));
    }
}
