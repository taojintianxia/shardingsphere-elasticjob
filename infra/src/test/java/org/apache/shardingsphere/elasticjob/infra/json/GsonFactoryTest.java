/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.elasticjob.infra.json;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GsonFactoryTest {
    
    @Test
    void assertGetGson() {
        assertThat(GsonFactory.getGson(), is(GsonFactory.getGson()));
    }
    
    @Test
    void assertRegisterTypeAdapter() {
        Gson beforeRegisterGson = GsonFactory.getGson();
        GsonFactory.registerTypeAdapter(GsonFactoryTest.class, new TypeAdapter() {
            
            @Override
            public Object read(final JsonReader in) {
                return null;
            }
            
            @Override
            public void write(final JsonWriter out, final Object value) throws IOException {
                out.jsonValue("test");
            }
        });
        assertThat(beforeRegisterGson.toJson(new GsonFactoryTest()), is("{}"));
        assertThat(GsonFactory.getGson().toJson(new GsonFactoryTest()), is("test"));
        GsonFactory.clean();
    }
    
    @Test
    void assertGetJsonParser() {
        assertThat(GsonFactory.getJsonParser(), is(GsonFactory.getJsonParser()));
    }
    
    @Test
    void assertParser() {
        String json = "{\"name\":\"test\"}";
        assertThat(GsonFactory.getJsonParser().parse(json).getAsJsonObject().get("name").getAsString(), is("test"));
    }
    
    @Test
    void assertParserWithException() {
        assertThrows(JsonParseException.class, () -> {
            String json = "{\"name\":\"test\"";
            assertThat(GsonFactory.getJsonParser().parse(json).getAsJsonObject().get("name").getAsString(), is("test"));
        });
    }
}
