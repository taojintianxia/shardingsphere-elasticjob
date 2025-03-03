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

package org.apache.shardingsphere.elasticjob.infra.concurrent;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Block utility.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BlockUtils {
    
    /**
     * Wait short time.
     */
    public static void waitingShortTime() {
        sleep(100L);
    }
    
    /**
     * Sleep for the specified number of milliseconds.
     *
     * @param  millis the duration of sleep in milliseconds
     */
    public static void sleep(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
