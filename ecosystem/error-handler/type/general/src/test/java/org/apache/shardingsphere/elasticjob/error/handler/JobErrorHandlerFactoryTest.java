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

package org.apache.shardingsphere.elasticjob.error.handler;

import org.apache.shardingsphere.elasticjob.error.handler.general.LogJobErrorHandler;
import org.apache.shardingsphere.elasticjob.error.handler.general.ThrowJobErrorHandler;
import org.apache.shardingsphere.elasticjob.infra.exception.JobConfigurationException;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JobErrorHandlerFactoryTest {
    
    @Test
    void assertGetDefaultHandler() {
        assertThat(JobErrorHandlerFactory.createHandler("", new Properties()).orElse(null), instanceOf(LogJobErrorHandler.class));
    }
    
    @Test
    void assertGetInvalidHandler() {
        assertThrows(JobConfigurationException.class, () -> JobErrorHandlerFactory.createHandler("INVALID", new Properties()).orElseThrow(() -> new JobConfigurationException("")));
    }
    
    @Test
    void assertGetHandler() {
        assertThat(JobErrorHandlerFactory.createHandler("THROW", new Properties()).orElse(null), instanceOf(ThrowJobErrorHandler.class));
    }
}
