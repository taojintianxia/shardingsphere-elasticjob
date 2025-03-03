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

package org.apache.shardingsphere.elasticjob.infra.exception;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class JobSystemExceptionTest {
    
    @Test
    void assertGetMessage() {
        assertThat(new JobSystemException("message is: '%s'", "test").getMessage(), is("message is: 'test'"));
    }
    
    @Test
    void assertGetMessageCause() {
        JobSystemException jobSystemException = new JobSystemException("message is: ", new RuntimeException());
        assertThat(jobSystemException.getMessage(), is("message is: "));
        assertThat(jobSystemException.getCause(), instanceOf(RuntimeException.class));
    }
    
    @Test
    void assertGetCause() {
        assertThat(new JobSystemException(new RuntimeException()).getCause(), instanceOf(RuntimeException.class));
    }
}
