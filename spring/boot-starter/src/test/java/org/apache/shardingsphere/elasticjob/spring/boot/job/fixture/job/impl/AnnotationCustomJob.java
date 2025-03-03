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

package org.apache.shardingsphere.elasticjob.spring.boot.job.fixture.job.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.annotation.ElasticJobConfiguration;
import org.apache.shardingsphere.elasticjob.annotation.ElasticJobProp;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.spring.boot.job.fixture.job.CustomJob;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(rollbackFor = Exception.class)
@ElasticJobConfiguration(
        jobName = "annotationCustomJobSchedule",
        description = "desc",
        shardingTotalCount = 3,
        shardingItemParameters = "0=a,1=b,2=c",
        cron = "*/1 * * * * ?",
        props = {
                @ElasticJobProp(key = "print.title", value = "test title"),
                @ElasticJobProp(key = "print.content", value = "test content")
        })
public class AnnotationCustomJob implements CustomJob {
    
    @Getter
    private static volatile boolean completed;
    
    @Override
    public void execute(final ShardingContext shardingContext) {
        log.info("AnnotationCustomJob execut");
        completed = true;
    }
    
    /**
     * Set completed to false.
     */
    public static void reset() {
        completed = false;
    }
}
