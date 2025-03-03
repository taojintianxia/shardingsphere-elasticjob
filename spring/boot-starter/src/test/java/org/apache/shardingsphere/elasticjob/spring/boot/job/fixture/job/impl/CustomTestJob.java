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

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.spring.boot.job.fixture.job.CustomJob;
import org.apache.shardingsphere.elasticjob.spring.boot.job.repository.BarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class CustomTestJob implements CustomJob {
    
    @Autowired
    private BarRepository barRepository;
    
    public CustomTestJob() {
        log.info("CustomTestJob init");
    }
    
    @Override
    public void execute(final ShardingContext shardingContext) {
        int i = shardingContext.getShardingItem();
        List<String> results = new ArrayList<>();
        String data;
        while (null != (data = barRepository.getById(i))) {
            results.add(data);
            i += shardingContext.getShardingTotalCount();
        }
        log.info("{}", results);
    }
}
