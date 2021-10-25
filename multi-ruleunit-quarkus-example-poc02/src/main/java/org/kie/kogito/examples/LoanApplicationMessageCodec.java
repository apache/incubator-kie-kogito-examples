/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.examples;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class LoanApplicationMessageCodec implements MessageCodec<LoanApplication, LoanApplication> {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void encodeToWire(Buffer buffer, LoanApplication loanApplication) {
        String jsonStr;
        try {
            jsonStr = objectMapper.writeValueAsString(loanApplication);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        buffer.appendInt(jsonStr.getBytes().length);
        buffer.appendString(jsonStr);
    }

    @Override
    public LoanApplication decodeFromWire(int position, Buffer buffer) {
        int _pos = position;
        int length = buffer.getInt(_pos);

        String jsonStr = buffer.getString(_pos += 4, _pos += length);

        LoanApplication loanApplication;
        try {
            loanApplication = objectMapper.readValue(jsonStr, new TypeReference<LoanApplication>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return loanApplication;
    }

    @Override
    public LoanApplication transform(LoanApplication loanApplication) {
        return loanApplication;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }

}
