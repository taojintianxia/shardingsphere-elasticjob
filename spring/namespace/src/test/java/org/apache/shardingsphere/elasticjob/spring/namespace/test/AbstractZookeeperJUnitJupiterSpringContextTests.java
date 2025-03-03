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

package org.apache.shardingsphere.elasticjob.spring.namespace.test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Background reference AbstractJUnit4SpringContextTests
 * and <a href="https://github.com/spring-projects/spring-framework/issues/29149">spring-projects/spring-framework#29149</a>.
 *
 * @see org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests
 */
@ExtendWith(SpringExtension.class)
@TestExecutionListeners(listeners = EmbedZookeeperTestExecutionListener.class, inheritListeners = false, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public abstract class AbstractZookeeperJUnitJupiterSpringContextTests {
}
